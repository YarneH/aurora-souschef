package com.aurora.souschef.SouschefProcessor.Facade;

import org.junit.Test;

import SouschefProcessor.Recipe.RecipeTimer;

public class RecipeTimerTest {

    @Test
    public void RecipeTimer_Constructor_LowerBoundNotBiggerThanUpperBound() throws RecipeTimer.TimerValueInvalidException {
        //four cases

        //case 1 upperbound argument bigger than lowerbound
        int upperbound = 20;
        int lowerbound = 10;
        RecipeTimer recipeTimer = new RecipeTimer(upperbound, lowerbound);
        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());

        //case 2 upperbound argument same as lowerbound
        lowerbound = upperbound;
        recipeTimer = new RecipeTimer(upperbound, lowerbound);
        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());

        //case 3 only one argument
        recipeTimer = new RecipeTimer(upperbound);
        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());

        //case 4 upperbound argument smaller than lowerbound argument
        lowerbound = 100;
        recipeTimer = new RecipeTimer(upperbound, lowerbound);
        assert (recipeTimer.getLowerBound() <= recipeTimer.getUpperBound());


    }
    @Test
    public void RecipeTimer_Constructor_InvalidTimerRaisesException(){
        //case 1 upperbound negative
        int upperbound = -10;
        int lowerbound = 10;
        boolean thrown = false;

        try{
            RecipeTimer recipeTimer = new RecipeTimer(upperbound, lowerbound);
        }
        catch(RecipeTimer.TimerValueInvalidException tvie){
            thrown = true;
        }
        assert(thrown);

        //case 2 lowerbound negative
        upperbound = 10;
        lowerbound = -10;
        thrown = false;

        try{
            RecipeTimer recipeTimer = new RecipeTimer(upperbound, lowerbound);
        }
        catch(RecipeTimer.TimerValueInvalidException tvie){
            thrown = true;
        }
        assert(thrown);

        //case 3 time negative
        int time = -7;
        thrown = false;

        try{
            RecipeTimer recipeTimer = new RecipeTimer(time);
        }
        catch(RecipeTimer.TimerValueInvalidException tvie){
            thrown = true;
        }
        assert(thrown);



    }
}
