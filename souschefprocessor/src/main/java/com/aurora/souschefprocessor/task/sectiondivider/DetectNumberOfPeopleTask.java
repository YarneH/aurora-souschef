package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

public class DetectNumberOfPeopleTask extends AbstractProcessingTask {

    private static final int DEFAULT_NUMBER = 4;
    private static final int DEFAULT_NO_NUMBER = -1;

    public DetectNumberOfPeopleTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    /**
     * Finds the amount of the people the recipe is for in a text
     *
     * @param text the text in which to search for the amount of people
     * @return The int representing the amount of people
     *  returns -1 if no amount of people was detected in the recipe text
     */
    private static int findNumberOfPeople(String text) {
        // dummy
        // No amount detected in first line
        if(!text.split("\n")[1].matches(".*\\d+.*")){
            return DEFAULT_NO_NUMBER;
        }
        else {
            return DEFAULT_NUMBER;
        }
    }

    public void doTask() {
        String text = this.mRecipeInProgress.getOriginalText();
        int number = findNumberOfPeople(text);
        this.mRecipeInProgress.setNumberOfPeople(number);
    }
}
