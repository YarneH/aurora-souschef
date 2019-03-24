package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class DetectIngredientsInListTaskUnitTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;
    private static String originalText;
    private static String ingredientList;
    private static CRFClassifier<CoreLabel> crfClassifier;


    private static HashMap<Ingredient.PositionKey, Position> irrelevantPositions = new HashMap<>();

    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {

        ingredientList = "500g spaghetti \n500 ounces sauce \n1 1/2 pounds minced meat\n 1 clove garlic\n twenty basil leaves\n salt";
        originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setIngredientsString(ingredientList);

        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        crfClassifier = CRFClassifier.getClassifier(modelName);

        detector = new DetectIngredientsInListTask(recipe, crfClassifier);

        Position pos = new Position(0, 1);
        for (Ingredient.PositionKey key : Ingredient.PositionKey.values()) {
            irrelevantPositions.put(key, pos);
        }
    }

    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
        recipe.setIngredientsString(ingredientList);
    }


    @Test
    public void DetectIngredientsInList_doTask_setHasBeenSet() {
        /**
         * After doing the detectingredientinlisttask the ingredients of the recipe cannot be null
         */
        // Act
        detector.doTask();
        // Assert
        assert (recipe.getIngredients() != null);
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasCorrectSize() {
        /**
         * After doing the detectingredientinlisttask the number of detected ingredients is correct
         */
        // Act
        detector.doTask();
        System.out.println(recipe.getIngredients());
        // Assert
        assert (recipe.getIngredients().size() == 5);
    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityNoPosition() {
        /**
         * These sample ingredients should be detected correctly
         */
        // Arrange
        Ingredient spaghettiIngredient = new ListIngredient("spaghetti", "g", 500, "irrelevant", irrelevantPositions);
        Ingredient sauceIngredient = new ListIngredient("sauce", "ounces", 500, "irrelevant", irrelevantPositions);
        Ingredient meatIngredient = new ListIngredient("minced meat", "pounds", 1.5, "irrelevant", irrelevantPositions);
        Ingredient garlicIngredient = new ListIngredient("garlic", "clove", 1.0, "irrelevant", irrelevantPositions);
        Ingredient basilIngredient = new ListIngredient("basil leaves", "", 20.0, "irrelevant", irrelevantPositions);
        // Act
        detector.doTask();

        // Assert
        boolean spaghetti = recipe.getIngredients().contains(spaghettiIngredient);
        boolean sauce = recipe.getIngredients().contains(sauceIngredient);
        boolean meat = recipe.getIngredients().contains(meatIngredient);
        boolean garlic = recipe.getIngredients().contains(garlicIngredient);
        boolean basil = recipe.getIngredients().contains(basilIngredient);
        assert (spaghetti);
        assert (sauce);
        assert (meat);
        assert (garlic);
        assert (basil);
    }

    @Test
    public void DetectIngredientsInList_doTask_ifNoIngredientsRaiseException() {
        // Arrange
        recipe.setIngredientsString("");
        boolean thrown = false;
        // Act
        try {
            detector.doTask();
        } catch (Exception e) {
            thrown = true;
        }
        // Assert
        assert (thrown);


    }

    @Test
    public void DetectIngredientsInList_doTask_randomInputStringDoesNotThrowError() {
        boolean thrown = false;

        recipe.setIngredientsString("20 pound xxx \n jqkfdksqfjkd// \n 45055 450 47d dkjq4 kdj  4 dqfd/n \n kjfqkf 450 ounce lfqj \n 20 pound xxx\"\n");
        try {
            detector.doTask();
        } catch (Exception e) {
            thrown = true;
        }
        assert (!thrown);
    }


    @Test
    public void DetectIngredientsInList_CorrectPositons() {
        detector.doTask();

        // first ingredient: 500 g spaghetti (spaces are added between numbers and letters)
        Position quantityPos = new Position(0, 3);
        Position unitPos = new Position(4, 5);
        Position namePos = new Position(6, 15);

        Ingredient ingredient = recipe.getIngredients().get(0);
        assert (ingredient.getNamePosition().equals(namePos));
        assert (ingredient.getQuantityPosition().equals(quantityPos));
        assert (ingredient.getUnitPosition().equals(unitPos));

        ingredientList = "500g spaghetti \n500 ounces sauce \n1 1/2 pounds minced meat\n 1 clove garlic\n twenty basil leaves";

        // second ingredient: 500 ounces sauce
        quantityPos = new Position(0, 3);
        unitPos = new Position(4, 10);
        namePos = new Position(11, 16);

        ingredient = recipe.getIngredients().get(1);
        assert (ingredient.getNamePosition().equals(namePos));
        assert (ingredient.getQuantityPosition().equals(quantityPos));
        assert (ingredient.getUnitPosition().equals(unitPos));

        // third ingredient 1 1/2 pounds minced meat
        quantityPos = new Position(0, 5);
        unitPos = new Position(6, 12);
        namePos = new Position(13, 24);

        ingredient = recipe.getIngredients().get(2);

        assert (ingredient.getQuantityPosition().equals(quantityPos));
        assert (ingredient.getUnitPosition().equals(unitPos));

        assert (ingredient.getNamePosition().equals(namePos));

        // fourth ingredient 1 clove garlic
        quantityPos = new Position(0, 1);
        unitPos = new Position(2, 7);
        namePos = new Position(8, 14);

        ingredient = recipe.getIngredients().get(3);
        assert (ingredient.getNamePosition().equals(namePos));

        assert (ingredient.getQuantityPosition().equals(quantityPos));
        assert (ingredient.getUnitPosition().equals(unitPos));

        // fifth ingredient twenty basil leaves
        quantityPos = new Position(0, 6);
        unitPos = new Position(0, 19); // the whole string because no unit
        namePos = new Position(7, 19);

        ingredient = recipe.getIngredients().get(4);
        assert (ingredient.getQuantityPosition().equals(quantityPos));
        assert (ingredient.getUnitPosition().equals(unitPos));
        assert (ingredient.getNamePosition().equals(namePos));


    }
}
