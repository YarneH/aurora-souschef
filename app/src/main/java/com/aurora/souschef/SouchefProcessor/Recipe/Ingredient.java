package com.aurora.souschef.SouchefProcessor.Recipe;

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

    public Ingredient(String ingredient, String unit, double amount) {
        this.mIngredient = ingredient;
        this.mAmount = new Amount(amount, unit);
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
        int result = 17;
        result = 31 * mAmount.hashCode();
        result = 31 * result + mIngredient.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            Ingredient iua = (Ingredient) o;
            if (iua.getAmount().equals(mAmount) && iua.getIngredient().equals(mIngredient)) {
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
