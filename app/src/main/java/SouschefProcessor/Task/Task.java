package SouschefProcessor.Task;

import SouschefProcessor.Recipe.Recipe;
import SouschefProcessor.Recipe.RecipeInProgress;

public interface Task {

     void doTask(RecipeInProgress recipe);
}
