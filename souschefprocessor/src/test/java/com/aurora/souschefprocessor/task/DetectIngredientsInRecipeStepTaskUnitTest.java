package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class DetectIngredientsInRecipeStepTaskUnitTest {

    private static DetectIngredientsInStepTask detector0;
    private static DetectIngredientsInStepTask detector1;
    private static DetectIngredientsInStepTask detector2;
    private static DetectIngredientsInStepTask detector3;
    private static RecipeInProgress recipe;
    private static ArrayList<RecipeStep> recipeSteps;
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();

    private static final String DEFAULT_UNIT = "";
    private static final Double DEFAULT_QUANTITY = 1.0;

    @BeforeClass
    public static void initialize() {
        // Initialize recipe in progress
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);

        // Initialize positions with dummy values
        irrelevantPositions = new HashMap<>();
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }

        recipe.setIngredientsString("irrelevant");
        List<ListIngredient> set = new ArrayList<>();
        ListIngredient spaghettiIngredient = new ListIngredient("spaghetti", "g", 500, "irrelevant", irrelevantPositions);
        ListIngredient sauceIngredient = new ListIngredient("sauce", "ounces", 500, "irrelevant", irrelevantPositions);
        ListIngredient meatIngredient = new ListIngredient("minced meat", "pounds", 1.5, "irrelevant", irrelevantPositions);
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
        Set<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();
        for(Ingredient ingr : stepIngredients){
            if(ingr.getName().equals(stepIngredientNoQuantity.getName())){
                ingredientNoQuantity = ingr;
            }
            if(ingr.getName().equals(stepIngredientNoUnit.getName())){
                ingredientNoUnit = ingr;
            }
        }

        // Asserts the correct absence of both the quantity and the unit equality
        assert(stepIngredientNoQuantity.equals(ingredientNoQuantity));
        assert(stepIngredientNoUnit.equals(ingredientNoUnit));
    }

    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        /**
         * After doing the task the ingredients field cannot be null
         */
        // Act
        detector0.doTask();
        detector1.doTask();
        // Assert
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isIngredientDetected());
            assert (s.getIngredients() != null);
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_setHasCorrectSize() {
        detector0.doTask();
        assert(recipe.getRecipeSteps().get(0).getIngredients().size() == 1);

        detector1.doTask();
        assert(recipe.getRecipeSteps().get(1).getIngredients().size() == 2);
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithoutUnit(){
        detector0.doTask();
        Ingredient stepIngredient = new Ingredient("spaghetti", DEFAULT_UNIT, DEFAULT_QUANTITY, irrelevantPositions);

        System.out.println(recipe.getRecipeSteps().get(0).getIngredients());
        Set<Ingredient> stepIngredients = recipe.getRecipeSteps().get(0).getIngredients();
        assert(stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnit(){
        detector1.doTask();
        Ingredient stepIngredient = new Ingredient("garlic", "clove", DEFAULT_QUANTITY, irrelevantPositions);

        Set<Ingredient> stepIngredients = recipe.getRecipeSteps().get(1).getIngredients();
        assert(stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndVerboseQuantity(){
        detector2.doTask();
        Ingredient stepIngredient = new Ingredient("basil leaves", DEFAULT_UNIT, 5.0, irrelevantPositions);

        Set<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();
        assert(stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndNumericalQuantity(){
        detector2.doTask();
        Ingredient stepIngredient = new Ingredient("sauce", "ounces", 250.0, irrelevantPositions);

        Set<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();
        assert(stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndQuantityAndPosition(){
        detector2.doTask();

        HashMap<Ingredient.PositionKeysForIngredients, Position> positions = new HashMap<>();
        positions.put(Ingredient.PositionKeysForIngredients.NAME, new Position(26, 31));
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, new Position(12, 18));
        positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(8, 11));
        Ingredient stepIngredient = new Ingredient("sauce", "ounces", 250.0, positions);

        // Retrieve the sauce ingredient detected in the recipe step
        Set<Ingredient> stepIngredients = recipe.getRecipeSteps().get(2).getIngredients();
        Ingredient detectedIngredient = null;
        for(Ingredient ingr : stepIngredients){
            if(ingr.equals(stepIngredient)){
                detectedIngredient = ingr;
            }
        }

        // Assert that the sauce ingredient it's positions are correct
        assert(detectedIngredient.getNamePosition().equals(stepIngredient.getNamePosition()));
        assert(detectedIngredient.getUnitPosition().equals(stepIngredient.getUnitPosition()));
        assert(detectedIngredient.getQuantityPosition().equals(stepIngredient.getQuantityPosition()));
    }

}
