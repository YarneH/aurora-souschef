package com.aurora.souschef;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.souschefprocessor.facade.Communicator;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Recipe;
import com.aurora.souschefprocessor.recipe.RecipeStep;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {
    private static final int MILLIS_BETWEEN_UPDATES = 500;
    private static final int DETECTION_STEPS = 10;
    private static final int MAX_WAIT_TIME = 15000;

    private MutableLiveData<Integer> mCurrentPeople;
    private MutableLiveData<Integer> progressStep;
    private MutableLiveData<Boolean> initialised;
    private MutableLiveData<Recipe> mRecipe = new MutableLiveData<>();
    private Context mContext;
    private String[] mDescriptionSteps = null;

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

    public LiveData<Integer> getProgressStep() {
        return progressStep;
    }

    public int getProgress() {
        return (int) (100.0 / DETECTION_STEPS * progressStep.getValue());
    }

    public void initialise() {
        if (initialised != null && initialised.getValue()) {
            return;
        }
        (new ProgressUpdate()).execute();
        (new SouschefInit(getText())).execute();
    }

    public List<RecipeStep> getRecipeSteps() {
        return mRecipe.getValue().getRecipeSteps();
    }

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

    class SouschefInit extends AsyncTask<Void, String, Recipe> {

        private String mText;

        protected SouschefInit(String text) {
            mText = text;
        }

        @Override
        protected Recipe doInBackground(Void... voids) {
            // Progressupdates are in demostate

            Communicator comm = Communicator.createCommunicator(mContext);
            return comm.process(mText);
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

    public String[] getDescriptionSteps() {
        return mDescriptionSteps;
    }

    public List<ListIngredient> getIngredients() {
        if (mRecipe == null) {
            return null;
        }
        return mRecipe.getValue().getIngredients();
    }

    public LiveData<Integer> getNumberOfPeople() {
        return mCurrentPeople;
    }

    public LiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    public void incrementPeople() {
        if (mCurrentPeople.getValue() == 80) {
            return;
        } else {
            mCurrentPeople.setValue(mCurrentPeople.getValue() + 1);
        }
    }

    public void decrementPeople() {
        if (mCurrentPeople.getValue() == 1) {
            return;
        } else {
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
