package com.aurora.souschefprocessor.recipe;

import java.util.Objects;

public class Amount {

    /** The value of this amount*/
    private double mValue;
    /** The unit of this amount*/
    private String mUnit;

    public Amount(double mValue, String unit) {
        if (mValue < 0.0) {
            throw new IllegalArgumentException("Value is negative");
        }
        this.mValue = mValue;
        this.mUnit = unit;
    }

    public void setValue(double value) {
        this.mValue = value;
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
        return "QUANTITY " + mValue + " UNIT " + mUnit;
    }

    public void setUnit(String unit) {
        this.mUnit = unit;
    }
}
