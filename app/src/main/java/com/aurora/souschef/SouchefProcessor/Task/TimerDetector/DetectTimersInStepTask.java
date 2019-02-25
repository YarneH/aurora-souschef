package com.aurora.souschef.SouchefProcessor.Task.TimerDetector;

import java.util.ArrayList;
import java.util.List;

import com.aurora.souschef.SouchefProcessor.Recipe.RecipeInProgress;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeStep;
import com.aurora.souschef.SouchefProcessor.Recipe.RecipeTimer;
import com.aurora.souschef.SouchefProcessor.Task.ProcessingTask;

import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.util.CoreMap;

/**
 * A task that detects timers in mRecipeSteps
 */
public class DetectTimersInStepTask extends ProcessingTask {
    int mStepIndex;

    public DetectTimersInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if(stepIndex < 0){
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()){
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: "+stepIndex +" ,size of list: "+recipeInProgress.getRecipeSteps().size());
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
            int recipeStepSeconds = (int) cm.get(TimeExpression.Annotation.class).getTemporal()
                    .getDuration().getJodaTimeDuration().getStandardSeconds();

            try{
                list.add(new RecipeTimer(recipeStepSeconds));
            }
            catch (IllegalArgumentException tvie) {
                //TODO do something meaningful
            }
        }

        return list;
    }
}
