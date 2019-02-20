package SouschefProcessor.Recipe;

import java.util.Set;

import SouschefProcessor.Recipe.IngredientUnitAmount;
import SouschefProcessor.TimerDetector.Timer;
public class Step {

    private Set<IngredientUnitAmount> ingredientUnitAmountSet;
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

}
