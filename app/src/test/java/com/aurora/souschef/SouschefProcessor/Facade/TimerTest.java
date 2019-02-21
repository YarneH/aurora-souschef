package com.aurora.souschef.SouschefProcessor.Facade;

import org.junit.Test;

import SouschefProcessor.Recipe.Timer;

public class TimerTest {

    @Test
    public void Timer_Constructor_LowerBoundNotBiggerThanUpperBounc(){
        //four cases
        //case 1 upperbound argument bigger than lowerbound
        int upperbound = 20;
        int lowerbound = 10;
        Timer timer = new Timer(upperbound, lowerbound);
        assert(timer.getLowerBound()<=timer.getUpperBound());
        //case 2 upperbound argument same as lowerbound
        lowerbound = upperbound;
        timer = new Timer(upperbound, lowerbound);
        assert(timer.getLowerBound()<=timer.getUpperBound());
        //case 3 only one argument
        timer = new Timer(upperbound);
        assert(timer.getLowerBound()<=timer.getUpperBound());
        //case 4 upperbound argument smaller than lowerbound argument
        lowerbound = 100;
        timer = new Timer(upperbound, lowerbound);
        assert(timer.getLowerBound()<=timer.getUpperBound());



    }
}
