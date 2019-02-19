package com.aurora.souschef.SouschefProcessorTest.IngredientDetectorTest;

import org.junit.Test;

import java.util.ArrayList;

import SouschefProcessor.IngredientDetector.IngredientDetector;
import SouschefProcessor.IngredientDetector.IngredientUnitAmount;

public class IngredientDetectorTest {

    @Test
    public void IngredientDetector_not_empyt(){
        String test = "500 gram spaghetti \n 500 gram sauce";
        ArrayList<IngredientUnitAmount> result = IngredientDetector.detectIngredients(test);

        assert(result.size() == 2);
    }
    @Test
    public void IngredientDetector_Ingredients_with_unit_filled_in() {
            //example with a unit
            String test = "500 gram spaghetti \n 500 gram sauce";
            ArrayList<IngredientUnitAmount> result = IngredientDetector.detectIngredients(test);
            for(IngredientUnitAmount ing: result){

                assert(ing.getIngredient()!="" && ing.getAmount() != 0 && ing.getUnit() != "");
            }
    }

}
