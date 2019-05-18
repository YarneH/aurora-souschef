package com.aurora.souschefprocessor.facade;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.Recipe;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.recipe.RecipeTimer;

import org.hamcrest.CoreMatchers;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DelegatorLongTest {


    private static List<String> validRecipesJSON;
    private static List<String> invalidRecipesJSON;
    private static Delegator delegator;
    private static CRFClassifier<CoreLabel> crfClassifier;

    @BeforeClass
    public static void initialize() {

        // Read in the jsons of the recipes, the first 7 are valid recipes
        List<String> jsonRecipes = initializeRecipesJSON();
        validRecipesJSON = jsonRecipes.subList(0, 7);
        invalidRecipesJSON = jsonRecipes.subList(7, jsonRecipes.size());


        // load in the model and create the delegator
        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        try {
            crfClassifier = CRFClassifier.getClassifier(modelName);
            // create the delegator object
            // still to check if the parallellization is time saving or consuming
            delegator = new Delegator(crfClassifier, false);
        } catch (IOException | ClassNotFoundException e) {
            // this should not happen, means that the model is not found check if it is present
        }

    }

    /**
     * Read in the json recipes
     *
     * @return a list of json recipes
     */
    static List<String> initializeRecipesJSON() {
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


    @Test
    public void Delegator_processText_NoExceptionsInDelegatorForValidRecipesJSON() {
        /**
         * Check that no exceptions are thrown when these recipes are read in
         */


        // for all valid recipes
        for (String json : validRecipesJSON) {
            // Arrange
            boolean thrown = false;
            String message = "";
            try {
                // get the extracted text object from the json
                ExtractedText text = ExtractedText.fromJson(json);
                // Act
                // do the processing
                Recipe recipe = delegator.processText(text);
                // print out the result for manual checks
                System.out.println(recipe + "\n--------------------------------");
                System.out.println(recipe.getDescription());
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

        // for all invalid recipes (jsons not containing the description of an actual recipe)
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
                // let the tester know which error was thrown
                System.out.println(message);
            }
            // Assert
            // assert that an error was thrown
            assertTrue("No exception was thrown for json ", thrown);
        }

    }

    @Test
    public void Delegator_processText_timeForDoingTasksNonParallelIsLowerThanThreshold() throws RecipeDetectionException {
        /**
         * Check that the average time on the test suite for doing the processing non parallel is lower than a
         * certain threshold
         */
        // Arrange
        // non-parallelize
        System.out.println("non-parallelize");
        // parallelize flag to false in delegator
        delegator = new Delegator(crfClassifier, false);

        // initialize the average time on zero
        int average_non = 0;

        // Act
        // for all valid recipes
        for (String json : validRecipesJSON) {
            // get the extractedText object from hte json
            ExtractedText text = ExtractedText.fromJson(json);
            // do the processing and log the time this processing cost
            long start = System.currentTimeMillis();
            delegator.processText(text);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_non += time;

        }
        // divide the average by the amount of recipes processed to get the average
        average_non = average_non / validRecipesJSON.size();

        // Assert
        // print out the average time
        int threshold = 500;
        System.out.println(average_non + "  NON PARALLEL TIME");
        assertTrue("The average time is not smaller than the threshold, average time: " + average_non + ", threshold" +
                        " " + threshold,
                average_non < threshold);

    }

    @Test
    public void Delegator_processText_timeForDoingTasksParallelIsLowerThanThreshold() throws RecipeDetectionException {
        /**
         * Check that the average time on the test suite for doing the processing  parallel is lower than a
         * certain threshold
         */
        // Arrange
        // initialize the average on 0
        int average_para = 0;
        System.out.println("parallelize");
        // create a parallel delegator
        delegator = new Delegator(crfClassifier, true);

        // Act
        // for all valid recipes
        for (String json : validRecipesJSON) {
            // get the extracted text object (not a part of the processing)
            ExtractedText text = ExtractedText.fromJson(json);
            // do the processing and log the time it took
            long start = System.currentTimeMillis();
            delegator.processText(text);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_para += time;
        }
        // divide by the number of recipes processed
        average_para = average_para / validRecipesJSON.size();


        // Assert
        int threshold = 10000;
        System.out.println(average_para + "  PARALLEL TIME");
        assertTrue("The average time is not smaller than the threshold, average time: " + average_para + ", threshold" +
                        " " + threshold,
                average_para < threshold);

    }

    @After
    public void wipeDelegator() {
        //set the the delegator back to the parallelized version (seems to be faster for now)
        delegator = new Delegator(crfClassifier, true);
    }


    @Test
    public void Delegator_process_AnnotatedFileByAuroraIsCorrectlyProcessed() throws RecipeDetectionException {
        // arrange
        // read in the file line by line
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

        // get the extracted text object
        ExtractedText text = ExtractedText.fromJson(contents);
        // Act
        // process and create the recipe object
        Recipe r = delegator.processText(text);
        System.out.println(r);

        //assert
        assertEquals("the description is not as expected", "Filename: src/test/res/Pasta.docx\n" +
                "\n" +
                "Pasta puttanesca\n" +
                "\n" +
                "Yield\n" +
                "\n" +
                "Active Time\n" +
                "30 minutes\n" +
                "Total Time\n" +
                "35 minutes", r.getDescription());
        assertEquals("The number of people is not as expected", 4, r.getNumberOfPeople());
        assertEquals("The length of the ingredientlist is not as expected", 11, r.getIngredients().size());
        assertEquals("The number of steps is not as expected", 5, r.getRecipeSteps().size());
        // check the specific ingredients
        ingredientsAsExpected(r);


        // check the steps
        stepsAsExpected(r);
    }

    private void ingredientsAsExpected(Recipe r) {
        /*
        Checks if the ingredients of recipe in ../app/src/main/res/raw/input.txt are correctly processed
         */
        // positions are irrelevant in equals operation
        Position pos = new Position(0, 1);
        Map<Ingredient.PositionKeysForIngredients, Position> irrelevantpositions = new HashMap<>();
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantpositions.put(key, pos);
        }
        List<ListIngredient> ingredients = r.getIngredients();
        assertThat(ingredients, CoreMatchers.hasItems(
                new ListIngredient("linguine or other long pasta", "pound", 1,
                        "1 pound linguine or other long pasta", irrelevantpositions),
                new ListIngredient("Kosher " + "salt", "", 1,
                        "Kosher salt", irrelevantpositions),
                new ListIngredient("diced tomatoes", "can", 1,
                        "1 (14-oz.) can diced tomatoes", irrelevantpositions),
                new ListIngredient("extra-virgin olive oil", "cup", 0.5,
                        "1/2 cup extra-virgin olive oil, divided", irrelevantpositions),
                new ListIngredient("capers", "cup", 0.25,
                        "1/4 cup capers, drained", irrelevantpositions),
                new ListIngredient("oil-packed anchovy fillets", "", 6,
                        "6 oil-packed anchovy fillets", irrelevantpositions),
                new ListIngredient("tomato paste", "tablespoon", 1,
                        "1 tablespoon tomato paste", irrelevantpositions),
                new ListIngredient("pitted Kalamata olives", "cup", 1.0 / 3,
                        "1/3 cup pitted Kalamata olives, halved", irrelevantpositions),
                new ListIngredient("dried oregano", "teaspoon", 2, "2 teaspoon dried " +
                        "oregano", irrelevantpositions),
                new ListIngredient("crushed red pepper flakes", "teaspoon", 0.5,
                        "1/2 teaspoon crushed red pepper flakes", irrelevantpositions),
                new ListIngredient("oil-packed tuna", "ounce", 6,
                        "6 ounce oil-packed tuna", irrelevantpositions)
        ));
    }

    private void stepsAsExpected(Recipe r) {
         /*
        Checks if the steps of recipe in ../app/src/main/res/raw/input.txt are correctly processed
         */
        List<RecipeStep> steps = r.getRecipeSteps();

        RecipeStep step0 = steps.get(0);
        RecipeStep step1 = steps.get(1);
        RecipeStep step2 = steps.get(2);
        RecipeStep step3 = steps.get(3);
        RecipeStep step4 = steps.get(4);
        // check the descriptions
        assertEquals("The description of the first step is not as expected", "Cook pasta in a large pot of " +
                "boiling salted water, stirring occasionally, until al dente. Drain pasta, reserving 1 cup pasta " +
                "cooking liquid; return pasta to pot.", step0.getDescription());
        assertEquals("The description of the second step is not as expected", "While pasta cooks, pour tomatoes into" +
                " a fine-mesh sieve set over a medium bowl. Shake to release as much juice as possible, then let " +
                "tomatoes drain in sieve, collecting juices in bowl, until ready to use.", step1.getDescription());
        assertEquals("The description of the third step is not as expected", "Heat 1/4 cup oil in a large deep-sided " +
                "skillet over medium-high. Add capers and cook, swirling pan occasionally, until they burst and are " +
                "crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper towel-lined plate, " +
                "reserving oil in skillet.", step2.getDescription());
        assertEquals("The description of the fourth step is not as expected", "Combine anchovies, tomato paste, and " +
                "drained tomatoes in skillet. Cook over medium-high heat, stirring occasionally, until tomatoes begin" +
                " to caramelize and anchovies start to break down, about 5 minutes. Add collected tomato juices, " +
                "olives, oregano, and red pepper flakes and bring to a simmer. Cook, stirring occasionally, until " +
                "sauce is slightly thickened, about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta " +
                "cooking liquid to pan. Cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking " +
                "liquid to loosen if needed, until sauce is thickened and emulsified, about 2 minutes. Flake tuna " +
                "into pasta and toss to combine.", step3.getDescription());
        assertEquals("The description of the fifth step is not as expected", "Divide pasta among plates. Top with " +
                "fried capers.", step4.getDescription());

        // check the timers
        // correct size check
        assertEquals("The number of timers for the first step is not correct", 0, step0.getRecipeTimers().size());
        assertEquals("The number of timers for the second step is not correct", 0, step1.getRecipeTimers().size());
        assertEquals("The number of timers for the third step is not correct", 1, step2.getRecipeTimers().size());
        assertEquals("The number of timers for the fourth step is not correct", 3, step3.getRecipeTimers().size());
        assertEquals("The number of timers for the fifth step is not correct", 0, step4.getRecipeTimers().size());

        // check correctness of timers
        // positions are irrelevant in equals operation
        Position pos = new Position(0, 1);

        assertThat("The timers for the third step are not correct", step2.getRecipeTimers(),
                CoreMatchers.hasItem(new RecipeTimer(3 * 60, pos)));
        assertThat("The timers for the fourth step are not correct", step3.getRecipeTimers(), CoreMatchers.hasItems(
                new RecipeTimer(2 * 60, pos),
                new RecipeTimer(5 * 60, pos),
                new RecipeTimer(5 * 60, pos)
        ));

        // check the ingredients
        assertEquals("The number of ingredients for the first step is not correct", 1, step0.getIngredients().size());
        assertEquals("The number of ingredients for the second step is not correct", 2, step1.getIngredients().size());
        assertEquals("The number of ingredients for the third step is not correct", 2, step2.getIngredients().size());
        assertEquals("The number of ingredients for the fourth step is not correct", 7, step3.getIngredients().size());
        assertEquals("The number of ingredients for the fifth step is not correct", 2, step4.getIngredients().size());

        // correctness per step
        // positions are irrelevant in equals operation
        Map<Ingredient.PositionKeysForIngredients, Position> irrelevantpositions = new HashMap<>();
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantpositions.put(key, pos);
        }
        assertThat("The ingredients for the first step are not correct", step0.getIngredients(), CoreMatchers.hasItems(
                new Ingredient("linguine or other long pasta", "", 1, irrelevantpositions)
        ));
        assertThat("The ingredients for the second step are not correct", step1.getIngredients(), CoreMatchers.hasItems(
                new Ingredient("linguine or other long pasta", "", 1, irrelevantpositions),
                new Ingredient("diced tomatoes", "", 1, irrelevantpositions)
        ));
        assertThat("The ingredients for the third step are not correct", step2.getIngredients(), CoreMatchers.hasItems(
                new Ingredient("extra-virgin olive oil", "cup", 0.25, irrelevantpositions),
                new Ingredient("capers", "", 1, irrelevantpositions)
        ));
        assertThat("The ingredients for the fourth step are not correct", step3.getIngredients(), CoreMatchers.hasItems(
                new Ingredient("linguine or other long pasta", "", 1, irrelevantpositions),
                new Ingredient("extra-virgin olive oil", "", 1, irrelevantpositions),
                new Ingredient("tomato paste", "", 1, irrelevantpositions),
                new Ingredient("diced tomatoes", "", 1, irrelevantpositions),
                new Ingredient("dried oregano", "", 1, irrelevantpositions),
                new Ingredient("crushed red pepper flakes", "", 1, irrelevantpositions),
                new Ingredient("oil-packed tuna", "", 1, irrelevantpositions)

        ));
        assertThat("The ingredients for the fifth step are not correct", step4.getIngredients(), CoreMatchers.hasItems(
                new Ingredient("linguine or other long pasta", "", 1, irrelevantpositions),
                new Ingredient("capers", "", 1, irrelevantpositions)
        ));
    }

    @Test
    public void Delegator_process_noExceptionsForFileWithWeirdSections() throws RecipeDetectionException{
        /*
        Check that extracted text object with only one section does not throw exceptions, also manual check on
        correctness of this recipe via print out
         */
        // arrange
        // read in the file line by line
        String contents = null;
        try {
            System.out.println(Paths.get("").toAbsolutePath().toString());
            BufferedReader reader = new BufferedReader(new FileReader("src/test/java/com/aurora/souschefprocessor" +
                    "/facade/json-with-weird-sections.txt"));
            StringBuilder bld = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                bld.append(line);
                line = reader.readLine();

            }
            contents = bld.toString();
            System.out.println(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the extracted text
        ExtractedText text = ExtractedText.fromJson(contents);

        // act
        //do the processing
        Recipe r = delegator.processText(text);
        // print out for manual check
        System.out.println(r);

    }

}