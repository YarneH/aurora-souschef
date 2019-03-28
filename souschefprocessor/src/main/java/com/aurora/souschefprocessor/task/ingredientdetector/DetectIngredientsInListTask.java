package com.aurora.souschefprocessor.task.ingredientdetector;

import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Detects the ListIngredients in the list of ingredients of a RecipeInProgress
 * It has a CRFClassifier that classifies a sentence containing an ingredient to UNIT, QUANTITY
 * and NAME
 */
public class DetectIngredientsInListTask extends DetectIngredientsTask {

    // Strings representing the classes of the classifier
    private static final String QUANTITY = "QUANTITY";
    private static final String UNIT = "UNIT";
    private static final String NAME = "NAME";


    //The classifier to detect ingredients
    private CRFClassifier<CoreLabel> mCRFClassifier;

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress, CRFClassifier<CoreLabel> crfClassifier) {
        super(recipeInProgress);
        mCRFClassifier = crfClassifier;
    }

    /**
     * Adds spaces in a line, for example 250g/3oz is turned into 250 g / 3 oz so the
     * classifier sees these as different tokens
     *
     * @param line The line on which to add spaces
     * @return The line with the spaces added
     */
    private static String addSpaces(String line) {
        line = line.trim();
        StringBuilder bld = new StringBuilder();
        char[] chars = line.toCharArray();

        // add the first character
        bld.append(chars[0]);

        for (int i = 1; i < chars.length - 1; i++) {
            char previous = chars[i - 1];
            char current = chars[i];
            char next = chars[i + 1];

            if (spaceNeededBetweenPreviousAndCurrent(previous, current)) {

                bld.append(" " + current);
            } else if (spaceNeededBetweenCurrentAndNext(previous, current, next)) {
                // if a slash or dash is followed by a number and is not preceded by a number
                // add a space between current and next
                bld.append(current + " ");
            } else {
                bld.append(current);
            }
        }

        // add the last character
        bld.append(chars[chars.length - 1]);
        // return the builder
        return bld.toString();

    }

    /**
     * Checks if a space is needed between the first and second character. this is the case if either
     * a number is followed by a letter or a letter is followed by a dash or a slash
     *
     * @param first  The first character
     * @param second The second character
     * @return a boolean indicating if a space is needed
     */
    private static boolean spaceNeededBetweenPreviousAndCurrent(char first, char second) {
        if ((Character.isDigit(first) || Character.getType(first) == Character.OTHER_NUMBER)
                && Character.isAlphabetic(second)) {
            // if a number is followed by a letter, add a space
            return true;
        } else {
            // if a letter is followed by a slash, add a space
            return Character.isAlphabetic(first) && second == '/';

        }
    }

    /**
     * Checks if a space is needed between the second and third character this is the case if
     * the first character is not a number, the second character is a slash or a dash and the third
     * character is a number (e.g. 500ml/250oz will need a space between the l and / so that the
     * classifier sees this as two different words)
     *
     * @param first  The first character of the sequence
     * @param second The second character of the sequence
     * @param third  The third character of the sequence
     * @return a boolean indicating if a space is needed
     */
    private static boolean spaceNeededBetweenCurrentAndNext(char first, char second,
                                                            char third) {
        boolean secondIsSlashOrDash = (second == '/' || second == '-');
        boolean thirdIsNumber = (Character.isDigit(third) || Character.getType(third) == Character.OTHER_NUMBER);
        boolean firstIsNumber = (Character.isDigit(first) || Character.getType(first) == Character.OTHER_NUMBER);

        return (secondIsSlashOrDash && thirdIsNumber && !firstIsNumber);
    }

    /**
     * Detects the ListIngredients presented in the ingredientsString and sets the mIngredients field
     * in the recipe to this set of ListIngredients.
     */
    public void doTask() {
        List<ListIngredient> list = detectIngredients(this.mRecipeInProgress.getIngredientsString());
        if (list == null || list.isEmpty()) {
            throw new RecipeDetectionException("No ingredients where detected, this is probably not a recipe");
        }
        this.mRecipeInProgress.setIngredients(list);
    }

    /**
     * Detetects ingredients in a string representing an ingredient list, makes corresponding
     * Ingredient Objects and returns a set of these
     *
     * @param ingredientList The string representing the ingredientList
     * @return A list of ListIngredient Objects detected in the string
     */
    private List<ListIngredient> detectIngredients(String ingredientList) {

        // if no ingredientList is provided return an empty list
        if (ingredientList == null || ("").equals(ingredientList)) {
            return new ArrayList<>();
        }

        List<ListIngredient> returnList = new ArrayList<>();
        // Split the list on new lines
        String[] list = ingredientList.split("\n");

        for (String ingredient : list) {
            if (ingredient != null && ingredient.length() > 0) {
                ListIngredient ing = (detectIngredient(addSpaces(ingredient)));

                returnList.add(ing);

            }
        }
        return returnList;
    }

    /**
     * Detects the ingredient described in the line and constructs an Ingredient object with this information
     *
     * @param line The line in which the ingredient is to be detected
     * @return a ListIngredient object constructed with the information from the line
     */
    private ListIngredient detectIngredient(String line) {
        // TODO optimize model further
        // TODO quantity detection fails on  1 1/2-ounce can (should be 1 gets 1.5)


        // classify the line
        List<List<CoreLabel>> classifiedList = mCRFClassifier.classify(line);
        // map to put classes and labeled tokens
        Map<String, List<CoreLabel>> map = new HashMap<>();

        for (List<CoreLabel> l : classifiedList) {
            for (CoreLabel cl : l) {
                String classifiedClass = (cl.get(CoreAnnotations.AnswerAnnotation.class));
                if (map.get(classifiedClass) == null) {
                    // if this key is not yet in the map construct a list and add the label to the list
                    List<CoreLabel> list = new ArrayList<>();
                    list.add(cl);
                    map.put(classifiedClass, list);
                } else {
                    map.get(classifiedClass).add(cl);
                }

            }
        }
        // the map for the positions of the detected ingredients
        Map<Ingredient.PositionKeysForIngredients, Position> positions = new HashMap<>();
        // if no value present, default to 1.0 'one'
        double quantity = 1.0;
        // if no value present, default to empty string
        String unit = "";
        String name = "";


        if (map.get(UNIT) != null) {
            // build the unit (gets the first succeeding elements labeled unit)
            List<CoreLabel> succeedingUnits = getSucceedingElements(map.get(UNIT), UNIT);
            unit = buildUnit(succeedingUnits);

            // Calculate the position and add it to the map
            // beginPosition of the first element and endPosition of the last element
            int beginPosition = succeedingUnits.get(0).beginPosition();
            int endPosition = succeedingUnits.get(succeedingUnits.size() - 1).endPosition();
            positions.put(Ingredient.PositionKeysForIngredients.UNIT, new Position(beginPosition, endPosition));
        } else {
            // if no unit detected make the position the whole string
            positions.put(Ingredient.PositionKeysForIngredients.UNIT, new Position(0, line.length()));
        }

        List<CoreLabel> nameList = map.get(NAME);
        if (nameList != null) {
            // build the name using the list of tokens in the nameList
            name = buildName(nameList);

            // Calculate the position and add it to the map
            // beginPosition of the first element and endPosition of the last element
            int beginPosition = nameList.get(0).beginPosition();
            int endPosition = nameList.get(nameList.size() - 1).endPosition();
            positions.put(Ingredient.PositionKeysForIngredients.NAME, new Position(beginPosition, endPosition));
        } else {
            // if no name detected make the position the whole string
            positions.put(Ingredient.PositionKeysForIngredients.NAME, new Position(0, line.length()));
        }
        if (map.get(QUANTITY) != null) {
            // calculate the quantity using the list of tokens in labeled QUANTITY
            // for now first element labeled as quantity and the succeeding elements
            // (endposition + 1 = beginposition) or endposition = beginposition
            List<CoreLabel> succeedingQuantities = getSucceedingElements(map.get(QUANTITY), QUANTITY);
            quantity = calculateQuantity(succeedingQuantities);

            // if quantity is -1 then no quantity could be caluclated
            if (quantity != -1.0) {
                // Calculate the position and add it to the map
                // beginPosition of the first element and endPosition of the last element
                int beginPosition = succeedingQuantities.get(0).beginPosition();
                int endPosition = succeedingQuantities.get(succeedingQuantities.size() - 1).endPosition();
                positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(beginPosition, endPosition));
            }


        }
        if (positions.get(Ingredient.PositionKeysForIngredients.QUANTITY) == null) {
            // if no quantity detected make the position the whole string
            // if no quantity detected then the position is still null so make the position the
            // whole string to signal that no quantity is detected
            // also set the quantity to 1 = "one"
            positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(0, line.length()));
            quantity = 1.0;
        }

        return new ListIngredient(name, unit, quantity, line, positions);
    }

    /**
     * Builds the name of the ingredient using a list of tokens that were classified as NAME
     *
     * @param nameList the list of tokens
     * @return a string which is a concatenation of all the words in the list
     */
    private String buildName(List<CoreLabel> nameList) {
        // return every entity labeled name
        StringBuilder bld = new StringBuilder();
        for (CoreLabel cl : nameList) {
            if (NAME.equals(cl.get(CoreAnnotations.AnswerAnnotation.class))) {
                bld.append(cl.word() + " ");
            }
        }
        // delete last added space
        bld.deleteCharAt(bld.length() - 1);
        return bld.toString();
    }


    /**
     * Builds the name of the ingredient using a list of succeeding tokens that were classified as UNIT
     *
     * @param succeedingUnitList the list of succeeding tokens
     * @return a string which is a concatenation of all the succeeding words in the list
     */
    private String buildUnit(List<CoreLabel> succeedingUnitList) {

        StringBuilder bld = new StringBuilder();
        for (CoreLabel cl : succeedingUnitList) {
            bld.append(cl.word() + " ");
        }
        // delete last added space
        bld.deleteCharAt(bld.length() - 1);
        return bld.toString();
    }

    /**
     * Gets the first sequence of succeeding elements of a class in a list of labels.
     * An element succeeds another element if they are at most one char apart in the string on which
     * the labels were classified.
     *
     * @param list       The list of labeled elements
     * @param classLabel The class for which th first sequence if found
     * @return
     */
    private List<CoreLabel> getSucceedingElements(List<CoreLabel> list, String classLabel) {
        // for now first element labeled as classLabel and the succeeding elements
        // (endposition + 1 = beginposition) or endposition = beginposition
        // at most one char apart

        boolean firstFound = false;
        boolean listComplete = false;
        int endIndex = -1;
        CoreLabel element = null;
        List<CoreLabel> tokenQuantities = new ArrayList<>();

        for (int i = 0; i < list.size() && !listComplete; i++) {
            element = list.get(i);
            // check if the element belongs to the needed class
            if (classLabel.equals(element.get(CoreAnnotations.AnswerAnnotation.class))) {
                if (!firstFound) {
                    //if the first element is not found yet add this element to the list and toggle
                    // firstFound
                    tokenQuantities.add(element);
                    firstFound = true;
                    //endIndex is the position of the first char not in the string of this element
                    endIndex = element.endPosition();
                } else {
                    // the first element of the sequence has been found, check if this element is
                    // at most one char apart
                    if (element.beginPosition() == endIndex + 1 || element.beginPosition() == endIndex) {
                        tokenQuantities.add(element);
                        endIndex = element.endPosition();
                    } else {
                        listComplete = true;
                    }
                }
            } else if (firstFound) {
                // this element does not belong to the needed class, if an element of the class has
                // already been found then set listComplete to true in order to break the for loop
                listComplete = true;
            }


        }
        return tokenQuantities;
    }
}
