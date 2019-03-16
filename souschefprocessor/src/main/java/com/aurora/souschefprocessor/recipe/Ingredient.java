package com.aurora.souschefprocessor.recipe;

import java.util.Map;
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


    private Map<PositionKey, Position> mPositions;

    public Ingredient(String name, String unit, double value, String originalText, Map<PositionKey, Position> positions) {
        this.mName = name;
        this.mAmount = new Amount(value, unit);
        this.mOrignalLine = originalText;

        // check if the positions are legal
        for (PositionKey key : PositionKey.values()) {
            Position position = positions.get(key);

            if (position == null) {
                throw new IllegalArgumentException("Position of " + key + " cannot be null");
            }
            if (!position.isLegalInString(originalText)) {
                throw new IllegalArgumentException("Position of " + key + " is too big");
            }
        }
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

    public enum PositionKey {
        NAME, QUANTITY, UNIT
    }


}
