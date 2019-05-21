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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
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
     * A constant for the length of a preposition in front of an ingredient in the step,
     * it is used for setting a search bound for finding the properties of the ingredient
     */
    private static final int PREPOSITION_LENGTH = 1;

    /**
     * A constant for the length of a fraction in front of an ingredient in the step,
     * it is used for setting a search bound for finding the properties of the ingredient
     */
    private static final int FRACTIONS_LENGTH = 1;

    /**
     * A constant for the maximum length of a quantity in front of an ingredient in the step,
     * it is used for setting a search bound for finding the properties of the ingredient
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
     * An array of tokens not tagged as noun but that are nouns in a cooking context most of the time
     */
    private static final String[] INGREDIENT_IDENTIFIERS = {"shortening", "orange", "mint"};

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
     * plurals (e.g "onion" and "onions" )
     *
     * @param string1 the first string
     * @param string2 the second string
     * @return a boolean indicating whether these strings differ in one erasure
     */
    private static boolean differInLastCharacter(String string1, String string2) {
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
            return extraCharachterAtTheBack(shortest, longest);
        }

        return false;

    }

    /**
     * Checks if the difference is an extra character at the back which is not a d (since this is conjugated verb and
     * this means that it is not a description of the ingredient of the shortest string)
     *
     * @param shortest the shortest string (its length is smaller than the longest)
     * @param longest  the longest string
     * @return a boolean indicating if the difference is only the character at the back
     */
    private static boolean extraCharachterAtTheBack(String shortest, String longest) {
        int shortLength = shortest.length();

        if (longest.substring(0, shortLength).equalsIgnoreCase(shortest)) {
            // make sure the last character wasn't a d since this is conjugated verb and this means that it is not a
            // description of the ingredient with the noun
            return longest.charAt(longest.length() - 1) != 'd';
        }

        return false;

    }

    /**
     * Detects the mIngredients for each recipeStep
     */
    public void doTask() {
        List<ListIngredient> ingredientListRecipe = mRecipeInProgress.getIngredients();
        initializeNamesOfListIngredientsSet(ingredientListRecipe);
        List<Ingredient> ingredientSet = detectIngredients(mRecipeStep);
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
     * @param recipeStep The recipeStep on which to detect the mIngredients
     * @return A set of Ingredient objects that represent the mIngredients contained in the recipeStep
     */
    private List<Ingredient> detectIngredients(RecipeStepInProgress recipeStep) {
        List<Ingredient> detectedIngredients = new ArrayList<>();


        // Maps list ingredients to a an array of words in their name for matching the name in the step
        // Necessary in case only a certain word of the list ingredient is used to describe it in the step
        Map<ListIngredient, List<String>> ingredientListMap = mRecipeInProgress.getNamePartsMap();
        Map<ListIngredient, List<String>> mergedCommonPartsMap =
                mRecipeInProgress.getNamePartsCommonElementsMergedMap();


        // Keeps track of already found ListIngredients in case the ingredient
        // is mentioned multiple times in the recipe step
        List<ListIngredient> foundIngredients = new ArrayList<>();

        // keeps track of the tokens and string combinations already used to identify an ingredient
        List<CoreLabel> usedTokens = new ArrayList<>();

        List<CoreMap> stepSentences = recipeStep.getSentenceAnnotations();


        // first search the map with the merged common parts so that if there are common parts it is more
        // likely that the correct ingredient is selected as found ingredient
        searchInMap(stepSentences, usedTokens, foundIngredients, detectedIngredients, mergedCommonPartsMap);
        searchInMap(stepSentences, usedTokens, foundIngredients, detectedIngredients, ingredientListMap);

        // order the ingredients and return
        return order(detectedIngredients);
    }

    /**
     * private helper function it searches for matches between tokens and name parts (parts of the names of the
     * listIngredients in the recipe). If a match is found an ingredient is constructed by also searching the unit
     * and quantity {@link #constructIngredient(int, List, Ingredient, List)}. It keeps track of all the usedTokens
     * and usedListIngredients as to not reuse them
     *
     * @param stepSentences       The list of sentences to find matches in
     * @param usedTokens          the list of used Tokens before execution, this list is updated if more tokens are used
     * @param usedListIngredients the list of used ListIngredients before execution, this list is updated if more
     *                            tokens
     *                            are used
     * @param detectedIngredients the list of detected Ingredients, this list will be updated if new ingredients are
     *                            constructed
     * @param map                 A map that maps ListIngredients to their NameParts
     */
    private void searchInMap(List<CoreMap> stepSentences, List<CoreLabel> usedTokens,
                             List<ListIngredient> usedListIngredients,
                             List<Ingredient> detectedIngredients, Map<ListIngredient, List<String>> map) {

        for (CoreMap sentence : stepSentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);

            for (CoreLabel token : tokens) {

                // iterate over the listIngredients
                for (ListIngredient listIngredient : map.keySet()) {

                    // get all the nameparts
                    List<String> nameParts = map.get(listIngredient);

                    // the index of the token used needed vor constructIngredient
                    int tokenIndex = tokens.indexOf(token);

                    // the sublist starting with the current token
                    List<CoreLabel> subListTokens = tokens.subList(tokenIndex, tokens.size());

                    if (!usedListIngredients.contains(listIngredient) &&
                            tokensContainedInNameParts(subListTokens, nameParts, usedTokens)) {

                        // add the listIngredient to the list of used ingredients so that if it is mentioned further
                        // in the description it is not used again
                        usedListIngredients.add(listIngredient);
                        // construct and add the detected ingredient
                        detectedIngredients.add(constructIngredient(tokenIndex, nameParts, listIngredient, tokens));
                        // stop iterating for this token
                        break;

                    }
                }
            }
        }
    }

    /**
     * Checks if the sublist of tokens is contained in the nameParts and if this is relevant (not part of the
     * {@link #TAGS_TO_IGNORE} or {@link #STRINGS_TO_IGNORE} arrays). It will also be contained
     * if the token differs with a namePart in one erasure see {@link #differInLastCharacter(String, String)}
     * this is needed to detect that "onion" refers to "onions" and to correct some spelling mistakes
     *
     * @param tokens    the sublist of tokens to check, starts with the first token that sould be contained in the
     *                  nameParts
     * @param nameParts the list of nameStrings to check
     * @return true if the token is contained
     */
    private boolean tokensContainedInNameParts(List<CoreLabel> tokens, List<String> nameParts,
                                               List<CoreLabel> usedLabels) {
        // go over all the strings
        for (String namePart : nameParts) {
            // check how many words are in the string
            String[] words = namePart.split(" ");
            int numberOfWords = words.length;


            // if the sublist is long enough to contain all the words of this namepart and the
            if (tokens.size() >= numberOfWords) {
                // get a sublist with the length of the words
                List<CoreLabel> subListTokens = tokens.subList(0, numberOfWords);
                // check if none of these tokens is already used
                if (usedLabels.isEmpty() || Collections.disjoint(subListTokens, usedLabels)) {
                    // do the words match with the tokens?
                    List<CoreLabel> tokensUsedForNamePart = tokensContainedInWords(words, subListTokens);
                    // if it is not empty then we have found a match, add all the used tokens to the used labels and
                    // return true
                    if (!tokensUsedForNamePart.isEmpty()) {
                        usedLabels.addAll(tokensUsedForNamePart);
                        return true;
                    }
                }

            }
        }
        return false;

    }

    /**
     * private helper function for {@link #tokensContainedInNameParts(List, List, List)}
     *
     * @param words  the words of a specific namePart to be checked
     * @param tokens the tokens to be checked (starting with the first token to be checked) it has the same size as
     *               the length of the words array
     * @return true the tokens that have been used to confirm the words are contained, if the tokens are not found in
     * these words than this will be an empty list
     */
    private List<CoreLabel> tokensContainedInWords(String[] words, List<CoreLabel> tokens) {
        List<CoreLabel> usedTokens = new ArrayList<>();
        int numberOfWords = words.length;
        // go over all the words in this namePart
        for (int i = 0; i < numberOfWords; i++) {
            // get the corresponding token and word
            CoreLabel token = tokens.get(i);
            String word = words[i];

            // check if we do not want to ignore this word or token, if the number of words is larger than 1
            // it means that we do not want to ignore since this is a result of a needed merging operation to
            // differentiate between the different ingredients
            if (numberOfWords > 1 || (doNotIgnoreString(word) && doNotIgnoreToken(token))) {
                String tokenText = token.originalText().toLowerCase(Locale.ENGLISH);

                // check if the tokentext is equal to the word or the difference is small enough to consider
                // it a match, if not return the empty list since this is not a match
                if (!(word.equalsIgnoreCase(tokenText) || differInLastCharacter(tokenText, word))) {
                    return Collections.emptyList();
                }
                // if we get here add the token to the usedTokens list
                usedTokens.add(token);
            }

        }
        // if we get through the list then all of the not ignored tokens matched with the words so return a list with
        // these tokens
        return usedTokens;
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

        boolean doNotIgnore = false;
        // if the string of the token should be ignored, the whole token should be ignored
        if (doNotIgnoreString(tokenText)) {
            // check all the known cooking nouns first, if it is one of these then do not ignore
            for (String cookingNoun : INGREDIENT_IDENTIFIERS) {
                if (cookingNoun.equalsIgnoreCase(tokenText)) {
                    return true;
                }
            }

            // check if this token is tagged with a tag to ignore in terms of name searching for ingredients
            for (String tagToIgnore : TAGS_TO_IGNORE) {
                if (token.tag().equals(tagToIgnore)) {
                    return false;
                }
            }
            // we got here so do not ignore
            doNotIgnore = true;
        }

        return doNotIgnore;
    }


    /**
     * Sorts the list on the beginIndex of the namePosition, since a name is always found this ordering feels
     * natural in a step
     *
     * @param list the list to sort
     * @return the sorted list
     */
    private List<Ingredient> order(List<Ingredient> list) {

        // order by beginindex of the name position
        Collections.sort(list, (Ingredient i1, Ingredient i2) ->
                Integer.compare(i1.getNamePosition().getBeginIndex(), i2.getNamePosition().getBeginIndex()));

        return list;

    }


    /**
     * Finds the attributes (name, unit and quantity) of the step ingredient in the recipe step sentence
     * If some attributes can'searchInMap be found they are set to their default absent value
     *
     * @param nameIndex      Index of the found ingredient name in the list of tokens
     * @param listIngredient ListIngredient corresponding to this found ingredient name
     * @param tokens         List of tokens representing this sentence
     * @return Step Ingredient
     */
    private Ingredient constructIngredient(int nameIndex, List<String> nameParts,
                                           Ingredient listIngredient, List<CoreLabel> tokens) {

        int beginPosOffset = mRecipeStep.getBeginPositionOffset();
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

    /**
     * Finds the position of unit tokens in the recipe step
     *
     * @param precedingTokens Tokens in front of detected name of an ingredient
     * @param ingredient      the list ingredient tied to this detected ingredient name
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

        return mNamesOfListIngredients.contains(token.originalText().replace(",", ""))
                && !nameOf.contains(token.originalText());
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
            quantityMultiplier *= calculateQuantity(Collections.singletonList(precedingTokens.get(tokenIndex)));
            tokenIsQuantity = true;
        }

        return new Pair<>(tokenIsQuantity, quantityMultiplier);
    }
}
