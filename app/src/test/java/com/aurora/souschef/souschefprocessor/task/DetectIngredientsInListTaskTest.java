package com.aurora.souschef.SouschefProcessor.task;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschef.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DetectIngredientsInListTaskTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static String originalText;
    private static String ingredientList;

    private static String testIngredients;
    private static ArrayList<String> testIngredientsList = new ArrayList<>();
    private static double[] testIngredientsQuantities = new double[100];
    private static boolean testIngredientsInitialized = false;
    private static RecipeInProgress testRecipe;
    private static DetectIngredientsInListTask testDetector;

    @BeforeClass
    public static void initialize() {
        ingredientList = "500 ounces spaghetti \n500 ounces sauce \n1 1/2 pounds minced meat\n 1 clove garlic\n twenty basil leaves";
        originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setIngredientsString(ingredientList);

        detector = new DetectIngredientsInListTask(recipe);
    }

    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
        recipe.setIngredientsString(ingredientList);
    }


    @Test
    public void DetectIngredientsInList_doTask_setHasBeenSet() {
        detector.doTask();
        assert (recipe.getIngredients() != null);
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasCorrectSize() {
        detector.doTask();
        System.out.println(recipe.getIngredients());
        assert (recipe.getIngredients().size() == 5);
    }

    @Test
    public void DetectIngredientsInList_doTask_setHasCorrectElements() {
        detector.doTask();
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "ounces", 500);
        Ingredient sauceIngredient = new Ingredient("sauce", "ounces", 500);
        Ingredient meatIngredient = new Ingredient("minced meat", "pounds", 1.5);
        Ingredient garlicIngredient = new Ingredient("garlic", "clove", 1.0);
        Ingredient basilIngredient = new Ingredient("basil leaves", "", 20.0);
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
    public void DetectIngredientsInList_doTask_ifNoIngredientsSetEmptyList() {
        recipe.setIngredientsString("");
        detector.doTask();
        assert (recipe.getIngredients() != null && recipe.getIngredients().size() == 0);

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
    public void DetectIngredientsInListTask_doTask_AccuracyForQuantityThreshold() {
        if (!testIngredientsInitialized) {
            initializeTestIngredients();

        }
        testDetector.doTask();

        int correct = 0;
        List<Ingredient> list = testRecipe.getIngredients();
        for (int i = 0; i < 100; i++) {
            if ((int) (1000 * list.get(i).getValue()) == (int) (1000 * testIngredientsQuantities[i])) {
                correct++;
            } else {
                System.out.println(testIngredientsQuantities[i] + " " + list.get(i).getValue());
                System.out.println(testIngredientsList.get(i));
            }
        }

        assert (correct >= 95);
    }

    private void initializeTestIngredients() {
        testIngredients = "1 quart fresh dark cherries\t1 \n" +
                "2 Tender Pie Crust dough disks\t2 \n" +
                "cracked black pepper\t1 \n" +
                "12 fresh Padron chile peppers\t12 \n" +
                "18 ounces bittersweet chocolate (62 to 72% cacao content), finely chopped, divided\t18 \n" +
                "3 green onions, thinly sliced\t3 \n" +
                "4 pounds large boiling potatoes (preferably white-fleshed)\t4 \n" +
                "2 (14.5 ounce) cans CONTADINA® Diced Tomatoes, undrained\t2 \n" +
                "1 pound tri-colored spiral pasta\t1 \n" +
                "1/4 teaspoon mace\t0.25 \n" +
                "Orange wedge\t1 \n" +
                "1 medium summer squash, halved and sliced\t1 \n" +
                "4 teaspoons crispy fried shallots\t4 \n" +
                "Oblong casserole dish (about 9 by 13 inches)\t1 \n" +
                "4 celery ribs from the heart with leaves, finely chopped\t4 \n" +
                "3/4 teaspoon anchovy paste or mashed anchovy fillet\t0.75 \n" +
                "1 lamb breast\t1 \n3 hearts of romaine, split lengthwise, cores removed, and coarsely chopped\t3 \n" +
                "1 2-inch piece fresh ginger, sliced\t1 \nBUTTER CRUNCH CRUST:\t1 \n" +
                "2 large onions, unpeeled, quartered\t2 \n" +
                "2 cups fresh peaches - peeled, pitted, and cut into bite-size pieces\t2 \n" +
                "1/4 cup brown mustard seeds\t0.25 \n" +
                "2 banana peppers, thinly sliced\t2 \n" +
                "3 cloves garlic, peeled and minced or pressed\t3 \n" +
                "1/4 cup diced dill pickles\t0.25 \n" +
                "1 handful fresh baby arugula\t1 \n" +
                "Pinch of ground cardamom\t1 \n" +
                "250ml/9fl oz cider or white wine\t250 \n" +
                "A good handful of fresh basil leaves, torn into small pieces\t1 \n" +
                "handful chives, finely chopped\t1 \n" +
                "1 pound boneless beef sirloin steak (about 1 inch thick)\t1 \n" +
                "3 chipotle chilies, canned in adobo\t3 \n" +
                "1/2 pound broccoli, sliced\t0.5 \n" +
                "4 4-ounce pieces skin-on hake or cod fillet\t4 \n" +
                "2 sweet potatoes, cleaned\t2 \n" +
                "1 (3.5 ounce) package microwave popcorn\t1 \n" +
                "2 black olives\t2 \n" +
                "2 cups whole milk, or more as needed\t2 \n" +
                "1/4 cup Chicken Stock\t0.25 \n" +
                "1/4 red onion, thinly sliced\t0.25 \n" +
                "2 celery stalks, finely chopped\t2 \n" +
                "1/4 pound cooked ham, cut into one inch cubes\t0.25 \n" +
                "1/2 teaspoon ground black pepper, divided\t0.5 \n1 large carrots, cut into thirds\t1 \n" +
                "4 pounds beef short ribs, cut into 2-inch lengths\t4 \n2 heads cabbage, finely shredded\t2 \n" +
                "1 (12 fluid ounce) can frozen berry juice concentrate, thawed\t1 \n" +
                "1/8 teaspoon seafood seasoning (such as Old Bay®), or to taste\t0.125 \n" +
                "1 carrot chopped fine\t1 \n5 Roma tomatoes\t5 \n" +
                "1/2 pound fresh shiitake mushrooms, stems discarded and caps cut into 1/2-inch slices (4 cups)\t0.5 \n" +
                "1 cup MIRACLE WHIP Dressing\t1 \n" +
                "1 teaspoon adobo sauce from canned chipotle chiles or more of taste (see Note)\t1 \n" +
                "2 teaspoon ground cinnamon\t2 \n1¼ cups whole wheat pastry flour (not bread flour), plus more if needed\t1.25 \n" +
                "2 tablespoon unsalted butter, melted\t2 \n4 (6- to 8-ounce) skinless, boneless halibut fillets\t4 \n" +
                "10 baby courgettes, finely sliced\t10 \n1/2 pound kale, stems trimmed, large ribs removed\t0.5 \n2 c. all-purpose flour\t2 \n" +
                "1 anchovy fillet, mashed to a paste\t1 \n1 medium celeriac\t1 \n2 slices Canadian bacon\t2 \n4 1/2-ounce cans vegetable broth\t4 \n" +
                "4 unwaxed lemons\t4 \n1/3 cup whole pitted black or kalamata olives\t0.3333333 \n1 tbsp fresh marjoram leaves\t1 \n" +
                "2 cups light soy sauce\t2 \n1/2 cup whipping cream, whipped\t0.5 \n" +
                "1/4 cup GENERAL FOODS INTERNATIONAL Sugar Free French Vanilla Cafe\t0.25 \n" +
                "1 (8 ounce) package frozen sugar snap peas, defrosted\t1 \n250g/9oz smooth peanut butter\t250 \n" +
                "12 ounces arugula (about 16 cups), tough stems removed\t12 \n4 pork fillets\t4 \n" +
                "2 1/2 cups rice-based gluten-free all-purpose flour*\t2.5 \n3 1/2 cups walnuts, chopped\t3.5 \n" +
                "2 tablespoons finely chopped dry-roasted peanuts\t2 \n" +
                "1 1/2 cups crumbled soft fresh goat cheese (such as Montrachet; about 7 ounces)\t1.5 \n2 very ripe (brown to black) plantains\t2 \n" +
                "30g/1oz dried 'instant noodles', seasoning sachet discarded\t30 \n1 orange, juice and zest only\t1 \n" +
                "2 pounds boneless pork loin roast, cut into 1-inch cubes\t2 \n50g/1¾oz demerara sugar\t50 \n" +
                "2 ounces cream cheese, room temperature\t2 \n1 heaping tablespoon freshly ground black pepper\t1 \n" +
                "2/3 cup solid vegetable shortening, melted, cooled\t0.6666667 \n1 1/2 teaspoons seeded, minced jalapeño\t1.5 \n" +
                "1/2 cup Bertolli® Extra Virgin Olive Oil\t0.5 \n2 large roasting potatoes, cut into cubes\t2 \n" +
                "1 1/2 teaspoons flaky sea salt\t1.5 \n150g/5½oz canned ackee, drained\t150 \n1 cup chopped Napa cabbage\t1 \n" +
                "1 1/2 cups light soy sauce\t1.5 \n2 cups refried beans\t2 \n1 qt. oil for frying\t1 \nPink rose petals\t1 \n" +
                "4 ounces pecorino cheese\t4 \n1 (18 ounce) bottle barbeque sauce (such as Montgomery Inn)\t1 \n" +
                "2 free-range eggs, separated\t2";
        String listForRecipe = "";
        List<String> list = Arrays.asList(testIngredients.split("\n"));
        int index = 0;
        for (String s : list) {
            String[] split = s.split("\t");
            listForRecipe += split[0] + "\n";
            testIngredientsQuantities[index] = Double.parseDouble(split[1]);
            testIngredientsList.add(s);
            index++;
        }

        testRecipe = new RecipeInProgress(originalText);
        testRecipe.setIngredientsString(listForRecipe);
        testDetector = new DetectIngredientsInListTask(testRecipe);
        testIngredientsInitialized = true;


    }


}
