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
                "cream and sprinkle with a few capers and lots of freshly ground black pepper.\n\n\n"
        );
    }

    private static String[] initializeDataSetTags() {
        return ("NO_NUMBER\n" +
                "NUMBER\n" +
                "NUMBER\n").split("\n");
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
                "hot.\n\n\n").split("\n\n\n");
    }

    @After
    public void wipeRecipe() {
        recipe.setNumberOfPeople(0);
    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_valueHasBeenRead() {
        detectNumberOfPeopleTask.doTask();
        assert (recipe.getNumberOfPeople() == DEFAULT_NUMBER);
    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_noNumberOfPeople() {
        String originalTextNoNumber = originalText.substring(originalText.indexOf('\n') + 1);
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
            } else if (recipeTag.equals("NUMBER")) {
                if (recipe.getNumberOfPeople() <= 0) {
                    correct--;
                }
            }
        }

        System.out.println(correct + " correct out of " + amount + " tested. Accuracy: " + correct * 100.0 / amount);
        assert (correct * 100.0 / amount > 95);
    }

}
