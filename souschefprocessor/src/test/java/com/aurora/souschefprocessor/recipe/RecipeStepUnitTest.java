package com.aurora.souschefprocessor.recipe;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.util.CoreMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


public class RecipeStepUnitTest {

    @Test
    public void RecipeStep_addTimer_PositionOfTimerBiggerThanLengthOfStepDescriptionThrowsException() {
        /*
         * The position of a timer cannot be bigger than the length of the original text, constructing this should
         * throw an exception
         */

        // Arrange
        String originalText = "This is the original Text";
        RecipeStep step = new RecipeStep(originalText);
        // upper and lowerbound are irrelevant for this test
        int lowerbound = 40;
        int upperbound = 80;

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        // Arrange
        int beginIndex = 0;
        int endIndex = originalText.length() + 1;
        boolean case1Thrown = false;
        Position pos = new Position(beginIndex, endIndex);
        RecipeTimer timer = new RecipeTimer(upperbound, lowerbound, pos);
        // Act
        try {
            step.add(timer);
        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        // Assert
        assertTrue ("No exception was thrown for postion "+ pos +"in step " +step, case1Thrown);

        // case 2 both too big
        // Arrange
        beginIndex = originalText.length();
        boolean case2Thrown = false;
        pos = new Position(beginIndex, endIndex);
        timer = new RecipeTimer(upperbound, lowerbound, pos);
        // Act
        try {
            step.add(timer);
        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        // Assert
        assertTrue ("No exception was thrown for postion "+ pos +"in step " +step, case2Thrown);
    }

    @Test
    public void RecipeStep_convertUnit_correctConversion() {
        /*
         * The conversion for as step is correct
         */
        // arrange
        // Create an empty ExtractedText for RecipeInProgress argument
        ExtractedText emptyExtractedText = new ExtractedText("", Collections.emptyList());

        RecipeInProgress rip = new RecipeInProgress(emptyExtractedText, "");
        EnumMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new EnumMap<>(Ingredient.PositionKeysForIngredients.class);
        // Add the ingredients to the recipe
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }
        ListIngredient ingredient = new ListIngredient("olive oil", "cup", 1 / 2.0, "1/2 cup extra-virgin olive oil, divided", irrelevantPositions);
        rip.setIngredients(new ArrayList<>(Collections.singleton(ingredient)));

        // construct the step and add it to the recipe
        String originalDescription = "Heat 0.25 cup oil in a large deep-sided skillet over medium-high";

        RecipeStepInProgress step = new RecipeStepInProgress(originalDescription);

        // annotate the step
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator());
        pipeline.addAnnotator(new WordsToSentencesAnnotator());
        pipeline.addAnnotator(new POSTaggerAnnotator());
        Annotation annotation = new Annotation(step.getDescription());
        pipeline.annotate(annotation);
        List<CoreMap> sentencesInAnnotation = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        step.setSentenceAnnotations(sentencesInAnnotation);
        step.setBeginPosition(0);

        rip.setStepsInProgress(Collections.singletonList(step));

        // detect the ingredients in the step (so the positions are set)
        DetectIngredientsInStepTask task = new DetectIngredientsInStepTask(rip, 0);
        task.doTask();

        // act & assert
        // convert the step
        step.convertUnit(true);
        assertNotEquals("The description is not as expected after conversion", "Heat 60 milliliter oil in a large deep-sided skillet over medium-high", step.getDescription());

        // act & assert
        // convert back
        step.convertUnit(false);
        assertEquals("The description is not the same after converting twice", originalDescription, step.getDescription());
    }
}
