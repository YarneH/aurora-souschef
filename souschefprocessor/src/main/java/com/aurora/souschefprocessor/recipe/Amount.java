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


    /**
     * The metric constant for going from milli to deci
     */
    private static final int METRIC_CONSTANT = 10;


    /**
     * The threshold for the equality of the {@link #mValue} field needed in the {@link #equals(Object)}
     * method
     */
    private static final double EQUALITY_THRESHOLD_DOUBLE = 2e-3;
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
        return UnitConversionUtilityClass.getBase(original);
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

    /**
     * A helper private helper method for the {@link #convert(boolean)} it converts the unit to a metric
     * unit, using the conversion factors from {@link UnitConversionUtilityClass} utility class
     */
    private void convertToMetric() {
        switch (mUnit) {

            case UnitConversionUtilityClass.CUP:
                mValue *= UnitConversionUtilityClass.CUP_TO_MILLILITER;
                mUnit = UnitConversionUtilityClass.MILLI;
                break;
            case UnitConversionUtilityClass.POUND:
                mValue /= UnitConversionUtilityClass.KG_TO_POUND;
                mUnit = UnitConversionUtilityClass.KILO;
                break;
            case UnitConversionUtilityClass.FLOZ:
                mValue *= UnitConversionUtilityClass.FLOZ_TO_MILLILITER;
                mUnit = UnitConversionUtilityClass.MILLI;
                break;
            case UnitConversionUtilityClass.OUNCE:
                mValue *= UnitConversionUtilityClass.OUNCE_TO_GRAM;
                mUnit = UnitConversionUtilityClass.GRAM;
                break;
            case UnitConversionUtilityClass.QUART:
                mValue *= UnitConversionUtilityClass.QUART_TO_LITER;
                mUnit = UnitConversionUtilityClass.LITER;
                break;
            case UnitConversionUtilityClass.PINT:
                mValue *= UnitConversionUtilityClass.PINT_TO_MILLILITER;
                mUnit = UnitConversionUtilityClass.MILLI;
                break;
            case UnitConversionUtilityClass.TSP:
                mValue *= UnitConversionUtilityClass.TEASPOON_TO_MILLILITER;
                mUnit = UnitConversionUtilityClass.MILLI;
                break;
            case UnitConversionUtilityClass.TBSP:
                mValue *= UnitConversionUtilityClass.TABLESPOON_TO_MILLILITER;
                mUnit = UnitConversionUtilityClass.MILLI;
                break;
            default:
                break;
        }
    }

    /**
     * Converts the amount to either metric or US
     * @param toMetric a boolean that indicates if it should be converted to metric or to US
     */
    void convert(boolean toMetric) {

        if (toMetric) {
            convertToMetric();

        } else {
            convertToUS();
        }
        // round to three decimals
        mValue = Math.round(1e3 * mValue) / 1e3;
    }

    /**
     * A helper private helper method for the {@link #convert(boolean)} it converts the unit to a US
     * unit, using the conversion factors from {@link UnitConversionUtilityClass} utility class
     */
    private void convertToUS() {
        switch (mUnit) {
            case UnitConversionUtilityClass.KILO:
                mValue *= UnitConversionUtilityClass.KG_TO_POUND;
                mUnit = UnitConversionUtilityClass.POUND;
                break;
            case UnitConversionUtilityClass.GRAM:
                mValue /= UnitConversionUtilityClass.OUNCE_TO_GRAM;
                mUnit = UnitConversionUtilityClass.OUNCE;
                break;
            case UnitConversionUtilityClass.LITER:
                mValue /= UnitConversionUtilityClass.QUART_TO_LITER;
                mUnit = UnitConversionUtilityClass.QUART;
                break;
            case UnitConversionUtilityClass.MILLI:
                changeMilliliter();

                break;
            case UnitConversionUtilityClass.DECI:
                mValue /= UnitConversionUtilityClass.TABLESPOON_TO_MILLILITER * METRIC_CONSTANT;
                mUnit = UnitConversionUtilityClass.TBSP;
                break;
            default:
                break;
        }
    }

    private void changeMilliliter() {
        if (mValue >= UnitConversionUtilityClass.CUP_TO_MILLILITER) {
            mValue /= UnitConversionUtilityClass.CUP_TO_MILLILITER;
            mUnit = UnitConversionUtilityClass.CUP;

        } else if (mValue >= UnitConversionUtilityClass.FLOZ_TO_MILLILITER) {
            mValue /= UnitConversionUtilityClass.FLOZ_TO_MILLILITER;
            mUnit = UnitConversionUtilityClass.FLOZ;

        } else if (mValue >= UnitConversionUtilityClass.TABLESPOON_TO_MILLILITER) {
            mValue /= UnitConversionUtilityClass.TABLESPOON_TO_MILLILITER;
            mUnit = UnitConversionUtilityClass.TBSP;
        }

        mValue /= UnitConversionUtilityClass.TEASPOON_TO_MILLILITER;
        mUnit = UnitConversionUtilityClass.TSP;
    }


}
