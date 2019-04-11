package com.aurora.souschef;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.souschefprocessor.recipe.Recipe;

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for logging.
     */
    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * ID of the overview tab.
     */
    private static final int TAB_OVERVIEW = 0;
    /**
     * ID of the ingredients tab.
     */
    private static final int TAB_INGREDIENTS = 1;
    /**
     * ID of the steps tab.
     */
    private static final int TAB_STEPS = 2;
    /**
     * Total number of tabs.
     */
    private static final int NUMBER_OF_TABS = 3;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter = null;
    /**
     * Holds the data of a recipe in a LifeCycle-friendly way.
     */
    private RecipeViewModel mRecipe;

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
                "Preparation \n" +
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

    /**
     * Overwritten method of Activity
     *
     * @param savedInstanceState The saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRecipe = ViewModelProviders.of(this).get(RecipeViewModel.class);

        super.onCreate(savedInstanceState);
        // TODO: Change back to the correct view
        setContentView(R.layout.activity_main);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        showProgress();

        // setup recipe data object (RecipeViewModel).
        mRecipe.getProgressStep().observe(this, (Integer step) -> {
                    ProgressBar pb = findViewById(R.id.pb_loading_screen);
                    pb.setProgress(mRecipe.getProgress());
                    // TODO: set TextView to visualize progress
                }
        );
        mRecipe.getInitialised().observe(this, (Boolean isInitialised) -> {
            if (isInitialised == null) {
                return;
            }
            if (!isInitialised) {
                return;
            }
            hideProgress();
        });

        /*
         * Handle Aurora starting the Plugin.
         * Each if statement calls initialise (with different paraments)
         * on the recipe data object.
         */
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            /*BasicPluginObject basicPluginObject = null
             * TODO remove this if statement probably. Is currently used to handle cases where a
             * plain String is sent instead of an ExtractedText
             */
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                // Plain Text
                String inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                Log.d(TAG, "Loading plain text.");
                mRecipe.initialiseWithPlainText(inputText);

            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                // Extracted Text
                // TODO Souschef should probably take an ExtractedText as input instead of just a String
                String inputTextJSON = intentThatStartedThisActivity.getStringExtra(
                        Constants.PLUGIN_INPUT_EXTRACTED_TEXT);
                ExtractedText extractedText = ExtractedText.fromJson(inputTextJSON);
                if (extractedText != null) {
                    Log.d(TAG, "Loading extracted text.");
                    mRecipe.initialiseWithExtractedText(extractedText);
                } else {
                    // Error in case ExtractedText was null.
                    Log.e(MainActivity.class.getSimpleName(), "ExtractedText-object was null.");
                }

            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                // Cached Object.
                // TODO handle a PluginObject that was cached
                String inputTextJSON = intentThatStartedThisActivity.getStringExtra(
                        Constants.PLUGIN_INPUT_OBJECT);
                Recipe receivedObject = PluginObject.fromJson(inputTextJSON, Recipe.class);
                // TODO catch if the receivedObject was not able to be de-JSONed.
                // Waiting for auroralib update for this.
                Log.d(TAG, "Loading cashed Object.");
                mRecipe.initialiseWithRecipe(receivedObject);
            }

        } else {
            Log.d(TAG, "Loading plain default text (getText())");
            mRecipe.initialiseWithPlainText(getText());
        }
    }

    /**
     * Show the progress-screen.
     */
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

    /**
     * Hide the progress-screen.
     */
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tempFrag;
            switch (position) {
                case TAB_OVERVIEW:
                    tempFrag = new Tab1Overview();
                    break;
                case TAB_INGREDIENTS:
                    tempFrag = new Tab2Ingredients();
                    break;
                case TAB_STEPS:
                    tempFrag = new Tab3Steps();
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




