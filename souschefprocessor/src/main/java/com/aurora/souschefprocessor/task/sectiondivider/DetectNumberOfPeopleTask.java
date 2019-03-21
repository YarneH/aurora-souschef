package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetectNumberOfPeopleTask extends AbstractProcessingTask {

    private static final int DEFAULT_NUMBER = 4;
    private static final int DEFAULT_NO_NUMBER = -1;
    private static final String[] BEFORE_DIGIT_WORDS = {"yields", "yield", "serves", "servings", "makes", "portion of"};
    private static final String[] AFTER_DIGIT_WORDS = {"persons", "people", "servings"};
    private static final String[] SEPERATOR_CHARACTERS = {":", " ", "(about)"};
    private static String regex = buildRegex();

    public DetectNumberOfPeopleTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    /**
     * Finds the amount of the people the recipe is for in a text
     *
     * @param text the text in which to search for the amount of people
     * @return The int representing the amount of people
     * returns -1 if no amount of people was detected in the recipe text
     */
    private static int findNumberOfPeople(String text) {

        String[] lines = text.split("\n");

        for (String line : lines) {
            Matcher match = Pattern.compile(regex).matcher(line.toLowerCase(Locale.ENGLISH));
            if (match.find()) {
                Matcher digitMatcher = Pattern.compile("\\d+").matcher((match.group()));
                if (digitMatcher.find()) {
                    String number = digitMatcher.group();
                    return Integer.parseInt(number);
                }
            }
        }

        return DEFAULT_NO_NUMBER;

    }

    /**
     * Builds the regex to match with the first words being the BEFORE_ words and the last the after
     * words
     * string to match = ((yields|serves)[ :]*\d+.*)|(.*\d+[ :]*(servings|people))
     */
    private static String buildRegex() {
        // start with two opening parentheses
        StringBuilder bld = new StringBuilder("((");
        // add the before words seperated by or
        for (String word : BEFORE_DIGIT_WORDS) {
            bld.append(word + "|");
        }
        // remove last added "|"
        bld.deleteCharAt(bld.length() - 1);

        bld.append(")[");

        for (String c : SEPERATOR_CHARACTERS) {
            bld.append(c);
        }
        bld.append("]*\\d+.*)|(.*\\d+[");

        for (String c : SEPERATOR_CHARACTERS) {
            bld.append(c);
        }

        bld.append("]*(");
        for (String word : AFTER_DIGIT_WORDS) {
            bld.append(word + "|");
        }
        // remove last added "|"
        bld.deleteCharAt(bld.length() - 1);
        bld.append("))");
        System.out.println(bld);
        return bld.toString();

    }

    public void doTask() {
        String text = this.mRecipeInProgress.getOriginalText();
        int number = findNumberOfPeople(text);
        this.mRecipeInProgress.setNumberOfPeople(number);
    }
}
