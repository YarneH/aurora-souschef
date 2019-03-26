package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.List;

/**
 * A AbstractProcessingTask that splits the string representing the mRecipeSteps into RecipeStep objects
 */
public class SplitStepsTask extends AbstractProcessingTask {

    public SplitStepsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }


    /**
     * This will split the stepsString in the RecipeInProgress Object into mRecipeSteps and modifies the
     * recipe object so that the mRecipeSteps are set
     */
    public void doTask() {
        String text = mRecipeInProgress.getStepsString();

        List<RecipeStep> recipeStepList = divideIntoSteps(text);
        if(recipeStepList == null || recipeStepList.isEmpty()){
            throw new RecipeDetectionException("No steps were detected, this is probably not a recipe");
        }
        this.mRecipeInProgress.setRecipeSteps(recipeStepList);
    }

    /**
     * This function splits the text, describing the mRecipeSteps, into different mRecipeSteps of the recipe
     *
     * @return A list of all mRecipeSteps in order
     */
    private List<RecipeStep> divideIntoSteps(String steps) {
        //TODO generate functionality to split attribute stepsText
        List<RecipeStep> list = new ArrayList<>();

        // TODO based on numeric and

        //split based on sections (punctuation followed by newline indicates block of text)
        String[] pointAndNewLine = steps.split("\\p{Punct}\n");
        if (pointAndNewLine.length > 1) {
            for (String line : pointAndNewLine) {
                if (line.charAt(line.length() - 1) != '.') {
                    line += '.';
                }
                list.add(new RecipeStep(line));
            }

        } else {
            list = splitStepsBySplittingOnPunctuation(steps);

        }
        return list;
    }

    /**
     * Splits the text on punctuation
     *
     * @param steps the text to be splitted
     * @return a list with recipesteps
     */
    private List<RecipeStep> splitStepsBySplittingOnPunctuation(String steps) {
        // A boolean that indicates if this is the first char of the sentence
        // This is used to make sure that the first character is not a whitspace
        boolean firstChar = true;
        char[] characters = steps.toCharArray();
        StringBuilder bld = new StringBuilder();
        List<RecipeStep> list = new ArrayList<>();
        for (char c : characters) {
            // if this is not the first character while also being a whitespace

            if ((!firstChar || !Character.isWhitespace(c))) {
                if (c != '\n') {
                    bld.append(c);
                } else {
                    // if a new line is present
                    bld.append(" ");
                }
                // set firstChar to false
                firstChar = false;
            }
            // if this is a punctuation character end the sentence and make a step
            if (c == '.' || c == '!') {
                bld.setCharAt(0, Character.toUpperCase(bld.charAt(0)));
                list.add(new RecipeStep(bld.toString()));

                // make a new builder and set the boolean back to true
                bld = new StringBuilder();
                firstChar = true;
            }
        }
        return list;
    }


}
