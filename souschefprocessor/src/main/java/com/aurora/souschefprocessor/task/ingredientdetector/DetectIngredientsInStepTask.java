package com.aurora.souschefprocessor.task.ingredientdetector;

import android.support.v4.util.Pair;
import android.util.Log;

import com.aurora.souschefprocessor.facade.Delegator;
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
import java.util.Locale;
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

// TODO add exceptions for illegal arguments and add tests for these exceptions

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInStepTask extends DetectIngredientsTask {

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
     * A string representing the word of
     */
    private static final String OF_PREPOSITION = "of";
    /**
     * TODO: @Piet
     */
    private static final int PREPOSITION_LENGTH = 1;
    /**
     * TODO: @Piet
     */
    private static final int FRACTIONS_LENGTH = 1;
    /**
     * TODO: @Piet
     */
    private static final int MAX_QUANTITY_LENGTH = 2;

    /**
     * The default unit is set to the empty string
     */
    private static final String DEFAULT_UNIT = "";
    /**
     * Default quantity is 1.0
     */
    private static final double DEFAULT_QUANTITY = 1.0;

    /**
     * A lock to ensure the only one thread accesses the {@link #sAnnotationPipeline} at the same time
     * and that the pipeline is only created once
     */
    private static final Object LOCK = new Object();

    /**
     * An array of strings that should be ignored when looking for matches between the ingredientlist and
     * the step description
     */
    private static final String[] STRINGS_TO_IGNORE = {"to", "all", "or", "and", "with", ".", ",",
            "(", ")", "warm", "cold", "!"};
    /**
     * An array of tags that should be ignored when looking for matches between the ingredientlist and
     * the step description. For the meaning of these tags checkout
     * <a href="https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html">The PennTreeBankProject</a>
     */
    private static final String[] TAGS_TO_IGNORE = {"TO", "IN", "JJ", "JJR", "JJS"};
    /**
     * A boolean that indicates if the pipelines have been created (or the creation has started)
     */
    private static boolean startedCreatingPipeline = false;
    /**
     * The pipeline for annotating the sentences
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
     * The step on which to do the detecting of ingredients
     */
    private RecipeStep mRecipeStep;

    public DetectIngredientsInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }
        if (stepIndex >= recipeInProgress.getRecipeSteps().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: "
                    + stepIndex + " ,size of list: " + recipeInProgress.getRecipeSteps().size());
        }
        this.mRecipeStep = recipeInProgress.getRecipeSteps().get(stepIndex);

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
            sAnnotationPipeline = createIngredientAnnotationPipeline();
            synchronized (LOCK) {
                // get the lock again to notify that the pipeline has been created
                LOCK.notifyAll();
            }
        });
        initialize.start();
    }

    /**
     * Creates custom annotation pipeline for detecting ingredients in a recipe step
     *
     * @return Annotation pipeline
     */
    private static AnnotationPipeline createIngredientAnnotationPipeline() {
        AnnotationPipeline pipeline = new AnnotationPipeline();

        Log.d("INGREDIENTS:", "0");
        Delegator.incrementProgressAnnotationPipelines();
        pipeline.addAnnotator(new TokenizerAnnotator(false));
        Delegator.incrementProgressAnnotationPipelines();
        Log.d("INGREDIENTS:", "1");
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        Delegator.incrementProgressAnnotationPipelines();
        Log.d("INGREDIENTS:", "2");
        pipeline.addAnnotator(new POSTaggerAnnotator(false));
        Delegator.incrementProgressAnnotationPipelines();
        Log.d("INGREDIENTS:", "3");
        return pipeline;
    }


    /**
     * Detects the mIngredients for each recipeStep
     */
    public void doTask() {
        List<ListIngredient> ingredientListRecipe = mRecipeInProgress.getIngredients();
        Set<Ingredient> ingredientSet = detectIngredients(mRecipeStep, ingredientListRecipe);
        for (Ingredient ing : ingredientSet) {
            // make sure all the positions are legal and not longer than the length of the description
            ing.trimPositionsToString(mRecipeStep.getDescription());
        }
        mRecipeStep.setIngredients(ingredientSet);
    }

    /**
     * Waits  until the sAnnotationPipeline is created
     */
    private void waitForPipeline() {
        while (sAnnotationPipeline == null) {
            try {

                synchronized (LOCK) {
                    LOCK.wait();
                }
            } catch (InterruptedException e) {
                Log.d("Interrupted", "detecttimer", e);
                Thread.currentThread().interrupt();
            }
        }
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

        waitForPipeline();

        // Maps list ingredients to a an array of words in their name for matching the name in the step
        // Necessary in case only a certain word of the list ingredient is used to describe it in the step
        HashMap<ListIngredient, List<String>> ingredientListMap = new HashMap<>();
        for (ListIngredient listIngr : ingredientListRecipe) {
            ingredientListMap.put(listIngr, Arrays.asList(listIngr.getName().toLowerCase(Locale.ENGLISH)
                    .replace(",", "").split(" ")));
        }

        // Keeps track of already found ListIngredients in case the ingredient
        // is mentioned multiple times in the recipe step
        List<Ingredient> foundIngredients = new ArrayList<>();
        Annotation recipeStepAnnotated = new Annotation(recipeStep.getDescription());
        sAnnotationPipeline.annotate(recipeStepAnnotated);

        List<CoreMap> stepSentences = recipeStepAnnotated.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : stepSentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            int tokenIndex = 0;
            while (tokenIndex < tokens.size()) {
                // This boolean eliminates unnecessary searching of the token in other ingredients of the list
                boolean foundName = false;
                Iterator<Map.Entry<ListIngredient, List<String>>> it = ingredientListMap.entrySet().iterator();
                while (it.hasNext() && !foundName) {
                    Map.Entry<ListIngredient, List<String>> entry = it.next();
                    List<String> nameParts = entry.getValue();
                    Ingredient listIngredient = entry.getKey();

                    // Found name of an ingredient from the list of ingredients
                    if (namePartsContainsTokenOneCharOff(tokens.get(tokenIndex), nameParts)
                            && !foundIngredients.contains(listIngredient)) {
                        foundIngredients.add(listIngredient);
                        set.add(getStepIngredient(tokenIndex, nameParts, listIngredient, tokens));
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
     * Checks if a string should be ignored (if it is contained in the {@link #STRINGS_TO_IGNORE}
     * list
     *
     * @param string the string to check
     * @return false if the string should be ignored, true if the string should not be ignored
     */
    private boolean doNotIgnoreString(String string) {
        for (String ignore : STRINGS_TO_IGNORE) {
            if (string.equalsIgnoreCase(ignore)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a string should be ignored (if it is contained in the {@link #STRINGS_TO_IGNORE}
     * list or it is an adjective
     *
     * @param token the token to check
     * @return false if the token should be ignored, true if the token should not be ignored
     */
    private boolean doNotIgnoreToken(CoreLabel token) {
        String tokenText = token.originalText();
        if (!doNotIgnoreString(tokenText)) {
            return false;
        }
        for (String tagToIgnore : TAGS_TO_IGNORE) {
            if (token.tag().equals(tagToIgnore)) {
                return false;
            }
        }
        return true;


    }

    /**
     * Checks if two strings only differ in the fact that one character is not present to catch
     * plurals (e.g "onion" and "onions" and spelling mistakes "fettuccine" and "fettucine"
     *
     * @param string1 the first string
     * @param string2 the second string
     * @return a boolean indicating whether these strings differ in one erasure
     */
    private boolean differInOneErasure(String string1, String string2) {
        // check for one erasure

        int string2Length = string2.length();
        int string1Length = string1.length();
        int lengthDif = string1Length - string2Length;

        String longest = "";
        String shortest = "";
        int shortLength;
        boolean lengthDifIs1 = Math.abs(lengthDif) == 1;
        if (lengthDifIs1) {
            if (lengthDif > 0) {
                longest = string1;
                shortest = string2;
                shortLength = string2Length;
            } else {
                longest = string2;
                shortest = string1;
                shortLength = string1Length;
            }
            // check if longest just contains an extra character at the back
            // to bypass the loop
            if (longest.substring(0, shortLength).equalsIgnoreCase(shortest)) {
                return true;
            }

            boolean difFound = false;
            char shortChar;
            char longChar;
            for (int i = 0; i < shortLength; i++) {
                shortChar = shortest.charAt(i);
                if (!difFound) {
                    longChar = longest.charAt(i);
                    if (longChar != shortChar) {
                        difFound = true;
                        if (i > 0 && i == shortLength - 1) {
                            return false;
                        }

                    }
                }
                if (difFound) {
                    longChar = longest.charAt(i + 1);
                    if (longChar != shortChar) {
                        // second difference found
                        return false;
                    }


                }
                if (i == shortLength - 1) {
                    return true;
                }

            }
        }

        return false;
    }

    private boolean namePartsContainsTokenOneCharOff(CoreLabel token, List<String> nameParts) {
        String tokenText = token.originalText().toLowerCase(Locale.ENGLISH);

        if (doNotIgnoreToken((token))) {
            for (String part : nameParts) {
                if (doNotIgnoreString(part) && (part.equalsIgnoreCase(tokenText) || differInOneErasure(tokenText, part))) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks if there are multiple words used to represent the ingredient
     * in the recipeStep and returns the amount of used words
     *
     * @param tokens    Tokens in in the recipe step
     * @param nameParts Separated words of the list ingredient it's name
     * @return the amount of additional separated words used in the recipe step
     */
    private int succeedingNameLength(int tokenIndex, List<CoreLabel> tokens, List<String> nameParts) {
        int succeedingLength = 0;

        if ((tokens.size() - 1) > tokenIndex) {
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
     * @param nameIndex      Index of the found ingredient name in the list of tokens
     * @param listIngredient ListIngredient corresponding to this found ingredient name
     * @param tokens         List of tokens representing this sentence
     * @return Step Ingredient
     */
    private Ingredient getStepIngredient(int nameIndex, List<String> nameParts,
                                         Ingredient listIngredient, List<CoreLabel> tokens) {
        Ingredient stepIngredient = defaultStepIngredient();
        stepIngredient.setName(listIngredient.getName());

        // Find the other parts of the mentioned name
        int lastNameIndex = nameIndex + succeedingNameLength(nameIndex, tokens, nameParts);
        Position namePos = new Position(tokens.get(nameIndex).beginPosition(), tokens.get(lastNameIndex).endPosition());
        stepIngredient.setNamePosition(namePos);

        // Check if a quantity or unit is possible for this ingredient
        if (nameIndex > 0 && isIsolatedName(tokens.subList(0, nameIndex - 1))) {
            return stepIngredient;
        }

        // Default amount
        Amount stepAmount = new Amount(DEFAULT_QUANTITY, DEFAULT_UNIT);

        // Check if a quantity or unit can be found for this ingredient in the step
        int unitLength = listIngredient.getUnit().split(" ").length;
        int precedingLength = unitLength + PREPOSITION_LENGTH + FRACTIONS_LENGTH + MAX_QUANTITY_LENGTH;
        List<CoreLabel> precedingTokens = tokens.subList(Math.max(0, nameIndex - (precedingLength)), nameIndex);
        if (!precedingTokens.isEmpty()) {
            Position unitPos = findUnitPosition(precedingTokens, listIngredient.getUnit());
            if (unitPos != null) {
                stepIngredient.setUnitPosition(unitPos);
                stepAmount.setUnit(listIngredient.getUnit());
            }
            double listQuantity = listIngredient.getAmount().getValue();
            Pair<Position, Double> quantityPair = findQuantityPositionAndValue(precedingTokens, listQuantity);
            if (quantityPair != null) {
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
     * @return Default ingredient
     */
    private Ingredient defaultStepIngredient() {
        HashMap<Ingredient.PositionKeysForIngredients, Position> map = new HashMap<>();

        // Initialize position on Position(0, length)
        int stepSentenceLength = mRecipeStep.getDescription().length();
        Position defaultPos = new Position(0, stepSentenceLength);
        String name = "";
        map.put(Ingredient.PositionKeysForIngredients.NAME, defaultPos);
        String unit = DEFAULT_UNIT;
        map.put(Ingredient.PositionKeysForIngredients.UNIT, defaultPos);
        double quantity = DEFAULT_QUANTITY;
        map.put(Ingredient.PositionKeysForIngredients.QUANTITY, defaultPos);
        return new Ingredient(name, unit, quantity, map);
    }

    /**
     * @param precedingTokens Tokens in front of detected name of an ingredient
     * @param listQuantity    The quantity of the list ingredient tied to this detected ingredient name
     * @return A pair with both the start and end position of the quantity of this detected ingredient
     * and the quantity detected in the ingredient step
     */
    private Pair<Position, Double> findQuantityPositionAndValue(List<CoreLabel> precedingTokens, Double listQuantity) {
        double stepQuantity = 1.0;
        boolean foundQuantities = false;

        int beginPos = precedingTokens.get(precedingTokens.size() - 1).endPosition();
        int endPos = 0;

        List<String> ingredientSeparators = new ArrayList<>();
        ingredientSeparators.add(",");

        // Stop when another ingredient is found to prevent quantity overlap
        // CC means Coordinating conjunction, such as "and"
        int i = precedingTokens.size() - 1;
        while (i > 0 && !ingredientSeparators.contains(precedingTokens.get(i).originalText())
                && !"CC".equals(precedingTokens.get(i).tag())) {
            // Detect verbose fractions
            Pair<Boolean, Double> quantityVerbose = detectVerboseFractions(i, precedingTokens, listQuantity);
            stepQuantity *= quantityVerbose.second;

            // Detect cardinal numbers: fractions, numbers and verbose numbers
            Pair<Boolean, Double> quantityCardinal = detectCardinalFractions(i, precedingTokens);
            stepQuantity *= quantityCardinal.second;

            if (quantityVerbose.first || quantityCardinal.first) {
                if (precedingTokens.get(i).beginPosition() < beginPos) {
                    beginPos = precedingTokens.get(i).beginPosition();
                }
                if (precedingTokens.get(i).endPosition() > endPos) {
                    endPos = precedingTokens.get(i).endPosition();
                }
                foundQuantities = true;
            }
            i--;
        }

        if (foundQuantities) {
            return new Pair<>(new Position(beginPos, endPos), stepQuantity);
        }
        return null;
    }

    /**
     * checks whether the passed token is verbose notation of a fraction quantity
     * e.g. 'half' an apple is 0.5 of an apple
     * but 'half' the apples means it should be half of the initial apples in the ingredient list
     *
     * @param tokenIndex      current token to check if it is a quantity fraction
     * @param precedingTokens tokens preceding the detectec name of the ingredient
     * @return Pair with a boolean indicating whether a verbose quantity was detected and the quantity itself
     */
    private Pair<Boolean, Double> detectCardinalFractions(int tokenIndex, List<CoreLabel> precedingTokens) {
        double quantityMultiplier = 1.0;
        boolean tokenIsQuantity = false;
        if ("CD".equals(precedingTokens.get(tokenIndex).tag())) {
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
     * @param tokenIndex      current token to check if it is a quantity fraction
     * @param precedingTokens tokens preceding the detectec name of the ingredient
     * @param listQuantity    the initial quantity detected in the ingredient list
     * @return Pair with a boolean indicating whether a cardinal quantity was detected and the quantity itself
     */
    private Pair<Boolean, Double> detectVerboseFractions(int tokenIndex,
                                                         List<CoreLabel> precedingTokens, Double listQuantity) {
        double quantityMultiplier = 1.0;
        boolean tokenIsQuantity = false;
        if (sFractionMultipliers.keySet().contains(precedingTokens.get(tokenIndex).originalText())) {
            quantityMultiplier *= sFractionMultipliers.get(precedingTokens.get(tokenIndex).originalText());
            if ("DT".equals(precedingTokens.get(precedingTokens.size() - 1).tag())) {
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
     * @param unit            The unit name of the list ingredient tied to this detected ingredient name
     * @return The start and end position of the unit of this detected ingredient
     */
    private Position findUnitPosition(List<CoreLabel> precedingTokens, String unit) {
        List<CoreLabel> unitTokens = new ArrayList<>();
        if (precedingTokens.get(precedingTokens.size() - 1).originalText().equals(OF_PREPOSITION)) {
            precedingTokens.remove(precedingTokens.size() - 1);
        }

        // Add singulars to the possible unit names
        Morphology singularMorph = new Morphology();
        String[] unitParts = unit.split(" ");
        List<String> unitPartsWithSingulars = new ArrayList<>();
        for (int i = 0; i < unitParts.length; i++) {
            unitPartsWithSingulars.add(unitParts[i]);
            unitPartsWithSingulars.add(singularMorph.stem(unitParts[i]));
        }

        // TODO add additional condition that it should stop when no more unit strings are found
        // TODO for e.g. add one tablespoon of melted better
        // TODO this means first skipping the determiners and adjectives such as melted
        int i = precedingTokens.size() - 1;
        while (i > 0) {
            if (unitPartsWithSingulars.contains(precedingTokens.get(i).originalText())) {
                unitTokens.add(precedingTokens.get(i));
            }
            i--;
        }

        if (!unitTokens.isEmpty()) {
            int unitStart = unitTokens.get(0).beginPosition();
            int unitEnd = unitTokens.get(unitTokens.size() - 1).endPosition();
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
    private boolean isIsolatedName(List<CoreLabel> precedingTokens) {
        if (!precedingTokens.isEmpty()) {
            return ("VB".equals(precedingTokens.get(precedingTokens.size() - 1).tag()));
        }
        // In case the ingredient name is the first word in the step
        return true;
    }

}
