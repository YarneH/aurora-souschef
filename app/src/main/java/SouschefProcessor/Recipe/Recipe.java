package SouschefProcessor.Recipe;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A data class representing a recipe. It has 4 fields:
 * ingredients: a set of Ingredient objecs, this represents the ingredients needed for
 * this recipe.
 * steps: A list of steps in this recipe
 * amountOfPeople: the amount of people the basic recipe is for
 */
public class Recipe {
    protected HashSet<Ingredient> ingredients;
    protected ArrayList<Step> steps;
    protected int amountOfPeople;
    protected String description;

    public Recipe(HashSet<Ingredient> ingredients, ArrayList<Step> steps, int amountOfPeople, String description) {
        this.ingredients = ingredients;
        this.steps = steps;
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

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public synchronized void setSteps(ArrayList<Step> steps) {
        this.steps = steps;
    }

    public HashSet<Ingredient> getIngredients() {
        return ingredients;
    }

    public synchronized void setIngredients(HashSet<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }


}
