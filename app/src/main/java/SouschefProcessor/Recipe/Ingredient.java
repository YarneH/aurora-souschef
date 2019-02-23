package SouschefProcessor.Recipe;

/**
 * A data class that represents an ingredient in the ingredient list of the recipe or a step of the
 * recipe. The class has three fields:
 * ingredient: which is a description of the ingredient (e.g. sugar, tomato)
 * unit: the unit of the ingredient (e.g. tablespoon, gram)
 * amount: the amount of the unit (e.g. 500)
 */
public class Ingredient {

    private String ingredient;
    private Amount amount;

    public Ingredient(String ingredient, String unit, double amount) {
        this.ingredient = ingredient;
        this.amount = new Amount(amount, unit);
    }

    public String getIngredient() {
        return ingredient;
    }


    public String getUnit() {
        return amount.getUnit();
    }

    public double getValue() {
        return amount.getValue();
    }

    public Amount getAmount(){ return amount;}


    @Override
    public String toString() {
        String res = amount + " ";
        //if unit is not "", add the unit and a space to the result
        return res + ingredient;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ingredient) {
            Ingredient iua = (Ingredient) o;
            if (iua.getAmount().equals(amount)  && iua.getIngredient().equals(ingredient)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * amount.hashCode();
        result = 31 * result + ingredient.hashCode();
        return result;
    }


}
