package com.aurora.souschefprocessor.facade;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class CommunicatorUnitTest {

    private static List<String> validRecipesFromPlainText;
    private static List<String> invalidRecipesFromPlainText;
    private static Communicator communicator;
    private static CRFClassifier<CoreLabel> crfClassifier;
    @BeforeClass
    public static void initialize() {
        // load in the recipes
        List<String> recipesFromPlainText = DelegatorLongTest.initializeRecipes();
        // split into valid and invalid
        // the first 5 recipes are valid recipes
        validRecipesFromPlainText = recipesFromPlainText.subList(0, 5);
        invalidRecipesFromPlainText = recipesFromPlainText.subList(5, 8);

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


    }
}
