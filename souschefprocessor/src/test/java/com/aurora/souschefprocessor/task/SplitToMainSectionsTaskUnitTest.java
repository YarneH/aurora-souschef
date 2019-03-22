package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
        map.put("STEPS", "\n\ncook pasta in a large pot of boiling salted water, stirring occasionally, until al dente. drain pasta, reserving 1 cup pasta cooking liquid; return pasta to pot.\n" +
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

        return fieldsList;

    }

    @After
    public void wipeRecipe() {
    }

    @Test
    public void SplitToMainSections_doTask_sectionsAreSet() {
        for (String text : recipeTexts) {
            RecipeInProgress rip = new RecipeInProgress(text);
            SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
            task.doTask();

            assert (rip.getStepsString() != null);
            assert (rip.getIngredientsString() != null);
            assert (rip.getDescription() != null);
        }
    }

    @Test
    public void SplitToMainSections_doTask_sectionsHaveCorrectValues() {
        List<Map<String, String>> fieldsList = initializeFieldList();

        for (int i = 1; i < 2; i++) {
            String text = recipeTexts.get(i);
            RecipeInProgress rip = new RecipeInProgress(text);
            SplitToMainSectionsTask task = new SplitToMainSectionsTask(rip);
            task.doTask();

            for(int j =0; j<rip.getStepsString().length(); j++){
                if(rip.getStepsString().charAt(j) != fieldsList.get(i).get("STEPS").charAt(j)){
                    System.out.println(rip.getStepsString().substring(0,587));
                    System.err.println(fieldsList.get(i).get("STEPS").substring(0,587));
                }
            }



            System.err.println(rip.getStepsString().length() + "  "+ fieldsList.get(i).get("STEPS").length());
            System.err.println(rip.getStepsString().equals(fieldsList.get(i).get("STEPS")));
            assert (rip.getStepsString().equals(fieldsList.get(i).get("STEPS")));
            assert (rip.getIngredientsString().equals(fieldsList.get(i).get("INGR")));
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
}