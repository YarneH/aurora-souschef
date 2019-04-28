package com.aurora.souschefprocessor.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A utility class for conversion of units. The plurals and abreviations of metric and US units are
 * matched to their base
 * It also contains the conversion constants between the base units from metric and US
 */
public final class UnitConversionUtils {
    /**
     * Conversion constant: x milliliter in 1 cup
     */
    public static final int CUP_TO_MILLILITER = 240;

    /**
     * Conversion constant: x pound in 1 kilogram
     */
    public static final double KG_TO_POUND = 2.205;

    /**
     * Conversion constant: x milliliter in 1 fluid ounce
     */
    public static final double FLOZ_TO_MILLILITER = 29.5735;

    /**
     * Conversion constant: x gram in 1 ounce
     */
    public static final double OUNCE_TO_GRAM = 28.3495;

    /**
     * Conversion constant: x milliliter in 1 teaspoon
     */
    public static final double TEASPOON_TO_MILLILITER = 4.92892;

    /**
     * Conversion constant: x milliliter in 1 tablespoon
     */
    public static final double TABLESPOON_TO_MILLILITER = 14.7868;

    /**
     * Conversion constant: x liter in 1 quart
     */
    public static final double QUART_TO_LITER = 0.946353;
    /**
     * Conversion constant: x milliliter in 1 pint
     */
    public static final double PINT_TO_MILLILITER = 473.176;

    /**
     * The milliliter baseUnit
     */
    public static final String MILLI = "milliliter";

    /**
     * The deciliter baseUnit
     */
    public static final String DECI = "deciliter";

    /**
     * The gram baseUnit
     */
    public static final String GRAM = "gram";

    /**
     * The kilogram baseUnit
     */
    public static final String KILO = "kilogram";

    /**
     * The liter baseUnit
     */
    public static final String LITER = "liter";

    /**
     * The tablespoon baseUnit
     */
    public static final String TBSP = "tablespoon";

    /**
     * The cup baseUnit
     */
    public static final String CUP = "cup";

    /**
     * The pound baseUnit
     */
    public static final String POUND = "pound";

    /**
     * The fluid ounce baseUnit
     */
    public static final String FLOZ = "fluid ounce";

    /**
     * The ounce baseUnit
     */
    public static final String OUNCE = "ounce";

    /**
     * The quart baseUnit
     */
    public static final String QUART = "quart";

    /**
     * The pint baseUnit
     */
    public static final String PINT = "pint";

    /**
     * The teaspoon baseUnit
     */
    public static final String TSP = "teaspoon";

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

        String lowerCase = original.toLowerCase(Locale.ENGLISH).trim();

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
     * @return A list of common units made up of all the baseunits and their plurals and abbreviations
     */
    public static List<String> getCommonUnits() {
        List<String> list = new ArrayList<>(Arrays.asList(BASE_UNITS_METRIC));
        list.addAll(Arrays.asList(BASE_UNITS_US));
        list.addAll(Arrays.asList(ABBREVIATIONS_METRIC));
        list.addAll(Arrays.asList(ABBREVIATIONS_US));
        list.addAll(Arrays.asList(PLURALS_US));
        list.addAll(Arrays.asList(PLURALS_METRIC));
        return list;

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
