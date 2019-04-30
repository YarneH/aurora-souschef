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
    private List<CoreMap> sentenceAnnotation;

    /**
     * The offset of the beginposition of this step, defaults to 0. This is the beginposition in the entire
     * text
     */
    private int beginPositionOffset = 0;

    /**
     * Construct a step using the description that can be used to detect ingredients and timers
     *
     * @param description the description of this step
     */
    public RecipeStepInProgress(String description) {
        super(description);


    }

    public List<CoreMap> getSentenceAnnotation() {
        return sentenceAnnotation;
    }

    public void setSentenceAnnotation(List<CoreMap> annotations) {
        this.sentenceAnnotation = annotations;
    }

    public int getBeginPositionOffset() {
        return beginPositionOffset;
    }

    public void setBeginPositionOffset(int beginPositionOffset) {
        this.beginPositionOffset = beginPositionOffset;
    }

    /**
     * Converts this to a {@link RecipeStep}
     *
     * @return the
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
     * This overrides the super method by subtracting the {@link #beginPositionOffset} of the position
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
                timer.getPosition().subtractOffset(beginPositionOffset);
                // this also checks if the position of the timer is valid

                add(timer);
            }
        }

        mTimerDetectionDone = true;
    }
}
