package com.aurora.souschefprocessor.recipe;

import java.util.Map;

/**
 * An extension of the class Ingredient. This is an ingredient that is described in the ingredient-
 * list of the recipe.
 * It has one extra field:
 * mOriginalLine: the line that was in the original text listing this ingredient. This line is used
 * to check whether the positions are legal in this line.
 */
public class ListIngredient extends Ingredient {
    /**
     * The originalLine this ListIngredient was detected in
     */
    private String mOriginalLine;

    public ListIngredient(String name, String unit, double value, String originalText,
                          Map<PositionKeysForIngredients, Position> positions) {
        super(name, unit, value, positions);
        mOriginalLine = originalText;

        // check if the positions are legal
        for (PositionKeysForIngredients key : PositionKeysForIngredients.values()) {
            Position position = positions.get(key);
            if (position == null) {
                throw new IllegalArgumentException("Position of " + key + " is null!");
            }
            if (!position.isLegalInString(originalText)) {
                throw new IllegalArgumentException("Position of " + key + " is too big");
            }
        }
    }

    /**
     * Standard getter
     *
     * @return the original line this listIngredient was detected
     */
    public String getOriginalLine() {
        return mOriginalLine;
    }

    /**
     * This function returns the original line where the unit and quantity are omitted, this can
     * be used to display the string when quantity and unit are changed.
     *
     * @return The original string but without the text describing the unit and quantity
     */
    public String getOriginalLineWithoutUnitAndQuantity() {
        StringBuilder bld = new StringBuilder();
        Position q = getQuantityPosition();
        Position u = getUnitPosition();
        boolean unit = unitDetected();
        boolean quantity = quantityDetected();
        if (unit && quantity) {
            // Both unit and quantity were detected, omit them both from the string
            if (q.getEndIndex() <= u.getBeginIndex()) {
                // the quantity is placed in the string before the unit
                bld.append(mOriginalLine.substring(0, q.getBeginIndex()));
                bld.append(mOriginalLine.substring(q.getEndIndex(), u.getBeginIndex()));
                bld.append(mOriginalLine.substring(u.getEndIndex()));

            } else {
                // the unit is placed in the string before the quantity
                bld.append(mOriginalLine.substring(0, u.getBeginIndex()));
                bld.append(mOriginalLine.substring(u.getEndIndex(), q.getBeginIndex()));
                bld.append(mOriginalLine.substring(q.getEndIndex()));
            }

        } else if (quantity) {
            // only quantity was detected
            bld.append(mOriginalLine.substring(0, q.getBeginIndex()));
            bld.append(mOriginalLine.substring(q.getEndIndex()));

        } else if (unit) {
            // only unit was detected
            bld.append(mOriginalLine.substring(0, u.getBeginIndex()));
            bld.append(mOriginalLine.substring(u.getEndIndex()));

        } else {
            // No unit and quantity detected just return original string
            return mOriginalLine;
        }
        // capitalize the first character
        bld.setCharAt(0, Character.toUpperCase(bld.charAt(0)));
        // replace " . " by "" and trim the string
        return bld.toString().replace(" . ", "").trim();
    }

    /**
     * A function that indicates whether this listingredient contains a unit detected in the string
     *
     * @return a boolean that indicates if a unit was detected
     */
    private boolean unitDetected() {
        return unitDetected(mOriginalLine);
    }

    /**
     * A function that indicates whether this listingredient contains a quantity detected in the string
     *
     * @return a boolean that indicates if a quantity was detected
     */
    private boolean quantityDetected() {
        return quantityDetected(mOriginalLine);
    }

    /**
     * Converts this ingredient to metric or US. This also changes the {@link #mOriginalLine}
     * field and the positions so that the converted ingredient can be shown to the UI
     *
     * @param toMetric a boolean to indicate wheter to convert to metric or to US
     */
    void convertUnit(boolean toMetric) {
        mOriginalLine = super.convertUnit(toMetric, mOriginalLine);
    }

}
