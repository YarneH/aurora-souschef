package com.aurora.souschef;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class StepsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static ArrayList<CardView> mCardViewTimers = new ArrayList<>();
    private static ArrayList<CountDownTimer> mCountDownTimers = new ArrayList<>();

    private static final String[] DUMMY_STEPS = {
            "Take the food out of the package",
            "Put the food in the microwave",
            "Serve the hot food on a plate",
            "Enjoy your meal!"};
    private static final int[] DUMMY_TIMER = {
            60,
            180,
            30,
            3600};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Prevent ViewPager from resetting timers
        mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int index = getArguments().getInt(ARG_SECTION_NUMBER);

            // Inflate the CardView
            View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

            // Set the TextViews
            TextView titleTextView = (TextView) rootView.findViewById(R.id.section_label);
            TextView stepTextView = (TextView) rootView.findViewById(R.id.tv_detail);
            titleTextView.setText(getString(R.string.section_format, index + 1));
            stepTextView.setText(DUMMY_STEPS[index]);

            // Inflate and save the Timers TODO: Add loop for more timers
            View timerView = inflater.inflate(R.layout.timer_card, null);
            ViewGroup insertPoint = (ViewGroup) rootView.findViewById(R.id.ll_step);
            mCardViewTimers.add((CardView) timerView);
            CountDownTimer test = new CountDownTimer(DUMMY_TIMER[index] * 1000, 1000) {
                TextView mTextViewTimer = (TextView) timerView.findViewById(R.id.tv_timer);

                public void onTick(long millisUntilFinished) {
                    // TODO: Add convertTimetoString (in ui-timer branch)
                    mTextViewTimer.setText(String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    mTextViewTimer.setText("Done!");
                }
            };
            mCountDownTimers.add(test);

            timerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    test.start();
                }
            });

            // Add the CardView with the right LayoutParams
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.setMargins(0, 10, 0, 10);
            insertPoint.addView(timerView, 2, layoutParams);

            return rootView;
        }
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Return total pages.
            return DUMMY_STEPS.length;
        }
    }
}
