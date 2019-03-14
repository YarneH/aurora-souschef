package com.aurora.souschefprocessor.task;


import com.aurora.souschefprocessor.task.sectiondivider.DetectNumberOfPeopleTask;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class DetectNumberOfPeopleTaskTest {
    private static final int DEFAULT_NUMBER = 4;
    private static RecipeInProgress recipe;
    private static DetectNumberOfPeopleTask detectNumberOfPeopleTask;
    private static String originalText;


    @BeforeClass
    public static void initialize() {
        originalText = initializeRecipeText();
        recipe = new RecipeInProgress(originalText);
        detectNumberOfPeopleTask = new DetectNumberOfPeopleTask(recipe);
    }

    private static String initializeRecipeText() {
        return ("Yield: 4 servings\n" +
                "Total Preparation Time: 60 minutes\n" +
                "1 lb. lean ground beef\n" +
                "1⁄2 c. chopped onion\n" +
                "1⁄4 c. chopped bell pepper\n" +
                "1⁄4 tsp. salt\n" +
                "1⁄4 tsp. garlic salt\n" +
                "1⁄2 tsp. ground black pepper\n" +
                "1 c. rice\n" +
                "1 tsp. salt\n" +
                "1 Tbsp. of dry onion soup mix\n" +
                "2 c. water\n" +
                "1 10 oz. can cream of mushroom soup\n" +
                "1⁄2 c. low-fat milk\n" +
                "1 c. crushed potato chips\n" +
                "Size of bake ware: 8” x 8” baking dish\n" +
                "Cooking temperature: 350F\n" +
                "Brown ground meat, onions, and bell pepper. Drain and return to skillet or Dutch oven.\n" +
                "Add salt, garlic salt, and black pepper.\n" +
                "Add rice, salt, dry onion soup, and water.\n" +
                "Cover and simmer for 20 minutes.\n" +
                "Stir in soup and milk.\n" +
                "Pour mixture into 8” x 8” baking dish.\n" +
                "Top with crushed chips.\n" +
                "Bake at 350F for 20 minutes.");
    }

    @After
    public void wipeRecipe() {
        recipe.setNumberOfPeople(0);

    }

    @Test
    public void DetectNumberOfPeopleTask_doTask_valueHasBeenRead() {
        detectNumberOfPeopleTask.doTask();
        assert (recipe.getNumberOfPeople() == (DEFAULT_NUMBER * initializeRecipeText().length()));
    }
}
