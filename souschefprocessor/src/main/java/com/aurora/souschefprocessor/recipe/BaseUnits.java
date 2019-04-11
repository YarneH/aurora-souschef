package com.aurora.souschefprocessor.recipe;

import java.util.Locale;

public class BaseUnits {
    private static String[] baseUnitsMetric = {"kilogram", "gram", "milliliter", "liter",
            "deciliter"};
    private static String[] baseUnitsUS =
            {"tablespoon", "cup", "pound", "fluid ounce", "ounce", "quart", "pint", "teaspoon" };
    private static String[] pluralsMetric =
            {"kilograms", "grams", "milliliters", "liters", "deciliters"};
    private static String[] pluralsUS =
            {"tablespoons", "cups", "pounds", "fluid ounces", "ounces", "quarts", "pints", "teaspoons"};
    private static String[] abbreviationsMetric = {"kg", "g", "ml", "l", "dl"};
    private static String[] abbreviationsUS =
            {"tbsp","c", "lb", "fl oz", "oz", "qt", "pt", "tsp"};



    public static String getBase(String original) {

        String lowerCase = original.toLowerCase(Locale.ENGLISH);
        // first plurals
        for (int i = 0; i < baseUnitsMetric.length; i++) {
            if (pluralsMetric[i].equals(lowerCase)) {
                return baseUnitsMetric[i];
            }
            if (abbreviationsMetric[i].equals(lowerCase)) {
                return baseUnitsMetric[i];
            }
        }
        for (int i = 0; i < baseUnitsUS.length; i++) {
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
