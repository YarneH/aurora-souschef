package com.aurora.souschef.SouchefProcessor.Recipe;

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
        int result = 17;
        result = 31 * result + mUnit.hashCode();
        result = 31 * result + Double.valueOf(mValue).hashCode();
        return result;
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
