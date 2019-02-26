package com.aurora.souschef.souschefprocessor.recipe;

import java.util.List;
import java.util.Set;

/**
 * A dataclass representing a step it has  fields
 * mIngredients: a set of mIngredients contained in this recipe (could be null)
 * mRecipeTimers: a list of timers contained in this recipe (could be null)
 * decription:  the textual mDescription of this step, which was written in the original text,
 * possibly updated to indicate references to elements in mIngredients and mRecipeTimers
 * mIngredientDetected: a boolean that indicates if the DetectIngredientsInStepTask task has been done
 * mTimerDetected: a boolean that indicates if the DetectTimersInStepTask task has been done on this step
 */
public class RecipeStep {

    private Set<Ingredient> mIngredients; //this could become a hashmap, with key the Ingredient and value the location in the mDescription
    private List<RecipeTimer> mRecipeTimers;
    private String mDescription;
    private boolean mIngredientDetected = false;
    private boolean mTimerDetected = false;

    public RecipeStep(String description) {
        this.mDescription = description;
    }

    public Set<Ingredient> getIngredients() {
        return mIngredients;
    }

    public synchronized void setIngredients(Set<Ingredient> ingredients) {
        this.mIngredients = ingredients;
        mIngredientDetected = true;
    }

    // This should maybe check if mIngredients != null, but maybe also create the HashSet if it is null
    // We could also initialize an empty HashSet in the constructor (but maybe still need to check if not null
    // to deal with setIngredients possibly setting mIngredients to null
    public synchronized void addIngredient(Ingredient ingredient) {
        if (this.mIngredients != null) {
            this.mIngredients.add(ingredient);
        }
    }

    public List<RecipeTimer> getRecipeTimers() {
        return mRecipeTimers;
    }

    public synchronized void setRecipeTimers(List<RecipeTimer> recipeTimers) {
        this.mRecipeTimers = recipeTimers;
        mTimerDetected = true;
    }

    // Same comment as for addIngredient
    public synchronized void addRecipeTimer(RecipeTimer recipeTimer) {
        if (this.mRecipeTimers != null) {
            this.mRecipeTimers.add(recipeTimer);
        }
    }

    public boolean isIngredientDetected() {
        return mIngredientDetected;
    }

    public boolean isTimerDetected() {
        return mTimerDetected;
    }

    public String getDescription() {
        return mDescription;
    }

    public synchronized void unsetTimer() {
        mRecipeTimers = null;
        mTimerDetected = false;
    }


    @Override
    public String toString() {
        String ret = "RecipeStep:\n " + mDescription + "\n mIngredientDetected: " + mIngredientDetected +
                "\n mIngredients:\n";
        if (mIngredientDetected) {
            for (Ingredient i : mIngredients) {
                ret += "\t" + i;
            }
        }
        ret += "\n mTimerDetected: " + mTimerDetected;
        if (mTimerDetected) {
            for (RecipeTimer t : mRecipeTimers) {
                ret += "\n RecipeTimer:" + t;
            }

        }
        return ret;
    }


}
