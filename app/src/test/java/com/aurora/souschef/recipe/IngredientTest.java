package com.aurora.souschef.recipe;

import org.junit.Test;

import java.util.ArrayList;

public class IngredientTest {

    @Test
    public void Ingredient_Equals_BehavesExpectedely() {
        Ingredient iua1 = new Ingredient("spaghetti", "gram", 500);
        Ingredient iua2 = new Ingredient("spaghetti", "gram", 500);
        Ingredient iua3 = new Ingredient("sauce", "gram", 500);
        assert (iua1.equals(iua2));
        assert (!iua1.equals(iua3));
        String randomobject = "3";
        assert (!iua1.equals(randomobject));
    }

    @Test
    public void Ingredient_HashCode_SameOnlyForObjectsThatAreEqual() {
        String[] ingredients = {"spaghetti", "sauce", "meatballs"};
        String[] units = {"gram", "kilogram"};
        double[] amounts = {500, 1};
        ArrayList<Ingredient> iuas = new ArrayList<>();
        while (iuas.size() < 20) {
            for (String ing : ingredients) {
                for (String uni : units) {
                    for (double a : amounts) {
                        iuas.add(new Ingredient(ing, uni, a));
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
