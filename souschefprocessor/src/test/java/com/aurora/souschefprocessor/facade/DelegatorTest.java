package com.aurora.souschefprocessor.facade;

import com.aurora.souschefprocessor.recipe.Recipe;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class DelegatorTest {
    private static List<String> recipes;
    private static Delegator delegator;
    private static CRFClassifier<CoreLabel> crfClassifier;


    @BeforeClass
    public static void initialize() {
        recipes = initializeRecipes();
        String modelName = "src/main/res/raw/detect_ingr_list_model.gz";
        try {
            crfClassifier = CRFClassifier.getClassifier(modelName);
            delegator = new Delegator(crfClassifier, false);
        } catch (IOException | ClassNotFoundException e) {
        }


    }

    private static List<String> initializeRecipes() {
        String filename = "src/test/java/com/aurora/souschefprocessor/facade/recipes.txt";
        List<String> list = new ArrayList<>();
        try {
            FileReader fReader = new FileReader(filename);
            BufferedReader reader = new BufferedReader(fReader);
            StringBuilder bld = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                if (!line.equals("----------")) {
                    bld.append(line + "\n");
                } else {
                    list.add(bld.toString());
                    bld = new StringBuilder();

                }
                line = reader.readLine();
            }
            list.add(bld.toString());
        } catch (IOException io) {
            System.err.print(io);
        }

        return list;
    }


    @Test
    public void Delegator_processText_NoExceptionsInDelegator() {
        boolean thrown = false;
        try {
            for (String text : recipes) {
                Recipe recipe = delegator.processText(text);
                System.out.println(recipe);

            }
        } catch (Exception e) {
            thrown = true;
            System.out.println(e);
        }
        assert (!thrown);
    }

    @Test
    public void timeForDoingTasksIsLowerThanThreshold() {


        int average_para = 0;
        System.out.println("parallelize");
        delegator = new Delegator(crfClassifier, true);
        for (String text : recipes) {

            long start = System.currentTimeMillis();
            Recipe recipe = delegator.processText(text);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_para += time;
            System.out.println(time / 1000 + " s");
            System.out.println(recipe);
        }
        average_para = average_para / recipes.size();

        // non-parallelize
        System.out.println("non-parallelize");
        delegator = new Delegator(crfClassifier, false);
        int average_non = 0;
        for (String recipeText : recipes) {
            long start = System.currentTimeMillis();
            Recipe recipe = delegator.processText(recipeText);
            long finish = System.currentTimeMillis();
            long time = finish - start;
            average_non += time;
            System.out.println(recipe);
            System.out.println(time / 1000 + " s");
        }
        average_non = average_non / recipes.size();

        System.out.println(average_non + "   " + average_para);
        assert (average_non < 15000);

    }
}