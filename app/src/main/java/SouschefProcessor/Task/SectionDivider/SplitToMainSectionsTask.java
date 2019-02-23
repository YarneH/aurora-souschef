package SouschefProcessor.Task.SectionDivider;

import java.util.concurrent.ThreadPoolExecutor;

import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.ProcessingTask;

/**
 * A ProcessingTask that divides the original text into usable sections
 */
public class SplitToMainSectionsTask implements ProcessingTask {


    /**
     * Divides the original text into a string representing list of ingredients, string representing
     * a list of recipeSteps, string representing the description of the recipe (if present) and an integer
     * representing the amount of people the orignal recipe is for. It will then modify the recipe
     * with these fields
     *
     * @param recipeInProgress     The recipe to split into sections
     * @param threadPoolExecutor The threadpool on which to execute threads if threads are used
     */
    public void doTask(RecipeInProgress recipeInProgress, ThreadPoolExecutor threadPoolExecutor) {
        //TODO all of this could be in seperate threads
        //TODO add check that an original text is contained
        String text = recipeInProgress.getOriginalText();
        String ingredients = findIngredients(text);
        String steps = findSteps(text);
        String description = findDescription(text);
        modifyRecipe(recipeInProgress, ingredients, steps, description);

    }

    /**
     * Modifies the recipe so that the ingredientsString, stepsString, description and amountOfPeople
     * fields are set.
     *
     * @param recipe      The recipe to modify
     * @param ingredients The string representing the ingredients
     * @param steps       The string representing the recipeSteps
     * @param description The string representing the desription
     */
    public void modifyRecipe(RecipeInProgress recipe, String ingredients, String steps, String description) {
        recipe.setIngredientsString(ingredients);
        recipe.setStepsString(steps);
        recipe.setDescription(description);


    }


    /**
     * Finds the ingredientslist in a text
     *
     * @param text the text in which to search for ingredients
     * @return The string representing the ingredients
     */
    public String findIngredients(String text) {
        //dummy
        return "500 gram sauce \n 500 gram spaghetti";
    }

    /**
     * Finds the recipeSteps in a text
     *
     * @param text the text in which to search for recipeSteps
     * @return The string representing the recipeSteps
     */
    public String findSteps(String text) {
        //dummy
        return "Put 500 gram spaghetti in boiling water for 9 minutes.\n"
                + "Put the sauce in the Microwave for 3 minutes \n"
                + "Put them together.";
    }

    /**
     * Finds the description of the recipe in a text
     *
     * @param text the text in which to search for the description of the recipe
     * @return The string representing the description of the recipe
     */
    public String findDescription(String text) {
        //dummy
        return "A spaghetti recipe";
    }




}
