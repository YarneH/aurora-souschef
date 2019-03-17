package com.aurora.souschefprocessor.task;

import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;
import com.aurora.souschefprocessor.task.sectiondivider.DetectNumberOfPeopleTask;
import com.aurora.souschefprocessor.task.sectiondivider.SplitToMainSectionsTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SplitToMainSectionsTaskTest {
    private static RecipeInProgress recipe;
    private static SplitToMainSectionsTask splitToMainSectionsTask;
    private static String originalText;

    @BeforeClass
    public static void initialize() throws IOException,ClassNotFoundException {
        originalText = initializeRecipeText();
        recipe = new RecipeInProgress(originalText);
        splitToMainSectionsTask = new SplitToMainSectionsTask(recipe);
    }

    @After
    public void wipeRecipe() {
        recipe.setIngredients(null);
        recipe.setDescription(null);
        recipe.setRecipeSteps(null);
    }

    @Test
    public void SplitToMainSections_doTask_sectionsAreSet() {
        splitToMainSectionsTask.doTask();
        assert (recipe.getStepsString() != null);
        assert (recipe.getIngredientsString() != null);
        assert (recipe.getDescription() != null);
    }

    @Test
    public void SplitToMainSections_doTask_sectionsHaveCorrectValues(){
        splitToMainSectionsTask.doTask();
        assert(recipe.getDescription().equals("crostini with smoked salmon & sour cream"));
        assert(recipe.getStepsString().equals("Toast baguette slices lightly on one side. " +
                "Layer each round with smoked salmon, top with a dollup of sour \n" +
                "cream and sprinkle with a few capers and lots of freshly ground black pepper."));
        assert(recipe.getIngredientsString().equals("8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve"));
    }

    private static String initializeRecipeText() {
        return ("crostini with smoked salmon & sour cream\n" +
                "serves 1\n\n" +
                "This is one of those effortless starters that feels a little bit special \n" +
                "but can be made in a flash from ingredients from your supermarket.\n\n" +
                "If  you  don't  have  access  to  capers,  chopped  chives  or  parsley  would work well.\n" +
                "It's more about getting some visual greenery and freshness.\n\n" +
                "Baguettes are lovely for crostini but I've also used crackers or larger slices of sourdough cut into small,\n"  +
                "bite sized pieces.\n\n" +
                "8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve\n\n" +
                "Toast baguette slices lightly on one side. Layer each round with smoked salmon, top with a dollup of sour \n" +
                "cream and sprinkle with a few capers and lots of freshly ground black pepper.\n\n\n"
        );
    }
}