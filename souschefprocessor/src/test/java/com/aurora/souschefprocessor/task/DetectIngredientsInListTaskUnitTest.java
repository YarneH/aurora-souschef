package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class DetectIngredientsInListTaskUnitTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;
    private static String originalText;
    private static String ingredientList;
    private static CRFClassifier<CoreLabel> crfClassifier;


    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();

    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {

        ingredientList = "500g spaghetti \n500 ounces sauce \n1 1/2 kg minced meat\n 1 clove garlic\n twenty basil leaves\n" +
                "salt\n 1 tsp. sugar\n4 x 120 g pack mixed nuts and dried fruit";
        originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setIngredientsString(ingredientList);

        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        crfClassifier = CRFClassifier.getClassifier(modelName);

        detector = new DetectIngredientsInListTask(recipe, crfClassifier);

        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }
    }

    @Before
    public void wipeRecipe() {
        ingredientList = "500g spaghetti \n500 ounces sauce \n1 1/2 kg minced meat\n 1 clove garlic\n twenty basil leaves\n" +
                "salt\n 1 tsp. sugar\n4 x 120 g pack mixed nuts and dried fruit";
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
        assert (recipe.getIngredients().size() == 8);
    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityNoPosition() {
        /**
         * These sample ingredients should be detected correctly
         */
        // Arrange
        Ingredient spaghettiIngredient = new ListIngredient("spaghetti", "g", 500, "irrelevant", irrelevantPositions);
        Ingredient sauceIngredient = new ListIngredient("sauce", "ounces", 500, "irrelevant", irrelevantPositions);
        Ingredient meatIngredient = new ListIngredient("minced meat", "kg", 1.5, "irrelevant", irrelevantPositions);
        Ingredient garlicIngredient = new ListIngredient("garlic", "clove", 1.0, "irrelevant", irrelevantPositions);
        Ingredient basilIngredient = new ListIngredient("basil leaves", "", 20.0, "irrelevant", irrelevantPositions);
        Ingredient sugarIngredient = new ListIngredient("sugar", "tsp", 1.0, "irrelevant", irrelevantPositions);
        Ingredient saltIngredient = new ListIngredient("salt", "", 1.0, "irrelevant", irrelevantPositions);
        Ingredient nutsIngredient = new ListIngredient("mixed nuts and dried fruit", "g", 4*120, "irrelevant", irrelevantPositions);
        // Act
        detector.doTask();

        // Assert
        boolean spaghetti = recipe.getIngredients().contains(spaghettiIngredient);
        boolean sauce = recipe.getIngredients().contains(sauceIngredient);
        boolean meat = recipe.getIngredients().contains(meatIngredient);
        boolean garlic = recipe.getIngredients().contains(garlicIngredient);
        boolean basil = recipe.getIngredients().contains(basilIngredient);
        boolean sugar = recipe.getIngredients().contains(sugarIngredient);
        boolean salt = recipe.getIngredients().contains(saltIngredient);
        boolean nuts = recipe.getIngredients().contains(nutsIngredient);
        assert (spaghetti);
        assert (sauce);
        assert (meat);
        assert (garlic);
        assert (basil);
        assert (sugar);
        assert (salt);
        assert(nuts);
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

        // third ingredient 1 1/2 kg minced meat
        quantityPos = new Position(0, 5);
        unitPos = new Position(6, 8);
        namePos = new Position(9, 20);

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

    @Test
    public void DetectIngredientsInListTask_doTask_ClutterExamplesCorrect() {
        // Arrange
        String clutterExamples = "2.5kg/5lb 8oz turkey crown (fully thawed if frozen)\n" +
                "750–900ml/1⅓–1⅔ pint readymade chicken gravy\n" +
                "500ml/18fl oz milk\n" +
                "200ml/7fl oz crème frâiche\n" +
                "350ml/12¼fl oz warm water\n" +
                "200ml/7fl oz fromage frais\n" +
                "100g/5½oz raisins";
        RecipeInProgress rip = new RecipeInProgress("");
        rip.setIngredientsString(clutterExamples);
        DetectIngredientsInListTask task = new DetectIngredientsInListTask(rip, crfClassifier);
        Ingredient turkeyIngredient = new Ingredient("turkey crown", "kg", 2.5, irrelevantPositions);
        Ingredient gravyIngredient = new Ingredient("readymade chicken gravy", "ml", 750, irrelevantPositions);
        Ingredient milkIngredient = new Ingredient("milk", "ml", 500.0, irrelevantPositions);
        Ingredient cremeIngredient = new Ingredient("crème frâiche", "ml", 200, irrelevantPositions);
        Ingredient waterIngredient = new Ingredient("warm water", "ml", 350, irrelevantPositions);
        Ingredient fromageIngredient = new Ingredient("fromage frais", "ml", 200, irrelevantPositions);
        Ingredient raisinsIngredient = new Ingredient("raisins", "g", 100, irrelevantPositions);

        // Act
        task.doTask();

        // Assert
        List<ListIngredient> list = rip.getIngredients();
        boolean turkey = list.contains(turkeyIngredient);
        boolean gravy = list.contains(gravyIngredient);
        boolean milk = list.contains(milkIngredient);
        boolean creme = list.contains(cremeIngredient);
        boolean water = list.contains(waterIngredient);
        boolean fromage = list.contains(fromageIngredient);
        boolean raisins = list.contains(raisinsIngredient);
        assert(turkey);
        assert(gravy);
        assert(milk);
        assert(creme);
        assert(water);
        assert(fromage);
        assert(raisins);


    }
}
