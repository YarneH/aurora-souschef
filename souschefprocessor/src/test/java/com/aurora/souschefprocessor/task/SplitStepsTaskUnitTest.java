package com.aurora.souschefprocessor.task;


import com.aurora.souschefprocessor.task.sectiondivider.SplitStepsTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class SplitStepsTaskUnitTest {
    private static RecipeInProgress recipe;
    private static SplitStepsTask splitStepsTask;
    private static String originalText;
    private static String stepList;

    private static RecipeInProgress recipeAcrossNewline;
    private static String stepListAcrossNewline;
    private static SplitStepsTask splitStepsTaskAcrossNewline;
    private static String getStepListAcrossNewline;

    @BeforeClass
    public static void initialize() {
        originalText = "irrelevant";

        stepList = initializeStepList();
        recipe = new RecipeInProgress(originalText);
        recipe.setStepsString(stepList);
        splitStepsTask = new SplitStepsTask(recipe);

        stepListAcrossNewline = initializeStepListAcrossNewline();
        recipeAcrossNewline = new RecipeInProgress(originalText);
        recipeAcrossNewline.setStepsString(stepListAcrossNewline);
        splitStepsTaskAcrossNewline = new SplitStepsTask(recipeAcrossNewline);
    }


    private static String initializeStepList() {
        return "In a medium bowl, with a potato masher or a fork, mash the beans with the soy sauce, " +
                "chopped pepper; and ginger, until pureed but not smooth.\n" +
                "Spoon into a small serving dish and top with scallion.\n" +
                "Serve with sesame crackers.";
    }

    private static String initializeStepListAcrossNewline() {
        return "Heat the oil in a medium skillet over medium heat. Add the garlic and stir for about a\n" +
                "minute. Then add the beans with their liquid. Mash the beans with a potato masher or the\n" +
                "back of a spoon until you have a coarse puree, then cook, stirring regularly, until the beans\n" +
                "are thickened just enough to hold their shape in a spoon, about 10 minutes.  Taste and add\n" +
                "up to ¼ teaspoon salt.\n";
    }

    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
        recipe.setStepsString(stepList);
        recipeAcrossNewline.setIngredientsString(null);
        recipeAcrossNewline.setStepsString(stepListAcrossNewline);
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
        assert (recipe.getRecipeSteps().get(0).getDescription()
                .equals("In a medium bowl, with a potato masher or a fork, " +
                        "mash the beans with the soy sauce, chopped pepper; and ginger, until pureed but not smooth."));
        assert (recipe.getRecipeSteps().get(1).getDescription()
                .equals("Spoon into a small serving dish and top with scallion."));

        assert (recipe.getRecipeSteps().get(2).getDescription()
                .equals("Serve with sesame crackers."));
    }

    @Test
    public void SplitStepsTask_doTask_setHasCorrectSizeAcrossNewline() {
        splitStepsTaskAcrossNewline.doTask();
        assert (recipeAcrossNewline.getRecipeSteps().size() == 5);
    }

    @Test
    public void SplitStepsTask_doTask_setHasCorrectValuesAcrossNewline() {
        splitStepsTaskAcrossNewline.doTask();
        assert (recipeAcrossNewline.getRecipeSteps().get(0).getDescription()
                .equals("Heat the oil in a medium skillet over medium heat."));
        assert (recipeAcrossNewline.getRecipeSteps().get(1).getDescription()
                .equals("Add the garlic and stir for about a minute."));
        assert (recipeAcrossNewline.getRecipeSteps().get(2).getDescription()
                .equals("Then add the beans with their liquid."));
        assert (recipeAcrossNewline.getRecipeSteps().get(3).getDescription()
                .equals("Mash the beans with a potato masher or the " +
                        "back of a spoon until you have a coarse puree, then cook, stirring regularly, until the beans " +
                        "are thickened just enough to hold their shape in a spoon, about 10 minutes."));
        assert (recipeAcrossNewline.getRecipeSteps().get(4).getDescription()
                .equals("Taste and add up to ¼ teaspoon salt."));
    }


}
