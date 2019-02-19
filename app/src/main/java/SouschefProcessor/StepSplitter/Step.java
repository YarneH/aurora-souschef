package SouschefProcessor.StepSplitter;

import SouschefProcessor.IngredientDetector.IngredientUnitAmount;
import SouschefProcessor.TimerDetector.Timer;
public class Step {

    private IngredientUnitAmount ingredientUnitAmount;
    private Timer timer;
    private String description;
    private boolean ingredientDetetected = false;
    private boolean timerDetected = false;

    public Step(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

}
