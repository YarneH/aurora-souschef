package com.aurora.souschefprocessor.facade;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.Recipe;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class DelegatorLongTest {
    private static List<String> validRecipesFromPlainText;
    private static List<String> invalidRecipesFromPlainText;

    private static List<ExtractedText> validRecipesJSON;
    private static List<ExtractedText> invalidRecipesJSON;
    private static Delegator delegator;
    private static CRFClassifier<CoreLabel> crfClassifier;

    private static List<ExtractedText> initializeRecipesJSON() {
        String filename = "src/test/java/com/aurora/souschefprocessor/facade/json-recipes.txt";
        List<ExtractedText> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), StandardCharsets.UTF_8));

            String line = reader.readLine();

            while (line != null) {
                list.add(ExtractedText.fromJson(line));
                line = reader.readLine();
            }
        } catch (IOException io) {
            System.out.println(io);
        }
        return list;
    }

    @BeforeClass
    public static void initialize() {


        List<ExtractedText> jsonRecipes = initializeRecipesJSON();
        validRecipesJSON = jsonRecipes.subList(0, 7);
        invalidRecipesJSON = jsonRecipes.subList(7, jsonRecipes.size());

        // load in the model
        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        try {
            crfClassifier = CRFClassifier.getClassifier(modelName);
            // create the delegator object
            // parallel is better when the number of cores are only half
            // sequnetial performs faster
            delegator = new Delegator(crfClassifier, false);
        } catch (IOException | ClassNotFoundException e) {
        }


    }

    /**
     * Read in the testrecipes
     *
     * @return A list of testrecipes
     */
    public static List<String> initializeRecipes() {
        String filename = "src/test/java/com/aurora/souschefprocessor/facade/recipes.txt";
        List<String> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), StandardCharsets.UTF_8));
            StringBuilder bld = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                if (!line.equals("----------")) {
                    bld.append(line + "\n");
                } else {
                    list.add(bld.toString());
                    bld = new StringBuilder();

                }
                line = reader.readLine();
            }
            list.add(bld.toString());
        } catch (IOException io) {
            System.err.print(io);
        }

        return list;
    }


    @Test
    public void Delegator_processText_NoExceptionsInDelegatorForValidRecipesJSON() {
        /**
         * Check that no exceptions are thrown when these recipes are read in
         */


        for (ExtractedText text : validRecipesJSON) {
            Recipe recipe = delegator.processText(text);
            System.out.println(recipe + "\n--------------------------------");

        }
    }


    @Test
    public void Delegator_processText_ExceptionsInDelegatorForInvalidRecipesJSON() {
        /**
         * Check that no exceptions are thrown when these recipes are read in
         */

        for (ExtractedText text : invalidRecipesJSON) {
            // Arrange
            // initialize on false
            boolean thrown = false;
            // Act
            try {
                Recipe recipe = delegator.processText(text);
                System.out.println(recipe);
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

    @Test
    public void Delegator_processText_timeForDoingTasksNonParallelIsLowerThanThreshold() {
        // Arrange
        // non-parallelize
        System.out.println("non-parallelize");
        // parallelize flag to false in delegator
        delegator = new Delegator(crfClassifier, false);
        int average_non = 0;

        // Act
        for (ExtractedText recipeText : validRecipesJSON) {
            // do the processing and add the time this processing costed
            long start = System.currentTimeMillis();
            delegator.processText(recipeText);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_non += time;

        }
        // divide by the amount of recipes processed to get the average
        average_non = average_non / validRecipesJSON.size();

        // Assert
        System.out.println(average_non + "  NON PARALLEL TIME");
        assert (average_non < 4000);

    }

    @Test
    public void Delegator_processText_timeForDoingTasksParallelIsLowerThanThreshold() {

        // Arrange
        int average_para = 0;
        System.out.println("parallelize");
        delegator = new Delegator(crfClassifier, true);

        // Act
        for (ExtractedText text : validRecipesJSON) {

            long start = System.currentTimeMillis();
            Recipe recipe = delegator.processText(text);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_para += time;
        }
        average_para = average_para / validRecipesJSON.size();


        // Assert
        System.out.println(average_para + "  PARALLEL TIME");
        assert (average_para < 15000);


    }

    @After
    public void wipeDelegator() {
        delegator = new Delegator(crfClassifier, true);
        List<ExtractedText> jsonRecipes = initializeRecipesJSON();
        validRecipesJSON = jsonRecipes.subList(0, 7);
        invalidRecipesJSON = jsonRecipes.subList(7, jsonRecipes.size());

    }

    @Test
    public void test_with_new_auroralib() {
        String contents = null;
        try {
            contents = new String(Files.readAllBytes(Paths.get("input.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExtractedText text = ExtractedText.fromJson(contents);

        Recipe r = delegator.processText(text);
        Position pos = r.getRecipeSteps().get(2).getIngredients().get(0).getQuantityPosition();
        System.out.println(r.getRecipeSteps().get(2).getDescription().substring(pos.getBeginIndex(), pos.getEndIndex()));
        System.out.println(r.getRecipeSteps().get(2).getDescription());


    }


}