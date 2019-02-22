package com.aurora.souschef.SouschefProcessor.Recipe;

import org.junit.Test;

import java.util.ArrayList;

import SouschefProcessor.Recipe.IngredientUnitAmount;

public class IngredientUnitAmountTest {

    @Test
    public void IngredientUnitAmount_Equals_BehavesExpectedely() {
        IngredientUnitAmount iua1 = new IngredientUnitAmount("spaghetti", "gram", 500);
        IngredientUnitAmount iua2 = new IngredientUnitAmount("spaghetti", "gram", 500);
        IngredientUnitAmount iua3 = new IngredientUnitAmount("sauce", "gram", 500);
        assert (iua1.equals(iua2));
        assert (!iua1.equals(iua3));
        String randomobject = "3";
        assert (!iua1.equals(randomobject));
    }

    @Test
    public void IngredientUnitAmount_HashCode_SameOnlyForObjectsThatAreEqual() {
        String[] ingredients = {"spaghetti", "sauce", "meatballs"};
        String[] units = {"gram", "kilogram"};
        double[] amounts = {500, 1};
        ArrayList<IngredientUnitAmount> iuas = new ArrayList<>();
        while (iuas.size() < 20) {
            for (String ing : ingredients) {
                for (String uni : units) {
                    for (double a : amounts) {
                        iuas.add(new IngredientUnitAmount(ing, uni, a));
                    }
                }
            }
        }

        for (int i = 0; i < iuas.size(); i++) {
            for (int j = i + 1; j < iuas.size(); j++) {
                boolean equal = iuas.get(i).equals(iuas.get(j));
                boolean hash = (iuas.get(i).hashCode() == iuas.get(j).hashCode());
                assert ((equal && hash) || (!equal && !hash));
            }
        }


    }
}
