package SouschefProcessor.Recipe;

public class IngredientUnitAmount {

    private String ingredient;
    private String unit;
    private double amount;

    public IngredientUnitAmount(String ingredient, String unit, double amount) {
        this.ingredient = ingredient;
        this.unit = unit;
        this.amount = amount;
    }

    public String getIngredient() {
        return ingredient;
    }


    public String getUnit() {
        return unit;
    }

    public double getAmount() {
        return amount;
    }


    @Override
    public String toString(){
        String res = amount + " ";
        //if unit is not "", add the unit and a space to the result
        if (unit != "") {
            res+=unit+" ";
        }
        return res+ingredient;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof IngredientUnitAmount){
            IngredientUnitAmount iua = (IngredientUnitAmount) o;
            if(iua.getUnit().equals(unit) && iua.getAmount() == amount && iua.getIngredient().equals(ingredient)){
                return true;
            }
        }
        return false;
    }


}
