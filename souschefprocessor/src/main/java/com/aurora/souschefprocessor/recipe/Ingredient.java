package com.aurora.souschefprocessor.recipe;

import java.util.Map;
import java.util.Objects;

/**
 * A data class that represents an mName in the mName list of the recipe or a step of the
 * recipe. The class has three fields:
 * mName: which is a mDescription of the mName (e.g. sugar, tomato)
 * mAmount: the mAmount of the unit (e.g. 500)
 * mPositions: A map that maps the keys: UNIT, NAME, QUANTITY to their positions, for an ingredient
 * in a step this is the position in the description of the step, for an ingredient in
 * the list of ingredients this is the position in the string describing this ingredient
 * in the list. If one of these three is not detected, its position is set from 0 to the length of
 * the description
 */
public class Ingredient {

    protected String mName;
    protected Amount mAmount;

    protected Map<PositionKey, Position> mPositions;

    public Ingredient(String name, String unit, double value, Map<PositionKey, Position> positions) {
        this.mName = name;
        this.mAmount = new Amount(value, unit);
        this.mPositions = positions;
    }

    public Position getNamePosition() {
        return mPositions.get(PositionKey.NAME);
    }

    public Position getQuantityPosition() {
        return mPositions.get(PositionKey.QUANTITY);
    }

    public Position getUnitPosition() {
        return mPositions.get(PositionKey.UNIT);
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
        String res = mAmount + " NAME ";
        return res + mName;
    }

    /**
     * This function checks if the positions of the NAME, UNIT and AMOUNT of this object are legal
     * in a string. Legal means: beginIndex at the most the length of the string minus 1 (last character)
     * and the endindex at most the length of the string (character after last character)
     *
     * @param string The string in which to check that the positions are legal
     * @return a boolean indicating if the positions are legal
     */
    public boolean arePositionsLegalInString(String string) {
        for (PositionKey key : PositionKey.values()) {
            if (!mPositions.get(key).isLegalInString(string)) {
                return false;
            }
        }
        return true;
    }



    public enum PositionKey {
        NAME, QUANTITY, UNIT
    }


}
