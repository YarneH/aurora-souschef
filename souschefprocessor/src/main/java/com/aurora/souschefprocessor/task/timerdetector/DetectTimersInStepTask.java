package com.aurora.souschefprocessor.task.timerdetector;

import android.util.Log;

import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.recipe.RecipeTimer;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

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

import static android.content.ContentValues.TAG;

/**
 * A task that detects timers in mRecipeSteps
 */
public class DetectTimersInStepTask extends AbstractProcessingTask {
    //TODO change detection of fractions and symbol notations into a non hard-coded solution
    private static final String FRACTION_HALF = "half";
    private static final Double FRACTION_HALF_MUL = 0.5;
    private static final String FRACTION_QUARTER = "quarter";
    private static final Double FRACTION_QUARTER_MUL = 0.25;
    private static final Integer MAX_FRACTION_DISTANCE = 15;
    private static final Integer MIN_TO_SECONDS = 60;
    private static final Integer HOUR_TO_SECONDS = 60 * 60;
    // Position of number in timex3 format (e.g. PT1H)
    private static final Integer TIMEX_NUM_POSITION = 2;
    private int mStepIndex;
    private Map<String, Double> mFractionMultipliers = new HashMap<>();


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
        this.mFractionMultipliers.put(FRACTION_HALF, FRACTION_HALF_MUL);
        this.mFractionMultipliers.put(FRACTION_QUARTER, FRACTION_QUARTER_MUL);
    }

    /**
     * Converts formatted string to actual seconds
     * e.g. PT1H to 1 * 60 60 (3600) seconds
     *
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

    /**
     * Add spaces in a recipestep, needed for detection of timers. A space is needed if a dash is
     * present between two numbers so that the pipeline can see this as seperate tokens (e.g. 4-5 minutes
     * should become 4 - 5 minutes)
     *
     * @param recipeStepDescription The description in which to add spaces
     * @return the description with the necessary spaces added
     */
    private static String addSpaces(String recipeStepDescription) {
        StringBuilder bld = new StringBuilder();
        char[] chars = recipeStepDescription.toCharArray();

        bld.append(chars[0]);

        for (int index = 1; index < chars.length - 1; index++) {
            char previous = chars[index - 1];
            char current = chars[index];
            char next = chars[index + 1];

            boolean previousIsNumber = Character.isDigit(previous);
            boolean currentIsDash = (current == '-');
            boolean nexIsNumber = Character.isDigit(next);

            if (previousIsNumber && currentIsDash && nexIsNumber) {
                bld.append(" " + current + " ");
            } else {
                bld.append(current);
            }

        }
        // add final character
        bld.append(chars[chars.length - 1]);
        return bld.toString();

    }

    /**
     * Detects the RecipeTimer in all the mRecipeSteps
     */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        // trim and add spaces to the description
        recipeStep.setDescription(addSpaces(recipeStep.getDescription().trim()));
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
        Annotation recipeStepAnnotated = new Annotation((recipeStep.getDescription()));
        pipeline.annotate(recipeStepAnnotated);

        List<CoreLabel> allTokens = recipeStepAnnotated.get(CoreAnnotations.TokensAnnotation.class);

        // Map fractions to their start timerPosition in the recipe step
        Map<Integer, String> fractionPositions = getFractionPositions(allTokens);

        // Detect and calculate symbol notations for time durations in the recipeStep
        detectSymbolPattern(list, allTokens);



        List<CoreMap> timexAnnotations = recipeStepAnnotated.get(TimeAnnotations.TimexAnnotations.class);
        for (CoreMap cm : timexAnnotations) {
            // the detected seconds
            int recipeStepSeconds;

            List<CoreLabel> labelList = cm.get(CoreAnnotations.TokensAnnotation.class);
            // The first detected token
            CoreLabel firstTimexToken = labelList.get(0);
            // the last detected token
            CoreLabel lastTimexToken = labelList.get(labelList.size() - 1);

            // The position of the detected timer
            Position timerPosition = new Position(firstTimexToken.beginPosition(), lastTimexToken.endPosition());

            // The dected annotation
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();

            // two cases: DurationRange or Single value
            if (!(temporal.getDuration() instanceof SUTime.DurationRange)) {
                // single value

                recipeStepSeconds = (int) temporal
                        .getDuration().getJodaTimeDuration().getStandardSeconds();

                recipeStepSeconds = changeToFractions(fractionPositions, timerPosition, recipeStepSeconds);
                try {
                    list.add(new RecipeTimer(recipeStepSeconds, timerPosition));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                    Log.e(TAG, "detectTimer: ", iae);
                }
            } else {
                // case: durationRange
                //formattedstring is the only way to access private min and max fields in DurationRange object

                SUTime.DurationRange durationRange = (SUTime.DurationRange) temporal.getDuration();
                String formattedString = durationRange.toString();
                String[] minAndMax = formattedString.split("/");
                int lowerBound = getSecondsFromFormattedString(minAndMax[0]);
                int upperBound = getSecondsFromFormattedString(minAndMax[1]);
                try {
                    list.add(new RecipeTimer(lowerBound, upperBound, timerPosition));
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
     *
     * @return Annotation pipeline
     */
    private AnnotationPipeline createTimerAnnotationPipeline() {
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
     *
     * @param allTokens tokens in a recipe step
     * @return Mapping of fractions to their timerPosition in the recipe step
     */
    private Map<Integer, String> getFractionPositions(List<CoreLabel> allTokens) {
        Map<Integer, String> fractionPositions = new HashMap<>();
        for (CoreLabel token : allTokens) {
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
     *
     * @param recipeTimers list containing the recipeTimers in this recipe step
     * @param allTokens    tokens in a recipe step
     */
    private void detectSymbolPattern(List<RecipeTimer> recipeTimers, List<CoreLabel> allTokens) {
        for (CoreLabel token : allTokens) {
            if (token.originalText().matches("(\\d+)[h|m|s|H|M|S]")) {
                try {
                    Position timerPosition = new Position(token.beginPosition(), token.endPosition());
                    recipeTimers.add(new RecipeTimer(getSecondsFromFormattedString("PT" + token.originalText()), timerPosition));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                    Log.e(TAG, "detectTimer: ", iae);
                }
            }
        }
    }

    /**
     * Checks if fractions are in proximity to the timex token and adapts
     * the recipeStepSeconds to these fractions
     *
     * @param fractionPositions timerPosition of fractions in the recipe step
     * @param originalPosition        token representing the time in this recipe step
     * @param recipeStepSeconds the seconds detected in this timex token
     * @return The updated value of recipeStepSeconds
     */
    private int changeToFractions(Map<Integer, String> fractionPositions,
                                      Position originalPosition, int recipeStepSeconds) {
        if (!fractionPositions.isEmpty()) {
            for (Map.Entry<Integer, String> fractionPosition : fractionPositions.entrySet()) {
                int relPosition = fractionPosition.getKey() - originalPosition.getBeginIndex();
                // Fraction in front of timex tag is assumed to be a decreasing multiplier (e.g. half an hour)
                // Fraction behind timex tag is assumed to be an increasing multiplier (e.g. for an hour and a half)
                if (-MAX_FRACTION_DISTANCE < relPosition && relPosition < 0) {
                    recipeStepSeconds *= mFractionMultipliers.get(fractionPosition.getValue());
                    // change the position so that the multiplier is included in the position
                    originalPosition.setBeginIndex(fractionPosition.getKey());
                } else if (0 < relPosition && relPosition < MAX_FRACTION_DISTANCE) {
                    recipeStepSeconds *= (1 + mFractionMultipliers.get(fractionPosition.getValue()));
                    // change the position so that the multiplier is included in the position
                    originalPosition.setEndIndex(fractionPosition.getKey() + fractionPosition.getValue().length());
                }
            }
        }
        return recipeStepSeconds;
    }
}
