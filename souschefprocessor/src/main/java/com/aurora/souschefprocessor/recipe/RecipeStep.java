package com.aurora.souschefprocessor.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A dataclass representing a step it has  fields
 * mIngredients: a set of mIngredients contained in this recipe (could be null)
 * mRecipeTimers: a list of timers contained in this recipe (could be null)
 * mDescription:  the textual mDescription of this step, which was written in the original text,
 * possibly updated to indicate references to elements in mIngredients and mRecipeTimers
 * mIngredientDetected: a boolean that indicates if the DetectIngredientsInStepTask task has been done
 * mTimerDetected: a boolean that indicates if the DetectTimersInStepTask task has been done on this step
 */
public class RecipeStep {
    // this could become a hashmap, with key the Ingredient and value the location in the mDescription
    private Set<Ingredient> mIngredients;
    private List<RecipeTimer> mRecipeTimers;
    private String mDescription;
    private boolean mIngredientDetected;
    private boolean mTimerDetected;

    public RecipeStep(String description) {
        this.mDescription = description;
        this.mIngredients = new HashSet<>();
        this.mRecipeTimers = new ArrayList<>();
        this.mIngredientDetected = false;
        this.mTimerDetected = false;
    }

    public synchronized Set<Ingredient> getIngredients() {
        return mIngredients;
    }

    public synchronized void setIngredients(Set<Ingredient> ingredients) {
        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                // this also checks if the position of the ingredient is valid
                add(ingredient);
            }
        }
        mIngredientDetected = true;
    }

    // This should maybe check if mIngredients != null, but maybe also create the HashSet if it is null
    // We could also initialize an empty HashSet in the constructor (but maybe still need to check if not null
    // to deal with setIngredients possibly setting mIngredients to null
    public synchronized void add(Ingredient ingredient) {
        if (ingredient != null && ingredient.arePositionsLegalInString(mDescription)) {
            this.mIngredients.add(ingredient);
        } else {
            throw new IllegalArgumentException("Positions of ingredient are not legal!");
        }
    }

    public synchronized List<RecipeTimer> getRecipeTimers() {
        return mRecipeTimers;
    }

    public synchronized void setRecipeTimers(List<RecipeTimer> recipeTimers) {
        if (recipeTimers != null) {
            for (RecipeTimer timer : recipeTimers) {
                // this also checks if the position of the timer is valid
                add(timer);
            }
        }
        mTimerDetected = true;
    }

    // Same comment as for addIngredient
    public synchronized void add(RecipeTimer recipeTimer) {
        if (recipeTimer.getPosition().isLegalInString(mDescription)) {
            this.mRecipeTimers.add(recipeTimer);
        } else {
            throw new IllegalArgumentException("Position of timer is not legal in description");
        }

    }

    public boolean isIngredientDetected() {
        return mIngredientDetected;
    }

    public void setIngredientDetected(boolean ingredientDetected) {
        mIngredientDetected = ingredientDetected;
    }

    public boolean isTimerDetected() {
        return mTimerDetected;
    }

    public void setTimerDetected(boolean timerDetected) {
        mTimerDetected = timerDetected;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public synchronized void unsetTimer() {
        mRecipeTimers = null;
        mTimerDetected = false;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("RecipeStep:\n " + mDescription + "\n mIngredientDetected: " + mIngredientDetected +
                "\n mIngredients:\n");
        if (mIngredientDetected) {
            for (Ingredient i : mIngredients) {
                bld.append("\t" + i);
            }
        }
        bld.append("\n mTimerDetected: " + mTimerDetected);
        if (mTimerDetected) {
            for (RecipeTimer t : mRecipeTimers) {
                bld.append("\n RecipeTimer:" + t);
            }

        }
        return bld.toString();
    }


}
