package SouschefProcessor.Recipe;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A data class representing a recipe. It has 4 fields:
 * ingredients: a set of Ingredient objecs, this represents the ingredients needed for
 * this recipe.
 * recipeSteps: A list of recipeSteps in this recipe
 * amountOfPeople: the amount of people the basic recipe is for
 */
public class Recipe {
    protected HashSet<Ingredient> ingredients;
    protected ArrayList<RecipeStep> recipeSteps;
    protected int amountOfPeople;
    protected String description;

    public Recipe(HashSet<Ingredient> ingredients, ArrayList<RecipeStep> recipeSteps, int amountOfPeople, String description) {
        this.ingredients = ingredients;
        this.recipeSteps = recipeSteps;
        this.amountOfPeople = amountOfPeople;
        this.description = description;
    }

    public Recipe() {

    }

    public synchronized void setAmountOfPeople(int amountOfPeople) {
        this.amountOfPeople = amountOfPeople;
    }

    public synchronized void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<RecipeStep> getRecipeSteps() {
        return recipeSteps;
    }

    public synchronized void setRecipeSteps(ArrayList<RecipeStep> recipeSteps) {
        this.recipeSteps = recipeSteps;
    }

    public HashSet<Ingredient> getIngredients() {
        return ingredients;
    }

    public synchronized void setIngredients(HashSet<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }


}
