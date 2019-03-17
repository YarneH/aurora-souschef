package com.aurora.souschef;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
         * The {@link ViewPager} that will host the section contents.
         * This variable is located here to minimize scope.
         * In case it is needed outside onCreate,
         * it is no problem to move it outside (private)
         *
         * Same for mSectionsPagerAdapter
         */
        ViewPager mViewPager;
        SectionsPagerAdapter mSectionsPagerAdapter;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout to follow the ViewPager.
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment tabFragment = null;
            switch (position) {
                case TAB_OVERVIEW:
                    tabFragment = new Tab1Overview();
                    break;
                case TAB_INGREDIENTS:
                    tabFragment = new Tab2Ingredients();
                    break;
                case TAB_STEPS:
                    tabFragment = new Tab3Steps();
                    break;
                default:
                    tabFragment = null;
                    break;
            }
            return tabFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total tabs.
            return NUMBER_OF_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String tabName = null;
            switch (position) {
                case TAB_OVERVIEW:
                    tabName = getString(R.string.overview);
                case TAB_INGREDIENTS:
                    tabName = getString(R.string.ingredients);
                case TAB_STEPS:
                    tabName = getString(R.string.steps);
                default:
                    // this should not happen
                    tabName = null;
            }
            return tabName;
        }
    }
}
