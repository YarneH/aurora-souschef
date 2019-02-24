package com.aurora.souschef.SouchefProcessor.Recipe;

import java.util.List;
import java.util.Set;

/**
 * A data class representing a recipe. It has 4 fields:
 * mIngredients: a set of Ingredient objecs, this represents the mIngredients needed for
 * this recipe.
 * mRecipeSteps: A list of mRecipeSteps in this recipe
 * mNumberOfPeople: the amount of people the basic recipe is for
 * mDescription: a description of the recipe (could be that it is not present)
 */
public class Recipe {
    protected Set<Ingredient> mIngredients;
    protected List<RecipeStep> mRecipeSteps;
    protected int mNumberOfPeople;
    protected String mDescription;


    public Recipe(Set<Ingredient> ingredients, List<RecipeStep> recipeSteps, int numberOfPeople, String description) {
        this.mIngredients = ingredients;
        this.mRecipeSteps = recipeSteps;
        this.mNumberOfPeople = numberOfPeople;
        this.mDescription = description;
    }

    public Recipe() {

    }

    public synchronized void setNumberOfPeople(int numberOfPeople) {
        this.mNumberOfPeople = numberOfPeople;
    }

    public synchronized void setDescription(String description) {
        this.mDescription = description;
    }

    public List<RecipeStep> getRecipeSteps() {
        return mRecipeSteps;
    }

    public synchronized void setRecipeSteps(List<RecipeStep> recipeSteps) {
        this.mRecipeSteps = recipeSteps;
    }

    public Set<Ingredient> getIngredients() {
        return mIngredients;
    }

    public synchronized void setIngredients(Set<Ingredient> ingredients) {
        this.mIngredients = ingredients;
    }


}
