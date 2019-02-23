package SouschefProcessor.Recipe;

/**
 * A DataClass representing a timer it has two fields
 * upperBound: an integer, representing the maximum time in seconds of the timer
 * lowerBound: an integer, representing the minimum time in seconds of the timer
 * If the timer has only one value for the time, then upperBound == lowerBound
 */
public class RecipeTimer {


    private int upperBound;
    private int lowerBound;


    public RecipeTimer(int upperBound, int lowerBound) throws TimerValueInvalidException {
        if (upperBound <= 0) {
            throw new TimerValueInvalidException("UpperBound is negative");
        }
        if (lowerBound <= 0) {
            throw new TimerValueInvalidException("LowerBound is negative");
        }
        //TODO maybe also a check for too high values?
        if (upperBound >= lowerBound) {
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
        } else {
            this.lowerBound = upperBound;
            this.upperBound = lowerBound;
        }
    }

    public RecipeTimer(int time) throws TimerValueInvalidException {
        if (time <= 0) {
            throw new TimerValueInvalidException("Time is negative");
        }
        this.upperBound = time;
        this.lowerBound = time;
    }

    public int getUpperBound() {
        return upperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }



    public class TimerValueInvalidException extends Exception {
        public TimerValueInvalidException(String message) {
            super(message);
        }

    }

}
