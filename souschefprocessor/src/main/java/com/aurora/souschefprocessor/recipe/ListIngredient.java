package com.aurora.souschefprocessor.recipe;

import java.util.HashMap;
import java.util.List;
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
            if (!position.isLegalInString(originalText)) {
                throw new IllegalArgumentException("Position of " + key + " is too big");
            }
        }
    }

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
        boolean stringSet = !("").equals(mAmount.getUnit());
        boolean positionSpansEntireLine = getUnitPosition().getBeginIndex() == 0 &&
                getUnitPosition().getEndIndex() == mOriginalLine.length();
        return stringSet && !positionSpansEntireLine;
    }

    /**
     * A function that indicates whether this listingredient contains a quantity detected in the string
     *
     * @return a boolean that indicates if a quantity was detected
     */
    private boolean quantityDetected() {
        return !(getQuantityPosition().getBeginIndex() == 0 &&
                getQuantityPosition().getEndIndex() == mOriginalLine.length());
    }

    /**
     * Converts this ingredient to metric or US. This also changes the {@link #mOriginalLine}
     * field and the positions so that the converted ingredient can be shown to the UI
     * @param toMetric a boolean to indicate wheter to convert to metric or to US
     */
    public void convertUnit(boolean toMetric) {

        // only convert if the unit and quantity are detected
        if (!unitDetected() || !quantityDetected()) {
            return;
        }
        // convert the amount
        mAmount.convert(toMetric);

        // a map that matches the UNIT and QUANTITY to their converted value
        Map<PositionKeysForIngredients, String> converted = new HashMap<>();
        converted.put(PositionKeysForIngredients.UNIT, mAmount.getUnit());
        converted.put(PositionKeysForIngredients.QUANTITY, "" + mAmount.getValue());

        // get the order of the NAME UNIT and QUANTITY
        List<PositionKeysForIngredients> order = getOrderOfPositions();

        // the offset for changing the positions
        int offset = 0;

        for (int i = 0; i < order.size(); i++) {
            PositionKeysForIngredients key = order.get(i);

            Position originalPos = mPositions.get(key);
            // change the indices so that the positions will be correct
            int newBegin = originalPos.getBeginIndex() + offset;
            int newEnd = originalPos.getEndIndex() + offset;

            if (!key.equals(PositionKeysForIngredients.NAME)) {

                // change the line
                mOriginalLine =  mOriginalLine.substring(0, newBegin) +
                        converted.get(key) + mOriginalLine.substring(newEnd);

                // calculate the new end
                newEnd = newBegin + converted.get(key).length();
                // update the offset
                offset = newEnd - originalPos.getEndIndex();


            }
            originalPos.setIndices(newBegin, newEnd);
        }
    }


}
