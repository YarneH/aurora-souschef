package com.aurora.souschefprocessor.task.ingredientdetector;

import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.List;
import java.util.Locale;

import edu.stanford.nlp.ling.CoreLabel;

/**
 * Superclass for detecting ingredients
 * provides constants and methods for calculating quantities when they have been detected
 */
abstract class DetectIngredientsTask extends AbstractProcessingTask {

    /**
     * An array of spelled out numbers, generally numbers greater than twelve are not spelled out so
     * these are numbers zero to twelve
     */
    private static final String[] NUMBERS_TO_TWELVE = {"zero", "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
    /**
     * An array of spelled out numbers since multiples of ten are also spelled out
     */
    private static final String[] MULTIPLES_OF_TEN = {"zero", "ten", "twenty", "thirty", "forty",
            "fifty", "sixty", "seventy", "eighty", "ninety", "hundred"};

    /**
     * The size if a string representing a fraction is split on the regex "/"
     */
    private static final int FRACTION_LENGTH = 2;

    /**
     * The number 10
     */
    private static final double TEN = 10;


    DetectIngredientsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    /**
     * Calculates the quantity based on an array of Strings that were tagged as quantity
     *
     * @param array the array of strings
     * @return the calculated value
     */
    private static double calculateQuantity(String[] array) {
        boolean multiply = false;
        double result = 0.0;
        for (String s : array) {
            String[] fraction = s.split("[/⁄]");
            try {

                // if the string was splitted in to two parts it was a fraction
                if (fraction.length == FRACTION_LENGTH) {
                    result = calculateFraction(fraction, result, multiply);
                    // set multiply to false after fraction because it is always multiplied when
                    // it was true
                    multiply = false;
                    // after this not a fraction
                } else if (multiply) {
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

    /**
     * Calculates and adds or multiplies a fraction from a string to an intermediateresult
     *
     * @param fraction           a string with length 2 representing a fraction where the first element is the
     *                           numerator and the second element is the denominator
     * @param intermediateResult the intermediate result to add or multiply the new value with
     * @param multiply           a boolean to indicate wheter it should be multiplied or added
     * @return the new result
     */
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
     * Calculates the quantity based on list with tokens labeled quantity
     *
     * @param list The list on which to calculate the quantity
     * @return a double representing the calculated value, if no value could be calculated -1.0 is
     * returned
     */
    double calculateQuantity(List<CoreLabel> list) {

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


}
