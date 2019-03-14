package com.aurora.souschefprocessor.task;


import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.sectiondivider.SplitStepsTask;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
public class SplitStepsTaskTest {
    private static RecipeInProgress recipe;
    private static SplitStepsTask splitStepsTask;
    private static String originalText;
    private static String stepList;
    @BeforeClass
    public static void initialize() {
        stepList = "In a medium bowl, with a potato masher or a fork, mash the beans with the soy sauce, " +
                "chopped pepper; and ginger, until pureed but not smooth.\n" +
                "Spoon into a small serving dish and top with scallion.\n" +
                "Serve with sesame crackers.";
        originalText = "irrelevant";
        recipe = new RecipeInProgress(originalText);
        recipe.setStepsString(stepList);
        splitStepsTask = new SplitStepsTask(recipe);
    }
    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
        recipe.setStepsString(stepList);
    }
    @Test
    public void SplitStepsTask_doTask_setHasBeenSet() {
        splitStepsTask.doTask();
        assert (recipe.getRecipeSteps() != null);
    }
    @Test
    public void SplitStepsTask_doTask_setHasCorrectSize() {
        splitStepsTask.doTask();
        assert (recipe.getRecipeSteps().size() == 3);
    }
    @Test
    public void SplitStepsTask_doTask_setHasCorrectValues() {
        splitStepsTask.doTask();
        assert(recipe.getRecipeSteps().get(0).getDescription().equals("In a medium bowl, with a potato masher or a fork, " +
                "mash the beans with the soy sauce, chopped pepper; and ginger, until pureed but not smooth."));
        assert(recipe.getRecipeSteps().get(1).getDescription().equals("Spoon into a small serving dish and top with scallion."));
        assert(recipe.getRecipeSteps().get(2).getDescription().equals("Serve with sesame crackers."));
    }
}
