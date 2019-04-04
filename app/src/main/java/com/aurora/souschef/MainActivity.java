package com.aurora.souschef;


import android.arch.lifecycle.ViewModelProviders;
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

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.facade.Communicator;
import com.aurora.souschefprocessor.recipe.Recipe;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private static final int TAB_OVERVIEW = 0;
    private static final int TAB_INGREDIENTS = 1;
    private static final int TAB_STEPS = 2;
    private static final int NUMBER_OF_TABS = 3;
    private static final int PROGRESS_PER_STEP = 14;
    private static final int MILLIS_BETWEEN_UPDATES = 500;
    private static final int MAX_WAIT_TIME = 15000;
    private static final int DETECTION_STEPS = 20;
    private static final String[] STEPS = {
            "Initializing...",
            "Creating new pipeline...",
            "Doing smart stuff to understand words...",
            "Understanding sentences...",
            "Revising some stuff",
            "Searching for timers...",
            "Finishing up..."};

    private SectionsPagerAdapter mSectionsPagerAdapter = null;
    private Context mContext = this;
    private RecipeViewModel recipe;

    public MainActivity() {
        // Default constructor
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The first thing we do is Souschef specific:
        // generate pipeline for creating annotations in separate thread.

        recipe = ViewModelProviders.of(this).get(RecipeViewModel.class);

        /*
         * The {@link ViewPager} that will host the section contents.
         * This variable is located here to minimize scope.
         * In case it is needed outside onCreate,
         * it is no problem to move it outside (private)
         *
         * Same for mSectionsPagerAdapter
         */

        super.onCreate(savedInstanceState);
        // TODO: Change back to the correct view
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        showProgress();

        //TODO: Update the following
        //Should only start in response to PLUGIN_ACTION in production
        //This means the else case should be omitted


        String inputText = "";
        /*
         * Handle Aurora starting the Plugin.
         */
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            /*BasicPluginObject basicPluginObject = null;
             * TODO remove this if statement probably. Is currently used to handle cases where a
             * plain String is sent instead of an ExtractedText
             */
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
            }

            // TODO Souschef should probably take an ExtracttedText as input instead of just a String
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                String inputTextJSON = intentThatStartedThisActivity.getStringExtra(
                        Constants.PLUGIN_INPUT_EXTRACTED_TEXT);
                ExtractedText extractedText = ExtractedText.fromJson(inputTextJSON);
                inputText = extractedText.toString();

            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                // TODO handle a PluginObject that was cached
                Log.d("NOT_IMPLEMENTED", "PLUGIN_INPUT_OBJECT needs to be implemented." +
                        "Instead using getText.");
                inputText = getText();
            }

        } else {
            inputText = getText();
        }


//        (new SouschefInit(inputText)).execute();
        recipe.getProgressStep().observe(this, integer -> {
                    ProgressBar pb = findViewById(R.id.pb_loading_screen);
                    pb.setProgress(recipe.getProgress());
                    // TODO: set textfield to visualize progress;
                }
        );
        recipe.getInitialised().observe(this, o -> {
            if (o == null) {
                return;
            }
            if (!o) {
                return;
            }
            Log.d("TEST", "hiding");
            hideProgress();
        });
        recipe.initialise();
    }

    private void showProgress() {
        ViewPager mViewPager;
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setVisibility(View.GONE);

        // Set up the TabLayout to follow the ViewPager.
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setVisibility(View.GONE);

        ConstraintLayout cl = findViewById(R.id.cl_loading_screen);
        cl.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        // get fields to update visibility
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        ViewPager mViewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabs);
        ConstraintLayout cl = findViewById(R.id.cl_loading_screen);

        // Load recipe in the user interface
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // update visibilities
        cl.setVisibility(View.GONE);
        appBarLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private Tab1Overview mTab1Overview = null;
        private Tab2Ingredients mTab2Ingredients = null;
        private Tab3Steps mTab3Steps = null;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

            mTab1Overview = new Tab1Overview();
            mTab2Ingredients = new Tab2Ingredients();
            mTab3Steps = new Tab3Steps();
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

