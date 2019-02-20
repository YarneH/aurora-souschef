package SouschefProcessor.Task.SectionDivider;

import java.util.ArrayList;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Recipe.Step;
import SouschefProcessor.Task.Task;

public class SectionDivider implements Task {

    private String text;



    public SectionDivider (String text){
        //dummy code
        this.text = text;
    }

    public void doTask(RecipeInProgress recipe){
        String[] division = divideIntoListStepsAndDescription(text);
        recipe.setIngredientsString(division[0]);
        recipe.setStepsString(division[1]);
        recipe.setAmountOfPeople(Integer.parseInt(division[3]));

    }

    /**
     * Divides the text into two parts, the first is the ingredientlist and the second part is the steps
     * @param recipe A string containing the full recipe as extracted by Aurora
     * @return An array where the first element is the list of ingredients and the second element contains the steps.
     */
    public  String[] divideIntoListStepsAndDescription(String recipe){
        String[] division = new String[3];

        //TODO generate functionality
        //dummy
        division[0] =  "500 gram sauce \n 500 gram spaghetti";
        division[1] = "Put 500 gram spaghetti in boiling water for 9 minutes.\n"
                + "Put the sauce in the Microwave for 3 minutes \n"
                + "Put them together.";
        division[3] = "A spaghetti recipe";
        return division;
    }





}
