package com.aurora.souschefprocessor.task;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A subclass of Recipe, representing a Recipe Object that is being constructed. It has
 * additional fields:
 * mIngredientsString: a string representing the mIngredients list
 * mStepsString: a string representing the different mRecipeSteps in the recipe
 * mExtractedText: the ExtractedText that was extracted by Aurora
 * mStepsInProgress: a list of recipeSteps that are in the progress of being built
 * mNamePartsMap: A map that maps all the listIngredients of this recipe to a list of words that make up the name of
 * this listingredient
 * mNamePartsCommonElementsMergedMap: A map that does the same as the aforementioned, but all the words in the lists
 * are unique, by making wordgroups if two lists would have had the same elements
 */
public class RecipeInProgress extends Recipe {
    /**
     * The string representing the ingredients list that was detected using the
     * {@link com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask}
     */
    private String mIngredientsString;

    /**
     * The string representing the steps list that was detected using the
     * {@link com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask}
     */
    private String mStepsString;


    /**
     * An extractedText object from Aurora
     */
    private ExtractedText mExtractedText;

    /**
     * A list of steps that are in progress of being built, in the {@link #convertToRecipe()} method these steps are
     * converted to {@link com.aurora.souschefprocessor.recipe.RecipeStep}
     */
    private List<RecipeStepInProgress> mStepsInProgress = new ArrayList<>();
    /**
     * Maps ListIngredients to a an array of words in their name for matching the name in the step
     * Necessary in case only a certain word of the list ingredient is used to describe it in the step
     */
    private Map<ListIngredient, List<String>> mNamePartsMap;
    /**
     * Maps ListIngredients to a an array of words in their name for matching the name in the step
     * Necessary in case only a certain word of the list ingredient is used to describe it in the step. However here
     * all lists contain unique elements so some elements are multiple words
     */
    private Map<ListIngredient, List<String>> mNamePartsCommonElementsMergedMap;

    public RecipeInProgress(ExtractedText originalText, String pluginName) {
        super(originalText.getFilename(), pluginName);
        this.mExtractedText = originalText;
        this.mDescription = "";
        this.mNumberOfPeople = -1;


    }

    /**
     * Default getter
     *
     * @return the string describing the steps
     */
    public synchronized String getStepsString() {
        return mStepsString;
    }

    /**
     * Default setter
     *
     * @param stepsString the new string describing the steps of this recipe
     */
    public synchronized void setStepsString(String stepsString) {
        this.mStepsString = stepsString;
    }

    /**
     * Default getter
     *
     * @return the string describing the ingredients
     */
    public synchronized String getIngredientsString() {
        return mIngredientsString;
    }


    /**
     * Default setter
     *
     * @param ingredientsString the new string describing the ingredients of this recipe
     */
    public synchronized void setIngredientsString(String ingredientsString) {
        this.mIngredientsString = ingredientsString;
    }

    /**
     * Converts the RecipeInProgress to a Recipe object by dropping the two additional fields and
     * converting the {@link RecipeStepInProgress} steps to {@link com.aurora.souschefprocessor.recipe.RecipeStep}
     * steps
     *
     * @return the converted recipe
     */
    public Recipe convertToRecipe() {
        for (RecipeStepInProgress step : mStepsInProgress) {
            mRecipeSteps.add(step.convertToRecipeStep());
        }
        return new Recipe(mFileName, mIngredients, mRecipeSteps, mNumberOfPeople, mDescription, mUniquePluginName);
    }

    /**
     * Default getter
     *
     * @return the extracted text
     */
    public ExtractedText getExtractedText() {
        return mExtractedText;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mIngredients, mNumberOfPeople, mStepsInProgress, mDescription);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RecipeInProgress) {
            RecipeInProgress r = (RecipeInProgress) o;
            return r.getIngredients().equals(mIngredients) && r.getNumberOfPeople() == mNumberOfPeople
                    && r.getStepsInProgress().equals(mStepsInProgress) &&
                    r.getDescription().equals(mDescription);
        }
        return false;
    }

    /**
     * Executes the {@link Recipe#setIngredients(List)} and also call
     * {@link #initializeListIngredientAndNamePartsMaps()}
     * for setting the {@link #mNamePartsCommonElementsMergedMap} and {@link #mNamePartsMap} maps
     *
     * @param ingredients the new ingredients, if this is null then the existing list is cleared
     */
    @Override
    public void setIngredients(List<ListIngredient> ingredients) {
        super.setIngredients(ingredients);
        initializeListIngredientAndNamePartsMaps();
    }

    @Override
    public String toString() {
        return "RECIPE{" +
                "INGREDIENTS='" + mIngredientsString + "\n" +
                ", STEPS='" + mStepsString + "\n" +
                ", DESCRIPTION='" + mDescription + "\n" +
                '}';
    }

    /**
     * creates the {@link #mNamePartsCommonElementsMergedMap} and {@link #mNamePartsCommonElementsMergedMap} maps
     */
    private void initializeListIngredientAndNamePartsMaps() {


        Map<ListIngredient, List<String>> ingredientListMap = new HashMap<>();
        for (ListIngredient listIngr : mIngredients) {
            ingredientListMap.put(listIngr, new LinkedList<>(
                    Arrays.asList(listIngr.getName().toLowerCase(Locale.ENGLISH)
                            .replace(",", "").split(" "))));
        }
        mNamePartsMap = (ingredientListMap);

        Map<ListIngredient, List<String>> mergedCommonPartsMap = (createCommonPartsMergedMap(mIngredients));
        for (ListIngredient listIngredient : mIngredients) {
            // remove the doubles so every string is only searched once
            mergedCommonPartsMap.get(listIngredient).removeAll(ingredientListMap.get(listIngredient));
        }
        mNamePartsCommonElementsMergedMap = (mergedCommonPartsMap);

    }

    /**
     * Construct a map which matches the listingredients with their nameparts, but if multiple ingredients have
     * common parts the parts are merged with preceding or suceeding parts to create unique identifiers
     *
     * @param ingredients the listingredients that will be the keys for the map
     * @return a map wich maps the ingredients with the unique name pars
     */
    private static Map<ListIngredient, List<String>> createCommonPartsMergedMap(List<ListIngredient> ingredients) {

        // first create the map with all the elements (these could be common for different keys), each element has
        // its own list of which they are currently the only element
        Map<ListIngredient, List<String>> commonPartsMerged = new HashMap<>();
        for (ListIngredient listIngredient : ingredients) {
            commonPartsMerged.put(listIngredient, new LinkedList<>(
                    Arrays.asList(listIngredient.getName().toLowerCase(Locale.ENGLISH)
                            .replace(",", "").split(" "))));
        }


        // if a word is present in several lists, merge it with the previous or next word
        // go over the ingredients
        for (int i = 0; i < ingredients.size(); i++) {
            List<String> listI = commonPartsMerged.get(ingredients.get(i));
            // go over the other ingredients as to only search each pair once
            for (int j = i + 1; j < ingredients.size(); j++) {
                List<String> listJ = commonPartsMerged.get(ingredients.get(j));
                mergeCommonElements(listI, listJ);
            }

        }

        return commonPartsMerged;
    }

    /**
     * If two lists share some identical elements then these elements will be merged with the element that comes
     * before them in the list (or afterwards if before is not possible) if possible
     *
     * @param list1 the first list
     * @param list2 the second list
     */
    private static void mergeCommonElements(List<String> list1, List<String> list2) {

        // make a new list that has all the common elements
        List<String> commonList = (new ArrayList<>(list1));
        commonList.retainAll(list2);
        // a boolean that indicates wheter the while loop should stop
        boolean stop = false;
        // while there are still common elements and the lists have more than 1 element (otherwise merging is not
        // possible anymore)
        while (!commonList.isEmpty() && !stop) {
            for (String commonString : commonList) {
                // list 1
                mergeElement(commonString, list1);
                mergeElement(commonString, list2);
            }
            commonList.clear();
            commonList.addAll(list1);
            commonList.retainAll(list2);
            if (list1.size() == 1 && list2.size() == 1) {
                stop = true;
            }
        }

    }

    /**
     * Merges an element of the list with the previous (if possible) or next (if possible element) by concatenating
     * them and adding a space between
     *
     * @param elementToBeMerged the element to be merged
     * @param list              the list where the element is in
     */
    private static void mergeElement(String elementToBeMerged, List<String> list) {
        int index = list.indexOf(elementToBeMerged);
        if (index > 0) {
            // merge with previous
            list.set(index - 1, list.get(index - 1) + " " + elementToBeMerged);

            list.remove(index);

        } else {
            if (index < list.size() - 1 && index > -1) {
                // merge with next
                list.set(index + 1, elementToBeMerged + " " + list.get(index + 1));
                list.remove(index);
            }
        }
    }

    /**
     * Default getter
     *
     * @return the list of the steps in progress
     */
    public List<RecipeStepInProgress> getStepsInProgress() {
        return mStepsInProgress;
    }

    /**
     * Default setter, if the argument is null then the existing list is cleared
     *
     * @param stepsInProgress the new list of steps in progress
     */
    public void setStepsInProgress(List<RecipeStepInProgress> stepsInProgress) {
        if (stepsInProgress == null) {
            mStepsInProgress.clear();
            return;
        }
        mStepsInProgress = stepsInProgress;
    }

    /**
     * Default getter
     *
     * @return the map mapping listingredients to the words of its name
     */
    public Map<ListIngredient, List<String>> getNamePartsMap() {
        return mNamePartsMap;
    }

    /**
     * Default getter
     *
     * @return the map mapping listingredients to the unique words or wordgroups of its name
     */
    public Map<ListIngredient, List<String>> getNamePartsCommonElementsMergedMap() {
        return mNamePartsCommonElementsMergedMap;
    }
}
