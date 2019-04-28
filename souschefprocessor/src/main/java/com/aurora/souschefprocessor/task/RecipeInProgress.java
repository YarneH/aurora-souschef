package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

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
     * An extractedtext object from Aurora
     */
    private ExtractedText mExtractedText;

    private List<RecipeStepInProgress> mStepsInProgress;

    public List<RecipeStepInProgress> getStepsInProgress() {
        return mStepsInProgress;
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


    public void setStepsInProgress(List<RecipeStepInProgress> mStepsInProgress) {
        this.mStepsInProgress = mStepsInProgress;
    }

    /**
     * Converts the RecipeInProgress to a Recipe object by dropping the two additional fields and
     * converting the {@link RecipeStepInProgress} steps to {@link com.aurora.souschefprocessor.recipe.RecipeStep}
     * steps
     *
     * @return the converted recipe
     */
    public Recipe convertToRecipe() {
        for (RecipeStepInProgress step : mStepsInProgress) {
            mRecipeSteps.add(step.convertToRecipeStep());
        }
        return new Recipe(mIngredients, mRecipeSteps, mNumberOfPeople, mDescription);
    }

    public ExtractedText getExtractedText() {
        return mExtractedText;
    }





}
