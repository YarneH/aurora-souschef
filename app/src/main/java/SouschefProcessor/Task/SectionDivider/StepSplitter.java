package SouschefProcessor.Task.SectionDivider;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.Task;

/**
 * A Task that splits the string representing the steps into Step objects
 */
public class StepSplitter implements Task {

    /**
     * This will split the stepsString in the RecipeInProgress Object into steps and modifies the
     * recipe object so that the steps are set
     *
     * @param recipe     The recipe on which to detect the steps and to modify the steps field
     * @param threadPool a threadPool to use if the task can be parallelized
     */
    public void doTask(RecipeInProgress recipe, ThreadPoolExecutor threadPool) {
        ArrayList<Step> stepList = divideIntoSteps(recipe.getStepsString());
        recipe.setSteps(stepList);
    }

    /**
     * This function splits the text, describing the steps, into different steps of the recipe
     *
     * @return A list of all steps in order
     */
    private ArrayList<Step> divideIntoSteps(String steps) {
        //TODO generate functionality to split attribute stepsText

        //dummy code
        ArrayList<Step> list = new ArrayList<>();
        Step s1 = new Step("Put 500 gram spaghetti in boiling water for 9 minutes.");
        Step s2 = new Step("Put the sauce in the Microwave for 3 minutes");
        list.add(s1);
        list.add(s2);
        return list;
    }

}
