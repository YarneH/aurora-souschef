package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the recipe steps tab.
 */
public class Tab3Steps extends Fragment {
    private static final int TIMER_MARGIN = 10;

    private static final String[] DUMMY_STEPS = {
            "Take the food out of the package",
            "Put the food in the microwave",
            "Serve the hot food on a plate",
            "Enjoy your meal!"
    };

    private static final int[] DUMMY_TIMER_LOWER = {
            60,
            180,
            30,
            3600,
            60,
            180,
            30,
            3600
    };

    private static final int[] DUMMY_TIMER_UPPER = {
            120,
            200,
            45,
            4000,
            60,
            180,
            30,
            3600,
    };
    private static Recipe mRecipe = null;
    private static String[] mDescriptionSteps = null;
    private StepsPagerAdapter mStepsPagerAdapter;
    private ViewPager mViewPager;

    public Tab3Steps() {
        // Default constructor
    }

    /**
     * Classic setter for the Recipe, used to communicate the recipe from the Main Activity
     */
    protected static void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    /**
     * Takes the recipe and transforms it into String-representations used for the TextViews
     * TODO Upgrade this function (timers, step ingredient,...)
     */
    private static void prepareRecipeParts() {
        int stepsCount = mRecipe.getRecipeSteps().size();
        mDescriptionSteps = new String[stepsCount];

        for (int i = 0; i < stepsCount; i++) {
            mDescriptionSteps[i] = mRecipe.getRecipeSteps().get(i).getDescription();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prepareRecipeParts();

        View rootView = inflater.inflate(R.layout.tab_3_steps, container, false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mStepsPagerAdapter = new StepsPagerAdapter(getActivity().getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_steps);
        mViewPager.setAdapter(mStepsPagerAdapter);

        // Prevent ViewPager from resetting timers
        mViewPager.setOffscreenPageLimit(mStepsPagerAdapter.getCount());

        return rootView;
    }

    /**
     * A placeholder fragment containing the view of a step of the recipe
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
            // Empty constructor (generated by Android Studio)
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int index = getArguments().getInt(ARG_SECTION_NUMBER);

            // Inflate the CardView
            View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

            // Set the TextViews
            TextView titleTextView = (TextView) rootView.findViewById(R.id.section_label);
            TextView stepTextView = (TextView) rootView.findViewById(R.id.tv_detail);
            titleTextView.setText(getString(R.string.section_format, index + 1));
            stepTextView.setText(mDescriptionSteps[index]);

            // Inflate and save the Timers
            // TODO: Add loop for more timers
            View timerView = inflater.inflate(R.layout.timer_card, null);

            // Create the UITimer, which handles all the clicks by himself
            UITimer uiTimer = new UITimer(DUMMY_TIMER_LOWER[index],
                    DUMMY_TIMER_UPPER[index], timerView.findViewById(R.id.tv_timer));
            uiTimer.setOnClickListeners();

            // Set the right layoutparams for the timerView
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(0, TIMER_MARGIN, 0, TIMER_MARGIN);

            // Add the timerView to the LinearLayout
            ViewGroup insertPoint = (ViewGroup) rootView.findViewById(R.id.ll_step);
            insertPoint.addView(timerView, insertPoint.getChildCount(), layoutParams);

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class StepsPagerAdapter extends FragmentPagerAdapter {

        public StepsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Return total pages.
            return mDescriptionSteps.length;
        }
    }
}
