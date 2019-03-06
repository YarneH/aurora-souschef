package com.aurora.souschef.souschefprocessor.task.ingredientdetector;

import android.util.Log;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import static android.content.ContentValues.TAG;

/**
 * Detects the mIngredients in the list of mIngredients
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
    // The size if a string representing a number (non-fraction) is split on the regex "/"
    private static final int NON_FRACTION_LENGTH = 1;

    // The number 10
    private static final double TEN = 10;

    // Strings representing the classes of the classifier
    private static final String QUANTITY = "QUANTITY";
    private static final String UNIT = "UNIT";
    private static final String NAME = "NAME";


    //The classifier to detect ingredients
    private CRFClassifier<CoreLabel> crf;

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);

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
     * Adds spaces in a line, for example 250g/3oz is turned into 250 g / 3 oz so the
     * classifier sees these as different tokens
     *
     * @param line The line on which to add spaces
     * @return The line with the spaces added
     */
    private static String addSpaces(String line) {
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
            }

            if (spaceNeededBetweenCurrentAndNext(previous, current, next)) {
                // if a slash or dash is followed by a number and is not preceded by a number
                // add a space between current and next
                bld.append(current + " " );
            } else {
                bld.append(current);
            }
        }

        // add the last character
        bld.append(chars[chars.length - 1]);
        // return the builded string
        return bld.toString();

    }

    private static boolean spaceNeededBetweenPreviousAndCurrent(char first, char second) {
        if ((Character.isDigit(first) || Character.getType(first) == Character.OTHER_NUMBER)
                && Character.isAlphabetic(second)) {
            // if a number is followed by a letter add a space
            return true;
        } else if (Character.isAlphabetic(first) && second == '/') {
            // if a letter is followed by a slash add a space
            return true;
        }
        return false;
    }

    private static boolean spaceNeededBetweenCurrentAndNext(char first, char second, char third) {
        if (second == '/' || second == '-') {
            if (Character.isDigit(third) || Character.getType(third) == Character.OTHER_NUMBER) {
                if (!(Character.isDigit(first) || Character.getType(first) == Character.OTHER_NUMBER)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Detects the mIngredients presented in the ingredientsString and sets the mIngredients field
     * in the recipe to this set of mIngredients.
     */
    public void doTask() {
        //TODO fallback if no mIngredients can be detected
        List<Ingredient> list = detectIngredients(this.mRecipeInProgress.getIngredientsString());
        this.mRecipeInProgress.setIngredients(list);
    }

    /**
     * Detetcs ingredients in a string representing an ingredient list, makes corresponding
     * Ingredient Objects and returns a set of these
     *
     * @param ingredientList The string representing the ingredientList
     * @return A set of Ingredient Objects detected in the string
     */
    private List<Ingredient> detectIngredients(String ingredientList) {

        if (ingredientList == null || ("").equals(ingredientList)) {
            return new ArrayList<>();
        }

        List<Ingredient> returnList = new ArrayList<>();
        String[] list = ingredientList.split("\n");

        for (String ingredient : list) {
            if (ingredient != null) {
                Ingredient ing = (detectIngredient(addSpaces(ingredient)));

                if (ing != null) {
                    returnList.add(ing);
                }
            }
        }
        return returnList;
    }

    /**
     * Detects the ingredient described in the line and constructs an Ingredient object with this information
     *
     * @param line The line in which the ingredient is to be detected
     * @return an Ingredient object constructed with the information from the line
     */
    private Ingredient detectIngredient(String line) {
        // TODO optimize model further
        // TODO quantity detection fails on 1¼ (should be 1.25 gets 1) and on 1 1/2-ounce can (should be 1 gets 1.5)

        try {
            if (crf == null) {
                // if classifier not loaded yet load the classifier
                String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
                crf = CRFClassifier.getClassifier(modelName);
            }
        } catch (IOException | ClassNotFoundException exception) {
            Log.e(TAG, "detect ingredients in list: classifier not loaded ", exception);
            return null;
        }

        // classify the line
        List<List<CoreLabel>> classifiedList = crf.classify(line);

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

        // if no value present, default to 1.0 'one'
        double quantity = 1.0;
        String unit = "";
        String name = "";

        //for now get the first element
        if (map.get(UNIT) != null) {
            unit = map.get(UNIT).get(0).toString();
        }

        List<CoreLabel> nameList = map.get(NAME);
        if (nameList != null) {
            // build the name using the list of tokens in the nameList
            name = buildName(nameList);
        }
        if (map.get(QUANTITY) != null) {
            // calculate the quantity using the list of tokens in labeled QUANTITY
            quantity = calculateQuantity(map.get(QUANTITY));
        }
        // if quantity is seen as negative revert
        if (quantity < 0.0) {
            quantity = -quantity;
        }

        return new Ingredient(name, unit, quantity);
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
     * Calculates the quantity based on list with tokens labeled quantity
     *
     * @param list The list on which to calculate the quantity
     * @return a double representing the calculated value, if no value could be detected 1.0 is
     * returned
     */
    private double calculateQuantity(List<CoreLabel> list) {
        double result = 0.0;
        // for now first element labeled as quantity
        boolean found = false;
        CoreLabel element = null;
        for (int i = 0; i < list.size() && !found; i++) {
            element = list.get(i);
            if (QUANTITY.equals(element.get(CoreAnnotations.AnswerAnnotation.class))) {
                found = true;
            }
        }
        String representation = element.word();

        // split on all whitespace characters
        String[] array = representation.split("[\\s\\xA0]+");
        for (String s : array) {

            String[] fraction = s.split("/");
            try {
                // if the string was splitted in to two parts it was a fraction
                if (fraction.length == FRACTION_LENGTH) {

                    double numerator = Double.parseDouble(fraction[0]);
                    double denominator = Double.parseDouble(fraction[1]);
                    result += numerator / denominator;
                }

                if (fraction.length == NON_FRACTION_LENGTH) {
                    result += Double.parseDouble(s);
                }
            } catch (NumberFormatException iae) {
                // String identified as quantity is not parsable...
                result += calculateNonParsableQuantity(s);
            }
        }

        if (result == 0.0) {
            // if no quantity value was detected return 1.0 "one"
            return 1.0;
        }
        return result;
    }
}
