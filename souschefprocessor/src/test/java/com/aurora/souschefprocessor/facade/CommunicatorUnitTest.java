package com.aurora.souschefprocessor.facade;

import android.util.Log;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class CommunicatorUnitTest {

    private static List<String> validRecipes;
    private static List<String> invalidRecipes;
    private static Communicator communicator;
    private static CRFClassifier<CoreLabel> crfClassifier;
    @BeforeClass
    public static void initialize() {
        // load in the recipes
        List<String> recipes = DelegatorLongTest.initializeRecipes();
        // split into valid and invalid
        // the first 5 recipes are valid recipes
        validRecipes = recipes.subList(0, 5);
        invalidRecipes = recipes.subList(5, 8);

        // load in the model
        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        try {
            crfClassifier = CRFClassifier.getClassifier(modelName);
            // create the delegator object
            // parallel is better when the number of cores are only half
            // sequnetial performs faster
            communicator = new Communicator(crfClassifier);
        } catch (IOException | ClassNotFoundException e) {
        }
    }

    @Test
    public void Communicator_process_ThrowsExceptionForInvalidRecipe(){

        for (String text : invalidRecipes) {
            // Arrange
            // initialize on false
            boolean thrown = false;
            // Act
            try {
                communicator.process(text);
            } catch (Exception e) {
                // set thrown to true, this should happen
                Log.e("Woop", "Error was thrown", e);
                thrown = true;
            }
            // Assert
            // assert that an error was thrown
            assert (thrown);
        }
    }
}
