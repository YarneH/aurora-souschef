package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class SplitToMainSectionsTaskTest {
    private static RecipeInProgress recipe1;
    private static SplitToMainSectionsTask splitToMainSectionsTask1;
    private static SplitToMainSectionsTask splitToMainSectionsTask2;
    private static String originalText1;
    private static String originalText2;
    private static RecipeInProgress recipe2;


    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {
        String [] recipeTexts = initializeRecipeText();
        originalText1 = recipeTexts[0];
        recipe1 = new RecipeInProgress(originalText1);
        originalText2 = recipeTexts[1];
        recipe2 = new RecipeInProgress(originalText2);
        splitToMainSectionsTask1 = new SplitToMainSectionsTask(recipe1);
        splitToMainSectionsTask2 = new SplitToMainSectionsTask(recipe2);
    }

    private static String[] initializeRecipeText() {
        String [] array = {"crostini with smoked salmon & sour cream\n" +
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
                , "Yield\n" +
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
                "        Divide pasta among plates. Top with fried capers. "};
        return array;
    }

    @After
    public void wipeRecipe() {
        recipe1.setIngredients(null);
        recipe1.setDescription(null);
        recipe1.setRecipeSteps(null);
    }

    @Test
    public void SplitToMainSections_doTask_sectionsAreSet() {
        splitToMainSectionsTask1.doTask();
        assert (recipe1.getStepsString() != null);
        assert (recipe1.getIngredientsString() != null);
        assert (recipe1.getDescription() != null);
    }

    @Test
    public void SplitToMainSections_doTask_sectionsHaveCorrectValues() {
        splitToMainSectionsTask1.doTask();
        assert (recipe1.getDescription().equals("crostini with smoked salmon & sour cream"));
        assert (recipe1.getStepsString().equals("Toast baguette slices lightly on one side. " +
                "Layer each round with smoked salmon, top with a dollup of sour \n" +
                "cream and sprinkle with a few capers and lots of freshly ground black pepper."));
        assert (recipe1.getIngredientsString().equals("8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve"));
    }
}