package SouschefProcessor.StepSplitter;

import java.util.ArrayList;

class StepSplitter {

    private String ingredientList;
    private String steps;

    public String getIngredientList() {
        return ingredientList;
    }

    private StepSplitter (String ingredientList, String steps){
        //dummy code
        this.ingredientList = ingredientList;
        this.steps = steps;
    }

    /**
     * Creates a StepSplitter for a recipe
     * @param recipe The recipe containing the text as extracted by Aurora
     * @return A StepSplitter Object with filled in ingredientList and steps
     */
    public final StepSplitter createStepSplitter(String recipe){
        String[] division = divideIntoListAndSteps(recipe);
        return new StepSplitter(division[0], division[1]);

    }
    /**
     * Divides the text into two parts, the first is the ingredientlist and the second part is the steps
     * @param recipe A string containing the full recipe as extracted by Aurora
     * @return An array where the first element is the list of ingredients and the second element contains the steps.
     */
    private final String[] divideIntoListAndSteps(String recipe){
        String[] division = new String[2];

        //TODO generate functionality
        //dummy
        division[0] =  "500 gram sauce \n 500 gram spaghetti";
        division[1] = "Put 500 gram spaghetti in boiling water for 9 minutes.\n"
                + "Put the sauce in the Microwave for 3 minutes \n"
                + "Put them together.";
        return division;
    }

    /**
     * This function splits the text, describing the steps, into different steps of the recipe
     * @return A list of all steps in order
     */
    public ArrayList<Step> getSteps (){
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
