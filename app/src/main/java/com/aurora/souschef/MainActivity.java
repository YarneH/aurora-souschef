package com.aurora.souschef;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.souschefprocessor.facade.Communicator;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.recipe.Recipe;

public class MainActivity extends AppCompatActivity implements Tab2Ingredients.OnAmountOfPeopleChangedListener {

    private static final int TAB_OVERVIEW = 0;
    private static final int TAB_INGREDIENTS = 1;
    private static final int TAB_STEPS = 2;
    private static final int NUMBER_OF_TABS = 3;
    private static final int PROGRESS_PER_STEP = 14;
    private static final int MILLIS_BETWEEN_UPDATES = 500;
    private static final int MAX_WAIT_TIME = 15000;
    private static final int DETECTION_STEPS = 6;
    private static final String[] STEPS = {
            "Initializing...",
            "Creating new pipeline...",
            "Doing smart stuff to understand words...",
            "Understanding sentences...",
            "Revising some stuff",
            "Searching for timers...",
            "Finishing up..."};
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private Context mContext = this;
    private SectionsPagerAdapter mSectionsPagerAdapter = null;
    private Tab2Ingredients.OnAmountOfPeopleChangedListener mOnAmountOfPeopleChangedListener = this;


    public MainActivity() {
        // Default constructor
    }

    /**
     * Dummy for this demo
     *
     * @return a recipe text
     */
    private static String getText() {
        return "Yield\n" +
                "    4 servings\n" +
                "Active Time\n" +
                "    30 minutes\n" +
                "Total Time\n" +
                "    35 minutes\n" +
                "\n" +
                "Ingredients\n" +
                "\n" +
                "        1 lb. linguine or other long pasta\n" +
                "        Kosher salt\n" +
                "        1 (14-oz.) can diced tomatoes\n" +
                "        1/2 cup extra-virgin olive oil, divided\n" +
                "        1/4 cup capers, drained\n" +
                "        6 oil-packed anchovy fillets\n" +
                "        1 Tbsp. tomato paste\n" +
                "        1/3 cup pitted Kalamata olives, halved\n" +
                "        2 tsp. dried oregano\n" +
                "        1/2 tsp. crushed red pepper flakes\n" +
                "        6 oz. oil-packed tuna\n" +
                "\n" +
                "\n" +
                "\n" +
                "        Cook pasta in a large pot of boiling salted water, stirring " +
                "occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking " +
                "liquid; return pasta to pot.\n" +
                "        While pasta cooks, pour tomatoes into a fine-mesh sieve set over " +
                "a medium bowl. Shake to release as much juice as possible, then let tomatoes " +
                "drain in sieve, collecting juices in bowl, until ready to use.\n" +
                "        Heat 1/4 cup oil in a large deep-sided skillet over medium-high. " +
                "Add capers and cook, swirling pan occasionally, until they burst and are " +
                "crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper " +
                "towel-lined plate, reserving oil in skillet.\n" +
                "        Combine anchovies, tomato paste, and drained tomatoes in skillet. " +
                "Cook over medium-high heat, stirring occasionally, until tomatoes begin " +
                "to caramelize and anchovies start to break down, about 5 minutes. Add " +
                "collected tomato juices, olives, oregano, and red pepper flakes and bring " +
                "to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, " +
                "about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta " +
                "cooking liquid to pan. Cook over medium heat, stirring and adding remaining " +
                "1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened " +
                "and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\n" +
                "        Divide pasta among plates. Top with fried capers.\n";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The first thing we do is Souschef specific:
        // generate pipeline for creating annotations in separate thread.

        Communicator.createAnnotationPipelines();

        /*
         * The {@link ViewPager} that will host the section contents.
         * This variable is located here to minimize scope.
         * In case it is needed outside onCreate,
         * it is no problem to move it outside (private)
         *
         * Same for mSectionsPagerAdapter
         */
        ViewPager mViewPager;

        super.onCreate(savedInstanceState);
        // TODO: Change back to the correct view
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());
        mViewPager.setVisibility(View.GONE);

        // Set up the TabLayout to follow the ViewPager.
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setVisibility(View.GONE);

        //TODO: Update the following
        //Should only start in response to PLUGIN_ACTION in production
        //This means the else case should be omitted


        String inputText = "";
        ExtractedText extractedText = null;
        /*
         * Handle Aurora starting the Plugin.
         */
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {


            // TODO remove this if statement probably. Is currently used to handle cases where a
            // plain String is sent instead of an ExtractedText
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
            }

            // TODO Souschef should probably take an ExtracttedText as input instead of just a String
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                String inputTextJSON = intentThatStartedThisActivity.getStringExtra(
                        Constants.PLUGIN_INPUT_EXTRACTED_TEXT);
                extractedText = ExtractedText.fromJson(inputTextJSON);


            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                // TODO handle a PluginObject that was cached
                String inputTextJSON = intentThatStartedThisActivity.getStringExtra(
                        Constants.PLUGIN_INPUT_OBJECT);
                Recipe receivedObject = PluginObject.fromJson(inputTextJSON, Recipe.class);
                // TODO catch if the receivedObject was not able to be de-JSONed.
                // Waiting for auroralib update for this.
                SouschefInit init = new SouschefInit("I don't think this text is important");
                init.initiateWithCachedObject((Recipe) receivedObject);
            }

        } else {
            inputText = getText();
        }
        if (extractedText != null) {
            // maybe in production this should always be the case
            // and the else should throw an error or let the user know that extracting text failed
            (new SouschefInit(extractedText)).execute();
        } else {

            (new SouschefInit(inputText)).execute();
        }
    }

    @Override
    public void onAmountOfPeopleChanged(int newAmount) {
        ((Tab3Steps) mSectionsPagerAdapter.getItem(TAB_STEPS)).setText("" + newAmount);
    }

    class ProgressUpdate extends AsyncTask<Void, Integer, Void> {
        private ProgressBar pb = null;
        private TextView tv = null;

        @Override
        protected Void doInBackground(Void... voids) {
            int upTime = 0;
            boolean isLoading = true;
            try {
                while (isLoading) {
                    Thread.sleep(MILLIS_BETWEEN_UPDATES);
                    upTime += MILLIS_BETWEEN_UPDATES;
                    publishProgress(Communicator.getProgressAnnotationPipelines());
                    if (Communicator.getProgressAnnotationPipelines() >= DETECTION_STEPS) {
                        isLoading = false;
                    }
                    if (upTime > MAX_WAIT_TIME) {
                        isLoading = false;
                    }
                }
            } catch (InterruptedException e) {
                Log.e("THREAD", "Caught interruptedException in MainActivity");
                Thread.currentThread().interrupt();

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb = findViewById(R.id.pb_loading_screen);
            tv = findViewById(R.id.tv_loading_text);

        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values[0] >= STEPS.length) {
                return;
            }
            tv.setText(STEPS[values[0]]);
            pb.setProgress(PROGRESS_PER_STEP * (values[0] + 1));
        }
    }

    class SouschefInit extends AsyncTask<Void, String, Recipe> {
        private String mText;
        private ExtractedText mExtractedText = null;

        protected SouschefInit(String text) {
            mText = text;
        }

        protected SouschefInit(ExtractedText text) {
            mExtractedText = text;
        }

        protected void initiateWithCachedObject(Recipe recipe) {
            onPostExecute(recipe);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView tv = findViewById(R.id.tv_loading_text);
            tv.setText("Initializing...");
            (new ProgressUpdate()).execute();
        }

        @Override
        protected Recipe doInBackground(Void... voids) {
            // Progressupdates are in demostate

            Communicator comm = Communicator.createCommunicator(mContext);

            // update 1:
            publishProgress("Loading the magic important stuff...");
            try {
                Recipe processedRecipe;
                if (mExtractedText == null) {
                    processedRecipe = comm.process(mText);
                } else {
                    processedRecipe = comm.process(mExtractedText);
                }
                publishProgress("Done!");
                return processedRecipe;
            } catch (RecipeDetectionException e) {
                runOnUiThread(() ->
                        Toast.makeText(mContext, "Representation failed because " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());

            }
            return null;


        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            TextView tv = findViewById(R.id.tv_loading_text);
            tv.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Recipe recipe) {
            if (recipe != null) {
                super.onPostExecute(recipe);

                // get fields to update visibility
                AppBarLayout appBarLayout = findViewById(R.id.appbar);
                ViewPager mViewPager = findViewById(R.id.container);
                TabLayout tabLayout = findViewById(R.id.tabs);
                ConstraintLayout cl = findViewById(R.id.cl_loading_screen);

                // Load recipe in the user interface
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), recipe);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                // update visibilities
                cl.setVisibility(View.GONE);
                appBarLayout.setVisibility(View.VISIBLE);
                mViewPager.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
            }


        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Recipe mRecipe = null;
        private Tab1Overview mTab1Overview = null;
        private Tab2Ingredients mTab2Ingredients = null;
        private Tab3Steps mTab3Steps = null;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public SectionsPagerAdapter(FragmentManager fm, Recipe recipe) {
            super(fm);
            mRecipe = recipe;

            mTab1Overview = new Tab1Overview();
            mTab1Overview.setRecipe(recipe);

            mTab2Ingredients = new Tab2Ingredients();
            mTab2Ingredients.setRecipe(recipe);
            mTab2Ingredients.setOnAmountOfPeopleChangedListener(mOnAmountOfPeopleChangedListener);

            mTab3Steps = new Tab3Steps();
            mTab3Steps.setRecipe(recipe);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tempFrag;
            switch (position) {
                case TAB_OVERVIEW:
                    tempFrag = mTab1Overview;
                    break;
                case TAB_INGREDIENTS:
                    tempFrag = mTab2Ingredients;
                    break;
                case TAB_STEPS:
                    tempFrag = mTab3Steps;
                    break;
                default:
                    tempFrag = null;
            }
            return tempFrag;
        }

        @Override
        public int getCount() {
            // Show 3 total tabs.
            return NUMBER_OF_TABS;
        }

        @Override

        public CharSequence getPageTitle(int position) {
            String tabName;
            switch (position) {
                case TAB_OVERVIEW:
                    tabName = getString(R.string.overview);
                    break;
                case TAB_INGREDIENTS:
                    tabName = getString(R.string.ingredients);
                    break;
                case TAB_STEPS:
                    tabName = getString(R.string.steps);
                    break;
                default:
                    // this should not happen
                    tabName = null;
                    break;
            }
            return tabName;
        }
    }
}



