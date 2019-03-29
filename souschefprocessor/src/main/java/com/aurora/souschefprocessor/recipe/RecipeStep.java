package com.aurora.souschefprocessor.recipe;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A dataclass representing a step. It has  fields
 * mIngredients: a set of mIngredients contained in this recipe (could be null)
 * mRecipeTimers: a list of timers contained in this recipe (could be null)
 * mDescription:  the textual mDescription of this step, which was written in the original text,
 * possibly updated to indicate references to elements in mIngredients and mRecipeTimers
 * mIngredientDetectionDone: a boolean that indicates if the DetectIngredientsInStepTask task has been done
 * mTimerDetectionDone: a boolean that indicates if the DetectTimersInStepTask task has been done on this step
 */
public class RecipeStep {

    /**
     * A set of {@link Ingredient}s that were detected in this step
     */
    private Set<Ingredient> mIngredients = new HashSet<>();
    /**
     * A list of {@link RecipeTimer}s that were detected in this step (in order)
     */
    private List<RecipeTimer> mRecipeTimers = new ArrayList<>();
    /**
     * The original description of this step. This is the string where timers and ingredients have been
     * detected in
     */
    private String mDescription;

    /**
     * A boolean indicating whether the
     * {@link com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask} task
     * has been executed on this step
     */
    private boolean mIngredientDetectionDone;
    /**
     * A boolean indicating whether the
     * {@link com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask} task
     * has been executed on this step
     */
    private boolean mTimerDetectionDone;

    /**
     * Construct a step using the description that can be used to detect ingredients and timers
     *
     * @param description
     */

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

    /**
     * Clasic setter for {@link #mIngredients}, if the argument is null then the existing
     * set will be cleared instead of setting the existing set to null. Also sets the {@link #mIngredientDetectionDone}
     * boolean to true.
     *
     * @param ingredients The set to set as ingredients
     */
    public synchronized void setIngredients(Set<Ingredient> ingredients) {
        mIngredients.clear();
        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                // this also checks if the position of the ingredient is valid
                add(ingredient);
            }
        }
        mIngredientDetectionDone = true;
    }

    /**
     * Adds an ingredient to the set of ingredients if it is not null and its {@link Position}s are
     * legal in the {@link #mDescription} (see {@link Ingredient#arePositionsLegalInString(String)}
     * If the ingredients is null or one of its positions is not legal an {@link IllegalArgumentException}
     * is thrown.
     *
     * @param ingredient The ingredient to add to the list
     */
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

    /**
     * Clasic setter for {@link #mRecipeTimers}, if the argument is null then the existing
     * list will be cleared instead of setting the existing list to null. Also sets the {@link #mTimerDetectionDone}
     * boolean to true.
     *
     * @param recipeTimers The list to set as ingredients
     */
    public synchronized void setRecipeTimers(List<RecipeTimer> recipeTimers) {
        mRecipeTimers.clear();
        if (recipeTimers != null && !recipeTimers.isEmpty()) {
            for (RecipeTimer timer : recipeTimers) {
                // this also checks if the position of the timer is valid
                add(timer);
            }
        }
        mTimerDetectionDone = true;
    }

    /**
     * Adds an ingredient to the set of ingredients if it is not null and its {@link Position}s are
     * legal in the {@link #mDescription} (see {@link Ingredient#arePositionsLegalInString(String)}
     * If the recipetimer is null or its position is not legal an {@link IllegalArgumentException}
     * is thrown.
     *
     * @param recipeTimer The ingredient to add to the list
     */
    public synchronized void add(RecipeTimer recipeTimer) {
        // do nothing if ingredient is null
        if (recipeTimer != null && recipeTimer.getPosition().isLegalInString(mDescription)) {
            if (this.mRecipeTimers == null) {
                this.mRecipeTimers = new ArrayList<>();
            }
            this.mRecipeTimers.add(recipeTimer);
        } else {
            throw new IllegalArgumentException("recipeTimer is null or Position of timer is not " +
                    "legal in description\n" +
                    "RecipeTimer: " + recipeTimer + "\n" +
                    "Positions: " + recipeTimer.getPosition() + "\n" + 
                    "Description: " + mDescription + " ( " + mDescription.length() + " length)");
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


    /**
     * Clears {@link #mRecipeTimers} and set {@link #mTimerDetectionDone} to false. This should be called
     * when one wants to redo the {@link com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask}
     */
    public synchronized void unsetTimers() {
        mRecipeTimers.clear();
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
