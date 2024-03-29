package com.aurora.souschefprocessor.task.timerdetector;

import android.util.Log;
import android.util.SparseArray;

import com.aurora.souschefprocessor.facade.Delegator;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.recipe.RecipeTimer;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
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
     * actual time detected (when they are placed after the time
     */
    private static final int MAX_FRACTION_DISTANCE_AFTER = 10;

    /**
     * An integer that states the maximum distance the words "half" or "quarter" can have to the
     * actual time detected (when they are placed BEFORE the time)
     */
    private static final int MAX_FRACTION_DISTANCE_BEFORE = 6;

    /**
     * An int representing the amount of seconds in a minute (60)
     */
    private static final int MIN_TO_SECONDS = 60;

    /**
     * An int representing the amount of seconds in an hour (3600)
     */
    private static final int HOUR_TO_SECONDS = 3600;

    /**
     * Position of number in timex3 format (e.g. PT1H)
     */
    private static final int TIMEX_NUM_POSITION = 2;

    /**
     * A lock that ensures the {@link #sAnnotationPipeline} is only created once
     */
    private static final Object LOCK_DETECT_TIMERS_IN_STEP_PIPELINE = new Object();

    /**
     * A list of words that are detected by the annotator as time expresssion but which is not needed
     * for souschef (e.g "overnight")
     */
    private static final ArrayList<String> TIME_WORDS_NOT_TO_INCLUDE =
            new ArrayList<>(Arrays.asList("overnight", "spring", "summer", "fall", "autumn", "winter"));

    /**
     * A static map that matches the {@link #FRACTION_HALF} and {@link #FRACTION_QUARTER} strings to
     * their numerical values
     */
    private static final Map<String, Double> FRACTION_MULTIPLIERS = new HashMap<>();
    /**
     * A tag for logging purposes
     */
    private static final String LOG_TAG = DetectTimersInStepTask.class.getSimpleName();
    /**
     * A boolean that indicates if the pipelines have been created (or the creation has started)
     */
    private static boolean sStartedCreatingPipeline = false;
    /**
     * The Pipeline for annotating the text to detect timers
     */
    private static AnnotationPipeline sAnnotationPipeline;

    /* populate the map, fill the not include list and try to create the pipeline */
    static {
        FRACTION_MULTIPLIERS.put(FRACTION_HALF, FRACTION_HALF_MUL);
        FRACTION_MULTIPLIERS.put(FRACTION_QUARTER, FRACTION_QUARTER_MUL);

        // statically create the annotator as to only create this object once
        initializeAnnotationPipeline();
    }

    /**
     * The step on which to do the detecting of timers
     */
    private RecipeStepInProgress mRecipeStep;

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

        if (stepIndex >= recipeInProgress.getStepsInProgress().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: " + stepIndex
                    + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }

        this.mRecipeStep = recipeInProgress.getStepsInProgress().get(stepIndex);
    }

    /**
     * Initializes the AnnotationPipeline, should be called before using the first detector. It also
     * checks if no other thread has already started to create the pipeline
     */
    public static void initializeAnnotationPipeline() {
        synchronized (LOCK_DETECT_TIMERS_IN_STEP_PIPELINE) {
            if (sStartedCreatingPipeline) {
                // creating already started or finished -> do not start again
                return;
            }
            // ensure no other thread can initialize
            sStartedCreatingPipeline = true;
        }
        sAnnotationPipeline = createTimerAnnotationPipeline();
        synchronized (LOCK_DETECT_TIMERS_IN_STEP_PIPELINE) {
            // get the lock again to notify that the pipeline has been created
            LOCK_DETECT_TIMERS_IN_STEP_PIPELINE.notifyAll();
        }
    }

    /**
     * Creates custom annotation pipeline for timers. It uses the  {@link TimeAnnotator} using sutime.
     *
     * @return Annotation pipeline
     */
    private static AnnotationPipeline createTimerAnnotationPipeline() {
        Properties props = new Properties();
        // Do not use binders, these are necessary for Hollidays but those are not needed for recipesteps
        // see https://mailman.stanford.edu/pipermail/java-nlp-user/2015-April/007006.html
        props.setProperty("sutime.binders", "0");

        AnnotationPipeline pipeline = new AnnotationPipeline();


        pipeline.addAnnotator(new TimeAnnotator("sutime", props));
        Delegator.incrementProgressAnnotationPipelines();
        return pipeline;
    }

    /**
     * Detects the RecipeTimer in all the mRecipeSteps. It first adds spaces to the description so
     * that tokens can be recognized (e.g. "4-5 minutes" should be "4 - 5 minutes"). Afterwards the
     * timers of this step are set to the detected timers.
     */
    public void doTask() {
        List<RecipeTimer> recipeTimers = detectTimer(mRecipeStep);
        recipeTimers = mergeTimers(recipeTimers);
        mRecipeStep.setRecipeTimers(recipeTimers);
    }

    /**
     * Detects the timer in a mRecipeStep
     *
     * @param recipeStep The mRecipeStep in which to detect a timer
     * @return A list of timers detected in the mRecipeStep
     */
    private List<RecipeTimer> detectTimer(RecipeStepInProgress recipeStep) {
        List<RecipeTimer> listOfTimers = new ArrayList<>();

        waitForCreationOfPipeline();

        Annotation recipeStepAnnotated = new Annotation(recipeStep.getSentenceAnnotations());
        sAnnotationPipeline.annotate(recipeStepAnnotated);

        List<CoreLabel> allTokens = recipeStepAnnotated.get(CoreAnnotations.TokensAnnotation.class);

        // Detect and calculate symbol notations for time durations in the mRecipeStep
        detectSymbolPattern(listOfTimers, allTokens);

        // Map fractions to their start timerPosition in the recipe step
        SparseArray<String> fractionPositions = getFractionPositions(allTokens);

        // A boolean that indicates if the token after a timextoken is "to"
        boolean toFound = false;
        // The index of a "to" after a timextoken in the list of all tokens
        int toIndex = -1;

        List<CoreMap> timexAnnotations = recipeStepAnnotated.get(TimeAnnotations.TimexAnnotations.class);
        for (CoreMap cm : timexAnnotations) {

            boolean added = false;
            List<CoreLabel> labelList = cm.get(CoreAnnotations.TokensAnnotation.class);

            // The first detected token
            CoreLabel firstTimexToken = labelList.get(0);

            // the last detected token
            CoreLabel lastTimexToken = labelList.get(labelList.size() - 1);

            // if after the previous timex a "to" was present
            if (toFound) {
                int index = allTokens.indexOf(firstTimexToken);
                // if the first token of this cm is the first token after the "to" that is the first
                // token after the previous timextoken
                if (index == toIndex + 1) {
                    addAfterTo(listOfTimers, cm, lastTimexToken.endPosition());
                    // this token has been added
                    added = true;
                }
                // always set to false
                toFound = false;
            }

            // check if the previous token is of structure number-number
            int firstIndex = allTokens.indexOf(firstTimexToken);
            if (firstIndex > 0) {
                CoreLabel previousToken = allTokens.get(firstIndex - 1);
                String previous = previousToken.word();
                Position positionWithDashStructure = new Position(previousToken.beginPosition(),
                        lastTimexToken.endPosition());
                added = addWithDashStructure(listOfTimers, cm, previous, positionWithDashStructure);

            }

            if (!added) {
                // check if the next token is "to" and get the position of the token after this cm
                toFound = nextTokenIsTo(allTokens, lastTimexToken);
                toIndex = allTokens.indexOf(lastTimexToken) + 1;


                // The position of the detected timer = beginIndex of the first token, endIndex of the last token
                Position timerPosition = new Position(firstTimexToken.beginPosition(),
                        lastTimexToken.endPosition());

                // The detected annotation
                SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();

                // two cases: DurationRange or Single value
                if (!(temporal.getDuration() instanceof SUTime.DurationRange)) {
                    // single value
                    addNonDurationToList(temporal, listOfTimers, timerPosition, cm, fractionPositions);

                } else {
                    // case: durationRange
                    addDurationToList((SUTime.DurationRange) temporal.getDuration(), listOfTimers, timerPosition);

                }
            }
        }

        return listOfTimers;
    }

    /**
     * Merges timers in the list. Merging happens when two timers are only one position apart (space
     * character). When two timers are merged their values are added up. (e.g "1 minute 30 seconds" becomes
     * 90 seconds)
     *
     * @param list the list to do the merging in
     * @return a new list with the necessary timers merged
     */
    private List<RecipeTimer> mergeTimers(List<RecipeTimer> list) {
        // check if list is empty
        if (list.isEmpty()) {
            return list;
        }

        ArrayList<RecipeTimer> newList = new ArrayList<>();

        RecipeTimer current;
        RecipeTimer next;
        boolean lastAdded = false;

        int j = 0;

        while (j < list.size() - 1) {
            current = list.get(j);
            next = list.get(j + 1);
            Position currentPosition = current.getPosition();
            Position nextPosition = next.getPosition();
            // check if next and current are only one position apart
            if (currentPosition.getEndIndex() + 1 == nextPosition.getBeginIndex()) {
                Position newPosition = new Position(
                        currentPosition.getBeginIndex(), nextPosition.getEndIndex());
                newList.add(new RecipeTimer(current.getLowerBound() + next.getLowerBound(), newPosition));

                if (j + 1 == list.size() - 1) {
                    // if next was the last element set lastAdded to true
                    lastAdded = true;
                }
                // increment so that the next loop is skipped
                j++;
            } else {
                newList.add(current);
            }
            j++;
        }

        if (!lastAdded) {
            // add the last timer
            newList.add(list.get(list.size() - 1));
        }

        return newList;
    }

    /**
     * Waits untill the static {@link #sAnnotationPipeline} is created
     */
    private void waitForCreationOfPipeline() {
        while (sAnnotationPipeline == null) {
            try {
                // wait unitill the pipeline is created
                synchronized (LOCK_DETECT_TIMERS_IN_STEP_PIPELINE) {
                    LOCK_DETECT_TIMERS_IN_STEP_PIPELINE.wait();
                }
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "detecttimer", e);
                Thread.currentThread().interrupt();
            }
        }
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
            if (token.originalText().matches("(\\d+)[hmsHMS]")) {
                try {
                    Position timerPosition = new Position(token.beginPosition(), token.endPosition());
                    recipeTimers.add(new RecipeTimer(getSecondsFromFormattedString
                            ("PT" + token.originalText()), timerPosition));
                } catch (IllegalArgumentException iae) {
                    Log.e(LOG_TAG, "detectTimer: ", iae);
                }
            }
        }
    }

    /**
     * Retrieves positions of fractions in the recipe step
     *
     * @param allTokens tokens in a recipe step
     * @return Mapping of fractions to their timerPosition in the recipe step
     */
    private static SparseArray<String> getFractionPositions(List<CoreLabel> allTokens) {
        SparseArray<String> fractionPositions = new SparseArray<>();
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
     * Adds  a coremap with endindex to the list where this coremaps comes right after "to" and another
     * coremap with timexAnnotations (e.g. "50 minutes to 1 hour", the 1 hour should be added as upperbound to
     * the timer with "50 minutes" and not be to seperate timers)
     *
     * @param list     the list to add to
     * @param coreMap       the coremap that came after the "to" in the sentence
     * @param endIndex the endindex of the coremap
     */
    private void addAfterTo(List<RecipeTimer> list, CoreMap coreMap, int endIndex) {

        if (TIME_WORDS_NOT_TO_INCLUDE.contains(coreMap.toString())) {
            // these words should not be included so just return
            return;
        }
        // get the previous timer
        RecipeTimer previousTimer = list.remove(list.size() - 1);
        int prevLowerBound = previousTimer.getLowerBound();
        int prevStartIndex = previousTimer.getPosition().getBeginIndex();
        // Construct the new position
        Position position = new Position(prevStartIndex, endIndex);

        // The detected annotation
        SUTime.Temporal temporal = coreMap.get(TimeExpression.Annotation.class).getTemporal();

        int recipeStepSecondsAfterTo = (int) temporal
                .getDuration().getJodaTimeDuration().getStandardSeconds();

        RecipeTimer timer = new RecipeTimer(prevLowerBound, recipeStepSecondsAfterTo, position);
        list.add(timer);
    }

    private boolean addWithDashStructure(List<RecipeTimer> timers, CoreMap timeAnnotations, String previous,
                                         Position position) {
        if (previous.matches("[0-9]+[−–—―‒-][0-9]+")) {
            String[] bounds = previous.split("[−–—―‒-]");
            int lower = Integer.parseInt(bounds[0]);
            int upper = Integer.parseInt(bounds[1]);
            SUTime.Temporal temporal = timeAnnotations.get(TimeExpression.Annotation.class).getTemporal();
            if (!(temporal.getDuration() instanceof SUTime.DurationRange)) {
                int seconds = (int) temporal
                        .getDuration().getJodaTimeDuration().getStandardSeconds();
                lower *= seconds;
                upper *= seconds;
                timers.add(new RecipeTimer(lower, upper, position));
                return true;
            }
        }
        return false;

    }

    /**
     * Checks if the token after the lastTimexToken is the word "to"
     *
     * @param allTokens      the list of all tokens
     * @param lastTimexToken the last timex token
     * @return a boolean indicating if the next token is the word "to"
     */
    private boolean nextTokenIsTo(List<CoreLabel> allTokens, CoreLabel lastTimexToken) {
        int lastIndexInOriginalList = allTokens.indexOf(lastTimexToken);
        if (lastIndexInOriginalList < allTokens.size() - 1) {
            CoreLabel nextToken = allTokens.get(lastIndexInOriginalList + 1);

            return ("to").equalsIgnoreCase(nextToken.originalText());
        }

        return false;
    }

    /**
     * Constructs a RecipeTimer from a temporal that does not represent a duration to the list
     *
     * @param temporal          The temporal of which a timer needs to be constructed
     * @param list              The list to add the timer to
     * @param timerPosition     The position of the temporal
     * @param coreMap                The Coremap which his the original representation of the temporal
     * @param fractionPositions The map of fractionpositions in the entire sentence
     */
    private void addNonDurationToList(SUTime.Temporal temporal, List<RecipeTimer> list, Position timerPosition,
                                      CoreMap coreMap, SparseArray<String> fractionPositions) {
        // the detected seconds
        int recipeStepSeconds;
        if (TIME_WORDS_NOT_TO_INCLUDE.contains(coreMap.toString())) {
            // these tokens do not require a timer
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

        // try adding this element to the step
        try {
            list.add(new RecipeTimer(recipeStepSeconds, timerPosition));
        } catch (IllegalArgumentException iae) {
            // timer adding failed, do not try to add it again but log the failure
            Log.e(TAG, "detectTimer: ", iae);
        }
    }

    /**
     * Adds a durationRange (will have a different upperboune and lowerbounc
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
            // if adding failed just log the failure
            Log.e(LOG_TAG, "detectTimer: ", iae);
        }
    }

    /**
     * Converts formatted string to actual seconds
     * e.g. PT1H to 1 * 60 * 60 (3600) seconds
     *
     * @param string formatted string
     * @return seconds from this formatted string
     */
    private static int getSecondsFromFormattedString(String string) {

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
     * Checks if fractions are in proximity to the timex token and adapts
     * the recipeStepSeconds to these fractions
     *
     * @param fractionPositions timerPosition of fractions in the recipe step
     * @param originalPosition  token representing the time in this recipe step
     * @param recipeStepSeconds the seconds detected in this timex token
     * @return The updated value of recipeStepSeconds
     */
    private int changeToFractions(SparseArray<String> fractionPositions, Position originalPosition,
                                  int recipeStepSeconds) {

        for (int index = 0; index < fractionPositions.size(); index++) {
            int key = fractionPositions.keyAt(index);
            String value = fractionPositions.valueAt(index);
            int relPosition = key - originalPosition.getBeginIndex();
            if (relPosition < 0) {
                // no comma allowed between the fraction and the timer
                recipeStepSeconds *= calculateMultiplierBefore(key, value, originalPosition,
                        relPosition);

            } else {
                relPosition = key - originalPosition.getEndIndex();
                if (0 < relPosition) {
                    recipeStepSeconds *= calculateMultiplierAfter(key, value, originalPosition,
                            relPosition);
                }
            }
        }

        return recipeStepSeconds;
    }

    /**
     * Calculates the multiplier when one of fraction multipliers was detected before the timer
     *
     * @param beginPositionFraction the begin position of the fraction multiplier
     * @param fractionString        the string of the multiplier
     * @param originalPosition      the position of the timer
     * @param positionsDistance     the distance between the end of the multiplier and the timer
     * @return the multiplier that the fractionString represents
     */
    private double calculateMultiplierBefore(int beginPositionFraction, String fractionString,
                                             Position originalPosition, int positionsDistance) {

        double multiplier = 1.0;
        String description = mRecipeStep.getDescription();
        // if there is a comma between 'half"/'quarter' and the found value, ignore this 'half'/'quarter'
        boolean containsComma = description.substring(
                beginPositionFraction, originalPosition.getEndIndex()).contains(",");

        if (!containsComma && -MAX_FRACTION_DISTANCE_BEFORE <= positionsDistance) {
            Double key = FRACTION_MULTIPLIERS.get(fractionString);
            if (key == null) {
                // should not get here but if it does return multiplier 1.0 to not harm anythin
                return 1.0;
            }
            multiplier = key;
            // change the position so that the multiplier is included in the position
            originalPosition.setBeginIndex(beginPositionFraction);
        }

        return multiplier;
    }

    /**
     * Calculates the multiplier when one of fraction multipliers was detected after the timer
     *
     * @param beginPositionFraction the begin position of the fraction multiplier
     * @param fractionString        the string of the multiplier
     * @param originalPosition      the position of the timer
     * @param positionsDistance     the distance between the end of the multiplier and the timer
     * @return the multiplier that the fractionString represents
     */
    private double calculateMultiplierAfter(int beginPositionFraction, String fractionString,
                                            Position originalPosition, int positionsDistance) {
        double multiplier = 1.0;
        String description = mRecipeStep.getDescription();
        boolean containsComma = description.substring(
                originalPosition.getEndIndex(), beginPositionFraction).contains(",");

        if (!containsComma && positionsDistance <= MAX_FRACTION_DISTANCE_AFTER) {
            Double key = FRACTION_MULTIPLIERS.get(fractionString);
            if (key == null) {
                // should not get here but if it does return multiplier 1.0 to not harm anythin
                return 1.0;
            }
            // after the timer, so considered increasing
            multiplier = 1 + key;
            // change the position so that the multiplier is included in the position
            originalPosition.setEndIndex(beginPositionFraction +
                    fractionString.length());
        }

        return multiplier;
    }
}
