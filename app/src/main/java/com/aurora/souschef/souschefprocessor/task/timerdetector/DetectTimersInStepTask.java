package com.aurora.souschef.souschefprocessor.task.timerdetector;

import android.util.Log;

import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.recipe.RecipeTimer;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

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

    private static final String FRACTION_HALF = "half";
    private static final String FRACTION_QUARTER = "quarter";

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
        this.fractionMultipliers.put(FRACTION_HALF, 0.5);
        this.fractionMultipliers.put(FRACTION_QUARTER, 0.25);
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

        Properties props = new Properties();

        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        pipeline.addAnnotator(new TimeAnnotator("sutime", props));

        Annotation recipeStepAnnotated = new Annotation(recipeStep.getDescription());

        pipeline.annotate(recipeStepAnnotated);

        // Map fractions to their start position in the recipe step
        // to compare their relative position to NER DURATION tags
        HashMap<Integer, String> fractionPositions = new HashMap<>();

        // Pattern for smybol notations
        String pattern = "(\\d+)[h|m|s|H|M|S]";
        Pattern symbolPattern = Pattern.compile(pattern);

        List<CoreLabel> allTokens = recipeStepAnnotated.get(CoreAnnotations.TokensAnnotation.class);
        for(CoreLabel token : allTokens){
            if(token.originalText().equals(FRACTION_HALF) ){
                fractionPositions.put(token.beginPosition(), FRACTION_HALF);
            }
            else if(token.originalText().equals(FRACTION_QUARTER)){
                fractionPositions.put(token.beginPosition(), FRACTION_QUARTER);
            }
            if(token.originalText().matches(pattern)){
                try {
                    list.add(new RecipeTimer(getSecondsFromFormattedString("PT" + token.originalText())));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                };
            }
        }

        List<CoreMap> timexAnnotations = recipeStepAnnotated.get(TimeAnnotations.TimexAnnotations.class);
        for (CoreMap cm : timexAnnotations) {
            int recipeStepSeconds;
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();

            //only one value
            if (!(temporal.getDuration() instanceof SUTime.DurationRange)) {
                recipeStepSeconds = (int) temporal
                        .getDuration().getJodaTimeDuration().getStandardSeconds();

                CoreLabel timexToken = cm.get(CoreAnnotations.TokensAnnotation.class).get(0);
                if(!fractionPositions.isEmpty()){
                    for (Map.Entry<Integer, String> fractionPosition : fractionPositions.entrySet()) {
                        int relPosition = fractionPosition.getKey() - timexToken.beginPosition();
                        // Fraction in front of timex tag is assumed to be a decreasing multiplier
                        // e.g. "half an hour" = 0.5 * 3600s
                        if(-15 < relPosition && relPosition < 0){
                            recipeStepSeconds *= fractionMultipliers.get(fractionPosition.getValue());
                        }
                        // Fraction behind timex tag is assumed to be an increasing multiplier
                        // e.g. "for an hour and a half" = 1.5 * 3600s
                        else if(0 < relPosition && relPosition < 15){
                            recipeStepSeconds *= (1+fractionMultipliers.get(fractionPosition.getValue()));
                        }
                    }
                }
                try {
                    list.add(new RecipeTimer(recipeStepSeconds));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                }
            } else {
                //formattedstring is the only way to access private min and max fields in DurationRange object
                SUTime.Duration durationRange = temporal.getDuration();

                String formattedString = temporal.toString();
                String[] minAndMax = formattedString.split("/");
                String min = minAndMax[0];
                String max = minAndMax[1];
                int lowerBound = getSecondsFromFormattedString(min);
                int upperBound = getSecondsFromFormattedString(max);
                try {
                    list.add(new RecipeTimer(lowerBound, upperBound));
                } catch (IllegalArgumentException iae) {
                    //TODO do something meaningful
                }
            }
        }

        return list;
    }

    private int getSecondsFromFormattedString(String string) {
        //TODO maybe this can be done less hardcoded, although for souschef I think this is good enough
        String number = string.substring(2, string.length() - 1);
        int num = Integer.parseInt(number);
        char unit = string.charAt(string.length() - 1);
        if (Character.toLowerCase(unit) == 'm') {
            return num * 60;
        } else if (Character.toLowerCase(unit) == 'h') {
            return num * 60 * 60;
        } else if (Character.toLowerCase(unit) == 's') {
            return num;
        }
        return 0;

    }
}
