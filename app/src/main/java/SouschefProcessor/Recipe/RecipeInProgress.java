package SouschefProcessor.Recipe;

/**
 * A subclass of Recipe, representing a Recipe Object that is being constructed. It has three
 * additional fields:
 * ingredientsString: a string representing the ingredients list
 * stepsString: a string representing the different steps in the recipe
 */
public class RecipeInProgress extends Recipe {
    private String ingredientsString;
    private String stepsString;
    private String originalText;


    public RecipeInProgress(String originalText){
        super();
        this.originalText = originalText;
    }

    public synchronized void setIngredientsString(String ingredientsString) {
        this.ingredientsString = ingredientsString;
    }

    public synchronized void setStepsString(String stepsString) {
        this.stepsString = stepsString;
    }

    public String getOriginalText(){
        return originalText;
    }
    public String getStepsString(){
        return stepsString;
    }

    public String getIngredientsString(){
        return ingredientsString;
    }

    /**
     * Converts the RecipeInProgress to a Recipe object by dropping the two additional fields
     * @return the converted recipe
     */
    public Recipe convertToRecipe(){
       return new Recipe(ingredients, steps, amountOfPeople, description);
    }
}
