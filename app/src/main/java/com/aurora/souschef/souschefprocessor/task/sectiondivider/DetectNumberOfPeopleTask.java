package com.aurora.souschef.souschefprocessor.task.sectiondivider;

import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.ProcessingTask;

public class DetectNumberOfPeopleTask extends ProcessingTask {

    public DetectNumberOfPeopleTask(RecipeInProgress recipeInProgress){
        super(recipeInProgress);
    }

    public void doTask(){
        String text = this.mRecipeInProgress.getOriginalText();
        int number = findNumberOfPeople(text);
        this.mRecipeInProgress.setNumberOfPeople(number);
    }

    /**
     * Finds the amount of the people the recipe is for in a text
     *
     * @param text the text in which to search for the amount of people
     * @return The int representing the amount of people
     */
    private int findNumberOfPeople(String text) {
        //dummy
        return 4;
    }
}
