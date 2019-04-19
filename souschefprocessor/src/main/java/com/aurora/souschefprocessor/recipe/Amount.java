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
     * A constant for half
     */
    private static final double HALF = 0.5;

    /**
     * A constant for 1 quarter
     */
    private static final double QUARTER = 0.25;

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

    /**
     * A private function that checks if two doubles are equal using the {@link #EQUALITY_THRESHOLD_DOUBLE}
     * value
     *
     * @param a the first double
     * @param b the second double
     * @return a boolean that indicates if the absolute difference of the two doubles is smaller than
     * the threshold
     */
    private static boolean doublesEqual(double a, double b) {
        return Math.abs(a - b) < EQUALITY_THRESHOLD_DOUBLE;
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
                    doublesEqual(a.getValue(), mValue));

        }
        return false;
    }

    @Override
    public String toString() {
        return "QUANTITY " + mValue + " UNIT " + mUnit;
    }

    /**
     * A helper private helper method for the {@link #convert(boolean)} it converts the unit to a metric
     * unit, using the conversion factors from {@link UnitConversionUtils} utility class
     */
    private void convertToMetric() {
        switch (mUnit) {
            case UnitConversionUtils.CUP:
                mValue *= UnitConversionUtils.CUP_TO_MILLILITER;
                mUnit = UnitConversionUtils.MILLI;
                break;
            case UnitConversionUtils.POUND:
                mValue /= UnitConversionUtils.KG_TO_POUND;
                mUnit = UnitConversionUtils.KILO;
                break;
            case UnitConversionUtils.FLOZ:
                mValue *= UnitConversionUtils.FLOZ_TO_MILLILITER;
                mUnit = UnitConversionUtils.MILLI;
                break;
            case UnitConversionUtils.OUNCE:
                mValue *= UnitConversionUtils.OUNCE_TO_GRAM;
                mUnit = UnitConversionUtils.GRAM;
                break;
            case UnitConversionUtils.QUART:
                mValue *= UnitConversionUtils.QUART_TO_LITER;
                mUnit = UnitConversionUtils.LITER;
                break;
            case UnitConversionUtils.PINT:
                mValue *= UnitConversionUtils.PINT_TO_MILLILITER;
                mUnit = UnitConversionUtils.MILLI;
                break;
            case UnitConversionUtils.TSP:
                mValue *= UnitConversionUtils.TEASPOON_TO_MILLILITER;
                mUnit = UnitConversionUtils.MILLI;
                break;
            case UnitConversionUtils.TBSP:
                mValue *= UnitConversionUtils.TABLESPOON_TO_MILLILITER;
                mUnit = UnitConversionUtils.MILLI;
                break;
            default:
                break;
        }
    }

    /**
     * Converts the amount to either metric or US
     *
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
     * unit, using the conversion factors from {@link UnitConversionUtils} utility class
     */
    private void convertToUS() {
        switch (mUnit) {
            case UnitConversionUtils.KILO:
                mValue *= UnitConversionUtils.KG_TO_POUND;
                mUnit = UnitConversionUtils.POUND;
                break;
            case UnitConversionUtils.GRAM:
                mValue /= UnitConversionUtils.OUNCE_TO_GRAM;
                mUnit = UnitConversionUtils.OUNCE;
                break;
            case UnitConversionUtils.LITER:
                mValue /= UnitConversionUtils.QUART_TO_LITER;
                mUnit = UnitConversionUtils.QUART;
                break;
            case UnitConversionUtils.MILLI:
                changeMilliliter();
                break;
            case UnitConversionUtils.DECI:
                mValue /= UnitConversionUtils.TABLESPOON_TO_MILLILITER * METRIC_CONSTANT;
                mUnit = UnitConversionUtils.TBSP;
                break;
            default:
                break;
        }
    }

    /**
     * A helper function (for {@link #convertToUS()} for the conversion of milliliters,
     * since this has many conversions in US
     */
    private void changeMilliliter() {
        if (convertWithThisConversionFactor(UnitConversionUtils.CUP_TO_MILLILITER)) {
            mValue /= UnitConversionUtils.CUP_TO_MILLILITER;
            mUnit = UnitConversionUtils.CUP;
            return;

        } else if (convertWithThisConversionFactor(UnitConversionUtils.FLOZ_TO_MILLILITER)) {
            mValue /= UnitConversionUtils.FLOZ_TO_MILLILITER;
            mUnit = UnitConversionUtils.FLOZ;
            return;

        } else if (convertWithThisConversionFactor(UnitConversionUtils.TABLESPOON_TO_MILLILITER)) {
            mValue /= UnitConversionUtils.TABLESPOON_TO_MILLILITER;
            mUnit = UnitConversionUtils.TBSP;
            return;
        }

        mValue /= UnitConversionUtils.TEASPOON_TO_MILLILITER;
        mUnit = UnitConversionUtils.TSP;
    }

    /**
     * A private helperfunction for {@link #changeMilliliter()} that indicates if this factor could be used for the conversion.
     * This is the case when the {@link #mValue} is bigger than the factor or is
     * equal to the factor {@link #doublesEqual(double, double)}.
     *
     * @param conversionFactor the factor to check
     * @return a boolean to indiciate wheter to convert with this factor
     */
    private boolean convertWithThisConversionFactor(double conversionFactor) {

        if (mValue >= conversionFactor) {
            return true;
        }

        if (doublesEqual(mValue, conversionFactor * QUARTER)) {
            return true;
        }
        return doublesEqual(mValue, conversionFactor * HALF);
    }
}
