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

import java.util.Objects;

/**
 * Holds the data of a recipe. Is responsible for keeping that data up to date,
 * and updating the UI when necessary.
 */
public class RecipeViewModel extends AndroidViewModel {

    /**
     * The maximum amount of people you can cook for.
     */
    private static final int MAX_PEOPLE = 99;

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
     * This LiveData value updates when the amount of people is not found and set to default
     */
    private MutableLiveData<Boolean> mDefaultAmountSet = new MutableLiveData<>();

    /**
     * Indicates whether or not this recipe is already being processed
     */
    private boolean isBeingProcessed = false;

    /**
     * Listener that listens to changes in the shared preferences. It is used to check when the user
     * changes the settings from metric to imperial or back.
     * <p>
     * Must be a variable of this class to prevent garbage collection and stop listening
     */
    private SharedPreferences.OnSharedPreferenceChangeListener mListener = null;


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
     * @param application Needed for the initialisation and lifetime of a viewModel
     */
    public RecipeViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
        MutableLiveData<Integer> progressStep = new MutableLiveData<>();
        progressStep.setValue(0);
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
    private void convertRecipeUnits(boolean toMetric) {
        // creating the recipe
        Recipe recipe = mRecipe.getValue();
        if (recipe != null) {
            recipe.convertUnit(toMetric);
        }
        mRecipe.postValue(recipe);
    }

    /**
     * Initialise the data from plain text.
     *
     * @param plainText where to extract recipe from.
     */
    void initialiseWithPlainText(String plainText) {
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
    void initialiseWithExtractedText(ExtractedText extractedText) {
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
    void initialiseWithRecipe(Recipe recipe) {
        recipe.convertUnit(!isImperial());
        RecipeViewModel.this.mRecipe.setValue(recipe);
        if (Objects.requireNonNull(mRecipe.getValue()).getNumberOfPeople() == -1) {
            mRecipe.getValue().setNumberOfPeople(DEFAULT_SERVINGS_AMOUNT);
            mDefaultAmountSet.setValue(true);
        }
        RecipeViewModel.this.mCurrentPeople.setValue(recipe.getNumberOfPeople());
        mInitialised.setValue(true);
    }

    /**
     * Returns whether or not the settings are set to imperial.
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
    LiveData<Boolean> getInitialised() {
        return mInitialised;
    }

    /**
     * Get the <b>current</b> number of people that is being cooked for
     *
     * @return number of people the user is cooking for
     */
    LiveData<Integer> getNumberOfPeople() {
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
    LiveData<Boolean> getProcessFailed() {
        return mProcessingFailed;
    }

    /**
     * Observable with boolean whether or not the default amount of guests is set.
     *
     * @return LiveData with boolean
     */
    LiveData<Boolean> getDefaultAmountSet() {
        return mDefaultAmountSet;
    }

    /**
     * Increment the amount of people.
     * A maximum of {@value MAX_PEOPLE} people can be cooked for.
     */
    void incrementPeople() {
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
    void decrementPeople() {
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
    boolean isBeingProcessed() {
        return isBeingProcessed;
    }

    /**
     * Sets whether or not a recipe is being processed.
     *
     * @param isBeingProcessed boolean
     */
    void setBeingProcessed(boolean isBeingProcessed) {
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

            SouschefProcessorCommunicator communicator = SouschefProcessorCommunicator.createCommunicator(mContext);

            if (communicator != null) {
                Recipe processedRecipe = (Recipe) communicator.pipeline(mExtractedText);
                // the processing has succeeded, set the flag to false and return the processedRecipe
                mProcessingFailed.postValue(false);
                return processedRecipe;
            }
            // if the communicator was not created return null to let onPostExecute know it failed
            return null;
        }


        @Override
        protected void onPostExecute(Recipe recipe) {
            // only initialize if the processing has not failed
            if (recipe != null) {
                initialiseWithRecipe(recipe);
            }else{
                // let everyone know processing failed
                mProcessingFailed.postValue(true);
            }

        }
    }

}

