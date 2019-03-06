package com.aurora.souschef.souschefprocessor.task.ingredientdetector;

import android.util.Log;

import com.aurora.souschef.recipe.Ingredient;
import com.aurora.souschef.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschef.souschefprocessor.task.RecipeInProgress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import static android.content.ContentValues.TAG;

/**
 * Detects the mIngredients in the list of mIngredients
 */
public class DetectIngredientsInListTask extends AbstractProcessingTask {


    // generally numbers greater than twelve are not spelled out
    private static final String[] NUMBERS_TO_TWELVE = {"zero", "one", "two", "three", "four", "five",
            "six", "seven", "eight", "nine", "ten", "eleven", "twelve"};
    // multiples of ten are also spelled out
    private static final String[] MULTIPLES_OF_TEN = {"zero", "ten", "twenty", "thirty", "fourty",
            "fifty", "sixty", "seventy", "eighty", "ninety", "hundred"};
    private static final int FRACTION_SIZE = 2;
    private static final int NON_FRACTION_LENGTH = 1;
    private static final double TEN = 10;
    private CRFClassifier<CoreLabel> crf;

    public DetectIngredientsInListTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);

    }

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

        // if not one of the previous cases consider wrongly labeled and return zero
        return 0.0;
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


                Ingredient ing = (detectIngredient(ingredient));
                if (ing != null) {
                    returnSet.add(ing);
                }

            }
        }
        return returnSet;
    }

    private Ingredient detectIngredient(String line) {
        Ingredient ing = null;
        double quantity = 0.0;
        String unit = "";
        String name = "";
        try {
            if (crf == null) {
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


            //for now get the first element
            if (map.get("UNIT") != null) {
                unit = map.get("UNIT").get(0).toString();
            }
            // return everything labeled name
            List<CoreLabel> nameList = map.get("NAME");
            if (nameList != null) {
                name = buildName(nameList);

            }
            if (map.get("QUANTITY") != null) {
                quantity += calculateQuantity(map.get("QUANTITY"));
            }
            // if quantity is seen as negative revert
            if (quantity < 0.0) {
                quantity = -quantity;
            }
         
            ing = new Ingredient(name, unit, quantity);
            return ing;
        } catch (IOException | ClassNotFoundException exception) {
            Log.e(TAG, "detect ingredients in list: classifier not loaded ", exception);
        }
        return null;
    }

    private String buildName(List<CoreLabel> nameList) {
        // return everey entity labeled name
        StringBuilder bld = new StringBuilder();
        for (CoreLabel cl : nameList) {
            bld.append(cl.word() + " ");
        }
        // delete last added space
        bld.deleteCharAt(bld.length() - 1);
        return bld.toString();
    }

    private double calculateQuantity(List<CoreLabel> list) {
        double result = 0.0;
        // for now first element
        CoreLabel element = list.get(0);
        String representation = element.word();

        // split on all whitespace characters
        String[] array = representation.split("[\\s\\xA0]+");
        for (String s : array) {

            String[] fraction = s.split("/");
            try {
                if (fraction.length == FRACTION_SIZE) {

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
}
