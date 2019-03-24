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

    private String mOriginalLine;

    public ListIngredient(String name, String unit, double value, String originalText,
                          Map<PositionKey, Position> positions) {
        super(name, unit, value, positions);
        mOriginalLine = originalText;

        // check if the positions are legal
        for (PositionKey key : PositionKey.values()) {
            Position position = positions.get(key);

            if (position == null) {
                throw new IllegalArgumentException("Position of " + key + " cannot be null");
            }
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
     * @return
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
        // replace " . " by "" and trim the string
        return bld.toString().replace(" . ", "").trim();

    }

    private boolean unitDetected() {
        boolean stringSet = !("").equals(mAmount.getUnit());
        boolean positionSpansEntireLine = getUnitPosition().getBeginIndex() == 0 &&
                getUnitPosition().getEndIndex() == mOriginalLine.length();
        return stringSet && !positionSpansEntireLine;
    }

    private boolean quantityDetected() {
        return !(getQuantityPosition().getBeginIndex() == 0 &&
                getQuantityPosition().getEndIndex() == mOriginalLine.length());
    }

}
