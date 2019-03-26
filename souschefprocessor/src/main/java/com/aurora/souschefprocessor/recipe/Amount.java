package com.aurora.souschefprocessor.recipe;

import java.util.Objects;

/**
 * A dataclass that represents an amount (e.g. 500 ounces)
 *
 * Two fields:
 * value: a double that is the value
 * unit: a string that is the unit
 */
public class Amount {

    private double mValue;
    private String mUnit;

    public Amount(double mValue, String unit) {
        if (mValue < 0.0) {
            throw new IllegalArgumentException("Value is negative");
        }
        this.mValue = mValue;
        this.mUnit = unit;
    }

    public double getValue() {
        return mValue;
    }

    public String getUnit() {
        return mUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUnit, mValue);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Amount) {
            Amount a = (Amount) o;
            if (a.getUnit().equalsIgnoreCase(mUnit) && a.getValue() == mValue) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "QUANTITY " + mValue + " UNIT " + mUnit;
    }
}
