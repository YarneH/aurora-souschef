package com.aurora.souschefprocessor.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class for translating a recipe
 */
final class TranslateHelper {


    /**
     * The diminutive for dutch words that google sometimes adds
     */
    private static final String DIMINUTIVE = "je";

    /**
     * A regex for translating to Dutch, if the translation of the timer is not found try to find if the part of the
     * translation of the timer that matches this regex is found. Used in
     * {@link #findPositionImpreciseTimerString(String, String, int)}
     */
    private static final Pattern DEFAULT_TIME_REGEX_DUTCH = Pattern.compile("[0-9]+[ ](minuten|minuut|uren|uur" +
            "|seconde|seconden)");

    private TranslateHelper() {
        //static fields
    }

    /**
     * Gets all the sentences that should be translated to translate this recipe
     *
     * @param recipe the recipe to translate
     * @return A list of all the sentences of the recipe that should be translated
     */
    static List<String> createSentencesToTranslate(Recipe recipe) {
        // Create the sentences to translate
        // split the description
        List<String> sentences = new ArrayList<>(Arrays.asList(recipe.getDescription().split("\n")));

        // Add the ingredients to the sentences to translate
        sentences.addAll(createIngredientsSentencesToTranslate(recipe.getIngredients()));

        // Add the steps
        sentences.addAll(createStepSentencesToTranslate(recipe.getRecipeSteps()));

        // replace ° by " degrees" otherwise it might not get translated
        for (String s : sentences) {
            sentences.set(sentences.indexOf(s), s.replace("°", " degrees"));
        }
        return sentences;
    }

    /**
     * Private helper method to create the sentences to translate of a list of ListIngredients
     *
     * @param originalIngredients the list of ingredients to translate
     * @return the list of sentences to translate
     */
    private static List<String> createIngredientsSentencesToTranslate(List<ListIngredient> originalIngredients) {
        List<String> ingredientSentences = new ArrayList<>();
        for (ListIngredient ingredient : originalIngredients) {
            String originalLine = ingredient.getOriginalLine();
            ingredientSentences.add(originalLine);
            ingredientSentences.add(ingredient.getName());
            if (ingredient.unitDetected(originalLine)) {
                ingredientSentences.add(ingredient.getUnit());
            }

            if (ingredient.quantityDetected(originalLine)) {
                Position quantityPosition = ingredient.getQuantityPosition();
                ingredientSentences.add(ingredient.getOriginalLine().substring(quantityPosition.getBeginIndex(),
                        quantityPosition.getEndIndex()));
            }
        }
        return ingredientSentences;
    }

    /**
     * Private helper method to create the sentences to translate of a list of RecipeStep
     *
     * @param originalSteps the list of steps to translate
     * @return the list of sentences to translate
     */
    private static List<String> createStepSentencesToTranslate(List<RecipeStep> originalSteps) {
        List<String> stepSentences = new ArrayList<>();
        for (RecipeStep step : originalSteps) {
            stepSentences.add(step.getDescription());

            // add the ingredients
            for (Ingredient ingredient : step.getIngredients()) {
                // name is always present
                stepSentences.add(ingredient.getName());
                if (ingredient.unitDetected(step.getDescription())) {
                    stepSentences.add(ingredient.getUnit());
                }
                if (ingredient.quantityDetected(step.getDescription())) {
                    Position quantityPosition = ingredient.getQuantityPosition();
                    stepSentences.add(step.getDescription().substring(quantityPosition.getBeginIndex(),
                            quantityPosition.getEndIndex()));
                }
                // add the name substring =/= name because name is the name of the corresponding listingredient
                Position namePosition = ingredient.getNamePosition();
                stepSentences.add(step.getDescription().substring(namePosition.getBeginIndex(),
                        namePosition.getEndIndex()));
            }

            // add the timers
            for (RecipeTimer timer : step.getRecipeTimers()) {
                Position timerPosition = timer.getPosition();
                stepSentences.add(step.getDescription().substring(timerPosition.getBeginIndex(),
                        timerPosition.getEndIndex()));
            }
        }
        return stepSentences;
    }

    /**
     * Creates a new recipe object that is the translated form of this recipe
     *
     * @param translatedSentences the translated sentences, this is the response from aurora to the
     *                            result of {@link #createSentencesToTranslate(Recipe)}
     * @param originalRecipe The original untranslated recipe
     * @return The new translated recipe
     */
    static Recipe getTranslatedRecipe(Recipe originalRecipe, String[] translatedSentences) {

        Queue<String> translations = new LinkedList<>(Arrays.asList(translatedSentences));
        Recipe recipe = new Recipe(originalRecipe.getFileName());

        // set the number of people (this does not change by translating)
        recipe.setNumberOfPeople(originalRecipe.getNumberOfPeople());

        // set the description and remove it from the queue
        StringBuilder descriptionBuilder = new StringBuilder();
        for (int i = 0; i < originalRecipe.getDescription().split("\n").length; i++) {
            descriptionBuilder.append(translations.poll()).append("\n");
        }
        // set the description and delete tha laste appended "\n"
        recipe.setDescription(descriptionBuilder.substring(0, descriptionBuilder.length() - 1));

        // the ingredients
        recipe.setIngredients(getTranslatedIngredients(translations, originalRecipe));

        // the steps
        recipe.setRecipeSteps(getTranslatedSteps(translations, originalRecipe));

        return recipe;
    }

    /**
     * Fills in the newIngredients list with the result of the translation
     *
     * @param translatedSentences the result of the translation of the entire recipe
     * @param originalRecipe      the untranslated recipe
     * @return a list with the translated list ingredients of this recipe
     */
    private static List<ListIngredient> getTranslatedIngredients(Queue<String> translatedSentences,
                                                                 Recipe originalRecipe) {
        List<ListIngredient> newIngredients = new ArrayList<>();
        for (ListIngredient ingredient : originalRecipe.getIngredients()) {
            String oldOriginalLine = ingredient.getOriginalLine();

            // get the original line
            String originalLine = translatedSentences.poll();
            // get the all the fields of the superclass
            Ingredient ing = getTranslatedIngredient(oldOriginalLine, ingredient, originalLine, translatedSentences);

            // add it to the list
            newIngredients.add(new ListIngredient(ing.getName(), ing.getUnit(), ing.getQuantity(), originalLine,
                    ing.getPositions()));

        }
        return newIngredients;

    }

    /**
     * Fills in the newSteps list with the result of the translation
     *
     * @param translatedSentences the result of the translation of the entire recipe, this will be
     *                            altered during the execution
     * @param originalRecipe      the untranslated version of the recipe
     * @return the list with the translated steps of this recipe
     */
    private static List<RecipeStep> getTranslatedSteps(Queue<String> translatedSentences, Recipe originalRecipe) {

        List<RecipeStep> newSteps = new ArrayList<>();
        for (RecipeStep oldStep : originalRecipe.getRecipeSteps()) {

            newSteps.add(getTranslatedStep(oldStep, translatedSentences));
        }
        return newSteps;

    }

    /**
     * Constructs the translated ingredient
     *
     * @param oldOriginalLine     the non-translated line of the old Ingredient
     * @param oldIngredient       the oldingredient
     * @param newOriginalLine     the translated original line
     * @param translatedSentences the queue with the translated sentences (this will be altered during execution)
     * @return a new Ingredient that is the translated version of the oldIngredient parameter
     */
    private static Ingredient getTranslatedIngredient(String oldOriginalLine, Ingredient oldIngredient,
                                                      String newOriginalLine,
                                                      Queue<String> translatedSentences) {


        String name;
        String unit = "";
        double quantity = 1;
        Map<Ingredient.PositionKeysForIngredients, Position> map =
                new EnumMap<>(Ingredient.PositionKeysForIngredients.class);
        // name

        name = translatedSentences.poll();
        // get the position
        int beginIndex = newOriginalLine.indexOf(name);
        if (beginIndex > -1) {
            int endIndex = beginIndex + name.length();
            map.put(Ingredient.PositionKeysForIngredients.NAME, new Position(beginIndex, endIndex));
        }


        // set name position to nothing detected if nothing detected or the nameposition is irrelevant
        map.putIfAbsent(Ingredient.PositionKeysForIngredients.NAME, new Position(0, newOriginalLine.length()));


        // unit
        if (oldIngredient.unitDetected(oldOriginalLine)) {
            unit = translatedSentences.poll();

            // get the position
            beginIndex = newOriginalLine.indexOf(unit);
            if (beginIndex > -1) {
                int endIndex = beginIndex + unit.length();
                if (DIMINUTIVE.equals(newOriginalLine.substring(endIndex, endIndex + DIMINUTIVE.length()))) {
                    // this is in true in the case of cup -> kop but in sentence it becomes kopje
                    endIndex += DIMINUTIVE.length();
                    unit += DIMINUTIVE;
                }
                map.put(Ingredient.PositionKeysForIngredients.UNIT, new Position(beginIndex, endIndex));
            }

        }
        // set unit position to nothing detected if nothing detected
        map.putIfAbsent(Ingredient.PositionKeysForIngredients.UNIT, new Position(0, newOriginalLine.length()));


        //quantity
        if (oldIngredient.quantityDetected(oldOriginalLine)) {
            String quantityString = translatedSentences.poll();
            // set the quantity to the original quantity (double does not change by translating)
            quantity = oldIngredient.getQuantity();
            // get the position
            beginIndex = newOriginalLine.indexOf(quantityString);
            if (beginIndex > -1) {
                int endIndex = beginIndex + quantityString.length();
                map.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(beginIndex, endIndex));
            }
        }
        // set quantity position to nothing detected if nothing detected
        map.putIfAbsent(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(0, newOriginalLine.length()));

        return new Ingredient(name, unit, quantity, map);
    }

    /**
     * A private helper function that translates the oldStep using the Queue of translated sentences this queue is a
     * result of the translation operation on the {@link #createSentencesToTranslate(Recipe)} where the elements that
     * come before this step have already been popped from the queue
     *
     * @param oldStep             the original step in English
     * @param translatedSentences the queue of translated sentences, this will be altered during this method
     * @return the new translated recipe step
     */
    private static RecipeStep getTranslatedStep(RecipeStep oldStep, Queue<String> translatedSentences) {
        // start with the description
        String description = translatedSentences.poll();
        RecipeStep newStep = new RecipeStep(description);

        // the ingredients
        if (oldStep.isIngredientDetectionDone()) {
            List<Ingredient> newIngredients = new ArrayList<>();

            for (Ingredient oldIngredient : oldStep.getIngredients()) {

                Ingredient newIngredient = (getTranslatedIngredient(oldStep.getDescription(),
                        oldIngredient, description, translatedSentences));
                // get the name position
                String translatedNameSubstring = translatedSentences.poll();

                int beginIndex = newStep.getDescription().indexOf(translatedNameSubstring);
                // if this substring is not found try deleting an end character that google might have added
                if (beginIndex < 0) {
                    translatedNameSubstring = translatedNameSubstring.
                            substring(0, translatedNameSubstring.length() - 1);
                    beginIndex = newStep.getDescription().indexOf(translatedNameSubstring);
                }
                // if it is still not found set to not detected
                if (beginIndex < 0) {
                    translatedNameSubstring = description;
                    beginIndex = 0;
                }
                Position namePos = new Position(beginIndex, beginIndex + translatedNameSubstring.length());
                newIngredient.setNamePosition(namePos);
                newIngredients.add(newIngredient);
            }
            newStep.setIngredientDetectionDone(true);
            newStep.setIngredients(newIngredients);
        }

        // the timers
        findTranslatedTimers(oldStep, newStep, translatedSentences);
        return newStep;

    }

    /**
     * helper function for finding the  translated timers in a step
     *
     * @param oldStep             the untranslated step
     * @param newStep             the new translated step
     * @param translatedSentences the queue of all the translated sentences
     */
    private static void findTranslatedTimers(RecipeStep oldStep, RecipeStep newStep,
                                             Queue<String> translatedSentences) {
        String description = newStep.getDescription();
        if (oldStep.isTimerDetectionDone()) {
            int startIndex = 0;
            List<RecipeTimer> newTimers = new ArrayList<>();
            for (RecipeTimer oldTimer : oldStep.getRecipeTimers()) {
                String newTimerString = translatedSentences.poll();
                int beginIndex = description.indexOf(newTimerString, startIndex);
                Position pos;
                if (beginIndex < 0) {
                    // string not found try to find actual time string like "20 minuten" string
                    pos = findPositionImpreciseTimerString(newTimerString, description, startIndex);
                } else {
                    int endIndex = beginIndex + newTimerString.length();

                    pos = new Position(beginIndex, endIndex);

                }
                // for next timer only start searching starting from this end if the end is not the end of the string
                if (pos.getEndIndex() < description.length()) {
                    startIndex = pos.getEndIndex();
                }
                newTimers.add(new RecipeTimer(oldTimer.getLowerBound(), oldTimer.getUpperBound(),
                        pos));
            }
            newStep.setTimerDetectionDone(true);
            newStep.setRecipeTimers(newTimers);
        }
    }

    /**
     * A helper function for when the translation of the timer is not contained in the translation of the description
     * of the string. This checks if the new timer string matches the {@link #DEFAULT_TIME_REGEX_DUTCH}
     *
     * @param newTimerString the translated string for the timer
     * @param description    the translated description of the step
     * @param startindex     the index from where to start searching for this timer
     * @return the position of the translated timer in the translated description, if it is still not found this is
     * the position corresponding to the last character
     */
    private static Position findPositionImpreciseTimerString(String newTimerString, String description,
                                                             int startindex) {
        Matcher matcher = DEFAULT_TIME_REGEX_DUTCH.matcher(newTimerString);
        if (matcher.find()) {
            String matched = matcher.group();
            int beginIndex = description.indexOf(matched, startindex);
            int endIndex = beginIndex + matched.length();
            if (beginIndex >= 0) {
                return new Position(beginIndex, endIndex);
            }
        }
        // nothing found, set timer at endIndex
        return new Position(description.length() - 1, description.length());

    }

}
