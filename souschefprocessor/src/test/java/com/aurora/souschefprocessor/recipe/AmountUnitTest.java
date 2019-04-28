package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AmountUnitTest {

    @Test
    public void Amount_convert_ConvertingToSystemAlreadyUsedDoesNotChangeAmount() {
        Amount a = new Amount(500, "gram");
        a.convert(true);
        assertEquals("Unit has changed", "gram", a.getUnit());
        assertEquals("Value has changed", 500, a.getValue(), 2e-3);

    }

    @Test
    public void Amount_convert_GramToOunceCorrect() {
        Amount a = new Amount(500, "gram");
        a.convert(false);
        Amount converted = new Amount(Math.round(17.637 * 1000) / 1000.0, "ounce");
        assertEquals("Conversion is not as expected", converted, a);

    }

    @Test
    public void Amount_convert_KGramToPoundCorrect() {
        Amount a = new Amount(1.5, "kilogram");
        a.convert(false);

        Amount converted = new Amount(Math.round(3.30693 * 1000) / 1000.0, "pound");
        assertEquals("Conversion is not as expected", converted, a);
    }

    @Test
    public void Amount_convert_PoundTOKGCorrect() {
        Amount a = new Amount(2, "pound");
        a.convert(true);


        Amount converted = new Amount(Math.round(1000 * 0.907185) / 1000.0, "kilogram");
        assertEquals("Conversion is not as expected", converted, a);
    }

    @Test
    public void Amount_convert_OunceToGram() {
        Amount a = new Amount(8, "ounce");
        a.convert(true);


        Amount converted = new Amount(Math.round(226.796 * 1000) / 1000.0, "gram");
        assertEquals("Conversion is not as expected", converted, a);
    }
}
