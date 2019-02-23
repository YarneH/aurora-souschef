package SouschefProcessor.Task.SectionDivider;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.RecipeStep;
import SouschefProcessor.Task.ProcessingTask;

/**
 * A ProcessingTask that splits the string representing the recipeSteps into RecipeStep objects
 */
public class SplitStepsTask implements ProcessingTask {

    /**
     * This will split the stepsString in the RecipeInProgress Object into recipeSteps and modifies the
     * recipe object so that the recipeSteps are set
     *
     * @param recipeInProgress     The recipe on which to detect the recipeSteps and to modify the recipeSteps field
     * @param threadPoolExecutor a threadPool to use if the task can be parallelized
     */
    public void doTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor) {
        ArrayList<RecipeStep> recipeStepList = divideIntoSteps(recipeInProgress.getStepsString());
        recipeInProgress.setRecipeSteps(recipeStepList);
    }

    /**
     * This function splits the text, describing the recipeSteps, into different recipeSteps of the recipe
     *
     * @return A list of all recipeSteps in order
     */
    private ArrayList<RecipeStep> divideIntoSteps(String steps) {
        //TODO generate functionality to split attribute stepsText

        //dummy code
        ArrayList<RecipeStep> list = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Put 500 gram spaghetti in boiling water for 9 minutes.");
        RecipeStep s2 = new RecipeStep("Put the sauce in the Microwave for 3 minutes");
        list.add(s1);
        list.add(s2);
        return list;
    }

}
