package SouschefProcessor.Task.SectionDivider;

import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.ProcessingTask;

public class DetectNumberOfPeopleTask implements ProcessingTask {

    public void doTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor){
        String text = recipeInProgress.getOriginalText();
        int amount = findAmountOfPeople(text);
    }

    /**
     * Finds the amount of the people the recipe is for in a text
     *
     * @param text the text in which to search for the amount of people
     * @return The int representing the amount of people
     */
    public int findAmountOfPeople(String text) {
        //dummy
        return 4;
    }
}
