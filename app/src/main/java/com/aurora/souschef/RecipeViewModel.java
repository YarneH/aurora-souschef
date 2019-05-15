package com.aurora.souschef;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.facade.SouschefProcessorCommunicator;
import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Holds the data of a recipe. Is responsible for keeping that data up to date,
 * and updating the UI when necessary.
 */
public class RecipeViewModel extends AndroidViewModel {


    /**
     * The amount of steps it takes to detect a recipe.
     * This is used to pick the interval updates of the progress bar.
     * These steps are hard-coded-counted. This means that when the implementation
     * of the Souschef-processor takes longer or shorter, this value must be changed.
     */
    private static final int DETECTION_STEPS = 3;

    /**
     * The maximum amount of people you can cook for.
     */
    private static final int MAX_PEOPLE = 99;


    /**
     * Percentages in 100%
     */
    private static final double MAX_PERCENTAGE = 100.0;

    /**
     * Default amount of people
     */
    private static final int DEFAULT_SERVINGS_AMOUNT = 4;

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
     * This LiveData value updates when the amount of people is not found and set to default
     */
    private MutableLiveData<Boolean> mDefaultAmountSet = new MutableLiveData<>();

    /**
     * Indicates whether or not this recipe is already being processed
     */
    private boolean isBeingProcessed = false;


    /**
     * The context of the application.
     * <p>
     * The only use of context is to get the Souschef NLP model loaded.
     * This leak is not an issue.
     */
    @SuppressLint("StaticFieldLeak")
    private Context mContext;

    /**
     * Listener that listens to changes in the shared preferences. It is used to check when the user
     * changes the settings from metric to imperial or back.
     * <p>
     * Must be a variable of this class to prevent garbage collection and stop listening
     */
    private SharedPreferences.OnSharedPreferenceChangeListener mListener = null;

    /**
     * Constructor that initialises the pipeline and LiveData.
     *
     * @param application Needed for the initialisation and lifetime of a viewModel
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
        mDefaultAmountSet.setValue(false);
        SouschefProcessorCommunicator.createAnnotationPipelines();
        SharedPreferences sharedPreferences = application.getSharedPreferences(
                Tab1Overview.SETTINGS_PREFERENCES,
                Context.MODE_PRIVATE);
        mListener = (SharedPreferences preferences, String key) -> {
            if (key.equals(Tab1Overview.IMPERIAL_SETTING)) {
                boolean imperial = preferences.getBoolean(key, false);
                convertRecipeUnits(!imperial);
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
    }

    /**
     * Converts all the units in the recipe
     *
     * @param toMetric boolean that indicates if the units should be converted to metric or to US
     */
    public void convertRecipeUnits(boolean toMetric) {
        // creating the recipe
        Recipe recipe = mRecipe.getValue();
        if (recipe != null) {
            recipe.convertUnit(toMetric);
        }
        mRecipe.postValue(recipe);
    }

    /**
     * Get the failure message observable .
     *
     * @return LiveData that updates failure messages
     */
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
        (new SouschefInit(extractedText)).execute();

    }

    /**
     * Initialise data directly with a recipe.
     *
     * @param recipe the recipe for data extraction.
     */
    public void initialiseWithRecipe(Recipe recipe) {
        recipe.convertUnit(!isImperial());
        RecipeViewModel.this.mRecipe.setValue(recipe);
        if (mRecipe.getValue().getNumberOfPeople() == -1) {
            mRecipe.getValue().setNumberOfPeople(DEFAULT_SERVINGS_AMOUNT);
            mDefaultAmountSet.setValue(true);
        }
        RecipeViewModel.this.mCurrentPeople.setValue(recipe.getNumberOfPeople());
        mInitialised.setValue(true);
    }

    /**
     * Returns whether or not the settings are set to imperial or not.
     * <p>
     * Accesses shared preferences.
     *
     * @return True if imperial
     */
    private boolean isImperial() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(
                Tab1Overview.SETTINGS_PREFERENCES,
                Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Tab1Overview.IMPERIAL_SETTING, false);
    }

    /**
     * Get whether or not the recipe is initialized yet.
     *
     * @return true if initialized
     */
    public LiveData<Boolean> getInitialised() {
        return mInitialised;
    }

    /**
     * Get the <b>current</b> number of people that is being cooked for
     *
     * @return number of people the user is cooking for
     */
    public LiveData<Integer> getNumberOfPeople() {
        return mCurrentPeople;
    }

    /**
     * Get the Observable of the recipe object.
     * <p>
     * Updates when the recipe is loaded.
     *
     * @return LiveData with the recipe object
     */
    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    /**
     * Observable with boolean whether or not the default amount of guests is set.
     *
     * @return LiveData with boolean
     */
    public LiveData<Boolean> getProcessFailed() {
        return mProcessingFailed;
    }

    /**
     * Observable with boolean whether or not the default amount of guests is set.
     *
     * @return LiveData with boolean
     */
    public LiveData<Boolean> getDefaultAmountSet() {
        return mDefaultAmountSet;
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
     * Whether or not the recipe is being processed.
     *
     * @return true if processing
     */
    public boolean isBeingProcessed() {
        return isBeingProcessed;
    }

    /**
     * Sets whether or not a recipe is being processed.
     *
     * @param isBeingProcessed boolean
     */
    public void setBeingProcessed(boolean isBeingProcessed) {
        this.isBeingProcessed = isBeingProcessed;
    }


    /**
     * Async taks executing the Souschef initialisation.
     */
    @SuppressLint("StaticFieldLeak")
    class SouschefInit extends AsyncTask<Void, String, Recipe> {

        private ExtractedText mExtractedText;

        SouschefInit(String text) {
            this.mExtractedText = ExtractedText.fromJson(text);
        }

        SouschefInit(ExtractedText extractedText) {
            this.mExtractedText = extractedText;
        }

        @Override
        protected Recipe doInBackground(Void... voids) {
            // Progressupdates are in demostate

            SouschefProcessorCommunicator comm = SouschefProcessorCommunicator.createCommunicator(mContext);
            if (comm != null) {
                return (Recipe) comm.pipeline(mExtractedText);
            }
            return null;
        }


        @Override
        protected void onPostExecute(Recipe recipe) {
            // only initialize if the processing has not failed
            if (!mProcessingFailed.getValue() && recipe != null) {
                initialiseWithRecipe(recipe);
            }
        }
    }

}

