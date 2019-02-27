package com.aurora.souschef.recipe;

import java.util.Objects;

/**
 * A data class that represents an mIngredient in the mIngredient list of the recipe or a step of the
 * recipe. The class has three fields:
 * mIngredient: which is a mDescription of the mIngredient (e.g. sugar, tomato)
 * unit: the unit of the mIngredient (e.g. tablespoon, gram)
 * mAmount: the mAmount of the unit (e.g. 500)
 */
public class Ingredient {

    private String mIngredient;
    private Amount mAmount;

    public Ingredient(String ingredient, String unit, double value) {
        this.mIngredient = ingredient;
        this.mAmount = new Amount(value, unit);
    }

    public String getIngredient() {
        return mIngredient;
    }


    public String getUnit() {
        return mAmount.getUnit();
    }

    public double getValue() {
        return mAmount.getValue();
    }

    public Amount getAmount() {
        return mAmount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mAmount,mIngredient);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) o;
            if (ingredient.getAmount().equals(mAmount) && ingredient.getIngredient().equals(mIngredient)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String res = mAmount + " ";
        return res + mIngredient;
    }


}
