package com.aurora.souschef.SouschefProcessorTest.IngredientDetectorTest;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import SouschefProcessor.Task.IngredientDetector.IngredientDetectorList;
import SouschefProcessor.Recipe.IngredientUnitAmount;
import SouschefProcessor.Recipe.Step;

public class IngredientDetectorTest {

    private static IngredientDetectorList detector1;
    private static Step s1_1; //step 1 used in detector 1
    private static Step s1_2; //step 2 used in detector 2

    @BeforeClass
    public static void initialize(){
        //set up for one detector with some steps
        String ingredients = "500 gram spaghetti \n 500 gram sauce";
        s1_1 = new Step("Put 500 gram sauce in the microwave");
        s1_2 = new Step("Put 500 gram spaghetti in boiling water");
        ArrayList<Step> steps = new ArrayList<>();
        steps.add(s1_2);
        steps.add(s1_1);
        detector1 = IngredientDetectorList.createIngredientDetector(ingredients,steps);
    }
    @Test
    public void IngredientDetector_ingredient_list_correct_size(){
        ArrayList<IngredientUnitAmount> result = detector1.getIngredientList();
        assert(result.size() == 2);
    }
    @Test
    public void IngredientDetector_Ingredients_with_unit_filled_in() {
            //example with a unit
            ArrayList<IngredientUnitAmount> result = detector1.getIngredientList();
            for(IngredientUnitAmount ing: result){
                assert(ing.getIngredient()!="" && ing.getAmount() != 0 && ing.getUnit() != "");
            }
    }

    @Test
    public void IngredientDetector_map_constructed_correct_size(){
        HashMap<Step, ArrayList<IngredientUnitAmount>> map = detector1.getIngredientsPerStep();
        assert(map.size() == 2);
    }

    @Test
    public void IngredientDetector_map_correctly(){

        HashMap<Step, ArrayList<IngredientUnitAmount>> map = detector1.getIngredientsPerStep();

        //assert first element of list is correct (list should only have 1 element)
        assert(map.get(s1_1).get(0).equals(new IngredientUnitAmount("sauce", "gram", 500)));
        assert(map.get(s1_2).get(0).equals(new IngredientUnitAmount("spaghetti", "gram", 500)));
    }

}
