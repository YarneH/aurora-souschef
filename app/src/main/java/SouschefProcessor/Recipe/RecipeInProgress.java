package SouschefProcessor.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RecipeInProgress extends Recipe {
    String ingredientsString;
    String stepsString;

    public RecipeInProgress(Set<IngredientUnitAmount> ingredients, ArrayList<Step> steps, int amountOfPeople, String description) {
        super(ingredients, steps, amountOfPeople, description);
    }

    public void setIngredientsString(String ingredientsString) {
        this.ingredientsString = ingredientsString;
    }

    public void setStepsString(String stepsString) {
        this.stepsString = stepsString;
    }
}
