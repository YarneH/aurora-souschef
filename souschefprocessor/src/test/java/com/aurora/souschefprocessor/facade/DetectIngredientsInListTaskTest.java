package com.aurora.souschefprocessor.facade;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class DetectIngredientsInListTaskTest {

    private static RecipeInProgress recipe;
    private static DetectIngredientsInListTask detector;
    private static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    private static String originalText;
    private static String ingredientList;

    private static String testIngredients;
    private static String[] testIngredientsUnits = new String[100];
    private static ArrayList<String> testIngredientsList = new ArrayList<>();
    private static double[] testIngredientsQuantities = new double[100];
    private static boolean testIngredientsInitialized = false;
    private static RecipeInProgress testRecipe;
    private static DetectIngredientsInListTask testDetector;

    private static CRFClassifier<CoreLabel> crfClassifier;

    @BeforeClass
    public static void initialize() {

        ingredientList = "500g spaghetti \n500 ounces sauce \n1 1/2 pounds minced meat\n 1 clove garlic\n twenty basil leaves";
        originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setIngredientsString(ingredientList);

        detector = new DetectIngredientsInListTask(recipe, null);
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
        Ingredient spaghettiIngredient = new Ingredient("spaghetti", "g", 500, "irrelevant");
        Ingredient sauceIngredient = new Ingredient("sauce", "ounces", 500, "irrelevant");
        Ingredient meatIngredient = new Ingredient("minced meat", "pounds", 1.5, "irrelevant");
        Ingredient garlicIngredient = new Ingredient("garlic", "clove", 1.0, "irrelevant");
        Ingredient basilIngredient = new Ingredient("basil leaves", "", 20.0, "irrelevant");
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
            // check if they are equal up to 3 decimal places
            if ((int) (1000 * list.get(i).getValue()) == (int) (1000 * testIngredientsQuantities[i])) {
                correct++;
            } else {
                System.out.println(testIngredientsQuantities[i] + " " + list.get(i).getValue());
                System.out.println(testIngredientsList.get(i));
            }
        }

        assert (correct >= 95);
    }

    @Test
    public void DetectIngredientsInListTask_doTask_AccuracyForUnitThreshold() {
        if (!testIngredientsInitialized) {
            initializeTestIngredients();

        }
        testDetector.doTask();

        int correct = 0;
        int correctButOneCharOff = 0;
        List<Ingredient> list = testRecipe.getIngredients();
        for (int i = 0; i < 100; i++) {
            // check if they are equal up to 3 decimal places
            if ((testIngredientsUnits[i]).equals(list.get(i).getUnit())) {
                correct++;
            } else if (oneCharOff(testIngredientsUnits[i], list.get(i).getUnit())) {
                correctButOneCharOff++;
            } else {

                System.out.println(testIngredientsUnits[i] + " " + list.get(i).getUnit());
                System.out.println(testIngredientsList.get(i));
            }
        }

        assert (correct + correctButOneCharOff >= 80);
        assert (correctButOneCharOff < 5);
        System.out.println(correct + " units were correctly set and " + correctButOneCharOff + " were correct with one char off");
    }

    private boolean oneCharOff(String a, String b) {
        if (Math.abs(a.length() - b.length()) > 1) {
            return false;
        }
        boolean noDifferenceFoundYet = true;
        boolean deletion = false;
        boolean wrongChar = false;
        String smallest = "";
        String biggest = "";
        if (a.length() < b.length()) {
            smallest = a;
            biggest = b;
            deletion = true;
        } else if (a.length() > b.length()) {
            smallest = b;
            biggest = a;
            deletion = true;
        } else {
            wrongChar = true;
        }

        if (wrongChar) {
            for (int i = 0; i < a.length(); i++) {
                if (a.charAt(i) != b.charAt(i)) {
                    if (noDifferenceFoundYet) {
                        noDifferenceFoundYet = false;
                    } else {
                        return false;
                    }

                }

            }
            return true;
        }
        if (deletion) {
            for (int i = 0; i < smallest.length(); i++) {
                if (noDifferenceFoundYet) {
                    if (smallest.charAt(i) != biggest.charAt(i)) {
                        noDifferenceFoundYet = false;
                        if (smallest.charAt(i) != biggest.charAt(i + 1)) {
                            return false;
                        }
                    }
                } else {
                    if (smallest.charAt(i) != biggest.charAt(i + 1)) {
                        return false;
                    }
                }

            }
            return true;
        }
        //should not get here
        return false;
    }


    private void initializeTestIngredients() {
        testIngredients = "1 quart fresh dark cherries\t1\tquart \n" +
                "2 Tender Pie Crust dough disks\t2\t \n" +
                "cracked black pepper\t1\t  \n" +
                "12 fresh Padron chile peppers\t12\t  \n" +
                "18 ounces bittersweet chocolate (62 to 72% cacao content), finely chopped, divided\t18\tounces \n" +
                "3 green onions, thinly sliced\t3\t  \n" +
                "4 pounds large boiling potatoes (preferably white-fleshed)\t4\tpounds \n" +
                "2 (14.5 ounce) cans CONTADINA® Diced Tomatoes, undrained\t2\tcans \n" +
                "1 pound tri-colored spiral pasta\t1\tpound \n" +
                "1/4 teaspoon mace\t0.25\tteaspoon \n" +
                "Orange wedge\t1\t  \n" +
                "1 medium summer squash, halved and sliced\t1\t  \n" +
                "4 teaspoons crispy fried shallots\t4\tteaspoons  \n" +
                "Oblong casserole dish (about 9 by 13 inches)\t1\t  \n" +
                "4 celery ribs from the heart with leaves, finely chopped\t4\t  \n" +
                "3/4 teaspoon anchovy paste or mashed anchovy fillet\t0.75\t teaspoon \n" +
                "1 lamb breast\t1\t  \n" +
                "3 hearts of romaine, split lengthwise, cores removed, and coarsely chopped\t3\t  \n" +
                "1 2-inch piece fresh ginger, sliced\t1\t2-inch piece \n" +
                "BUTTER CRUNCH CRUST:\t1\t  \n" +
                "2 large onions, unpeeled, quartered\t2\t  \n" +
                "2 cups fresh peaches - peeled, pitted, and cut into bite-size pieces\t2\tcups  \n" +
                "1/4 cup brown mustard seeds\t0.25\t cup \n" +
                "2 banana peppers, thinly sliced\t2\t  \n" +
                "3 cloves garlic, peeled and minced or pressed\t3\tcloves \n" +
                "1/4 cup diced dill pickles\t0.25\tcup  \n" +
                "1 handful fresh baby arugula\t1\t handful \n" +
                "Pinch of ground cardamom\t1\tpinch  \n" +
                "250ml/9fl oz cider or white wine\t250\tml  \n" +
                "A good handful of fresh basil leaves, torn into small pieces\t1\thandful \n" +
                "handful chives, finely chopped\t1\thandful \n" +
                "1 pound boneless beef sirloin steak (about 1 inch thick)\t1\tpound \n" +
                "3 chipotle chilies, canned in adobo\t3\t  \n" +
                "1/2 pound broccoli, sliced\t0.5\tpound \n" +
                "4 4-ounce pieces skin-on hake or cod fillet\t4\t4-ounce pieces \n" +
                "2 sweet potatoes, cleaned\t2\t  \n" +
                "1 (3.5 ounce) package microwave popcorn\t1\tpackage \n" +
                "2 black olives\t2\t  \n" +
                "2 cups whole milk, or more as needed\t2\tcups \n" +
                "1/4 cup Chicken Stock\t0.25\tcup \n" +
                "1/4 red onion, thinly sliced\t0.25\t  \n" +
                "2 celery stalks, finely chopped\t2\t  \n" +
                "1/4 pound cooked ham, cut into one inch cubes\t0.25\t pound \n" +
                "1/2 teaspoon ground black pepper, divided\t0.5\tteaspoon \n" +
                "1 large carrots, cut into thirds\t1\t \n" +
                "4 pounds beef short ribs, cut into 2-inch lengths\t4\tpounds \n" +
                "2 heads cabbage, finely shredded\t2\t  \n" +
                "1 (12 fluid ounce) can frozen berry juice concentrate, thawed\t1\t can \n" +
                "1/8 teaspoon seafood seasoning (such as Old Bay®), or to taste\t0.125\t teaspoon \n" +
                "1 carrot chopped fine\t1\t  \n" +
                "5 Roma tomatoes\t5\t  \n" +
                "1/2 pound fresh shiitake mushrooms, stems discarded and caps cut into 1/2-inch slices (4 cups)\t0.5\tpound  \n" +
                "1 cup MIRACLE WHIP Dressing\t1\tcup \n" +
                "1 teaspoon adobo sauce from canned chipotle chiles or more of taste (see Note)\t1\tteaspoon \n" +
                "2 teaspoon ground cinnamon\t2\tteaspoon \n" +
                "1¼ cups whole wheat pastry flour (not bread flour), plus more if needed\t1.25\tcups \n" +
                "2 tablespoon unsalted butter, melted\t2\ttablespoon \n" +
                "4 (6- to 8-ounce) skinless, boneless halibut fillets\t4\t  \n" +
                "10 baby courgettes, finely sliced\t10\t  \n" +
                "1/2 pound kale, stems trimmed, large ribs removed\t0.5\tpound \n" +
                "2 c. all-purpose flour\t2\tc. \n" +
                "1 anchovy fillet, mashed to a paste\t1\t  \n" +
                "1 medium celeriac\t1\t  \n" +
                "2 slices Canadian bacon\t2\tslices \n" +
                "4 1/2-ounce cans vegetable broth\t4\tcans \n" +
                "4 unwaxed lemons\t4\t  \n" +
                "1/3 cup whole pitted black or kalamata olives\t0.3333333\tcup \n" +
                "1 tbsp fresh marjoram leaves\t1\ttbsp \n" +
                "2 cups light soy sauce\t2\tcups \n" +
                "1/2 cup whipping cream, whipped\t0.5\tcup \n" +
                "1/4 cup GENERAL FOODS INTERNATIONAL Sugar Free French Vanilla Cafe\t0.25\tcup \n" +
                "1 (8 ounce) package frozen sugar snap peas, defrosted\t1\tpackage \n" +
                "250g/9oz smooth peanut butter\t250\tg \n" +
                "12 ounces arugula (about 16 cups), tough stems removed\t12\tounces \n" +
                "4 pork fillets\t4\t  \n" +
                "2 1/2 cups rice-based gluten-free all-purpose flour*\t2.5\tcups \n" +
                "3 1/2 cups walnuts, chopped\t3.5\tcups \n" +
                "2 tablespoons finely chopped dry-roasted peanuts\t2\ttablespoons \n" +
                "1 1/2 cups crumbled soft fresh goat cheese (such as Montrachet; about 7 ounces)\t1.5\tcups \n" +
                "2 very ripe (brown to black) plantains\t2\t  \n" +
                "30g/1oz dried 'instant noodles', seasoning sachet discarded\t30\tg \n" +
                "1 orange, juice and zest only\t1\t  \n" +
                "2 pounds boneless pork loin roast, cut into 1-inch cubes\t2\tpounds \n" +
                "50g/1¾oz demerara sugar\t50\tg \n" +
                "2 ounces cream cheese, room temperature\t2\tounces \n" +
                "1 heaping tablespoon freshly ground black pepper\t1\theaping tablespoon \n" +
                "2/3 cup solid vegetable shortening, melted, cooled\t0.6666667\tcup \n" +
                "1 1/2 teaspoons seeded, minced jalapeño\t1.5\tteaspoons \n" +
                "1/2 cup Bertolli® Extra Virgin Olive Oil\t0.5\tcup \n" +
                "2 large roasting potatoes, cut into cubes\t2\t  \n" +
                "1 1/2 teaspoons flaky sea salt\t1.5\tteaspoons  \n" +
                "150g/5½oz canned ackee, drained\t150\tg \n" +
                "1 cup chopped Napa cabbage\t1\tcup \n" +
                "1 1/2 cups light soy sauce\t1.5\tcups \n" +
                "2 cups refried beans\t2\tcups \n" +
                "1 qt. oil for frying\t1\tqt. \n" +
                "Pink rose petals\t1\t  \n" +
                "4 ounces pecorino cheese\t4\tounces \n" +
                "1 (18 ounce) bottle barbeque sauce (such as Montgomery Inn)\t1\tbottle \n" +
                "2 free-range eggs, separated\t2\t ";
        String listForRecipe = "";
        List<String> list = Arrays.asList(testIngredients.split("\n"));
        int index = 0;
        for (String s : list) {
            String[] split = s.split("\t");
            listForRecipe += split[0] + "\n";
            testIngredientsQuantities[index] = Double.parseDouble(split[1]);
            testIngredientsUnits[index] = split[2].trim();
            testIngredientsList.add(s);
            index++;
        }

        testRecipe = new RecipeInProgress(originalText);
        testRecipe.setIngredientsString(listForRecipe);
        testDetector = new DetectIngredientsInListTask(testRecipe, null);
        testIngredientsInitialized = true;


    }


}
