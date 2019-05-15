package com.aurora.souschefprocessor.task;


import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.task.sectiondivider.SplitStepsTask;

import org.apache.xerces.impl.xpath.regex.REUtil;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

import static org.junit.Assert.assertEquals;

public class SplitStepsTaskUnitTest {
    private static RecipeInProgress recipe;
    private static SplitStepsTask splitStepsTask;
    private static String originalText;
    private static String stepList;

    private static RecipeInProgress recipeAcrossNewline;
    private static String stepListAcrossNewline;
    private static SplitStepsTask splitStepsTaskAcrossNewline;
    private static ExtractedText testEmptyExtractedText;

    private static AnnotationPipeline sPipeline;

    private static String mockAuroraOutput(String stepList) {
        Annotation annotatedList = new Annotation(stepList);
        if (sPipeline == null) {
            sPipeline = new AnnotationPipeline();
            sPipeline.addAnnotator(new TokenizerAnnotator());
            sPipeline.addAnnotator(new WordsToSentencesAnnotator());
            sPipeline.addAnnotator(new POSTaggerAnnotator());
        }
        sPipeline.annotate(annotatedList);
        ProtobufAnnotationSerializer serializer = new ProtobufAnnotationSerializer(true);
        ExtractedText aurora = new ExtractedText("", Collections.emptyList());
        aurora.setTitle("");
        Section s = new Section();
        s.setBody(stepList);
        s.setTitle("");
        s.setBodyAnnotationProto(serializer.toProto(annotatedList));
        aurora.addSection(s);
        String json = aurora.toJSON();

        return json;
    }

    @BeforeClass
    public static void initialize() {

        stepList = initializeStepList();
        String json = mockAuroraOutput(stepList);
        recipe = new RecipeInProgress(ExtractedText.fromJson(json));
        recipe.setStepsString(stepList);
        splitStepsTask = new SplitStepsTask(recipe);

        stepListAcrossNewline = initializeStepListAcrossNewline();
        json = mockAuroraOutput(stepListAcrossNewline);
        ExtractedText text = ExtractedText.fromJson(json);

        recipeAcrossNewline = new RecipeInProgress(ExtractedText.fromJson(json));
        recipeAcrossNewline.setStepsString(stepListAcrossNewline);
        splitStepsTaskAcrossNewline = new SplitStepsTask(recipeAcrossNewline);
        testEmptyExtractedText = new ExtractedText("", Collections.emptyList());
    }


    private static String initializeStepList() {
        return "In a medium bowl, with a potato masher or a fork, mash the beans with the soy sauce, " +
                "chopped pepper; and ginger, until pureed but not smooth.\n" +
                "Spoon into a small serving dish and top with scallion.\n" +
                "Serve with sesame crackers.";
    }

    private static String initializeStepListAcrossNewline() {
        return "Heat the oil in a medium skillet over medium heat. Add the garlic and stir for about a\n" +
                "minute. Then add the beans with their liquid. Mash the beans with a potato masher or the\n" +
                "back of a spoon until you have a coarse puree, then cook, stirring regularly, until the beans\n" +
                "are thickened just enough to hold their shape in a spoon, about 10 minutes.  Taste and add\n" +
                "up to ¼ teaspoon salt.\n";
    }

    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
        recipe.setStepsString(stepList);
        recipeAcrossNewline.setIngredientsString(null);
        recipeAcrossNewline.setStepsString(stepListAcrossNewline);
    }

    @Test
    public void SplitStepsTask_doTask_setHasBeenSet() throws RecipeDetectionException {
        /**
         * After doing the tasks the recipeSteps list cannot be null
         */
        // Act
        splitStepsTask.doTask();
        // Assert
        assert (recipe.getRecipeSteps() != null);
    }

    @Test
    public void SplitStepsTask_doTask_setHasCorrectSize() throws RecipeDetectionException {
        /**
         * The correct amount of steps is detected
         */
        // Act
        splitStepsTask.doTask();

        // Assert
        assert (recipe.getStepsInProgress().size() == 3);
    }

    @Test
    public void SplitStepsTask_doTask_setHasCorrectValues() throws RecipeDetectionException {
        /**
         * The correct steps are detected
         */
        // Act
        splitStepsTask.doTask();
        // Assert
        assert (recipe.getStepsInProgress().get(0).getDescription()
                .equals("In a medium bowl, with a potato masher or a fork, " +
                        "mash the beans with the soy sauce, chopped pepper; and ginger, until pureed but not smooth."));
        assertEquals("The step is not correct", "Spoon into a small serving dish and top with scallion.", recipe.getStepsInProgress().get(1).getDescription() );

        assertEquals("The step is not correct", "Serve with sesame crackers.", recipe.getStepsInProgress().get(2).getDescription() );

    }

    @Test
    public void SplitStepsTask_doTask_setHasCorrectSizeAcrossNewline() throws RecipeDetectionException{
        /**
         * If the steps have a new line in the middle of a sentence, the correct amount of steps
         * is detected
         */
        // Act
        splitStepsTaskAcrossNewline.doTask();
        // Assert
        assert (recipeAcrossNewline.getStepsInProgress().size() == 5);
    }

    @Test
    public void SplitStepsTask_doTask_ExceptionThrownWhenStepStringIsEmpty() {
        /**
         * If the step string is empty then this is probably not a recipe, throw an error
         */
        // Arrange
        RecipeInProgress emptyStep = new RecipeInProgress(testEmptyExtractedText);
        emptyStep.setStepsString("");
        SplitStepsTask task = new SplitStepsTask(emptyStep);
        boolean thrown = false;
        // Act
        try {
            task.doTask();
        } catch (Exception e) {
            thrown = true;
        }
        assert (thrown);

    }

    @Test
    public void SplitStepsTask_doTask_setHasCorrectValuesAcrossNewline() throws RecipeDetectionException{
        /**
         * If the steps have a new line in the middle of a sentence, the correct steps are detected
         */

        // Act
        splitStepsTaskAcrossNewline.doTask();
        // Assert
        assert (recipeAcrossNewline.getStepsInProgress().get(0).getDescription()
                .equals("Heat the oil in a medium skillet over medium heat."));
        assert (recipeAcrossNewline.getStepsInProgress().get(1).getDescription()
                .equals("Add the garlic and stir for about a minute."));
        assert (recipeAcrossNewline.getStepsInProgress().get(2).getDescription()
                .equals("Then add the beans with their liquid."));
        assert (recipeAcrossNewline.getStepsInProgress().get(3).getDescription()
                .equals("Mash the beans with a potato masher or the " +
                        "back of a spoon until you have a coarse puree, then cook, stirring regularly, until the beans " +
                        "are thickened just enough to hold their shape in a spoon, about 10 minutes."));
        assert (recipeAcrossNewline.getStepsInProgress().get(4).getDescription()
                .equals("Taste and add up to ¼ teaspoon salt."));
    }


}
