package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.recipe.RecipeTimer;
import com.aurora.souschefprocessor.task.timerdetector.DetectTimersInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

import static org.junit.Assert.assertEquals;


public class DetectTimersInStepTaskUnitTest {

    private static List<DetectTimersInStepTask> detectors = new ArrayList<>();
    private static RecipeInProgress recipe;
    private static ArrayList<RecipeStepInProgress> recipeSteps;
    private static Position irrelevantPosition = new Position(0, 1);
    private static ExtractedText emptyExtractedText = new ExtractedText("", null);


    @BeforeClass
    public static void initialize() {

        recipeSteps = new ArrayList<>();
        recipeSteps.add(new RecipeStepInProgress("Put 500 gram sauce in the microwave for 3 minutes")); //0 minutes
        recipeSteps.add(new RecipeStepInProgress("Heat the oil in a saucepan and gently fry the onion until softened, about 4-5 minutes.")); //1 upperbound and lowerbound with dash //"Put 500 gram spaghetti in boiling water 7 to 9 minutes")); //1 (upperbound and lowerbound different)
        recipeSteps.add(new RecipeStepInProgress("Put in the oven for 30 minutes and let rest for 20 minutes.")); //2 (two timers)
        recipeSteps.add(new RecipeStepInProgress("Grate cheese for 30 seconds")); //3 (seconds)
        recipeSteps.add(new RecipeStepInProgress("Wait for 4 hours")); //4 (hours)
        recipeSteps.add(new RecipeStepInProgress("Let cool down for an hour and a half.")); //5 (verbose hour)
        recipeSteps.add(new RecipeStepInProgress("Put the lasagna in the oven for 1h"));//6 (symbol hour)
        recipeSteps.add(new RecipeStepInProgress("Put 500 gram spaghetti in boiling water 7 to 9 minutes")); //7 (upperbound and lowerbound different)))
        recipeSteps.add(new RecipeStepInProgress("Let boil for 1 minute 30 seconds")); //8 merging timer
        recipeSteps.add(new RecipeStepInProgress("Put in the oven for 50 minutes to 1 hour")); //9 to case

        String originalText = "irrelevant";
        recipe = new RecipeInProgress(emptyExtractedText);
        recipe.setStepsInProgress(recipeSteps);

        for (int stepIndex = 0; stepIndex < recipeSteps.size(); stepIndex++) {
            detectors.add(new DetectTimersInStepTask(recipe, stepIndex));
        }

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
        System.out.println("initialize the pipeline");
        DetectTimersInStepTask.initializeAnnotationPipeline();


    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.unsetTimers();
        }
    }


    @Test
    public void DetectTimersInStep_doTask_timersHaveBeenSetForAllSteps() {
        /**
         * After doing the task the timer objects are not null
         */

        for (DetectTimersInStepTask detector : detectors) {
            detector.doTask();
        }
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isTimerDetectionDone());
            assert (s.getRecipeTimers() != null);
        }
    }

    @Test
    public void DetectTimersInStep_doTask_detectMinuteTimer() {
        /**
         * The detection of a timer containing a minute is correct
         */
        // Arrange
        int stepIndex = 0; //index zero has minutes
        DetectTimersInStepTask detector = detectors.get(stepIndex); //index zero has minutes

        // Act
        detector.doTask();

        // Assert
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(3 * 60, irrelevantPosition);
        assert (timer.equals(recipe.getStepsInProgress().get(stepIndex).getRecipeTimers().get(0)));

    }

    @Test
    public void DetectTimersInStep_doTask_detectHoursTimer() {
        int stepIndex = 4; //index four has hours
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        RecipeTimer timer = new RecipeTimer(4 * 60 * 60, irrelevantPosition);
        assert (timer.equals(recipe.getStepsInProgress().get(stepIndex).getRecipeTimers().get(0)));
    }

    @Test
    public void DetectTimersInStep_doTask_detectSecondsTimer() {
        int stepIndex = 3; //index three has seconds
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(30, irrelevantPosition);
        assert (timer.equals(recipe.getStepsInProgress().get(stepIndex).getRecipeTimers().get(0)));
    }

    @Test
    public void DetectTimersInStep_doTask_detectMultipleTimers() {
        int stepIndex = 2; //index two has multiple timers
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer1 = new RecipeTimer(30 * 60, irrelevantPosition);
        RecipeTimer timer2 = new RecipeTimer(20 * 60, irrelevantPosition);
        assert (recipeSteps.get(stepIndex).getRecipeTimers().contains(timer1));
        assert (recipeSteps.get(stepIndex).getRecipeTimers().contains(timer2));
    }

    @Test
    public void DetectTimersInStep_doTask_verboseHoursTimers() {
        int stepIndex = 5; //index five has verbose hours
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer((int) (60 * 60 * 1.5), irrelevantPosition);

        System.out.println(recipeSteps.get(stepIndex).getRecipeTimers().get(0));
        assert (timer.equals(recipeSteps.get(stepIndex).getRecipeTimers().get(0)));

    }

    @Test
    public void DetectTimersInStep_doTask_hourSymbolTimers() {
        int stepIndex = 6; //index six has symbol hours
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(60 * 60, irrelevantPosition);
        assert (timer.equals(recipeSteps.get(stepIndex).getRecipeTimers().get(0)));
    }

    @Test
    public void DetectTimersInStep_doTask_upperBoundAndLowerBoundNotEqualWithDash() {

        int stepIndex = 1; //index 1 has upper and lower bound
        RecipeStep step = recipeSteps.get(stepIndex);


        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(5 * 60, 4 * 60, irrelevantPosition);
        assertEquals("The timers are not equal", timer, recipeSteps.get(stepIndex).getRecipeTimers().get(0));


    }

    @Test
    public void DetectTimersInStep_doTask_upperBoundAndLowerBoundNotEqualWithoutDash() {
        int stepIndex = 7; //index 7has upper and lower bound
        DetectTimersInStepTask detector = detectors.get(stepIndex);
        detector.doTask();
        //assert detection
        assert (recipeSteps.get(stepIndex).getRecipeTimers().size() > 0);
        //assert correct detection
        RecipeTimer timer = new RecipeTimer(9 * 60, 7 * 60, irrelevantPosition);
        assertEquals("The timers are not equal", timer, recipeSteps.get(stepIndex).getRecipeTimers().get(0));

    }

    @Test
    public void DetectTimersInStep_doTask_PositionOfTimersCorrectlyDetected() {
        // TODO: make these seprate tests

        // first case: "Put 500 gram sauce in the microwave for 3 minutes"
        // timer = "3 minutes"
        int index = 0;
        detectors.get(index).doTask();
        String timeString = "3 minutes";
        Position pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        String description = recipeSteps.get(index).getDescription();

        String substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));

        // second case: "Heat the oil in a saucepan and gently fry the onion until softened, about 4-5 minutes."
        // timer = "about 4 - 5 minutes" (spaces added for seperate tokens)
        index = 1;
        detectors.get(index).doTask();
        timeString = "4-5 minutes";
        pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        description = recipeSteps.get(index).getDescription();

        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assertEquals("the substrings are not okay", timeString, substring);
        assert (substring.equals(timeString));

        // third case:  "Put in the oven for 30 minutes and let rest for 20 minutes."
        // timer 1: 30 minutes
        // timer 2: 20 minutes
        index = 2;
        detectors.get(index).doTask();
        timeString = "30 minutes";
        pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        description = recipeSteps.get(index).getDescription();
        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));

        timeString = "20 minutes";
        pos = recipeSteps.get(index).getRecipeTimers().get(1).getPosition();
        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));

        // fourth case: "Grate cheese for 30 seconds"
        // timer: "30 seconds"
        index = 3;
        detectors.get(index).doTask();
        timeString = "30 seconds";
        pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        description = recipeSteps.get(index).getDescription();
        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));

        // fifth case:  "Wait for 4 hours"
        // timer = "4 hours"
        index = 4;
        detectors.get(index).doTask();
        timeString = "4 hours";
        pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        description = recipeSteps.get(index).getDescription();
        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));

        // sixth case: "Let cool down for an hour and a half."
        // timer: "an hour and a half"
        index = 5;
        detectors.get(index).doTask();
        timeString = "an hour and a half";
        pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        description = recipeSteps.get(index).getDescription();
        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));

        // seventh case: "Put the lasagna in the oven for 1h"
        // timer: "1h"
        index = 6;
        detectors.get(index).doTask();
        timeString = "1h";
        pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        description = recipeSteps.get(index).getDescription();
        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));


        // eight case: "Put 500 gram spaghetti in boiling water 7 to 9 minutes"
        // timer: "7 to 9 minutes"
        index = 7;
        detectors.get(index).doTask();
        timeString = "7 to 9 minutes";
        pos = recipeSteps.get(index).getRecipeTimers().get(0).getPosition();
        description = recipeSteps.get(index).getDescription();
        substring = description.substring(pos.getBeginIndex(), pos.getEndIndex());
        assert (substring.equals(timeString));

    }

    @Test
    public void DetectTimersInStep_doTask_ToWithDifferentUnitsStillcorrect() {
        int index = 9;
        DetectTimersInStepTask task = new DetectTimersInStepTask(recipe, index);
        RecipeTimer goal = new RecipeTimer(50 * 60, 1 * 3600, irrelevantPosition);
        task.doTask();
        assert (recipeSteps.get(index).getRecipeTimers().contains(goal));


    }

    @Test
    public void DetectTimersInStep_doTask_TimerToBeMergedIsmerged() {
        int index = 8;
        DetectTimersInStepTask task = new DetectTimersInStepTask(recipe, index);
        RecipeTimer goal = new RecipeTimer(1 * 60 + 30, irrelevantPosition);
        task.doTask();
        assert (recipeSteps.get(index).getRecipeTimers().contains(goal));
    }


}
