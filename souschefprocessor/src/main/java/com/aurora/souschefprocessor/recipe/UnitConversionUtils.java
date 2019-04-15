package com.aurora.souschefprocessor.recipe;

import java.util.Locale;

/**
 * A utility class for conversion of units. The plurals and abreviations of metric and US units are matched to their base
 * It also contains the conversion constants between the base units from metric and US
 */
public class UnitConversionUtils {

    /**
     * Conversion constant: x milliliter in 1 cup
     */
    static final int CUP_TO_MILLILITER = 240;
    /**
     * Conversion constant: x pound in 1 kilogram
     */
    static final double KG_TO_POUND = 2.205;
    /**
     * Conversion constant: x milliliter in 1 fluid ounce
     */
    static final double FLOZ_TO_MILLILITER = 29.5735;
    /**
     * Conversion constant: x gram in 1 ounce
     */
    static final double OUNCE_TO_GRAM = 28.3495;
    /**
     * Conversion constant: x milliliter in 1 teaspoon
     */
    static final double TEASPOON_TO_MILLILITER = 4.92892;
    /**
     * Conversion constant: x milliliter in 1 tablespoon
     */
    static final double TABLESPOON_TO_MILLILITER = 14.7868;
    /**
     * Conversion constant: x liter in 1 quart
     */
    static final double QUART_TO_LITER = 0.946353;
    /**
     * Conversion constant: x milliliter in 1 pint
     */
    static final double PINT_TO_MILLILITER = 473.176;


    /**
     * The milliliter baseUnit
     */
    static final String MILLI = "milliliter";
    /**
     * The deciliter baseUnit
     */
    static final String DECI = "deciliter";
    /**
     * The gram baseUnit
     */
    static final String GRAM = "gram";
    /**
     * The kilogram baseUnit
     */
    static final String KILO = "kilogram";
    /**
     * The liter baseUnit
     */
    static final String LITER = "liter";

    /**
     * The tablespoon baseUnit
     */
    static final String TBSP = "tablespoon";
    /**
     * The cup baseUnit
     */
    static final String CUP = "cup";
    /**
     * The pound baseUnit
     */
    static final String POUND = "pound";
    /**
     * The fluid ounce baseUnit
     */
    static final String FLOZ = "fluid ounce";
    /**
     * The ounce baseUnit
     */
    static final String OUNCE = "ounce";
    /**
     * The quart baseUnit
     */
    static final String QUART = "quart";
    /**
     * The pint baseUnit
     */
    static final String PINT = "pint";
    /**
     * The teaspoon baseUnit
     */
    static final String TSP = "teaspoon";


    /**
     * An array containing the metric base units
     */
    private static final String[] BASE_UNITS_METRIC = {KILO, GRAM, MILLI, LITER, DECI};
    /**
     * An array containing the US base units
     */
    private static final String[] BASE_UNITS_US =
            {TBSP, CUP, POUND, FLOZ, OUNCE, QUART, PINT, TSP};
    /**
     * An array containing the metric plurals of the base units
     */
    private static final String[] PLURALS_METRIC =
            {"kilograms", "grams", "milliliters", "liters", "deciliters"};
    /**
     * An array containing the US plurals of the base units
     */
    private static final String[] PLURALS_US =
            {"tablespoons", "cups", "pounds", "fluid ounces", "ounces", "quarts", "pints", "teaspoons"};
    /**
     * An array containing the metric abbreviations of the base units
     */
    private static final String[] ABBREVIATIONS_METRIC = {"kg", "g", "ml", "l", "dl"};
    /**
     * An array containing the US abbreviations of the base units
     */
    private static final String[] ABBREVIATIONS_US =
            {"tbsp", "c", "lb", "fl oz", "oz", "qt", "pt", "tsp"};


    private UnitConversionUtils() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Finds the base unit of the string that was passed. This matches plurals and abbreviations to a base
     *
     * @param original the string to get the base of
     * @return the base of the string, if no base was found it just retuns the orignal
     */
    public static String getBase(String original) {

        String lowerCase = original.toLowerCase(Locale.ENGLISH);
        String base = getBaseMetric(lowerCase);
        // base is found, return base
        if (base != null) {
            return base;
        }
        // if base was not found via metric
        base = getBaseUS(lowerCase);
        if (base != null) {
            return base;
        }

        // nothing found -> return the original
        return original;
    }

    /**
     * A private helperfunction for {@link #getBase(String)}. It tries to find the base unit as a metric unit
     * It expects is input to be in lowerCase
     *
     * @param lowerCase the lowercase string to get the metric base of
     * @return the base of the string, if no base was found it returns null
     */
    private static String getBaseMetric(String lowerCase) {
        for (int i = 0; i < BASE_UNITS_METRIC.length; i++) {
            if (PLURALS_METRIC[i].equals(lowerCase)) {
                return BASE_UNITS_METRIC[i];
            }
            if (ABBREVIATIONS_METRIC[i].equals(lowerCase)) {
                return BASE_UNITS_METRIC[i];
            }
        }
        return null;
    }


    /**
     * A private helperfunction for {@link #getBase(String)}. It tries to find the base unit as a US unit.
     * It expects is input to be in lowerCase.
     *
     * @param lowerCase the lowercase string to get the US base of
     * @return the base of the string, if no base was found it returns null
     */
    private static String getBaseUS(String lowerCase) {
        for (int i = 0; i < BASE_UNITS_US.length; i++) {
            if (PLURALS_US[i].equals(lowerCase)) {
                return BASE_UNITS_US[i];
            }

            if (ABBREVIATIONS_US[i].equals(lowerCase)) {
                return BASE_UNITS_US[i];
            }
        }
        return null;
    }

}
