package com.aurora.souschefprocessor.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * A map that maps the base units to other representations
     */
    private static final Map<String, List<String>> BASE_UNITS_TO_OTHER_REPRESENTATIONS = new HashMap<>();

    // populate the map
    static {
        // add the US units
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(TBSP, Arrays.asList("tbsp", "tablespoons", "T"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(CUP, Arrays.asList("c", "cups"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(TSP, Arrays.asList("tsp", "teaspoons", "t"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(POUND, Arrays.asList("lb", "pounds"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(FLOZ, Arrays.asList("fl oz", "fluid ounces"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(OUNCE, Arrays.asList("oz", "ounces"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(QUART, Arrays.asList("qt", "quarts"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(PINT, Arrays.asList("pt", "pints"));
        // add the metric units
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(KILO, Arrays.asList("kg", "kilograms"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(GRAM, Arrays.asList("grams", "g"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(MILLI, Arrays.asList("ml", "milliliters"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(LITER, Arrays.asList("l", "liters"));
        BASE_UNITS_TO_OTHER_REPRESENTATIONS.put(DECI, Arrays.asList("dl", "deciliters"));
    }

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

        String trim = original.trim();
        String lowerCase = trim.toLowerCase();


        for (String base : BASE_UNITS_US) {
            List<String> others = BASE_UNITS_TO_OTHER_REPRESENTATIONS.get(base);
            if (others != null && (others.contains(trim) || others.contains(lowerCase))) {
                return base;
            }
        }

        for (String base : BASE_UNITS_METRIC) {
            List<String> others = BASE_UNITS_TO_OTHER_REPRESENTATIONS.get(base);
            if (others != null && (others.contains(trim) || others.contains(lowerCase))) {
                return base;
            }
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
        for (String base : BASE_UNITS_METRIC) {
            list.addAll(BASE_UNITS_TO_OTHER_REPRESENTATIONS.get(base));
        }
        for (String base : BASE_UNITS_US) {
            list.addAll(BASE_UNITS_TO_OTHER_REPRESENTATIONS.get(base));
        }
        return list;

    }
}
