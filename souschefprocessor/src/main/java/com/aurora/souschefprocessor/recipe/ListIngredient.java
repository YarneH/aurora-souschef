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


}
