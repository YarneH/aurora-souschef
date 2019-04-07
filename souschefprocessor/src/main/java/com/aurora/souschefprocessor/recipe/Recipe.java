package com.aurora.souschefprocessor.recipe;

import com.aurora.auroralib.PluginObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A data class representing a recipe. It has 4 fields:
 * mIngredients: a list of ListIngredient objecs, this represents the ListIngredients needed for
 * this recipe.
 * mRecipeSteps: A list of RecipeSteps in this recipe
 * mNumberOfPeople: the amount of people the basic recipe is for
 * mDescription: a description of the recipe (could be that it is not present)
 */
public class Recipe extends PluginObject {
    /**
     * The list of detected {@link ListIngredient} in this recipe
     */
    protected List<ListIngredient> mIngredients = new ArrayList<>();
    /**
     * The list of detected {@link RecipeStep} in this recipe
     */
    protected List<RecipeStep> mRecipeSteps = new ArrayList<>();
    /**
     * The detected number of people this recipe is for
     */
    protected int mNumberOfPeople;
    /**
     * The (optional) description of this recipe
     */
    protected String mDescription;


    public Recipe(List<ListIngredient> ingredients, List<RecipeStep> recipeSteps,
                  int numberOfPeople, String description) {
        this.mIngredients = ingredients;
        this.mRecipeSteps = recipeSteps;
        this.mNumberOfPeople = numberOfPeople;
        this.mDescription = description;
    }

    public Recipe() {}

    public synchronized int getNumberOfPeople() {
        return mNumberOfPeople;
    }

    public synchronized void setNumberOfPeople(int numberOfPeople) {
        this.mNumberOfPeople = numberOfPeople;
    }

    public synchronized String getDescription() {
        return mDescription;
    }

    public synchronized void setDescription(String description) {
        this.mDescription = description;
    }

    public synchronized List<RecipeStep> getRecipeSteps() {
        return mRecipeSteps;
    }

    public synchronized void setRecipeSteps(List<RecipeStep> recipeSteps) {
        if (recipeSteps == null) {
            mRecipeSteps.clear();
        } else {
            this.mRecipeSteps = recipeSteps;
        }
    }

    public synchronized List<ListIngredient> getIngredients() {
        return mIngredients;
    }

    public synchronized void setIngredients(List<ListIngredient> ingredients) {
        if (ingredients == null) {
            mIngredients.clear();
        } else {
            this.mIngredients = ingredients;
        }
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "mIngredients=" + mIngredients +
                "\n mRecipeSteps=" + mRecipeSteps +
                "\n mNumberOfPeople=" + mNumberOfPeople +
                "\n mDescription='" + mDescription + '\'' +
                '}';
    }
}
