package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.task.sectiondivider.DetectNumberOfPeopleTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TODO add more testing data to increase acceptance test reliability
public class DetectNumberOfPeopleTaskUnitTest {

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
        return ("NUMBER\t4\n" +
                "NUMBER\t4\n" +
                "NUMBER\t4\n" +
                "NUMBER\t4\n" +
                "NUMBER\t8\n" +
                "NUMBER\t2525\n" +
                "NO_NUMBER\n" +
                "NO_NUMBER\n").split("\n");
    }

    private static List<String> initializeDataSet() {
        String filename = "src/test/java/com/aurora/souschefprocessor/facade/recipes.txt";
        List<String> list = new ArrayList<>();
        try {
            FileReader fReader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fReader);
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

    @After
    public void wipeRecipe() {
        recipe.setNumberOfPeople(0);
    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_valueHasBeenRead() {
        /**
         * Check if the correct number is detected for the sample recipe
         */
        detectNumberOfPeopleTask.doTask();
        assert (recipe.getNumberOfPeople() == 1);
    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_noNumberOfPeople() {
        /**
         * Check if no number of people is detected when the section of the recipe where the number
         * of people are mentioned is ommitted.
         */
        // arrange
        String originalTextNoNumber = originalText.substring(0, originalText.indexOf('\n') + 1);
        RecipeInProgress recipeNoNumber = new RecipeInProgress(originalTextNoNumber);
        DetectNumberOfPeopleTask detectNumberOfPeopleTask = new DetectNumberOfPeopleTask(recipeNoNumber);
        // act
        detectNumberOfPeopleTask.doTask();
        // assert
        assert (recipeNoNumber.getNumberOfPeople() == DEFAULT_NO_NUMBER);
    }


    @Test
    public void DetectNumberOfPeopleTask_doTask_acceptanceTest95PercentAccuracy() {
        /**
         * The number of people detected should be correctly detected in 95% of the cases
         */
        List<String> dataSet = initializeDataSet();
        String[] dataSetTags = initializeDataSetTags();
        int amount = dataSet.size();
        int correct = amount;

        for (int i = 1; i <= amount ; i++) {
            String recipeText = dataSet.get(i - 1);
            String recipeTag = dataSetTags[i - 1];

            RecipeInProgress recipe = new RecipeInProgress(recipeText);
            DetectNumberOfPeopleTask detector = new DetectNumberOfPeopleTask(recipe);
            detector.doTask();

            if (recipeTag.equals("NO_NUMBER")) {
                if (recipe.getNumberOfPeople() != -1) {
                    System.out.println(recipeText);
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
