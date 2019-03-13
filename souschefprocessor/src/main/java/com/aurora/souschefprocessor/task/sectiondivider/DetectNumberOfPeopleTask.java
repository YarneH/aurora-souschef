package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

public class DetectNumberOfPeopleTask extends AbstractProcessingTask {

    private static final int DEFAULT_NUMBER = 4;

    public DetectNumberOfPeopleTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    /**
     * Finds the amount of the people the recipe is for in a text
     *
     * @param text the text in which to search for the amount of people
     * @return The int representing the amount of people
     */
    private static int findNumberOfPeople(String text) {
        // dummy
        // static modifier is for sonar but could change

        if (("irrelevant").equals(text)) {

            return DEFAULT_NUMBER;
        }
        return DEFAULT_NUMBER * text.length();
    }

    public void doTask() {
        String text = this.mRecipeInProgress.getOriginalText();
        int number = findNumberOfPeople(text);
        this.mRecipeInProgress.setNumberOfPeople(number);
    }
}
