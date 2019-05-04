package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
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

import static org.junit.Assert.assertEquals;

public class DetectIngredientsInListTaskUnitTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;

    private static CRFClassifier<CoreLabel> crfClassifier;


    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static ExtractedText emptyExtractedText = new ExtractedText("", null);


    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {


        String originalText = "irrelevant";
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

        recipe.setIngredients(null);
        recipe.setIngredientsString("");
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasBeenSet() {
        /**
         * After doing the detectingredientinlisttask the ingredients of the recipe cannot be null
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
        assert (recipe.getIngredients() != null);
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
        assert (recipe.getIngredients().size() == ingredientList.length);
    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForOuncesSimplestCase() {

        // Arrange
        recipe.setIngredientsString("500 ounces sauce");
        Ingredient sauceIngredient = new ListIngredient("sauce", "ounce", 500, "irrelevant", irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        assert (recipe.getIngredients().contains(sauceIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForNoSpaceBetweenQuantityAndUnit() {
        //ingredientList[0] = "500g spaghetti"; //0 "g" and no space
        // Arrange
        recipe.setIngredientsString("500g spaghetti");
        Ingredient spaghettiIngredient = new ListIngredient("spaghetti", "gram", 500, "irrelevant", irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        assert (recipe.getIngredients().contains(spaghettiIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityFractions() {
        //ingredientList[2] = "1 1/2 kg minced meat"; //2 kg and sum of number and fraction
        // Arrange
        recipe.setIngredientsString("1 1/2 kg minced meat\n" + "½ cup cashews\n" + "2 1/2 cups tequila (such as 1800® Premium Reposado)\n" + "1½ tablespoons tomato paste\n");
        Ingredient meatIngredient = new ListIngredient("minced meat", "kilogram", 1.5, "irrelevant", irrelevantPositions);
        Ingredient cashewIngredient = new ListIngredient("cashews", "cup", 0.5, "irrelevant", irrelevantPositions);
        Ingredient tequillaIngredient = new ListIngredient("tequila", "cup", 2.5, "irrelevant", irrelevantPositions);
        Ingredient pasteIngredient = new ListIngredient("tomato paste", "tablespoon", 1.5, "irrelevant", irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assert (list.contains(meatIngredient));
        assert (list.contains(cashewIngredient));
        assert (list.contains(tequillaIngredient));

        assert (list.contains(pasteIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForUnusualUnit() {
        // ingredientList[3] = "1 clove garlic"; //3 clove as unit
        // Arrange
        recipe.setIngredientsString("1 clove garlic\n" + "4 slices Cheddar cheese\n" + "1 small handful shelled unsalted pistachio nuts");
        Ingredient garlicIngredient = new ListIngredient("garlic", "clove", 1.0, "irrelevant", irrelevantPositions);
        Ingredient cheeseIngredient = new ListIngredient("Cheddar cheese", "slices", 4, "irrelevant", irrelevantPositions);
        Ingredient pistachioIngredient = new ListIngredient("pistachio nuts", "small handful", 1, "irrelevant", irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assert (list.contains(garlicIngredient));
        assert (list.contains(cheeseIngredient));
        assert (list.contains(pistachioIngredient));

    }


    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForSpelledOutQuantityAndNoUnit() {

        // ingredientList[4] = "twenty basil leaves"; //4 no unit and spelled out number
        // Arrange
        recipe.setIngredientsString("twenty basil leaves");
        Ingredient basilIngredient = new ListIngredient("basil leaves", "", 20.0, "irrelevant", irrelevantPositions);
        // Act
        detector.doTask();
        // Assert
        assert (recipe.getIngredients().contains(basilIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForUnitWithPoint() {
        //ingredientList[6] = "1 tsp. sugar"; //6 unit with a "."

        // Arrange
        recipe.setIngredientsString("1 tsp. sugar\n" + "2 tsp. whole mustard seeds");
        Ingredient sugarIngredient = new ListIngredient("sugar", "teaspoon", 1.0, "irrelevant", irrelevantPositions);
        Ingredient mustardIngredient = new ListIngredient("whole mustard seeds", "teaspoon", 2.0, "irrelevant", irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assert (list.contains(mustardIngredient));
        assert (list.contains(sugarIngredient));
    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForNoUnitAndNoQuantity() {
        // ingredientList[5] = "salt"; //5 no quantity and unit

        // Arrange
        recipe.setIngredientsString("salt\n" +
                "Olive oil (optional)\n" + "juice of 1 lemon");
        Ingredient saltIngredient = new ListIngredient("salt", "", 1.0, "irrelevant", irrelevantPositions);
        Ingredient oilIngredient = new ListIngredient("Olive oil", "", 1.0, "irrelevant", irrelevantPositions);
        Ingredient juiceIngredient = new ListIngredient("juice of 1 lemon", "", 1.0, "irrelevant", irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assert (list.contains(saltIngredient));
        assert (list.contains(oilIngredient));
        assert (list.contains(juiceIngredient));
    }


    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForCaseMultiplication() {

        // Arrange
        recipe.setIngredientsString("4 x 120 g pack mixed nuts and dried fruit\n" +
                "2 x 375g/13oz ready-made all-butter puff pastry");
        Ingredient nutsIngredient = new ListIngredient("mixed nuts and dried fruit", "gram", 4 * 120, "irrelevant", irrelevantPositions);
        Ingredient pastryIngredient = new ListIngredient("ready-made all-butter puff pastry", "gram", 2 * 375, "irrelevant", irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assert (list.contains(nutsIngredient));
        assert (list.contains(pastryIngredient));

    }

    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForSecondWordIsNotUnit() {

        // Arrange
        recipe.setIngredientsString("5 free-range eggs\n" + //free-range is not a unit but part of the name
                "1 large red bell pepper, cut into 3/4-inch-thick strips"); // large is not a unit
        Ingredient eggsIngredient = new ListIngredient("free-range eggs", "", 5, "irrelevant", irrelevantPositions);
        Ingredient pepperIngredient = new ListIngredient("large red bell pepper", "", 1, "irrelevant", irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        List<ListIngredient> list = recipe.getIngredients();
        System.out.println(recipe.getIngredients());
        assert (list.contains(eggsIngredient));
        assert (list.contains(pepperIngredient));

    }


    @Test
    public void DetectIngredientsInList_doTask_correctDetectionOfNameUnitAndQuantityForClutteredIngredientWithDash() {

        //  ingredientList[10] =  "750–900ml/1⅓–1⅔ pint readymade chicken gravy"; //10 cluttered with a dash

        recipe.setIngredientsString("750–900ml/1⅓–1⅔ pint readymade chicken gravy\n" +
                "3-4 cups of rice");

        Ingredient gravyIngredient = new Ingredient("readymade chicken gravy", "milliliter", 750, irrelevantPositions);
        Ingredient riceIngredient = new Ingredient("rice", "cup", 3, irrelevantPositions);

        // Act
        detector.doTask();
        // Assert
        System.out.println(recipe.getIngredients());
        assert (recipe.getIngredients().contains(gravyIngredient));
        assert (recipe.getIngredients().contains(riceIngredient));

    }


    @Test
    public void DetectIngredientsInListTask_doTask_ClutterExamplesCorrect() {
        // Arrange
        String clutterExamples = "2.5kg/5lb 8oz turkey crown (fully thawed if frozen)\n" +
                "55g/2oz Stinking Bishop cheese, diced\n" +
                "500ml/18fl oz milk\n" +
                "200ml/7fl oz crème frâiche\n" +
                "350ml/12¼fl oz warm water\n" +
                "200ml/7fl oz fromage frais\n" +
                "100g/5½oz raisins";
        RecipeInProgress rip = new RecipeInProgress(emptyExtractedText);
        rip.setIngredientsString(clutterExamples);
        DetectIngredientsInListTask task = new DetectIngredientsInListTask(rip, crfClassifier);
        Ingredient turkeyIngredient = new Ingredient("turkey crown", "kilogram", 2.5, irrelevantPositions);
        Ingredient cheeseIngredient = new ListIngredient("Stinking Bishop cheese", "gram", 55, "irrelevant", irrelevantPositions);
        Ingredient milkIngredient = new Ingredient("milk", "milliliter", 500.0, irrelevantPositions);
        Ingredient cremeIngredient = new Ingredient("crème frâiche", "milliliter", 200, irrelevantPositions);
        Ingredient waterIngredient = new Ingredient("warm water", "milliliter", 350, irrelevantPositions);
        Ingredient fromageIngredient = new Ingredient("fromage frais", "milliliter", 200, irrelevantPositions);
        Ingredient raisinsIngredient = new Ingredient("raisins", "gram", 100, irrelevantPositions);

        // Act
        task.doTask();

        // Assert
        List<ListIngredient> list = rip.getIngredients();
        boolean turkey = list.contains(turkeyIngredient);
        boolean cheese = list.contains(cheeseIngredient);
        boolean milk = list.contains(milkIngredient);
        boolean creme = list.contains(cremeIngredient);
        boolean water = list.contains(waterIngredient);
        boolean fromage = list.contains(fromageIngredient);
        boolean raisins = list.contains(raisinsIngredient);
        assert (turkey);
        assert (milk);
        assert (creme);
        assert (water);
        assert (fromage);
        assert (raisins);
        assert (cheese);


    }

    @Test
    public void DetectIngredientsInList_CorrectPositons() {
        // Arrange
        recipe.setIngredientsString("500g spaghetti \n500 ounces sauce \n1 1/2 kg minced meat\n 1 clove garlic\n twenty basil leaves");

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
        assert (ingredient.getNamePosition().equals(namePos));
        assert (ingredient.getQuantityPosition().equals(quantityPos));
        assert (ingredient.getUnitPosition().equals(unitPos));

        // second ingredient: 500 ounces sauce -> 500 ounce sauce
        quantityPos = new Position(0, 3);
        unitPos = new Position(4, 9);
        namePos = new Position(10, 15);

        ingredient = recipe.getIngredients().get(1);
        assert (ingredient.getNamePosition().equals(namePos));
        assert (ingredient.getQuantityPosition().equals(quantityPos));
        assert (ingredient.getUnitPosition().equals(unitPos));

        // third ingredient 1 1/2 kg minced meat
        quantityPos = new Position(0, 5);
        unitPos = new Position(6, 14);
        namePos = new Position(15, 26);

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
        // test will fail if this throws an exception
        detector.doTask();
    }


    @Test
    public void DetectIngredientsInListTask_doTask_IngredientsWithWeirdBackslashForFraction() {
        // Arrange
        String ingredients = "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper";
        Ingredient onion = new Ingredient("chopped onion", "cup", 0.5, irrelevantPositions);
        Ingredient bellPepper = new Ingredient("chopped bell pepper", "cup", 0.25, irrelevantPositions);
        Ingredient salt = new Ingredient("salt", "teaspoon", 0.25, irrelevantPositions);
        Ingredient garlicSalt = new Ingredient("garlic salt", "teaspoon", 0.25, irrelevantPositions);
        Ingredient pepper = new Ingredient("black pepper", "teaspoon", 0.5, irrelevantPositions);


        RecipeInProgress rip = new RecipeInProgress(emptyExtractedText);
        rip.setIngredientsString(ingredients);
        DetectIngredientsInListTask task = new DetectIngredientsInListTask(rip, crfClassifier);

        //Act
        task.doTask();

        //Assert
        System.out.println(rip.getIngredients());
        assert (rip.getIngredients().contains(salt));
        assert (rip.getIngredients().contains(garlicSalt));
        assert (rip.getIngredients().contains(pepper));
        assert (rip.getIngredients().contains(bellPepper));
        assert (rip.getIngredients().contains(onion));
    }


    @Test
    public void DetectIngredientsInListTask_doTask_getOriginalLineWithoutUnitAndQuantityCorrect() {
        String ingredient = " 1 lb. linguine or other long pasta";
        RecipeInProgress rip = new RecipeInProgress(emptyExtractedText);
        rip.setIngredientsString(ingredient);

        // Do the detecting
        DetectIngredientsInListTask task = new DetectIngredientsInListTask(rip, crfClassifier);
        task.doTask();
        assertEquals("The line for the UI is not as expected", "linguine or other long pasta", rip.getIngredients().get(0).getOriginalLineWithoutUnitAndQuantity());

        //convert and check if still the same
        rip.convertUnit(true);
        assertEquals("The line for the UI is not as expected after conversion", "linguine or other long pasta", rip.getIngredients().get(0).getOriginalLineWithoutUnitAndQuantity());


    }

    @Test
    public void DetectIngredientsInListTask_doTask_CorrectForIngredientsWithLargeInDescription() {
        String ingredient = "2 large apples (or 3 medium)";
        RecipeInProgress rip = new RecipeInProgress(emptyExtractedText);
        rip.setIngredientsString(ingredient);
        ListIngredient target = new ListIngredient("large apples", "", 2, "irrelevant", irrelevantPositions);

        DetectIngredientsInListTask task = new DetectIngredientsInListTask(rip, crfClassifier);
        task.doTask();

        ListIngredient detected = rip.getIngredients().get(0);
        assertEquals("The detected ingredient is incorrect", target, detected);
    }


}
