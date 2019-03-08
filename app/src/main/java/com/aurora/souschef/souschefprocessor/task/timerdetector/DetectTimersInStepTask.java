package com.aurora.souschef.souschefprocessor.task.timerdetector;

import android.util.Log;

import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.recipe.RecipeTimer;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.SUTime;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

/**
 * A task that detects timers in mRecipeSteps
 */
public class DetectTimersInStepTask extends AbstractProcessingTask {
    private int mStepIndex;

    //TODO change detection of fractions and symbol notations into a non hard-coded solution
    private static final String FRACTION_HALF = "half";
    private static final Double FRACTION_HALF_MUL = 0.5;
    private static final String FRACTION_QUARTER = "quarter";
    private static final Double FRACTION_QUARTER_MUL = 0.25;
    private static final Integer MAX_FRACTION_DISTANCE = 15;

    private static final Integer MIN_TO_SECONDS = 60;
    private static final Integer HOUR_TO_SECONDS = 60*60;

    // Position of number in timex3 format (e.g. PT1H)
    private static final Integer TIMEX_NUM_POSITION = 2;

    private Map<String, Double> fractionMultipliers = new HashMap<>();

    public DetectTimersInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: " + stepIndex
                    + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }
        this.mStepIndex = stepIndex;
        this.fractionMultipliers.put(FRACTION_HALF, FRACTION_HALF_MUL);
        this.fractionMultipliers.put(FRACTION_QUARTER, FRACTION_QUARTER_MUL);
    }

    /**
     * Detects the RecipeTimer in all the mRecipeSteps
     */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        List<RecipeTimer> recipeTimers = detectTimer(recipeStep);
        recipeStep.setRecipeTimers(recipeTimers);
    }

    /**
     * Detects the timer in a recipeStep
     *
     * @param recipeStep The recipeStep in which to detect a timer
     * @return A timer detected in the recipeStep
     */
    private List<RecipeTimer> detectTimer(RecipeStep recipeStep) {
        List<RecipeTimer> list = new ArrayList<>();

        AnnotationPipeline pipeline = createTimerAnnotationPipeline();
        Annotation recipeStepAnnotated = new Annotation(recipeStep.getDescription());
        pipeline.annotate(recipeStepAnnotated);

        List<CoreLabel> allTokens = recipeStepAnnotated.get(CoreAnnotations.TokensAnnotation.class);

        // Map fractions to their start position in the recipe step
        Map<Integer, String> fractionPositions = getFractionPositions(allTokens);

        // Detect and calculate symbol notations for time durations in the recipeStep
        detectSymbolPattern(list, allTokens);

        List<CoreMap> timexAnnotations = recipeStepAnnotated.get(TimeAnnotations.TimexAnnotations.class);
        for (CoreMap cm : timexAnnotations) {
            int recipeStepSeconds;
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();

            //only one value
            if (!(temporal.getDuration() instanceof SUTime.DurationRange)) {
                recipeStepSeconds = (int) temporal
                        .getDuration().getJodaTimeDuration().getStandardSeconds();

                CoreLabel timexToken = cm.get(CoreAnnotations.TokensAnnotation.class).get(0);
                recipeStepSeconds = changeToFractions(fractionPositions, timexToken, recipeStepSeconds);
                try {
                    list.add(new RecipeTimer(recipeStepSeconds));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                    Log.e(TAG, "detectTimer: ", iae);
                }
            } else {
                //formattedstring is the only way to access private min and max fields in DurationRange object
                SUTime.DurationRange durationRange = (SUTime.DurationRange) temporal.getDuration();
                String formattedString = durationRange.toString();
                String[] minAndMax = formattedString.split("/");
                int lowerBound = getSecondsFromFormattedString(minAndMax[0]);
                int upperBound = getSecondsFromFormattedString(minAndMax[1]);
                try {
                    list.add(new RecipeTimer(lowerBound, upperBound));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                    Log.e(TAG, "detectTimer: ", iae);
                }
            }
        }
        return list;
    }

    /**
     * Creates custom annotation pipeline for timers
     * @return Annotation pipeline
     */
    private AnnotationPipeline createTimerAnnotationPipeline(){
        Properties props = new Properties();
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));
        return pipeline;
    }

    /**
     * Retrieves positions of fractions in the recipe step
     * @param allTokens tokens in a recipe step
     * @return Mapping of fractions to their position in the recipe step
     */
    private Map<Integer, String> getFractionPositions(List<CoreLabel> allTokens) {
        Map<Integer, String> fractionPositions = new HashMap<>();
        for(CoreLabel token : allTokens) {
            if (token.originalText().equals(FRACTION_HALF)) {
                fractionPositions.put(token.beginPosition(), FRACTION_HALF);
            } else if (token.originalText().equals(FRACTION_QUARTER)) {
                fractionPositions.put(token.beginPosition(), FRACTION_QUARTER);
            }
        }
        return fractionPositions;
    }

    /**
     * Detects symbol notations for timers in the recipe step
     * and adds their timer to the list of recipeTimers
     * @param recipeTimers list containing the recipeTimers in this recipe step
     * @param allTokens tokens in a recipe step
     */
    private void detectSymbolPattern(List<RecipeTimer> recipeTimers, List<CoreLabel> allTokens){
        for(CoreLabel token : allTokens) {
            if(token.originalText().matches("(\\d+)[h|m|s|H|M|S]")){
                try {
                    recipeTimers.add(new RecipeTimer(getSecondsFromFormattedString("PT" + token.originalText())));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                    Log.e(TAG, "detectTimer: ", iae);
                };
            }
        }
    }

    /**
     * Checks if fractions are in proximity to the timex token and adapts
     * the recipeStepSeconds to these fractions
     * @param fractionPositions position of fractions in the recipe step
     * @param timexToken token representing the time in this recipe step
     * @param recipeStepSeconds the seconds detected in this timex token
     * @return
     */
    private Integer changeToFractions(Map<Integer, String> fractionPositions,
                                      CoreLabel timexToken, int recipeStepSeconds){
        if(!fractionPositions.isEmpty()){
            for (Map.Entry<Integer, String> fractionPosition : fractionPositions.entrySet()) {
                int relPosition = fractionPosition.getKey() - timexToken.beginPosition();
                // Fraction in front of timex tag is assumed to be a decreasing multiplier (e.g. half an hour)
                // Fraction behind timex tag is assumed to be an increasing multiplier (e.g. for an hour and a half)
                if(-MAX_FRACTION_DISTANCE < relPosition && relPosition < 0){
                    recipeStepSeconds *= fractionMultipliers.get(fractionPosition.getValue());
                } else if(0 < relPosition && relPosition < MAX_FRACTION_DISTANCE){
                    recipeStepSeconds *= (1+fractionMultipliers.get(fractionPosition.getValue()));
                }
            }
        }
        return recipeStepSeconds;
    }

    /**
     * Converts formatted string to actual seconds
     * e.g. PT1H to 1 * 60 60 (3600) seconds
     * @param string formatted string
     * @return seconds
     */
    private static int getSecondsFromFormattedString(String string) {
        //TODO maybe this can be done less hardcoded, although for souschef I think this is good enough
        String number = string.substring(TIMEX_NUM_POSITION, string.length() - 1);
        int num = Integer.parseInt(number);
        char unit = string.charAt(string.length() - 1);
        if (Character.toLowerCase(unit) == 'm') {
            return num * MIN_TO_SECONDS;
        } else if (Character.toLowerCase(unit) == 'h') {
            return num * HOUR_TO_SECONDS;
        }
        return num;
    }
}
