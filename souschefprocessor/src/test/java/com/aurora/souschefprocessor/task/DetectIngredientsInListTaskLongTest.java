package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


public class DetectIngredientsInListTaskLongTest {

    private static RecipeInProgress testRecipe;
    private static DetectIngredientsInListTask testDetector;
    private static String originalText = "irrelevant";
    private static CRFClassifier<CoreLabel> crfClassifier;

    private static String testIngredients;
    private static String[] testIngredientsUnits;
    private static double[] testIngredientsQuantities;
    private static boolean testIngredientsInitialized = false;
    private static ArrayList<String> testIngredientsList = new ArrayList<>();


    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {
        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        crfClassifier = CRFClassifier.getClassifier(modelName);
    }


    /**
     * A function to check if the strings only differ in one character
     *
     * @param a
     * @param b
     * @return
     */
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
            System.out.println("ONE CHAR OFF: " + a + " " + b);
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
            System.out.println("ONE CHAR OFF: " + a + " " + b);
            return true;
        }
        //should not get here
        return false;
    }


    /**
     * Set the testingredients to this database
     */
    private void initializeTestIngredients() {
        testIngredients = "1 quart fresh dark cherries\t1\tquart  \n" +
                "2 Tender Pie Crust dough disks\t2\t  \n" +
                "cracked black pepper\t1\t   \n" +
                "12 fresh Padron chile peppers\t12\t   \n" +
                "18 ounces bittersweet chocolate (62 to 72% cacao content), finely chopped, divided\t18\tounce  \n" +
                "3 green onions, thinly sliced\t3\t   \n" +
                "4 pounds large boiling potatoes (preferably white-fleshed)\t4\tpound  \n" +
                "2 (14.5 ounce) cans CONTADINA® Diced Tomatoes, undrained\t2\t-LRB- 14.5 ounce -RRB- cans  \n" +
                "1 pound tri-colored spiral pasta\t1\tpound  \n" +
                "1/4 teaspoon mace\t0.25\tteaspoon  \n" +
                "Orange wedge\t1\t   \n" +
                "1 medium summer squash, halved and sliced\t1\t   \n" +
                "4 teaspoons crispy fried shallots\t4\tteaspoon   \n" +
                "Oblong casserole dish (about 9 by 13 inches)\t1\t   \n" +
                "4 celery ribs from the heart with leaves, finely chopped\t4\t   \n" +
                "3/4 teaspoon anchovy paste or mashed anchovy fillet\t0.75\t teaspoon  \n" +
                "1 lamb breast\t1\t   \n" +
                "3 hearts of romaine, split lengthwise, cores removed, and coarsely chopped\t3\t   \n" +
                "1 2-inch piece fresh ginger, sliced\t1\t2-inch piece  \n" +
                "BUTTER CRUNCH CRUST:\t1\t   \n" +
                "2 large onions, unpeeled, quartered\t2\t   \n" +
                "2 cups fresh peaches - peeled, pitted, and cut into bite-size pieces\t2\tcup   \n" +
                "1/4 cup brown mustard seeds\t0.25\tcup  \n" +
                "2 banana peppers, thinly sliced\t2\t   \n" +
                "3 cloves garlic, peeled and minced or pressed\t3\tcloves  \n" +
                "1/4 cup diced dill pickles\t0.25\tcup   \n" +
                "1 handful fresh baby arugula\t1\t handful  \n" +
                "Pinch of ground cardamom\t1\tpinch   \n" +
                "250ml/9fl oz cider or white wine\t250\tmilliliter   \n" +
                "A good handful of fresh basil leaves, torn into small pieces\t1\tgood handful  \n" +
                "handful chives, finely chopped\t1\thandful  \n" +
                "1 pound boneless beef sirloin steak (about 1 inch thick)\t1\tpound  \n" +
                "3 chipotle chilies, canned in adobo\t3\t   \n" +
                "1/2 pound broccoli, sliced\t0.5\tpound  \n" +
                "4 4-ounce pieces skin-on hake or cod fillet\t4\t4-ounce pieces  \n" +
                "2 sweet potatoes, cleaned\t2\t   \n" +
                "1 (3.5 ounce) package microwave popcorn\t1\tpackage  \n" +
                "2 black olives\t2\t   \n" +
                "2 cups whole milk, or more as needed\t2\tcup  \n" +
                "1/4 cup Chicken Stock\t0.25\tcup  \n" +
                "1/4 red onion, thinly sliced\t0.25\t   \n" +
                "2 celery stalks, finely chopped\t2\t   \n" +
                "1/4 pound cooked ham, cut into one inch cubes\t0.25\t pound  \n" +
                "1/2 teaspoon ground black pepper, divided\t0.5\tteaspoon  \n" +
                "1 large carrots, cut into thirds\t1\t  \n" +
                "4 pounds beef short ribs, cut into 2-inch lengths\t4\tpound  \n" +
                "2 heads cabbage, finely shredded\t2\t   \n" +
                "1 (12 fluid ounce) can frozen berry juice concentrate, thawed\t1\t-LRB- 12 fluid ounce -RRB- can  \n" +
                "1/8 teaspoon seafood seasoning (such as Old Bay®), or to taste\t0.125\t teaspoon  \n" +
                "1 carrot chopped fine\t1\t   \n" +
                "5 Roma tomatoes\t5\t   \n" +
                "1/2 pound fresh shiitake mushrooms, stems discarded and caps cut into 1/2-inch slices (4 cups)\t0.5\tpound   \n" +
                "1 cup MIRACLE WHIP Dressing\t1\tcup  \n" +
                "1 teaspoon adobo sauce from canned chipotle chiles or more of taste (see Note)\t1\tteaspoon  \n" +
                "2 teaspoon ground cinnamon\t2\tteaspoon  \n" +
                "1¼ cups whole wheat pastry flour (not bread flour), plus more if needed\t1.25\tcup  \n" +
                "2 tablespoon unsalted butter, melted\t2\ttablespoon  \n" +
                "4 (6- to 8-ounce) skinless, boneless halibut fillets\t4\t   \n" +
                "10 baby courgettes, finely sliced\t10\t   \n" +
                "1/2 pound kale, stems trimmed, large ribs removed\t0.5\tpound  \n" +
                "2 c. all-purpose flour\t2\tcup  \n" +
                "1 anchovy fillet, mashed to a paste\t1\t   \n" +
                "1 medium celeriac\t1\t   \n" +
                "2 slices Canadian bacon\t2\tslices  \n" +
                "4 1/2-ounce cans vegetable broth\t4\tcans  \n" +
                "4 unwaxed lemons\t4\t   \n" +
                "1/3 cup whole pitted black or kalamata olives\t0.3333333\tcup  \n" +
                "1 tbsp fresh marjoram leaves\t1\ttablespoon  \n" +
                "2 cups light soy sauce\t2\tcup \n" +
                "1/2 cup whipping cream, whipped\t0.5\tcup  \n" +
                "1/4 cup GENERAL FOODS INTERNATIONAL Sugar Free French Vanilla Cafe\t0.25\tcup  \n" +
                "1 (8 ounce) package frozen sugar snap peas, defrosted\t1\tpackage  \n" +
                "250g/9oz smooth peanut butter\t250\tgram  \n" +
                "12 ounces arugula (about 16 cups), tough stems removed\t12\tounce \n" +
                "4 pork fillets\t4\t   \n" +
                "2 1/2 cups rice-based gluten-free all-purpose flour*\t2.5\tcup  \n" +
                "3 1/2 cups walnuts, chopped\t3.5\tcup  \n" +
                "2 tablespoons finely chopped dry-roasted peanuts\t2\ttablespoon  \n" +
                "1 1/2 cups crumbled soft fresh goat cheese (such as Montrachet; about 7 ounces)\t1.5\tcup  \n" +
                "2 very ripe (brown to black) plantains\t2\t   \n" +
                "30g/1oz dried 'instant noodles', seasoning sachet discarded\t30\tgram  \n" +
                "1 orange, juice and zest only\t1\t   \n" +
                "2 pounds boneless pork loin roast, cut into 1-inch cubes\t2\tpound  \n" +
                "50g/1¾oz demerara sugar\t50\tgram  \n" +
                "2 ounces cream cheese, room temperature\t2\tounce  \n" +
                "1 heaping tablespoon freshly ground black pepper\t1\theaping tablespoon  \n" +
                "2/3 cup solid vegetable shortening, melted, cooled\t0.6666667\tcup  \n" +
                "1 1/2 teaspoons seeded, minced jalapeño\t1.5\tteaspoon  \n" +
                "1/2 cup Bertolli® Extra Virgin Olive Oil\t0.5\tcup  \n" +
                "2 large roasting potatoes, cut into cubes\t2\t   \n" +
                "1 1/2 teaspoons flaky sea salt\t1.5\tteaspoon \n" +
                "150g/5½oz canned ackee, drained\t150\tgram  \n" +
                "1 cup chopped Napa cabbage\t1\tcup  \n" +
                "1 1/2 cups light soy sauce\t1.5\tcup  \n" +
                "2 cups refried beans\t2\tcup \n" +
                "1 qt. oil for frying\t1\tquart  \n" +
                "Pink rose petals\t1\t   \n" +
                "4 ounces pecorino cheese\t4\tounce  \n" +
                "1 (18 ounce) bottle barbeque sauce (such as Montgomery Inn)\t1\tbottle  \n" +
                "2 free-range eggs, separated\t2\t  \n" +
                "8–12 chipolata sausages, wrapped in bacon\t8\t  \n" +
                "2.5kg/5lb 8oz turkey crown (fully thawed if frozen)\t2.5\tkilogram \n" +
                "2 x 80g/3oz packs Parma ham, snipped into small pieces\t160\tgram \n" +
                "salt and pepper, to taste\t1\t  \n" +
                "food colouring, if using\t1\t  \n" +
                "7 cups whole wheat flour\t7\tcup \n" +
                "1/4 cup cracked wheat\t0.25\tcup \n" +
                "1 x 120g pack mixed nuts and dried fruit (I use one containing brazil nuts, pecans, almonds, sultanas and dried cranberries)\t120\tgram \n" +
                "750–900ml/1⅓–1⅔ pint readymade chicken gravy\t750\tmilliliter \n" +
                "500ml/18fl oz milk\t500\tmilliliter \n" +
                "freshly grated nutmeg, to serve (optional)\t1\t  \n" +
                "200ml/7fl oz crème frâiche\t200\tmilliliter \n" +
                "1 x 7g sachet easy-blend dried yeast\t7\tgram \n" +
                "350ml/12¼fl oz warm water\t350\tmilliliter \n" +
                "1 purple or yellow swede, peeled and sliced into 1cm/½in cubes\t1\t  \n" +
                "sea salt and freshly ground white pepper\t1\t  \n" +
                "3 large eggs at room temperature\t3\t  \n" +
                "a little sifted icing sugar, for dusting\t1\t  \n" +
                "200ml/7fl oz fromage frais\t200\tmilliliter \n" +
                "1 jar (340g/12oz) lemon curd\t1\tjar \n" +
                "1 small handful shelled unsalted pistachio nuts\t1\tsmall handful \n" +
                "One 10-inch-long beef tenderloin roast cut from the heart of the tenderloin (2½ to 3 pounds), butterflied (see Note)\t1\t10-inch-long \n" +
                "Salt\t1\t  \n" +
                "One 28-ounce can diced tomatoes in juice (preferably fire-roasted)\t1\t28-ounce can \n" +
                "1 to 2 canned chipotle chiles en adobo, stemmed and seeded\t1\t  \n" +
                "1 cup warm (105° to 115°F) whole milk (3.5%)\t1\tcup \n" +
                "1 pound conch meat (from 3 large or 4 medium conch)\t1\tpound \n" +
                "Juice of 1 small juicy orange\t1\t  \n" +
                "Juice of 1 juicy lime\t1\t  \n" +
                "Juice of ½ juicy lemon\t1\t  \n" +
                "1 small onion (4 ounces), cut into 1/4 –inch dice\t1\t  \n" +
                "1 small tomato, cut into small dice\t1\t  \n" +
                "1 (2-inch) piece ginger, peeled\t1\t-LRB 2-inch -RRB- piece \n" +
                "8 skinless, boneless chicken thighs (about 3 pounds), halved, quartered if large\t8\t  \n" +
                "1 1/2 cups granulated sugar\t1.5\tcup  \n" +
                "1/2 cup brown sugar\t0.5\tcup \n" +
                "1/2 cup slivered or sliced almonds (optional)\t0.50\tcup  \n" +
                "Orange slices for garnish\t1\t  \n" +
                "Assorted eggcups and salt wells, for serving\t1\t  \n" +
                "Waxed or parchment paper\t1\t  \n" +
                "Cake plate or 4-inch round cardboard base (optional)\t1\t  \n" +
                "Small plate\t1\t  \n" +
                "10-inch cake plate, for serving\t1\t  \n" +
                "Small, sharp knife\t1\t  \n" +
                "1/4 teaspoon cream of tartar or 2 teaspoons light-colored corn syrup\t0.2500\tteaspoon   \n" +
                "One purchased 9-inch angel food cake\t1\t ";
        String listForRecipe = "";
        List<String> list = Arrays.asList(testIngredients.split("\n"));
        System.out.println(list.size());
        int total = list.size();
        testIngredientsQuantities = new double[total];
        testIngredientsUnits = new String[total];
        int index = 0;
        for (String s : list) {
            String[] split = s.split("\t");
            listForRecipe += split[0] + "\n";
            testIngredientsQuantities[index] = Double.parseDouble(split[1]);
            testIngredientsUnits[index] = split[2].trim();
            testIngredientsList.add(s);
            index++;
        }

        testRecipe = new RecipeInProgress(null);
        testRecipe.setIngredientsString(listForRecipe);
        testDetector = new DetectIngredientsInListTask(testRecipe, crfClassifier);
        testIngredientsInitialized = true;


    }

    @Test
    public void DetectIngredientsInListTask_doTask_AccuracyForQuantityThreshold() {
        /**
         * The accuracy of the quantities detected of ingredients is at least 95%
         */
        // Arrange
        if (!testIngredientsInitialized) {
            initializeTestIngredients();

        }
        testDetector.doTask();

        int correct = 0;
        List<ListIngredient> list = testRecipe.getIngredients();

        // Act
        for (int i = 0; i < testIngredientsUnits.length; i++) {
            // check if they are equal up to 3 decimal places
            System.out.println(list.get(i));
            list.get(i).getQuantity();
            if ((int) (1000 * list.get(i).getQuantity()) == (int) (1000 * testIngredientsQuantities[i])) {
                correct++;
            } else {
                System.out.println(testIngredientsQuantities[i] + " " + list.get(i).getQuantity());
                System.out.println(testIngredientsList.get(i));
            }
        }
        // Assert
        System.out.println("Correct: " + correct * 100.0 / testIngredientsQuantities.length + "%");
        assert (correct * 100.0 / testIngredientsUnits.length >= 95);
    }


    @Test
    public void DetectIngredientsInListTask_doTask_AccuracyForUnitThreshold() {
        /**
         * The accuracy for the unit should be higher than 80% and at most 5% can differ in one
         * character
         */
        // Arrange
        if (!testIngredientsInitialized) {
            initializeTestIngredients();

        }
        testDetector.doTask();

        int correct = 0;
        int correctButOneCharOff = 0;
        List<ListIngredient> list = testRecipe.getIngredients();
        // Act
        for (int i = 0; i < testIngredientsUnits.length; i++) {
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

        // Assert
        double multiplier = 100.0 / testIngredientsUnits.length;
        assert ((correct + correctButOneCharOff) * multiplier >= 85);
        assert (correctButOneCharOff * multiplier < 5);
        System.out.println(correct + " units were correctly set and " + correctButOneCharOff + " were correct with one char off out of " + testIngredientsQuantities.length + " examples");
    }
}
