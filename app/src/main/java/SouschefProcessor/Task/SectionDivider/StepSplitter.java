package SouschefProcessor.Task.SectionDivider;

import java.util.ArrayList;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.Task;

class StepSplitter implements Task {


    private String steps;

    public StepSplitter (String steps){
        this.steps = steps;
    }

    public void doTask(RecipeInProgress recipe){
        ArrayList<Step> stepList = divideIntoSteps(steps);
        recipe.setSteps(stepList);
    }

    /**
     * This function splits the text, describing the steps, into different steps of the recipe
     * @return A list of all steps in order
     */
    private ArrayList<Step> divideIntoSteps (String steps){
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
