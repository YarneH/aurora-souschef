package com.aurora.souschefprocessor.facade;

import com.aurora.souschefprocessor.recipe.Recipe;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class DelegatorLongTest {
    private static List<String> validRecipes;
    private static List<String> invalidRecipes;
    private static Delegator delegator;
    private static CRFClassifier<CoreLabel> crfClassifier;


    @BeforeClass
    public static void initialize() {
        //DetectTimersInStepTask.initializeAnnotationPipeline();
        // load in the recipes
        List<String> recipes = initializeRecipes();
        // split into valid and invalid
        // the first 5 recipes are valid recipes
        validRecipes = recipes.subList(0, 5);
        invalidRecipes = recipes.subList(5, 6);

        // load in the model
        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        try {
            crfClassifier = CRFClassifier.getClassifier(modelName);
            // create the delegator object
            // parallel is better when the number of cores are only half
            // sequnetial performs faster
            delegator = new Delegator(crfClassifier, false);
        } catch (IOException | ClassNotFoundException e) {
        }


    }

    /**
     * Read in the testrecipes
     *
     * @return A list of testrecipes
     */
    private static List<String> initializeRecipes() {
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


    @Test
    public void Delegator_processText_NoExceptionsInDelegatorForValidRecipes() {
        /**
         * Check that no exceptions are thrown when these recipes are read in
         */
        // Arrange
        // initialize on false
        boolean thrown = false;


        // Act
        try {

            for (String text : validRecipes) {
                Recipe recipe = delegator.processText(text);
                System.out.println(recipe.getRecipeSteps());

            }
        } catch (Exception e) {
            // set thrown to true, this should not happen

            thrown = true;
            System.out.println(e);
        }

        // Assert
        // assert that no error where thrown
        assert (!thrown);
    }

    @Test
    public void Delegator_processText_ExceptionsInDelegatorForInvalidRecipes() {
        /**
         * Check that no exceptions are thrown when these recipes are read in
         */
        // Arrange
        // initialize on false
        boolean thrown = false;

        // Act
        try {
            // the 6th recipe is invalid
            for (String text : invalidRecipes) {
                delegator.processText(text);
            }
        } catch (Exception e) {
            // set thrown to true, this should happen
            thrown = true;
        }
        // Assert
        // assert that an error was thrown
        assert (thrown);
    }

    @Test
    public void Delegator_processText_timeForDoingTasksNonParallelIsLowerThanThreshold() {
        // Arrange
        // non-parallelize
        System.out.println("non-parallelize");
        // parallelize flag to false in delegator
        delegator = new Delegator(crfClassifier, false);
        int average_non = 0;

        // Act
        for (String recipeText : validRecipes) {
            // do the processing and add the time this processing costed
            long start = System.currentTimeMillis();
            delegator.processText(recipeText);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_non += time;

        }
        // divide by the amount of recipes processed to get the average
        average_non = average_non / validRecipes.size();

        // Assert
        System.out.println(average_non + "  NON PARALLEL TIME");
        assert (average_non < 4000);

    }

    @Test
    public void Delegator_processText_timeForDoingTasksParallelIsLowerThanThreshold() {

        // Arrange
        int average_para = 0;
        System.out.println("parallelize");
        delegator = new Delegator(crfClassifier, true);

        // Act
        for (String text : validRecipes) {
            long start = System.currentTimeMillis();
            Recipe recipe = delegator.processText(text);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_para += time;
        }
        average_para = average_para / validRecipes.size();


        // Assert
        System.out.println(average_para + "  PARALLEL TIME");
        assert (average_para < 15000);


    }

    @After
    public void wipeDelegator() {
        delegator = new Delegator(crfClassifier, true);
    }

    @Test
    public void newTest(){
        RecipeStep step = new RecipeStep("Zucchini is easy to shred on the large holes of a box grater, with the shredding attachment of a food processor, or with a mandoline.");
        RecipeInProgress rip = new RecipeInProgress("");
        rip.setIngredientsString("Salt\n" +
                "1 pound fettuccine\n" +
                "4 tablespoons extra-virgin olive oil\n" +
                "3 or 4 garlic cloves, finely chopped or grated\n" +
                "Zest of 1 to 2 lemons\n" +
                "2 medium- large or 4 small zucchini, cleaned but not peeled, and shredded\n" +
                "Freshly ground pepper\n" +
                "1/4 cup chopped fresh flat-leaf parsley\n" +
                "1/4 cup chopped fresh mint\n" +
                "1 cup fresh whole-milk ricotta cheese, at room temperature");
        DetectIngredientsInListTask listTask = new DetectIngredientsInListTask(rip, crfClassifier);
        listTask.doTask();
        ArrayList<RecipeStep> list = new ArrayList<>();
        list.add(step);
        rip.setRecipeSteps(list);
        DetectIngredientsInStepTask task = new DetectIngredientsInStepTask(rip,0);
        task.doTask();
        System.out.println(rip.getIngredients());
        System.out.println(step);

    }

    @Test
    public void newTest2(){
        RecipeStep step = new RecipeStep("7) Serve these warm with your favorite filling!");
        RecipeInProgress rip = new RecipeInProgress("");
        rip.setIngredientsString("2 c. Gluten-free all purpose flour (or 2 c. White rice flour)\n" +
                "2 tsp. Xanthan gum or Guar gum\n" +
                "1 tsp. Gluten-free baking powder\n" +
                "2 tsp. Brown sugar\n" +
                "1 tsp. Salt\n" +
                "1 c. Warm water\n" +
                " Top-Rated Gluten-Free Flour at AmazonTop-Rated Gluten-Free Flour");
        DetectIngredientsInListTask listTask = new DetectIngredientsInListTask(rip, crfClassifier);
        listTask.doTask();
        ArrayList<RecipeStep> list = new ArrayList<>();
        list.add(step);
        rip.setRecipeSteps(list);
        DetectIngredientsInStepTask task = new DetectIngredientsInStepTask(rip,0);
        task.doTask();
        System.out.println(step);

    }

}