package com.aurora.souschef.recipe;

import java.util.Objects;

public class Amount {

    private double mValue;
    private String mUnit;

    public Amount(double mValue, String unit) {
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
            if (a.getUnit().equals(mUnit) && a.getValue() == mValue) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return mValue + " " + mUnit;
    }
}
