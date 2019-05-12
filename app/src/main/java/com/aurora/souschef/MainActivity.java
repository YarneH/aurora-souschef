package com.aurora.souschef;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Recipe;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.BufferedReader;
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
    private RecipeViewModel mRecipeViewModel;

    public MainActivity() {
        // Default constructor
    }

    /**
     * Overwritten method of Activity
     *
     * @param savedInstanceState The saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

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
        if (mRecipeViewModel.isBeingProcessed()) {
            return;
        }
        mRecipeViewModel.setBeingProcessed(true);
        Log.d(TAG, "setup");
        Intent intentThatStartedThisActivity = getIntent();

        boolean intentIsOkay = true;

        if (intentThatStartedThisActivity.getAction() == null) {
            Toast.makeText(this, "ERROR: The intent had no action.",
                    Snackbar.LENGTH_LONG).show();
            intentIsOkay = false;
        } else if (!intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            Toast.makeText(this, "ERROR: The intent had incorrect action.",
                    Snackbar.LENGTH_LONG).show();
            intentIsOkay = false;
        } else if (!intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TYPE)) {
            Toast.makeText(this, "ERROR: The intent had no specified input type.",
                    Snackbar.LENGTH_LONG).show();
            intentIsOkay = false;
        }

        if (intentIsOkay) {
            handleIntentThatOpenedPlugin(intentThatStartedThisActivity);
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
     * Sets up the observation of the recipeviewmodel
     */
    private void setUpRecipeDataObject() {
        mRecipeViewModel.getProgressStep().observe(this, (Integer step) -> {
                    ProgressBar pb = findViewById(R.id.pb_loading_screen);
                    pb.setProgress(mRecipeViewModel.getProgress());

                    // TODO: set TextView to visualize progress
                }
        );
        mRecipeViewModel.getInitialised().observe(this, (Boolean isInitialised) -> {
            if (isInitialised == null) {
                return;
            }
            if (!isInitialised) {
                showProgress();
                return;
            }
            hideProgress();
        });
        mRecipeViewModel.getProcessFailed().observe(this, (Boolean failed) -> {
            if (failed != null && failed) {
                Toast.makeText(this, "Detection failed: " +
                                mRecipeViewModel.getFailureMessage().getValue(),
                        Toast.LENGTH_LONG).show();
                ProgressBar pb = findViewById(R.id.pb_loading_screen);
                pb.setProgress(0);
            }
        });
        mRecipeViewModel.getDefaultAmountSet().observe(this, (Boolean set) -> {
            if (set != null && set) {
                Toast.makeText(this, "Amount of servings not found. Default (4) is set!",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Initializes mRecipe according to the parameters in the Intent that opened the plugin
     *
     * @param intentThatStartedThisActivity Intent that opened the plugin
     */
    private void handleIntentThatOpenedPlugin(Intent intentThatStartedThisActivity) {
        // Get the Uri to the transferred file
        Uri fileUri = intentThatStartedThisActivity.getData();
        if (fileUri == null) {
            Toast.makeText(this, "ERROR: The intent had no url in the data field",
                    Snackbar.LENGTH_LONG).show();
        } else {
            // Get the input type
            String inputType = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TYPE);
            // Switch on the different kinds of input types that could be in the temp file
            switch (inputType) {
                case Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT:
                    // Convert the read file to an ExtractedText object
                    convertReadFileToExtractedText(fileUri);
                    break;
                case Constants.PLUGIN_INPUT_TYPE_OBJECT:
                    // Convert the read file to an PluginObject
                    convertReadFileToRecipe(fileUri);
                    break;
                default:
                    Toast.makeText(this, "ERROR: The intent had an unsupported input type.",
                            Snackbar.LENGTH_LONG).show();
                    Log.d(TAG, "Loading plain default text (getText())");
                    mRecipeViewModel.initialiseWithPlainText(getText());
            }
        }
    }

    /**
     * Hardcoded recipe with extracted text and annotations
     *
     * @return the json of the annotated extracted text
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
            Log.e("MAIN", "opening default file failed", e);
        }
        Log.d("read", bld.toString());
        return bld.toString();
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
     * Convert the read file to an ExtractedText object
     *
     * @param fileUri Uri to the file
     */
    private void convertReadFileToExtractedText(Uri fileUri) {
        try {
            ExtractedText extractedText = ExtractedText.getExtractedTextFromFile(fileUri,
                    this);
            if (extractedText != null) {
                Log.i(TAG, "Loading extracted text.");
                mRecipeViewModel.initialiseWithExtractedText(extractedText);
            } else {
                // Error in case ExtractedText was null.
                Log.e(TAG, "ExtractedText-object was null.");
            }
        } catch (IOException e) {
            Log.e(TAG,
                    "IOException while loading data from aurora", e);
        }
    }

    /**
     * Convert the read file to an PluginObject
     *
     * @param fileUri Uri to the file
     */
    private void convertReadFileToRecipe(Uri fileUri) {
        try {
            Recipe receivedObject = Recipe.getPluginObjectFromFile(fileUri, this,
                    Recipe.class);

            Log.i(TAG, "Loading cashed Object.");
            mRecipeViewModel.initialiseWithRecipe(receivedObject);

        } catch (IOException e) {
            Log.e(TAG, "IOException while loading data from aurora", e);
        }
    }

    /**
     * A {@link FragmentStatePagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

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




