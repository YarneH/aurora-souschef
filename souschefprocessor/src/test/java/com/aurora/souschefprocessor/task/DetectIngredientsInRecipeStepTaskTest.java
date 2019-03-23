package com.aurora.souschefprocessor.task;

import android.util.Log;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschefprocessor.task.ingredientdetector.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static android.content.ContentValues.TAG;

//TODO Implement the required tests for positions and ingredients
public class DetectIngredientsInRecipeStepTaskTest {

    private static DetectIngredientsInStepTask detector0;
    private static DetectIngredientsInStepTask detector1;
    private static RecipeInProgress recipe;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static ArrayList<RecipeStep> recipeSteps;
    private static HashMap<Ingredient.PositionKey, Position> irrelevantPositions = new HashMap<>();
    private static String ingredientList;

    @BeforeClass
    public static void initialize() {
        // Initialize recipe in progress
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);

        // TODO change to hard coded ingredient list to avoid
        // TODO dependence on DetectIngredientsInStepTask
        // Initialize ingredient list
        ingredientList = initializeIngredientList();
        recipe.setIngredientsString(ingredientList);
        DetectIngredientsInListTask taskx = new DetectIngredientsInListTask(recipe, null);
        taskx.doTask();
        System.out.println("Ingredients detected in ingredients string: ");
        for(Ingredient ingr : recipe.getIngredients()){
            System.out.println(ingr.getName() + ", " + ingr.getUnit() + ", " + ingr.getAmount().getValue());
        }

        // Initialize positions with dummy values
        irrelevantPositions = new HashMap<>();
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKey key : Ingredient.PositionKey.values()) {
            irrelevantPositions.put(key, pos);
        }

        // Initialize recipe steps with dummy values
        recipeSteps = new ArrayList<>();
        RecipeStep s1 = new RecipeStep("Place the onion, garlic, ginger, curry powder, and cayenne pepper in a food processor and process to combine. Add the oil and process until a smooth puree is formed. Transfer the curry puree to a large pot and cook over medium heat, stirring frequently, about 5 minutes. Add the tomato paste and cook, stirring frequently, until the mixture begins to darken, about 5 minutes more.");
        RecipeStep s2 = new RecipeStep("Add the vegetable broth, coconut milk, cinnamon stick and ¼ teaspoon black pepper and bring to a boil. Reduce the heat and simmer for 10 minutes. Add the cauliflower, sweet potatoes, carrots, and tomatoes, season with salt and pepper, and return to a boil. Reduce the heat to medium low, cover, and simmer until the vegetables are tender, about 25 minutes. Remove the cinnamon stick. Stir in the lime zest and juice, chickpeas, and spinach and cook until the spinach is wilted, about 5 minutes. Season with up to ¾ teaspoon salt");

        recipeSteps.add(s1);
        recipeSteps.add(s2);
        int stepIndex0 = 0;
        int stepIndex1 = 1;
        recipe.setRecipeSteps(recipeSteps);

        // Initialize detectors
        detector0 = new DetectIngredientsInStepTask(recipe, stepIndex0);
        detector1 = new DetectIngredientsInStepTask(recipe, stepIndex1);
    }

    @After
    public void wipeRecipeSteps() {
        recipe.setIngredientsString(initializeIngredientList());
        for (RecipeStep s : recipeSteps) {
            s.setIngredients(null);
        }
    }

    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        detector0.doTask();
        detector1.doTask();

        for (RecipeStep s : recipe.getRecipeSteps()) {
            System.out.println(s.getIngredients());
            assert (s.isIngredientDetected());
            assert (s.getIngredients() != null);
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_stepsHaveCorrectElements() {
        detector0.doTask();
        detector1.doTask();

        for(int i = 0; i < recipe.getRecipeSteps().size(); i++){
            for(int x = 0; x < recipe.getRecipeSteps().get(i).getIngredients().size(); x++){
                System.out.println(recipe.getRecipeSteps().get(i).getIngredients());
            }
        }
    }

    private static String initializeIngredientList(){
        return "1 large onion, coarsely chopped\n" +
                "4 cloves garlic, peeled\n" +
                "1 1½-inch length fresh ginger, peeled and thinly sliced\n" +
                "1½ tablespoons yellow curry powder\n" +
                "¼ teaspoon cayenne pepper, plus more to taste\n" +
                "2 tablespoons canola oil\n" +
                "2 tablespoons tomato paste\n" +
                "2 cups low-sodium vegetable broth\n" +
                "1 cup light coconut milk\n" +
                "1 cinnamon stick\n" +
                "¼ teaspoon freshly ground black pepper, plus more to taste\n" +
                "½ head cauliflower, broken into 1½-inch-wide florets (about 3 cups)\n" +
                "1 pound sweet potatoes, peeled and cut into 1-inch cubes\n" +
                "2 large carrots, peeled and cut into 1-inch rounds\n" +
                "2 tomatoes, cored and chopped\n" +
                "Grated zest of 1 lime\n" +
                "2 tablespoons fresh lime juice\n" +
                "1 15-ounce can no-salt-added chickpeas, drained and rinsed \n" +
                "5 cups fresh baby spinach leaves\n" +
                "¾ teaspoon salt\n" +
                "¼ cup chopped fresh cilantro leaves\n" +
                "3 cups cooked brown rice, for serving, optional";
    }


}
