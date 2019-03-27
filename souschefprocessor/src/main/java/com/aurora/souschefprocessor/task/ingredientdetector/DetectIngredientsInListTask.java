package com.aurora.souschefprocessor.task.ingredientdetector;

import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Detects the ListIngredients in the list of ingredients of a RecipeInProgress
 * It has a CRFClassifier that classifies a sentence containing an ingredient to UNIT, QUANTITY
 * and NAME
 */
public class DetectIngredientsInListTask extends AbstractProcessingTask {


    // generally numbers greater than twelve are not spelled out
    private static final String[] NUMBERS_TO_TWELVE = {"zero", "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
    // multiples of ten are also spelled out
    private static final String[] MULTIPLES_OF_TEN = {"zero", "ten", "twenty", "thirty", "forty",
            "fifty", "sixty", "seventy", "eighty", "ninety", "hundred"};

    // The size if a string representing a fraction is split on the regex "/"
    private static final int FRACTION_LENGTH = 2;

    // The number 10
    private static final double TEN = 10;

    // Strings representing the classes of the classifier
    private static final String QUANTITY = "QUANTITY";
    private static final String UNIT = "UNIT";
    private static final String NAME = "NAME";

    // These regexes will remove clutter to pass the clutter test in DetectIngredientsInListTaskUnitTest
    private static final String CLUTTER_REGEX = "[/][0-9\\p{No}]+(([–-][0-9\\p{No}]+)+( pint)?|" +
            "fl oz|[a-z]+([ ][0-9\\p{No}](oz))?)";
    private static final String CLUTTER_DASH_REGEX = "[–-][0-9\\p{No}]+";


    //The classifier to detect ingredients
    private CRFClassifier<CoreLabel> mCRFClassifier;

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress, CRFClassifier<CoreLabel> crfClassifier) {
        super(recipeInProgress);
        mCRFClassifier = crfClassifier;
    }

    /**
     * Checks if the string is a spelled out version of the numbers 0 to 12 or a multiple of 10 (up to 100)
     *
     * @param s The string to be checked
     * @return The numeric representation of the string
     */
    private static double calculateNonParsableQuantity(String s) {
        String lower = s.toLowerCase(Locale.ENGLISH);
        // check if  number is 0-12
        for (int i = 0; i < NUMBERS_TO_TWELVE.length; i++) {
            if (lower.equals(NUMBERS_TO_TWELVE[i])) {
                return i;
            }
        }

        // check is string is a multiple of ten
        for (int i = 0; i < MULTIPLES_OF_TEN.length; i++) {
            if (lower.equals(MULTIPLES_OF_TEN[i])) {
                return i * TEN;
            }
        }

        // if not one of the previous cases consider wrongly labeled
        return 0.0;
    }

    /**
     * This calls the removeClutter method, this makes sure that the following sort of conversion
     * happens: 500ml/3fl oz -> 500 ml
     * Also, adds spaces in a line, for example 250g is turned in to 250 g. This is needed because the
     * classifier needs to see 250 and "g" as seperate tokens.
     * It also deletes the "." character when it is not between two digits, as in "1 lb. of pasta"
     *
     * @param line The line on which to add spaces, remove clutter and delete "."
     * @return The line with the spaces added and the points deleted
     */
    private static String removeClutterAddSpacesAndRemovePoint(String line) {
        line = line.trim();
        line = removeClutter(line);
        StringBuilder bld = new StringBuilder();
        char[] chars = line.toCharArray();

        // add the first character
        bld.append(chars[0]);

        for (int i = 1; i < chars.length - 1; i++) {
            char previous = chars[i - 1];
            char current = chars[i];
            char next = chars[i + 1];

            // do not append automatically if it is a point
            if (current != '.') {

                if (spaceNeededBetweenPreviousAndCurrent(previous, current)) {
                    bld.append(" ");
                    bld.append(current);
                } else {
                    bld.append(current);
                }
            } else {
                if (pointNeededBetweenPreviousAndNext(previous, next)) {
                    bld.append(current);
                }
            }
        }

        // add the last character
        bld.append(chars[chars.length - 1]);
        // return the builder
        return bld.toString();

    }

    /**
     * Checks if the "." is still needed between these characters, which is the case if both characters
     * are digits
     *
     * @param previous The character before the "."
     * @param next     The character after the "."
     * @return A boolean indicating whether the "." character is needed
     */
    private static boolean pointNeededBetweenPreviousAndNext(char previous, char next) {
        return Character.isDigit(previous) && Character.isDigit(next);
    }

    /**
     * Removes clutter from an ingredient line, some examples of cluttered lines and their conversion
     * 2.5kg/5lb 8oz turkey crown (fully thawed if frozen) => 2.5kg turkey crown (fully thawed if frozen)
     * 750–900ml/1⅓–1⅔ pint readymade chicken gravy => 750ml readymade chicken gravy
     * 500ml/18fl oz milk => 500ml milk
     * 200ml/7fl oz crème frâiche => 200ml crème frâiche
     * 350ml/12¼fl oz warm water => 350ml warm water
     * 200ml/7fl oz fromage frais => 200ml fromage frais
     * 100g/5½oz raisins => 100g raisins
     *
     * @param line The line from where to remove the clutter
     * @return The line with the clutter removed (a string)
     */
    private static String removeClutter(String line) {
        List<Pattern> patterns = new ArrayList<>();
        patterns.add(Pattern.compile(CLUTTER_REGEX));
        patterns.add(Pattern.compile(CLUTTER_DASH_REGEX));
        line = removeMatchingRegexesInOrder(line, patterns);
        return line;
    }

    /**
     * Removes the part of the line that matches the pattern for each pattern in the list, in order,
     * so the second pattern is mathced against the result of the operation with the first pattern.
     *
     * @param line     The line to match against the patterns
     * @param patterns the patterns to match
     * @return the line with the matching patterns removed
     */
    private static String removeMatchingRegexesInOrder(String line, List<Pattern> patterns) {
        Matcher match;
        for (Pattern pattern : patterns) {
            match = pattern.matcher(line);
            if (match.find()) {
                String remove = match.group();
                line = line.replace(remove, "");
            }
        }
        return line;
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


    private static double calculateQuantity(String[] array) {
        boolean multiply = false;
        double result = 0.0;
        for (String s : array) {
            String[] fraction = s.split("/");
            try {
                // if the string was splitted in to two parts it was a fraction
                if (fraction.length == FRACTION_LENGTH) {
                    result = calculateFraction(fraction, result, multiply);
                    // set multiply to false after fraction because it is always multiplied when
                    // it was true
                    multiply = false;
                }
                // not a fraction
                else if (multiply) {
                    // if previous was multiplication, multiply
                    result *= Double.parseDouble(s);
                    multiply = false;
                } else if ("x".equalsIgnoreCase(s)) {
                    // if this is a multiplication sign set multiply to two
                    multiply = true;
                } else {
                    // just add the result
                    result += Double.parseDouble(s);

                }
            } catch (NumberFormatException iae) {
                // String identified as quantity is not parsable...
                double nonParsableQuantity = calculateNonParsableQuantity(s);
                if (multiply) {
                    result *= nonParsableQuantity;
                    multiply = false;
                } else {
                    result += nonParsableQuantity;
                }
            }
        }
        return result;
    }

    private static double calculateFraction(String[] fraction, double intermediateResult, boolean multiply) {
        double numerator = Double.parseDouble(fraction[0]);
        double denominator = Double.parseDouble(fraction[1]);
        if (!multiply) {
            intermediateResult += numerator / denominator;
        } else {
            intermediateResult *= numerator / denominator;
        }
        return intermediateResult;

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
                ListIngredient ing = (detectIngredient(removeClutterAddSpacesAndRemovePoint(ingredient)));

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

        // classify the line
        List<List<CoreLabel>> classifiedList = mCRFClassifier.classify(line);
        // map to put classes and labeled tokens
        Map<String, List<CoreLabel>> map = new HashMap<>();

        for (List<CoreLabel> l : classifiedList) {
            for (CoreLabel cl : l) {
                String classifiedClass = (cl.get(CoreAnnotations.AnswerAnnotation.class)).trim();
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
        Map<Ingredient.PositionKey, Position> positions = new HashMap<>();
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
            positions.put(Ingredient.PositionKey.UNIT, new Position(beginPosition, endPosition));
        } else {
            // if no unit detected make the position the whole string
            positions.put(Ingredient.PositionKey.UNIT, new Position(0, line.length()));
        }

        List<CoreLabel> nameList = map.get(NAME);
        if (nameList != null) {
            // build the name using the list of tokens in the nameList
            name = buildName(nameList);

            // Calculate the position and add it to the map
            // beginPosition of the first element and endPosition of the last element
            int beginPosition = nameList.get(0).beginPosition();
            int endPosition = nameList.get(nameList.size() - 1).endPosition();
            positions.put(Ingredient.PositionKey.NAME, new Position(beginPosition, endPosition));
        } else {
            // if no name detected make the position the whole string
            positions.put(Ingredient.PositionKey.NAME, new Position(0, line.length()));
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
                positions.put(Ingredient.PositionKey.QUANTITY, new Position(beginPosition, endPosition));
            }


        }
        if (positions.get(Ingredient.PositionKey.QUANTITY) == null) {
            // if no quantity detected make the position the whole string
            // if no quantity detected then the position is still null so make the position the
            // whole string to signal that no quantity is detected
            // also set the quantity to 1 = "one"
            positions.put(Ingredient.PositionKey.QUANTITY, new Position(0, line.length()));
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
                bld.append(cl.word());
                bld.append(" ");
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
            bld.append(cl.word());
            bld.append(" ");
        }
        // delete last added space
        bld.deleteCharAt(bld.length() - 1);
        return bld.toString();
    }

    /**
     * Calculates the quantity based on list with tokens labeled quantity
     *
     * @param list The list on which to calculate the quantity
     * @return a double representing the calculated value, if no value could be calculated -1.0 is
     * returned
     */
    private double calculateQuantity(List<CoreLabel> list) {

        StringBuilder bld = new StringBuilder();
        for (CoreLabel cl : list) {
            bld.append(cl.word());
            bld.append(" ");
        }

        String representation = bld.toString();

        // split on all whitespace characters
        String[] array = representation.split("[\\s\\xA0]+");
        double result = calculateQuantity(array);

        if (result == 0.0) {
            // if no quantity value was detected return -1.0 to signal that detected quantity is
            // not a quantity
            return -1;
        }
        return result;
    }

    /**
     * Gets the first sequence of succeeding elements of a class in a list of labels.
     * An element succeeds another element if they are at most one char apart in the string on which
     * the labels were classified.
     *
     * @param list       The list of labeled elements
     * @param classLabel The class for which th first sequence if found
     * @return The list of succeeding elements with this classLabel
     */
    private List<CoreLabel> getSucceedingElements(List<CoreLabel> list, String classLabel) {
        // for now first element labeled as classLabel and the succeeding elements
        // (endposition + 1 = beginposition) or endposition = beginposition
        // at most one char apart

        boolean firstFound = false;
        boolean listComplete = false;
        int endIndex = -1;
        CoreLabel element;
        List<CoreLabel> tokenQuantities = new ArrayList<>();

        for (int i = 0; i < list.size() && !listComplete; i++) {
            element = list.get(i);
            // check if the element belongs to the needed class
            if (classLabel.equals((element.get(CoreAnnotations.AnswerAnnotation.class)).trim())) {
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
