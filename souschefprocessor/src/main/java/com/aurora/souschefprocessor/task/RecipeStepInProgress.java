package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.recipe.RecipeTimer;

import java.util.List;

import edu.stanford.nlp.util.CoreMap;

/**
 * A subclass of RecipeStep, it stores the annotations that were received by Aurora as well as the offset
 * that this step has towards the entire step.
 */
public class RecipeStepInProgress extends RecipeStep {

    /**
     * The annotation concerned with the description of this step
     */
    private List<CoreMap> mSentenceAnnotations;

    /**
     * The offset of the beginposition of this step, defaults to 0. This is the beginposition in the entire
     * text
     */
    private int mBeginPositionOffset = 0;

    /**
     * Construct a step using the description that can be used to detect ingredients and timers
     *
     * @param description the description of this step
     */
    public RecipeStepInProgress(String description) {
        super(description);

    }

    /**
     * Default getter
     *
     * @return the annotations of the description of this step
     */
    public List<CoreMap> getSentenceAnnotations() {
        return mSentenceAnnotations;
    }

    /**
     * Default setter
     *
     * @param annotations the new annotations for the description of this step
     */
    public void setSentenceAnnotations(List<CoreMap> annotations) {
        this.mSentenceAnnotations = annotations;
    }

    /**
     * Default setter
     *
     * @return the begin position offset of the annotations of the description of this step
     */
    public int getBeginPositionOffset() {
        return mBeginPositionOffset;
    }

    /**
     * Default setter
     *
     * @param mBeginPositionOffset the new begin position offset of the annotations of the description of this step
     */
    public void setBeginPosition(int mBeginPositionOffset) {
        this.mBeginPositionOffset = mBeginPositionOffset;
    }

    /**
     * Converts this to a {@link RecipeStep} by first cleaning up and trimming the description and then calling the
     * {@link RecipeStep#convertToRecipeStep()} method
     *
     * @return the recipe
     */
    public RecipeStep convertToRecipeStep() {
        cleanUp();
        return super.convertToRecipeStep();
    }

    /**
     * Trim the step, remove new lines and update positions
     */
    private void cleanUp() {
        mDescription = mDescription.replace("\n", " ");
        // trim the description and update the positions
        String trimmed = mDescription.trim();
        int offset = mDescription.indexOf(trimmed);

        for (RecipeTimer timer : mRecipeTimers) {
            timer.getPosition().addOffset(offset);
        }

        for (Ingredient ingredient : mIngredients) {
            ingredient.addOffsetToPositions(offset);
        }
    }

    /**
     * {@inheritDoc}
     * This overrides the super method by subtracting the {@link #mBeginPositionOffset} of the position
     * of the timers
     *
     * @param recipeTimers The list to set as ingredients
     */
    @Override
    public synchronized void setRecipeTimers(List<RecipeTimer> recipeTimers) {
        mRecipeTimers.clear();
        if (recipeTimers != null && !recipeTimers.isEmpty()) {
            for (RecipeTimer timer : recipeTimers) {
                // change the position of the timer by subtrackting the offset
                timer.getPosition().subtractOffset(mBeginPositionOffset);
                // this also checks if the position of the timer is valid
                add(timer);
            }
        }

        mTimerDetectionDone = true;
    }
}
