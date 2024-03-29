package com.aurora.souschefprocessor.recipe;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;


public class IngredientUnitTest {
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();

    @BeforeClass
    public static void initialize() {
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void Ingredient_NegativeAmountThrowsIllegalArgumentException() {
        /**
         * An ingredient cannot have a negative amount, when constructing an object with a negative
         * amount an exception should be thrown
         */
        Ingredient ing = new Ingredient("spaghetti", "ounces", -500, irrelevantPositions);
    }

    @Test
    public void ListIngredient_QuantityPositionBiggerThanLengthOfOriginalTextThrowsException() {
        /**
         * The postion of the quantity cannot be larger than the length of the original string,
         * trying to construct this should throw an exception
         */

        // Arrange
        HashMap<Ingredient.PositionKeysForIngredients, Position> positions = new HashMap<>();
        String originalText = "This is the original Text";
        int beginIndexNameAndUnit = 0;
        int endIndexNameAndUnit = originalText.length();
        Position nameAndUnitPosition = new Position(beginIndexNameAndUnit, endIndexNameAndUnit);
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, nameAndUnitPosition);
        positions.put(Ingredient.PositionKeysForIngredients.NAME, nameAndUnitPosition);

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        // Arrange
        int beginIndexQuantity = 0;
        int endIndexQuantity = originalText.length() + 1;
        boolean case1Thrown = false;
        Position pos = new Position(beginIndexQuantity, endIndexQuantity);
        positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, pos);

        // Act
        try {
            Ingredient ing = new ListIngredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        // Assert
        assertTrue("Only endIndex too big does not throw exception", case1Thrown);

        // case 2 both too big
        // Arrange
        beginIndexQuantity = originalText.length();
        endIndexQuantity++;
        boolean case2Thrown = false;
        pos = new Position(beginIndexQuantity, endIndexQuantity);
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, pos);
        //Act
        try {

            Ingredient ing = new ListIngredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        // Assert
        assertTrue("Both end- and beginIndex too big does not throw exception", case2Thrown);


    }

    @Test
    public void ListIngredient_NamePositionBiggerThanLengthOfOrriginalTextThrowsException() {
        /**
         * The position of the name cannot be larger than the length of the original string,
         * trying to construct this should throw an exception
         */
        // Arrange
        HashMap<Ingredient.PositionKeysForIngredients, Position> positions = new HashMap<>();
        String originalText = "This is the original Text";
        int beginIndexUnitAndValue = 0;
        int endIndexNameAndValue = originalText.length();
        Position unitAndValuePosition = new Position(beginIndexUnitAndValue, endIndexNameAndValue);
        positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, unitAndValuePosition);
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, unitAndValuePosition);

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        // Arrange
        int beginIndexName = 0;
        int endIndexName = originalText.length() + 1;
        boolean case1Thrown = false;
        Position pos = new Position(beginIndexName, endIndexName);
        positions.put(Ingredient.PositionKeysForIngredients.NAME, pos);
        // Act
        try {

            Ingredient ing = new ListIngredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        // Assert
        assertTrue("Only endIndex too big does not throw exception", case1Thrown);

        // case 2 both too big
        // Arrange
        beginIndexName = originalText.length();
        boolean case2Thrown = false;
        pos = new Position(beginIndexName, endIndexName);
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, pos);
        // Act
        try {

            Ingredient ing = new ListIngredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        // Assert
        assertTrue("Both end- and beginIndex too big does not throw exception", case1Thrown);

    }

    @Test
    public void ListIngredient_UnitPositionBiggerThanLengthOfOriginalTextThrowsException() {
        /**
         * The position of the unit cannot be larger than the length of the original string,
         * trying to construct this should throw an exception
         */
        // Arrange
        HashMap<Ingredient.PositionKeysForIngredients, Position> positions = new HashMap<>();
        String originalText = "This is the original Text";
        int beginIndexNameAndValue = 0;
        int endIndexNameAndValue = originalText.length();
        Position nameAndValuePosition = new Position(beginIndexNameAndValue, endIndexNameAndValue);
        positions.put(Ingredient.PositionKeysForIngredients.QUANTITY, nameAndValuePosition);
        positions.put(Ingredient.PositionKeysForIngredients.NAME, nameAndValuePosition);
        // 2 cases
        // case 1 beginindex small enough, endindex too big
        // Arrange
        int beginIndexUnit = 0;
        int endIndexUnit = originalText.length() + 1;
        boolean case1Thrown = false;
        Position pos = new Position(beginIndexUnit, endIndexUnit);
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, pos);
        // Act
        try {

            Ingredient ing = new ListIngredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        // Assert
        assertTrue("Only endIndex too big does not throw exception", case1Thrown);

        // case 2 both too big
        // Arrange
        beginIndexUnit = originalText.length();
        boolean case2Thrown = false;
        pos = new Position(beginIndexUnit, endIndexUnit);
        positions.put(Ingredient.PositionKeysForIngredients.UNIT, pos);
        // Act
        try {
            Ingredient ing = new ListIngredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        // Assert
        assertTrue("Both begin- and endIndex too big does not throw exception", case2Thrown);
    }


    @Test
    public void Ingredient_Equals_BehavesExpectedely() {
        /**
         * An ingredient should be equal when the name, unit and value are equal
         */
        // Arrange
        Ingredient ing1 = new Ingredient("spaghetti", "gram", 500, irrelevantPositions);
        Ingredient ing2 = new Ingredient("spaghetti", "gram", 500, irrelevantPositions);
        Ingredient ing3 = new Ingredient("sauce", "gram", 500, irrelevantPositions);

        HashMap<Ingredient.PositionKeysForIngredients, Position> newPositions =
                new HashMap<>(irrelevantPositions);

        // Make sure a different position does not make the equal false
        Ingredient ing4 = new Ingredient("spaghetti", "gram", 500, newPositions);
        newPositions.put(Ingredient.PositionKeysForIngredients.QUANTITY, new Position(0, 5));


        // Capitalization should not make a difference
        Ingredient ing5 = new Ingredient("Spaghetti", "GRAM", 500, irrelevantPositions);

        // Act and Assert
        assertEquals("Same inputs is not equal", ing1, ing2);
        assertEquals("Equal is not commutative", ing2, ing1);
        assertEquals("Different positions do make a difference", ing1, ing4);
        assertNotEquals("Different inputs are equal", ing3, ing1);
        assertNotEquals("Random object is equal to Ingredient", ing1, "3");
        assertEquals("Capitalization does make a difference", ing1, ing5);
    }

    @Test
    public void Ingredient_HashCode_SameOnlyForObjectsThatAreEqual() {
        /*
         * A small check that checks if the hashcode of equal objects is the same
         */
        // arrange create som elements
        String[] names = {"spaghetti", "sauce", "meatballs"};
        String[] units = {"gram", "kilogram"};
        double[] amounts = {500, 1};
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        // create the the elements repeatedly
        while (ingredients.size() < 20) {
            for (String name : names) {
                for (String unit : units) {
                    for (double value : amounts) {
                        ingredients.add(new Ingredient(name, unit, value, irrelevantPositions));
                    }
                }
            }
        }

        for (int i = 0; i < ingredients.size(); i++) {
            for (int j = i + 1; j < ingredients.size(); j++) {
                boolean equal = ingredients.get(i).equals(ingredients.get(j));
                boolean hash = (ingredients.get(i).hashCode() == ingredients.get(j).hashCode());
                assertTrue("hashcodes are different for equal objects, or are equal for unequal objects", equal == hash);
            }
        }


    }

    @Test
    public void ListIngredient_convert_ConversionIsCorrectInIngredientAndPosition() {
        String original = "500 gram spaghetti";
        Position quantityPos = new Position(0, 3);
        Position unitPos = new Position(4, 8);
        Position namePos = new Position(9, 18);
        Map<Ingredient.PositionKeysForIngredients, Position> map = new HashMap<>();
        map.put(Ingredient.PositionKeysForIngredients.QUANTITY, quantityPos);
        map.put(Ingredient.PositionKeysForIngredients.UNIT, unitPos);
        map.put(Ingredient.PositionKeysForIngredients.NAME, namePos);
        ListIngredient listIngredient = new ListIngredient("spaghetti", "gram", 500, original, map);

        listIngredient.convertUnit(false);

        Position quantityPosN = new Position(0, 6);
        Position unitPosN = new Position(7, 12);
        Position namePosN = new Position(13, 22);
        Map<Ingredient.PositionKeysForIngredients, Position> mapN = new HashMap<>();
        mapN.put(Ingredient.PositionKeysForIngredients.QUANTITY, quantityPosN);
        mapN.put(Ingredient.PositionKeysForIngredients.UNIT, unitPosN);
        mapN.put(Ingredient.PositionKeysForIngredients.NAME, namePosN);
        ListIngredient n = new ListIngredient("spaghetti", "ounce", 17.637, "17.637 ounce spaghetti", mapN);
        assertEquals("The ingredient is not the same after conversion", n, listIngredient);
        // check positions

        assertEquals("Quantity positions are not equal", quantityPos, quantityPosN);
        assertEquals("NAME positions are not equal", namePos, namePosN);
        assertEquals("Unit positions are not equal", unitPos, unitPosN);
    }

}
