package SouschefProcessor.Recipe;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A dataclass representing a step it has  fields
 * ingredients: a set of ingredients contained in this recipe (could be null)
 * recipeTimers: a list of timers contained in this recipe (could be null)
 * decription:  the textual description of this step, which was written in the original text,
 * possibly updated to indicate references to elements in ingredients and recipeTimers
 * ingredientDetected: a boolean that indicates if the DetectIngredientsInStepTask task has been done
 * timerDetected: a boolean that indicates if the DetectTimersInStepTask task has been done on this step
 */
public class RecipeStep {

    private HashSet<Ingredient> ingredients; //this could become a hashmap, with key the Ingredient and value the location in the description
    private ArrayList<RecipeTimer> recipeTimers;
    private String description;
    private boolean ingredientDetected = false;
    private boolean timerDetected = false;

    public RecipeStep(String description) {
        this.description = description;
    }

    public HashSet<Ingredient> getIngredients() {
        return ingredients;
    }

    public synchronized void setIngredients(HashSet<Ingredient> ingredients) {
        this.ingredients = ingredients;
        ingredientDetected = true;
    }

    // This should maybe check if ingredients != null, but maybe also create the HashSet if it is null
    // We could also initialize an empty HashSet in the constructor (but maybe still need to check if not null
    // to deal with setIngredients possibly setting ingredients to null
    public synchronized void addIngredient(Ingredient ingredient){
        if (this.ingredients != null) {
            this.ingredients.add(ingredient);
        }
    }

    public ArrayList<RecipeTimer> getRecipeTimers() {
        return recipeTimers;
    }

    public synchronized void setRecipeTimers(ArrayList<RecipeTimer> recipeTimers) {
        this.recipeTimers = recipeTimers;
        timerDetected = true;
    }

    // Same comment as for addIngredient
    public synchronized void addRecipeTimer(RecipeTimer recipeTimer){
        if (this.recipeTimers != null) {
            this.recipeTimers.add(recipeTimer);
        }
    }

    public boolean isIngredientDetected() {
        return ingredientDetected;
    }

    public boolean isTimerDetected() {
        return timerDetected;
    }

    public String getDescription() {
        return description;
    }

    public synchronized void unsetTimer() {
        recipeTimers = null;
        timerDetected = false;
    }


    @Override
    public String toString() {
        String ret = "RecipeStep:\n " + description + "\n ingredientDetected: " + ingredientDetected +
                "\n ingredients:\n";
        if (ingredientDetected) {
            for (Ingredient i : ingredients) {
                ret += "\t" + i;
            }
        }
        ret += "\n timerDetected: " + timerDetected;
        if (timerDetected) {
            for (RecipeTimer t : recipeTimers){
                ret += "\n RecipeTimer:" + t;
            }

        }
        return ret;
    }


}
