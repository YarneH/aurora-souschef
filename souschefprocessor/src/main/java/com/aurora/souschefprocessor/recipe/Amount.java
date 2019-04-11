package com.aurora.souschefprocessor.recipe;

import java.util.Objects;

/**
 * A dataclass that represents an amount (e.g. 500 ounces)
 * <p>
 * Two fields:
 * value: a double that is the value
 * unit: a string that is the unit
 */
class Amount {

    private final static  int CUP_TO_MILLILITER = 240;
    private final static double KG_TO_POUND = 2.205;
    private final static double FLOZ_TO_MILLILITER = 29.5735;
    private final static double OUNCE_TO_GRAM = 28.3495;
    private final static double TEASPOON_TO_MILLILITER = 4.92892;
    private final static double TABLESPOON_TO_MILLILITER = 14.7868;

    private final static double QUART_TO_LITER = 0.946353;
    private final static double PINT_TO_MILLILITER = 473.176;
    private final static int METRIC_CONSTANT = 10;

    private final static double EQUALITY_THRESHOLD_DOUBLE = 2e-3;
    /**
     * The value of this amount
     */
    private double mValue;
    /**
     * The unit of this amount
     */
    private String mUnit;

    Amount(double mValue, String unit) {
        if (mValue < 0.0) {
            throw new IllegalArgumentException("Value is negative");
        }
        this.mValue = mValue;
        this.mUnit = unit;
    }

    static String getBaseUnit(String original) {
        return BaseUnits.getBase(original);
    }

    double getValue() {
        return mValue;
    }

    void setValue(double value) {
        this.mValue = value;
    }

    String getUnit() {
        return mUnit;
    }

    void setUnit(String unit) {
        this.mUnit = unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mUnit, mValue);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Amount) {
            Amount a = (Amount) o;
            return (a.getUnit().equalsIgnoreCase(mUnit) &&
                    Math.abs(a.getValue() - mValue) < EQUALITY_THRESHOLD_DOUBLE);

        }
        return false;
    }

    @Override
    public String toString() {
        return "QUANTITY " + mValue + " UNIT " + mUnit;
    }

    private void convertToMetric() {
        switch (mUnit) {

            case "cup":
                mValue *= CUP_TO_MILLILITER;
                mUnit = "milliliter";
                break;
            case "pound":
                mValue /= KG_TO_POUND;
                mUnit = "kilogram";
                break;
            case "fluid ounce":
                mValue *= FLOZ_TO_MILLILITER;
                mUnit = "milliliter";
                break;
            case "ounce":
                mValue *= OUNCE_TO_GRAM;
                mUnit = "gram";
                break;
            case "quart":
                mValue *= QUART_TO_LITER;
                mUnit = "liter";
                break;
            case "pint":
                mValue *= PINT_TO_MILLILITER;
                mUnit = "milliliter";
                break;
            case "teaspoon":
                mValue *= TEASPOON_TO_MILLILITER;
                mUnit = "milliliter";
                break;
            case "tablespoon":
                mValue *= TABLESPOON_TO_MILLILITER;
                mUnit = "milliliter";
                break;
            default:
                break;
        }
    }

    void convert(boolean toMetric) {
        // source https://en.wikipedia.org/wiki/Cup_(unit)#Metric_cup
        if (toMetric) {
            convertToMetric();

        } else {
            convertToUS();
        }
        // round to three decimals
        mValue = Math.round(1e3 * mValue)/1e3;
    }

    private void convertToUS() {
        switch (mUnit) {
            case "kilogram":
                mValue *= KG_TO_POUND;
                mUnit = "pound";
                break;
            case "gram":
                mValue /= OUNCE_TO_GRAM;
                mUnit = "ounce";
                break;
            case "liter":
                mValue /= QUART_TO_LITER;
                mUnit = "quart";
                break;
            case "milliliter":
                changeMilliliter();

                break;
            case "deciliter":
                mValue /= TABLESPOON_TO_MILLILITER * METRIC_CONSTANT;
                mUnit = "tablespoon";
                break;
            default:
                break;
        }
    }

    private void changeMilliliter(){
        if (mValue >= CUP_TO_MILLILITER) {
            mValue /= CUP_TO_MILLILITER;
            mUnit = "cup";
        } else if (mValue >= FLOZ_TO_MILLILITER) {
            mValue /= FLOZ_TO_MILLILITER;
            mUnit = "fluid ounce";
        } else if (mValue >= TABLESPOON_TO_MILLILITER) {
            mValue /= TABLESPOON_TO_MILLILITER;
            mUnit = "tablespoon";
        }

        mValue /= TEASPOON_TO_MILLILITER;
        mUnit = "teaspoon";
    }


}
