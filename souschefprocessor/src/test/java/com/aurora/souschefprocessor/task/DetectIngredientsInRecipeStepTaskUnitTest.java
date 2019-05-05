package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.recipe.RecipeStep;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


public class DetectIngredientsInRecipeStepTaskUnitTest {

    private static final String DEFAULT_UNIT = "";
    private static final Double DEFAULT_QUANTITY = 1.0;

    private static RecipeInProgress recipe;
    private static ArrayList<RecipeStepInProgress> recipeSteps;
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static List<String> descriptions;
    private static ExtractedText emptyExtractedText = new ExtractedText("", null);


    @BeforeClass
    public static void initialize() {
        // Initialize recipe in progress
        String originalText = "irrelevant";
        recipe = new RecipeInProgress(emptyExtractedText);

        // Initialize positions with dummy values
        irrelevantPositions = new HashMap<>();
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }

        recipe.setIngredientsString("irrelevant");
        List<ListIngredient> listIngredients = new ArrayList<>();
        ListIngredient spaghettiIngredient = new ListIngredient("spaghetti", "gram", 500, "irrelevant", irrelevantPositions);
        ListIngredient sauceIngredient = new ListIngredient("sauce", "ounce", 500, "irrelevant", irrelevantPositions);
        ListIngredient meatIngredient = new ListIngredient("minced meat", "pound", 1.5, "irrelevant", irrelevantPositions);
        ListIngredient garlicIngredient = new ListIngredient("garlic", "clove", DEFAULT_QUANTITY, "irrelevant", irrelevantPositions);
        ListIngredient basilIngredient = new ListIngredient("basil leaves", DEFAULT_UNIT, 20.0, "irrelevant", irrelevantPositions);
        ListIngredient saltIngredient = new ListIngredient("salt", "cup", DEFAULT_QUANTITY, "irrelevant", irrelevantPositions);
        ListIngredient butterIngredient = new ListIngredient("butter", "", 1.0, "  ", irrelevantPositions);
        listIngredients.add(spaghettiIngredient);
        listIngredients.add(sauceIngredient);
        listIngredients.add(meatIngredient);
        listIngredients.add(garlicIngredient);
        listIngredients.add(basilIngredient);
        listIngredients.add(saltIngredient);
        listIngredients.add(butterIngredient);
        recipe.setIngredients(listIngredients);

        descriptions = new ArrayList<>(Arrays.asList("Cook spaghetti according to package directions.", "Combine meat and a clove of garlic in a large saucepan, and cook over medium-high heat until browned.",
                "Stir in 250 ounces of the sauce and five basil leaves. Add a cup of salt.", "No ingredients are in this recipe step.", "Add one tablespoon of melted butter"));
        RecipeStepInProgress s0 = new RecipeStepInProgress(descriptions.get(0));
        RecipeStepInProgress s1 = new RecipeStepInProgress(descriptions.get(1));
        RecipeStepInProgress s2 = new RecipeStepInProgress(descriptions.get(2));
        RecipeStepInProgress s3 = new RecipeStepInProgress(descriptions.get(3));
        RecipeStepInProgress s4 = new RecipeStepInProgress(descriptions.get(4));


        recipeSteps = new ArrayList<>(Arrays.asList(s0, s1, s2, s3, s4));
        recipe.setStepsInProgress(recipeSteps);

        // annotate the steps
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator());
        pipeline.addAnnotator(new WordsToSentencesAnnotator());
        pipeline.addAnnotator(new POSTaggerAnnotator());

        for (RecipeStepInProgress s : recipeSteps) {
            Annotation a = new Annotation(s.getDescription());
            pipeline.annotate(a);
            s.setSentenceAnnotations(Collections.singletonList(a));
            s.setBeginPosition(0);
        }

    }

    @After
    public void wipeRecipeSteps() {
        for (RecipeStep s : recipeSteps) {
            s.setIngredients(null);
            s.setDescription(descriptions.get(recipeSteps.indexOf(s)));
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_ingredientDetectedWithAbsentFields() {
        int stepIndex = 2;
        (new DetectIngredientsInStepTask(recipe, stepIndex)).doTask();
        Ingredient stepIngredientNoQuantity = new Ingredient("salt", "cup", DEFAULT_QUANTITY, irrelevantPositions);
        Ingredient stepIngredientNoUnit = new Ingredient("basil leaves", DEFAULT_UNIT, 5.0, irrelevantPositions);

        Ingredient ingredientNoQuantity = null;
        Ingredient ingredientNoUnit = null;
        Collection<Ingredient> stepIngredients = recipeSteps.get(stepIndex).getIngredients();
        for (Ingredient ingr : stepIngredients) {
            if (ingr.getName().equals(stepIngredientNoQuantity.getName())) {
                ingredientNoQuantity = ingr;
            }
            if (ingr.getName().equals(stepIngredientNoUnit.getName())) {
                ingredientNoUnit = ingr;
            }
        }

        // Asserts the correct absence of both the quantity and the unit equality
        assertEquals("The detection of the ingredient with no unit failed", stepIngredientNoUnit, ingredientNoUnit);
        assertEquals("The detection of the ingredient with no quantity failed", stepIngredientNoQuantity, ingredientNoQuantity);


    }

    @Test
    public void IngredientDetectorStep_doTask_setHasBeenSetForAllSteps() {
        // Act
        (new DetectIngredientsInStepTask(recipe, 0)).doTask();
        (new DetectIngredientsInStepTask(recipe, 1)).doTask();
        (new DetectIngredientsInStepTask(recipe, 2)).doTask();
        (new DetectIngredientsInStepTask(recipe, 3)).doTask();
        (new DetectIngredientsInStepTask(recipe, 4)).doTask();

        // Assert
        for (RecipeStep s : recipe.getRecipeSteps()) {
            assert (s.isIngredientDetectionDone());
            assert (s.getIngredients() != null);
        }
    }

    @Test
    public void DetectIngredientsInStep_doTask_setHasCorrectSize() {
        // Act
        (new DetectIngredientsInStepTask(recipe, 0)).doTask();
        (new DetectIngredientsInStepTask(recipe, 1)).doTask();
        (new DetectIngredientsInStepTask(recipe, 2)).doTask();
        (new DetectIngredientsInStepTask(recipe, 3)).doTask();
        (new DetectIngredientsInStepTask(recipe, 4)).doTask();

        // Assert
        assert (recipeSteps.get(0).getIngredients().size() == 1);
        assert (recipeSteps.get(1).getIngredients().size() == 2);
        //TODO other steps

    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithoutUnit() {
        // Arrange
        int stepIndex = 0;
        Ingredient stepIngredient = new Ingredient("spaghetti", DEFAULT_UNIT, DEFAULT_QUANTITY, irrelevantPositions);

        // Act
        (new DetectIngredientsInStepTask(recipe, stepIndex)).doTask();

        Collection<Ingredient> stepIngredients = recipeSteps.get(stepIndex).getIngredients();

        // Assert
        assert (stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnit() {
        // Arrange
        int stepIndex = 1;
        Ingredient stepIngredient = new Ingredient("garlic", "clove", DEFAULT_QUANTITY, irrelevantPositions);

        // Act
        (new DetectIngredientsInStepTask(recipe, stepIndex)).doTask();

        Collection<Ingredient> stepIngredients = recipeSteps.get(stepIndex).getIngredients();

        // Assert
        assert (stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndVerboseQuantity() {
        // Arrange
        int stepIndex = 2;
        Ingredient stepIngredient = new Ingredient("basil leaves", DEFAULT_UNIT, 5.0, irrelevantPositions);

        // Act
        (new DetectIngredientsInStepTask(recipe, stepIndex)).doTask();

        Collection<Ingredient> stepIngredients = recipeSteps.get(stepIndex).getIngredients();

        // Assert
        assert (stepIngredients.contains(stepIngredient));
    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndNumericalQuantity() {
        // Arrange
        int stepIndex = 2;
        Ingredient stepIngredient = new Ingredient("sauce", "ounce", 250.0, irrelevantPositions);

        // Act
        (new DetectIngredientsInStepTask(recipe, stepIndex)).doTask();

        List<Ingredient> stepIngredients = recipeSteps.get(stepIndex).getIngredients();


        // Assert

        assertThat("The detection of ingredient with numerical quantity failed", stepIngredients, hasItem(stepIngredient));

    }

    @Test
    public void IngredientDetectorStep_doTask_ingredientDetectedWithUnitAndQuantityAndPosition() {
        // Arrange
        int stepIndex = 2;
        HashMap<Ingredient.PositionKeysForIngredients, Position> positions = new HashMap<>();
        positions.put(Ingredient.PositionKeysForIngredients.NAME, new Position(25, 30));
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, new Position(12, 17));
        positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(8, 11));
        Ingredient stepIngredient = new Ingredient("sauce", "ounce", 250.0, positions);

        // Act
        (new DetectIngredientsInStepTask(recipe, stepIndex)).doTask();

        // Retrieve the sauce ingredient detected in the recipe step
        Collection<Ingredient> stepIngredients = recipeSteps.get(stepIndex).getIngredients();

        Ingredient detectedIngredient = null;
        for (Ingredient ingr : stepIngredients) {
            if (ingr.equals(stepIngredient)) {
                detectedIngredient = ingr;
            }
        }


        // Assert

        assert (detectedIngredient.getUnitPosition().equals(stepIngredient.getUnitPosition()));
        assert (detectedIngredient.getQuantityPosition().equals(stepIngredient.getQuantityPosition()));
        assert (detectedIngredient.getNamePosition().equals(stepIngredient.getNamePosition()));
    }


    @Test
    public void DetectIngredientsInRecipeStepTask_doTask_CorrectDetectionOfUnitIfAdjectiveBetweenUnitAndName() {
        /**
         * stepdescription = "Add one tablespoon of melted butter"
         * Check if the tablespoon is correclty detected
         */
        int stepIndex = 4;

        (new DetectIngredientsInStepTask(recipe, stepIndex)).doTask();


        assertEquals("Tablespoon unit was not detected", "tablespoon", recipeSteps.get(stepIndex).getIngredients().get(0).getUnit());

    }


}
