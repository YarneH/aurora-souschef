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

import com.aurora.souschefprocessor.facade.Communicator;
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
    private static final int DETECTION_STEPS = 10;
    /**
     * The maximum amount of people you can cook for.
     */
    private static final int MAX_PEOPLE = 80;
    /**
     * Stop actively updating the progressbar after MAX_WAIT_TIME.
     */
    private static final int MAX_WAIT_TIME = 15000;

    /**
     * LiveData of the current amount of people. Used for changing the amount of people,
     * especially tab 2.
     */
    private MutableLiveData<Integer> mCurrentPeople;
    /**
     * LiveData of the progress. Used to update the UI according to the progress.
     */
    private MutableLiveData<Integer> progressStep;
    /**
     * This LiveData value updates when the initialisation is finished.
     */
    private MutableLiveData<Boolean> initialised;
    /**
     * When the recipe is set, this value changes -> all observers act.
     * Proficiat! Je hebt deze hidden comment gevonden! Tof.
     */
    private MutableLiveData<Recipe> mRecipe = new MutableLiveData<>();

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
     * @param application
     */
    public RecipeViewModel(@NonNull Application application) {
        super(application);
        this.mContext = application;
        progressStep = new MutableLiveData<>();
        progressStep.setValue(0);
        initialised = new MutableLiveData<>();
        initialised.setValue(false);
        mCurrentPeople = new MutableLiveData<>();
        mCurrentPeople.setValue(0);
        Communicator.createAnnotationPipelines();
    }

    /**
     * Get the progress LiveData object
     * @return live progress
     */
    public LiveData<Integer> getProgressStep() {
        return progressStep;
    }

    /**
     * Get the actual progress, in percentages.
     * @return progress-percentage
     */
    public int getProgress() {
        if(progressStep == null || progressStep.getValue() == null) {
            return 0;
        }
        return (int) (100.0 / DETECTION_STEPS * progressStep.getValue());
    }

    /**
     * Initialise the ViewModel. This starts the progressbar, but also the extraction of text.
     */
    public void initialise() {
        if (initialised != null && initialised.getValue() != null && initialised.getValue()) {
            return;
        }
        (new ProgressUpdate()).execute();
        (new SouschefInit(getText())).execute();
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
                while (!initialised.getValue()) {
                    Thread.sleep(MILLIS_BETWEEN_UPDATES);
                    upTime += MILLIS_BETWEEN_UPDATES;

                    publishProgress(Communicator.getProgressAnnotationPipelines());
                    if (Communicator.getProgressAnnotationPipelines() >= DETECTION_STEPS) {
                        return null;
                    }
                    if (upTime > MAX_WAIT_TIME) {
                        return null;
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
            progressStep.setValue(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /**
     * Async taks executing the Souschef initialisation.
     */
    @SuppressLint("StaticFieldLeak")
    class SouschefInit extends AsyncTask<Void, String, Recipe> {

        private String mText;

        protected SouschefInit(String text) {
            mText = text;
        }

        @Override
        protected Recipe doInBackground(Void... voids) {
            // Progressupdates are in demostate

            Communicator comm = Communicator.createCommunicator(mContext);
            if (comm != null) {
                return comm.process(mText);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Recipe recipe) {
            RecipeViewModel.this.mRecipe.setValue(recipe);
            Log.d(RecipeViewModel.class.getSimpleName(), "Recipe set");
            RecipeViewModel.this.mCurrentPeople.setValue(recipe.getNumberOfPeople());
            initialised.setValue(true);
        }
    }
    public LiveData<Boolean> getInitialised() {
        return initialised;
    }

    public LiveData<Integer> getNumberOfPeople() {
        return mCurrentPeople;
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    /**
     * Increment the amount of people.
     * A maximum of {@value MAX_PEOPLE} people can be cooked for.
     */
    public void incrementPeople() {
        if(mCurrentPeople == null || mCurrentPeople.getValue() == null) {
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
        if(mCurrentPeople == null || mCurrentPeople.getValue() == null) {
            return;
        }
        if (mCurrentPeople.getValue() > 1) {
            mCurrentPeople.setValue(mCurrentPeople.getValue() - 1);
        }
    }

    /**
     * Dummy for this demo
     *
     * @return a recipe text
     */
    private static String getText() {
        return "4 people\n" +
                "\n" +
                "Ingredients\n" +
                "\n" +
                "    150 g pure chocolade 78%\n" +
                "    2 large eggs\n" +
                "    50 g witte basterdsuiker\n" +
                "    200 ml verse slagroom\n" +
                "\n" +
                "Directions\n" +
                "\n" +
                "\n" +
                "    Hak de chocolade fijn. Laat de chocolade in ca. 5 min. au bain-marie smelten in " +
                "een kom boven een pan kokend water. Roer af en toe. Neem de kom van de pan.\n" +
                "    Splits de eieren. Klop het eiwit met de helft van de suiker met een mixer ca. " +
                "5 min. totdat het glanzende stijve pieken vormt. Doe de slagroom in een ruime kom en " +
                "klop in ca. 3 min. stijf.\n" +
                "    Klop de eidooiers los met een garde. Roer de rest van de suiker erdoor.\n" +
                "    Roer de gesmolten chocolade door het eidooier-suikermengsel. Spatel het door de " +
                "slagroom. Spatel het eiwit snel en luchtig in delen door het chocolademengsel.\n" +
                "    Schep de chocolademousse in glazen, potjes of coupes, dek af met vershoudfolie " +
                "en laat minimaal 2 uur opstijven in de koelkast.\n";
    }


}
