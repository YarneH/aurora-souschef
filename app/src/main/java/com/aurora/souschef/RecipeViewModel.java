package com.aurora.souschef;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.facade.Communicator;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Holds the data of a recipe. Is responsible for keeping that data up to date,
 * and updating the UI when necessary.
 */
public class RecipeViewModel extends AndroidViewModel {
    /**
     * When initialising Souschef, poll every MILLIS_BETWEEN_UPDATES milliseconds
     * for updates on the progressbar. This could also be done with an observable.
     */
    private static final int MILLIS_BETWEEN_UPDATES = 500;
    /**
     * The amount of steps it takes to detect a recipe.
     * This is used to pick the interval updates of the progress bar.
     * These steps are hard-coded-counted. This means that when the implementation
     * of the Souschef-processor takes longer or shorter, this value must be changed.
     */
    private static final int DETECTION_STEPS = 5;
    /**
     * The maximum amount of people you can cook for.
     */
    private static final int MAX_PEOPLE = 80;
    /**
     * Stop actively updating the progressbar after MAX_WAIT_TIME.
     */
    private static final int MAX_WAIT_TIME = 15000;
    /**
     * Percentages in 100%
     */
    private static final double MAX_PERCENTAGE = 100.0;

    /**
     * LiveData of the current amount of people. Used for changing the amount of people,
     * especially tab 2.
     */
    private MutableLiveData<Integer> mCurrentPeople;
    /**
     * LiveData of the progress. Used to update the UI according to the progress.
     */
    private MutableLiveData<Integer> mProgressStep;
    /**
     * This LiveData value updates when the initialisation is finished.
     */
    private MutableLiveData<Boolean> mInitialised;
    /**
     * When the recipe is set, this value changes -> all observers act.
     * Proficiat! Je hebt deze hidden comment gevonden! Tof.
     */
    private MutableLiveData<Recipe> mRecipe = new MutableLiveData<>();

    /**
     * This LiveData value updates when the processing has failed
     */
    private MutableLiveData<Boolean> mProcessingFailed = new MutableLiveData<>();

    /**
     * This LiveData value updates when the processing has failed and sets the failing message
     */
    private MutableLiveData<String> mFailureMessage = new MutableLiveData<>();

    /**
     * The context of the application.
     * <p>
     * The only use of context is to get the Souschef NLP model loaded.
     * This leak is not an issue.
     */
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    /**
     * Constructor that initialises the pipeline and LiveData.
     *
     * @param application
     */
    public RecipeViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
        mProgressStep = new MutableLiveData<>();
        mProgressStep.setValue(0);
        mInitialised = new MutableLiveData<>();
        mInitialised.setValue(false);
        mCurrentPeople = new MutableLiveData<>();
        mCurrentPeople.setValue(0);
        mProcessingFailed.setValue(false);
        Communicator.createAnnotationPipelines();
    }

    public LiveData<String> getFailureMessage() {
        return mFailureMessage;
    }

    /**
     * Get the progress LiveData object
     *
     * @return live progress
     */
    public LiveData<Integer> getProgressStep() {
        return mProgressStep;
    }

    /**
     * Get the actual progress, in percentages.
     *
     * @return progress-percentage
     */
    public int getProgress() {
        if (mProgressStep == null || mProgressStep.getValue() == null) {
            return 0;
        }
        return (int) (MAX_PERCENTAGE / DETECTION_STEPS * mProgressStep.getValue());
    }

    /**
     * Initialise the data from plain text.
     *
     * @param plainText where to extract recipe from.
     */
    public void initialiseWithPlainText(String plainText) {
        if (mInitialised != null && mInitialised.getValue() != null && mInitialised.getValue()) {
            return;
        }
        (new ProgressUpdate()).execute();
        (new SouschefInit(plainText)).execute();
    }

    /**
     * Initialise the data with {@link ExtractedText}.
     *
     * @param extractedText where to get recipe from.
     */
    public void initialiseWithExtractedText(ExtractedText extractedText) {
        if (mInitialised != null && mInitialised.getValue() != null && mInitialised.getValue()) {
            return;
        }
        (new ProgressUpdate()).execute();
        (new SouschefInit(extractedText)).execute();

    }

    /**
     * Initialise data directly with a recipe.
     *
     * @param recipe the recipe for data extraction.
     */
    public void initialiseWithRecipe(Recipe recipe) {
        RecipeViewModel.this.mRecipe.setValue(recipe);
        RecipeViewModel.this.mCurrentPeople.setValue(recipe.getNumberOfPeople());
        mInitialised.setValue(true);
    }

    public LiveData<Boolean> getInitialised() {
        return mInitialised;
    }

    public LiveData<Integer> getNumberOfPeople() {
        return mCurrentPeople;
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    public LiveData<Boolean> getProcessFailed() {
        return mProcessingFailed;
    }

    /**
     * Increment the amount of people.
     * A maximum of {@value MAX_PEOPLE} people can be cooked for.
     */
    public void incrementPeople() {
        if (mCurrentPeople == null || mCurrentPeople.getValue() == null) {
            return;
        }
        if (mCurrentPeople.getValue() < MAX_PEOPLE) {
            mCurrentPeople.setValue(mCurrentPeople.getValue() + 1);
        }
    }

    /**
     * Decrement the amount of people.
     * Decrementing cannot go below 1.
     */
    public void decrementPeople() {
        if (mCurrentPeople == null || mCurrentPeople.getValue() == null) {
            return;
        }
        if (mCurrentPeople.getValue() > 1) {
            mCurrentPeople.setValue(mCurrentPeople.getValue() - 1);
        }
    }

    /**
     * Converts all the units in the recipe
     *
     * @param toMetric boolean that indicates if the units should be converted to metric or to US
     */
    public void convertRecipeUnits(boolean toMetric) {
        // TODO call this function after user has chosen/changed preference and/or when first
        // creating the recipe
        Recipe recipe = mRecipe.getValue();
        if (recipe != null) {
            recipe.convertUnit(toMetric);
        }
    }

    /**
     * Async task executing the logic for the progress bar.
     * If leaked, it will stop after {@value MAX_WAIT_TIME} milliseconds.
     */
    @SuppressLint("StaticFieldLeak")
    class ProgressUpdate extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            int upTime = 0;
            try {
                while (!mInitialised.getValue()) {
                    Thread.sleep(MILLIS_BETWEEN_UPDATES);
                    upTime += MILLIS_BETWEEN_UPDATES;

                    publishProgress(Communicator.getProgressAnnotationPipelines());
                    if (Communicator.getProgressAnnotationPipelines() >= DETECTION_STEPS || upTime > MAX_WAIT_TIME) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Log.e(RecipeViewModel.class.getSimpleName(), "Caught interruptedException");
                Thread.currentThread().interrupt();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressStep.setValue(values[0]);
        }
    }

    /**
     * Async taks executing the Souschef initialisation.
     */
    @SuppressLint("StaticFieldLeak")
    class SouschefInit extends AsyncTask<Void, String, Recipe> {

        private String mText;
        private ExtractedText mExtractedText;
        private boolean mWithExtractedText = false;

        public SouschefInit(String text) {
            this.mText = text;
        }

        public SouschefInit(ExtractedText extractedText) {
            this.mExtractedText = extractedText;
            mWithExtractedText = true;
        }

        @Override
        protected Recipe doInBackground(Void... voids) {
            // Progressupdates are in demostate

            Communicator comm = Communicator.createCommunicator(mContext);
            if (comm != null) {
                // Pick the correct type of text.
                try {
                    if (mWithExtractedText) {
                        if(mExtractedText.getSections() ==  null){
                            throw new RecipeDetectionException("The received text from Aurora did " +
                                    "not contain sections" +
                                    ", make sure you can open this type of file. If the problem" +
                                    " persists, please send feedback in Aurora");
                        }
                        return comm.process(mExtractedText);
                    } else {
                        return comm.process(mText);
                    }
                } catch (RecipeDetectionException rde) {
                    Log.d("FAILURE", rde.getMessage());
                    mFailureMessage.postValue(rde.getMessage());
                    mProcessingFailed.postValue(true);

                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Recipe recipe) {
            // only initialize if the processing has not failed
            if (!mProcessingFailed.getValue()) {
                initialiseWithRecipe(recipe);
            }
        }
    }
}
