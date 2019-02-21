package com.aurora.souschef.SouschefProcessor.Recipe;

import org.junit.Test;

import SouschefProcessor.Recipe.IngredientUnitAmount;

public class IngredientUnitAmountTest {

    @Test
    public void IngredientUnitAmount_Equals_BehavesExpectedely(){
        IngredientUnitAmount iua1 = new IngredientUnitAmount("spaghetti", "gram", 500);
        IngredientUnitAmount iua2 = new IngredientUnitAmount("spaghetti","gram", 500);
        IngredientUnitAmount iua3 = new IngredientUnitAmount("sauce", "gram", 500);
        assert(iua1.equals(iua2));
        assert(!iua1.equals(iua3));
        String randomobject = "3";
        assert(!iua1.equals(randomobject));
    }
}
