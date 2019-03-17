package com.aurora.souschefprocessor.task.ingredientdetector;

import android.net.Uri;
import android.util.Log;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import static android.content.ContentValues.TAG;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInStepTask extends AbstractProcessingTask {
    private static final String RAW_RESOURCE_DIR = "src/main/res/raw/";
    private static final String INGREDIENT_NER_TAG = "INGREDIENT";

    private int mStepIndex;

    //NEW
    DetectIngredientsInListTask mDetectIngredientsInListTask;

    public DetectIngredientsInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: "
                    + stepIndex + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }
        this.mStepIndex = stepIndex;
    }

    /**
     * Detects the mIngredients for each recipeStep
     */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        List<Ingredient> ingredientListRecipe = mRecipeInProgress.getIngredients();
        Set<Ingredient> iuaSet = detectIngredients(recipeStep, ingredientListRecipe);
        recipeStep.setIngredients(iuaSet);
    }

    /**
     * Detects the set of mIngredients in a recipeStep. It also checks if this corresponds with the mIngredients of the
     * recipe.
     *
     * @param recipeStep           The recipeStep on which to detect the mIngredients
     * @param ingredientListRecipe The set of mIngredients contained in the recipe of which the recipeStep is a part
     * @return A set of Ingredient objects that represent the mIngredients contained in the recipeStep
     */
    private Set<Ingredient> detectIngredients(RecipeStep recipeStep, List<Ingredient> ingredientListRecipe) {
        // TODO generate functionality
        Set<Ingredient> set = new HashSet<>();
        if (ingredientListRecipe != null) {
            //Creates temporary rule file
            File tempRuleFile = null;
            try {
                tempRuleFile = File.createTempFile("ingredientrulefile", ".txt", new File(RAW_RESOURCE_DIR));
                tempRuleFile.deleteOnExit();
                FileWriter writer = new FileWriter(tempRuleFile);
                for (Ingredient ingr : ingredientListRecipe){
                    writer.write(ingr.getName() + "\t" + INGREDIENT_NER_TAG + "\n");
                }
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Detect ingredients in step: failed to create and write to temporary rule file.", e);
                tempRuleFile.delete();
            }

            AnnotationPipeline pipeline = createIngredientAnnotationPipeline(tempRuleFile.getPath());
            Annotation recipeStepAnnotated = new Annotation(recipeStep.getDescription());
            pipeline.annotate(recipeStepAnnotated);

            List<CoreMap> sentences = recipeStepAnnotated.get(CoreAnnotations.SentencesAnnotation.class);
            for(CoreMap sentence : sentences){
                for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    // this is the NER label of the token
                    String nerTag = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                    if(nerTag.equals(INGREDIENT_NER_TAG)){
                        Ingredient ingr = findInIngredientList(token.originalText(), ingredientListRecipe);
                        if(ingr != null){
                            set.add(ingr);
                        }
                    }
                }
            }
        }
        return set;
    }

    public Ingredient findInIngredientList(String ingredientName, List<Ingredient> ingredientListRecipe){
        for(Ingredient ingr : ingredientListRecipe){
            if(ingr.getName().equals(ingredientName)){
                return ingr;
            }
        }
        return null;
    }


    /**
     * Creates custom annotation pipeline for detecting ingredients in a recipe step
     * Uses statistical NER tagging before applying a custom NER rule file defined in regexnerMappingPath
     *
     * @param regexnerMappingPath   Path to rule file
     * @return Annotation pipeline
     */
    private AnnotationPipeline createIngredientAnnotationPipeline(String regexnerMappingPath) {
        //TODO try to customise the pipeline
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma, ner, regexner");
        props.put("regexner.mapping", regexnerMappingPath);
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        return pipeline;
    }


}
