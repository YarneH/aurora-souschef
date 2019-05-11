package com.aurora.souschefprocessor.task.ingredientdetector;

import android.support.v4.util.Pair;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.UnitConversionUtils;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.util.CoreMap;


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
     * An array of strings that should be ignored when looking for matches between the ingredientlist and
     * the step description
     */
    private static final String[] STRINGS_TO_IGNORE = {".", ",", "(", ")", "!", "icing"};
    /**
     * An array of tags that should be ignored when looking for matches between the ingredientlist and
     * the step description. For the meaning of these tags checkout
     * <a href="https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html">The PennTreeBankProject</a>
     */
    private static final String[] TAGS_TO_IGNORE = {"TO", "IN", "JJ", "JJR", "JJS", "VBG", "PDT", "CC", "DT", "VBN",
            "RB"};


    /**
     * A static map that matches the {@link #FRACTION_HALF} and {@link #FRACTION_QUARTER} strings to
     * their numerical values
     */
    private static Map<String, Double> sFractionMultipliers = new HashMap<>();

    /* populate the map */
    static {
        sFractionMultipliers.put(FRACTION_HALF, FRACTION_HALF_MUL);
        sFractionMultipliers.put(FRACTION_QUARTER, FRACTION_QUARTER_MUL);
    }

    /**
     * The step on which to do the detecting of ingredients
     */
    private RecipeStepInProgress mRecipeStep;

    /**
     * A set containing the names of the listingredients of the recipeInProgress
     */
    private HashSet<String> mNamesOfListIngredients;

    public DetectIngredientsInStepTask(RecipeInProgress recipeInProgress, int stepIndex) {
        super(recipeInProgress);
        if (stepIndex < 0) {
            throw new IllegalArgumentException("Negative stepIndex passed");
        }

        if (stepIndex >= recipeInProgress.getStepsInProgress().size()) {
            throw new IllegalArgumentException("stepIndex passed too large, stepIndex: "
                    + stepIndex + ", size of list: " + recipeInProgress.getStepsInProgress().size());
        }

        this.mRecipeStep = recipeInProgress.getStepsInProgress().get(stepIndex);
    }

    /**
     * Checks if a string should be ignored (if it is contained in the {@link #STRINGS_TO_IGNORE}
     * list
     *
     * @param string the string to check
     * @return false if the string should be ignored, true if the string should not be ignored
     */
    private static boolean doNotIgnoreString(String string) {
        for (String ignore : STRINGS_TO_IGNORE) {
            if (string.equalsIgnoreCase(ignore)) {
                // if the string is contained in de STRINGS_TO_IGNORE array then ignore this string
                // and return false
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
    private static boolean differInOneErasure(String string1, String string2) {
        // check for one erasure

        int string2Length = string2.length();
        int string1Length = string1.length();
        int lengthDif = string1Length - string2Length;

        // start with this initialization, if needed they will be swapped
        String longest = string2;
        String shortest = string1;
        // One erasure is only possible when the difference in lenght is 1
        if (Math.abs(lengthDif) == 1) {
            // get the shortest and longest
            // lengthDif is string1Length - string2Length
            if (lengthDif > 0) {
                longest = string1;
                shortest = string2;
            }
            return differenceIsOneErasedCharacter(shortest, longest);
        }

        return false;

    }

    /**
     * Checks if the difference is just in one erased character and not a completely different word
     *
     * @param shortest the shortest string (its length is smaller than the longest)
     * @param longest  the longest string
     * @return a boolean indicating if the difference is one erased character or not
     */
    private static boolean differenceIsOneErasedCharacter(String shortest, String longest) {
        int shortLength = shortest.length();

        // check if longest just contains an extra character at the back
        // to bypass the loop
        if (longest.substring(0, shortLength).equalsIgnoreCase(shortest)) {
            // make sure the last character wasn't a d since this is conjugated verb
            return longest.charAt(longest.length() - 1) != 'd';
        }

        return false;
/*
        // a boolean to indicate if one difference has been found
        boolean difFound = false;
        // the character of the shortest string
        char shortChar;
        // the character of the longest string
        char longChar;
        for (int i = 0; i < shortLength; i++) {
            shortChar = shortest.charAt(i);
            if (!difFound) {
                // if no difference found yet check the character at the same index
                longChar = longest.charAt(i);
                // if they are unequal a difference has been found
                difFound = longChar != shortChar;
                if(difFound && i==0){
                    return false;
                }
            }
            if (difFound) {
                // if one difference has been found check the character after this character
                longChar = longest.charAt(i + 1);
                if (longChar != shortChar) {
                    // second difference found
                    return false;
                }
            }
        }

        return true;*/
    }

    /**
     * Detects the mIngredients for each recipeStep
     */
    public void doTask() {
        List<ListIngredient> ingredientListRecipe = mRecipeInProgress.getIngredients();
        initializeNamesOfListIngredientsSet(ingredientListRecipe);
        List<Ingredient> ingredientSet = detectIngredients(mRecipeStep, ingredientListRecipe);
        for (Ingredient ing : ingredientSet) {
            // make sure all the positions are legal and not longer than the length of the description
            ing.trimPositionsToString(mRecipeStep.getDescription());
        }

        mRecipeStep.setIngredients(ingredientSet);
    }

    private void initializeNamesOfListIngredientsSet(List<ListIngredient> list) {
        mNamesOfListIngredients = new HashSet<>();
        for (ListIngredient ing : list) {
            String[] parts = ing.getName().replace(",", "").split(" ");
            mNamesOfListIngredients.addAll(Arrays.asList(parts));
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
    private List<Ingredient> detectIngredients(RecipeStepInProgress recipeStep,
                                               List<ListIngredient> ingredientListRecipe) {
        List<Ingredient> detectedIngredients = new ArrayList<>();


        // Maps list ingredients to a an array of words in their name for matching the name in the step
        // Necessary in case only a certain word of the list ingredient is used to describe it in the step
        Map<ListIngredient, List<String>> ingredientListMap = new HashMap<>();
        for (ListIngredient listIngr : ingredientListRecipe) {
            ingredientListMap.put(listIngr, new LinkedList<>(
                    Arrays.asList(listIngr.getName().toLowerCase(Locale.ENGLISH)
                            .replace(",", "").split(" "))));
        }
        Map<ListIngredient, List<String>> mergedCommonPartsMap = createCommonPartsMergedMap(ingredientListRecipe);

        for (ListIngredient listIngr : ingredientListRecipe) {
            // remove the doubles so every string is only searche once
            ingredientListMap.get(listIngr).removeAll(mergedCommonPartsMap.get(listIngr));
        }

        // Keeps track of already found ListIngredients in case the ingredient
        // is mentioned multiple times in the recipe step
        List<Ingredient> foundIngredients = new ArrayList<>();

        // keeps track of the tokens and string combinations already used to identify an ingredient
        List<CoreLabel> usedTokens = new ArrayList<>();
        List<String> usedCombinations = new ArrayList<>();

        List<CoreMap> stepSentences = recipeStep.getSentenceAnnotations();

        for (CoreMap sentence : stepSentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            // first search the map with the merged common parts so that if there are common parts it is more
            // likely that the correct ingredient is selected as found ingredient
            for (CoreLabel token : tokens) {
                if (!usedTokens.contains(token)) {

                    usedTokens.addAll(searchInMap(mergedCommonPartsMap, tokens, token, foundIngredients,
                            detectedIngredients));

                }
            }
            for(CoreLabel token: tokens){
                if (!usedTokens.contains(token)) {
                    searchInMap(ingredientListMap, tokens, token, foundIngredients, detectedIngredients);
                }
            }
        }

        return order(detectedIngredients);
    }

    private List<Ingredient> order(List<Ingredient> list){

        // order by beginindex of the name position
        Collections.sort(list, (Ingredient i1, Ingredient i2) ->
                Integer.compare(i1.getNamePosition().getBeginIndex(), i2.getNamePosition().getBeginIndex()));

        return list;

    }

    private List<CoreLabel> searchInMap(Map<ListIngredient, List<String>> map, List<CoreLabel> tokens,
                                        CoreLabel token,
                                        List<Ingredient> foundIngredients, List<Ingredient> detectedIngredients
                                        ) {

        // if found stop searching through the other ingredients
        boolean foundName = false;
        List<CoreLabel> usedLabels = new ArrayList<>();


        for (ListIngredient listIngredient : map.keySet()) {
            List<String> nameParts = map.get(listIngredient);

            int tokenIndex = tokens.indexOf(token);
            // Found name of an ingredient from the list of ingredients
            if (tokenIsContainedInNameParts(tokens.subList(tokenIndex,
                    tokens.size()), nameParts)
                    && !foundIngredients.contains(listIngredient)) {

                foundIngredients.add(listIngredient);

                detectedIngredients.add(getStepIngredient(tokenIndex, nameParts, listIngredient, tokens));
                foundName = true;


                // Check if the mentioned ingredient is being described by multiple words in the step
                // Skip these words for further analysis of the recipe step
                usedLabels.addAll(getAllUsedTokens(tokenIndex, tokens, nameParts));

            }

            if (foundName) {
                break;
            }
        }
        return usedLabels;

    }

    private Map<ListIngredient, List<String>> createCommonPartsMergedMap(List<ListIngredient> ingredients) {

        Map<ListIngredient, List<String>> commonPartsMerged = new HashMap<>();
        for (ListIngredient listIngr : ingredients) {
            commonPartsMerged.put(listIngr, new LinkedList<>(
                    Arrays.asList(listIngr.getName().toLowerCase(Locale.ENGLISH)
                            .replace(",", "").split(" "))));
        }


        // if a word is present in several lists, merge it with the previous or next word
        for (int i = 0; i < ingredients.size(); i++) {
            List<String> listI = commonPartsMerged.get(ingredients.get(i));
            for (int j = i + 1; j < ingredients.size(); j++) {
                List<String> listJ = commonPartsMerged.get(ingredients.get(j));
                mergeCommonElements(listI, listJ);
            }

        }

        return commonPartsMerged;
    }

    private void mergeCommonElements(List<String> list1, List<String> list2) {


        List<String> commonList = (new ArrayList<>(list1));
        commonList.retainAll(list2);
        boolean stop = false;
        // while there are still common elements but they still
        while (!commonList.isEmpty() && !stop) {
            for (String commonString : commonList) {
                // list 1
                mergeElement(commonString, list1);
                mergeElement(commonString, list2);
            }
            commonList = (new ArrayList<>(list1));
            commonList.retainAll(list2);
            if (list1.size() == 1 && list2.size() == 1) {
                stop = true;
            }
        }

    }

    private void mergeElement(String commonString, List<String> list) {
        int index = list.indexOf(commonString);
        if (index > 0) {
            // merge with previous
            list.set(index - 1, list.get(index - 1) + " " + commonString);

            list.remove(index);

        } else {
            if (index < list.size() - 1 && index > -1) {
                // merge with next
                list.set(index + 1, commonString + " " + list.get(index + 1));
                list.remove(index);
            }
        }
    }

    /**
     * An array of tokens not tagged as noun but that are nouns in a cooking context most of the time
     */
    private static final String[] COOKING_NOUNS = {"shortening", "orange", "mint"};

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
            // if the string of the token should be ignored, the whole token should be ignored
            // so return false
            return false;
        }

        for(String continuousVerb: COOKING_NOUNS){
            if(continuousVerb.equalsIgnoreCase(tokenText)){
                return true;
            }
        }
        for (String tagToIgnore : TAGS_TO_IGNORE) {
            if (token.tag().equals(tagToIgnore)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the sublist of tokens is contained in the nameParts and if this is relevant (not part of the
     * {@link #TAGS_TO_IGNORE} or {@link #STRINGS_TO_IGNORE} arrays). It will also be contained
     * if the token differs with a namePart in one erasure see {@link #differInOneErasure(String, String)}
     * this is needed to detect that "onion" refers to "onions" and to correct some spelling mistakes
     *
     * @param tokens    the sublist of tokens to check
     * @param nameParts the list of nameStrings to check
     * @return true if the token is contained
     */
    private boolean tokenIsContainedInNameParts(List<CoreLabel> tokens, List<String> nameParts) {
        for (String namePart : nameParts) {
            int partLength = namePart.split(" ").length;

            if (tokens.size() >= partLength) {
                boolean namePartOkay = false;

                for (int i = 0; i < partLength; i++) {
                    CoreLabel token = tokens.get(i);
                    String part = namePart.split(" ")[i];

                    if (partLength > 1 || (doNotIgnoreString(part) && doNotIgnoreToken(token))) {
                        String tokenText = token.originalText().toLowerCase(Locale.ENGLISH);

                        if (!(part.equalsIgnoreCase(tokenText) || differInOneErasure(tokenText, part))) {
                            namePartOkay = false;

                            break;
                        } else {
                            namePartOkay = true;
                        }
                    }

                }
                if (namePartOkay) {
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
     * @param nameParts Separated words of the list ingredient's name
     * @return a list of tokens that is used to define this ingredient
     */
    private List<CoreLabel> getAllUsedTokens(int tokenIndex, List<CoreLabel> tokens, List<String> nameParts) {
        List<CoreLabel> usedLabels = new ArrayList<>();

        for (String namePart : nameParts) {
            for (String part : namePart.split(" ")) {
                if (tokens.get(tokenIndex).originalText().equals(part)) {
                    usedLabels.add(tokens.get(tokenIndex));
                    tokenIndex++;
                }
                if (tokenIndex == tokens.size()) {
                    // reached the end
                    return usedLabels;
                }

            }
        }

        List<String> remainingTokensText = new ArrayList<>();

        for (CoreLabel token : tokens.subList(tokenIndex, tokens.size())) {
            remainingTokensText.add(token.originalText());
        }

        int foundIndex = remainingTokensText.size();

        // check if a namepart with a space can be found another time in the list of tokens
        for (String namePart : nameParts) {
            String[] split = namePart.split(" ");
            if (split.length > 1) {
                boolean firstPart = true;
                List<CoreLabel> refoundTokens = new ArrayList<>();
                for (String part : split) {
                    if (firstPart) {
                        foundIndex = remainingTokensText.indexOf(part);
                        if (foundIndex < 0 || foundIndex > remainingTokensText.size() - split.length) {
                            // not found again or no space left to find the whole part
                            break;
                        }
                    } else {
                        if(!part.equals(remainingTokensText.get(++foundIndex))){
                            // not found again
                            refoundTokens.clear();
                            break;
                        }
                    }
                    refoundTokens.add(tokens.get(tokenIndex + foundIndex));
                    firstPart = false;
                }
                usedLabels.addAll(refoundTokens);
            }
        }

        return usedLabels;
    }

    /**
     * Checks if there are multiple words used to represent the ingredient
     * in the recipeStep and returns the amount of used words
     *
     * @param tokens    Tokens in in the recipe step
     * @param nameParts Separated words of the list ingredient's name
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

        int beginPosOffset = mRecipeStep.getBeginPosition();
        Ingredient stepIngredient = defaultStepIngredient();
        stepIngredient.setName(listIngredient.getName());

        // Find the other parts of the mentioned name
        int lastNameIndex = nameIndex + succeedingNameLength(nameIndex, tokens, nameParts);
        Position namePos = new Position(tokens.get(nameIndex).beginPosition() - beginPosOffset,
                tokens.get(lastNameIndex).endPosition() - beginPosOffset);
        stepIngredient.setNamePosition(namePos);

        // Check if a quantity or unit is possible for this ingredient
        if (nameIndex > 0 && isIsolatedName(tokens.subList(0, nameIndex - 1))) {
            return stepIngredient;
        }

        // set default for quantity and unit
        stepIngredient.setQuantity(DEFAULT_QUANTITY);
        stepIngredient.setUnit(DEFAULT_UNIT);

        // Check if a quantity or unit can be found for this ingredient in the step
        int unitLength = listIngredient.getUnit().split(" ").length;
        int precedingLength = unitLength + PREPOSITION_LENGTH + FRACTIONS_LENGTH + MAX_QUANTITY_LENGTH;


        List<CoreLabel> precedingTokens = new ArrayList<>(tokens.subList(Math.max(0, nameIndex - (precedingLength)),
                nameIndex));

        if (!precedingTokens.isEmpty()) {
            String foundUnit = findUnit(precedingTokens, listIngredient);

            if (!foundUnit.isEmpty()) {

                // create the position

                // get the base unit
                String baseUnit = UnitConversionUtils.getBase(foundUnit);
                int beginIndex = mRecipeStep.getDescription().indexOf(foundUnit);
                if (beginIndex < 0) {
                    beginIndex = mRecipeStep.getDescription().indexOf(baseUnit);
                }

                Position unitPos = new Position(beginIndex, beginIndex + foundUnit.length());
                stepIngredient.setUnitPosition(unitPos);


                //update the description with the baseUnit
                mRecipeStep.setDescription(mRecipeStep.getDescription().replace(foundUnit, baseUnit));

                // update the unit position & unit
                int oldEndIndex = unitPos.getEndIndex();
                int newEndIndex = unitPos.getBeginIndex() + baseUnit.length();
                unitPos.setEndIndex(newEndIndex);
                stepIngredient.setUnit(baseUnit);

                // check if an update of the name position is necessary
                if (namePos.getEndIndex() >= oldEndIndex) {
                    int offset = newEndIndex - oldEndIndex;
                    namePos.setIndices(namePos.getBeginIndex() + offset, namePos.getEndIndex() + offset);
                }
            }


            double listQuantity = listIngredient.getQuantity();
            Pair<Position, Double> quantityPair = findQuantityPositionAndValue(precedingTokens, listQuantity);
            if (quantityPair != null && quantityPair.second != null) {
                quantityPair.first.subtractOffset(beginPosOffset);
                stepIngredient.setQuantityPosition(quantityPair.first);
                stepIngredient.setQuantity(quantityPair.second);
            }
        }


        return stepIngredient;
    }

    /**
     * Creates a default ingredient with values initialized to their absent value
     *
     * @return Default ingredient
     */
    private Ingredient defaultStepIngredient() {
        // construct the map
        Map<Ingredient.PositionKeysForIngredients, Position> map
                = new EnumMap<>(Ingredient.PositionKeysForIngredients.class);

        // Initialize name on empty string
        String name = "";

        // Initialize position on Position(0, length)
        int stepSentenceLength = mRecipeStep.getDescription().length();
        Position defaultPos = new Position(0, stepSentenceLength);
        map.put(Ingredient.PositionKeysForIngredients.NAME, defaultPos);
        map.put(Ingredient.PositionKeysForIngredients.UNIT, defaultPos);
        map.put(Ingredient.PositionKeysForIngredients.QUANTITY, defaultPos);

        return new Ingredient(name, DEFAULT_UNIT, DEFAULT_QUANTITY, map);
    }

    /**
     * @param precedingTokens Tokens in front of detected name of an ingredient
     * @param listQuantity    The quantity of the list ingredient tied to this detected ingredient name
     * @return A pair with both the start and end position of the quantity of this detected ingredient
     * and the quantity detected in the ingredient step
     */
    private Pair<Position, Double> findQuantityPositionAndValue(List<CoreLabel> precedingTokens, Double
            listQuantity) {
        double stepQuantity = 1.0;
        boolean foundQuantities = false;

        int beginPos = precedingTokens.get(precedingTokens.size() - 1).endPosition();
        int endPos = 0;

        List<String> ingredientSeparators = Arrays.asList(",", ".");


        // Stop when another ingredient is found to prevent quantity overlap
        // CC means Coordinating conjunction, such as "and"
        int i = precedingTokens.size() - 1;
        while (i >= 0 && !ingredientSeparators.contains(precedingTokens.get(i).originalText())
                && !"CC".equals(precedingTokens.get(i).tag())) {
            // Detect verbose fractions
            Pair<Boolean, Double> quantityVerbose = detectVerboseFractions(i, precedingTokens, listQuantity);
            // Detect cardinal numbers: fractions, numbers and verbose numbers
            Pair<Boolean, Double> quantityCardinal = detectCardinalFractions(i, precedingTokens);
            stepQuantity *= quantityVerbose.second * quantityCardinal.second;

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
     * A function that indicates if a the search for a unit should stop. Currently this is when a comma
     * is encountered
     *
     * @param token the current token, where the search is at
     * @return a boolean that indicates if the search should be stopped
     */
    private boolean stopSearchingForUnit(CoreLabel token, String nameOf) {
        if (token.originalText().contains(",")) {
            return true;
        }

        return mNamesOfListIngredients.contains(token.originalText().replace(",", "")) && !nameOf.contains(token.originalText());
    }

    /**
     * Finds the position of unit tokens in the recipe step
     *
     * @param precedingTokens Tokens in front of detected name of an ingredient
     * @param ingredient           the list ingredient tied to this detected ingredient name
     * @return The start and end position of the unit of this detected ingredient
     */
    private String findUnit(List<CoreLabel> precedingTokens, Ingredient ingredient) {

        String unitOfIngredientInList = ingredient.getUnit();
        List<CoreLabel> unitTokens = new ArrayList<>();
        if (precedingTokens.get(precedingTokens.size() - 1).originalText().equals(OF_PREPOSITION)) {
            precedingTokens.remove(precedingTokens.size() - 1);
        }

        // Add singulars to the possible unit names
        Morphology singularMorph = new Morphology();
        String[] unitParts = unitOfIngredientInList.split(" ");
        List<String> unitPartsWithSingulars = new ArrayList<>();
        for (String unitPart : unitParts) {
            unitPartsWithSingulars.add(unitPart);
            unitPartsWithSingulars.add(singularMorph.stem(unitPart));
        }

        // Add common units to the possible unit names
        unitPartsWithSingulars.addAll(UnitConversionUtils.getCommonUnits());

        // TODO add additional condition that it should stop when no more unit strings are found
        int i = precedingTokens.size() - 1;
        while (i > 0) {

            if (stopSearchingForUnit(precedingTokens.get(i), ingredient.getName())) {
                i = 0;

            } else if (doNotIgnoreToken(precedingTokens.get(i)) &&
                    unitPartsWithSingulars.contains(precedingTokens.get(i).originalText())) {

                unitTokens.add(precedingTokens.get(i));
                i--;

            } else if (!unitTokens.isEmpty()) {
                // previous unit words were found and this is not a unit anymore, this means
                // it is time to stop
                i = 0;

            } else {
                i--;
            }
        }

        if (!unitTokens.isEmpty()) {
            StringBuilder bld = new StringBuilder();
            for (CoreLabel unitLabel : unitTokens) {
                bld.append(unitLabel.word());
                bld.append(" ");
            }
            // remove the last space
            bld.deleteCharAt(bld.length() - 1);
            return bld.toString();
        }

        return "";
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