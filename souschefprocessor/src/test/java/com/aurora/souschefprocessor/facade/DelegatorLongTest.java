package com.aurora.souschefprocessor.facade;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.Recipe;
import com.aurora.souschefprocessor.recipe.RecipeStep;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DelegatorLongTest {


    private static List<String> validRecipesJSON;
    private static List<String> invalidRecipesJSON;
    private static Delegator delegator;
    private static CRFClassifier<CoreLabel> crfClassifier;

    @BeforeClass
    public static void initialize() {

        // Read in the recipes, the first 7 are valid recipes
        List<String> jsonRecipes = initializeRecipesJSON();
        validRecipesJSON = jsonRecipes.subList(0, 7);
        invalidRecipesJSON = jsonRecipes.subList(7, jsonRecipes.size());


        // load in the model and create the delegator
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
     * Read in the json recipes
     *
     * @return a list of json recipes
     */
    public static List<String> initializeRecipesJSON() {
        String filename = "src/test/java/com/aurora/souschefprocessor/facade/json-recipes.txt";
        List<String> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), StandardCharsets.UTF_8));

            String line = reader.readLine();


            while (line != null) {
                list.add(line);
                line = reader.readLine();
            }
        } catch (IOException io) {
            System.out.println(io);
        }
        return list;
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


        for (String json : validRecipesJSON) {
            // Arrange
            boolean thrown = false;
            String message = "";
            try {
                ExtractedText text = ExtractedText.fromJson(json);
                // Act
                Recipe recipe = delegator.processText(text);
                System.out.println(recipe + "\n--------------------------------");
            } catch (Exception e) {
                // set thrown to true, this should not happen
                thrown = true;
                message = e.getMessage();
                System.out.println(e);
            }
            // Assert
            // assert that no errors were thrown
            assertFalse("an exception was thrown for json " + json + "/n" +
                    "Exception = " + message, thrown);
        }
    }


    @Test
    public void Delegator_processText_ExceptionsInDelegatorForInvalidRecipesJSON() {
        /**
         * Check that exceptions are thrown when these recipes are read in
         */

        for (String json : invalidRecipesJSON) {
            // Arrange
            boolean thrown = false;
            String message = "";
            try {
                ExtractedText text = ExtractedText.fromJson(json);
                // Act
                Recipe recipe = delegator.processText(text);
            } catch (Exception e) {
                // set thrown to true, this should not happen
                thrown = true;
                message = e.getMessage();
            }
            // Assert
            // assert that an error was thrown
            assertTrue("No exception was thrown for json ", thrown);
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
        for (String json : validRecipesJSON) {
            ExtractedText text = ExtractedText.fromJson(json);
            // do the processing and add the time this processing costed
            long start = System.currentTimeMillis();
            delegator.processText(text);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_non += time;

        }
        // divide by the amount of recipes processed to get the average
        average_non = average_non / validRecipesJSON.size();

        // Assert
        System.out.println(average_non + "  NON PARALLEL TIME");
        assert (average_non < 500);

    }

    @Test
    public void Delegator_processText_timeForDoingTasksParallelIsLowerThanThreshold() {

        // Arrange
        int average_para = 0;
        System.out.println("parallelize");
        delegator = new Delegator(crfClassifier, true);

        // Act

        for (String json : validRecipesJSON) {
            ExtractedText text = ExtractedText.fromJson(json);
            long start = System.currentTimeMillis();
            Recipe recipe = delegator.processText(text);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_para += time;
        }
        average_para = average_para / validRecipesJSON.size();


        // Assert
        System.out.println(average_para + "  PARALLEL TIME");
        assert (average_para < 2000);

    }

    @After
    public void wipeDelegator() {
        delegator = new Delegator(crfClassifier, true);
        List<String> jsonRecipes = initializeRecipesJSON();
        validRecipesJSON = jsonRecipes.subList(0, 7);
        invalidRecipesJSON = jsonRecipes.subList(7, jsonRecipes.size());

    }


    @Test
    public void test_with_new_auroralib() {
        String contents = null;
        try {
            System.out.println(Paths.get("").toAbsolutePath().toString());
            BufferedReader reader = new BufferedReader(new FileReader("../app/src/main/res/raw/input.txt"));
            StringBuilder bld = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                bld.append(line);
                line = reader.readLine();

            }
            contents = bld.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExtractedText text = ExtractedText.fromJson(contents);


        Recipe r = delegator.processText(text);
        System.out.println(r);

    }

    @Test
    public void n(){
        String contents = null;
        try {
            System.out.println(Paths.get("").toAbsolutePath().toString());
            BufferedReader reader = new BufferedReader(new FileReader("src/test/java/com/aurora/souschefprocessor/facade/hulp.txt"));
            StringBuilder bld = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                bld.append(line);
                line = reader.readLine();

            }
            contents = bld.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExtractedText text = ExtractedText.fromJson(contents);

        Recipe r = delegator.processText(text);
        System.out.println(r);
        RecipeStep s = r.getRecipeSteps().get(2);
        System.out.println(s.getDescription());

        for(Ingredient ing: s.getIngredients()){
            System.out.println(ing);
            System.out.println(ing.getNamePosition());
            System.out.println(ing.getUnitPosition());
            System.out.println(ing.getQuantityPosition());
        }


    }



}