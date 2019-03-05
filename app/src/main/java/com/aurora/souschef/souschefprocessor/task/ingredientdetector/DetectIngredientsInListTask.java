package com.aurora.souschef.souschefprocessor.task.ingredientdetector;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInListTask extends AbstractProcessingTask {

    private CRFClassifier<CoreLabel> crf;
    // this is for the dummy detect code
    private static final int INGREDIENT_WITH_UNIT_SIZE = 3;
    private static final int INGREDIENT_WITHOUT_UNIT_SIZE = 2;
    private static final int INGREDIENT_PLACE = 2;
    private static final int UNIT_PLACE = 1;
    private static final int AMOUNT_PLACE = 0;

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);

    }

    /**
     * Detects the mIngredients presented in the ingredientsString and sets the mIngredients field
     * in the recipe to this set of mIngredients.
     */
    public void doTask() {
        //TODO fallback if no mIngredients can be detected
        Set<Ingredient> set = detectIngredients(this.mRecipeInProgress.getIngredientsString());
        this.mRecipeInProgress.setIngredients(set);
    }

    /**
     * Detetcs ingredients in a string representing an ingredient list, makes corresponding
     * Ingredient Objects and returns a set of these
     *
     * @param ingredientList The string representing the ingredientList
     * @return A set of Ingredient Objects detected in the string
     */
    private Set<Ingredient> detectIngredients(String ingredientList) {
        // TODO generate functionality

        // dummy
        if (ingredientList == null || ("").equals(ingredientList)) {
            return new HashSet<>();
        }
        Set<Ingredient> returnSet = new HashSet<>();
        String[] list = ingredientList.split("\n");

        for (String ingredient : list) {
            if (ingredient != null) {

                try {
                    Ingredient ing = (detectIngredient(ingredient));
                    if (ing != null) {
                        returnSet.add(ing);
                    }
                } catch (NumberFormatException nfe) {
                    //TODO have appropriate catch if ingredient does not contain a number that is parseable to double
                }
            }
        }
        return returnSet;
    }

    private Ingredient detectIngredient(String line) {
        Ingredient ing = null;
        try {
            if(crf==null) {
                //if classifier not loaded yet load the classifier
                String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
                crf = CRFClassifier.getClassifier(modelName);
            }

            List<List<CoreLabel>> classifiedList = crf.classify(line);
            Map<String, List<CoreLabel>> map = new HashMap<>();

            for (List<CoreLabel> l : classifiedList) {
                for (CoreLabel cl : l) {
                    String entity = (cl.get(CoreAnnotations.AnswerAnnotation.class));
                    if (map.get(entity) == null) {
                        List<CoreLabel> list = new ArrayList<>();
                        list.add(cl);
                        map.put(entity, list);
                    } else {
                        map.get(entity).add(cl);
                    }

                }
            }
            double quantity = 0.0;
            String unit = "";
            String name = "";

            //for now get the first element
            if (map.get("QUANTITY") != null) {
                quantity += Double.parseDouble(map.get("QUANTITY").get(0).toString());
            }
            if (map.get("UNIT") != null) {
                unit = map.get("UNIT").get(0).toString();
            }
            if (map.get("NAME") != null) {
                name = map.get("NAME").get(0).toString();
            }
            ing = new Ingredient(name, unit, quantity);
            return ing;
        } catch (IOException ioe) {

        } catch (ClassNotFoundException cnfe) {

        }
        return null;


    }
}
