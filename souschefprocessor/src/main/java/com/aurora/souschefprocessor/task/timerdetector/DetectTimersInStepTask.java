package com.aurora.souschefprocessor.task.timerdetector;

import android.util.Log;

import com.aurora.souschefprocessor.facade.Delegator;
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

    /**
     * A string representing the word half
     */
    private static final String FRACTION_HALF = "half";
    /**
     * A double representing 1/2 to be used when the string "half" is detected
     */
    private static final double FRACTION_HALF_MUL = 0.5;
    /**
     * A string representing the word half
     */
    private static final String FRACTION_QUARTER = "quarter";
    /**
     * A double representing 1/4 to be used when the string "quarter" is detected
     */
    private static final double FRACTION_QUARTER_MUL = 0.25;
    /**
     * An integer that states the maximum distance the words "half" or "quarter" can have to the
     * actual time detected
     */
    private static final int MAX_FRACTION_DISTANCE = 15;
    /**
     * An int representing the amount of seconds in a minute (60)
     */
    private static final int MIN_TO_SECONDS = 60;
    /**
     * An int representing the amount of seconds in an hour (3600)
     */
    private static final int HOUR_TO_SECONDS = 3600;
    /**
     * A constant for logging the steps of the pipeline
     */
    private static final String PIPELINE = "PIPELINE";
    /**
     * Position of number in timex3 format (e.g. PT1H)
     */
    private static final int TIMEX_NUM_POSITION = 2;
    /**
     * A lock that ensures the {@link #sAnnotationPipeline} is only created once
     */
    private static final Object LOCK = new Object();
    /**
     * A boolean that indicates if the pipelines have been created (or the creation has started)
     */
    private static boolean startedCreatingPipeline = false;
    /**
     * The Pipeline for annotating the text to detect timers
     */
    private static AnnotationPipeline sAnnotationPipeline;
    /**
     * A static map that matches the {@link #FRACTION_HALF} and {@link #FRACTION_QUARTER} strings to
     * their numerical values
     */
    private static Map<String, Double> sFractionMultipliers = new HashMap<>();

    /* populate the map and try to create the pipeline */
    static {
        sFractionMultipliers.put(FRACTION_HALF, FRACTION_HALF_MUL);
        sFractionMultipliers.put(FRACTION_QUARTER, FRACTION_QUARTER_MUL);
        initializeAnnotationPipeline();
    }

    /**
     * The step on which to do the detecting of timers
     */
    private RecipeStep recipeStep;

    /**
     * Constructs a DetectTimersInStepTask by using the {@link RecipeStep} with index stepIndex from the
     * passed recipeInprogress (also checks if the stepindex is valid)
     *
     * @param recipeInProgress The recipe to get the step from
     * @param stepIndex        the index of the step to get
     */
    public DetectTimersInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: " + stepIndex
                    + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }
        this.recipeStep = recipeInProgress.getRecipeSteps().get(stepIndex);
    }


    /**
     * Initializes the AnnotationPipeline, should be called before using the first detector. It also
     * checks if no other thread has already started to create the pipeline
     */
    public static void initializeAnnotationPipeline() {
        Thread initialize = new Thread(() -> {
            synchronized (LOCK) {
                if (startedCreatingPipeline) {
                    // creating already started or finished -> do not start again
                    return;
                }
                // ensure no other thread can initialize
                startedCreatingPipeline = true;
            }
            sAnnotationPipeline = createTimerAnnotationPipeline();
            synchronized (LOCK) {
                // get the lock again to notify that the pipeline has been created
                LOCK.notifyAll();
            }
        });
        initialize.start();
    }


    /**
     * Converts formatted string to actual seconds
     * e.g. PT1H to 1 * 60 * 60 (3600) seconds
     *
     * @param string formatted string
     * @return seconds from this formatted string
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
     * present between two numbers so that the pipeline can see this as seprate tokens (e.g. 4-5 minutes
     * should become 4 - 5 minutes)
     *
     * @param recipeStepDescription The description in which to add spaces
     * @return the description with the necessary spaces added
     */
    private static String addSpaces(String recipeStepDescription) {
        // if the description is empyt this is not a step
        if (recipeStepDescription.length() == 0) {
            return "";
        }
        StringBuilder bld = new StringBuilder();
        char[] chars = recipeStepDescription.toCharArray();

        bld.append(chars[0]);
        int length = chars.length;

        // loop for the second character to the second to last
        for (int index = 1; index < length - 1; index++) {
            char previous = chars[index - 1];
            char current = chars[index];
            char next = chars[index + 1];

            boolean previousIsNumber = Character.isDigit(previous);
            boolean currentIsDash = (current == '-');
            boolean nexIsNumber = Character.isDigit(next);

            if (previousIsNumber && currentIsDash && nexIsNumber) {
                // add spaces in the case of 4-5 -> 4 - 5
                bld.append(" ");
                bld.append(current);
                bld.append(" ");
            } else {
                bld.append(current);
            }

        }
        // add final character
        bld.append(chars[length - 1]);
        return bld.toString();

    }

    /**
     * Adds a durationRange (will have a different {@link RecipeTimer#mUpperBound} and {@link RecipeTimer#mUpperBound})
     * with its position to the list of detected timers
     *
     * @param durationRange the range to add
     * @param list          the list of detected timers
     * @param timerPosition the position of the durationRange
     */
    private static void addDurationToList(SUTime.DurationRange durationRange, List<RecipeTimer> list,
                                          Position timerPosition) {

        //formattedstring is the only way to access private min and max fields in DurationRange object
        String formattedString = durationRange.toString();
        String[] minAndMax = formattedString.split("/");
        try {

            int lowerBound = getSecondsFromFormattedString(minAndMax[0]);
            int upperBound = getSecondsFromFormattedString(minAndMax[1]);
            list.add(new RecipeTimer(lowerBound, upperBound, timerPosition));

        } catch (IllegalArgumentException iae) {
            //TODO do something meaningful
            Log.e(TAG, "detectTimer: ", iae);
        }
    }

    /**
     * Creates custom annotation pipeline for timers
     *
     * @return Annotation pipeline
     */
    private static AnnotationPipeline createTimerAnnotationPipeline() {
        Properties props = new Properties();

        // Do not use binders, these are necessary for Hollidays but those are not needed for
        // recipesteps
        // see https://mailman.stanford.edu/pipermail/java-nlp-user/2015-April/007006.html
        props.setProperty("sutime.binders", "0");
        Log.d(PIPELINE, "0");
        Delegator.incrementProgressAnnotationPipelines();
        AnnotationPipeline pipeline = new AnnotationPipeline();
        Log.d(PIPELINE, "1");
        Delegator.incrementProgressAnnotationPipelines();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        Log.d(PIPELINE, "2");
        Delegator.incrementProgressAnnotationPipelines();
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        Log.d(PIPELINE, "3");
        Delegator.incrementProgressAnnotationPipelines();
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        Log.d(PIPELINE, "4");
        Delegator.incrementProgressAnnotationPipelines();
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));
        Log.d(PIPELINE, "5");
        Delegator.incrementProgressAnnotationPipelines();
        return pipeline;
    }


    /**
     * Constructs a RecipeTimer from a temporal that does not represent a duration to the list
     *
     * @param temporal          The temporal of which a timer needs to be constructed
     * @param list              The list to add the timer to
     * @param timerPosition     The position of the temporal
     * @param cm                The Coremap which his the original representation of the temporal
     * @param fractionPositions The map of fractionpositions in the entire sentence
     */
    private static void addNonDurationToList(SUTime.Temporal temporal, List<RecipeTimer> list,
                                             Position timerPosition, CoreMap cm,
                                             Map<Integer, String> fractionPositions) {
        // the detected seconds
        int recipeStepSeconds;
        if ("overnight".equals(cm.toString())) {
            // overnight should not be a timer
            // this might be expanded to other tokens that do not require a timer
            recipeStepSeconds = 0;
        } else if ((temporal.getDuration() != null)) {
            recipeStepSeconds = (int) temporal
                    .getDuration().getJodaTimeDuration().getStandardSeconds();

        } else {
            // duration was null, try with formatted string
            try {
                recipeStepSeconds = getSecondsFromFormattedString(temporal.toString());
            } catch (NumberFormatException nfe) {
                Log.e("TIMERS", "DetectTimer: ", nfe);
                recipeStepSeconds = 0;
            }

        }

        recipeStepSeconds = changeToFractions(fractionPositions, timerPosition, recipeStepSeconds);

        try {
            list.add(new RecipeTimer(recipeStepSeconds, timerPosition));
        } catch (IllegalArgumentException iae) {
            //TODO do something meaningful
            Log.e(TAG, "detectTimer: ", iae);
        }
    }

    /**
     * Retrieves positions of fractions in the recipe step
     *
     * @param allTokens tokens in a recipe step
     * @return Mapping of fractions to their timerPosition in the recipe step
     */
    private static Map<Integer, String> getFractionPositions(List<CoreLabel> allTokens) {
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
     * and
     * s their timer to the list of recipeTimers
     *
     * @param recipeTimers list containing the recipeTimers in this recipe step
     * @param allTokens    tokens in a recipe step
     */
    private static void detectSymbolPattern(List<RecipeTimer> recipeTimers, List<CoreLabel> allTokens) {
        for (CoreLabel token : allTokens) {
            if (token.originalText().matches("(\\d+)(h|m|s|H|M|S)")) {
                try {
                    Position timerPosition = new Position(token.beginPosition(), token.endPosition());
                    recipeTimers.add(new RecipeTimer(getSecondsFromFormattedString
                            ("PT" + token.originalText()), timerPosition));
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
     * @param originalPosition  token representing the time in this recipe step
     * @param recipeStepSeconds the seconds detected in this timex token
     * @return The updated value of recipeStepSeconds
     */
    private static int changeToFractions(Map<Integer, String> fractionPositions,
                                         Position originalPosition, int recipeStepSeconds) {
        if (!fractionPositions.isEmpty()) {
            for (Map.Entry<Integer, String> fractionPosition : fractionPositions.entrySet()) {
                int relPosition = fractionPosition.getKey() - originalPosition.getBeginIndex();
                // Fraction in front of timex tag is assumed to be a decreasing multiplier (e.g. half an hour)
                // Fraction behind timex tag is assumed to be an increasing multiplier (e.g. for an hour and a half)
                if (-MAX_FRACTION_DISTANCE < relPosition && relPosition < 0) {
                    recipeStepSeconds *= sFractionMultipliers.get(fractionPosition.getValue());
                    // change the position so that the multiplier is included in the position
                    originalPosition.setBeginIndex(fractionPosition.getKey());
                } else if (0 < relPosition && relPosition < MAX_FRACTION_DISTANCE) {
                    recipeStepSeconds *= (1 + sFractionMultipliers.get(fractionPosition.getValue()));
                    // change the position so that the multiplier is included in the position
                    originalPosition.setEndIndex(fractionPosition.getKey() + fractionPosition.getValue().length());
                }
            }
        }
        return recipeStepSeconds;
    }

    /**
     * Detects the timer in a recipeStep
     *
     * @param recipeStep The recipeStep in which to detect a timer
     * @return A timer detected in the recipeStep
     */
    private List<RecipeTimer> detectTimer(RecipeStep recipeStep) {
        List<RecipeTimer> list = new ArrayList<>();
        while (sAnnotationPipeline == null) {
            try {
                // wait unitill the pipeline is created
                synchronized (LOCK) {
                    LOCK.wait();
                }
            } catch (InterruptedException e) {
                Log.d("Interrupted", "detecttimer", e);
                Thread.currentThread().interrupt();
            }
        }
        Annotation recipeStepAnnotated = new Annotation((recipeStep.getDescription()));
        sAnnotationPipeline.annotate(recipeStepAnnotated);

        List<CoreLabel> allTokens = recipeStepAnnotated.get(CoreAnnotations.TokensAnnotation.class);

        // Detect and calculate symbol notations for time durations in the recipeStep
        detectSymbolPattern(list, allTokens);

        // Map fractions to their start timerPosition in the recipe step
        Map<Integer, String> fractionPositions = getFractionPositions(allTokens);


        List<CoreMap> timexAnnotations = recipeStepAnnotated.get(TimeAnnotations.TimexAnnotations.class);
        for (CoreMap cm : timexAnnotations) {

            List<CoreLabel> labelList = cm.get(CoreAnnotations.TokensAnnotation.class);

            // The first detected token
            CoreLabel firstTimexToken = labelList.get(0);

            // the last detected token
            CoreLabel lastTimexToken = labelList.get(labelList.size() - 1);

            // The position of the detected timer = beginIndex of the first token, endIndex of the last token
            Position timerPosition = new Position(firstTimexToken.beginPosition(), lastTimexToken.endPosition());

            // The detected annotation
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();

            // two cases: DurationRange or Single value
            if (!(temporal.getDuration() instanceof SUTime.DurationRange)) {
                // single value
                addNonDurationToList(temporal, list, timerPosition, cm, fractionPositions);

            } else {
                // case: durationRange

                addDurationToList((SUTime.DurationRange) temporal.getDuration(), list, timerPosition);
            }
        }
        return list;
    }

    /**
     * Detects the RecipeTimer in all the mRecipeSteps. It first adds spaces to the description so
     * that tokens can be recognized (e.g. "4-5 minutes" should be "4 - 5 minutes"). Afterwards the
     * timers of this step are set to the detected timers.
     */
    public void doTask() {

        // trim and add spaces to the description
        recipeStep.setDescription(addSpaces(recipeStep.getDescription().trim()));
        List<RecipeTimer> recipeTimers = detectTimer(recipeStep);
        recipeStep.setRecipeTimers(recipeTimers);
    }
}
