package com.aurora.souschef.souschefprocessor.task;

import com.aurora.souschef.recipe.Recipe;

/**
 * A subclass of Recipe, representing a Recipe Object that is being constructed. It has three
 * additional fields:
 * mIngredientsString: a string representing the mIngredients list
 * mStepsString: a string representing the different mRecipeSteps in the recipe
 * mOriginalText: a string that is the original text that was extracted by Aurora
 */
public class RecipeInProgress extends Recipe {
    private String mIngredientsString;
    private String mStepsString;
    private String mOriginalText;


    public RecipeInProgress(String originalText) {
        super();
        this.mOriginalText = originalText;
    }

    public String getOriginalText() {
        return mOriginalText;
    }

    public synchronized String getStepsString() {
        return mStepsString;
    }

    public synchronized void setStepsString(String stepsString) {
        this.mStepsString = stepsString;
    }

    public synchronized String getIngredientsString() {
        return mIngredientsString;
    }

    public synchronized void setIngredientsString(String ingredientsString) {
        this.mIngredientsString = ingredientsString;
    }

    /**
     * Converts the RecipeInProgress to a Recipe object by dropping the two additional fields
     *
     * @return the converted recipe
     */
    public Recipe convertToRecipe() {
        return new Recipe(mIngredients, mRecipeSteps, mNumberOfPeople, mDescription);
    }
}
