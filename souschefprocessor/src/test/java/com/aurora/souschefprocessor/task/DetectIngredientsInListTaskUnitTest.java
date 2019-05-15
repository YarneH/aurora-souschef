package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;


public class DetectIngredientsInListTaskUnitTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;

    private static CRFClassifier<CoreLabel> crfClassifier;


    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static ExtractedText emptyExtractedText = new ExtractedText("", null);


    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {


       // create a recipeInProgress and detector
        recipe = new RecipeInProgress(emptyExtractedText);

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
        // after each test set the ingredients to an empty list
        recipe.setIngredients(null);
        recipe.setIngredientsString("");
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasBeenSet() {
        /*
         * After doing the detectingredientinlisttask the ingredients of the recipe cannot be empty
         */
        //Arrange put all the ingredients as the string
        String[] ingredientList = {"1 1/2 kg minced meat", "½ cup cashews",
                "2 1/2 cups tequila (such as 1800® Premium Reposado)", "1½ tablespoons tomato paste"};

        StringBuilder bld = new StringBuilder();
        for (String ing : ingredientList) {
            bld.append(ing);
            bld.append("\n");
        }
        recipe.setIngredientsString(bld.toString());
        // Act
        detector.doTask();
        // Assert
        assertFalse("The ingredients list is empty", recipe.getIngredients().isEmpty());
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasCorrectSize() {
        /**
         * After doing the detectingredientinlisttask the number of detected ingredients is correct
         */
        //Arrange put all the ingredients as the string
        String[] ingredientList = {"1 1/2 kg minced meat", "½ cup cashews",
                "2 1/2 cups tequila (such as 1800® Premium Reposado)", "1½ tablespoons tomato paste"};

        StringBuilder bld = new StringBuilder();
        for (String ing : ingredientList) {
            bld.append(ing);
            bld.append("\n");
        }
        recipe.setIngredientsString(bld.toString());
        // Act
        detector.doTask();
        System.out.println(recipe.getIngredients());
        // Assert
        assertEquals("The number of detected ingredients is not correct", ingredientList.length,
                recipe.getIngredients().size());

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForOuncesSimplestCase() {
        /*
        For a simplste case of QUANTITY UNIT NAME the detection is correct
         */
        // Arrange
        recipe.setIngredientsString("500 ounces sauce");
        ListIngredient sauceIngredient = new ListIngredient("sauce", "ounce", 500, "irrelevant", irrelevantPositions);
        // Act
        detector.doTask();
        // Assert

        assertThat(recipe.getIngredients(), CoreMatchers.hasItem(sauceIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForNoSpaceBetweenQuantityAndUnit() {
        /*
        If no space is between the value and unit the detection is still correct.
         */
        // Arrange
        recipe.setIngredientsString("500g spaghetti");
        ListIngredient spaghettiIngredient = new ListIngredient("spaghetti", "gram", 500, "irrelevant",
                irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        assertThat(recipe.getIngredients(), CoreMatchers.hasItem(spaghettiIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityFractions() {
        /*
        The detection is correct for ingredients with fractions and sums of fractions
         */
        // Arrange
        recipe.setIngredientsString("1 1/2 kg minced meat\n" + "½ cup cashews\n" + "2 1/2 cups tequila (such as 1800®" +
                " Premium Reposado)\n" + "1½ tablespoons tomato paste\n");
        ListIngredient meatIngredient = new ListIngredient("minced meat", "kilogram", 1.5, "irrelevant",
                irrelevantPositions);
        ListIngredient cashewIngredient = new ListIngredient("cashews", "cup", 0.5, "irrelevant", irrelevantPositions);
        ListIngredient tequillaIngredient = new ListIngredient("tequila", "cup", 2.5, "irrelevant",
                irrelevantPositions);
        ListIngredient pasteIngredient = new ListIngredient("tomato paste", "tablespoon", 1.5, "irrelevant",
                irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assertThat(list, CoreMatchers.hasItem(meatIngredient));
        assertThat(list, CoreMatchers.hasItem(cashewIngredient));
        assertThat(list, CoreMatchers.hasItem(pasteIngredient));
        assertThat(list,
                CoreMatchers.hasItem(tequillaIngredient));
    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForUnusualUnit() {
       /*
       The detection is correct for more unusual units
        */
        // Arrange
        recipe.setIngredientsString("1 clove garlic\n" + "4 slices Cheddar cheese\n" + "1 small handful shelled " +
                "unsalted pistachio nuts");
        ListIngredient garlicIngredient = new ListIngredient("garlic", "clove", 1.0, "irrelevant", irrelevantPositions);
        ListIngredient cheeseIngredient = new ListIngredient("Cheddar cheese", "slices", 4, "irrelevant",
                irrelevantPositions);
        ListIngredient pistachioIngredient = new ListIngredient("pistachio nuts", "small handful", 1, "irrelevant",
                irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(garlicIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(cheeseIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(pistachioIngredient));

    }


    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForSpelledOutQuantityAndNoUnit() {
        /*
        The detection is correct for a spelled out quantity
         */
        // Arrange
        recipe.setIngredientsString("twenty basil leaves");
        ListIngredient basilIngredient = new ListIngredient("basil leaves", "", 20.0, "irrelevant",
                irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        assertThat(recipe.getIngredients(), CoreMatchers.hasItem(basilIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForUnitWithPoint() {
       /*
       The detection is correct for abbreviated units
        */

        // Arrange
        recipe.setIngredientsString("1 tsp. sugar\n" + "2 tsp. whole mustard seeds\n" + "4 1/2 c. white rice");
        ListIngredient sugarIngredient = new ListIngredient("sugar", "teaspoon", 1.0, "irrelevant",
                irrelevantPositions);
        ListIngredient mustardIngredient = new ListIngredient("whole mustard seeds", "teaspoon", 2.0, "irrelevant",
                irrelevantPositions);
        ListIngredient riceIngredient = new ListIngredient("white rice", "cup", 4.5, "irrelevant",
                irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();

        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(sugarIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(mustardIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(riceIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForNoUnitAndNoQuantity() {
       /*
       The detection is correct if quantity and unit are not present
        */

        // Arrange
        recipe.setIngredientsString("salt\n" +
                "Olive oil (optional)\n" + "juice of 1 lemon");
        ListIngredient saltIngredient = new ListIngredient("salt", "", 1.0, "irrelevant", irrelevantPositions);
        ListIngredient oilIngredient = new ListIngredient("Olive oil", "", 1.0, "irrelevant", irrelevantPositions);
        ListIngredient juiceIngredient = new ListIngredient("juice of 1 lemon", "", 1.0, "irrelevant",
                irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();

        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(juiceIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(oilIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(saltIngredient));
    }


    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForCaseMultiplication() {
/*
The detection is correct in case of a multiplication sign in the quantity
 */
        // Arrange
        recipe.setIngredientsString("4 x 120 g pack mixed nuts and dried fruit\n" +
                "2 x 375g/13oz ready-made all-butter puff pastry");
        ListIngredient nutsIngredient = new ListIngredient("mixed nuts and dried fruit", "gram", 4 * 120, "irrelevant",
                irrelevantPositions);
        ListIngredient pastryIngredient = new ListIngredient("ready-made all-butter puff pastry", "gram", 2 * 375,
                "irrelevant", irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(nutsIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(pastryIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForSecondWordIsNotUnit() {

        /*
        The detection is correct if the second word is not the unit
         */
        // Arrange
        recipe.setIngredientsString("5 free-range eggs\n" + //free-range is not a unit but part of the name
                "1 large red bell pepper, cut into 3/4-inch-thick strips\n" + "2 large apples (or 3 medium)"); // large is not a unit
        ListIngredient eggsIngredient = new ListIngredient("free-range eggs", "", 5, "irrelevant", irrelevantPositions);
        ListIngredient pepperIngredient = new ListIngredient("large red bell pepper", "", 1, "irrelevant",
                irrelevantPositions);
        ListIngredient appleIngredient = new ListIngredient("large apples", "", 2, "irrelevant",
                irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(pepperIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(eggsIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(appleIngredient));

    }


    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForClutteredIngredientWithDash() {

        /*
        The detection is correct for a cluttered description with dashes
         */
        recipe.setIngredientsString("750–900ml/1⅓–1⅔ pint readymade chicken gravy\n" +
                "3-4 cups of rice");

        ListIngredient gravyIngredient = new ListIngredient("readymade chicken gravy", "milliliter", 750, "irrelevant",
                irrelevantPositions);
        ListIngredient riceIngredient = new ListIngredient("rice", "cup", 3, "irrelevant", irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(gravyIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(riceIngredient));

    }


    @Test
    public void DetectIngredientsInListTask_doTask_ClutterExamplesCorrect() {
        /*
        The detection is correct for a cluttered description
         */
        // Arrange
        String clutterExamples = "2.5kg/5lb 8oz turkey crown (fully thawed if frozen)\n" +
                "55g/2oz Stinking Bishop cheese, diced\n" +
                "500ml/18fl oz milk\n" +
                "200ml/7fl oz crème frâiche\n" +
                "350ml/12¼fl oz warm water\n" +
                "200ml/7fl oz fromage frais\n" +
                "100g/5½oz raisins";


        ListIngredient turkeyIngredient = new ListIngredient("turkey crown", "kilogram", 2.5, "irrelevant",
                irrelevantPositions);
        ListIngredient cheeseIngredient = new ListIngredient("Stinking Bishop cheese", "gram", 55, "irrelevant",
                irrelevantPositions);
        ListIngredient milkIngredient = new ListIngredient("milk", "milliliter", 500.0, "irrelevant",
                irrelevantPositions);
        ListIngredient cremeIngredient = new ListIngredient("crème frâiche", "milliliter", 200, "irrelevant",
                irrelevantPositions);
        ListIngredient waterIngredient = new ListIngredient("warm water", "milliliter", 350, "irrelevant",
                irrelevantPositions);
        ListIngredient fromageIngredient = new ListIngredient("fromage frais", "milliliter", 200, "irrelevant",
                irrelevantPositions);
        ListIngredient raisinsIngredient = new ListIngredient("raisins", "gram", 100, "irrelevant",
                irrelevantPositions);

        // Act
        recipe.setIngredientsString(clutterExamples);
        detector.doTask();

        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(turkeyIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(cheeseIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(milkIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(cremeIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(waterIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(fromageIngredient));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(raisinsIngredient));
    }

    @Test
    public void DetectIngredientsInList_CorrectPositons() {

        /*
        The positions are correctly detected for the ingredients
         */
        // Arrange
        recipe.setIngredientsString("500g spaghetti \n500 ounces sauce \n1 1/2 kg minced meat\n 1 clove garlic\n " +
                "twenty basil leaves");

        // Act
        detector.doTask();

        // first ingredient: 500g spaghetti ->500 gram spaghetti (spaces are added between numbers and letters)
        // Arrange
        Position quantityPos = new Position(0, 3);
        Position unitPos = new Position(4, 8);
        Position namePos = new Position(9, 18);

        // Assert
        Ingredient ingredient = recipe.getIngredients().get(0);
        System.out.println(((ListIngredient) ingredient).getOriginalLine());
        assertEquals("The name position is not equal", namePos, ingredient.getNamePosition());
        assertEquals("The unit position is not equal", unitPos, ingredient.getUnitPosition());
        assertEquals("The quantity position is not equal", quantityPos, ingredient.getQuantityPosition());

        // second ingredient: 500 ounces sauce -> 500 ounce sauce
        quantityPos = new Position(0, 3);
        unitPos = new Position(4, 9);
        namePos = new Position(10, 15);

        ingredient = recipe.getIngredients().get(1);
        assertEquals("The name position is not equal", namePos, ingredient.getNamePosition());
        assertEquals("The unit position is not equal", unitPos, ingredient.getUnitPosition());
        assertEquals("The quantity position is not equal", quantityPos, ingredient.getQuantityPosition());


        // third ingredient 1 1/2 kg minced meat
        quantityPos = new Position(0, 5);
        unitPos = new Position(6, 14);
        namePos = new Position(15, 26);

        ingredient = recipe.getIngredients().get(2);

        assertEquals("The name position is not equal", namePos, ingredient.getNamePosition());
        assertEquals("The unit position is not equal", unitPos, ingredient.getUnitPosition());
        assertEquals("The quantity position is not equal", quantityPos, ingredient.getQuantityPosition());


        // fourth ingredient 1 clove garlic
        quantityPos = new Position(0, 1);
        unitPos = new Position(2, 7);
        namePos = new Position(8, 14);

        ingredient = recipe.getIngredients().get(3);

        assertEquals("The name position is not equal", namePos, ingredient.getNamePosition());
        assertEquals("The unit position is not equal", unitPos, ingredient.getUnitPosition());
        assertEquals("The quantity position is not equal", quantityPos, ingredient.getQuantityPosition());


        // fifth ingredient twenty basil leaves
        quantityPos = new Position(0, 6);
        unitPos = new Position(0, 19); // the whole string because no unit
        namePos = new Position(7, 19);

        ingredient = recipe.getIngredients().get(4);
        assertEquals("The name position is not equal", namePos, ingredient.getNamePosition());
        assertEquals("The unit position is not equal", unitPos, ingredient.getUnitPosition());
        assertEquals("The quantity position is not equal", quantityPos, ingredient.getQuantityPosition());

    }

    @Test(expected = RecipeDetectionException.class)
    public void DetectIngredientsInList_doTask_ifNoIngredientsRaiseException() {
        /*
        An empty ingredientsString should  throw an error if processed
         */
        // Arrange
        recipe.setIngredientsString("");
        detector.doTask();

    }

    @Test
    public void DetectIngredientsInList_doTask_randomInputStringDoesNotThrowError() {
        /*
        A jibberish ingredientsstring does not throw an exception
         */
        recipe.setIngredientsString("20 pound xxx \n jqkfdksqfjkd// \n 45055 450 47d dkjq4 kdj  4 dqfd/n \n kjfqkf " +
                "450 ounce lfqj \n 20 pound xxx\"\n qdjfaejzotinbgqgkjmajtlkjlkjmf44sdqf5dsfsdf454545 545454 54 " +
                "54f5qdfkjs");
        // test will fail if this throws an exception
        detector.doTask();
    }


    @Test
    public void DetectIngredientsInListTask_doTask_IngredientsWithWeirdBackslashForFraction() {
        /*
        Ingredients with a weird backslash are detected correctly
         */
        // Arrange
        String ingredients = "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper";
        ListIngredient onion = new ListIngredient("chopped onion", "cup", 0.5,"irrelevant", irrelevantPositions);
        ListIngredient bellPepper = new ListIngredient("chopped bell pepper", "cup", 0.25,"irrelevant", irrelevantPositions);
        ListIngredient salt = new ListIngredient("salt", "teaspoon", 0.25, "irrelevant",irrelevantPositions);
        ListIngredient garlicSalt = new ListIngredient("garlic salt", "teaspoon", 0.25,"irrelevant", irrelevantPositions);
        ListIngredient pepper = new ListIngredient("black pepper", "teaspoon", 0.5, "irrelevant",irrelevantPositions);


        //Act
        recipe.setIngredientsString(ingredients);
        detector.doTask();

        //Assert
        List<ListIngredient> list = recipe.getIngredients();
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(onion));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(bellPepper));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(salt));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(garlicSalt));
        assertThat("The ingredient is not contained", list, CoreMatchers.hasItem(pepper));
    }

}
