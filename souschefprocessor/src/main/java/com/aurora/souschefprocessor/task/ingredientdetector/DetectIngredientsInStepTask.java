package com.aurora.souschefprocessor.task.ingredientdetector;

import android.support.v4.util.Pair;

import com.aurora.souschefprocessor.recipe.Amount;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.util.CoreMap;

import static android.content.ContentValues.TAG;

// TODO add exceptions for illegal arguments and add tests for these exceptions
/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInStepTask extends DetectIngredientsTask {

    // Strings for matching in recipe step
    private static final String FRACTION_HALF = "half";
    private static final Double FRACTION_HALF_MUL = 0.5;
    private static final String FRACTION_QUARTER = "quarter";
    private static final Double FRACTION_QUARTER_MUL = 0.25;
    private static final String OF_PREPOSITION = "of";
    private static final int PREPOSITION_LENGTH = 1;
    private static final int FRACTIONS_LENGTH = 1;
    private static final int MAX_QUANTITY_LENGTH = 2;

    private Map<String, Double> mFractionMultipliers = new HashMap<>();

    private int mStepIndex;

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

        this.mFractionMultipliers.put(FRACTION_HALF, FRACTION_HALF_MUL);
        this.mFractionMultipliers.put(FRACTION_QUARTER, FRACTION_QUARTER_MUL);
    }

    /**
     * Detects the mIngredients for each recipeStep
     */
    public void doTask() {
        RecipeStep recipeStep = mRecipeInProgress.getRecipeSteps().get(mStepIndex);
        List<ListIngredient> ingredientListRecipe = mRecipeInProgress.getIngredients();
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
    private Set<Ingredient> detectIngredients(RecipeStep recipeStep, List<ListIngredient> ingredientListRecipe) {
        Set<Ingredient> set = new HashSet<>();

        // Maps list ingredients to a an array of words in their name for matching the name in the step
        // Necessary in case only a certain word of the list ingredient is used to describe it in the step
        HashMap<ListIngredient, List<String>> ingredientListMap = new HashMap<>();
        for (ListIngredient listIngr : ingredientListRecipe) {
            ingredientListMap.put(listIngr, Arrays.asList(listIngr.getName().toLowerCase().split(" ")));
        }

        // Keeps track of already found ListIngredients in case the ingredient
        // is mentioned multiple times in the recipe step
        List<Ingredient> foundIngredients = new ArrayList<>();

        AnnotationPipeline pipeline = createIngredientAnnotationPipeline();
        Annotation recipeStepAnnotated = new Annotation(recipeStep.getDescription());
        pipeline.annotate(recipeStepAnnotated);

        List<CoreMap> stepSentences = recipeStepAnnotated.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : stepSentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            int tokenIndex = 0;
            while(tokenIndex < tokens.size()){
                // This boolean eliminates unnecessary searching of the token in other ingredients of the list
                boolean foundName = false;
                Iterator it = ingredientListMap.entrySet().iterator();
                while(it.hasNext() && !foundName){
                    Map.Entry<ListIngredient, List<String>> entry = (Map.Entry<ListIngredient, List<String>>) it.next();
                    List<String> nameParts = entry.getValue();
                    Ingredient listIngredient = entry.getKey();

                    // Found name of an ingredient from the list of ingredients
                    if (nameParts.contains(tokens.get(tokenIndex).originalText())
                            && !foundIngredients.contains(listIngredient)) {
                        foundIngredients.add(listIngredient);
                        set.add(getStepIngredient(tokenIndex, listIngredient, tokens));
                        foundName = true;

                        // Check if the mentioned ingredient is being described by multiple words in the step
                        // Skip these words for further analysis of the recipe step
                        tokenIndex += succeedingNameLength(tokenIndex, tokens, entry.getValue());

                    }
                }
                tokenIndex++;
            }
        }

        return set;
    }

    /**
     * Checks if there are multiple words used to represent the ingredient
     * in the recipeStep and returns the amount of used words
     *
     * @param tokens Tokens in front of detected name of an ingredient
     * @param nameParts Separated words of the list ingredient it's name
     * @return the amount of additional separated words used in the recipe step
     */
    private int succeedingNameLength(int tokenIndex, List<CoreLabel> tokens, List<String> nameParts){
        int succeedingLength = 0;

        if((tokens.size()-1) > tokenIndex) {
            int maxNameIndex = Math.max(tokens.size() - 1, nameParts.size() - 1);
            List<CoreLabel> succeedingTokens = tokens.subList(tokenIndex + 1, maxNameIndex);
            for (CoreLabel token : succeedingTokens) {
                if (nameParts.contains(token.originalText())) {
                    succeedingLength += 1;
                }
            }
        }
        return succeedingLength;
    }

    /**
     * Finds the attributes (name, unit and quantity) of the step ingredient in the recipe step sentence
     * If some attributes can't be found they are set to their default absent value
     *
     * @param nameIndex Index of the found ingredient name in the list of tokens
     * @param listIngredient ListIngredient corresponding to this found ingredient name
     * @param tokens List of tokens representing this sentence
     * @return Step Ingredient
     */
    private Ingredient getStepIngredient(int nameIndex, Ingredient listIngredient, List<CoreLabel> tokens){
        Ingredient stepIngredient = defaultStepIngredient(tokens.get(tokens.size()-1).endPosition());
        stepIngredient.setName(listIngredient.getName());
        Position namePos = new Position(tokens.get(nameIndex).beginPosition(), tokens.get(nameIndex).endPosition());
        stepIngredient.setNamePosition(namePos);

        // Check if a quantity or unit is possible for this ingredient
        if(isIsolatedName(tokens.subList(0, nameIndex-1))){
            return stepIngredient;
        }

        // Default amount
        Amount stepAmount = new Amount(1.0, "");

        // Check if a quantity or unit can be found for this ingredient in the step
        int unitLength = listIngredient.getUnit().split(" ").length;
        int precedingLength = unitLength + PREPOSITION_LENGTH + FRACTIONS_LENGTH + MAX_QUANTITY_LENGTH;
        List<CoreLabel> precedingTokens = tokens.subList(Math.max(0, nameIndex - (precedingLength)), nameIndex);
        if(!precedingTokens.isEmpty()){
            Position unitPos = findUnitPosition(precedingTokens, listIngredient.getUnit());
            if(unitPos != null){
                stepIngredient.setUnitPosition(unitPos);
                stepAmount.setUnit(listIngredient.getUnit());
            }
            Double listQuantity = listIngredient.getAmount().getValue();
            Pair<Position, Double> quantityPair = findQuantityPositionAndValue(precedingTokens, listQuantity);
            if(quantityPair != null){
                stepIngredient.setQuantityPosition(quantityPair.first);
                stepAmount.setValue(quantityPair.second);
            }
            stepIngredient.setmAmount(stepAmount);
        }
        return stepIngredient;
    }

    /**
     * Creates a default ingredient with values initialized to their absent value
     *
     * @param stepSentenceLength The length of the step sentence
     * @return Default ingredient
     */
    private Ingredient defaultStepIngredient(int stepSentenceLength){
        HashMap<Ingredient.PositionKey, Position> map = new HashMap<>();
        Position defaultPos = new Position(0, stepSentenceLength);
        String name = "";
        map.put(Ingredient.PositionKey.NAME, defaultPos);
        String unit = "";
        map.put(Ingredient.PositionKey.UNIT, defaultPos);
        Double quantity = 0.0;
        map.put(Ingredient.PositionKey.QUANTITY, defaultPos);
        return new Ingredient(name, unit, quantity, map);
    }

    /**
     *
     *
     * @param precedingTokens Tokens in front of detected name of an ingredient
     * @param listQuantity The quantity of the list ingredient tied to this detected ingredient name
     *
     * @return A pair with both the start and end position of the quantity of this detected ingredient
     * and the quantity detected in the ingredient step
     */
    private Pair<Position, Double> findQuantityPositionAndValue(List<CoreLabel> precedingTokens, Double listQuantity){
        Double stepQuantity = 1.0;
        boolean foundQuantities = false;

        int beginPos = precedingTokens.get(precedingTokens.size()-1).endPosition();
        int endPos = 0;

        List<String> ingredientSeparators = new ArrayList<>();
        ingredientSeparators.add(",");

        // Stop when another ingredient is found to prevent quantity overlap
        // CC means Coordinating conjunction, such as "and"
        int i = precedingTokens.size()-1;
        while(i > 0 && !ingredientSeparators.contains(precedingTokens.get(i).originalText())
                && !"CC".equals(precedingTokens.get(i).tag())){
            // Detect verbose fractions
            Pair<Boolean, Double> quantityVerbose = detectVerboseFractions(i, precedingTokens, listQuantity);
            stepQuantity *= quantityVerbose.second;

            // Detect cardinal numbers: fractions, numbers and verbose numbers
            Pair<Boolean, Double> quantityCardinal = detectCardinalFractions(i, precedingTokens);
            stepQuantity *= quantityCardinal.second;

            if(quantityVerbose.first || quantityCardinal.first){
                if(precedingTokens.get(i).beginPosition() < beginPos){
                    beginPos = precedingTokens.get(i).beginPosition();
                }
                if(precedingTokens.get(i).endPosition() > endPos){
                    endPos = precedingTokens.get(i).endPosition();
                }
                foundQuantities = true;
            }
            i--;
        }

        if(foundQuantities){
            return new Pair<>(new Position(beginPos, endPos), stepQuantity);
        }
        return null;
    }

    /**
     * checks whether the passed token is verbose notation of a fraction quantity
     * e.g. 'half' an apple is 0.5 of an apple
     * but 'half' the apples means it should be half of the initial apples in the ingredient list
     *
     * @param tokenIndex current token to check if it is a quantity fraction
     * @param precedingTokens tokens preceding the detectec name of the ingredient
     * @return Pair with a boolean indicating whether a verbose quantity was detected and the quantity itself
     */
    private Pair<Boolean, Double> detectCardinalFractions(int tokenIndex, List<CoreLabel> precedingTokens){
        Double quantityMultiplier = 1.0;
        Boolean tokenIsQuantity = false;
        if("CD".equals(precedingTokens.get(tokenIndex).tag())){
            quantityMultiplier *= calculateQuantity(Arrays.asList(precedingTokens.get(tokenIndex)));
            tokenIsQuantity = true;
        }
        return new Pair<>(tokenIsQuantity, quantityMultiplier);
    }

    /**
     * checks whether the passed token is a cardinal quantity
     * this includes a numerical, fraction and verbose description of the quantity
     * e.g. '1/5' cup of salt OR '15' cups of salt or 'five' cups of salt
     *
     * @param tokenIndex current token to check if it is a quantity fraction
     * @param precedingTokens tokens preceding the detectec name of the ingredient
     * @param listQuantity the initial quantity detected in the ingredient list
     * @return Pair with a boolean indicating whether a cardinal quantity was detected and the quantity itself
     */
    private Pair<Boolean, Double> detectVerboseFractions(int tokenIndex,
                                                         List<CoreLabel> precedingTokens, Double listQuantity){
        Double quantityMultiplier = 1.0;
        Boolean tokenIsQuantity = false;
        if(mFractionMultipliers.keySet().contains(precedingTokens.get(tokenIndex).originalText())){
            quantityMultiplier *= mFractionMultipliers.get(precedingTokens.get(tokenIndex).originalText());
            if("DT".equals(precedingTokens.get(precedingTokens.size()-1).tag())) {
                quantityMultiplier *= listQuantity;
            }
            tokenIsQuantity = true;
        }
        return new Pair<>(tokenIsQuantity, quantityMultiplier);
    }

    /**
     * Finds the position of unit tokens in the recipe step
     *
     * @param precedingTokens Tokens in front of detected name of an ingredient
     * @param unit The unit name of the list ingredient tied to this detected ingredient name
     * @return The start and end position of the unit of this detected ingredient
     */
    private Position findUnitPosition(List<CoreLabel> precedingTokens, String unit){
        List<CoreLabel> unitTokens = new ArrayList<>();
        if(precedingTokens.get(precedingTokens.size()-1).originalText().equals(OF_PREPOSITION)){
            precedingTokens.remove(precedingTokens.size()-1);
        }

        // Add singulars to the possible unit names
        Morphology singularMorph = new Morphology();
        String[] unitParts = unit.split(" ");
        List<String> unitPartsWithSingulars = new ArrayList<>();
        for(int i = 0; i < unitParts.length; i++){
            unitPartsWithSingulars.add(unitParts[i]);
            unitPartsWithSingulars.add(singularMorph.stem(unitParts[i]));
        }

        // TODO add additional condition that it should stop when no more unit strings are found
        // TODO for e.g. add one tablespoon of melted better
        // TODO this means first skipping the determiners and adjectives such as melted
        int i = precedingTokens.size()-1;
        while(i > 0){
            if(unitPartsWithSingulars.contains(precedingTokens.get(i).originalText())){
                unitTokens.add(precedingTokens.get(i));
            }
            i--;
        }

        if(!unitTokens.isEmpty()){
            int unitStart = unitTokens.get(0).beginPosition();
            int unitEnd = unitTokens.get(unitTokens.size()-1).endPosition();
            return new Position(unitStart, unitEnd);
        }
        return null;
    }

    /**
     * Checks whether there can be quantities or unit's found in front of the ingredient it's name
     * For verbs before a name e.g. 'put' celery or 'add' celery there will be no unit
     *
     * @param precedingTokens Tokens in front of detected name of an ingredient
     * @return True if quantity tokens or unit tokens might be present
     */
    private boolean isIsolatedName(List<CoreLabel> precedingTokens){
        if(!precedingTokens.isEmpty()) {
            return ("VB".equals(precedingTokens.get(precedingTokens.size()-1).tag()));
        }
        // In case the ingredient name is the first word in the step
        return true;
    }

    /**
     * Creates custom annotation pipeline for detecting ingredients in a recipe step
     *
     * @return Annotation pipeline
     */
    private AnnotationPipeline createIngredientAnnotationPipeline() {
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        return pipeline;
    }

}
