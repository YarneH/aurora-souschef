package SouschefProcessor.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Recipe {
    protected Set<IngredientUnitAmount> ingredients;
    protected ArrayList<Step> steps;
    protected int amountOfPeople;
    protected String description;

    public Recipe(Set<IngredientUnitAmount> ingredients, ArrayList<Step> steps, int amountOfPeople, String description) {
        this.ingredients = ingredients;
        this.steps = steps;
        this.amountOfPeople = amountOfPeople;
        this.description = description;
    }

    public void setSteps(ArrayList<Step> steps){
        this.steps = steps;
    }

    public void setAmountOfPeople(int amountOfPeople) {
        this.amountOfPeople = amountOfPeople;
    }

    public void setIngredients(Set<IngredientUnitAmount> ingredients) {
        this.ingredients = ingredients;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
