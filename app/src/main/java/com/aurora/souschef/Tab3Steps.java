package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the recipe steps tab.
 */
public class Tab3Steps extends Fragment {
    private static Recipe mRecipe = null;
    private static String[] mDescriptionSteps = null;
    private StepsPagerAdapter mStepsPagerAdapter;
    private ViewPager mViewPager;
    private int mAmountPeople;

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

    protected void setNewAmount(int newAmount) {
        // mStepsPagerAdapter.updateFragments(newAmount);
    }

    /**
     * A placeholder fragment containing the view of a step of the recipe
     */

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class StepsPagerAdapter extends FragmentPagerAdapter {
        private StepPlaceholderFragment[] mFragments = new StepPlaceholderFragment[mDescriptionSteps.length];

        public StepsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a StepPlaceholderFragment (defined as a static inner class below).
            StepPlaceholderFragment fragment = StepPlaceholderFragment.newInstance(mRecipe, position, getCount());
            mFragments[position] = fragment;
            return fragment;
        }

        @Override
        public int getCount() {
            // Return total pages.
            return mDescriptionSteps.length;
        }

        protected void updateFragments(int newAmount) {
            for (StepPlaceholderFragment fragment : mFragments) {
                fragment.update(newAmount);
            }
        }
    }
}
