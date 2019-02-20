package com.aurora.souschef.SouschefProcessorTest.IngredientDetectorTest;

import org.junit.Test;

import java.util.ArrayList;

import SouschefProcessor.IngredientDetector.IngredientDetector;
import SouschefProcessor.IngredientDetector.IngredientUnitAmount;
import SouschefProcessor.StepSplitter.Step;

public class IngredientDetectorTest {

    @Test
    public void IngredientDetector_ingredient_list_correct_size(){
        String test = "500 gram spaghetti \n 500 gram sauce";
        ArrayList<Step> steps = new ArrayList<>();
        IngredientDetector detector = IngredientDetector.createIngredientDetector(test,steps);
        ArrayList<IngredientUnitAmount> result = detector.getIngredientList();

        assert(result.size() == 2);
    }
    @Test
    public void IngredientDetector_Ingredients_with_unit_filled_in() {
            //example with a unit
            String test = "500 gram spaghetti \n 500 gram sauce";
            ArrayList<Step> steps = new ArrayList<>();
            IngredientDetector detector = IngredientDetector.createIngredientDetector(test,steps);
            ArrayList<IngredientUnitAmount> result = detector.getIngredientList();
            for(IngredientUnitAmount ing: result){

                assert(ing.getIngredient()!="" && ing.getAmount() != 0 && ing.getUnit() != "");
            }
    }

}
