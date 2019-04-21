package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetectIngredientsInRecipeStepTaskUnitTest {

    private static final String DEFAULT_UNIT = "";
    private static final Double DEFAULT_QUANTITY = 1.0;
    private static DetectIngredientsInStepTask detector0;
    private static DetectIngredientsInStepTask detector1;
    private static DetectIngredientsInStepTask detector2;
    private static DetectIngredientsInStepTask detector3;
    private static RecipeInProgress recipe;
    private static ArrayList<RecipeStep> recipeSteps;
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();

    @BeforeClass
    public static void initialize() {
        // Initialize recipe in progress
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        DetectIngredientsInStepTask.initializeAnnotationPipeline();

        // Initialize positions with dummy values
        irrelevantPositions = new HashMap<>();
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }

        recipe.setIngredientsString("irrelevant");
        List<ListIngredient> set = new ArrayList<>();
        ListIngredient spaghettiIngredient = new ListIngredient("spaghetti", "gram", 500, "irrelevant", irrelevantPositions);
        ListIngredient sauceIngredient = new ListIngredient("sauce", "ounce", 500, "irrelevant", irrelevantPositions);
        ListIngredient meatIngredient = new ListIngredient("minced meat", "pound", 1.5, "irrelevant", irrelevantPositions);
        ListIngredient garlicIngredient = new ListIngredient("garlic", "clove", DEFAULT_QUANTITY, "irrelevant", irrelevantPositions);
        ListIngredient basilIngredient = new ListIngredient("basil leaves", DEFAULT_UNIT, 20.0, "irrelevant", irrelevantPositions);
        ListIngredient saltIngredient = new ListIngredient("salt", "cup", DEFAULT_QUANTITY, "irrelevant", irrelevantPositions);
        set.add(spaghettiIngredient);
        set.add(sauceIngredient);
        set.add(meatIngredient);
        set.add(garlicIngredient);
        set.add(basilIngredient);
        set.add(saltIngredient);
        recipe.setIngredients(set);

        recipeSteps = new ArrayList<>();
        RecipeStep s0 = new RecipeStep("Cook spaghetti according to package directions.");
        RecipeStep s1 = new RecipeStep("Combine meat and a clove of garlic in a large saucepan, and cook over medium-high heat until browned.");
        RecipeStep s2 = new RecipeStep("Stir in 250 ounces of the sauce and five basil leaves. Add a cup of salt.");
        RecipeStep s3 = new RecipeStep("No ingredients are in this recipe step.");

        recipeSteps.add(s0);
        recipeSteps.add(s1);
        recipeSteps.add(s2);
        recipeSteps.add(s3);
        int stepIndex0 = 0;
        int stepIndex1 = 1;
        int stepIndex2 = 2;
        int stepIndex3 = 3;
        recipe.setRecipeSteps(recipeSteps);

        // Initialize detectors
        detector0 = new DetectIngredientsInStepTask(recipe, stepIndex0);
        detector1 = new DetectIngredientsInStepTask(recipe, stepIndex1);
        detector2 = new DetectIngredientsInStepTask(recipe, stepIndex2);
        detector3 = new DetectIngredientsInStepTask(recipe, stepIndex3);
    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.setIngredients(null);
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_ingredientDetectedWithAbsentFields() {
        detector2.doTask();
        Ingredient stepIngredientNoQuantity = new Ingredient("salt", "cup", DEFAULT_QUANTITY, irrelevantPositions);
        Ingredient stepIngredientNoUnit = new Ingredient("basil leaves", DEFAULT_UNIT, 5.0, irrelevantPositions);

        Ingredient ingredientNoQuantity = null;
        Ingredient ingredientNoUnit = null;
        Collection<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();
        for (Ingredient ingr : stepIngredients) {
            if (ingr.getName().equals(stepIngredientNoQuantity.getName())) {
                ingredientNoQuantity = ingr;
            }
            if (ingr.getName().equals(stepIngredientNoUnit.getName())) {
                ingredientNoUnit = ingr;
            }
        }

        // Asserts the correct absence of both the quantity and the unit equality
        assert (stepIngredientNoQuantity.equals(ingredientNoQuantity));
        assert (stepIngredientNoUnit.equals(ingredientNoUnit));
    }

    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        // Act
        detector0.doTask();
        detector1.doTask();

        // Assert
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isIngredientDetectionDone());
            assert (s.getIngredients() != null);
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_setHasCorrectSize() {
        // Act
        detector0.doTask();
        detector1.doTask();

        // Assert
        assert(recipe.getRecipeSteps().get(0).getIngredients().size() == 1);
        assert(recipe.getRecipeSteps().get(1).getIngredients().size() == 2);
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithoutUnit(){
        // Arrange
        Ingredient stepIngredient = new Ingredient("spaghetti", DEFAULT_UNIT, DEFAULT_QUANTITY, irrelevantPositions);

        // Act
        detector0.doTask();

        Collection<Ingredient> stepIngredients = recipe.getRecipeSteps().get(0).getIngredients();

        // Assert
        assert(stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnit(){
        // Arrange
        Ingredient stepIngredient = new Ingredient("garlic", "clove", DEFAULT_QUANTITY, irrelevantPositions);

        // Act
        detector1.doTask();

        Collection<Ingredient> stepIngredients = recipe.getRecipeSteps().get(1).getIngredients();

        // Assert
        assert(stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndVerboseQuantity(){
        // Arrange
        Ingredient stepIngredient = new Ingredient("basil leaves", DEFAULT_UNIT, 5.0, irrelevantPositions);

        // Act
        detector2.doTask();

        Collection<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();

        // Assert
        assert(stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndNumericalQuantity() {
        // Arrange
        Ingredient stepIngredient = new Ingredient("sauce", "ounce", 250.0, irrelevantPositions);

        // Act
        detector2.doTask();

        List<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();


        // Assert
        assert (stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndQuantityAndPosition(){
        // Arrange
        HashMap<Ingredient.PositionKeysForIngredients, Position> positions = new HashMap<>();
        positions.put(Ingredient.PositionKeysForIngredients.NAME, new Position(25, 30));
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, new Position(12, 17));
        positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(8, 11));
        Ingredient stepIngredient = new Ingredient("sauce", "ounce", 250.0, positions);

        // Act
        detector2.doTask();

        // Retrieve the sauce ingredient detected in the recipe step
        Collection<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();

        Ingredient detectedIngredient = null;
        for (Ingredient ingr : stepIngredients) {
            if (ingr.equals(stepIngredient)) {
                detectedIngredient = ingr;
            }
        }


        // Assert

        assert(detectedIngredient.getUnitPosition().equals(stepIngredient.getUnitPosition()));
        assert(detectedIngredient.getQuantityPosition().equals(stepIngredient.getQuantityPosition()));
        assert(detectedIngredient.getNamePosition().equals(stepIngredient.getNamePosition()));
    }


    @Test
    public void newTest(){
        RecipeStep step = new RecipeStep("Add one tablespoon of melted butter");
        RecipeInProgress rip = new RecipeInProgress("");
        ListIngredient ing = new ListIngredient("butter", "", 1.0, "  ",irrelevantPositions);
        ArrayList<ListIngredient> list = new ArrayList<>();
        list.add(ing);
        rip.setIngredients(list);
        ArrayList<RecipeStep> stepList = new ArrayList<>();
        stepList.add(step);
        rip.setRecipeSteps(stepList);

        DetectIngredientsInStepTask task = new DetectIngredientsInStepTask(rip, 0);
        task.doTask();
        System.out.println(step);
        assert(step.getIngredients().get(0).getUnit().equals("tablespoon"));

    }




}
