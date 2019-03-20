package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

/**
 * A AbstractProcessingTask that divides the original text into usable sections
 */
public class SplitToMainSectionsTask extends AbstractProcessingTask {

    public SplitToMainSectionsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    /**
     * Divides the original text into a string representing list of mIngredients, string representing
     * a list of mRecipeSteps, string representing the mDescription of the recipe (if present) and an integer
     * representing the amount of people the orignal recipe is for. It will then modify the recipe
     * with these fields
     */
    public void doTask() {
        // TODO all of this could be in seperate threads
        // TODO add check that an original text is contained
        String text = this.mRecipeInProgress.getOriginalText();
        String ingredients = findIngredients(text);
        String steps = findSteps(text);
        String description = findDescription(text);
        modifyRecipe(this.mRecipeInProgress, ingredients, steps, description);

    }

    /**
     * Modifies the recipe so that the ingredientsString, stepsString, mDescription and amountOfPeople
     * fields are set.
     *
     * @param recipe      The recipe to modify
     * @param ingredients The string representing the mIngredients
     * @param steps       The string representing the mRecipeSteps
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
     * @param text the text in which to search for mIngredients
     * @return The string representing the mIngredients
     */
    public String findIngredients(String text) {
        // dummy

        return "8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve";
    }

    /**
     * Finds the mRecipeSteps in a text
     *
     * @param text the text in which to search for mRecipeSteps
     * @return The string representing the mRecipeSteps
     */
    public String findSteps(String text) {
        // dummy
        // return "Put 500 gram spaghetti in boiling water for 9 minutes.\n"
        // + "Put the sauce in the Microwave for 3 minutes \n"
        //        + "Put them together."

        return "Toast baguette slices lightly on one side. Layer each round " +
                "with smoked salmon, top with a dollup of sour \n" +
                "cream and sprinkle with a few capers and lots of freshly ground black pepper.";

    }

    /**
     * Finds the mDescription of the recipe in a text
     *
     * @param text the text in which to search for the mDescription of the recipe
     * @return The string representing the mDescription of the recipe
     */
    public String findDescription(String text) {
        // dummy
        // return "A spaghetti recipe"
        return "crostini with smoked salmon & sour cream";
    }


}
