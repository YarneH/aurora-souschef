/**
 * This package is responsible for constructing a {@link com.aurora.souschefprocessor.recipe.Recipe} from an {@link android.view.inputmethod.ExtractedText}
 * object. This is done by executing {@link com.aurora.souschefprocessor.task.AbstractProcessingTask} tasks on a
 * {@link com.aurora.souschefprocessor.task.RecipeInProgress} that has
 * {@link com.aurora.souschefprocessor.task.RecipeStepInProgress} steps.
 *
 *
 * This flow that should be called is as follows
 * 1 {@link com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask}: The text is split in to three
 * sections: ingredients, steps and overview, these are set in the {@link com.aurora.souschefprocessor.task.RecipeInProgress}
 * 2 {@link com.aurora.souschefprocessor.task.sectiondivider.DetectNumberOfPeopleTask} The number of people this
 * recipe is for is detected in the description
 * 3 {@link com.aurora.souschefprocessor.task.sectiondivider.SplitStepsTask}: The steps are split in to distinct steps
 * 4 {@link com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask}: The ingredients in the
 * list of ingredients are detected
 * 5 {@link com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask}: The ingredients are
 * detected in each step
 * 6{@link com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask}: The timers are detected in each step
 *
 * Some of these steps can be done in parallel. The dependencies are as follows
 *     |-- 2
 *     |         |-- 6
 *     |         |
 * 1 --|-- 3 ----|
 *     |         |
 *     |         |-- 5
 *     |-- 4 ----|
 *
 * With element to the right depending on element to their left
 *
 */
package com.aurora.souschefprocessor.task;
