package com.aurora.souschefprocessor.recipe;

import android.util.Log;

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
 * mIngredientDetectionDone: a boolean that indicates if the DetectIngredientsInStepTask task has been done
 * mTimerDetectionDone: a boolean that indicates if the DetectTimersInStepTask task has been done on this step
 */
public class RecipeStep {
    // this could become a hashmap, with key the Ingredient and value the location in the mDescription
    private Set<Ingredient> mIngredients;
    private List<RecipeTimer> mRecipeTimers;
    private String mDescription;
    private boolean mIngredientDetectionDone;
    private boolean mTimerDetectionDone;

    public RecipeStep(String description) {
        this.mDescription = description;
        this.mIngredients = new HashSet<>();
        this.mRecipeTimers = new ArrayList<>();
        this.mIngredientDetectionDone = false;
        this.mTimerDetectionDone = false;
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
        mIngredientDetectionDone = true;
    }

    // This should maybe check if mIngredients != null, but maybe also create the HashSet if it is null
    // We could also initialize an empty HashSet in the constructor (but maybe still need to check if not null
    // to deal with setIngredients possibly setting mIngredients to null
    public synchronized void add(Ingredient ingredient) {
        // do nothing if ingredient is null
        if (ingredient != null) {
            try {
                if (ingredient.arePositionsLegalInString(mDescription)) {
                    if (this.mIngredients == null) {
                        this.mIngredients = new HashSet<>();
                    }
                    this.mIngredients.add(ingredient);
                } else {

                    throw new IllegalArgumentException("Positions of ingredient are not legal!\n" +
                            "Ingredient: " + ingredient + "\n" +
                            "Positions: " + ingredient.getQuantityPosition() + ", " +
                            ingredient.getUnitPosition() + ", " + ingredient.getNamePosition() +
                            "\nDescription: " + mDescription + " ( " + mDescription.length() + " length)");

                }
            } catch (IllegalArgumentException iae) {
                Log.e("RECIPESTEP", "Add ingredient failed: ", iae);
            }
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
        mTimerDetectionDone = true;
    }

    // Same comment as for addIngredient
    public synchronized void add(RecipeTimer recipeTimer) {
        // do nothing if ingredient is null
        if (recipeTimer != null) {
            if (recipeTimer.getPosition().isLegalInString(mDescription)) {
                if (this.mRecipeTimers == null) {
                    this.mRecipeTimers = new ArrayList<>();
                }
                this.mRecipeTimers.add(recipeTimer);
            } else {
                throw new IllegalArgumentException("Positions of the recipe timer are not legal!\n" +
                        "RecipeTimer: " + recipeTimer + "\n" +
                        "Positions: " + recipeTimer.getPosition() +
                        "\nDescription: " + mDescription + " ( " + mDescription.length() + " length)");
            }
        }
    }

    public boolean isIngredientDetectionDone() {
        return mIngredientDetectionDone;
    }

    public void setIngredientDetectionDone(boolean ingredientDetectionDone) {
        this.mIngredientDetectionDone = ingredientDetectionDone;
    }

    public boolean isTimerDetectionDone() {
        return mTimerDetectionDone;
    }

    public void setTimerDetectionDone(boolean timerDetectionDone) {
        mTimerDetectionDone = timerDetectionDone;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public synchronized void unsetTimer() {
        mRecipeTimers = null;
        mTimerDetectionDone = false;
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();
        bld.append("RecipeStep:\n ");
        bld.append(mDescription);
        bld.append("\n");
        bld.append("IngredientDetectionDone ");
        bld.append(mIngredientDetectionDone);
        if (mIngredientDetectionDone) {
            bld.append("\nIngredients:\n");
            for (Ingredient i : mIngredients) {
                bld.append("Ingredient:\t");
                bld.append(i);
                bld.append("\n");
            }
        }
        bld.append("TimerDetectionDone: ");
        bld.append(mTimerDetectionDone);
        if (mTimerDetectionDone) {
            bld.append("\nTimers:\n");
            for (RecipeTimer t : mRecipeTimers) {
                bld.append("RecipeTimer:\t");
                bld.append(t);
                bld.append("\n");
            }

        }
        return bld.toString();
    }


}
