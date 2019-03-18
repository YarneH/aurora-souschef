package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.task.sectiondivider.DetectNumberOfPeopleTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

//TODO add more testing data to increase acceptance test reliability
public class DetectNumberOfPeopleTaskTest {
    private static final int DEFAULT_NUMBER = 4;
    private static final int DEFAULT_NO_NUMBER = -1;
    private static RecipeInProgress recipe;
    private static DetectNumberOfPeopleTask detectNumberOfPeopleTask;
    private static String originalText;

    @BeforeClass
    public static void initialize() {
        originalText = initializeRecipeText();
        recipe = new RecipeInProgress(originalText);
        detectNumberOfPeopleTask = new DetectNumberOfPeopleTask(recipe);
    }

    private static String initializeRecipeText() {
        return ("crostini with smoked salmon & sour cream\n" +
                "serves 1\n\n" +
                "This is one of those effortless starters that feels a little bit special \n" +
                "but can be made in a flash from ingredients from your supermarket.\n\n" +
                "If  you  don't  have  access  to  capers,  chopped  chives  or  parsley  would work well.\n" +
                "It's more about getting some visual greenery and freshness.\n\n" +
                "Baguettes are lovely for crostini but I've also used crackers or larger slices of sourdough cut into small,\n" +
                "bite sized pieces.\n\n" +
                "8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve\n\n" +
                "Toast baguette slices lightly on one side. Layer each round with smoked salmon, top with a dollup of sour \n" +
                "cream and sprinkle with a few capers and lots of freshly ground black pepper.\n\n\n" +
                ""
        );
    }

    private static String[] initializeDataSetTags() {
        return ("NO_NUMBER\n" +
                "NUMBER\t2\n" +
                "NUMBER\t1\n" +
                "NUMBER\t4\n").split("\n");
    }

    private static String[] initializeDataSet() {
        return ("crostini with smoked salmon & sour cream\n" +
                "This is one of those effortless starters that feels a little bit special \n" +
                "but can be made in a flash from ingredients from your supermarket.\n\n" +
                "If  you  don't  have  access  to  capers,  chopped  chives  or  parsley  would work well.\n" +
                "It's more about getting some visual greenery and freshness.\n\n" +
                "Baguettes are lovely for crostini but I've also used crackers or larger slices of sourdough cut into small,\n" +
                "bite sized pieces.\n\n" +
                "8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve\n\n" +
                "Toast baguette slices lightly on one side. Layer each round with smoked salmon, top with a dollup of sour \n" +
                "cream and sprinkle with a few capers and lots of freshly ground black pepper.\n\n\n" +
                "cheese on toast\n" +
                "serves 2\n" +
                "The choice of cheese is in your court. \n" +
                "The mayo makes it a bit moister so you could easily use hard cheese such as parmesan or manchengo. \n" +
                "For me right now, Irish cheddar is where it's at.\n" +
                "Also lovely with a slice or two of smoked ham layered under the cheese. \n" +
                "For an even more rich experience replace the mayo with an egg yolk.\n\n" +
                "2 slices rustic bread\n" +
                "butter\n" +
                "2 handfuls grated cheese\n" +
                "1 teaspoon wholegrain mustard\n" +
                "1 tablespoon whole egg mayonnaise\n\n" +
                "Preheat oven to 250C (480F).\n" +
                "Generously butter bread and place on a baking tray lined with foil or \n" +
                "baking paper. Bake for 3 minutes or until butter is melted.\n\n" +
                "Combine cheese, mustard and mayo. Completely cover the bread \n" +
                "with the cheese mixture. \n\n" +
                "Bake for another 5 minutes or until the cheese is melted and \n" +
                "bubbling with golden brown patches.\n\n\n" +
                "simple soba noodle soup\n" +
                "serves 1\n" +
                "Soba  noodles  are  made  of  buckwheat  as  well  as regular wheat and \n" +
                "have a subtle healthy flavour. Most other noodles could be used here if you \n" +
                "prefer. Likewise, the veg can be varied to suit your taste (and what \n" +
                "you have in the fridge!) baby spinach would be lovely. \n\n" +
                "Remember that the noodles are going to keep cooking in the broth after \n" +
                "you've served up so best to slightly undercook first.\n\n" +
                "1 1/2 cups vegetable stockhandful soba noodles (approx 50g or 2oz) 3 heads baby bok choy, \n" +
                "leaves separated large pinch chilli flakes, \n" +
                "optional 1/2 tablespoons soy sauce \n\n" +
                "Bring stock to the boil in a medium saucepan. Add noodles and \n" +
                "simmer for 2 minutes. \n\n" +
                "Add bok choy and chilli and 1T soy sauce and simmer for another \n" +
                "minute or until noodles are only just cooked (see note above).\n\n" +
                "Remove from the heat. Taste and add extra soy if needed. Serve \n" +
                "hot.\n\n\n" +
                "Yield\n" +
                "    4 servings\n" +
                "Active Time\n" +
                "    30 minutes\n" +
                "Total Time\n" +
                "    35 minutes\n" +
                "\n" +
                "Ingredients\n" +
                "\n" +
                "        1 lb. linguine or other long pasta\n" +
                "        Kosher salt\n" +
                "        1 (14-oz.) can diced tomatoes\n" +
                "        1/2 cup extra-virgin olive oil, divided\n" +
                "        1/4 cup capers, drained\n" +
                "        6 oil-packed anchovy fillets\n" +
                "        1 Tbsp. tomato paste\n" +
                "        1/3 cup pitted Kalamata olives, halved\n" +
                "        2 tsp. dried oregano\n" +
                "        1/2 tsp. crushed red pepper flakes\n" +
                "        6 oz. oil-packed tuna\n" +
                "\n" +
                "Preparation\n" +
                "\n" +
                "        Cook pasta in a large pot of boiling salted water, stirring occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking liquid; return pasta to pot.\n" +
                "        While pasta cooks, pour tomatoes into a fine-mesh sieve set over a medium bowl. Shake to release as much juice as possible, then let tomatoes drain in sieve, collecting juices in bowl, until ready to use.\n" +
                "        Heat 1/4 cup oil in a large deep-sided skillet over medium-high. Add capers and cook, swirling pan occasionally, until they burst and are crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper towel–lined plate, reserving oil in skillet.\n" +
                "        Combine anchovies, tomato paste, and drained tomatoes in skillet. Cook over medium-high heat, stirring occasionally, until tomatoes begin to caramelize and anchovies start to break down, about 5 minutes. Add collected tomato juices, olives, oregano, and red pepper flakes and bring to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta cooking liquid to pan. Cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\n" +
                "        Divide pasta among plates. Top with fried capers. \n\n\n").split("\n\n\n");
    }

    @After
    public void wipeRecipe() {
        recipe.setNumberOfPeople(0);
    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_valueHasBeenRead() {
        detectNumberOfPeopleTask.doTask();
        assert (recipe.getNumberOfPeople() == 1);
    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_noNumberOfPeople() {
        String originalTextNoNumber = originalText.substring(0,originalText.indexOf('\n') + 1);
        RecipeInProgress recipeNoNumber = new RecipeInProgress(originalTextNoNumber);
        DetectNumberOfPeopleTask detectNumberOfPeopleTask = new DetectNumberOfPeopleTask(recipeNoNumber);
        detectNumberOfPeopleTask.doTask();
        assert (recipeNoNumber.getNumberOfPeople() == DEFAULT_NO_NUMBER);
    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_acceptanceTest95PercentAccuracy() {
        String[] dataSet = initializeDataSet();
        String[] dataSetTags = initializeDataSetTags();
        int amount = dataSet.length;
        int correct = amount;

        for (int i = 1; i <= dataSet.length; i++) {
            String recipeText = dataSet[i - 1];
            String recipeTag = dataSetTags[i - 1];

            RecipeInProgress recipe = new RecipeInProgress(recipeText);
            DetectNumberOfPeopleTask detector = new DetectNumberOfPeopleTask(recipe);
            detector.doTask();

            if (recipeTag.equals("NO_NUMBER")) {
                if (recipe.getNumberOfPeople() != -1) {
                    correct--;
                }
            } else if (recipeTag.startsWith("NUMBER")) {
                String[] split = recipeTag.split("\t");
                String number = split[1];
                int num = Integer.parseInt(number);
                if (recipe.getNumberOfPeople() != num) {
                    correct--;
                }
            }
        }

        System.out.println(correct + " correct out of " + amount + " tested. Accuracy: " + correct * 100.0 / amount);
        assert (correct * 100.0 / amount > 95);
    }

}
