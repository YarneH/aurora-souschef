package com.aurora.souschefprocessor.task.ingredientdetector;

import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.stanford.nlp.ling.CoreLabel;

/**
 * Superclass for detecting ingredients
 * provides constants and methods for calculating quantities when they have been detected
 */
public abstract class DetectIngredientsTask extends AbstractProcessingTask {

    // generally numbers greater than twelve are not spelled out
    protected static final String[] NUMBERS_TO_TWELVE = {"zero", "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
    // multiples of ten are also spelled out
    protected static final String[] MULTIPLES_OF_TEN = {"zero", "ten", "twenty", "thirty", "forty",
            "fifty", "sixty", "seventy", "eighty", "ninety", "hundred"};

    // The number 10
    protected static final double TEN = 10;

    // The size if a string representing a fraction is split on the regex "/"
    protected static final int FRACTION_LENGTH = 2;
    // The size if a string representing a number (non-fraction) is split on the regex "/"
    protected static final int NON_FRACTION_LENGTH = 1;

    public DetectIngredientsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }
    
    /**
     * Calculates the quantity based on list with tokens labeled quantity
     *
     * @param list The list on which to calculate the quantity
     * @return a double representing the calculated value, if no value could be detected 1.0 is
     * returned
     */
    protected double calculateQuantity(List<CoreLabel> list) {
        double result = 0.0;

        StringBuilder bld = new StringBuilder();
        for (CoreLabel cl : list) {
            bld.append(cl.word() + " ");
        }

        String representation = bld.toString();

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
        return result;
    }

    /**
     * Checks if the string is a spelled out version of the numbers 0 to 12 or a multiple of 10 (up to 100)
     *
     * @param s The string to be checked
     * @return The numeric representation of the string
     */
    protected static double calculateNonParsableQuantity(String s) {
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


}
