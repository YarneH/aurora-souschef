package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.helpertasks.ParallelizeStepsTask;
import com.aurora.souschefprocessor.task.helpertasks.StepTaskNames;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

import static com.aurora.souschefprocessor.task.helpertasks.StepTaskNames.INGREDIENT;
import static com.aurora.souschefprocessor.task.helpertasks.StepTaskNames.TIMER;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


public class ParallelizableTaskLongTest {

    private static ThreadPoolExecutor mThreadPoolExecutor;
    private static List<RecipeStepInProgress> recipeSteps = new ArrayList<>();
    private static StepTaskNames[] onlyTimerName = {TIMER};
    private static StepTaskNames[] onlyIngrName = {INGREDIENT};
    private static StepTaskNames[] both = {TIMER, INGREDIENT};
    private static ParallelizeStepsTask onlyTimerstask;
    private static ParallelizeStepsTask onlyIngrtask;
    private static ParallelizeStepsTask bothtasks;

    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static String original = "irrelevant";
    private static ExtractedText emptyExtractedText = new ExtractedText("", "");

    @BeforeClass
    public static void initialize() {
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }
        RecipeInProgress rip = new RecipeInProgress(emptyExtractedText);

        DetectTimersInStepTask.initializeAnnotationPipeline();

        // what a yummy recipe

        // create the ingredients
        ListIngredient sauce = new ListIngredient("sauce", "gram", 500, original, irrelevantPositions);
        ListIngredient oil = new ListIngredient("Olive oil", "cup", 1 / 4, original, irrelevantPositions);
        ListIngredient dough = new ListIngredient("pre-made cake dough", "kg", 1, original, irrelevantPositions);
        ListIngredient cheese = new ListIngredient("parmesan cheese", "cup", 1, original, irrelevantPositions);
        ListIngredient coffee = new ListIngredient("coffee", "", 1, original, irrelevantPositions);
        ListIngredient chips = new ListIngredient("chips", "ounce", 14, original, irrelevantPositions);
        ListIngredient lasagna = new ListIngredient("lasagna sheets", "", 10, original, irrelevantPositions);
        ListIngredient spaghetti = new ListIngredient("spaghetti or linguini", "gram", 500, original,
                irrelevantPositions);
        rip.setIngredients(new ArrayList<>(Arrays.asList(sauce, oil, dough, coffee, cheese, lasagna, spaghetti,
                chips)));

        // create the steps
        RecipeStepInProgress sauceStep =
                new RecipeStepInProgress("Put 500 gram sauce in the microwave for 3 minutes"); //0 minutes
        RecipeStepInProgress oilStep = new RecipeStepInProgress("Heat the oil in a saucepan and gently fry the onion " +
                "until softened, about 4-5 minutes."); //1 upperbound and lowerbound with dash //"Put 500 gram
        // spaghetti in boiling water 7 to 9 minutes")); //1 (upperbound and lowerbound different)
        RecipeStepInProgress doughStep = new RecipeStepInProgress("Put the dough in the oven for 30 minutes and let " +
                "rest for 20 minutes."); //2 (two timers)
        RecipeStepInProgress cheeseStep = new RecipeStepInProgress("Grate cheese for 30 seconds"); //3 (seconds)
        RecipeStepInProgress coffeeStep = new RecipeStepInProgress("Wait for 4 hours and drink your coffee"); //4
        // (hours)
        RecipeStepInProgress chipsStep = new RecipeStepInProgress("Let cool down for an hour and a half. The chips " +
                "are now very crispy"); //5 (verbose hour)
        RecipeStepInProgress lasagnaStep = new RecipeStepInProgress("Put the lasagna in the oven for 1h");//6 (symbol
        // hour)
        RecipeStepInProgress spaghettiStep = new RecipeStepInProgress("Put 500 gram spaghetti in boiling water 7 to 9" +
                " minutes"); //7 (upperbound and lowerbound different)))
        recipeSteps = new ArrayList<>(Arrays.asList(oilStep, sauceStep, spaghettiStep, lasagnaStep, doughStep,
                chipsStep, cheeseStep, coffeeStep));
        rip.setStepsInProgress(recipeSteps);
        // annotate the steps
        AnnotationPipeline pipeline = new AnnotationPipeline();

        pipeline.addAnnotator(new TokenizerAnnotator());
        pipeline.addAnnotator(new WordsToSentencesAnnotator());
        pipeline.addAnnotator(new POSTaggerAnnotator());


        for (RecipeStepInProgress step : recipeSteps) {
            Annotation a = new Annotation(step.getDescription());
            pipeline.annotate(a);
            step.setSentenceAnnotations(Collections.singletonList(a));
            step.setBeginPosition(0);
        }

        // create the threadpool

        setUpThreadPool();

        // create the tasks
        onlyTimerstask = new ParallelizeStepsTask(rip, mThreadPoolExecutor, onlyTimerName);
        onlyIngrtask = new ParallelizeStepsTask(rip, mThreadPoolExecutor, onlyIngrName);
        bothtasks = new ParallelizeStepsTask(rip, mThreadPoolExecutor, both);
    }

    private static void setUpThreadPool() {
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         */
        int numberOfCores =
                Runtime.getRuntime().availableProcessors();
        // A queue of Runnables
        final BlockingQueue<Runnable> decodeWorkQueue;
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        decodeWorkQueue = new LinkedBlockingQueue<>();
        // Sets the amount of time an idle thread waits before terminating
        final int KEEP_ALIVE_TIME = 1;
        // Sets the Time Unit to seconds
        final TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;
        // Creates a thread pool manager
        mThreadPoolExecutor = new ThreadPoolExecutor(
                // Initial pool size
                numberOfCores,
                // Max pool size
                numberOfCores,
                KEEP_ALIVE_TIME,
                keepAliveTimeUnit,
                decodeWorkQueue);
    }

    @After
    public void wipeRecipe() {
        for (RecipeStep step : recipeSteps) {
            step.setIngredients(null);
            step.setRecipeTimers(null);
            step.setIngredientDetectionDone(false);
            step.setTimerDetectionDone(false);
        }
    }

    @Test
    public void ParrallelizableStepTask_doTask_TimersDetectedForAllSteps() throws RecipeDetectionException {
        // The parallel tasks set the timers for all steps
        // check if this is done for all the steps

        // Arrange
        onlyTimerstask.doTask();

        // Assert
        for (RecipeStep step : recipeSteps) {
            assertTrue("The timer detection variable is not set to true after the task should have been done",
                    step.isTimerDetectionDone());

            // for each of these steps a timer can be detected so assert non null value
            assertNotEquals("No timer was detected for step " + step.getDescription(), 0,
                    step.getRecipeTimers().size());

        }
    }

    @Test
    public void ParrallelizableStepTask_doTask_IngredientsDetectedForAllSteps() throws RecipeDetectionException {
        // The parallel tasks set the ingredients for all steps
        // check if the ingredients are detected for all the steps

        // Arrange
        onlyIngrtask.doTask();

        // Assert
        for (RecipeStep step : recipeSteps) {

            // check if states that the detection was done
            assertTrue("The ingredient detection variable is not set to true after the task should have been done",
                    step.isIngredientDetectionDone());

            // for each of these steps an ingredient can be detected so assert the list is not emty
            assertNotEquals("No ingredient was detected for step " + step.getDescription(), 0,
                    step.getIngredients().size());

        }
    }

    @Test
    public void ParrallelizableStepTask_doTask_BothTimersAndIngredientsDetectedForAllSteps() throws RecipeDetectionException {
        // The parallel tasks set the ingredients for all steps
        // check if both timers and ingredients are detected for all the steps

        // Arrange
        bothtasks.doTask();

        // Assert
        for (RecipeStep step : recipeSteps) {
            assertTrue("The ingredient detection variable is not set to true after the task should have been done",
                    step.isIngredientDetectionDone());

            // for each of these steps an ingredient can be detected so assert the list is not emty
            assertNotEquals("No ingredient was detected for step " + step.getDescription(), 0,
                    step.getIngredients().size());

            assertTrue("The timer detection variable is not set to true after the task should have been done",
                    step.isTimerDetectionDone());

            // for each of these steps a timer can be detected so assert the list is not empty
            assertNotEquals("No timer was detected for step " + step.getDescription(), 0,
                    step.getRecipeTimers().size());
            System.out.println(step);
        }
    }

}
