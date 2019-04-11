package com.aurora.souschefprocessor.recipe;

import java.util.Locale;

public class BaseUnits {
    private static String[] baseUnitsMetric = {"kilogram", "gram", "milliliter", "liter",
            "deciliter"};
    private static String[] baseUnitsUS = {"cup", "pound", "fluid ounce", "ounce", "quart",
            "pint", "teaspoon", "tablespoon"};
    private static String[] pluralsMetric = {"kilograms", "grams", "milliliters", "liters",
            "deciliters"};
    private static String[] pluralsUS = {"cups", "pounds", "fluid ounces", "ounces", "quarts",
            "pints", "teaspoons", "tablespoons"};
    private static String[] abbreviationsMetric = {"kg", "g", "ml", "l", "dl"};
    private static String[] abbreviationsUS = {"c", "lb", "fl oz", "oz", "qt", "pt", "tsp",
            "tbsp"};

    public static String[] getBaseUnitsMetric() {
        return baseUnitsMetric;
    }

    public static String[] getBaseUnitsUS() {
        return baseUnitsUS;
    }

    public static String[] getPluralsMetric() {
        return pluralsMetric;
    }

    public static String[] getPluralsUS() {
        return pluralsUS;
    }

    public static String[] getAbbreviationsMetric() {
        return abbreviationsMetric;
    }

    public static String[] getAbbreviationsUS() {
        return abbreviationsUS;
    }

    public static String getBase(String original) {
        int number = baseUnitsMetric.length;
        String lowerCase = original.toLowerCase(Locale.ENGLISH);
        // first plurals
        for (int i = 0; i < number; i++) {
            if (pluralsMetric[i].equals(lowerCase)) {
                return baseUnitsMetric[i];
            }
            if (abbreviationsMetric[i].equals(lowerCase)) {
                return baseUnitsMetric[i];
            }
        }
        for (int i = 0; i < number; i++) {
            if (pluralsUS[i].equals(lowerCase)) {
                return baseUnitsUS[i];
            }
            if (abbreviationsUS[i].equals(lowerCase)) {
                return baseUnitsUS[i];
            }
        }
        // nothing found -> return the original
        return original;
    }

}
