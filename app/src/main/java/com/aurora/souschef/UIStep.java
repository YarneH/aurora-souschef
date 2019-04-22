package com.aurora.souschef;

import com.aurora.souschefprocessor.recipe.RecipeStep;

public class UIStep extends RecipeStep {
    private String[] mCurrentDescription = null;
    private String[] mOriginalDescription = null;
    /**
     * Construct a step using the description that can be used to detect ingredients and timers
     *
     * @param description the description of this step
     */
    public UIStep(String description) {
        super(description);
        mOriginalDescription = new String[super.getRecipeTimers().size() + 1];
        mCurrentDescription = new String[super.getRecipeTimers().size() + 1];
    }

    public void changeNumberOfPeople(int originalNumber, int newNumber) {

    }
}
