package com.aurora.souschefprocessor.recipe;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A data class that represents an mName in the mName list of the recipe or a step of the
 * recipe. The class has three fields:
 * mName: which is a mDescription of the mName (e.g. sugar, tomato)
 * mAmount: the mAmount of the unit (e.g. 500)
 * mPositions: A map that maps the keys: UNIT, NAME, QUANTITY to their positions, for an ingredient
 * in a step this is the position in the description of the step, for an ingredient in
 * the list of ingredients this is the position in the string describing this ingredient
 * in the list. If one of these three is not detected, its position is set from 0 to the length of
 * the description
 */
public class Ingredient {
    /**
     * The name of this ingredient
     */
    private String mName;

    /**
     * The {@link Amount} that has as value quantity and as unit the unit of this ingredient
     */
    private Amount mAmount;

    /**
     * A map with the {@link Position}s of te name, unit and value of this ingredient in the string
     * they were classified. Each of these Positions cannot be null.
     */
    private Map<PositionKeysForIngredients, Position> mPositions;


    public Ingredient(String name, String unit, double value, Map<PositionKeysForIngredients, Position> positions) {
        this.mName = name;
        this.mAmount = new Amount(value, unit);

        //Check if the positions are not null
        for (PositionKeysForIngredients key : PositionKeysForIngredients.values()) {
            Position position = positions.get(key);
            if (position == null) {
                throw new IllegalArgumentException("Position of " + key + " cannot be null");
            }
        }

        this.mPositions = positions;
    }

    /**
     * Default getter
     *
     * @return the unit of this amount
     */
    public String getUnit() {
        return mAmount.getUnit();
    }

    /**
     * Default setter
     *
     * @param unit the new unit of this ingredient
     */
    public void setUnit(String unit) {
        mAmount.setUnit(unit);
    }

    /**
     * Default getter
     *
     * @return the quantity of this ingredient
     */
    public double getQuantity() {
        return mAmount.getValue();
    }

    /**
     * Default setter
     *
     * @param quantity the quantity of this ingredient
     */
    public void setQuantity(double quantity) {
        mAmount.setValue(quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mAmount, mName);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            Ingredient ingredient = (Ingredient) o;
            return (ingredient.getAmount().equals(mAmount) && ingredient.getName().equalsIgnoreCase(mName));
        }
        return false;
    }

    /**
     * Default getter
     *
     * @return the amount of this ingredient (has both quantity and unit)
     */
    public Amount getAmount() {
        return mAmount;
    }

    /**
     * Default getter
     *
     * @return the name of this ingredient
     */
    public String getName() {
        return mName;
    }

    /**
     * Default setter
     *
     * @param name the new name of this ingredient
     */
    public void setName(String name) {
        this.mName = name;
    }

    @Override
    public String toString() {
        String res = mAmount + " NAME ";
        return res + mName;
    }

    /**
     * This function checks if the positions of the NAME, UNIT and AMOUNT of this object are legal
     * in a string. Legal means: beginIndex at the most the length of the string minus 1 (last character)
     * and the endindex at most the length of the string (character after last character)
     *
     * @param string The string in which to check that the positions are legal
     * @return a boolean indicating if the positions are legal
     */
    boolean arePositionsLegalInString(String string) {
        for (PositionKeysForIngredients key : PositionKeysForIngredients.values()) {
            Position position = mPositions.get(key);
            if (position == null) {
                throw new IllegalArgumentException("Position of " + key + " is null!");
            }
            if (!position.isLegalInString(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This trims the positions of this ingredient to the passed string. It uses the
     * {@link Position#trimToLengthOfString(String)}
     * method. This ensures that the endindex of the positions is never bigger than the length of the passed string
     *
     * @param s the string to trim the positions to
     */
    public void trimPositionsToString(String s) {
        for (PositionKeysForIngredients key : PositionKeysForIngredients.values()) {
            Position position = mPositions.get(key);
            if (position != null) {
                position.trimToLengthOfString(s);
            }
        }
    }

    /**
     * Converts the units of this ingredient (which is located in a given description) to metric or to US imperial
     *
     * @param toMetric    a boolean; if true the ingredient will be converted to metric, if false to US imperial system
     * @param description the description where this ingredient is located in
     * @return the new description with converted units
     */
    String convertUnit(boolean toMetric, String description) {

        // only convert if the unit and quantity are detected
        if (!unitDetected(description) || !quantityDetected(description)) {
            return description;
        }
        // convert the amount
        mAmount.convert(toMetric);

        // a map that matches the UNIT and QUANTITY to their converted value
        Map<PositionKeysForIngredients, String> converted =
                new EnumMap<>(PositionKeysForIngredients.class);
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

            if (key != PositionKeysForIngredients.NAME) {

                // change the line
                description = description.substring(0, newBegin) +
                        converted.get(key) + description.substring(newEnd);

                // calculate the new end
                newEnd = newBegin + converted.get(key).length();
                // update the offset
                offset = newEnd - originalPos.getEndIndex();
            }
            originalPos.setIndices(newBegin, newEnd);
        }

        return description;
    }

    /**
     * A function that indicates whether this step contains a unit detected in the string
     *
     * @param description the description in which this ingredient was detected
     * @return a boolean that indicates if a unit was detected
     */
    boolean unitDetected(String description) {
        boolean stringSet = !("").equals(mAmount.getUnit());
        boolean positionSpansEntireLine = getUnitPosition().getBeginIndex() == 0 &&
                getUnitPosition().getEndIndex() == description.length();
        return stringSet && !positionSpansEntireLine;
    }

    /**
     * A function that indicates whether this ingredient contains a quantity detected in the string
     *
     * @param description the description in which this ingredient was detected
     * @return a boolean that indicates if a quantity was detected
     */
    boolean quantityDetected(String description) {
        return !(getQuantityPosition().getBeginIndex() == 0 &&
                getQuantityPosition().getEndIndex() == description.length());
    }

    /**
     * Gets the order of the positions, it checks in which order the QUANTITY UNIT and name are mentioned
     *
     * @return a list, where the first element is the element that is stated first in the sentence, in case of
     * ex aequo the list is ordered with the following priority: 1 QUANTITY, 2 UNIT, 3 NAME
     */
    private List<PositionKeysForIngredients> getOrderOfPositions() {

        int qEnd = getQuantityPosition().getEndIndex();

        int uEnd = getUnitPosition().getEndIndex();

        int nEnd = getNamePosition().getEndIndex();
        List<PositionKeysForIngredients> list = new ArrayList<>();
        if (qEnd < nEnd && qEnd < uEnd) {
            list.add(PositionKeysForIngredients.QUANTITY);
            if (uEnd < nEnd) {
                list.add(PositionKeysForIngredients.UNIT);
                list.add(PositionKeysForIngredients.NAME);
            } else {
                list.add(PositionKeysForIngredients.NAME);
                list.add(PositionKeysForIngredients.UNIT);

            }
        } else if (uEnd < qEnd && uEnd < nEnd) {
            list.add(PositionKeysForIngredients.UNIT);
            if (qEnd < nEnd) {
                list.add(PositionKeysForIngredients.QUANTITY);
                list.add(PositionKeysForIngredients.NAME);
            } else {
                list.add(PositionKeysForIngredients.NAME);
                list.add(PositionKeysForIngredients.QUANTITY);
            }

        } else {
            list.add(PositionKeysForIngredients.NAME);
            if (qEnd < uEnd) {
                list.add(PositionKeysForIngredients.QUANTITY);
                list.add(PositionKeysForIngredients.UNIT);
            } else {
                list.add(PositionKeysForIngredients.NAME);
                list.add(PositionKeysForIngredients.UNIT);
            }
        }
        return list;
    }

    /**
     * Gets the unit position of this ingredient
     * @return the unit of this ingredient
     */
    public Position getUnitPosition() {
        return mPositions.get(PositionKeysForIngredients.UNIT);
    }

    /**
     * Gets the quantity position of this ingredient
     * @return the quantity of this ingredient
     */
    public Position getQuantityPosition() {
        return mPositions.get(PositionKeysForIngredients.QUANTITY);
    }

    /**
     * Gets the name position of this ingredient
     * @return the name of this ingredient
     */
    public Position getNamePosition() {
        return mPositions.get(PositionKeysForIngredients.NAME);
    }

    /**
     * Sets the position of the name
     *
     * @param namePosition the new name position
     */
    public void setNamePosition(Position namePosition) {
        mPositions.put(PositionKeysForIngredients.NAME, namePosition);
    }

    /**
     * Sets the position of the quantity
     *
     * @param quantityPosition the new quantity position
     */
    public void setQuantityPosition(Position quantityPosition) {
        mPositions.put(PositionKeysForIngredients.QUANTITY, quantityPosition);
    }

    /**
     * Sets the position of the unit
     *
     * @param unitPosition the new unit position
     */
    public void setUnitPosition(Position unitPosition) {
        mPositions.put(PositionKeysForIngredients.UNIT, unitPosition);
    }

    /**
     * Updates the positions of the QUANTITY UNIT and name by adding an offset. Calls the
     * {@link Position#addOffset(int)}
     * method
     *
     * @param offset the offset to add
     */
    public void addOffsetToPositions(int offset) {
        for (PositionKeysForIngredients key : PositionKeysForIngredients.values()) {
            mPositions.get(key).addOffset(offset);
        }
    }

    /**
     * Default getter
     *
     * @return the positions of NAME UNIT and QUANTITY
     */
    public Map<PositionKeysForIngredients, Position> getPositions() {
        return mPositions;
    }

    /**
     * Helper function that makes sure that after converting the ingredient the positions of the not
     * detected elements are set to 0 and the length of the new description
     *
     * @param originalLength the length of the description before converting
     * @param newLength      the length of the description after converting
     */
    public void setPositionEndOfStringCorrect(int originalLength, int newLength) {
        for (PositionKeysForIngredients key : PositionKeysForIngredients.values()) {
            Position pos = mPositions.get(key);
            if (pos.getEndIndex() == originalLength && pos.getBeginIndex() == 0) {
                pos.setEndIndex(newLength);
            }
        }

    }

    /**
     * An enum of the core elements of an ingredient: namely NAME UNIT and QUANTITY
     */
    public enum PositionKeysForIngredients {
        NAME, QUANTITY, UNIT
    }
}
