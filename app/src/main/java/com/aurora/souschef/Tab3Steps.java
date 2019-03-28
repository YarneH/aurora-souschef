package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.Recipe;
import com.aurora.souschefprocessor.recipe.RecipeTimer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private TextView mDummyTextView;
    private View mView;

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
        mDummyTextView = rootView.findViewById(R.id.tv_dummy);

        Log.d("Test","onCreateView " + this);
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

    protected void setText(String newText) {
        ((TextView) getView().findViewById(R.id.tv_dummy)).setText(newText);

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

            // Inflate the CardView and get the View
            View rootView = inflater.inflate(R.layout.fragment_steps, container, false);
            TextView titleTextView = (TextView) rootView.findViewById(R.id.tv_title);

            // Set the title TextViews
            titleTextView.setText(getString(R.string.section_format, index + 1));

            // Add Text and Timer
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, TIMER_MARGIN, 0, TIMER_MARGIN);
            ViewGroup insertPoint = (ViewGroup) rootView.findViewById(R.id.ll_step);
            int currentPosition = 0;

            for (RecipeTimer timer : mRecipe.getRecipeSteps().get(index).getRecipeTimers()) {
                // Inflate the layout of a text and a timer
                View timerView = inflater.inflate(R.layout.timer_card, null);
                TextView textView = (TextView) inflater.inflate(R.layout.step_textview, null);

                // Set Text of the TextView
                int tempPosition = timer.getPosition().getEndIndex();
                String currentSubstring = mDescriptionSteps[index].substring(currentPosition, tempPosition);
                Pattern p = Pattern.compile("\\p{Alpha}");
                Matcher m = p.matcher(currentSubstring);
                if (m.find()) {
                    textView.setText(currentSubstring.substring(m.start()));
                }

                // Create a UITimer and set its on click listeners
                UITimer uiTimer = new UITimer(timer, timerView.findViewById(R.id.tv_timer));
                uiTimer.setOnClickListeners();

                // Add the timer to the LinearLayout
                insertPoint.addView(textView, insertPoint.getChildCount(), layoutParams);
                insertPoint.addView(timerView, insertPoint.getChildCount(), layoutParams);

                // Set the current position to the temporary position
                currentPosition = tempPosition;
            }

            // Check if there is still some text coming after the last timer
            if (currentPosition != mDescriptionSteps[index].length()) {
                TextView textView = (TextView) inflater.inflate(R.layout.step_textview, null);
                String currentSubstring = mDescriptionSteps[index].substring(currentPosition);
                Pattern p = Pattern.compile("\\p{Alpha}");
                Matcher m = p.matcher(mDescriptionSteps[index].substring(currentPosition));
                if (m.find()) {
                    textView.setText(currentSubstring.substring(m.start()));
                }

                insertPoint.addView(textView, insertPoint.getChildCount(), layoutParams);
            }

            // Add all timers to the view
            // Set the right layoutparams for the timerView
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
