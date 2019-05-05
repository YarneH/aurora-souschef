package com.aurora.souschefprocessor.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * A helper class for translating a recipe
 */
 final class TranslateHelper {


    /**
     * The diminutive for dutch words that google sometimes adds
     */
    private final static String DIMINUTIVE = "je";

    private TranslateHelper() {
        //static fields
    }

    static List<String> sentencesToTranslate(Recipe recipe) {
        // Create the sentences to translate
        // split the description
        List<String> sentences = new ArrayList<>(Arrays.asList(recipe.getDescription().split("\n")));

        // Add the ingredients to the sentences to translate
        for (ListIngredient ingredient : recipe.getIngredients()) {
            String originalLine = ingredient.getOriginalLine();
            sentences.add(originalLine);
            sentences.add(ingredient.getName());
            if (ingredient.unitDetected(originalLine)) {
                sentences.add(ingredient.getUnit());
            }

            if (ingredient.quantityDetected(originalLine)) {
                Position quantityPosition = ingredient.getQuantityPosition();
                sentences.add(ingredient.getOriginalLine().substring(quantityPosition.getBeginIndex(),
                        quantityPosition.getEndIndex()));
            }
        }

        // Add the steps
        for (RecipeStep step : recipe.getRecipeSteps()) {
            sentences.add(step.getDescription());

            // add the ingredients
            for (Ingredient ingredient : step.getIngredients()) {
                // name is always present
                sentences.add(ingredient.getName());
                if (ingredient.unitDetected(step.getDescription())) {
                    sentences.add(ingredient.getUnit());
                }
                if (ingredient.quantityDetected(step.getDescription())) {
                    Position quantityPosition = ingredient.getQuantityPosition();
                    sentences.add(step.getDescription().substring(quantityPosition.getBeginIndex(),
                            quantityPosition.getEndIndex()));
                }
                // add the name substring =/= name because name is the name of the corresponding listingredient
                Position namePosition = ingredient.getNamePosition();
                sentences.add(step.getDescription().substring(namePosition.getBeginIndex(),
                        namePosition.getEndIndex()));
            }

            // add the timers
            for (RecipeTimer timer : step.getRecipeTimers()) {
                Position timerPosition = timer.getPosition();
                sentences.add(step.getDescription().substring(timerPosition.getBeginIndex(),
                        timerPosition.getEndIndex()));
            }
        }

        return sentences;
    }

    /**
     * Creates a new recipe object that is the translated form of this recipe
     *
     * @param translatedSentences the translated sentences, this is the response from aurora to the
     *                            result of {@link #sentencesToTranslate(Recipe)}
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
    private static List<ListIngredient> getTranslatedIngredients(Queue<String> translatedSentences, Recipe originalRecipe) {
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
     * @param translatedSentences the result of the translation of the entire recipe, this will be altered during the execution
     * @param originalRecipe      the untranslated version of the recipe
     * @return the list with the translated steps of this recipe
     */
    private static List<RecipeStep> getTranslatedSteps(Queue<String> translatedSentences, Recipe originalRecipe) {
        List<RecipeStep> newSteps = new ArrayList<>();
        for (RecipeStep oldStep : originalRecipe.getRecipeSteps()) {
            // start with the description
            String description = translatedSentences.poll();
            RecipeStep newStep = new RecipeStep(description);

            // the ingredients
            if (oldStep.isIngredientDetectionDone()) {
                List<Ingredient> newIngredients = new ArrayList<>();

                for (Ingredient oldIngredient : oldStep.getIngredients()) {

                    Ingredient newIngredient = (getTranslatedIngredient(oldStep.getDescription(), oldIngredient, description, translatedSentences));
                    // get the name position
                    String translatedNameSubstring = translatedSentences.poll();

                    int beginIndex = newStep.getDescription().indexOf(translatedNameSubstring);
                    // if this substring is not found try deleting an end character that google might have added
                    if (beginIndex < 0) {
                        translatedNameSubstring = translatedNameSubstring.substring(0, translatedNameSubstring.length() - 1);
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
            if (oldStep.isTimerDetectionDone()) {
                int startIndex = 0;
                List<RecipeTimer> newTimers = new ArrayList<>();
                for (RecipeTimer oldTimer : oldStep.getRecipeTimers()) {
                    String newTimerString = translatedSentences.poll();

                    int beginIndex = description.substring(startIndex).indexOf(newTimerString) + startIndex;
                    int endIndex = beginIndex + newTimerString.length();
                    // for next timer only start searching starting from this end
                    startIndex = endIndex - startIndex;
                    newTimers.add(new RecipeTimer(oldTimer.getLowerBound(), oldTimer.getUpperBound(),
                            new Position(beginIndex, endIndex)));
                }
                newStep.setTimerDetectionDone(true);
                newStep.setRecipeTimers(newTimers);
            }
            newSteps.add(newStep);
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
    private static Ingredient getTranslatedIngredient(String oldOriginalLine, Ingredient oldIngredient, String newOriginalLine,
                                                      Queue<String> translatedSentences) {


        String name;
        String unit = "";
        double quantity = 1;
        Map<Ingredient.PositionKeysForIngredients, Position> map = new EnumMap<>(Ingredient.PositionKeysForIngredients.class);
        // name
        {
            name = translatedSentences.poll();
            // get the position
            int beginIndex = newOriginalLine.indexOf(name);
            if (beginIndex > -1) {
                int endIndex = beginIndex + name.length();
                map.put(Ingredient.PositionKeysForIngredients.NAME, new Position(beginIndex, endIndex));
            }

        }

        // set name position to nothing detected if nothing detected or the nameposition is irrelevant
        map.putIfAbsent(Ingredient.PositionKeysForIngredients.NAME, new Position(0, newOriginalLine.length()));


        // unit
        if (oldIngredient.unitDetected(oldOriginalLine)) {
            unit = translatedSentences.poll();

            // get the position
            int beginIndex = newOriginalLine.indexOf(unit);
            if (beginIndex > -1) {
                int endIndex = beginIndex + unit.length();
                if (DIMINUTIVE.equals(newOriginalLine.substring(endIndex, endIndex + 2))) {
                    // this is in true in the case of cup -> kop but in sentence it becomes kopje
                    endIndex += 2;
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
            int beginIndex = newOriginalLine.indexOf(quantityString);
            if (beginIndex > -1) {
                int endIndex = beginIndex + quantityString.length();
                map.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(beginIndex, endIndex));
            }
        }
        // set quantity position to nothing detected if nothing detected
        map.putIfAbsent(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(0, newOriginalLine.length()));

        return new Ingredient(name, unit, quantity, map);
    }

}
