package SouschefProcessor.Recipe;

import java.util.HashSet;

/**
 * A dataclass representing a step it has  fields
 * ingredientAmountSet: a set of ingredients contained in this recipe (could be null)
 * timer: a timer contained in this recipe (could be null)
 * decription:  the textual description of this recipe, which was written in the original text
 * ingredientDetected: a boolean that indicates if the IngredientDetectorStep task has been done
 * timerDetected: a boolean that indicates if the TimerDetector task has been done on this step
 */
public class Step {

    private HashSet<IngredientUnitAmount> ingredientUnitAmountSet; //this could become a hashmap, with key the IngredientUnitAmount and value the location in the description
    private Timer timer;
    private String description;
    private boolean ingredientDetected = false;
    private boolean timerDetected = false;

    public Step(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public synchronized void setIngredientUnitAmountSet(HashSet<IngredientUnitAmount> iuaSet){
        ingredientUnitAmountSet = iuaSet;
        ingredientDetected = true;
    }

    public synchronized void setTimer(Timer t){
        timer = t;
        timerDetected = true;
    }



}
