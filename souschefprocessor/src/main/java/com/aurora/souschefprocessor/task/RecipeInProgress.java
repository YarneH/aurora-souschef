package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * A subclass of Recipe, representing a Recipe Object that is being constructed. It has three
 * additional fields:
 * mIngredientsString: a string representing the mIngredients list
 * mStepsString: a string representing the different mRecipeSteps in the recipe
 * mOriginalText: a string that is the original text that was extracted by Aurora
 */
public class RecipeInProgress extends Recipe {
    /**
     * The string representing the ingredients list that was detected using the
     * {@link com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask}
     */
    private String mIngredientsString;
    /**
     * The string representing the steps list that was detected using the
     * {@link com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask}
     */
    private String mStepsString;
    /**
     * The original text given by Aurora (could change to Annotation)
     */
    private String mOriginalText;

    /**
     * An extractedtet object from Aurora
     */
    private ExtractedText mExtractedText;


    public RecipeInProgress(String originalText) {
        super();
        this.mOriginalText = originalText;
    }

    public RecipeInProgress(ExtractedText originalText) {
        super();
        this.mExtractedText = originalText;
    }

    @Override
    public String toString() {
        return "RECIPE{" +
                "INGREDIENTS='" + mIngredientsString + "\n" +
                ", STEPS='" + mStepsString + "\n" +
                ", DESCRIPTION='" + mDescription + "\n" +
                '}';
    }


    public String getOriginalText() {
        return mOriginalText;
    }

    public synchronized String getStepsString() {
        return mStepsString;
    }

    public synchronized void setStepsString(String stepsString) {
        this.mStepsString = stepsString;
    }

    public synchronized String getIngredientsString() {
        return mIngredientsString;
    }

    public synchronized void setIngredientsString(String ingredientsString) {
        this.mIngredientsString = ingredientsString;
    }

    /**
     * Converts the RecipeInProgress to a Recipe object by dropping the two additional fields
     *
     * @return the converted recipe
     */
    public Recipe convertToRecipe() {
        return new Recipe(mIngredients, mRecipeSteps, mNumberOfPeople, mDescription);
    }

    public ExtractedText getExtractedText() {
        return mExtractedText;
    }
}
