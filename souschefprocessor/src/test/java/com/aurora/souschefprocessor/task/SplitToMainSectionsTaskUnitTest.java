package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
                            new FileInputStream(filename), StandardCharsets.UTF_8));

            String line = reader.readLine();
            while (line != null) {
                list.add(ExtractedText.fromJson(line));
                line = reader.readLine();
            }

        } catch (IOException io) {
            System.err.print(io);
        }

        return list.subList(0, 7);


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
        int i = 0;
        for (ExtractedText text : recipeTexts) {
            System.out.println(i);
            // Arrange
            RecipeInProgress rip = new RecipeInProgress(text);
            SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
            // Act
            task.doTask();
            // Assert
            assert (rip.getStepsString() != null);
            assert (rip.getIngredientsString() != null);
            assert (rip.getDescription() != null);

            i++;
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



    /**
     * This test asserts that even if the ingredients are listed in different sections of the Extracted text, they are still all recognized and added
     * to the ingredientssection
     */
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
}