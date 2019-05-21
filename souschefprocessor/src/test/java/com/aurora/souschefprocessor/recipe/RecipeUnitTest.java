package com.aurora.souschefprocessor.recipe;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RecipeUnitTest {

    private static Position irrelevantPosition = new Position(0, 1);
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static RecipeInProgress rip;
    private static ExtractedText emptyExtractedText = new ExtractedText("", "");


    @BeforeClass
    public static void initialize() {

        // create a very simple recipe in progress object that can be converted to a recipe in the tests
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, irrelevantPosition);
        }
        rip = new RecipeInProgress(emptyExtractedText);
        RecipeStepInProgress step1 = new RecipeStepInProgress("Let the pasta boil for 10 minutes");
        RecipeTimer timer1 = new RecipeTimer(10 * 60, irrelevantPosition);

        step1.add(timer1);
        Position qpos1 = new Position(0, step1.getDescription().length());
        Position upos1 = new Position(0, step1.getDescription().length());
        Position npos1 = new Position(step1.getDescription().indexOf("pasta"), step1.getDescription().indexOf("pasta") + 5);
        Map<Ingredient.PositionKeysForIngredients, Position> map1 = new HashMap<Ingredient.PositionKeysForIngredients, Position>() {{
            put(Ingredient.PositionKeysForIngredients.QUANTITY, qpos1);
            put(Ingredient.PositionKeysForIngredients.UNIT, upos1);
            put(Ingredient.PositionKeysForIngredients.NAME, npos1);
        }};
        Ingredient ing1 = new Ingredient("pasta", "", 1, map1);
        step1.add(ing1);
        RecipeStepInProgress step2 = new RecipeStepInProgress("Put 500 gram sauce in the microwave for 3 to 5 minutes");
        RecipeTimer timer2 = new RecipeTimer(3 * 60, 5 * 60, irrelevantPosition);
        step2.add(timer2);

        String description2 = step2.getDescription();
        int qbegin = description2.indexOf("500");
        Position qpos2 = new Position(qbegin, qbegin + 3);
        int ubegin = description2.indexOf("gram");
        Position upos2 = new Position(ubegin, ubegin + 4);
        int nbegin = description2.indexOf("sauce");
        Position npos2 = new Position(nbegin, nbegin + 5);
        Map<Ingredient.PositionKeysForIngredients, Position> map2 = new HashMap<Ingredient.PositionKeysForIngredients, Position>() {{
            put(Ingredient.PositionKeysForIngredients.QUANTITY, qpos2);
            put(Ingredient.PositionKeysForIngredients.UNIT, upos2);
            put(Ingredient.PositionKeysForIngredients.NAME, npos2);
        }};
        Ingredient ing2 = new Ingredient("sauce", "gram", 500, map2);
        step2.add(ing2);

        List<RecipeStepInProgress> steps = new ArrayList<>(Arrays.asList(step1, step2));
        rip.setStepsInProgress(steps);

        Map<Ingredient.PositionKeysForIngredients, Position> listMap = new HashMap<Ingredient.PositionKeysForIngredients, Position>() {{
            put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(0, 3));
            put(Ingredient.PositionKeysForIngredients.UNIT, new Position(4, 8));
            put(Ingredient.PositionKeysForIngredients.NAME, new Position(9, 14));
        }};
        ListIngredient LI1 = new ListIngredient("pasta", "gram", 500, "500 gram pasta", irrelevantPositions);
        ListIngredient LI2 = new ListIngredient("sauce", "gram", 500, "500 gram sauce", irrelevantPositions);
        List<ListIngredient> ingredients = new ArrayList<>();
        ingredients.add(LI1);
        ingredients.add(LI2);
        rip.setIngredients(ingredients);
        rip.setDescription("This is a very simple recipe");

    }

    @Test
    public void Recipe_toJSON_afterConversionRecipeIsEqual() {
        /*
        Test that the conversion from and to json render the same recipe
         */
        // Arrange
        Recipe recipe = rip.convertToRecipe();

        // Act
        String json = recipe.toJSON();
        Recipe recipeAfterConversion = Recipe.fromJson(json, Recipe.class);

        // Assert
        assert (recipe.equals(recipeAfterConversion));

    }

    @Test
    public void Recipe_convertUnit_afterConvertingTwiceRecipeIsEqual() {
        /*
        Converting the units of a recipe should be reversible, so converting twice should yield the
        original recipe
         */

        // get the original
        Recipe recipe = rip.convertToRecipe();

        Recipe unitConvertingRecipe = rip.convertToRecipe();

        // convert the converting recipe twice
        unitConvertingRecipe.convertUnit(false);
        unitConvertingRecipe.convertUnit(true);

        // Assert after converting twice it is back the same recipe
        assertEquals("The recipe is not the same after converting twice", recipe, unitConvertingRecipe);

    }


    @Test
    public void Recipe_sentencesToTranslate_getTranslatedRecipe_ReconstructedRecipeJSONisTheSameIfNoTranslation() {
        /*
        The recipe that is created by using the sentencesToTranslate for getting a translated recipe (without
        actually translating) should render the same recipe, so everything should be equal
         */
        String json = "{\"mIngredients\":[{\"mOriginalLine\":\"1 pound linguine or other long pasta\"," +
                "\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"pound\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":36},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2,\"mEndIndex\":7}}},{\"mOriginalLine\":\"Kosher salt\"," +
                "\"mName\":\"Kosher salt\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":0,\"mEndIndex\":11},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":11},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":11}}},{\"mOriginalLine\":\"1 (14-oz) can" +
                " diced tomatoes\",\"mName\":\"can diced tomatoes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":10,\"mEndIndex\":28},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":28}}},{\"mOriginalLine\":\"1/2 cup " +
                "extra-virgin olive oil, divided\",\"mName\":\"extra-virgin olive oil\",\"mAmount\":{\"mValue\":0.5," +
                "\"mUnit\":\"cup\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":30}," +
                "\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":7}}}," +
                "{\"mOriginalLine\":\"1/4 cup capers, drained\",\"mName\":\"capers\",\"mAmount\":{\"mValue\":0.25," +
                "\"mUnit\":\"cup\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":14}," +
                "\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":7}}}," +
                "{\"mOriginalLine\":\"6 oil-packed anchovy fillets\",\"mName\":\"oil-packed anchovy fillets\"," +
                "\"mAmount\":{\"mValue\":6.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":2," +
                "\"mEndIndex\":28},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":28}}},{\"mOriginalLine\":\"1 tablespoon tomato paste\",\"mName\":\"tomato paste\"," +
                "\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"tablespoon\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":13," +
                "\"mEndIndex\":25},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2," +
                "\"mEndIndex\":12}}},{\"mOriginalLine\":\"1/3 cup pitted Kalamata olives, halved\",\"mName\":\"pitted" +
                " Kalamata olives\",\"mAmount\":{\"mValue\":0.3333333333333333,\"mUnit\":\"cup\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":30},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":7}}},{\"mOriginalLine\":\"2 teaspoon " +
                "dried oregano\",\"mName\":\"dried oregano\",\"mAmount\":{\"mValue\":2.0,\"mUnit\":\"teaspoon\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":11,\"mEndIndex\":24},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2,\"mEndIndex\":10}}},{\"mOriginalLine\":\"1/2 teaspoon " +
                "crushed red pepper flakes\",\"mName\":\"crushed red pepper flakes\",\"mAmount\":{\"mValue\":0.5," +
                "\"mUnit\":\"teaspoon\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":13,\"mEndIndex\":38}," +
                "\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":12}}}," +
                "{\"mOriginalLine\":\"6 ounce oil-packed tuna\",\"mName\":\"oil-packed tuna\"," +
                "\"mAmount\":{\"mValue\":6.0,\"mUnit\":\"ounce\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8," +
                "\"mEndIndex\":23},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2," +
                "\"mEndIndex\":7}}}],\"mRecipeSteps\":[{\"mIngredients\":[{\"mName\":\"linguine or other long " +
                "pasta\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":5," +
                "\"mEndIndex\":10},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":161},\"UNIT\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":161}}}],\"mRecipeTimers\":[],\"mDescription\":\"Cook pasta in a large pot of boiling " +
                "salted water, stirring occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking " +
                "liquid; return pasta to pot.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true}," +
                "{\"mIngredients\":[{\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0," +
                "\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":6,\"mEndIndex\":11}," +
                "\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":204},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":204}}}," +
                "{\"mName\":\"can diced tomatoes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":24,\"mEndIndex\":32},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":204},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":204}}}],\"mRecipeTimers\":[]," +
                "\"mDescription\":\"While pasta cooks, pour tomatoes into a fine-mesh sieve set over a medium bowl. " +
                "Shake to release as much juice as possible, then let tomatoes drain in sieve, collecting juices in " +
                "bowl, until ready to use.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true}," +
                "{\"mIngredients\":[{\"mName\":\"extra-virgin olive oil\",\"mAmount\":{\"mValue\":0.25," +
                "\"mUnit\":\"cup\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":13,\"mEndIndex\":16}," +
                "\"QUANTITY\":{\"mBeginIndex\":5,\"mEndIndex\":8},\"UNIT\":{\"mBeginIndex\":9,\"mEndIndex\":12}}}," +
                "{\"mName\":\"capers\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":69,\"mEndIndex\":75},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":256},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":256}}}]," +
                "\"mRecipeTimers\":[{\"mUpperBound\":180,\"mLowerBound\":180,\"mPosition\":{\"mBeginIndex\":145," +
                "\"mEndIndex\":160}}],\"mDescription\":\"Heat 1/4 cup oil in a large deep-sided skillet over " +
                "medium-high. Add capers and cook, swirling pan occasionally, until they burst and are crisp, about 3" +
                " minutes. Using a slotted spoon, transfer capers to a paper towel-lined plate, reserving oil in " +
                "skillet.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true}," +
                "{\"mIngredients\":[{\"mName\":\"tomato paste\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":19,\"mEndIndex\":31},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"can diced " +
                "tomatoes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":45," +
                "\"mEndIndex\":53},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":654}}},{\"mName\":\"extra-virgin olive oil\",\"mAmount\":{\"mValue\":1.0," +
                "\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":233,\"mEndIndex\":239}," +
                "\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}}," +
                "{\"mName\":\"dried oregano\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":241,\"mEndIndex\":248},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"crushed red pepper " +
                "flakes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":258," +
                "\"mEndIndex\":271},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":654}}},{\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0," +
                "\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":380,\"mEndIndex\":386}," +
                "\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}}," +
                "{\"mName\":\"oil-packed tuna\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":618,\"mEndIndex\":622},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}}]," +
                "\"mRecipeTimers\":[{\"mUpperBound\":300,\"mLowerBound\":300,\"mPosition\":{\"mBeginIndex\":187," +
                "\"mEndIndex\":202}},{\"mUpperBound\":300,\"mLowerBound\":300,\"mPosition\":{\"mBeginIndex\":359," +
                "\"mEndIndex\":374}},{\"mUpperBound\":120,\"mLowerBound\":120,\"mPosition\":{\"mBeginIndex\":595," +
                "\"mEndIndex\":610}}],\"mDescription\":\"Combine anchovies, tomato paste, and drained tomatoes in " +
                "skillet. Cook over medium-high heat, stirring occasionally, until tomatoes begin to caramelize and " +
                "anchovies start to break down, about 5 minutes. Add collected tomato juices, olives, oregano, and " +
                "red pepper flakes and bring to a simmer. Cook, stirring occasionally, until sauce is slightly " +
                "thickened, about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta cooking liquid to " +
                "pan. Cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking liquid to loosen if " +
                "needed, until sauce is thickened and emulsified, about 2 minutes. Flake tuna into pasta and toss to " +
                "combine.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true}," +
                "{\"mIngredients\":[{\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0," +
                "\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":7,\"mEndIndex\":12}," +
                "\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":49},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":49}}}," +
                "{\"mName\":\"capers\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"}," +
                "\"mPositions\":{\"NAME\":{\"mBeginIndex\":42,\"mEndIndex\":48},\"QUANTITY\":{\"mBeginIndex\":0," +
                "\"mEndIndex\":49},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":49}}}],\"mRecipeTimers\":[]," +
                "\"mDescription\":\"Divide pasta among plates. Top with fried capers.\"," +
                "\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true}],\"mNumberOfPeople\":4," +
                "\"mDescription\":\"Pasta puttanesca\\nYield\\n\\nActive Time\\n30 minutes\\nTotal Time\\n35 " +
                "minutes\",\"mFileName\":\"\"}";

        Recipe recipe = Recipe.fromJson(json, Recipe.class);
        List<String> sentences = recipe.createSentencesToTranslate();




        Recipe reconstructedRecipe = recipe.getTranslatedRecipe( sentences.toArray(new String[0]));
        assertEquals("The recipes are not equal", recipe, reconstructedRecipe);

        // check the ingredients
        for(int i=0; i<recipe.getIngredients().size(); i++){
            ListIngredient original = recipe.getIngredients().get(i);
            ListIngredient translated = reconstructedRecipe.getIngredients().get(i);
            assertEquals("name", original.getNamePosition(), translated.getNamePosition());
            assertEquals("unit", original.getUnitPosition(), translated.getUnitPosition());
            assertEquals("quant", original.getQuantityPosition(), translated.getQuantityPosition());
        }

        // check the steps
        for(int i=0; i<recipe.getRecipeSteps().size(); i++){
            RecipeStep original = recipe.getRecipeSteps().get(i);
            RecipeStep translated = reconstructedRecipe.getRecipeSteps().get(i);
            // check the ingredients in the steps
            for(int j=0; j< original.getIngredients().size(); j++){
                Ingredient originalIngredient = original.getIngredients().get(j);
                Ingredient newIngredient = translated.getIngredients().get(j);
                assertEquals("name", originalIngredient.getNamePosition(), newIngredient.getNamePosition());
                assertEquals("unit", originalIngredient.getUnitPosition(), newIngredient.getUnitPosition());
                assertEquals("quant", originalIngredient.getQuantityPosition(), newIngredient.getQuantityPosition());
            }

            // check the timers in the steps
            for(int j=0; j< original.getRecipeTimers().size(); j++){
                RecipeTimer originalTimer = original.getRecipeTimers().get(j);
                RecipeTimer newTimer = translated.getRecipeTimers().get(j);
                assertEquals("timer", originalTimer.getPosition(), newTimer.getPosition());

            }
           
        }

        //check the json
        assertEquals("The jsons are not equal", json, reconstructedRecipe.toJSON());


    }

    @Test
    public void Recipe_creatingFromTranslationsDoesNotThrowException(){
        /*
        Constructing a recipe from a Dutch translation does not throw an exception
         */
        // Arrange
        String json = "{\"mIngredients\":[{\"mOriginalLine\":\"1 pound linguine or other long pasta\",\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"pound\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":36},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2,\"mEndIndex\":7}}},{\"mOriginalLine\":\"Kosher salt\",\"mName\":\"Kosher salt\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":0,\"mEndIndex\":11},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":11},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":11}}},{\"mOriginalLine\":\"1 (14-oz) can diced tomatoes\",\"mName\":\"can diced tomatoes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":10,\"mEndIndex\":28},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":28}}},{\"mOriginalLine\":\"1/2 cup extra-virgin olive oil, divided\",\"mName\":\"extra-virgin olive oil\",\"mAmount\":{\"mValue\":0.5,\"mUnit\":\"cup\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":30},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":7}}},{\"mOriginalLine\":\"1/4 cup capers, drained\",\"mName\":\"capers\",\"mAmount\":{\"mValue\":0.25,\"mUnit\":\"cup\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":14},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":7}}},{\"mOriginalLine\":\"6 oil-packed anchovy fillets\",\"mName\":\"oil-packed anchovy fillets\",\"mAmount\":{\"mValue\":6.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":2,\"mEndIndex\":28},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":28}}},{\"mOriginalLine\":\"1 tablespoon tomato paste\",\"mName\":\"tomato paste\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"tablespoon\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":13,\"mEndIndex\":25},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2,\"mEndIndex\":12}}},{\"mOriginalLine\":\"1/3 cup pitted Kalamata olives, halved\",\"mName\":\"pitted Kalamata olives\",\"mAmount\":{\"mValue\":0.3333333333333333,\"mUnit\":\"cup\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":30},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":7}}},{\"mOriginalLine\":\"2 teaspoon dried oregano\",\"mName\":\"dried oregano\",\"mAmount\":{\"mValue\":2.0,\"mUnit\":\"teaspoon\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":11,\"mEndIndex\":24},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2,\"mEndIndex\":10}}},{\"mOriginalLine\":\"1/2 teaspoon crushed red pepper flakes\",\"mName\":\"crushed red pepper flakes\",\"mAmount\":{\"mValue\":0.5,\"mUnit\":\"teaspoon\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":13,\"mEndIndex\":38},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":3},\"UNIT\":{\"mBeginIndex\":4,\"mEndIndex\":12}}},{\"mOriginalLine\":\"6 ounce oil-packed tuna\",\"mName\":\"oil-packed tuna\",\"mAmount\":{\"mValue\":6.0,\"mUnit\":\"ounce\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":8,\"mEndIndex\":23},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":1},\"UNIT\":{\"mBeginIndex\":2,\"mEndIndex\":7}}}],\"mRecipeSteps\":[{\"mIngredients\":[{\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":5,\"mEndIndex\":10},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":161},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":161}}}],\"mRecipeTimers\":[],\"mDescription\":\"Cook pasta in a large pot of boiling salted water, stirring occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking liquid; return pasta to pot.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true},{\"mIngredients\":[{\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":6,\"mEndIndex\":11},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":204},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":204}}},{\"mName\":\"can diced tomatoes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":24,\"mEndIndex\":32},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":204},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":204}}}],\"mRecipeTimers\":[],\"mDescription\":\"While pasta cooks, pour tomatoes into a fine-mesh sieve set over a medium bowl. Shake to release as much juice as possible, then let tomatoes drain in sieve, collecting juices in bowl, until ready to use.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true},{\"mIngredients\":[{\"mName\":\"extra-virgin olive oil\",\"mAmount\":{\"mValue\":0.25,\"mUnit\":\"cup\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":13,\"mEndIndex\":16},\"QUANTITY\":{\"mBeginIndex\":5,\"mEndIndex\":8},\"UNIT\":{\"mBeginIndex\":9,\"mEndIndex\":12}}},{\"mName\":\"capers\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":69,\"mEndIndex\":75},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":256},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":256}}}],\"mRecipeTimers\":[{\"mUpperBound\":180,\"mLowerBound\":180,\"mPosition\":{\"mBeginIndex\":145,\"mEndIndex\":160}}],\"mDescription\":\"Heat 1/4 cup oil in a large deep-sided skillet over medium-high. Add capers and cook, swirling pan occasionally, until they burst and are crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper towel-lined plate, reserving oil in skillet.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true},{\"mIngredients\":[{\"mName\":\"tomato paste\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":19,\"mEndIndex\":31},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"can diced tomatoes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":45,\"mEndIndex\":53},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"extra-virgin olive oil\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":233,\"mEndIndex\":239},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"dried oregano\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":241,\"mEndIndex\":248},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"crushed red pepper flakes\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":258,\"mEndIndex\":271},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":380,\"mEndIndex\":386},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}},{\"mName\":\"oil-packed tuna\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":618,\"mEndIndex\":622},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":654},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":654}}}],\"mRecipeTimers\":[{\"mUpperBound\":300,\"mLowerBound\":300,\"mPosition\":{\"mBeginIndex\":187,\"mEndIndex\":202}},{\"mUpperBound\":300,\"mLowerBound\":300,\"mPosition\":{\"mBeginIndex\":359,\"mEndIndex\":374}},{\"mUpperBound\":120,\"mLowerBound\":120,\"mPosition\":{\"mBeginIndex\":595,\"mEndIndex\":610}}],\"mDescription\":\"Combine anchovies, tomato paste, and drained tomatoes in skillet. Cook over medium-high heat, stirring occasionally, until tomatoes begin to caramelize and anchovies start to break down, about 5 minutes. Add collected tomato juices, olives, oregano, and red pepper flakes and bring to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta cooking liquid to pan. Cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true},{\"mIngredients\":[{\"mName\":\"linguine or other long pasta\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":7,\"mEndIndex\":12},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":49},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":49}}},{\"mName\":\"capers\",\"mAmount\":{\"mValue\":1.0,\"mUnit\":\"\"},\"mPositions\":{\"NAME\":{\"mBeginIndex\":42,\"mEndIndex\":48},\"QUANTITY\":{\"mBeginIndex\":0,\"mEndIndex\":49},\"UNIT\":{\"mBeginIndex\":0,\"mEndIndex\":49}}}],\"mRecipeTimers\":[],\"mDescription\":\"Divide pasta among plates. Top with fried capers.\",\"mIngredientDetectionDone\":true,\"mTimerDetectionDone\":true}],\"mNumberOfPeople\":4,\"mDescription\":\"Pasta puttanesca\\nYield\\n\\nActive Time\\n30 minutes\\nTotal Time\\n35 minutes\",\"mUniquePluginName\":\"com.aurora.souschef\"}";
        Recipe recipe = Recipe.fromJson(json, Recipe.class);

        String[] translations = {"Pasta puttanesca", "Opbrengst", "", "Actieve tijd", "30 minuten", "Totale tijd", "35 minuten", "1 pond linguine of andere lange pasta", "linguine of andere lange pasta", "pond", "1", "Kosjer zout", "Kosjer zout", "1 (14-oz) kan tomaten in blokjes snijden", "kan tomaten in blokjes snijden", "1", "1/2 kopje extra vierge olijfolie, verdeeld", "extra vergine olijfolie", "kop", "1/2", "1/4 kopje kappertjes, uitgelekt", "kappertjes", "kop", "1/4", "6 ansjovisfilets gevuld met olie", "ansjovisfilets met ansjovis", "6", "1 eetlepel tomatenpuree", "tomatenpuree", "eetlepel", "1", "1/3 kop ontpitte Kalamata-olijven, gehalveerd", "ontpitte Kalamata-olijven", "kop", "1/3", "2 theelepel gedroogde oregano", "gedroogde oregano", "theelepel", "2", "1/2 theelepel gemalen rode pepervlokken", "gemalen rode pepervlokken", "theelepel", "1/2", "6 ons olie-verpakte tonijn", "met olie gevulde tonijn", "ons", "6", "Kook de pasta in een grote pan met kokend zout water, af en toe roerend, totdat je al dente bent. Giet de pasta af en bewaar 1 kop pasta kookvloeistof; breng pasta terug naar de pot.", "linguine of andere lange pasta", "pasta", "Terwijl de pasta kookt, giet je tomaten in een fijnmazige zeef over een middelgrote kom. Schud om zoveel mogelijk sap vrij te geven, laat de tomaten in de zeef weglopen en sap in de kom verzamelen tot ze klaar zijn voor gebruik.", "linguine of andere lange pasta", "pasta", "kan tomaten in blokjes snijden", "tomaten", "Verwarm 1/4 kopje olie in een grote, diepe koekenpan op middelhoog. Voeg kappertjes toe en bak af en toe, draai af en toe rond tot ze barsten en knapperig zijn, ongeveer 3 minuten. Gebruik een lepel met sleuven en breng kappertjes over op een met papieren handdoek beklede plaat, waarbij u olie in de pan bewaart.", "extra vergine olijfolie", "kop", "1/4", "olie-", "kappertjes", "kappertjes", "ongeveer 3 minuten", "Combineer ansjovis, tomatenpuree en uitgelekte tomaten in een koekenpan. Kook op middelhoog vuur, af en toe roerend, tot de tomaten beginnen te karamelliseren en ansjovis beginnen te breken, ongeveer 5 minuten. Voeg verzamelde tomatensappen, olijven, oregano en rode pepervlokken toe en breng aan de kook. Kook, af en toe roerend, tot de saus iets dikker is, ongeveer 5 minuten. Voeg pasta, resterende 1/4 kop olie en 3/4 kop pasta kookvloeistof toe om te pannen. Kook op middelhoog vuur, roer en voeg de resterende 1/4 kop pasta kookvloeistof toe om zo nodig los te maken, tot de saus verdikt en geëmulgeerd is, ongeveer 2 minuten. Schep de tonijn in de pasta en meng om te combineren.", "tomatenpuree", "tomatenpuree", "kan tomaten in blokjes snijden", "tomaten", "extra vergine olijfolie", "olijven", "gedroogde oregano", "oregano", "gemalen rode pepervlokken", "pepervlokken", "linguine of andere lange pasta", "pasta,", "met olie gevulde tonijn", "tonijn", "ongeveer 5 minuten", "ongeveer 5 minuten", "ongeveer 2 minuten", "Verdeel pasta onder borden. Top met gefrituurde kappertjes.", "linguine of andere lange pasta", "pasta", "kappertjes", "kappertjes"};

        // act
        Recipe translatedRecipe = recipe.getTranslatedRecipe(translations);

        // print out the translated recipe for a manual check
        System.out.println(translatedRecipe);

    }

}
