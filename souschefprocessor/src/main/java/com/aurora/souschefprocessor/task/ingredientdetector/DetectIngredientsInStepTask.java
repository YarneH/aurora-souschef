package com.aurora.souschefprocessor.task.ingredientdetector;

import android.support.v4.util.Pair;

import com.aurora.souschefprocessor.recipe.Amount;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
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

// TODO add javadoc documentation
// TODO add Super class for DetectIngredientsTask to remove the duplicated code present atm.
/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInStepTask extends AbstractProcessingTask {

    // Strings for matching
    private static final String FRACTION_HALF = "half";
    private static final Double FRACTION_HALF_MUL = 0.5;
    private static final String FRACTION_QUARTER = "quarter";
    private static final Double FRACTION_QUARTER_MUL = 0.25;
    private static final String OF_PREPOSITION = "of";
    private static final int PREPOSITION_LENGTH = 1;
    private static final int FRACTIONS_LENGTH = 1;
    private static final int MAX_QUANTITY_LENGTH = 2;

    private Map<String, Double> mFractionMultipliers = new HashMap<>();

    // The number 10
    private static final double TEN = 10;

    // The size if a string representing a fraction is split on the regex "/"
    private static final int FRACTION_LENGTH = 2;
    // The size if a string representing a number (non-fraction) is split on the regex "/"
    private static final int NON_FRACTION_LENGTH = 1;
    // generally numbers greater than twelve are not spelled out
    private static final String[] NUMBERS_TO_TWELVE = {"zero", "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
    // multiples of ten are also spelled out
    private static final String[] MULTIPLES_OF_TEN = {"zero", "ten", "twenty", "thirty", "forty",
            "fifty", "sixty", "seventy", "eighty", "ninety", "hundred"};

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

            // Set default values for ingredient fields in case they can't be found in the step
            Ingredient stepIngredient = defaultStepIngredient(recipeStep.getDescription());

            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (int i = 0; i < tokens.size(); i++) {

                // This boolean eliminates unnecessary searching of the token in other ingredients of the list
                boolean foundName = false;
                Iterator it = ingredientListMap.entrySet().iterator();
                while(it.hasNext() && !foundName){
                    Map.Entry<ListIngredient, List<String>> entry = (Map.Entry<ListIngredient, List<String>>) it.next();
                    List<String> nameParts = entry.getValue();
                    Ingredient listIngredient = entry.getKey();

                    // Found name of an ingredient from the list of ingredients
                    if (nameParts.contains(tokens.get(i).originalText()) && !foundIngredients.contains(listIngredient)) {
                        foundIngredients.add(listIngredient);
                        stepIngredient.setName(listIngredient.getName());
                        Position namePos = new Position(tokens.get(i).beginPosition(), tokens.get(i).endPosition());
                        stepIngredient.setNamePosition(namePos);
                        foundName = true;

                        // Check if a quantity or unit is possible for this ingredient
                        if(isIsolatedName(tokens.subList(0, i-1))){
                            set.add(stepIngredient);

                            // Continue searching for other ingredients in the step
                            stepIngredient = defaultStepIngredient(recipeStep.getDescription());
                            continue;
                        }

                        // Default amount
                        Amount stepAmount = new Amount(0.0, "");

                        // Check if a quantity or unit can be found for this ingredient in the step
                        int unitLength = listIngredient.getUnit().split(" ").length;
                        int precedingLength = unitLength + PREPOSITION_LENGTH + FRACTIONS_LENGTH + MAX_QUANTITY_LENGTH;
                        List<CoreLabel> precedingTokens = tokens.subList(Math.max(0, i - (precedingLength)), i);
                        if(precedingTokens.size() > 0){
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
                            set.add(stepIngredient);
                            stepIngredient = defaultStepIngredient(recipeStep.getDescription());
                        }

                        // Check if the mentioned ingredient is being described by multiple words in the step
                        // Skip these words for further analysis of the recipe step
                        if(tokens.size() > i+1){
                            int maxNameIndex = Math.max(tokens.size()-1, entry.getValue().size()-1);
                            i += succeedingNameLength(tokens.subList(i+1, maxNameIndex), entry.getValue());
                        }
                    }
                }
            }
        }

        return set;
    }

    /**
     * Creates a default ingredient with values initialized to their absent value
     *
     * @param recipeStepDescription Complete recipe step string
     * @return Default ingredient
     */
    private Ingredient defaultStepIngredient(String recipeStepDescription){
        HashMap<Ingredient.PositionKey, Position> map = new HashMap<>();
        Position defaultPos = new Position(0, recipeStepDescription.length());
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

        // TODO add more ingredient separators, maybe "." but this might give errors
        // TODO as NLP might wrongfully split "." from abbreviated units
        List<String> ingredientSeparators = new ArrayList<>();
        ingredientSeparators.add(",");

        // Stop when another ingredient is found to prevent quantity overlap
        // CC means Coordinating conjunction, such as "and"
        int i = precedingTokens.size()-1;
        while(i > 0 && !ingredientSeparators.contains(precedingTokens.get(i).originalText())
                && !precedingTokens.get(i).tag().equals("CC")){
            // Detect cardinal numbers: fractions, numbers and verbose numbers
            boolean tokenIsQuantity = false;
            if(precedingTokens.get(i).tag().equals("CD")){
                stepQuantity *= calculateQuantity(Arrays.asList(precedingTokens.get(i)));
                tokenIsQuantity = true;
            }
            // Detect verbose fractions
            if(mFractionMultipliers.keySet().contains(precedingTokens.get(i).originalText())){
                stepQuantity *= mFractionMultipliers.get(precedingTokens.get(i).originalText());
                if(precedingTokens.get(precedingTokens.size()-1).tag().equals("DT")) {
                    stepQuantity *= listQuantity;
            }
                tokenIsQuantity = true;

            }
            if(tokenIsQuantity){
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
     * Calculates the quantity based on list with tokens labeled quantity
     *
     * @param list The list on which to calculate the quantity
     * @return a double representing the calculated value, if no value could be detected 1.0 is
     * returned
     */
    private double calculateQuantity(List<CoreLabel> list) {
        double result = 0.0;

        StringBuilder bld = new StringBuilder();
        for (CoreLabel cl : list) {
            bld.append(cl.word() + " ");
        }

        String representation = bld.toString();

        // split on all whitespace characters
        String[] array = representation.split("[\\s\\xA0]+");
        for (String s : array) {

            String[] fraction = s.split("/");
            try {
                // if the string was splitted in to two parts it was a fraction
                if (fraction.length == FRACTION_LENGTH) {

                    double numerator = Double.parseDouble(fraction[0]);
                    double denominator = Double.parseDouble(fraction[1]);
                    result += numerator / denominator;
                }

                if (fraction.length == NON_FRACTION_LENGTH) {
                    result += Double.parseDouble(s);
                }
            } catch (NumberFormatException iae) {
                // String identified as quantity is not parsable...
                result += calculateNonParsableQuantity(s);
            }
        }
        return result;
    }

    /**
     * Checks if the string is a spelled out version of the numbers 0 to 12 or a multiple of 10 (up to 100)
     *
     * @param s The string to be checked
     * @return The numeric representation of the string
     */
    private static double calculateNonParsableQuantity(String s) {
        String lower = s.toLowerCase(Locale.ENGLISH);
        // check if  number is 0-12
        for (int i = 0; i < NUMBERS_TO_TWELVE.length; i++) {
            if (lower.equals(NUMBERS_TO_TWELVE[i])) {
                return i;
            }
        }

        // check is string is a multiple of ten
        for (int i = 0; i < MULTIPLES_OF_TEN.length; i++) {
            if (lower.equals(MULTIPLES_OF_TEN[i])) {
                return i * TEN;
            }
        }

        // if not one of the previous cases consider wrongly labeled
        return 0.0;
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

        if(unitTokens.size() > 0){
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
        int precedingSize = precedingTokens.size();
        if(precedingSize > 0) {
            if (precedingTokens.get(precedingSize - 1).tag().equals("VB")) {
                return true;
            }
            return false;
        }
        // In case the ingredient name is the first word in the step
        return true;
    }

    /**
     * Sees if there are multiple words used to represent the ingredient
     * in the recipeStep
     *
     * @param succeedingTokens Tokens in front of detected name of an ingredient
     * @param ingredientNameParts Separated words of the list ingredient it's name
     * @return the amount of additional separated words used in the recipe step
     */
    private int succeedingNameLength(List<CoreLabel> succeedingTokens, List<String> ingredientNameParts){
        int succeedingLength = 0;
        for(CoreLabel token : succeedingTokens){
            if(ingredientNameParts.contains(token.originalText())){
                succeedingLength += 1;
            }
        }
        return succeedingLength;
    }

    /**
     * Creates custom annotation pipeline for detecting ingredients in a recipe step
     *
     * @return Annotation pipeline
     */
    private AnnotationPipeline createIngredientAnnotationPipeline() {
        Properties props = new Properties();
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        return pipeline;
    }

}
