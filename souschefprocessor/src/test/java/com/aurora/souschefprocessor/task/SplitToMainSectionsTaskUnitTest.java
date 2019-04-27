package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

import static org.junit.Assert.assertEquals;


public class SplitToMainSectionsTaskUnitTest {
    private static List<ExtractedText> recipeTexts;


    @BeforeClass
    public static void initialize() throws IOException, ClassNotFoundException {
        recipeTexts = initializeRecipeText();
    }

    private static List<ExtractedText> initializeRecipeText() {

        String filename = "src/test/java/com/aurora/souschefprocessor/facade/json-recipes.txt";
        List<ExtractedText> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), "UTF8"));

            String line = reader.readLine();
            while (line != null) {
                list.add(ExtractedText.fromJson(line));
                line = reader.readLine();
            }

        } catch (IOException io) {
            System.err.print(io);
        }

        return list;


    }

    private static List<Map<String, String>> initializeFieldList() {
        List<Map<String, String>> fieldsList = new ArrayList<>();
        //recipe 1
        Map<String, String> map = new HashMap<>();

        // recipe 2
        map = new HashMap<>();
        map.put("STEPS", "Place a bowl over a pan of simmering water (the water shouldn't touch the bottom of the bowl) and gently melt the chocolate in the bowl. Remove from the heat once melted and let it cool slightly.\n" +
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
                "Just before serving, top each marquise with a dollop of crème fraîche and two fresh cherries, then sprinkle with cocoa powder.");
        map.put("INGR", "225g/8oz dark chocolate\n" +
                "5 medium free-range eggs\n" +
                "100g/3½oz caster sugar\n" +
                "170g/6oz unsalted butter\n" +
                "200ml/7fl oz crème fraîche\n" +
                "12-16 fresh cherries\n" +
                "cocoa powder, for dusting");
        fieldsList.add(map);

        // recipe 3
        map = new HashMap<>();
        map.put("STEPS", "Cook pasta in a large pot of boiling salted water, stirring occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking liquid; return pasta to pot.\n" +
                "While pasta cooks, pour tomatoes into a fine-mesh sieve set over a medium bowl. Shake to release as much juice as possible, then let tomatoes drain in sieve, collecting juices in bowl, until ready to use.\n" +
                "Heat 1/4 cup oil in a large deep-sided skillet over medium-high. Add capers and cook, swirling pan occasionally, until they burst and are crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper towel-lined plate, reserving oil in skillet.\n" +
                "Combine anchovies, tomato paste, and drained tomatoes in skillet. Cook over medium-high heat, stirring occasionally, until tomatoes begin to caramelize and anchovies start to break down, about 5 minutes. Add collected tomato juices, olives, oregano, and red pepper flakes and bring to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta cooking liquid to pan. Cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\n" +
                "Divide pasta among plates. Top with fried capers.");
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

        // recipe 4
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
                "1 tbsp. of dry onion soup mix\n" +
                "2 c. water\n" +
                "1 10 oz. can cream of mushroom soup\n" +
                "1⁄2 c. low-fat milk\n" +
                "1 c. crushed potato chips\n"
                + "size of bake ware: 8” x 8” baking dish\n" +
                "cooking temperature: 350f");
        fieldsList.add(map);

        // recipe 4
        map = new HashMap<>();
        map.put("STEPS", "Place a large pot of water over high heat. When the water is at a rolling boil, add a big pinch of salt, drop in the fettucine, and stir. Cook the pasta, stirring from time to time, according to package directions for al dente, usually about 12 minutes. Meanwhile, heat the olive oil in a large skillet over medium heat. When the oil is warm, add the garlic and sauté until golden, about 1 minute. Add the lemon zest and cook for 30 seconds longer. Increase the heat to medium-high, add the zucchini, and cook, stirring, until tender, 2 to 3 minutes. Season with salt and pepper.\n" +
                "Remove and reserve about 1/2 cup of the cooking water, then drain the pasta and quickly toss with the zucchini, parsley, and mint. Spoon on the ricotta and toss lightly again, add small amounts of the cooking water to lighten the cheese to the consistency you like, and serve.\n" +
                "\n" +
                "Cooks' Note\n" +
                "Zucchini is easy to shred on the large holes of a box grater, with the shredding attachment of a food processor, or with a mandoline.");
        map.put("INGR", "salt\n" +
                "1 pound fettuccine\n" +
                "4 tablespoons extra-virgin olive oil\n" +
                "3 or 4 garlic cloves, finely chopped or grated\n" +
                "zest of 1 to 2 lemons\n" +
                "2 medium- large or 4 small zucchini, cleaned but not peeled, and shredded\n" +
                "freshly ground pepper\n" +
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
        for (ExtractedText text : recipeTexts) {
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
        recipeTexts = initializeRecipeText();
        /**
         * The correct sections are detected
         */
        // Arrange
        List<Map<String, String>> fieldsList = initializeFieldList();

        for (int i = 0; i < fieldsList.size(); i++) {
            // Arrange
            ExtractedText text = recipeTexts.get(i);
            RecipeInProgress rip = new RecipeInProgress(text);
            SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
            // Act
            task.doTask();

            // Assert
            assertEquals("steps", fieldsList.get(i).get("STEPS"), rip.getStepsString());
            assertEquals("ingredients", fieldsList.get(i).get("INGR").toLowerCase(), rip.getIngredientsString().toLowerCase());

            // assert (rip.getDescription() != null);
        }
    }

    @Test
    public void SplitToMainSectionsTaskTest_doTask_NoExceptionsAreThrown() {
        List<ExtractedText> array = initializeRecipeText();
        boolean thrown = false;
        try {
            for (ExtractedText text : array) {
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
                "By Lesley Waters\n\n" +
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
        assertEquals("ingredients", ingredients, rip.getIngredientsString());
        assertEquals("description", description, rip.getDescription());
        assertEquals("steps", steps, rip.getStepsString());


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
    @Ignore
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
        //Section section1 = new Section(firstBody);
        //Section section2 = new Section(secondBody);
        //Section ingredients = new Section(titleIngredients, bodyIngredients, images);
        //Section stepsSection = new Section(titleSteps, steps, images);

        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
        //text.addSection(section1);
        //text.addSection(section2);
        //text.addSection(ingredients);
        //text.addSection(stepsSection);

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();
        System.out.println(rip.getStepsString());
        assertEquals("ingredients", rip.getIngredientsString(), bodyIngredients);
        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + firstBody + "\n" + secondBody));
    }

    @Test
    @Ignore
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
    /*    Section section1 = new Section(firstBody);
        Section section2 = new Section(secondBody);
        Section ingredients = new Section(bodyIngredients);
        Section stepsSection = new Section(titleSteps, steps, images);
*/
        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
     /*   text.addSection(section1);
        text.addSection(section2);
        text.addSection(ingredients);
        text.addSection(stepsSection);*/

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();

        assertEquals("ingredients", bodyIngredients, rip.getIngredientsString());
        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + firstBody + "\n" + secondBody));
    }

    @Test
    @Ignore
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
        /*Section section1 = new Section(firstBody);
        Section section2 = new Section(secondBody);
        Section step = new Section(steps);
        Section ingredients = new Section(titleIngredients, bodyIngredients, images);*/

        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
       /* text.addSection(section1);
        text.addSection(section2);
        text.addSection(ingredients);
        text.addSection(step);*/

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();


        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + firstBody + "\n" + secondBody));
    }

    @Test
    @Ignore
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
        /*Section section1 = new Section(titleFirstBody, firstBody, images);
        Section section2 = new Section(titleSecondBody, secondBody, images);
        Section step = new Section(steps);
        Section ingredients = new Section(titleIngredients, bodyIngredients, images);*/

        ExtractedText text = new ExtractedText("", new Date(System.currentTimeMillis()));
        text.setTitle(title);
        /*text.addSection(section1);
        text.addSection(section2);
        text.addSection(ingredients);
        text.addSection(step);*/

        RecipeInProgress rip = new RecipeInProgress(text);
        SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
        task.doTask();


        assert (rip.getIngredientsString().equals(bodyIngredients));

        assert (rip.getStepsString().equals(steps));
        assert (rip.getDescription().equals(title + "\n" + titleFirstBody + "\n" + firstBody +
                "\n" + titleSecondBody + "\n" + secondBody));
    }


    @Test
    public void SplitToMainSectionsTask_doTask_NotAllIngredientsInSameSection() {
        // Arrange

       String json = "{\n" +
                "   \"mFilename\": \"content://com.google.android.apps.docs.storage/document/acc%3D1%3Bdoc%3Dencoded%3DGDPoHpBnY6%2BmsRjpbyFZ64nchB90csqZM1KpNqa1adcFQ1v9eXj7Snb0Fgo%3D\",\n" +
                "   \"mSections\": [\n" +
                "       {\n" +
                "           \"mBody\": \"The beste chocomousse for: 4 people!\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"150 g pure chocolade 78%\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"2 large eggs\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"50 g witte basterdsuiker\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"200 ml verse slagroom\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"Hak de chocolade fijn. Laat de chocolade in ca. 5 min. au bain-marie smelten in\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0,\n" +
                "           \"mTitle\": \"Cooking steps\"\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"een kom boven een pan kokend water. Roer af en toe. Neem de kom van de pan.\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"Splits de eieren. Klop het eiwit met de helft van de suiker met een mixer ca.\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"5 min. totdat het glanzende stijve pieken vormt. Doe de slagroom in een ruime kom en\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"klop in ca. 3 min. stijf.\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"Klop de eidooiers los met een garde. Roer de rest van de suiker erdoor.\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"Roer de gesmolten chocolade door het eidooier-suikermengsel. Spatel het door de\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"slagroom. Spatel het eiwit snel en luchtig in delen door het chocolademengsel.\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"Schep de chocolademousse in glazen, potjes of coupes, dek af met vershoudfolie\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       },\n" +
                "       {\n" +
                "           \"mBody\": \"en laat minimaal 2 uur opstijven in de koelkast.  ;\\n\",\n" +
                "           \"mImages\": [],\n" +
                "           \"mLevel\": 0\n" +
                "       }\n" +
                "   ],\n" +
                "   \"mTitle\": \"Chocomousse\"\n" +
                "}";
        String ingredients = "150 g pure chocolade 78%\n" +
                "2 large eggs\n" +
                "50 g witte basterdsuiker\n" +
                "200 ml verse slagroom";
        String steps = "Hak de chocolade fijn. Laat de chocolade in ca. 5 min. au bain-marie smelten in\n" +
                "een kom boven een pan kokend water. Roer af en toe. Neem de kom van de pan.\n" +
                "Splits de eieren. Klop het eiwit met de helft van de suiker met een mixer ca.\n" +
                "5 min. totdat het glanzende stijve pieken vormt. Doe de slagroom in een ruime kom en\n" +
                "klop in ca. 3 min. stijf.\n" +
                "Klop de eidooiers los met een garde. Roer de rest van de suiker erdoor.\n" +
                "Roer de gesmolten chocolade door het eidooier-suikermengsel. Spatel het door de\n" +
                "slagroom. Spatel het eiwit snel en luchtig in delen door het chocolademengsel.\n" +
                "Schep de chocolademousse in glazen, potjes of coupes, dek af met vershoudfolie\n" +
                "en laat minimaal 2 uur opstijven in de koelkast.  ;";
        String description = "Chocomousse\n" +
                "The beste chocomousse for: 4 people!";

        // Act
        ExtractedText text = ExtractedText.fromJson(json);
        RecipeInProgress rip = new RecipeInProgress(text);
        (new SplitToMainSectionsTask(rip)).doTask();

        // Assert
        assertEquals("ingredients", ingredients, rip.getIngredientsString());
        assertEquals("description", description, rip.getDescription());
        assertEquals("steps", steps, rip.getStepsString());

    }
    


    @Test
    public void test_with_new_auroralib(){
        final String trial = StringUtils.join(new String [] {
                "{\"mFilename\":\"src/test/res/Pasta.docx\",\"mTitle\":\"Pasta puttanesca\",\"mTitleAnnotationProto\":{\n" +
                        "  \"text\": \"Pasta puttanesca\",\n" +
                        "  \"sentence\": [{\n" +
                        "    \"token\": [{\n" +
                        "      \"word\": \"Pasta\",\n" +
                        "      \"value\": \"Pasta\",\n" +
                        "      \"before\": \"\",\n" +
                        "      \"after\": \" \",\n" +
                        "      \"originalText\": \"Pasta\",\n" +
                        "      \"beginChar\": 0,\n" +
                        "      \"endChar\": 5,\n" +
                        "      \"tokenBeginIndex\": 0,\n" +
                        "      \"tokenEndIndex\": 1,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }, {\n" +
                        "      \"word\": \"puttanesca\",\n" +
                        "      \"value\": \"puttanesca\",\n" +
                        "      \"before\": \" \",\n" +
                        "      \"after\": \"\",\n" +
                        "      \"originalText\": \"puttanesca\",\n" +
                        "      \"beginChar\": 6,\n" +
                        "      \"endChar\": 16,\n" +
                        "      \"tokenBeginIndex\": 1,\n" +
                        "      \"tokenEndIndex\": 2,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }],\n" +
                        "    \"tokenOffsetBegin\": 0,\n" +
                        "    \"tokenOffsetEnd\": 2,\n" +
                        "    \"sentenceIndex\": 0,\n" +
                        "    \"characterOffsetBegin\": 0,\n" +
                        "    \"characterOffsetEnd\": 16,\n" +
                        "    \"hasRelationAnnotations\": false,\n" +
                        "    \"hasNumerizedTokensAnnotation\": false,\n" +
                        "    \"hasEntityMentionsAnnotation\": false\n" +
                        "  }],\n" +
                        "  \"xmlDoc\": false,\n" +
                        "  \"hasEntityMentionsAnnotation\": false,\n" +
                        "  \"hasCorefMentionAnnotation\": false,\n" +
                        "  \"hasCorefAnnotation\": false\n" +
                        "},\"mSections\":[{\"mTitle\":\"Yield\",\"mTitleAnnotationProto\":{\n" +
                        "  \"text\": \"Yield\",\n" +
                        "  \"sentence\": [{\n" +
                        "    \"token\": [{\n" +
                        "      \"word\": \"Yield\",\n" +
                        "      \"value\": \"Yield\",\n" +
                        "      \"before\": \"\",\n" +
                        "      \"after\": \"\",\n" +
                        "      \"originalText\": \"Yield\",\n" +
                        "      \"beginChar\": 0,\n" +
                        "      \"endChar\": 5,\n" +
                        "      \"tokenBeginIndex\": 0,\n" +
                        "      \"tokenEndIndex\": 1,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }],\n" +
                        "    \"tokenOffsetBegin\": 0,\n" +
                        "    \"tokenOffsetEnd\": 1,\n" +
                        "    \"sentenceIndex\": 0,\n" +
                        "    \"characterOffsetBegin\": 0,\n" +
                        "    \"characterOffsetEnd\": 5,\n" +
                        "    \"hasRelationAnnotations\": false,\n" +
                        "    \"hasNumerizedTokensAnnotation\": false,\n" +
                        "    \"hasEntityMentionsAnnotation\": false\n" +
                        "  }],\n" +
                        "  \"xmlDoc\": false,\n" +
                        "  \"hasEntityMentionsAnnotation\": false,\n" +
                        "  \"hasCorefMentionAnnotation\": false,\n" +
                        "  \"hasCorefAnnotation\": false\n" +
                        "},\"mBody\":\"4 servings\\nActive Time\\n30 minutes\\nTotal Time\\n35 minutes\\n\",\"mBodyAnnotationProto\":{\n" +
                        "  \"text\": \"4 servings\\nActive Time\\n30 minutes\\nTotal Time\\n35 minutes\\n\",\n" +
                        "  \"sentence\": [{\n" +
                        "    \"token\": [{\n" +
                        "      \"word\": \"4\",\n" +
                        "      \"value\": \"4\",\n" +
                        "      \"before\": \"\",\n" +
                        "      \"after\": \" \",\n" +
                        "      \"originalText\": \"4\",\n" +
                        "      \"beginChar\": 0,\n" +
                        "      \"endChar\": 1,\n" +
                        "      \"tokenBeginIndex\": 0,\n" +
                        "      \"tokenEndIndex\": 1,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }, {\n" +
                        "      \"word\": \"servings\",\n" +
                        "      \"value\": \"servings\",\n" +
                        "      \"before\": \" \",\n" +
                        "      \"after\": \"\\n\",\n" +
                        "      \"originalText\": \"servings\",\n" +
                        "      \"beginChar\": 2,\n" +
                        "      \"endChar\": 10,\n" +
                        "      \"tokenBeginIndex\": 1,\n" +
                        "      \"tokenEndIndex\": 2,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }, {\n" +
                        "      \"word\": \"Active\",\n" +
                        "      \"value\": \"Active\",\n" +
                        "      \"before\": \"\\n\",\n" +
                        "      \"after\": \" \",\n" +
                        "      \"originalText\": \"Active\",\n" +
                        "      \"beginChar\": 11,\n" +
                        "      \"endChar\": 17,\n" +
                        "      \"tokenBeginIndex\": 2,\n" +
                        "      \"tokenEndIndex\": 3,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }, {\n" +
                        "      \"word\": \"Time\",\n" +
                        "      \"value\": \"Time\",\n" +
                        "      \"before\": \" \",\n" +
                        "      \"after\": \"\\n\",\n" +
                        "      \"originalText\": \"Time\",\n" +
                        "      \"beginChar\": 18,\n" +
                        "      \"endChar\": 22,\n" +
                        "      \"tokenBeginIndex\": 3,\n" +
                        "      \"tokenEndIndex\": 4,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }, {\n" +
                        "      \"word\": \"30\",\n" +
                        "      \"value\": \"30\",\n" +
                        "      \"before\": \"\\n\",\n" +
                        "      \"after\": \" \",\n" +
                        "      \"originalText\": \"30\",\n" +
                        "      \"beginChar\": 23,\n" +
                        "      \"endChar\": 25,\n" +
                        "      \"tokenBeginIndex\": 4,\n" +
                        "      \"tokenEndIndex\": 5,\n" +
                        "      \"hasXmlContext\": false,\n" +
                        "      \"isNewline\": false\n" +
                        "    }, {\n" , "      \"word\": \"minutes\",\n" +
                "      \"value\": \"minutes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"minutes\",\n" +
                "      \"beginChar\": 26,\n" +
                "      \"endChar\": 33,\n" +
                "      \"tokenBeginIndex\": 5,\n" +
                "      \"tokenEndIndex\": 6,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"Total\",\n" +
                "      \"value\": \"Total\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Total\",\n" +
                "      \"beginChar\": 34,\n" +
                "      \"endChar\": 39,\n" +
                "      \"tokenBeginIndex\": 6,\n" +
                "      \"tokenEndIndex\": 7,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"Time\",\n" +
                "      \"value\": \"Time\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"Time\",\n" +
                "      \"beginChar\": 40,\n" +
                "      \"endChar\": 44,\n" +
                "      \"tokenBeginIndex\": 7,\n" +
                "      \"tokenEndIndex\": 8,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"35\",\n" +
                "      \"value\": \"35\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"35\",\n" +
                "      \"beginChar\": 45,\n" +
                "      \"endChar\": 47,\n" +
                "      \"tokenBeginIndex\": 8,\n" +
                "      \"tokenEndIndex\": 9,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"minutes\",\n" +
                "      \"value\": \"minutes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"minutes\",\n" +
                "      \"beginChar\": 48,\n" +
                "      \"endChar\": 55,\n" +
                "      \"tokenBeginIndex\": 9,\n" +
                "      \"tokenEndIndex\": 10,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 0,\n" +
                "    \"tokenOffsetEnd\": 10,\n" +
                "    \"sentenceIndex\": 0,\n" +
                "    \"characterOffsetBegin\": 0,\n" +
                "    \"characterOffsetEnd\": 55,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }],\n" , "  \"xmlDoc\": false,\n" +
                "  \"hasEntityMentionsAnnotation\": false,\n" +
                "  \"hasCorefMentionAnnotation\": false,\n" +
                "  \"hasCorefAnnotation\": false\n" +
                "},\"mImages\":[],\"mLevel\":0},{\"mTitle\":\"Ingredients\",\"mTitleAnnotationProto\":{\n" +
                "  \"text\": \"Ingredients\",\n" +
                "  \"sentence\": [{\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Ingredients\",\n" +
                "      \"value\": \"Ingredients\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"Ingredients\",\n" +
                "      \"beginChar\": 0,\n" +
                "      \"endChar\": 11,\n" +
                "      \"tokenBeginIndex\": 0,\n" +
                "      \"tokenEndIndex\": 1,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 0,\n" +
                "    \"tokenOffsetEnd\": 1,\n" +
                "    \"sentenceIndex\": 0,\n" +
                "    \"characterOffsetBegin\": 0,\n" +
                "    \"characterOffsetEnd\": 11,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }],\n" +
                "  \"xmlDoc\": false,\n" +
                "  \"hasEntityMentionsAnnotation\": false,\n" +
                "  \"hasCorefMentionAnnotation\": false,\n" +
                "  \"hasCorefAnnotation\": false\n" +
                "},\"mBody\":\"1 lb. linguine or other long pasta\\nKosher salt\\n1 (14-oz.) can diced tomatoes\\n1/2 cup extra-virgin olive oil, divided\\n1/4 cup capers, drained\\n6 oil-packed anchovy fillets\\n1 Tbsp. tomato paste\\n1/3 cup pitted Kalamata olives, halved\\n2 tsp. dried oregano\\n1/2 tsp. crushed red pepper flakes\\n6 oz. oil-packed tuna\\n\",\"mBodyAnnotationProto\":{\n" +
                "  \"text\": \"1 lb. linguine or other long pasta\\nKosher salt\\n1 (14-oz.) can diced tomatoes\\n1/2 cup extra-virgin olive oil, divided\\n1/4 cup capers, drained\\n6 oil-packed anchovy fillets\\n1 Tbsp. tomato paste\\n1/3 cup pitted Kalamata olives, halved\\n2 tsp. dried oregano\\n1/2 tsp. crushed red pepper flakes\\n6 oz. oil-packed tuna\\n\",\n" +
                "  \"sentence\": [{\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"1\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1\",\n" +
                "      \"beginChar\": 0,\n" +
                "      \"endChar\": 1,\n" +
                "      \"tokenBeginIndex\": 0,\n" +
                "      \"tokenEndIndex\": 1,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"lb\",\n" +
                "      \"value\": \"lb\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"lb\",\n" +
                "      \"beginChar\": 2,\n" +
                "      \"endChar\": 4,\n" +
                "      \"tokenBeginIndex\": 1,\n" +
                "      \"tokenEndIndex\": 2,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 4,\n" +
                "      \"endChar\": 5,\n" +
                "      \"tokenBeginIndex\": 2,\n" +
                "      \"tokenEndIndex\": 3,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 0,\n" +
                "    \"tokenOffsetEnd\": 3,\n" +
                "    \"sentenceIndex\": 0,\n" +
                "    \"characterOffsetBegin\": 0,\n" +
                "    \"characterOffsetEnd\": 5,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"linguine\",\n" +
                "      \"value\": \"linguine\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"linguine\",\n" +
                "      \"beginChar\": 6,\n" +
                "      \"endChar\": 14,\n" +
                "      \"tokenBeginIndex\": 3,\n" +
                "      \"tokenEndIndex\": 4,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"or\",\n" +
                "      \"value\": \"or\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"or\",\n" +
                "      \"beginChar\": 15,\n" +
                "      \"endChar\": 17,\n" +
                "      \"tokenBeginIndex\": 4,\n" +
                "      \"tokenEndIndex\": 5,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"other\",\n" +
                "      \"value\": \"other\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"other\",\n" +
                "      \"beginChar\": 18,\n" +
                "      \"endChar\": 23,\n" +
                "      \"tokenBeginIndex\": 5,\n" +
                "      \"tokenEndIndex\": 6,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"long\",\n" +
                "      \"value\": \"long\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"long\",\n" +
                "      \"beginChar\": 24,\n" +
                "      \"endChar\": 28,\n" +
                "      \"tokenBeginIndex\": 6,\n" +
                "      \"tokenEndIndex\": 7,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 29,\n" +
                "      \"endChar\": 34,\n" +
                "      \"tokenBeginIndex\": 7,\n" +
                "      \"tokenEndIndex\": 8,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"Kosher\",\n" +
                "      \"value\": \"Kosher\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Kosher\",\n" +
                "      \"beginChar\": 35,\n" +
                "      \"endChar\": 41,\n" +
                "      \"tokenBeginIndex\": 8,\n" +
                "      \"tokenEndIndex\": 9,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"salt\",\n" +
                "      \"value\": \"salt\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"salt\",\n" +
                "      \"beginChar\": 42,\n" +
                "      \"endChar\": 46,\n" +
                "      \"tokenBeginIndex\": 9,\n" +
                "      \"tokenEndIndex\": 10,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1\",\n" +
                "      \"beginChar\": 47,\n" +
                "      \"endChar\": 48,\n" +
                "      \"tokenBeginIndex\": 10,\n" +
                "      \"tokenEndIndex\": 11,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"-LRB-\",\n" +
                "      \"value\": \"-LRB-\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"(\",\n" +
                "      \"beginChar\": 49,\n" +
                "      \"endChar\": 50,\n" +
                "      \"tokenBeginIndex\": 11,\n" +
                "      \"tokenEndIndex\": 12,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"14-oz\",\n" +
                "      \"value\": \"14-oz\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"14-oz\",\n" +
                "      \"beginChar\": 50,\n" +
                "      \"endChar\": 55,\n" +
                "      \"tokenBeginIndex\": 12,\n" +
                "      \"tokenEndIndex\": 13,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 55,\n" +
                "      \"endChar\": 56,\n" +
                "      \"tokenBeginIndex\": 13,\n" +
                "      \"tokenEndIndex\": 14,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"-RRB-\",\n" +
                "      \"value\": \"-RRB-\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \")\",\n" +
                "      \"beginChar\": 56,\n" +
                "      \"endChar\": 57,\n" +
                "      \"tokenBeginIndex\": 14,\n" +
                "      \"tokenEndIndex\": 15,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 3,\n" +
                "    \"tokenOffsetEnd\": 15,\n" +
                "    \"sentenceIndex\": 1,\n" +
                "    \"characterOffsetBegin\": 6,\n" +
                "    \"characterOffsetEnd\": 57,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"can\",\n" +
                "      \"value\": \"can\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"can\",\n" +
                "      \"beginChar\": 58,\n" +
                "      \"endChar\": 61,\n" +
                "      \"tokenBeginIndex\": 15,\n" +
                "      \"tokenEndIndex\": 16,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"diced\",\n" +
                "      \"value\": \"diced\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"diced\",\n" +
                "      \"beginChar\": 62,\n" +
                "      \"endChar\": 67,\n" +
                "      \"tokenBeginIndex\": 16,\n" +
                "      \"tokenEndIndex\": 17,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomatoes\",\n" +
                "      \"value\": \"tomatoes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"tomatoes\",\n" +
                "      \"beginChar\": 68,\n" +
                "      \"endChar\": 76,\n" +
                "      \"tokenBeginIndex\": 17,\n" +
                "      \"tokenEndIndex\": 18,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1/2\",\n" +
                "      \"value\": \"1/2\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1/2\",\n" +
                "      \"beginChar\": 77,\n" +
                "      \"endChar\": 80,\n" +
                "      \"tokenBeginIndex\": 18,\n" +
                "      \"tokenEndIndex\": 19,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 81,\n" +
                "      \"endChar\": 84,\n" +
                "      \"tokenBeginIndex\": 19,\n" +
                "      \"tokenEndIndex\": 20,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"extra-virgin\",\n" +
                "      \"value\": \"extra-virgin\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"extra-virgin\",\n" +
                "      \"beginChar\": 85,\n" +
                "      \"endChar\": 97,\n" +
                "      \"tokenBeginIndex\": 20,\n" +
                "      \"tokenEndIndex\": 21,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" , "      \"word\": \"olive\",\n" +
                "      \"value\": \"olive\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"olive\",\n" +
                "      \"beginChar\": 98,\n" +
                "      \"endChar\": 103,\n" +
                "      \"tokenBeginIndex\": 21,\n" +
                "      \"tokenEndIndex\": 22,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oil\",\n" +
                "      \"value\": \"oil\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"oil\",\n" +
                "      \"beginChar\": 104,\n" +
                "      \"endChar\": 107,\n" +
                "      \"tokenBeginIndex\": 22,\n" +
                "      \"tokenEndIndex\": 23,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 107,\n" +
                "      \"endChar\": 108,\n" +
                "      \"tokenBeginIndex\": 23,\n" +
                "      \"tokenEndIndex\": 24,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"divided\",\n" +
                "      \"value\": \"divided\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"divided\",\n" +
                "      \"beginChar\": 109,\n" +
                "      \"endChar\": 116,\n" +
                "      \"tokenBeginIndex\": 24,\n" +
                "      \"tokenEndIndex\": 25,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1/4\",\n" +
                "      \"value\": \"1/4\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1/4\",\n" +
                "      \"beginChar\": 117,\n" +
                "      \"endChar\": 120,\n" +
                "      \"tokenBeginIndex\": 25,\n" +
                "      \"tokenEndIndex\": 26,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 121,\n" +
                "      \"endChar\": 124,\n" +
                "      \"tokenBeginIndex\": 26,\n" +
                "      \"tokenEndIndex\": 27,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"capers\",\n" +
                "      \"value\": \"capers\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"capers\",\n" +
                "      \"beginChar\": 125,\n" +
                "      \"endChar\": 131,\n" +
                "      \"tokenBeginIndex\": 27,\n" +
                "      \"tokenEndIndex\": 28,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 131,\n" +
                "      \"endChar\": 132,\n" +
                "      \"tokenBeginIndex\": 28,\n" +
                "      \"tokenEndIndex\": 29,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"drained\",\n" +
                "      \"value\": \"drained\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"drained\",\n" +
                "      \"beginChar\": 133,\n" +
                "      \"endChar\": 140,\n" +
                "      \"tokenBeginIndex\": 29,\n" +
                "      \"tokenEndIndex\": 30,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"6\",\n" +
                "      \"value\": \"6\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"6\",\n" +
                "      \"beginChar\": 141,\n" +
                "      \"endChar\": 142,\n" +
                "      \"tokenBeginIndex\": 30,\n" +
                "      \"tokenEndIndex\": 31,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oil-packed\",\n" +
                "      \"value\": \"oil-packed\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"oil-packed\",\n" +
                "      \"beginChar\": 143,\n" +
                "      \"endChar\": 153,\n" +
                "      \"tokenBeginIndex\": 31,\n" +
                "      \"tokenEndIndex\": 32,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"anchovy\",\n" +
                "      \"value\": \"anchovy\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"anchovy\",\n" +
                "      \"beginChar\": 154,\n" +
                "      \"endChar\": 161,\n" +
                "      \"tokenBeginIndex\": 32,\n" +
                "      \"tokenEndIndex\": 33,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"fillets\",\n" +
                "      \"value\": \"fillets\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"fillets\",\n" +
                "      \"beginChar\": 162,\n" +
                "      \"endChar\": 169,\n" +
                "      \"tokenBeginIndex\": 33,\n" +
                "      \"tokenEndIndex\": 34,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1\",\n" +
                "      \"beginChar\": 170,\n" +
                "      \"endChar\": 171,\n" +
                "      \"tokenBeginIndex\": 34,\n" +
                "      \"tokenEndIndex\": 35,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"Tbsp.\",\n" +
                "      \"value\": \"Tbsp.\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Tbsp.\",\n" +
                "      \"beginChar\": 172,\n" +
                "      \"endChar\": 177,\n" +
                "      \"tokenBeginIndex\": 35,\n" +
                "      \"tokenEndIndex\": 36,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomato\",\n" +
                "      \"value\": \"tomato\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tomato\",\n" +
                "      \"beginChar\": 178,\n" +
                "      \"endChar\": 184,\n" +
                "      \"tokenBeginIndex\": 36,\n" +
                "      \"tokenEndIndex\": 37,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"paste\",\n" +
                "      \"value\": \"paste\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"paste\",\n" +
                "      \"beginChar\": 185,\n" +
                "      \"endChar\": 190,\n" +
                "      \"tokenBeginIndex\": 37,\n" +
                "      \"tokenEndIndex\": 38,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1/3\",\n" +
                "      \"value\": \"1/3\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1/3\",\n" +
                "      \"beginChar\": 191,\n" +
                "      \"endChar\": 194,\n" +
                "      \"tokenBeginIndex\": 38,\n" +
                "      \"tokenEndIndex\": 39,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 195,\n" +
                "      \"endChar\": 198,\n" +
                "      \"tokenBeginIndex\": 39,\n" +
                "      \"tokenEndIndex\": 40,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pitted\",\n" +
                "      \"value\": \"pitted\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pitted\",\n" +
                "      \"beginChar\": 199,\n" +
                "      \"endChar\": 205,\n" +
                "      \"tokenBeginIndex\": 40,\n" +
                "      \"tokenEndIndex\": 41,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"Kalamata\",\n" +
                "      \"value\": \"Kalamata\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Kalamata\",\n" +
                "      \"beginChar\": 206,\n" +
                "      \"endChar\": 214,\n" +
                "      \"tokenBeginIndex\": 41,\n" +
                "      \"tokenEndIndex\": 42,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"olives\",\n" +
                "      \"value\": \"olives\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"olives\",\n" +
                "      \"beginChar\": 215,\n" +
                "      \"endChar\": 221,\n" +
                "      \"tokenBeginIndex\": 42,\n" +
                "      \"tokenEndIndex\": 43,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 221,\n" +
                "      \"endChar\": 222,\n" +
                "      \"tokenBeginIndex\": 43,\n" +
                "      \"tokenEndIndex\": 44,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"halved\",\n" +
                "      \"value\": \"halved\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"halved\",\n" +
                "      \"beginChar\": 223,\n" +
                "      \"endChar\": 229,\n" +
                "      \"tokenBeginIndex\": 44,\n" +
                "      \"tokenEndIndex\": 45,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"2\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"2\",\n" +
                "      \"beginChar\": 230,\n" +
                "      \"endChar\": 231,\n" +
                "      \"tokenBeginIndex\": 45,\n" +
                "      \"tokenEndIndex\": 46,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tsp.\",\n" +
                "      \"value\": \"tsp.\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tsp.\",\n" +
                "      \"beginChar\": 232,\n" +
                "      \"endChar\": 236,\n" +
                "      \"tokenBeginIndex\": 46,\n" +
                "      \"tokenEndIndex\": 47,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"dried\",\n" +
                "      \"value\": \"dried\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"dried\",\n" +
                "      \"beginChar\": 237,\n" +
                "      \"endChar\": 242,\n" +
                "      \"tokenBeginIndex\": 47,\n" +
                "      \"tokenEndIndex\": 48,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oregano\",\n" +
                "      \"value\": \"oregano\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"oregano\",\n" +
                "      \"beginChar\": 243,\n" +
                "      \"endChar\": 250,\n" +
                "      \"tokenBeginIndex\": 48,\n" +
                "      \"tokenEndIndex\": 49,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1/2\",\n" +
                "      \"value\": \"1/2\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1/2\",\n" +
                "      \"beginChar\": 251,\n" +
                "      \"endChar\": 254,\n" +
                "      \"tokenBeginIndex\": 49,\n" +
                "      \"tokenEndIndex\": 50,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tsp.\",\n" +
                "      \"value\": \"tsp.\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tsp.\",\n" +
                "      \"beginChar\": 255,\n" +
                "      \"endChar\": 259,\n" +
                "      \"tokenBeginIndex\": 50,\n" +
                "      \"tokenEndIndex\": 51,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"crushed\",\n" +
                "      \"value\": \"crushed\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"crushed\",\n" +
                "      \"beginChar\": 260,\n" +
                "      \"endChar\": 267,\n" +
                "      \"tokenBeginIndex\": 51,\n" +
                "      \"tokenEndIndex\": 52,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"red\",\n" +
                "      \"value\": \"red\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"red\",\n" +
                "      \"beginChar\": 268,\n" +
                "      \"endChar\": 271,\n" +
                "      \"tokenBeginIndex\": 52,\n" +
                "      \"tokenEndIndex\": 53,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pepper\",\n" +
                "      \"value\": \"pepper\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pepper\",\n" +
                "      \"beginChar\": 272,\n" +
                "      \"endChar\": 278,\n" +
                "      \"tokenBeginIndex\": 53,\n" +
                "      \"tokenEndIndex\": 54,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"flakes\",\n" +
                "      \"value\": \"flakes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"flakes\",\n" +
                "      \"beginChar\": 279,\n" +
                "      \"endChar\": 285,\n" +
                "      \"tokenBeginIndex\": 54,\n" +
                "      \"tokenEndIndex\": 55,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"6\",\n" +
                "      \"value\": \"6\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"6\",\n" +
                "      \"beginChar\": 286,\n" +
                "      \"endChar\": 287,\n" +
                "      \"tokenBeginIndex\": 55,\n" +
                "      \"tokenEndIndex\": 56,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oz\",\n" +
                "      \"value\": \"oz\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"oz\",\n" +
                "      \"beginChar\": 288,\n" +
                "      \"endChar\": 290,\n" +
                "      \"tokenBeginIndex\": 56,\n" +
                "      \"tokenEndIndex\": 57,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" ,  "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 290,\n" +
                "      \"endChar\": 291,\n" +
                "      \"tokenBeginIndex\": 57,\n" +
                "      \"tokenEndIndex\": 58,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 15,\n" +
                "    \"tokenOffsetEnd\": 58,\n" +
                "    \"sentenceIndex\": 2,\n" +
                "    \"characterOffsetBegin\": 58,\n" +
                "    \"characterOffsetEnd\": 291,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"oil-packed\",\n" +
                "      \"value\": \"oil-packed\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"oil-packed\",\n" +
                "      \"beginChar\": 292,\n" +
                "      \"endChar\": 302,\n" +
                "      \"tokenBeginIndex\": 58,\n" +
                "      \"tokenEndIndex\": 59,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tuna\",\n" +
                "      \"value\": \"tuna\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \"tuna\",\n" +
                "      \"beginChar\": 303,\n" +
                "      \"endChar\": 307,\n" +
                "      \"tokenBeginIndex\": 59,\n" +
                "      \"tokenEndIndex\": 60,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 58,\n" +
                "    \"tokenOffsetEnd\": 60,\n" +
                "    \"sentenceIndex\": 3,\n" +
                "    \"characterOffsetBegin\": 292,\n" +
                "    \"characterOffsetEnd\": 307,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }],\n" +
                "  \"xmlDoc\": false,\n" +
                "  \"hasEntityMentionsAnnotation\": false,\n" +
                "  \"hasCorefMentionAnnotation\": false,\n" +
                "  \"hasCorefAnnotation\": false\n" +
                "},\"mImages\":[],\"mLevel\":0},{\"mTitle\":\"Preparation\",\"mTitleAnnotationProto\":{\n" +
                "  \"text\": \"Preparation\",\n" +
                "  \"sentence\": [{\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Preparation\",\n" +
                "      \"value\": \"Preparation\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"Preparation\",\n" +
                "      \"beginChar\": 0,\n" +
                "      \"endChar\": 11,\n" +
                "      \"tokenBeginIndex\": 0,\n" +
                "      \"tokenEndIndex\": 1,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 0,\n" +
                "    \"tokenOffsetEnd\": 1,\n" +
                "    \"sentenceIndex\": 0,\n" +
                "    \"characterOffsetBegin\": 0,\n" +
                "    \"characterOffsetEnd\": 11,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }],\n" +
                "  \"xmlDoc\": false,\n" +
                "  \"hasEntityMentionsAnnotation\": false,\n" +
                "  \"hasCorefMentionAnnotation\": false,\n" +
                "  \"hasCorefAnnotation\": false\n" +
                "},\"mBody\":\"Cook pasta in a large pot of boiling salted water, stirring occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking liquid; return pasta to pot.\\nWhile pasta cooks, pour tomatoes into a fine-mesh sieve set over a medium bowl. Shake to release as much juice as possible, then let tomatoes drain in sieve, collecting juices in bowl, until ready to use.\\nHeat 1/4 cup oil in a large deep-sided skillet over medium-high. Add capers and cook, swirling pan occasionally, until they burst and are crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper towel-lined plate, reserving oil in skillet.\\nCombine anchovies, tomato paste, and drained tomatoes in skillet. Cook over medium-high heat, stirring occasionally, until tomatoes begin to caramelize and anchovies start to break down, about 5 minutes. Add collected tomato juices, olives, oregano, and red pepper flakes and bring to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta cooking liquid to pan. Cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\\nDivide pasta among plates. Top with fried capers.\\n\",\"mBodyAnnotationProto\":{\n" +
                "  \"text\": \"Cook pasta in a large pot of boiling salted water, stirring occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking liquid; return pasta to pot.\\nWhile pasta cooks, pour tomatoes into a fine-mesh sieve set over a medium bowl. Shake to release as much juice as possible, then let tomatoes drain in sieve, collecting juices in bowl, until ready to use.\\nHeat 1/4 cup oil in a large deep-sided skillet over medium-high. Add capers and cook, swirling pan occasionally, until they burst and are crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper towel-lined plate, reserving oil in skillet.\\nCombine anchovies, tomato paste, and drained tomatoes in skillet. Cook over medium-high heat, stirring occasionally, until tomatoes begin to caramelize and anchovies start to break down, about 5 minutes. Add collected tomato juices, olives, oregano, and red pepper flakes and bring to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta cooking liquid to pan. Cook over medium heat, stirring and adding remaining 1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\\nDivide pasta among plates. Top with fried capers.\\n\",\n" +
                "  \"sentence\": [{\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Cook\",\n" +
                "      \"value\": \"Cook\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Cook\",\n" +
                "      \"beginChar\": 0,\n" +
                "      \"endChar\": 4,\n" +
                "      \"tokenBeginIndex\": 0,\n" +
                "      \"tokenEndIndex\": 1,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 5,\n" +
                "      \"endChar\": 10,\n" +
                "      \"tokenBeginIndex\": 1,\n" +
                "      \"tokenEndIndex\": 2,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"in\",\n" +
                "      \"value\": \"in\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"in\",\n" +
                "      \"beginChar\": 11,\n" +
                "      \"endChar\": 13,\n" +
                "      \"tokenBeginIndex\": 2,\n" +
                "      \"tokenEndIndex\": 3,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"a\",\n" +
                "      \"value\": \"a\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"a\",\n" +
                "      \"beginChar\": 14,\n" +
                "      \"endChar\": 15,\n" +
                "      \"tokenBeginIndex\": 3,\n" +
                "      \"tokenEndIndex\": 4,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"large\",\n" +
                "      \"value\": \"large\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"large\",\n" +
                "      \"beginChar\": 16,\n" +
                "      \"endChar\": 21,\n" +
                "      \"tokenBeginIndex\": 4,\n" +
                "      \"tokenEndIndex\": 5,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pot\",\n" +
                "      \"value\": \"pot\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pot\",\n" +
                "      \"beginChar\": 22,\n" +
                "      \"endChar\": 25,\n" +
                "      \"tokenBeginIndex\": 5,\n" +
                "      \"tokenEndIndex\": 6,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"of\",\n" +
                "      \"value\": \"of\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"of\",\n" +
                "      \"beginChar\": 26,\n" +
                "      \"endChar\": 28,\n" +
                "      \"tokenBeginIndex\": 6,\n" +
                "      \"tokenEndIndex\": 7,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"boiling\",\n" +
                "      \"value\": \"boiling\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"boiling\",\n" +
                "      \"beginChar\": 29,\n" +
                "      \"endChar\": 36,\n" +
                "      \"tokenBeginIndex\": 7,\n" +
                "      \"tokenEndIndex\": 8,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"salted\",\n" +
                "      \"value\": \"salted\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"salted\",\n" +
                "      \"beginChar\": 37,\n" +
                "      \"endChar\": 43,\n" +
                "      \"tokenBeginIndex\": 8,\n" +
                "      \"tokenEndIndex\": 9,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"water\",\n" +
                "      \"value\": \"water\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"water\",\n" +
                "      \"beginChar\": 44,\n" +
                "      \"endChar\": 49,\n" +
                "      \"tokenBeginIndex\": 9,\n" +
                "      \"tokenEndIndex\": 10,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 49,\n" +
                "      \"endChar\": 50,\n" +
                "      \"tokenBeginIndex\": 10,\n" +
                "      \"tokenEndIndex\": 11,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"stirring\",\n" +
                "      \"value\": \"stirring\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"stirring\",\n" +
                "      \"beginChar\": 51,\n" +
                "      \"endChar\": 59,\n" +
                "      \"tokenBeginIndex\": 11,\n" +
                "      \"tokenEndIndex\": 12,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"occasionally\",\n" +
                "      \"value\": \"occasionally\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"occasionally\",\n" +
                "      \"beginChar\": 60,\n" +
                "      \"endChar\": 72,\n" +
                "      \"tokenBeginIndex\": 12,\n" +
                "      \"tokenEndIndex\": 13,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 72,\n" +
                "      \"endChar\": 73,\n" +
                "      \"tokenBeginIndex\": 13,\n" +
                "      \"tokenEndIndex\": 14,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"until\",\n" +
                "      \"value\": \"until\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"until\",\n" +
                "      \"beginChar\": 74,\n" +
                "      \"endChar\": 79,\n" +
                "      \"tokenBeginIndex\": 14,\n" +
                "      \"tokenEndIndex\": 15,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"al\",\n" +
                "      \"value\": \"al\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"al\",\n" +
                "      \"beginChar\": 80,\n" +
                "      \"endChar\": 82,\n" +
                "      \"tokenBeginIndex\": 15,\n" +
                "      \"tokenEndIndex\": 16,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"dente\",\n" +
                "      \"value\": \"dente\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"dente\",\n" +
                "      \"beginChar\": 83,\n" +
                "      \"endChar\": 88,\n" +
                "      \"tokenBeginIndex\": 16,\n" +
                "      \"tokenEndIndex\": 17,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 88,\n" +
                "      \"endChar\": 89,\n" +
                "      \"tokenBeginIndex\": 17,\n" +
                "      \"tokenEndIndex\": 18,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 0,\n" +
                "    \"tokenOffsetEnd\": 18,\n" +
                "    \"sentenceIndex\": 0,\n" +
                "    \"characterOffsetBegin\": 0,\n" +
                "    \"characterOffsetEnd\": 89,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Drain\",\n" +
                "      \"value\": \"Drain\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Drain\",\n" +
                "      \"beginChar\": 90,\n" +
                "      \"endChar\": 95,\n" +
                "      \"tokenBeginIndex\": 18,\n" +
                "      \"tokenEndIndex\": 19,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 96,\n" +
                "      \"endChar\": 101,\n" +
                "      \"tokenBeginIndex\": 19,\n" +
                "      \"tokenEndIndex\": 20,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 101,\n" +
                "      \"endChar\": 102,\n" +
                "      \"tokenBeginIndex\": 20,\n" +
                "      \"tokenEndIndex\": 21,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"reserving\",\n" +
                "      \"value\": \"reserving\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"reserving\",\n" +
                "      \"beginChar\": 103,\n" +
                "      \"endChar\": 112,\n" +
                "      \"tokenBeginIndex\": 21,\n" +
                "      \"tokenEndIndex\": 22,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1\",\n" +
                "      \"value\": \"1\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1\",\n" +
                "      \"beginChar\": 113,\n" +
                "      \"endChar\": 114,\n" +
                "      \"tokenBeginIndex\": 22,\n" +
                "      \"tokenEndIndex\": 23,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 115,\n" +
                "      \"endChar\": 118,\n" +
                "      \"tokenBeginIndex\": 23,\n" +
                "      \"tokenEndIndex\": 24,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 119,\n" +
                "      \"endChar\": 124,\n" +
                "      \"tokenBeginIndex\": 24,\n" +
                "      \"tokenEndIndex\": 25,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cooking\",\n" +
                "      \"value\": \"cooking\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cooking\",\n" +
                "      \"beginChar\": 125,\n" +
                "      \"endChar\": 132,\n" +
                "      \"tokenBeginIndex\": 25,\n" +
                "      \"tokenEndIndex\": 26,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"liquid\",\n" +
                "      \"value\": \"liquid\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"liquid\",\n" +
                "      \"beginChar\": 133,\n" +
                "      \"endChar\": 139,\n" +
                "      \"tokenBeginIndex\": 26,\n" +
                "      \"tokenEndIndex\": 27,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \";\",\n" +
                "      \"value\": \";\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \";\",\n" +
                "      \"beginChar\": 139,\n" +
                "      \"endChar\": 140,\n" +
                "      \"tokenBeginIndex\": 27,\n" +
                "      \"tokenEndIndex\": 28,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"return\",\n" +
                "      \"value\": \"return\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"return\",\n" +
                "      \"beginChar\": 141,\n" +
                "      \"endChar\": 147,\n" +
                "      \"tokenBeginIndex\": 28,\n" +
                "      \"tokenEndIndex\": 29,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 148,\n" +
                "      \"endChar\": 153,\n" +
                "      \"tokenBeginIndex\": 29,\n" +
                "      \"tokenEndIndex\": 30,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 154,\n" +
                "      \"endChar\": 156,\n" +
                "      \"tokenBeginIndex\": 30,\n" +
                "      \"tokenEndIndex\": 31,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pot\",\n" +
                "      \"value\": \"pot\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"pot\",\n" +
                "      \"beginChar\": 157,\n" +
                "      \"endChar\": 160,\n" +
                "      \"tokenBeginIndex\": 31,\n" +
                "      \"tokenEndIndex\": 32,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 160,\n" +
                "      \"endChar\": 161,\n" +
                "      \"tokenBeginIndex\": 32,\n" +
                "      \"tokenEndIndex\": 33,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 18,\n" +
                "    \"tokenOffsetEnd\": 33,\n" +
                "    \"sentenceIndex\": 1,\n" +
                "    \"characterOffsetBegin\": 90,\n" +
                "    \"characterOffsetEnd\": 161,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"While\",\n" +
                "      \"value\": \"While\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"While\",\n" +
                "      \"beginChar\": 162,\n" +
                "      \"endChar\": 167,\n" +
                "      \"tokenBeginIndex\": 33,\n" +
                "      \"tokenEndIndex\": 34,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 168,\n" +
                "      \"endChar\": 173,\n" +
                "      \"tokenBeginIndex\": 34,\n" +
                "      \"tokenEndIndex\": 35,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cooks\",\n" +
                "      \"value\": \"cooks\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"cooks\",\n" +
                "      \"beginChar\": 174,\n" +
                "      \"endChar\": 179,\n" +
                "      \"tokenBeginIndex\": 35,\n" +
                "      \"tokenEndIndex\": 36,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 179,\n" +
                "      \"endChar\": 180,\n" +
                "      \"tokenBeginIndex\": 36,\n" +
                "      \"tokenEndIndex\": 37,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pour\",\n" +
                "      \"value\": \"pour\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pour\",\n" +
                "      \"beginChar\": 181,\n" +
                "      \"endChar\": 185,\n" +
                "      \"tokenBeginIndex\": 37,\n" +
                "      \"tokenEndIndex\": 38,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomatoes\",\n" +
                "      \"value\": \"tomatoes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tomatoes\",\n" +
                "      \"beginChar\": 186,\n" +
                "      \"endChar\": 194,\n" +
                "      \"tokenBeginIndex\": 38,\n" +
                "      \"tokenEndIndex\": 39,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"into\",\n" +
                "      \"value\": \"into\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"into\",\n" +
                "      \"beginChar\": 195,\n" +
                "      \"endChar\": 199,\n" +
                "      \"tokenBeginIndex\": 39,\n" +
                "      \"tokenEndIndex\": 40,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"a\",\n" +
                "      \"value\": \"a\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"a\",\n" +
                "      \"beginChar\": 200,\n" +
                "      \"endChar\": 201,\n" +
                "      \"tokenBeginIndex\": 40,\n" +
                "      \"tokenEndIndex\": 41,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"fine-mesh\",\n" +
                "      \"value\": \"fine-mesh\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"fine-mesh\",\n" +
                "      \"beginChar\": 202,\n" +
                "      \"endChar\": 211,\n" +
                "      \"tokenBeginIndex\": 41,\n" +
                "      \"tokenEndIndex\": 42,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"sieve\",\n" +
                "      \"value\": \"sieve\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"sieve\",\n" +
                "      \"beginChar\": 212,\n" +
                "      \"endChar\": 217,\n" +
                "      \"tokenBeginIndex\": 42,\n" +
                "      \"tokenEndIndex\": 43,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"set\",\n" +
                "      \"value\": \"set\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"set\",\n" +
                "      \"beginChar\": 218,\n" +
                "      \"endChar\": 221,\n" +
                "      \"tokenBeginIndex\": 43,\n" +
                "      \"tokenEndIndex\": 44,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"over\",\n" +
                "      \"value\": \"over\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"over\",\n" +
                "      \"beginChar\": 222,\n" +
                "      \"endChar\": 226,\n" +
                "      \"tokenBeginIndex\": 44,\n" +
                "      \"tokenEndIndex\": 45,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"a\",\n" +
                "      \"value\": \"a\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"a\",\n" +
                "      \"beginChar\": 227,\n" +
                "      \"endChar\": 228,\n" +
                "      \"tokenBeginIndex\": 45,\n" +
                "      \"tokenEndIndex\": 46,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" , "      \"word\": \"medium\",\n" +
                "      \"value\": \"medium\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"medium\",\n" +
                "      \"beginChar\": 229,\n" +
                "      \"endChar\": 235,\n" +
                "      \"tokenBeginIndex\": 46,\n" +
                "      \"tokenEndIndex\": 47,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"bowl\",\n" +
                "      \"value\": \"bowl\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"bowl\",\n" +
                "      \"beginChar\": 236,\n" +
                "      \"endChar\": 240,\n" +
                "      \"tokenBeginIndex\": 47,\n" +
                "      \"tokenEndIndex\": 48,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 240,\n" +
                "      \"endChar\": 241,\n" +
                "      \"tokenBeginIndex\": 48,\n" +
                "      \"tokenEndIndex\": 49,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 33,\n" +
                "    \"tokenOffsetEnd\": 49,\n" +
                "    \"sentenceIndex\": 2,\n" +
                "    \"characterOffsetBegin\": 162,\n" +
                "    \"characterOffsetEnd\": 241,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Shake\",\n" +
                "      \"value\": \"Shake\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Shake\",\n" +
                "      \"beginChar\": 242,\n" +
                "      \"endChar\": 247,\n" +
                "      \"tokenBeginIndex\": 49,\n" +
                "      \"tokenEndIndex\": 50,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 248,\n" +
                "      \"endChar\": 250,\n" +
                "      \"tokenBeginIndex\": 50,\n" +
                "      \"tokenEndIndex\": 51,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"release\",\n" +
                "      \"value\": \"release\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"release\",\n" +
                "      \"beginChar\": 251,\n" +
                "      \"endChar\": 258,\n" +
                "      \"tokenBeginIndex\": 51,\n" +
                "      \"tokenEndIndex\": 52,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"as\",\n" +
                "      \"value\": \"as\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"as\",\n" +
                "      \"beginChar\": 259,\n" +
                "      \"endChar\": 261,\n" +
                "      \"tokenBeginIndex\": 52,\n" +
                "      \"tokenEndIndex\": 53,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"much\",\n" +
                "      \"value\": \"much\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"much\",\n" +
                "      \"beginChar\": 262,\n" +
                "      \"endChar\": 266,\n" +
                "      \"tokenBeginIndex\": 53,\n" +
                "      \"tokenEndIndex\": 54,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"juice\",\n" +
                "      \"value\": \"juice\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"juice\",\n" +
                "      \"beginChar\": 267,\n" +
                "      \"endChar\": 272,\n" +
                "      \"tokenBeginIndex\": 54,\n" +
                "      \"tokenEndIndex\": 55,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"as\",\n" +
                "      \"value\": \"as\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"as\",\n" +
                "      \"beginChar\": 273,\n" +
                "      \"endChar\": 275,\n" +
                "      \"tokenBeginIndex\": 55,\n" +
                "      \"tokenEndIndex\": 56,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"possible\",\n" +
                "      \"value\": \"possible\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"possible\",\n" +
                "      \"beginChar\": 276,\n" +
                "      \"endChar\": 284,\n" +
                "      \"tokenBeginIndex\": 56,\n" +
                "      \"tokenEndIndex\": 57,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 284,\n" +
                "      \"endChar\": 285,\n" +
                "      \"tokenBeginIndex\": 57,\n" +
                "      \"tokenEndIndex\": 58,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"then\",\n" +
                "      \"value\": \"then\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"then\",\n" +
                "      \"beginChar\": 286,\n" +
                "      \"endChar\": 290,\n" +
                "      \"tokenBeginIndex\": 58,\n" +
                "      \"tokenEndIndex\": 59,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"let\",\n" +
                "      \"value\": \"let\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"let\",\n" +
                "      \"beginChar\": 291,\n" +
                "      \"endChar\": 294,\n" +
                "      \"tokenBeginIndex\": 59,\n" +
                "      \"tokenEndIndex\": 60,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomatoes\",\n" +
                "      \"value\": \"tomatoes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tomatoes\",\n" +
                "      \"beginChar\": 295,\n" +
                "      \"endChar\": 303,\n" +
                "      \"tokenBeginIndex\": 60,\n" +
                "      \"tokenEndIndex\": 61,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"drain\",\n" +
                "      \"value\": \"drain\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"drain\",\n" +
                "      \"beginChar\": 304,\n" +
                "      \"endChar\": 309,\n" +
                "      \"tokenBeginIndex\": 61,\n" +
                "      \"tokenEndIndex\": 62,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"in\",\n" +
                "      \"value\": \"in\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"in\",\n" +
                "      \"beginChar\": 310,\n" +
                "      \"endChar\": 312,\n" +
                "      \"tokenBeginIndex\": 62,\n" +
                "      \"tokenEndIndex\": 63,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"sieve\",\n" +
                "      \"value\": \"sieve\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"sieve\",\n" +
                "      \"beginChar\": 313,\n" +
                "      \"endChar\": 318,\n" +
                "      \"tokenBeginIndex\": 63,\n" +
                "      \"tokenEndIndex\": 64,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 318,\n" +
                "      \"endChar\": 319,\n" +
                "      \"tokenBeginIndex\": 64,\n" +
                "      \"tokenEndIndex\": 65,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"collecting\",\n" +
                "      \"value\": \"collecting\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"collecting\",\n" +
                "      \"beginChar\": 320,\n" +
                "      \"endChar\": 330,\n" +
                "      \"tokenBeginIndex\": 65,\n" +
                "      \"tokenEndIndex\": 66,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"juices\",\n" +
                "      \"value\": \"juices\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"juices\",\n" +
                "      \"beginChar\": 331,\n" +
                "      \"endChar\": 337,\n" +
                "      \"tokenBeginIndex\": 66,\n" +
                "      \"tokenEndIndex\": 67,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"in\",\n" +
                "      \"value\": \"in\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"in\",\n" +
                "      \"beginChar\": 338,\n" +
                "      \"endChar\": 340,\n" +
                "      \"tokenBeginIndex\": 67,\n" +
                "      \"tokenEndIndex\": 68,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"bowl\",\n" +
                "      \"value\": \"bowl\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"bowl\",\n" +
                "      \"beginChar\": 341,\n" +
                "      \"endChar\": 345,\n" +
                "      \"tokenBeginIndex\": 68,\n" +
                "      \"tokenEndIndex\": 69,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 345,\n" +
                "      \"endChar\": 346,\n" +
                "      \"tokenBeginIndex\": 69,\n" +
                "      \"tokenEndIndex\": 70,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"until\",\n" +
                "      \"value\": \"until\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"until\",\n" +
                "      \"beginChar\": 347,\n" +
                "      \"endChar\": 352,\n" +
                "      \"tokenBeginIndex\": 70,\n" +
                "      \"tokenEndIndex\": 71,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"ready\",\n" +
                "      \"value\": \"ready\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"ready\",\n" +
                "      \"beginChar\": 353,\n" +
                "      \"endChar\": 358,\n" +
                "      \"tokenBeginIndex\": 71,\n" +
                "      \"tokenEndIndex\": 72,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 359,\n" +
                "      \"endChar\": 361,\n" +
                "      \"tokenBeginIndex\": 72,\n" +
                "      \"tokenEndIndex\": 73,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"use\",\n" +
                "      \"value\": \"use\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"use\",\n" +
                "      \"beginChar\": 362,\n" +
                "      \"endChar\": 365,\n" +
                "      \"tokenBeginIndex\": 73,\n" +
                "      \"tokenEndIndex\": 74,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 365,\n" +
                "      \"endChar\": 366,\n" +
                "      \"tokenBeginIndex\": 74,\n" +
                "      \"tokenEndIndex\": 75,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 49,\n" +
                "    \"tokenOffsetEnd\": 75,\n" +
                "    \"sentenceIndex\": 3,\n" +
                "    \"characterOffsetBegin\": 242,\n" +
                "    \"characterOffsetEnd\": 366,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Heat\",\n" +
                "      \"value\": \"Heat\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Heat\",\n" +
                "      \"beginChar\": 367,\n" +
                "      \"endChar\": 371,\n" +
                "      \"tokenBeginIndex\": 75,\n" +
                "      \"tokenEndIndex\": 76,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1/4\",\n" +
                "      \"value\": \"1/4\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1/4\",\n" +
                "      \"beginChar\": 372,\n" +
                "      \"endChar\": 375,\n" +
                "      \"tokenBeginIndex\": 76,\n" +
                "      \"tokenEndIndex\": 77,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 376,\n" +
                "      \"endChar\": 379,\n" +
                "      \"tokenBeginIndex\": 77,\n" +
                "      \"tokenEndIndex\": 78,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oil\",\n" +
                "      \"value\": \"oil\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"oil\",\n" +
                "      \"beginChar\": 380,\n" +
                "      \"endChar\": 383,\n" +
                "      \"tokenBeginIndex\": 78,\n" +
                "      \"tokenEndIndex\": 79,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"in\",\n" +
                "      \"value\": \"in\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"in\",\n" +
                "      \"beginChar\": 384,\n" +
                "      \"endChar\": 386,\n" +
                "      \"tokenBeginIndex\": 79,\n" +
                "      \"tokenEndIndex\": 80,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"a\",\n" +
                "      \"value\": \"a\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"a\",\n" +
                "      \"beginChar\": 387,\n" +
                "      \"endChar\": 388,\n" +
                "      \"tokenBeginIndex\": 80,\n" +
                "      \"tokenEndIndex\": 81,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"large\",\n" +
                "      \"value\": \"large\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"large\",\n" +
                "      \"beginChar\": 389,\n" +
                "      \"endChar\": 394,\n" +
                "      \"tokenBeginIndex\": 81,\n" +
                "      \"tokenEndIndex\": 82,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"deep-sided\",\n" +
                "      \"value\": \"deep-sided\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"deep-sided\",\n" +
                "      \"beginChar\": 395,\n" +
                "      \"endChar\": 405,\n" +
                "      \"tokenBeginIndex\": 82,\n" +
                "      \"tokenEndIndex\": 83,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"skillet\",\n" +
                "      \"value\": \"skillet\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"skillet\",\n" +
                "      \"beginChar\": 406,\n" +
                "      \"endChar\": 413,\n" +
                "      \"tokenBeginIndex\": 83,\n" +
                "      \"tokenEndIndex\": 84,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"over\",\n" +
                "      \"value\": \"over\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"over\",\n" +
                "      \"beginChar\": 414,\n" +
                "      \"endChar\": 418,\n" +
                "      \"tokenBeginIndex\": 84,\n" +
                "      \"tokenEndIndex\": 85,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"medium-high\",\n" +
                "      \"value\": \"medium-high\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"medium-high\",\n" +
                "      \"beginChar\": 419,\n" +
                "      \"endChar\": 430,\n" +
                "      \"tokenBeginIndex\": 85,\n" +
                "      \"tokenEndIndex\": 86,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 430,\n" +
                "      \"endChar\": 431,\n" +
                "      \"tokenBeginIndex\": 86,\n" +
                "      \"tokenEndIndex\": 87,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 75,\n" +
                "    \"tokenOffsetEnd\": 87,\n" +
                "    \"sentenceIndex\": 4,\n" +
                "    \"characterOffsetBegin\": 367,\n" +
                "    \"characterOffsetEnd\": 431,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Add\",\n" +
                "      \"value\": \"Add\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Add\",\n" +
                "      \"beginChar\": 432,\n" +
                "      \"endChar\": 435,\n" +
                "      \"tokenBeginIndex\": 87,\n" +
                "      \"tokenEndIndex\": 88,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"capers\",\n" +
                "      \"value\": \"capers\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"capers\",\n" +
                "      \"beginChar\": 436,\n" +
                "      \"endChar\": 442,\n" +
                "      \"tokenBeginIndex\": 88,\n" +
                "      \"tokenEndIndex\": 89,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 443,\n" +
                "      \"endChar\": 446,\n" +
                "      \"tokenBeginIndex\": 89,\n" +
                "      \"tokenEndIndex\": 90,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cook\",\n" +
                "      \"value\": \"cook\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"cook\",\n" +
                "      \"beginChar\": 447,\n" +
                "      \"endChar\": 451,\n" +
                "      \"tokenBeginIndex\": 90,\n" +
                "      \"tokenEndIndex\": 91,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 451,\n" +
                "      \"endChar\": 452,\n" +
                "      \"tokenBeginIndex\": 91,\n" +
                "      \"tokenEndIndex\": 92,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"swirling\",\n" +
                "      \"value\": \"swirling\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"swirling\",\n" +
                "      \"beginChar\": 453,\n" +
                "      \"endChar\": 461,\n" +
                "      \"tokenBeginIndex\": 92,\n" +
                "      \"tokenEndIndex\": 93,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pan\",\n" +
                "      \"value\": \"pan\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pan\",\n" +
                "      \"beginChar\": 462,\n" +
                "      \"endChar\": 465,\n" +
                "      \"tokenBeginIndex\": 93,\n" +
                "      \"tokenEndIndex\": 94,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"occasionally\",\n" +
                "      \"value\": \"occasionally\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"occasionally\",\n" +
                "      \"beginChar\": 466,\n" +
                "      \"endChar\": 478,\n" +
                "      \"tokenBeginIndex\": 94,\n" +
                "      \"tokenEndIndex\": 95,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 478,\n" +
                "      \"endChar\": 479,\n" +
                "      \"tokenBeginIndex\": 95,\n" +
                "      \"tokenEndIndex\": 96,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"until\",\n" +
                "      \"value\": \"until\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"until\",\n" +
                "      \"beginChar\": 480,\n" +
                "      \"endChar\": 485,\n" +
                "      \"tokenBeginIndex\": 96,\n" +
                "      \"tokenEndIndex\": 97,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"they\",\n" +
                "      \"value\": \"they\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"they\",\n" +
                "      \"beginChar\": 486,\n" +
                "      \"endChar\": 490,\n" +
                "      \"tokenBeginIndex\": 97,\n" +
                "      \"tokenEndIndex\": 98,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" , "      \"word\": \"burst\",\n" +
                "      \"value\": \"burst\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"burst\",\n" +
                "      \"beginChar\": 491,\n" +
                "      \"endChar\": 496,\n" +
                "      \"tokenBeginIndex\": 98,\n" +
                "      \"tokenEndIndex\": 99,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 497,\n" +
                "      \"endChar\": 500,\n" +
                "      \"tokenBeginIndex\": 99,\n" +
                "      \"tokenEndIndex\": 100,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"are\",\n" +
                "      \"value\": \"are\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"are\",\n" +
                "      \"beginChar\": 501,\n" +
                "      \"endChar\": 504,\n" +
                "      \"tokenBeginIndex\": 100,\n" +
                "      \"tokenEndIndex\": 101,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"crisp\",\n" +
                "      \"value\": \"crisp\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"crisp\",\n" +
                "      \"beginChar\": 505,\n" +
                "      \"endChar\": 510,\n" +
                "      \"tokenBeginIndex\": 101,\n" +
                "      \"tokenEndIndex\": 102,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 510,\n" +
                "      \"endChar\": 511,\n" +
                "      \"tokenBeginIndex\": 102,\n" +
                "      \"tokenEndIndex\": 103,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"about\",\n" +
                "      \"value\": \"about\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"about\",\n" +
                "      \"beginChar\": 512,\n" +
                "      \"endChar\": 517,\n" +
                "      \"tokenBeginIndex\": 103,\n" +
                "      \"tokenEndIndex\": 104,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"3\",\n" +
                "      \"value\": \"3\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"3\",\n" +
                "      \"beginChar\": 518,\n" +
                "      \"endChar\": 519,\n" +
                "      \"tokenBeginIndex\": 104,\n" +
                "      \"tokenEndIndex\": 105,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"minutes\",\n" +
                "      \"value\": \"minutes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"minutes\",\n" +
                "      \"beginChar\": 520,\n" +
                "      \"endChar\": 527,\n" +
                "      \"tokenBeginIndex\": 105,\n" +
                "      \"tokenEndIndex\": 106,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 527,\n" +
                "      \"endChar\": 528,\n" +
                "      \"tokenBeginIndex\": 106,\n" +
                "      \"tokenEndIndex\": 107,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 87,\n" +
                "    \"tokenOffsetEnd\": 107,\n" +
                "    \"sentenceIndex\": 5,\n" +
                "    \"characterOffsetBegin\": 432,\n" +
                "    \"characterOffsetEnd\": 528,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Using\",\n" +
                "      \"value\": \"Using\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Using\",\n" +
                "      \"beginChar\": 529,\n" +
                "      \"endChar\": 534,\n" +
                "      \"tokenBeginIndex\": 107,\n" +
                "      \"tokenEndIndex\": 108,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"a\",\n" +
                "      \"value\": \"a\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"a\",\n" +
                "      \"beginChar\": 535,\n" +
                "      \"endChar\": 536,\n" +
                "      \"tokenBeginIndex\": 108,\n" +
                "      \"tokenEndIndex\": 109,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"slotted\",\n" +
                "      \"value\": \"slotted\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"slotted\",\n" +
                "      \"beginChar\": 537,\n" +
                "      \"endChar\": 544,\n" +
                "      \"tokenBeginIndex\": 109,\n" +
                "      \"tokenEndIndex\": 110,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"spoon\",\n" +
                "      \"value\": \"spoon\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"spoon\",\n" +
                "      \"beginChar\": 545,\n" +
                "      \"endChar\": 550,\n" +
                "      \"tokenBeginIndex\": 110,\n" +
                "      \"tokenEndIndex\": 111,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 550,\n" +
                "      \"endChar\": 551,\n" +
                "      \"tokenBeginIndex\": 111,\n" +
                "      \"tokenEndIndex\": 112,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"transfer\",\n" +
                "      \"value\": \"transfer\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"transfer\",\n" +
                "      \"beginChar\": 552,\n" +
                "      \"endChar\": 560,\n" +
                "      \"tokenBeginIndex\": 112,\n" +
                "      \"tokenEndIndex\": 113,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"capers\",\n" +
                "      \"value\": \"capers\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"capers\",\n" +
                "      \"beginChar\": 561,\n" +
                "      \"endChar\": 567,\n" +
                "      \"tokenBeginIndex\": 113,\n" +
                "      \"tokenEndIndex\": 114,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 568,\n" +
                "      \"endChar\": 570,\n" +
                "      \"tokenBeginIndex\": 114,\n" +
                "      \"tokenEndIndex\": 115,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"a\",\n" +
                "      \"value\": \"a\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"a\",\n" +
                "      \"beginChar\": 571,\n" +
                "      \"endChar\": 572,\n" +
                "      \"tokenBeginIndex\": 115,\n" +
                "      \"tokenEndIndex\": 116,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"paper\",\n" +
                "      \"value\": \"paper\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"paper\",\n" +
                "      \"beginChar\": 573,\n" +
                "      \"endChar\": 578,\n" +
                "      \"tokenBeginIndex\": 116,\n" +
                "      \"tokenEndIndex\": 117,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"towel-lined\",\n" +
                "      \"value\": \"towel-lined\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"towel-lined\",\n" +
                "      \"beginChar\": 579,\n" +
                "      \"endChar\": 590,\n" +
                "      \"tokenBeginIndex\": 117,\n" +
                "      \"tokenEndIndex\": 118,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"plate\",\n" +
                "      \"value\": \"plate\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"plate\",\n" +
                "      \"beginChar\": 591,\n" +
                "      \"endChar\": 596,\n" +
                "      \"tokenBeginIndex\": 118,\n" +
                "      \"tokenEndIndex\": 119,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 596,\n" +
                "      \"endChar\": 597,\n" +
                "      \"tokenBeginIndex\": 119,\n" +
                "      \"tokenEndIndex\": 120,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"reserving\",\n" +
                "      \"value\": \"reserving\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"reserving\",\n" +
                "      \"beginChar\": 598,\n" +
                "      \"endChar\": 607,\n" +
                "      \"tokenBeginIndex\": 120,\n" +
                "      \"tokenEndIndex\": 121,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oil\",\n" +
                "      \"value\": \"oil\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"oil\",\n" +
                "      \"beginChar\": 608,\n" +
                "      \"endChar\": 611,\n" +
                "      \"tokenBeginIndex\": 121,\n" +
                "      \"tokenEndIndex\": 122,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"in\",\n" +
                "      \"value\": \"in\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"in\",\n" +
                "      \"beginChar\": 612,\n" +
                "      \"endChar\": 614,\n" +
                "      \"tokenBeginIndex\": 122,\n" +
                "      \"tokenEndIndex\": 123,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"skillet\",\n" +
                "      \"value\": \"skillet\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"skillet\",\n" +
                "      \"beginChar\": 615,\n" +
                "      \"endChar\": 622,\n" +
                "      \"tokenBeginIndex\": 123,\n" +
                "      \"tokenEndIndex\": 124,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 622,\n" +
                "      \"endChar\": 623,\n" +
                "      \"tokenBeginIndex\": 124,\n" +
                "      \"tokenEndIndex\": 125,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 107,\n" +
                "    \"tokenOffsetEnd\": 125,\n" +
                "    \"sentenceIndex\": 6,\n" +
                "    \"characterOffsetBegin\": 529,\n" +
                "    \"characterOffsetEnd\": 623,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Combine\",\n" +
                "      \"value\": \"Combine\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Combine\",\n" +
                "      \"beginChar\": 624,\n" +
                "      \"endChar\": 631,\n" +
                "      \"tokenBeginIndex\": 125,\n" +
                "      \"tokenEndIndex\": 126,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"anchovies\",\n" +
                "      \"value\": \"anchovies\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"anchovies\",\n" +
                "      \"beginChar\": 632,\n" +
                "      \"endChar\": 641,\n" +
                "      \"tokenBeginIndex\": 126,\n" +
                "      \"tokenEndIndex\": 127,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 641,\n" +
                "      \"endChar\": 642,\n" +
                "      \"tokenBeginIndex\": 127,\n" +
                "      \"tokenEndIndex\": 128,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomato\",\n" +
                "      \"value\": \"tomato\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tomato\",\n" +
                "      \"beginChar\": 643,\n" +
                "      \"endChar\": 649,\n" +
                "      \"tokenBeginIndex\": 128,\n" +
                "      \"tokenEndIndex\": 129,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"paste\",\n" +
                "      \"value\": \"paste\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"paste\",\n" +
                "      \"beginChar\": 650,\n" +
                "      \"endChar\": 655,\n" +
                "      \"tokenBeginIndex\": 129,\n" +
                "      \"tokenEndIndex\": 130,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 655,\n" +
                "      \"endChar\": 656,\n" +
                "      \"tokenBeginIndex\": 130,\n" +
                "      \"tokenEndIndex\": 131,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 657,\n" +
                "      \"endChar\": 660,\n" +
                "      \"tokenBeginIndex\": 131,\n" +
                "      \"tokenEndIndex\": 132,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"drained\",\n" +
                "      \"value\": \"drained\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"drained\",\n" +
                "      \"beginChar\": 661,\n" +
                "      \"endChar\": 668,\n" +
                "      \"tokenBeginIndex\": 132,\n" +
                "      \"tokenEndIndex\": 133,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomatoes\",\n" +
                "      \"value\": \"tomatoes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tomatoes\",\n" +
                "      \"beginChar\": 669,\n" +
                "      \"endChar\": 677,\n" +
                "      \"tokenBeginIndex\": 133,\n" +
                "      \"tokenEndIndex\": 134,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"in\",\n" +
                "      \"value\": \"in\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"in\",\n" +
                "      \"beginChar\": 678,\n" +
                "      \"endChar\": 680,\n" +
                "      \"tokenBeginIndex\": 134,\n" +
                "      \"tokenEndIndex\": 135,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"skillet\",\n" +
                "      \"value\": \"skillet\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"skillet\",\n" +
                "      \"beginChar\": 681,\n" +
                "      \"endChar\": 688,\n" +
                "      \"tokenBeginIndex\": 135,\n" +
                "      \"tokenEndIndex\": 136,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" ,  "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 688,\n" +
                "      \"endChar\": 689,\n" +
                "      \"tokenBeginIndex\": 136,\n" +
                "      \"tokenEndIndex\": 137,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 125,\n" +
                "    \"tokenOffsetEnd\": 137,\n" +
                "    \"sentenceIndex\": 7,\n" +
                "    \"characterOffsetBegin\": 624,\n" +
                "    \"characterOffsetEnd\": 689,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Cook\",\n" +
                "      \"value\": \"Cook\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Cook\",\n" +
                "      \"beginChar\": 690,\n" +
                "      \"endChar\": 694,\n" +
                "      \"tokenBeginIndex\": 137,\n" +
                "      \"tokenEndIndex\": 138,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"over\",\n" +
                "      \"value\": \"over\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"over\",\n" +
                "      \"beginChar\": 695,\n" +
                "      \"endChar\": 699,\n" +
                "      \"tokenBeginIndex\": 138,\n" +
                "      \"tokenEndIndex\": 139,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"medium-high\",\n" +
                "      \"value\": \"medium-high\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"medium-high\",\n" +
                "      \"beginChar\": 700,\n" +
                "      \"endChar\": 711,\n" +
                "      \"tokenBeginIndex\": 139,\n" +
                "      \"tokenEndIndex\": 140,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"heat\",\n" +
                "      \"value\": \"heat\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"heat\",\n" +
                "      \"beginChar\": 712,\n" +
                "      \"endChar\": 716,\n" +
                "      \"tokenBeginIndex\": 140,\n" +
                "      \"tokenEndIndex\": 141,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 716,\n" +
                "      \"endChar\": 717,\n" +
                "      \"tokenBeginIndex\": 141,\n" +
                "      \"tokenEndIndex\": 142,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"stirring\",\n" +
                "      \"value\": \"stirring\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"stirring\",\n" +
                "      \"beginChar\": 718,\n" +
                "      \"endChar\": 726,\n" +
                "      \"tokenBeginIndex\": 142,\n" +
                "      \"tokenEndIndex\": 143,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"occasionally\",\n" +
                "      \"value\": \"occasionally\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"occasionally\",\n" +
                "      \"beginChar\": 727,\n" +
                "      \"endChar\": 739,\n" +
                "      \"tokenBeginIndex\": 143,\n" +
                "      \"tokenEndIndex\": 144,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 739,\n" +
                "      \"endChar\": 740,\n" +
                "      \"tokenBeginIndex\": 144,\n" +
                "      \"tokenEndIndex\": 145,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"until\",\n" +
                "      \"value\": \"until\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"until\",\n" +
                "      \"beginChar\": 741,\n" +
                "      \"endChar\": 746,\n" +
                "      \"tokenBeginIndex\": 145,\n" +
                "      \"tokenEndIndex\": 146,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomatoes\",\n" +
                "      \"value\": \"tomatoes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tomatoes\",\n" +
                "      \"beginChar\": 747,\n" +
                "      \"endChar\": 755,\n" +
                "      \"tokenBeginIndex\": 146,\n" +
                "      \"tokenEndIndex\": 147,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"begin\",\n" +
                "      \"value\": \"begin\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"begin\",\n" +
                "      \"beginChar\": 756,\n" +
                "      \"endChar\": 761,\n" +
                "      \"tokenBeginIndex\": 147,\n" +
                "      \"tokenEndIndex\": 148,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 762,\n" +
                "      \"endChar\": 764,\n" +
                "      \"tokenBeginIndex\": 148,\n" +
                "      \"tokenEndIndex\": 149,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"caramelize\",\n" +
                "      \"value\": \"caramelize\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"caramelize\",\n" +
                "      \"beginChar\": 765,\n" +
                "      \"endChar\": 775,\n" +
                "      \"tokenBeginIndex\": 149,\n" +
                "      \"tokenEndIndex\": 150,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 776,\n" +
                "      \"endChar\": 779,\n" +
                "      \"tokenBeginIndex\": 150,\n" +
                "      \"tokenEndIndex\": 151,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"anchovies\",\n" +
                "      \"value\": \"anchovies\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"anchovies\",\n" +
                "      \"beginChar\": 780,\n" +
                "      \"endChar\": 789,\n" +
                "      \"tokenBeginIndex\": 151,\n" +
                "      \"tokenEndIndex\": 152,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"start\",\n" +
                "      \"value\": \"start\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"start\",\n" +
                "      \"beginChar\": 790,\n" +
                "      \"endChar\": 795,\n" +
                "      \"tokenBeginIndex\": 152,\n" +
                "      \"tokenEndIndex\": 153,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 796,\n" +
                "      \"endChar\": 798,\n" +
                "      \"tokenBeginIndex\": 153,\n" +
                "      \"tokenEndIndex\": 154,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"break\",\n" +
                "      \"value\": \"break\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"break\",\n" +
                "      \"beginChar\": 799,\n" +
                "      \"endChar\": 804,\n" +
                "      \"tokenBeginIndex\": 154,\n" +
                "      \"tokenEndIndex\": 155,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"down\",\n" +
                "      \"value\": \"down\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"down\",\n" +
                "      \"beginChar\": 805,\n" +
                "      \"endChar\": 809,\n" +
                "      \"tokenBeginIndex\": 155,\n" +
                "      \"tokenEndIndex\": 156,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" ,"      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 809,\n" +
                "      \"endChar\": 810,\n" +
                "      \"tokenBeginIndex\": 156,\n" +
                "      \"tokenEndIndex\": 157,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"about\",\n" +
                "      \"value\": \"about\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"about\",\n" +
                "      \"beginChar\": 811,\n" +
                "      \"endChar\": 816,\n" +
                "      \"tokenBeginIndex\": 157,\n" +
                "      \"tokenEndIndex\": 158,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"5\",\n" +
                "      \"value\": \"5\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"5\",\n" +
                "      \"beginChar\": 817,\n" +
                "      \"endChar\": 818,\n" +
                "      \"tokenBeginIndex\": 158,\n" +
                "      \"tokenEndIndex\": 159,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"minutes\",\n" +
                "      \"value\": \"minutes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"minutes\",\n" +
                "      \"beginChar\": 819,\n" +
                "      \"endChar\": 826,\n" +
                "      \"tokenBeginIndex\": 159,\n" +
                "      \"tokenEndIndex\": 160,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 826,\n" +
                "      \"endChar\": 827,\n" +
                "      \"tokenBeginIndex\": 160,\n" +
                "      \"tokenEndIndex\": 161,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 137,\n" +
                "    \"tokenOffsetEnd\": 161,\n" +
                "    \"sentenceIndex\": 8,\n" +
                "    \"characterOffsetBegin\": 690,\n" +
                "    \"characterOffsetEnd\": 827,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Add\",\n" +
                "      \"value\": \"Add\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Add\",\n" +
                "      \"beginChar\": 828,\n" +
                "      \"endChar\": 831,\n" +
                "      \"tokenBeginIndex\": 161,\n" +
                "      \"tokenEndIndex\": 162,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"collected\",\n" +
                "      \"value\": \"collected\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"collected\",\n" +
                "      \"beginChar\": 832,\n" +
                "      \"endChar\": 841,\n" +
                "      \"tokenBeginIndex\": 162,\n" +
                "      \"tokenEndIndex\": 163,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tomato\",\n" +
                "      \"value\": \"tomato\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tomato\",\n" +
                "      \"beginChar\": 842,\n" +
                "      \"endChar\": 848,\n" +
                "      \"tokenBeginIndex\": 163,\n" +
                "      \"tokenEndIndex\": 164,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"juices\",\n" +
                "      \"value\": \"juices\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"juices\",\n" +
                "      \"beginChar\": 849,\n" +
                "      \"endChar\": 855,\n" +
                "      \"tokenBeginIndex\": 164,\n" +
                "      \"tokenEndIndex\": 165,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 855,\n" +
                "      \"endChar\": 856,\n" +
                "      \"tokenBeginIndex\": 165,\n" +
                "      \"tokenEndIndex\": 166,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"olives\",\n" +
                "      \"value\": \"olives\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"olives\",\n" +
                "      \"beginChar\": 857,\n" +
                "      \"endChar\": 863,\n" +
                "      \"tokenBeginIndex\": 166,\n" +
                "      \"tokenEndIndex\": 167,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 863,\n" +
                "      \"endChar\": 864,\n" +
                "      \"tokenBeginIndex\": 167,\n" +
                "      \"tokenEndIndex\": 168,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oregano\",\n" +
                "      \"value\": \"oregano\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"oregano\",\n" +
                "      \"beginChar\": 865,\n" +
                "      \"endChar\": 872,\n" +
                "      \"tokenBeginIndex\": 168,\n" +
                "      \"tokenEndIndex\": 169,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 872,\n" +
                "      \"endChar\": 873,\n" +
                "      \"tokenBeginIndex\": 169,\n" +
                "      \"tokenEndIndex\": 170,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 874,\n" +
                "      \"endChar\": 877,\n" +
                "      \"tokenBeginIndex\": 170,\n" +
                "      \"tokenEndIndex\": 171,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"red\",\n" +
                "      \"value\": \"red\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"red\",\n" +
                "      \"beginChar\": 878,\n" +
                "      \"endChar\": 881,\n" +
                "      \"tokenBeginIndex\": 171,\n" +
                "      \"tokenEndIndex\": 172,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pepper\",\n" +
                "      \"value\": \"pepper\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pepper\",\n" +
                "      \"beginChar\": 882,\n" +
                "      \"endChar\": 888,\n" +
                "      \"tokenBeginIndex\": 172,\n" +
                "      \"tokenEndIndex\": 173,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"flakes\",\n" +
                "      \"value\": \"flakes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"flakes\",\n" +
                "      \"beginChar\": 889,\n" +
                "      \"endChar\": 895,\n" +
                "      \"tokenBeginIndex\": 173,\n" +
                "      \"tokenEndIndex\": 174,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 896,\n" +
                "      \"endChar\": 899,\n" +
                "      \"tokenBeginIndex\": 174,\n" +
                "      \"tokenEndIndex\": 175,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"bring\",\n" +
                "      \"value\": \"bring\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"bring\",\n" +
                "      \"beginChar\": 900,\n" +
                "      \"endChar\": 905,\n" +
                "      \"tokenBeginIndex\": 175,\n" +
                "      \"tokenEndIndex\": 176,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 906,\n" +
                "      \"endChar\": 908,\n" +
                "      \"tokenBeginIndex\": 176,\n" +
                "      \"tokenEndIndex\": 177,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"a\",\n" +
                "      \"value\": \"a\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"a\",\n" +
                "      \"beginChar\": 909,\n" +
                "      \"endChar\": 910,\n" +
                "      \"tokenBeginIndex\": 177,\n" +
                "      \"tokenEndIndex\": 178,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"simmer\",\n" +
                "      \"value\": \"simmer\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"simmer\",\n" +
                "      \"beginChar\": 911,\n" +
                "      \"endChar\": 917,\n" +
                "      \"tokenBeginIndex\": 178,\n" +
                "      \"tokenEndIndex\": 179,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 917,\n" +
                "      \"endChar\": 918,\n" +
                "      \"tokenBeginIndex\": 179,\n" +
                "      \"tokenEndIndex\": 180,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 161,\n" +
                "    \"tokenOffsetEnd\": 180,\n" +
                "    \"sentenceIndex\": 9,\n" +
                "    \"characterOffsetBegin\": 828,\n" +
                "    \"characterOffsetEnd\": 918,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Cook\",\n" +
                "      \"value\": \"Cook\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"Cook\",\n" +
                "      \"beginChar\": 919,\n" +
                "      \"endChar\": 923,\n" +
                "      \"tokenBeginIndex\": 180,\n" +
                "      \"tokenEndIndex\": 181,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 923,\n" +
                "      \"endChar\": 924,\n" +
                "      \"tokenBeginIndex\": 181,\n" +
                "      \"tokenEndIndex\": 182,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"stirring\",\n" +
                "      \"value\": \"stirring\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"stirring\",\n" +
                "      \"beginChar\": 925,\n" +
                "      \"endChar\": 933,\n" +
                "      \"tokenBeginIndex\": 182,\n" +
                "      \"tokenEndIndex\": 183,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"occasionally\",\n" +
                "      \"value\": \"occasionally\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"occasionally\",\n" +
                "      \"beginChar\": 934,\n" +
                "      \"endChar\": 946,\n" +
                "      \"tokenBeginIndex\": 183,\n" +
                "      \"tokenEndIndex\": 184,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 946,\n" +
                "      \"endChar\": 947,\n" +
                "      \"tokenBeginIndex\": 184,\n" +
                "      \"tokenEndIndex\": 185,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"until\",\n" +
                "      \"value\": \"until\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"until\",\n" +
                "      \"beginChar\": 948,\n" +
                "      \"endChar\": 953,\n" +
                "      \"tokenBeginIndex\": 185,\n" +
                "      \"tokenEndIndex\": 186,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"sauce\",\n" +
                "      \"value\": \"sauce\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"sauce\",\n" +
                "      \"beginChar\": 954,\n" +
                "      \"endChar\": 959,\n" +
                "      \"tokenBeginIndex\": 186,\n" +
                "      \"tokenEndIndex\": 187,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"is\",\n" +
                "      \"value\": \"is\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"is\",\n" +
                "      \"beginChar\": 960,\n" +
                "      \"endChar\": 962,\n" +
                "      \"tokenBeginIndex\": 187,\n" +
                "      \"tokenEndIndex\": 188,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"slightly\",\n" +
                "      \"value\": \"slightly\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"slightly\",\n" +
                "      \"beginChar\": 963,\n" +
                "      \"endChar\": 971,\n" +
                "      \"tokenBeginIndex\": 188,\n" +
                "      \"tokenEndIndex\": 189,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"thickened\",\n" +
                "      \"value\": \"thickened\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"thickened\",\n" +
                "      \"beginChar\": 972,\n" +
                "      \"endChar\": 981,\n" +
                "      \"tokenBeginIndex\": 189,\n" +
                "      \"tokenEndIndex\": 190,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 981,\n" +
                "      \"endChar\": 982,\n" +
                "      \"tokenBeginIndex\": 190,\n" +
                "      \"tokenEndIndex\": 191,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"about\",\n" +
                "      \"value\": \"about\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"about\",\n" +
                "      \"beginChar\": 983,\n" +
                "      \"endChar\": 988,\n" +
                "      \"tokenBeginIndex\": 191,\n" +
                "      \"tokenEndIndex\": 192,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"5\",\n" +
                "      \"value\": \"5\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"5\",\n" +
                "      \"beginChar\": 989,\n" +
                "      \"endChar\": 990,\n" +
                "      \"tokenBeginIndex\": 192,\n" +
                "      \"tokenEndIndex\": 193,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"minutes\",\n" +
                "      \"value\": \"minutes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"minutes\",\n" +
                "      \"beginChar\": 991,\n" +
                "      \"endChar\": 998,\n" +
                "      \"tokenBeginIndex\": 193,\n" +
                "      \"tokenEndIndex\": 194,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 998,\n" +
                "      \"endChar\": 999,\n" +
                "      \"tokenBeginIndex\": 194,\n" +
                "      \"tokenEndIndex\": 195,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 180,\n" +
                "    \"tokenOffsetEnd\": 195,\n" +
                "    \"sentenceIndex\": 10,\n" +
                "    \"characterOffsetBegin\": 919,\n" +
                "    \"characterOffsetEnd\": 999,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Add\",\n" +
                "      \"value\": \"Add\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Add\",\n" +
                "      \"beginChar\": 1000,\n" +
                "      \"endChar\": 1003,\n" +
                "      \"tokenBeginIndex\": 195,\n" +
                "      \"tokenEndIndex\": 196,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 1004,\n" +
                "      \"endChar\": 1009,\n" +
                "      \"tokenBeginIndex\": 196,\n" +
                "      \"tokenEndIndex\": 197,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 1009,\n" +
                "      \"endChar\": 1010,\n" +
                "      \"tokenBeginIndex\": 197,\n" +
                "      \"tokenEndIndex\": 198,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"remaining\",\n" +
                "      \"value\": \"remaining\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"remaining\",\n" +
                "      \"beginChar\": 1011,\n" +
                "      \"endChar\": 1020,\n" +
                "      \"tokenBeginIndex\": 198,\n" +
                "      \"tokenEndIndex\": 199,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1/4\",\n" +
                "      \"value\": \"1/4\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1/4\",\n" +
                "      \"beginChar\": 1021,\n" +
                "      \"endChar\": 1024,\n" +
                "      \"tokenBeginIndex\": 199,\n" +
                "      \"tokenEndIndex\": 200,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 1025,\n" +
                "      \"endChar\": 1028,\n" +
                "      \"tokenBeginIndex\": 200,\n" +
                "      \"tokenEndIndex\": 201,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"oil\",\n" +
                "      \"value\": \"oil\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"oil\",\n" +
                "      \"beginChar\": 1029,\n" +
                "      \"endChar\": 1032,\n" +
                "      \"tokenBeginIndex\": 201,\n" +
                "      \"tokenEndIndex\": 202,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 1032,\n" +
                "      \"endChar\": 1033,\n" +
                "      \"tokenBeginIndex\": 202,\n" +
                "      \"tokenEndIndex\": 203,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 1034,\n" +
                "      \"endChar\": 1037,\n" +
                "      \"tokenBeginIndex\": 203,\n" +
                "      \"tokenEndIndex\": 204,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"3/4\",\n" +
                "      \"value\": \"3/4\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"3/4\",\n" +
                "      \"beginChar\": 1038,\n" +
                "      \"endChar\": 1041,\n" +
                "      \"tokenBeginIndex\": 204,\n" +
                "      \"tokenEndIndex\": 205,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 1042,\n" +
                "      \"endChar\": 1045,\n" +
                "      \"tokenBeginIndex\": 205,\n" +
                "      \"tokenEndIndex\": 206,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 1046,\n" +
                "      \"endChar\": 1051,\n" +
                "      \"tokenBeginIndex\": 206,\n" +
                "      \"tokenEndIndex\": 207,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cooking\",\n" +
                "      \"value\": \"cooking\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cooking\",\n" +
                "      \"beginChar\": 1052,\n" +
                "      \"endChar\": 1059,\n" +
                "      \"tokenBeginIndex\": 207,\n" +
                "      \"tokenEndIndex\": 208,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"liquid\",\n" +
                "      \"value\": \"liquid\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"liquid\",\n" +
                "      \"beginChar\": 1060,\n" +
                "      \"endChar\": 1066,\n" +
                "      \"tokenBeginIndex\": 208,\n" +
                "      \"tokenEndIndex\": 209,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 1067,\n" +
                "      \"endChar\": 1069,\n" +
                "      \"tokenBeginIndex\": 209,\n" +
                "      \"tokenEndIndex\": 210,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pan\",\n" +
                "      \"value\": \"pan\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"pan\",\n" +
                "      \"beginChar\": 1070,\n" +
                "      \"endChar\": 1073,\n" +
                "      \"tokenBeginIndex\": 210,\n" +
                "      \"tokenEndIndex\": 211,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 1073,\n" +
                "      \"endChar\": 1074,\n" +
                "      \"tokenBeginIndex\": 211,\n" +
                "      \"tokenEndIndex\": 212,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 195,\n" +
                "    \"tokenOffsetEnd\": 212,\n" +
                "    \"sentenceIndex\": 11,\n" +
                "    \"characterOffsetBegin\": 1000,\n" +
                "    \"characterOffsetEnd\": 1074,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Cook\",\n" +
                "      \"value\": \"Cook\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Cook\",\n" +
                "      \"beginChar\": 1075,\n" +
                "      \"endChar\": 1079,\n" +
                "      \"tokenBeginIndex\": 212,\n" +
                "      \"tokenEndIndex\": 213,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"over\",\n" +
                "      \"value\": \"over\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"over\",\n" +
                "      \"beginChar\": 1080,\n" +
                "      \"endChar\": 1084,\n" +
                "      \"tokenBeginIndex\": 213,\n" +
                "      \"tokenEndIndex\": 214,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"medium\",\n" +
                "      \"value\": \"medium\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"medium\",\n" +
                "      \"beginChar\": 1085,\n" +
                "      \"endChar\": 1091,\n" +
                "      \"tokenBeginIndex\": 214,\n" +
                "      \"tokenEndIndex\": 215,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"heat\",\n" +
                "      \"value\": \"heat\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"heat\",\n" +
                "      \"beginChar\": 1092,\n" +
                "      \"endChar\": 1096,\n" +
                "      \"tokenBeginIndex\": 215,\n" +
                "      \"tokenEndIndex\": 216,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 1096,\n" +
                "      \"endChar\": 1097,\n" +
                "      \"tokenBeginIndex\": 216,\n" +
                "      \"tokenEndIndex\": 217,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"stirring\",\n" +
                "      \"value\": \"stirring\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"stirring\",\n" +
                "      \"beginChar\": 1098,\n" +
                "      \"endChar\": 1106,\n" +
                "      \"tokenBeginIndex\": 217,\n" +
                "      \"tokenEndIndex\": 218,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 1107,\n" +
                "      \"endChar\": 1110,\n" +
                "      \"tokenBeginIndex\": 218,\n" +
                "      \"tokenEndIndex\": 219,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"adding\",\n" +
                "      \"value\": \"adding\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"adding\",\n" +
                "      \"beginChar\": 1111,\n" +
                "      \"endChar\": 1117,\n" +
                "      \"tokenBeginIndex\": 219,\n" +
                "      \"tokenEndIndex\": 220,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"remaining\",\n" +
                "      \"value\": \"remaining\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"remaining\",\n" +
                "      \"beginChar\": 1118,\n" +
                "      \"endChar\": 1127,\n" +
                "      \"tokenBeginIndex\": 220,\n" +
                "      \"tokenEndIndex\": 221,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"1/4\",\n" +
                "      \"value\": \"1/4\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"1/4\",\n" +
                "      \"beginChar\": 1128,\n" +
                "      \"endChar\": 1131,\n" +
                "      \"tokenBeginIndex\": 221,\n" +
                "      \"tokenEndIndex\": 222,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cup\",\n" +
                "      \"value\": \"cup\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cup\",\n" +
                "      \"beginChar\": 1132,\n" +
                "      \"endChar\": 1135,\n" +
                "      \"tokenBeginIndex\": 222,\n" +
                "      \"tokenEndIndex\": 223,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 1136,\n" +
                "      \"endChar\": 1141,\n" +
                "      \"tokenBeginIndex\": 223,\n" +
                "      \"tokenEndIndex\": 224,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"cooking\",\n" +
                "      \"value\": \"cooking\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"cooking\",\n" +
                "      \"beginChar\": 1142,\n" +
                "      \"endChar\": 1149,\n" +
                "      \"tokenBeginIndex\": 224,\n" +
                "      \"tokenEndIndex\": 225,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"liquid\",\n" +
                "      \"value\": \"liquid\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"liquid\",\n" +
                "      \"beginChar\": 1150,\n" +
                "      \"endChar\": 1156,\n" +
                "      \"tokenBeginIndex\": 225,\n" +
                "      \"tokenEndIndex\": 226,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 1157,\n" +
                "      \"endChar\": 1159,\n" +
                "      \"tokenBeginIndex\": 226,\n" +
                "      \"tokenEndIndex\": 227,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"loosen\",\n" +
                "      \"value\": \"loosen\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"loosen\",\n" +
                "      \"beginChar\": 1160,\n" +
                "      \"endChar\": 1166,\n" +
                "      \"tokenBeginIndex\": 227,\n" +
                "      \"tokenEndIndex\": 228,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"if\",\n" +
                "      \"value\": \"if\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"if\",\n" +
                "      \"beginChar\": 1167,\n" +
                "      \"endChar\": 1169,\n" +
                "      \"tokenBeginIndex\": 228,\n" +
                "      \"tokenEndIndex\": 229,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"needed\",\n" +
                "      \"value\": \"needed\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"needed\",\n" +
                "      \"beginChar\": 1170,\n" +
                "      \"endChar\": 1176,\n" +
                "      \"tokenBeginIndex\": 229,\n" +
                "      \"tokenEndIndex\": 230,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 1176,\n" +
                "      \"endChar\": 1177,\n" +
                "      \"tokenBeginIndex\": 230,\n" +
                "      \"tokenEndIndex\": 231,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"until\",\n" +
                "      \"value\": \"until\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"until\",\n" +
                "      \"beginChar\": 1178,\n" +
                "      \"endChar\": 1183,\n" +
                "      \"tokenBeginIndex\": 231,\n" +
                "      \"tokenEndIndex\": 232,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"sauce\",\n" +
                "      \"value\": \"sauce\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"sauce\",\n" +
                "      \"beginChar\": 1184,\n" +
                "      \"endChar\": 1189,\n" +
                "      \"tokenBeginIndex\": 232,\n" +
                "      \"tokenEndIndex\": 233,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"is\",\n" +
                "      \"value\": \"is\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"is\",\n" +
                "      \"beginChar\": 1190,\n" +
                "      \"endChar\": 1192,\n" +
                "      \"tokenBeginIndex\": 233,\n" +
                "      \"tokenEndIndex\": 234,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"thickened\",\n" +
                "      \"value\": \"thickened\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"thickened\",\n" +
                "      \"beginChar\": 1193,\n" +
                "      \"endChar\": 1202,\n" +
                "      \"tokenBeginIndex\": 234,\n" +
                "      \"tokenEndIndex\": 235,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 1203,\n" +
                "      \"endChar\": 1206,\n" +
                "      \"tokenBeginIndex\": 235,\n" +
                "      \"tokenEndIndex\": 236,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"emulsified\",\n" +
                "      \"value\": \"emulsified\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"emulsified\",\n" +
                "      \"beginChar\": 1207,\n" +
                "      \"endChar\": 1217,\n" +
                "      \"tokenBeginIndex\": 236,\n" +
                "      \"tokenEndIndex\": 237,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \",\",\n" +
                "      \"value\": \",\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \",\",\n" +
                "      \"beginChar\": 1217,\n" +
                "      \"endChar\": 1218,\n" +
                "      \"tokenBeginIndex\": 237,\n" +
                "      \"tokenEndIndex\": 238,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"about\",\n" +
                "      \"value\": \"about\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"about\",\n" +
                "      \"beginChar\": 1219,\n" +
                "      \"endChar\": 1224,\n" +
                "      \"tokenBeginIndex\": 238,\n" +
                "      \"tokenEndIndex\": 239,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"2\",\n" +
                "      \"value\": \"2\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"2\",\n" +
                "      \"beginChar\": 1225,\n" +
                "      \"endChar\": 1226,\n" +
                "      \"tokenBeginIndex\": 239,\n" +
                "      \"tokenEndIndex\": 240,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"minutes\",\n" +
                "      \"value\": \"minutes\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"minutes\",\n" +
                "      \"beginChar\": 1227,\n" +
                "      \"endChar\": 1234,\n" +
                "      \"tokenBeginIndex\": 240,\n" +
                "      \"tokenEndIndex\": 241,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 1234,\n" +
                "      \"endChar\": 1235,\n" +
                "      \"tokenBeginIndex\": 241,\n" +
                "      \"tokenEndIndex\": 242,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 212,\n" +
                "    \"tokenOffsetEnd\": 242,\n" +
                "    \"sentenceIndex\": 12,\n" +
                "    \"characterOffsetBegin\": 1075,\n" +
                "    \"characterOffsetEnd\": 1235,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Flake\",\n" +
                "      \"value\": \"Flake\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Flake\",\n" +
                "      \"beginChar\": 1236,\n" +
                "      \"endChar\": 1241,\n" +
                "      \"tokenBeginIndex\": 242,\n" +
                "      \"tokenEndIndex\": 243,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"tuna\",\n" +
                "      \"value\": \"tuna\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"tuna\",\n" +
                "      \"beginChar\": 1242,\n" +
                "      \"endChar\": 1246,\n" +
                "      \"tokenBeginIndex\": 243,\n" +
                "      \"tokenEndIndex\": 244,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"into\",\n" +
                "      \"value\": \"into\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"into\",\n" +
                "      \"beginChar\": 1247,\n" +
                "      \"endChar\": 1251,\n" +
                "      \"tokenBeginIndex\": 244,\n" +
                "      \"tokenEndIndex\": 245,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 1252,\n" +
                "      \"endChar\": 1257,\n" +
                "      \"tokenBeginIndex\": 245,\n" +
                "      \"tokenEndIndex\": 246,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"and\",\n" +
                "      \"value\": \"and\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"and\",\n" +
                "      \"beginChar\": 1258,\n" +
                "      \"endChar\": 1261,\n" +
                "      \"tokenBeginIndex\": 246,\n" +
                "      \"tokenEndIndex\": 247,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"toss\",\n" +
                "      \"value\": \"toss\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"toss\",\n" +
                "      \"beginChar\": 1262,\n" +
                "      \"endChar\": 1266,\n" +
                "      \"tokenBeginIndex\": 247,\n" +
                "      \"tokenEndIndex\": 248,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"to\",\n" +
                "      \"value\": \"to\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"to\",\n" +
                "      \"beginChar\": 1267,\n" +
                "      \"endChar\": 1269,\n" +
                "      \"tokenBeginIndex\": 248,\n" +
                "      \"tokenEndIndex\": 249,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"combine\",\n" +
                "      \"value\": \"combine\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"combine\",\n" +
                "      \"beginChar\": 1270,\n" +
                "      \"endChar\": 1277,\n" +
                "      \"tokenBeginIndex\": 249,\n" +
                "      \"tokenEndIndex\": 250,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 1277,\n" +
                "      \"endChar\": 1278,\n" +
                "      \"tokenBeginIndex\": 250,\n" +
                "      \"tokenEndIndex\": 251,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 242,\n" +
                "    \"tokenOffsetEnd\": 251,\n" +
                "    \"sentenceIndex\": 13,\n" +
                "    \"characterOffsetBegin\": 1236,\n" +
                "    \"characterOffsetEnd\": 1278,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Divide\",\n" +
                "      \"value\": \"Divide\",\n" +
                "      \"before\": \"\\n\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Divide\",\n" +
                "      \"beginChar\": 1279,\n" +
                "      \"endChar\": 1285,\n" +
                "      \"tokenBeginIndex\": 251,\n" +
                "      \"tokenEndIndex\": 252,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"pasta\",\n" +
                "      \"value\": \"pasta\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"pasta\",\n" +
                "      \"beginChar\": 1286,\n" +
                "      \"endChar\": 1291,\n" +
                "      \"tokenBeginIndex\": 252,\n" +
                "      \"tokenEndIndex\": 253,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"among\",\n" +
                "      \"value\": \"among\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"among\",\n" +
                "      \"beginChar\": 1292,\n" +
                "      \"endChar\": 1297,\n" +
                "      \"tokenBeginIndex\": 253,\n" +
                "      \"tokenEndIndex\": 254,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"plates\",\n" +
                "      \"value\": \"plates\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"plates\",\n" +
                "      \"beginChar\": 1298,\n" +
                "      \"endChar\": 1304,\n" +
                "      \"tokenBeginIndex\": 254,\n" +
                "      \"tokenEndIndex\": 255,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 1304,\n" +
                "      \"endChar\": 1305,\n" +
                "      \"tokenBeginIndex\": 255,\n" +
                "      \"tokenEndIndex\": 256,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" +
                "    \"tokenOffsetBegin\": 251,\n" +
                "    \"tokenOffsetEnd\": 256,\n" +
                "    \"sentenceIndex\": 14,\n" +
                "    \"characterOffsetBegin\": 1279,\n" +
                "    \"characterOffsetEnd\": 1305,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }, {\n" +
                "    \"token\": [{\n" +
                "      \"word\": \"Top\",\n" +
                "      \"value\": \"Top\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"Top\",\n" +
                "      \"beginChar\": 1306,\n" +
                "      \"endChar\": 1309,\n" +
                "      \"tokenBeginIndex\": 256,\n" +
                "      \"tokenEndIndex\": 257,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"with\",\n" +
                "      \"value\": \"with\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"with\",\n" +
                "      \"beginChar\": 1310,\n" +
                "      \"endChar\": 1314,\n" +
                "      \"tokenBeginIndex\": 257,\n" +
                "      \"tokenEndIndex\": 258,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"fried\",\n" +
                "      \"value\": \"fried\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \" \",\n" +
                "      \"originalText\": \"fried\",\n" +
                "      \"beginChar\": 1315,\n" +
                "      \"endChar\": 1320,\n" +
                "      \"tokenBeginIndex\": 258,\n" +
                "      \"tokenEndIndex\": 259,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \"capers\",\n" +
                "      \"value\": \"capers\",\n" +
                "      \"before\": \" \",\n" +
                "      \"after\": \"\",\n" +
                "      \"originalText\": \"capers\",\n" +
                "      \"beginChar\": 1321,\n" +
                "      \"endChar\": 1327,\n" +
                "      \"tokenBeginIndex\": 259,\n" +
                "      \"tokenEndIndex\": 260,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }, {\n" +
                "      \"word\": \".\",\n" +
                "      \"value\": \".\",\n" +
                "      \"before\": \"\",\n" +
                "      \"after\": \"\\n\",\n" +
                "      \"originalText\": \".\",\n" +
                "      \"beginChar\": 1327,\n" +
                "      \"endChar\": 1328,\n" +
                "      \"tokenBeginIndex\": 260,\n" +
                "      \"tokenEndIndex\": 261,\n" +
                "      \"hasXmlContext\": false,\n" +
                "      \"isNewline\": false\n" +
                "    }],\n" ,"    \"tokenOffsetBegin\": 256,\n" +
                "    \"tokenOffsetEnd\": 261,\n" +
                "    \"sentenceIndex\": 15,\n" +
                "    \"characterOffsetBegin\": 1306,\n" +
                "    \"characterOffsetEnd\": 1328,\n" +
                "    \"hasRelationAnnotations\": false,\n" +
                "    \"hasNumerizedTokensAnnotation\": false,\n" +
                "    \"hasEntityMentionsAnnotation\": false\n" +
                "  }],\n" +
                "  \"xmlDoc\": false,\n" +
                "  \"hasEntityMentionsAnnotation\": false,\n" +
                "  \"hasCorefMentionAnnotation\": false,\n" +
                "  \"hasCorefAnnotation\": false\n" +
                "},\"mImages\":[],\"mLevel\":0}]}\n" +
                "\n"
        });

        ExtractedText text = ExtractedText.fromJson(trial);
        RecipeInProgress rip = new RecipeInProgress(text);

        (new SplitToMainSectionsTask(rip)).doTask();
        System.out.println(rip);
    }
}