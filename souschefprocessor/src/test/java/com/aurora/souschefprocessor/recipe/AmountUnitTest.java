package com.aurora.souschefprocessor.recipe;

import org.junit.Test;

public class AmountUnitTest {

    @Test
    public void Amount_convert_ConvertingToSystemAlreadyUsedDoesNotChangeAmount(){
        Amount a = new Amount(500, "gram");
        a.convert(true);
        assert(a.getUnit().equals("gram") && a.getValue() == 500);
    }

    @Test
    public void Amount_convert_GramToOunceCorrect(){
        Amount a = new Amount(500, "gram");
        a.convert(false);
        Amount converted = new Amount(Math.round(17.637*1000)/1000.0, "ounce");
        assert(a.equals(converted));
    }

    @Test
    public void Amount_convert_KGramToPoundCorrect(){
        Amount a = new Amount(1.5, "kilogram");
        a.convert(false);
        System.out.println(a);

        Amount converted = new Amount(Math.round(3.30693*1000)/1000.0, "pound");
        System.out.println(converted);
        assert(a.equals(converted));
    }

    @Test
    public void Amount_convert_PoundTOKGCorrect(){
        Amount a = new Amount(2, "pound");
        a.convert(true);
        System.out.println(a);

        Amount converted = new Amount(Math.round(1000*0.907185)/1000.0, "kilogram");
        System.out.println(converted);
        assert(a.equals(converted));
    }

    @Test
    public void Amount_convert_OunceToGram(){
        Amount a = new Amount(8, "ounce");
        a.convert(true);
        System.out.println(a);

        Amount converted = new Amount(Math.round(226.796*1000)/1000.0, "gram");
        System.out.println(converted);
        assert(a.equals(converted));
    }
}
