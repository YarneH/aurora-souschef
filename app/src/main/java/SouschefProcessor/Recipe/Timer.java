package SouschefProcessor.Recipe;

/**
 * A DataClass representing a timer it has two fields
 * upperBound: an integer, representing the maximum time in seconds of the timer
 * lowerBound: an integer, representing the minimum time in seconds of the timer
 * If the timer has only one value for the time, then upperBound == lowerBound
 */
public class Timer {


    private int upperBound;
    private int lowerBound;

    public Timer(int upperBound, int lowerBound) {
        if (upperBound >= lowerBound) {
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
        } else {
            this.lowerBound = upperBound;
            this.upperBound = lowerBound;
        }
    }

    public Timer(int time) {
        this.upperBound = time;
        this.lowerBound = time;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }

}
