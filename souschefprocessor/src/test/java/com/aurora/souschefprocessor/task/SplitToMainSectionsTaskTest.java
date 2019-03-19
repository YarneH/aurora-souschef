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

    private static RecipeInProgress recipe3;
    private static SplitToMainSectionsTask splitToMainSectionsTask3;
    private static String originalText3;


    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {
        String [] recipeTexts = initializeRecipeText();
        originalText1 = recipeTexts[0];
        recipe1 = new RecipeInProgress(originalText1);
        originalText2 = recipeTexts[1];
        recipe2 = new RecipeInProgress(originalText2);
        splitToMainSectionsTask1 = new SplitToMainSectionsTask(recipe1);
        splitToMainSectionsTask2 = new SplitToMainSectionsTask(recipe2);
        originalText3 = recipeTexts[2];
        recipe3 = new RecipeInProgress(originalText3);
        splitToMainSectionsTask3 = new SplitToMainSectionsTask(recipe3);
    }

    private static String[] initializeRecipeText() {
        String [] array = new String[4];
        array[0] = "crostini with smoked salmon & sour cream\n" +
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
                "cream and sprinkle with a few capers and lots of freshly ground black pepper." +
                "\n\n\n" ;
        array[1] = "Yield\n" +
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
                "        Divide pasta among plates. Top with fried capers. ";
        array[2]=
                "Yield 4–6 servings\n" +
                "\n" +
                "Ingredients\n" +
                "\n" +
                "        Salt\n" +
                "        1 pound fettuccine\n" +
                "        4 tablespoons extra-virgin olive oil\n" +
                "        3 or 4 garlic cloves, finely chopped or grated\n" +
                "        Zest of 1 to 2 lemons\n" +
                "        2 medium- large or 4 small zucchini, cleaned but not peeled, and shredded\n" +
                "        Freshly ground pepper\n" +
                "        1/4 cup chopped fresh flat-leaf parsley\n" +
                "        1/4 cup chopped fresh mint\n" +
                "        1 cup fresh whole-milk ricotta cheese, at room temperature\n" +
                "\n" +
                "Cooking steps\n" +
                "\n" +
                "        Place a large pot of water over high heat. When the water is at a rolling boil, add a big pinch of salt, drop in the fettucine, and stir. Cook the pasta, stirring from time to time, according to package directions for al dente, usually about 12 minutes. Meanwhile, heat the olive oil in a large skillet over medium heat. When the oil is warm, add the garlic and sauté until golden, about 1 minute. Add the lemon zest and cook for 30 seconds longer. Increase the heat to medium-high, add the zucchini, and cook, stirring, until tender, 2 to 3 minutes. Season with salt and pepper.\n" +
                "        Remove and reserve about 1/2 cup of the cooking water, then drain the pasta and quickly toss with the zucchini, parsley, and mint. Spoon on the ricotta and toss lightly again, add small amounts of the cooking water to lighten the cheese to the consistency you like, and serve. \n" +
                "\n" +
                "Cooks' Note\n" +
                "Zucchini is easy to shred on the large holes of a box grater, with the shredding attachment of a food processor, or with a mandoline.";
        array[3] = "Dr. Jennifer’s Virgin Diet Recipes\n" +
                "www.thevirgindiet.com\n" +
                "\n" +
                "Gluten-Free Flour Tortillas\n" +
                "\n" +
                "Introduction\n" +
                "Minutes to Prepare: 15\n" +
                "Minutes to Cook: 35\n" +
                "Number of Servings: 8\n" +
                "\n" +
                "\n" +
                "Ingredients\n" +
                "You will need:\n" +
                "\n" +
                "2 c. Gluten-free all purpose flour (or 2 c. White rice flour)\n" +
                "2 tsp. Xanthan gum or Guar gum\n" +
                "1 tsp. Gluten-free baking powder\n" +
                "2 tsp. Brown sugar \n" +
                "1 tsp. Salt\n" +
                "1 c. Warm water\n" +
                " Top-Rated Gluten-Free Flour at AmazonTop-Rated Gluten-Free Flour\n" +
                "\n" +
                "\n" +
                "Directions\n" +
                "1) Add the dry ingredients to a large mixing bowl and mix the ingredients thoroughly. \n" +
                "\n" +
                "2) Add the cup of warm water to the bowl and mix the dry ingredients into the water with your hand until its an even mixture.\n" +
                "\n" +
                "3) Separate the dough into 8 pieces and roll each piece into a ball. Place all but one of the dough balls back into the bowl and cover with plastic wrap until you're ready to work with them.\n" +
                "\n" +
                "4) Sprinkle a clean, flat surface with a bit of buckwheat flour and then roll your dough ball into a roughly circular shape and get as thin as possible.\n" +
                "\n" +
                "5) Throw the tortilla onto a hot griddle (I use a cast iron griddle on medium heat with just a smidge of olive oil) and let it cook approximately 1-2 minutes or until it has started puffing up and the bottom side is developing those lovely brown spots. Flip the tortilla and cook the other side until is toasty as well.\n" +
                "\n" +
                "6) Slide the cooked tortilla onto a covered plate to stay warm and repeat from step 4 until you've cooked all 8 tortillas. I generally roll one tortilla out while another is cooking, so that there is always a tortilla on the griddle.\n" +
                "\n" +
                "7) Serve these warm with your favorite filling! \n";
        return array;
    }

    @After
    public void wipeRecipe() {
        recipe1.setIngredients(null);
        recipe1.setDescription(null);
        recipe1.setRecipeSteps(null);
    }

  /*  @Test
    public void SplitToMainSections_doTask_sectionsAreSet() {
        splitToMainSectionsTask1.doTask();
        splitToMainSectionsTask2.doTask();
        System.out.println("\n\n\n\n Third task: ");
        splitToMainSectionsTask3.doTask();
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
    }*/

  @Test
    public void testing_main(){
      String[] array = initializeRecipeText();
      String text = array[3];
      //for(String text: array){
          RecipeInProgress rip = new RecipeInProgress(text);
          SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
          task.doTask();
          System.out.println(rip + "\n---------------------------");
      //}


  }
}