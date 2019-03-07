package com.aurora.souschef.recipe;

import java.util.Objects;

/**
 * A data class that represents an mName in the mName list of the recipe or a step of the
 * recipe. The class has three fields:
 * mName: which is a mDescription of the mName (e.g. sugar, tomato)
 * mAmount: the mAmount of the unit (e.g. 500)
 */
public class Ingredient {

    private String mName;
    private Amount mAmount;
    private String mOrignalLine;

    public Ingredient(String name, String unit, double value, String originalText) {
        this.mName = name;
        this.mAmount = new Amount(value, unit);
        this.mOrignalLine = originalText;
    }

    public String getOriginalLine() {
        return mOrignalLine;
    }

    public String getName() {
        return mName;
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
        return Objects.hash(mAmount, mName);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) o;
            if (ingredient.getAmount().equals(mAmount) && ingredient.getName().equals(mName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String res = mAmount + " ";
        return res + mName;
    }


}
