package com.aurora.souschef.souschefprocessor.task.timerdetector;

import android.util.Log;

import com.aurora.souschef.recipe.RecipeStep;
import com.aurora.souschef.recipe.RecipeTimer;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

import java.util.ArrayList;
import java.util.List;
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
    int mStepIndex;

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

        List<CoreMap> timexAnnotations = recipeStepAnnotated.get(TimeAnnotations.TimexAnnotations.class);
        for (CoreMap cm : timexAnnotations) {
            List<CoreLabel> tokens = cm.get(CoreAnnotations.TokensAnnotation.class);
            int recipeStepSeconds;
            SUTime.Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();
            if (!(temporal.getDuration() instanceof SUTime.DurationRange)) {
                //only one value
                recipeStepSeconds = (int) temporal
                        .getDuration().getJodaTimeDuration().getStandardSeconds();
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
        if (unit == 'M') {
            return num * 60;
        } else if (unit == 'H') {
            return num * 60 * 60;
        } else if (unit == 'S') {
            return num;
        }
        return 0;

    }
}
