package com.aurora.souschefprocessor.facade;


import com.aurora.auroralib.ExtractedText;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SouschefProcessorCommunicatorUnitTest {

    private static List<String> validRecipes;
    private static List<String> invalidRecipes;
    private static SouschefProcessorCommunicator communicator;
    private static CRFClassifier<CoreLabel> crfClassifier;

    @BeforeClass
    public static void initialize() {
        // load in the recipes
        List<String> jsonRecipes = DelegatorLongTest.initializeRecipesJSON();
        // split into valid and invalid
        // the first 6 recipes are valid recipes
        validRecipes = jsonRecipes.subList(0, 7);
        invalidRecipes = jsonRecipes.subList(7, jsonRecipes.size());

        // load in the model and create the communicator
        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        try {
            crfClassifier = CRFClassifier.getClassifier(modelName);
            communicator = new SouschefProcessorCommunicator(null, crfClassifier);
        } catch (IOException | ClassNotFoundException e) {
        }
    }

    /**
     * Assert that invalid recipes will throw an error
     */
    @Test
    public void Communicator_process_ThrowsExceptionForInvalidRecipe() throws RecipeDetectionException{
        for (String json : invalidRecipes) {
            boolean thrown = false;
            try {
                ExtractedText text = ExtractedText.fromJson(json);
                communicator.process(text);
            } catch (RecipeDetectionException e) {
                thrown = true;
            }
            assertTrue("no exception was thrown for json " + json, thrown);
        }
    }

    /**
     * Assert that the processing for valid recipes does not throw any errors
     */
    @Test
    public void Communicator_process_NoExceptionForValidRecipe() throws RecipeDetectionException{
        for (String json : validRecipes) {
            boolean thrown = false;
            String message = "";
            try {
                ExtractedText text = ExtractedText.fromJson(json);
                communicator.process(text);
            } catch (RecipeDetectionException e) {
                thrown = true;
                message = e.getMessage();

            }
            assertFalse("an exception was thrown for json " + json + "/n" +
                    "Exception = " + message, thrown);
        }
    }
}
