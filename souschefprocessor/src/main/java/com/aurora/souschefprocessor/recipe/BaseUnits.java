package com.aurora.souschefprocessor.recipe;

import java.util.Locale;

public class BaseUnits {
    private static String[] sBaseUnitsMetric = {"kilogram", "gram", "milliliter", "liter",
            "deciliter"};
    private static String[] sBaseUnitsUS =
            {"tablespoon", "cup", "pound", "fluid ounce", "ounce", "quart", "pint", "teaspoon"};
    private static String[] sPluralsMetric =
            {"kilograms", "grams", "milliliters", "liters", "deciliters"};
    private static String[] sPluralsUS =
            {"tablespoons", "cups", "pounds", "fluid ounces", "ounces", "quarts", "pints", "teaspoons"};
    private static String[] sAbbreviationsMetric = {"kg", "g", "ml", "l", "dl"};
    private static String[] sAbbreviationsUS =
            {"tbsp", "c", "lb", "fl oz", "oz", "qt", "pt", "tsp"};


    private BaseUnits() {
        throw new IllegalStateException("Utility class");
    }


    public static String getBase(String original) {

        String lowerCase = original.toLowerCase(Locale.ENGLISH);
        String base = getBaseMetric(lowerCase);
        if (base != null) {
            return base;
        }
        base = getBaseUS(lowerCase);
        if (base != null) {
            return base;
        }

        // nothing found -> return the original
        return original;
    }

    private static String getBaseMetric(String lowerCase) {
        for (int i = 0; i < sBaseUnitsMetric.length; i++) {
            if (sPluralsMetric[i].equals(lowerCase)) {
                return sBaseUnitsMetric[i];
            }
            if (sAbbreviationsMetric[i].equals(lowerCase)) {
                return sBaseUnitsMetric[i];
            }
        }
        return null;
    }

    private static String getBaseUS(String lowerCase) {
        for (int i = 0; i < sBaseUnitsUS.length; i++) {
            if (sPluralsUS[i].equals(lowerCase)) {
                return sBaseUnitsUS[i];
            }
            if (sAbbreviationsUS[i].equals(lowerCase)) {
                return sBaseUnitsUS[i];
            }
        }
        return null;
    }

}
