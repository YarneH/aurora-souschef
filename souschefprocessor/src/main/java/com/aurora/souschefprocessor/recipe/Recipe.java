package com.aurora.souschefprocessor.recipe;

import com.aurora.auroralib.PluginObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A data class representing a recipe. It has 4 fields:
 * mIngredients: a list of ListIngredient objecs, this represents the ListIngredients needed for
 * this recipe.
 * mRecipeSteps: A list of RecipeSteps in this recipe
 * mNumberOfPeople: the amount of people the basic recipe is for
 * mDescription: a description of the recipe (could be that it is not present)
 */
public class Recipe extends PluginObject {
    /**
     * The list of detected {@link ListIngredient} in this recipe
     */
    protected List<ListIngredient> mIngredients = new ArrayList<>();

    /**
     * The list of detected {@link RecipeStep} in this recipe
     */
    protected List<RecipeStep> mRecipeSteps = new ArrayList<>();

    /**
     * The detected number of people this recipe is for
     */
    protected int mNumberOfPeople;

    /**
     * The (optional) description of this recipe
     */
    protected String mDescription;


    public Recipe(String fileName, List<ListIngredient> ingredients, List<RecipeStep> recipeSteps,
                  int numberOfPeople, String description, String pluginName) {
        super(fileName, pluginName);
        this.mIngredients = ingredients;
        this.mRecipeSteps = recipeSteps;
        this.mNumberOfPeople = numberOfPeople;
        this.mDescription = description;
    }

    public Recipe(String fileName, String pluginName) {
        super(fileName, pluginName);
    }

    /**
     * Converts the units of the this recipe to metric or US
     *
     * @param toMetric boolean to indicate if it should be converted to metric or US
     */
    public void convertUnit(boolean toMetric) {
        for (ListIngredient listIngredient : mIngredients) {
            listIngredient.convertUnit(toMetric);
        }
        for (RecipeStep step : mRecipeSteps) {
            step.convertUnit(toMetric);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(mIngredients, mNumberOfPeople, mRecipeSteps, mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Recipe) {
            Recipe r = (Recipe) o;
            return r.getIngredients().equals(mIngredients) && r.getNumberOfPeople() == mNumberOfPeople
                    && r.getRecipeSteps().equals(mRecipeSteps) && r.getDescription().equals(mDescription);
        }
        return false;
    }

    /**
     * Default getter
     *
     * @return the list of ListIngredients
     */
    public synchronized List<ListIngredient> getIngredients() {
        return mIngredients;
    }

    /**
     * Default getter
     *
     * @return the number of people this recipe is for
     */
    public synchronized int getNumberOfPeople() {
        return mNumberOfPeople;
    }

    /**
     * Default setter
     *
     * @param numberOfPeople the new number of people this recipe is for
     */
    public synchronized void setNumberOfPeople(int numberOfPeople) {
        this.mNumberOfPeople = numberOfPeople;
    }

    /**
     * Default getter
     *
     * @return the list of RecipeSteps of this recipe
     */
    public synchronized List<RecipeStep> getRecipeSteps() {
        return mRecipeSteps;
    }

    /**
     * Default getter
     *
     * @return the description of this recipe
     */
    public synchronized String getDescription() {
        return mDescription;
    }

    /**
     * Default setter
     *
     * @param description the new description of this recipe
     */
    public synchronized void setDescription(String description) {
        this.mDescription = description;
    }

    /**
     * Sets the recipesteps
     *
     * @param recipeSteps the new recipesteps, if this is null then the existing list is cleared
     */
    public synchronized void setRecipeSteps(List<RecipeStep> recipeSteps) {
        if (recipeSteps == null) {
            mRecipeSteps.clear();
        } else {
            this.mRecipeSteps = recipeSteps;
        }
    }

    /**
     * Sets the ingredients
     *
     * @param ingredients the new ingredients, if this is null then the existing list is cleared
     */
    public synchronized void setIngredients(List<ListIngredient> ingredients) {
        if (ingredients == null) {
            mIngredients.clear();
        } else {
            this.mIngredients = ingredients;
        }
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "mIngredients=" + mIngredients +
                "\n mRecipeSteps=" + mRecipeSteps +
                "\n mNumberOfPeople=" + mNumberOfPeople +
                "\n mDescription='" + mDescription + '\'' +
                '}';
    }


    /**
     * Gets all the sentences that should be translated to translate this recipe
     *
     * @return A list of all the sentences that should be translated
     */
    public List<String> createSentencesToTranslate() {
        return TranslateHelper.createSentencesToTranslate(this);

    }


    /**
     * Creates a new recipe object that is the translated form of this recipe
     *
     * @param translatedSentences the translated sentences, this is the response from aurora to the
     *                            result of {@link #createSentencesToTranslate()}
     * @return The new translated recipe
     */
    public Recipe getTranslatedRecipe(String[] translatedSentences) {
        return TranslateHelper.getTranslatedRecipe(this, translatedSentences);
    }

}
