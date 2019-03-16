package com.aurora.souschefprocessor.recipe;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class IngredientTest {
    private static HashMap<Ingredient.PositionKey, Position> irrelevantPositions = new HashMap<>();

    @BeforeClass
    public static void initialize() {
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKey key : Ingredient.PositionKey.values()) {
            irrelevantPositions.put(key, pos);
        }
    }

    @Test
    public void Ingredient_NegativeAmountThrowsIllegalArgumentException() {
        boolean thrown = false;
        try {

            Ingredient ing = new Ingredient("spaghetti", "ounces", -500, "irrelevant", irrelevantPositions);
        } catch (IllegalArgumentException iae) {
            thrown = true;
        }
        assert (thrown);

    }

    @Test
    public void Ingredient_QuantityPositionBiggerThanLengthOfOrriginalTextThrowsException() {
        HashMap<Ingredient.PositionKey, Position> positions = new HashMap<>();
        String originalText = "This is the original Text";
        int beginIndexNameAndUnit = 0;
        int endIndexNameAndUnit = originalText.length();
        Position nameAndUnitPosition = new Position(beginIndexNameAndUnit, endIndexNameAndUnit);
        positions.put(Ingredient.PositionKey.UNIT, nameAndUnitPosition);
        positions.put(Ingredient.PositionKey.NAME, nameAndUnitPosition);

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        int beginIndexQuantity = 0;
        int endIndexQuantity = originalText.length() + 1;
        boolean case1Thrown = false;
        try {
            Position pos = new Position(beginIndexQuantity, endIndexQuantity);
            positions.put(Ingredient.PositionKey.QUANTITY, pos);
            Ingredient ing = new Ingredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        assert (case1Thrown);

        // case 2 both too big
        beginIndexQuantity = originalText.length();
        endIndexQuantity++;
        boolean case2Thrown = false;
        try {
            Position pos = new Position(beginIndexQuantity, endIndexQuantity);
            positions.put(Ingredient.PositionKey.UNIT, pos);
            Ingredient ing = new Ingredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        assert (case2Thrown);


    }

    @Test
    public void Ingredient_NamePositionBiggerThanLengthOfOrriginalTextThrowsException() {
        HashMap<Ingredient.PositionKey, Position> positions = new HashMap<>();
        String originalText = "This is the original Text";
        int beginIndexUnitAndValue = 0;
        int endIndexNameAndValue = originalText.length();
        Position unitAndValuePosition = new Position(beginIndexUnitAndValue, endIndexNameAndValue);
        positions.put(Ingredient.PositionKey.QUANTITY, unitAndValuePosition);
        positions.put(Ingredient.PositionKey.UNIT, unitAndValuePosition);

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        int beginIndexName = 0;
        int endIndexName = originalText.length() + 1;
        boolean case1Thrown = false;
        try {
            Position pos = new Position(beginIndexName, endIndexName);
            positions.put(Ingredient.PositionKey.NAME, pos);
            Ingredient ing = new Ingredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        assert (case1Thrown);

        // case 2 both too big
        beginIndexName = originalText.length();
        boolean case2Thrown = false;
        try {
            Position pos = new Position(beginIndexName, endIndexName);
            positions.put(Ingredient.PositionKey.UNIT, pos);
            Ingredient ing = new Ingredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        assert (case2Thrown);

    }

    @Test
    public void Ingredient_UnitPositionBiggerThanLengthOfOrriginalTextThrowsException() {
        HashMap<Ingredient.PositionKey, Position> positions = new HashMap<>();
        String originalText = "This is the original Text";
        int beginIndexNameAndValue = 0;
        int endIndexNameAndValue = originalText.length();
        Position nameAndValuePosition = new Position(beginIndexNameAndValue, endIndexNameAndValue);
        positions.put(Ingredient.PositionKey.QUANTITY, nameAndValuePosition);
        positions.put(Ingredient.PositionKey.NAME, nameAndValuePosition);

        // 2 cases
        // case 1 beginindex small enough, endindex too big
        int beginIndexUnit = 0;
        int endIndexUnit = originalText.length() + 1;
        boolean case1Thrown = false;
        try {
            Position pos = new Position(beginIndexUnit, endIndexUnit);
            positions.put(Ingredient.PositionKey.UNIT, pos);
            Ingredient ing = new Ingredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case1Thrown = true;
        }
        assert (case1Thrown);

        // case 2 both too big
        beginIndexUnit = originalText.length();
        boolean case2Thrown = false;
        try {
            Position pos = new Position(beginIndexUnit, endIndexUnit);
            positions.put(Ingredient.PositionKey.UNIT, pos);
            Ingredient ing = new Ingredient("irrelevant", "irrelevant", 0.0, originalText, positions);

        } catch (IllegalArgumentException iae) {
            case2Thrown = true;
        }
        assert (case2Thrown);
    }

    @Test
    public void Ingredient_Equals_BehavesExpectedely() {
        Ingredient iua1 = new Ingredient("spaghetti", "gram", 500, "irrelevant", irrelevantPositions);
        Ingredient iua2 = new Ingredient("spaghetti", "gram", 500, "irrelevant", irrelevantPositions);
        Ingredient iua3 = new Ingredient("sauce", "gram", 500, "irrelevant", irrelevantPositions);
        assert (iua1.equals(iua2));
        assert (!iua1.equals(iua3));
        String randomobject = "3";
        assert (!iua1.equals(randomobject));
    }

    @Test
    public void Ingredient_HashCode_SameOnlyForObjectsThatAreEqual() {
        String[] ingredients = {"spaghetti", "sauce", "meatballs"};
        String[] units = {"gram", "kilogram"};
        double[] amounts = {500, 1};
        ArrayList<Ingredient> iuas = new ArrayList<>();
        while (iuas.size() < 20) {
            for (String ing : ingredients) {
                for (String uni : units) {
                    for (double a : amounts) {
                        iuas.add(new Ingredient(ing, uni, a, "irrelevant", irrelevantPositions));
                    }
                }
            }
        }

        for (int i = 0; i < iuas.size(); i++) {
            for (int j = i + 1; j < iuas.size(); j++) {
                boolean equal = iuas.get(i).equals(iuas.get(j));
                boolean hash = (iuas.get(i).hashCode() == iuas.get(j).hashCode());
                assert ((equal && hash) || (!equal && !hash));
            }
        }


    }
}
