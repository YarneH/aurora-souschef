package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SplitToMainSectionsTaskUnitTest {
    private static List<String> recipeTexts;


    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {
        recipeTexts = initializeRecipeText();
    }

    private static List<String> initializeRecipeText() {

        String filename = "src/test/java/com/aurora/souschefprocessor/facade/recipes.txt";
        List<String> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), "UTF8"));

            StringBuilder bld = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                if (!line.equals("----------")) {
                    bld.append(line + "\n");
                } else {
                    list.add(bld.toString());
                    bld = new StringBuilder();

                }
                line = reader.readLine();
            }
            list.add(bld.toString());
        } catch (IOException io) {
            System.err.print(io);
        }

        return list;


    }

    private static List<Map<String, String>> initializeFieldList() {
        List<Map<String, String>> fieldsList = new ArrayList<>();
        //recipe 1
        Map<String, String> map = new HashMap<>();
        map.put("STEPS", "Toast baguette slices lightly on one side.\n" +
                "Layer each round with smoked salmon, top with a dollup of sour\n" +
                "cream and sprinkle with a few capers and lots of freshly ground black\npepper.");
        map.put("INGR", "8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve");
        fieldsList.add(map);

        // recipe 2
        map = new HashMap<>();
        map.put("STEPS", "cook pasta in a large pot of boiling salted water, stirring occasionally, until al dente. drain pasta, reserving 1 cup pasta cooking liquid; return pasta to pot.\n" +
                "while pasta cooks, pour tomatoes into a fine-mesh sieve set over a medium bowl. shake to release as much juice as possible, then let tomatoes drain in sieve, collecting juices in bowl, until ready to use.\n" +
                "heat 1/4 cup oil in a large deep-sided skillet over medium-high. add capers and cook, swirling pan occasionally, until they burst and are crisp, about 3 minutes. using a slotted spoon, transfer capers to a paper towel-lined plate, reserving oil in skillet.\n" +
                "combine anchovies, tomato paste, and drained tomatoes in skillet. cook over medium-high heat, stirring occasionally, until tomatoes begin to caramelize and anchovies start to break down, about 5 minutes. add collected tomato juices, olives, oregano, and red pepper flakes and bring to a simmer. cook, stirring occasionally, until sauce is slightly thickened, about 5 minutes. add pasta, remaining 1/4 cup oil, and 3/4 cup pasta cooking liquid to pan. cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened and emulsified, about 2 minutes. flake tuna into pasta and toss to combine.\n" +
                "divide pasta among plates. top with fried capers.");
        map.put("INGR", "1 lb. linguine or other long pasta\n" +
                "kosher salt\n" +
                "1 (14-oz.) can diced tomatoes\n" +
                "1/2 cup extra-virgin olive oil, divided\n" +
                "1/4 cup capers, drained\n" +
                "6 oil-packed anchovy fillets\n" +
                "1 tbsp. tomato paste\n" +
                "1/3 cup pitted kalamata olives, halved\n" +
                "2 tsp. dried oregano\n" +
                "1/2 tsp. crushed red pepper flakes\n" +
                "6 oz. oil-packed tuna");
        fieldsList.add(map);

        // recipe 3
        map = new HashMap<>();
        map.put("STEPS", "Brown ground meat, onions, and bell pepper. Drain and return to skillet or Dutch oven. Add salt, garlic salt, and black pepper. Add rice, salt, dry onion soup, and water. Cover and simmer for 20 minutes. Stir in soup and milk. Pour mixture into 8” x 8” baking dish. Top with crushed chips. Bake at 350F for 20 minutes.");
        map.put("INGR", "1 lb. lean ground beef\n" +
                "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper\n" +
                "1 c. rice\n" +
                "1 tsp. salt\n" +
                "1 Tbsp. of dry onion soup mix\n" +
                "2 c. water\n" +
                "1 10 oz. can cream of mushroom soup\n" +
                "1⁄2 c. low-fat milk\n" +
                "1 c. crushed potato chips");
        fieldsList.add(map);

        // recipe 4
        map = new HashMap<>();
        map.put("STEPS", "Place a large pot of water over high heat. When the water is at a rolling boil, add a big pinch of salt, drop in the fettucine, and stir. Cook the pasta, stirring from time to time, according to package directions for al dente, usually about 12 minutes. Meanwhile, heat the olive oil in a large skillet over medium heat. When the oil is warm, add the garlic and sauté until golden, about 1 minute. Add the lemon zest and cook for 30 seconds longer. Increase the heat to medium-high, add the zucchini, and cook, stirring, until tender, 2 to 3 minutes. Season with salt and pepper.\n" +
                "Remove and reserve about 1/2 cup of the cooking water, then drain the pasta and quickly toss with the zucchini, parsley, and mint. Spoon on the ricotta and toss lightly again, add small amounts of the cooking water to lighten the cheese to the consistency you like, and serve.\n" +
                "\n" +
                "Cooks' Note\n" +
                "Zucchini is easy to shred on the large holes of a box grater, with the shredding attachment of a food processor, or with a mandoline."
        );
        map.put("INGR", "" +
                "Salt\n" +
                "1 pound fettuccine\n" +
                "4 tablespoons extra-virgin olive oil\n" +
                "3 or 4 garlic cloves, finely chopped or grated\n" +
                "Zest of 1 to 2 lemons\n" +
                "2 medium- large or 4 small zucchini, cleaned but not peeled, and shredded\n" +
                "Freshly ground pepper\n" +
                "1/4 cup chopped fresh flat-leaf parsley\n" +
                "1/4 cup chopped fresh mint\n" +
                "1 cup fresh whole-milk ricotta cheese, at room temperature");
        fieldsList.add(map);


        // recipe 5
        map = new HashMap<>();
        map.put("STEPS", "1) Add the dry ingredients to a large mixing bowl and mix the ingredients thoroughly.\n" +
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
                "7) Serve these warm with your favorite filling!");
        map.put("INGR", "2 c. Gluten-free all purpose flour (or 2 c. White rice flour)\n" +
                "2 tsp. Xanthan gum or Guar gum\n" +
                "1 tsp. Gluten-free baking powder\n" +
                "2 tsp. Brown sugar\n" +
                "1 tsp. Salt\n" +
                "1 c. Warm water\n" +
                "Top-Rated Gluten-Free Flour at AmazonTop-Rated Gluten-Free Flour");
        fieldsList.add(map);
        return fieldsList;

    }

    @After
    public void wipeRecipe() {
    }

    @Test
    public void SplitToMainSections_doTask_sectionsAreSet() {
        /**
         * After the task the sections are not null
         */
        for (String text : recipeTexts) {
            // Arrange
            RecipeInProgress rip = new RecipeInProgress(text);
            SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
            // Act
            task.doTask();
            // Assert
            assert (rip.getStepsString() != null);
            assert (rip.getIngredientsString() != null);
            assert (rip.getDescription() != null);
        }
    }

    @Test
    public void SplitToMainSections_doTask_sectionsHaveCorrectValues() {
        /**
         * The correct sections are detected
         */
        // Arrange
        List<Map<String, String>> fieldsList = initializeFieldList();

        for (int i = 0; i < fieldsList.size(); i++) {
            // Arrange
            String text = recipeTexts.get(i);
            RecipeInProgress rip = new RecipeInProgress(text);
            SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
            // Act
            task.doTask();

            // Assert
            assert (rip.getIngredientsString().equalsIgnoreCase(fieldsList.get(i).get("INGR")));
            assert (rip.getStepsString().equalsIgnoreCase(fieldsList.get(i).get("STEPS")));
            assert (rip.getDescription() != null);
        }
    }

    @Test
    public void SplitToMainSectionsTaskTest_doTask_NoExceptionsAreThrown() {
        List<String> array = initializeRecipeText();
        boolean thrown = false;
        try {
            for (String text : array) {
                RecipeInProgress rip = new RecipeInProgress(text);
                SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
                task.doTask();

            }
        } catch (Exception e) {
            thrown = true;
        }

        assert (!thrown);


    }

    @Test
    public void SplitToMainSectionsTaskTest_doTask_CorrectDetectionOfSectionsWithExtractedTextObject() {
        // parser is not needed

        // Arrange
        String json = "{\"mFilename\":\"\",\"mTitle\":\"How to make chocolate mousse\\n2 ratings\\nHow to make chocolate mousse\\nBy Lesley Waters\",\"mSections\":[{\"mBody\":\"Shopping list\"},{\"mBody\":\"Print recipe\"},{\"mBody\":\"Preparation time\"},{\"mBody\":\"30 mins to 1 hour\"},{\"mBody\":\"Cooking time\"},{\"mBody\":\"10 to 30 mins\"},{\"mBody\":\"Serves\"},{\"mBody\":\"Serves 6-8\"},{\"mBody\":\"Dietary\\n \"},{\"mBody\":\"Vegetarian\"},{\"mBody\":\"\\n    225g/8oz dark chocolate\\n    5 medium free-range eggs\\n    100g/3½oz caster sugar\\n    170g/6oz unsalted butter\\n    200ml/7fl oz crème fraîche\\n    12-16 fresh cherries\\n    cocoa powder, for dusting\"},{\"mBody\":\"Method\"},{\"mBody\":\"    Place a bowl over a pan of simmering water (the water shouldn\\u0027t touch the bottom of the bowl) and gently melt the chocolate in the bowl. Remove from the heat once melted and let it cool slightly.\"},{\"mBody\":\"    Separate the egg yolks from the egg whites. Beat the egg yolks and most of the sugar together until creamy and pale in colour (keep two teaspoons of sugar to one side for the egg whites).\"},{\"mBody\":\"    When it has cooled slightly, whisk the chocolate into the egg yolk and sugar mixture.\"},{\"mBody\":\"    Melt the butter in a pan over a low heat.\"},{\"mBody\":\"    Whisk the melted butter into the chocolate mixture. If it gets too thick, add a couple of tablespoons of water.\"},{\"mBody\":\"    In a clean bowl, whisk the egg whites and the remaining two teaspoons of sugar with an electric whisk until they\\u0027re light and fluffy and hold a soft peak. Do not over-beat. The sugar will give them a gentle sheen.\"},{\"mBody\":\"    Carefully fold the egg whites into the chocolate mixture using a metal spoon.\"},{\"mBody\":\"    Spoon the chocolate mixture into small teacups or ramekins and refrigerate for about two hours.\"},{\"mBody\":\"    Just before serving, top each marquise with a dollop of crème fraîche and two fresh cherries, then sprinkle with cocoa powder.\\n\"}]}\n";
        ExtractedText text = ExtractedText.fromJson(json);
        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        String description = "How to make chocolate mousse\n" +
                "2 ratings\n" +
                "How to make chocolate mousse\n" +
                "By Lesley Waters\n" +
                "Preparation time\n" +
                "30 mins to 1 hour\n" +
                "Cooking time\n" +
                "10 to 30 mins\n" +
                "Serves\n" +
                "Serves 6-8\n" +
                "Dietary\n" +
                "Vegetarian";
        String ingredients = "225g/8oz dark chocolate\n" +
                "5 medium free-range eggs\n" +
                "100g/3½oz caster sugar\n" +
                "170g/6oz unsalted butter\n" +
                "200ml/7fl oz crème fraîche\n" +
                "12-16 fresh cherries\n" +
                "cocoa powder, for dusting";

        String steps = "Place a bowl over a pan of simmering water (the water shouldn't touch the bottom of the bowl) and gently melt the chocolate in the bowl. Remove from the heat once melted and let it cool slightly.\n" +
                "\n" +
                "Separate the egg yolks from the egg whites. Beat the egg yolks and most of the sugar together until creamy and pale in colour (keep two teaspoons of sugar to one side for the egg whites).\n" +
                "\n" +
                "When it has cooled slightly, whisk the chocolate into the egg yolk and sugar mixture.\n" +
                "\n" +
                "Melt the butter in a pan over a low heat.\n" +
                "\n" +
                "Whisk the melted butter into the chocolate mixture. If it gets too thick, add a couple of tablespoons of water.\n" +
                "\n" +
                "In a clean bowl, whisk the egg whites and the remaining two teaspoons of sugar with an electric whisk until they're light and fluffy and hold a soft peak. Do not over-beat. The sugar will give them a gentle sheen.\n" +
                "\n" +
                "Carefully fold the egg whites into the chocolate mixture using a metal spoon.\n" +
                "\n" +
                "Spoon the chocolate mixture into small teacups or ramekins and refrigerate for about two hours.\n" +
                "\n" +
                "Just before serving, top each marquise with a dollop of crème fraîche and two fresh cherries, then sprinkle with cocoa powder.";


        // Act
        task.doTask();
        System.out.println(rip.getStepsString());
        assert (rip.getDescription().equals(description));
        assert (rip.getIngredientsString().equals(ingredients));
        assert (rip.getStepsString().equals(steps));


        // test that needs the parser

        // Arrange
        json = "{\"mFilename\":\"\",\"mTitle\":\"How to make chocolate mousse\\n2 ratings\\nHow to make chocolate mousse\\nBy Lesley Waters\",\"mSections\":[{\"mBody\":\"Shopping list\"},{\"mBody\":\"Print recipe\"},{\"mBody\":\"Preparation time\"},{\"mBody\":\"30 mins to 1 hour\"},{\"mBody\":\"Cooking time\"},{\"mBody\":\"10 to 30 mins\"},{\"mBody\":\"Serves\"},{\"mBody\":\"Serves 6-8\"},{\"mBody\":\"Dietary\\n \"},{\"mBody\":\"Vegetarian\"},{\"mBody\":\"\\n    225g/8oz dark chocolate\\n    5 medium free-range eggs\\n    100g/3½oz caster sugar\\n    170g/6oz unsalted butter\\n    200ml/7fl oz crème fraîche\\n    12-16 fresh cherries\\n    cocoa powder, for dusting\"},{\"mBody\":\"\"},{\"mBody\":\"    Place a bowl over a pan of simmering water (the water shouldn\\u0027t touch the bottom of the bowl) and gently melt the chocolate in the bowl. Remove from the heat once melted and let it cool slightly.\"},{\"mBody\":\"    Separate the egg yolks from the egg whites. Beat the egg yolks and most of the sugar together until creamy and pale in colour (keep two teaspoons of sugar to one side for the egg whites).\"},{\"mBody\":\"    When it has cooled slightly, whisk the chocolate into the egg yolk and sugar mixture.\"},{\"mBody\":\"    Melt the butter in a pan over a low heat.\"},{\"mBody\":\"    Whisk the melted butter into the chocolate mixture. If it gets too thick, add a couple of tablespoons of water.\"},{\"mBody\":\"    In a clean bowl, whisk the egg whites and the remaining two teaspoons of sugar with an electric whisk until they\\u0027re light and fluffy and hold a soft peak. Do not over-beat. The sugar will give them a gentle sheen.\"},{\"mBody\":\"    Carefully fold the egg whites into the chocolate mixture using a metal spoon.\"},{\"mBody\":\"    Spoon the chocolate mixture into small teacups or ramekins and refrigerate for about two hours.\"},{\"mBody\":\"    Just before serving, top each marquise with a dollop of crème fraîche and two fresh cherries, then sprinkle with cocoa powder.\\n\"}]}\n";
        text = ExtractedText.fromJson(json);
        rip = new RecipeInProgress(text);
        task = new SplitToMainSectionsTask(rip);


        // Act
        task.doTask();

        // Assert
        assert (rip.getDescription().equals(description));
        assert (rip.getIngredientsString().equals(ingredients));
        assert (rip.getStepsString().equals(steps));


    }

    @Test
    public void SplitToMainSectionsTask_doTask_correctWithExtractedTextWithTitlesForIngredientsAndSteps() {


        String title = "BEEF AND RICE CASSEROLE";
        String firstBody = "Yield: 4 servings\n" +
                "Total Preparation Time: 60 minutes";
        String secondBody = "Size of bake ware: 8” x 8” baking dish\n" +
                "Cooking temperature: 350F";
        String titleSteps = "Cooking steps";
        String steps = "Brown ground meat, onions, and bell pepper. Drain and return to skillet or Dutch oven. Add salt, garlic salt, and black pepper. Add rice, salt, dry onion soup, and water. Cover and simmer for 20 minutes. Stir in soup and milk. Pour mixture into 8” x 8” baking dish. Top with crushed chips. Bake at 350F for 20 minutes.";
        List<String> images = new ArrayList<>();

        String titleIngredients = "Ingredients";
        String bodyIngredients = "1 lb. lean ground beef\n" +
                "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper\n" +
                "1 c. rice\n" +
                "1 tsp. salt\n" +
                "1 Tbsp. of dry onion soup mix\n" +
                "2 c. water\n" +
                "1 10 oz. can cream of mushroom soup\n" +
                "1⁄2 c. low-fat milk\n" +
                "1 c. crushed potato chips";
        Section section1 = new Section(firstBody);
        Section section2 = new Section(secondBody);
        Section ingredients = new Section(titleIngredients, bodyIngredients, images);
        Section stepsSection = new Section(titleSteps, steps, images);

        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
        text.addSection(section1);
        text.addSection(section2);
        text.addSection(ingredients);
        text.addSection(stepsSection);

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();


        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + firstBody + "\n" + secondBody));
    }

    @Test
    public void SplitToMainSectionsTask_doTask_correctWithExtractedTextWithTitlesOnlyForSteps() {


        String title = "BEEF AND RICE CASSEROLE";
        String firstBody = "Yield: 4 servings\n" +
                "Total Preparation Time: 60 minutes";
        String secondBody = "Size of bake ware: 8” x 8” baking dish\n" +
                "Cooking temperature: 350F";
        String titleSteps = "Cooking steps";
        String steps = "Brown ground meat, onions, and bell pepper. Drain and return to skillet or Dutch oven. Add salt, garlic salt, and black pepper. Add rice, salt, dry onion soup, and water. Cover and simmer for 20 minutes. Stir in soup and milk. Pour mixture into 8” x 8” baking dish. Top with crushed chips. Bake at 350F for 20 minutes.";
        List<String> images = new ArrayList<>();


        String bodyIngredients = "1 lb. lean ground beef\n" +
                "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper\n" +
                "1 c. rice\n" +
                "1 tsp. salt\n" +
                "1 Tbsp. of dry onion soup mix\n" +
                "2 c. water\n" +
                "1 10 oz. can cream of mushroom soup\n" +
                "1⁄2 c. low-fat milk\n" +
                "1 c. crushed potato chips";
        Section section1 = new Section(firstBody);
        Section section2 = new Section(secondBody);
        Section ingredients = new Section(bodyIngredients);
        Section stepsSection = new Section(titleSteps, steps, images);

        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
        text.addSection(section1);
        text.addSection(section2);
        text.addSection(ingredients);
        text.addSection(stepsSection);

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();


        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + firstBody + "\n" + secondBody));
    }

    @Test
    public void SplitToMainSectionsTask_doTask_correctWithExtractedTextWithTitlesOnlyForIngredients() {


        String title = "BEEF AND RICE CASSEROLE";
        String firstBody = "Yield: 4 servings\n" +
                "Total Preparation Time: 60 minutes";
        String secondBody = "Size of bake ware: 8” x 8” baking dish\n" +
                "Cooking temperature: 350F";
        String titleIngredients = "Ingredients";
        String steps = "Brown ground meat, onions, and bell pepper. Drain and return to skillet or Dutch oven. Add salt, garlic salt, and black pepper. Add rice, salt, dry onion soup, and water. Cover and simmer for 20 minutes. Stir in soup and milk. Pour mixture into 8” x 8” baking dish. Top with crushed chips. Bake at 350F for 20 minutes.";
        List<String> images = new ArrayList<>();


        String bodyIngredients = "1 lb. lean ground beef\n" +
                "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper\n" +
                "1 c. rice\n" +
                "1 tsp. salt\n" +
                "1 Tbsp. of dry onion soup mix\n" +
                "2 c. water\n" +
                "1 10 oz. can cream of mushroom soup\n" +
                "1⁄2 c. low-fat milk\n" +
                "1 c. crushed potato chips";
        Section section1 = new Section(firstBody);
        Section section2 = new Section(secondBody);
        Section step = new Section(steps);
        Section ingredients = new Section(titleIngredients, bodyIngredients, images);

        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
        text.addSection(section1);
        text.addSection(section2);
        text.addSection(ingredients);
        text.addSection(step);

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();


        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + firstBody + "\n" + secondBody));
    }

    @Test
    public void SplitToMainSectionsTask_doTask_correctWithExtractedTextWithTitlesOnlyForDescriptionBodies() {


        String title = "BEEF AND RICE CASSEROLE";
        String titleFirstBody = "Yield: 4 servings";
        String firstBody = "" +
                "Total Preparation Time: 60 minutes";
        String titleSecondBody = "Utensils:";
        String secondBody = "Size of bake ware: 8” x 8” baking dish\n" +
                "Cooking temperature: 350F";
        String titleIngredients = "Ingredients";
        String steps = "Brown ground meat, onions, and bell pepper. Drain and return to skillet or Dutch oven. Add salt, garlic salt, and black pepper. Add rice, salt, dry onion soup, and water. Cover and simmer for 20 minutes. Stir in soup and milk. Pour mixture into 8” x 8” baking dish. Top with crushed chips. Bake at 350F for 20 minutes.";
        List<String> images = new ArrayList<>();


        String bodyIngredients = "1 lb. lean ground beef\n" +
                "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper\n" +
                "1 c. rice\n" +
                "1 tsp. salt\n" +
                "1 Tbsp. of dry onion soup mix\n" +
                "2 c. water\n" +
                "1 10 oz. can cream of mushroom soup\n" +
                "1⁄2 c. low-fat milk\n" +
                "1 c. crushed potato chips";
        Section section1 = new Section(titleFirstBody, firstBody, images);
        Section section2 = new Section(titleSecondBody,secondBody, images);
        Section step = new Section(steps);
        Section ingredients = new Section(titleIngredients, bodyIngredients, images);

        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
        text.addSection(section1);
        text.addSection(section2);
        text.addSection(ingredients);
        text.addSection(step);

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();


        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + titleFirstBody+ "\n" + firstBody +
                "\n" +titleSecondBody +"\n" + secondBody));
    }
}