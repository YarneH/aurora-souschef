package com.aurora.souschef;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.souschefprocessor.recipe.Recipe;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
     * FireBase analytics instance.
     */
    private FirebaseAnalytics mFirebaseAnalytics;

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
     * @return a recipe json
     */

    private String getText() {

        InputStream stream = getResources().openRawResource(R.raw.input);
        StringBuilder bld = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            String line = reader.readLine();
            while (line != null) {
                bld.append(line);

                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e("MAIN", "opening default file failed", e );
        }
        Log.d("read", bld.toString());
        return bld.toString();
    }

    /**
     * Sets up the observation of the recipeviewmodel
     */
    private void setUpRecipeDataObject() {
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
                showProgress();
                return;
            }
            hideProgress();
        });
        mRecipe.getProcessFailed().observe(this, (Boolean failed) -> {
            if (failed != null && failed) {
                Toast.makeText(this, "Detection failed: " +
                                mRecipe.getFailureMessage().getValue(),
                        Toast.LENGTH_LONG).show();
                ProgressBar pb = findViewById(R.id.pb_loading_screen);
                pb.setProgress(0);
            }
        });

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

        // Obtain the FirebaseAnalytics instance.
        // Most of firebase analytics is done automatically.
        // Probably nothing more is needed.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        showProgress();

        // setup recipe data object (RecipeViewModel).
        setUpRecipeDataObject();

        /*
         * Handle Aurora starting the Plugin.
         * Each if statement calls initialise (with different paraments)
         * on the recipe data object.
         */
        if (mRecipe.isBeingProcessed()) {
            return;
        }
        mRecipe.setBeingProcessed(true);
        Log.d(TAG, "setup");
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                // Extracted Text
                //String inputTextJSON = intentThatStartedThisActivity.getStringExtra(
                //        Constants.PLUGIN_INPUT_EXTRACTED_TEXT);

                // Get the Uri to the transferred file
                Uri fileUri = intentThatStartedThisActivity.getData();

                StringBuilder total = new StringBuilder();
                if(fileUri != null) {
                    // Open the file
                    ParcelFileDescriptor inputPFD = null;
                    try {
                        inputPFD = getContentResolver().openFileDescriptor(fileUri, "r");
                    } catch (FileNotFoundException e) {
                        Log.e("MAIN", "There was a problem receiving the file from " +
                                "the plugin", e);
                    }

                    // Read the file
                    if(inputPFD != null) {
                        InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());
                        BufferedReader r = new BufferedReader(new InputStreamReader(fileStream));
                        try {
                            for (String line; (line = r.readLine()) != null; ) {
                                total.append(line).append('\n');
                            }
                        } catch (IOException e) {
                            Log.e("MAIN", "There was a problem receiving the file from " +
                                    "the plugin", e);
                        }
                    } else {
                        Log.e("MAIN", "There was a problem receiving the file from " +
                                "the plugin");
                    }
                } else {
                    Log.e("MAIN", "There was a problem receiving the file from " +
                            "the plugin");
                }

                // Convert the read file to an ExtractedText object
                ExtractedText extractedText = ExtractedText.fromJson(total.toString());
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
                String recipeJSON = intentThatStartedThisActivity.getStringExtra(
                        Constants.PLUGIN_INPUT_OBJECT);
                Recipe receivedObject = PluginObject.fromJson(recipeJSON, Recipe.class);
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




