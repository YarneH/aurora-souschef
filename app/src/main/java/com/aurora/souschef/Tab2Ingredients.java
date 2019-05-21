package com.aurora.souschef;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the ingredients tab.
 */
public class Tab2Ingredients extends Fragment {
    /**
     * Used to access recipe data.
     */
    private RecipeViewModel mRecipe = null;
    /**
     * Holds the amount of people the user is cooking for.
     */
    private TextView mAmountTextView = null;
    /**
     * Adapter for the ingredient list.
     */
    private IngredientAdapter mIngredientAdapter = null;
    /**
     * Holds the ingredients.
     */
    private RecyclerView mIngredientList = null;
    /**
     * Define the preferences category for the settings
     */
    public static final String SETTINGS_PREFERENCES = "Settings";
    /**
     * The key value for storing whether the recipe is displayed in imperial or metric units.
     */
    public static final String IMPERIAL_SETTING = "imperial";
    /**
     * The card containing the settings. Is GONE by default.
     */
    private CardView mSettingsCard = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate fragment layout.
        View rootView = inflater.inflate(R.layout.tab_2_ingredients, container, false);


        // Add the floating action button to change the settings.
        FloatingActionButton mSettingsFab = rootView.findViewById(R.id.fab_settings);
        mSettingsFab.setOnClickListener(v -> onSettingsClicked());

        // Get the card and make it disappear on clicking outside of it.
        mSettingsCard = rootView.findViewById(R.id.cv_settings);
        rootView.setOnClickListener(v -> mSettingsCard.setVisibility(View.GONE));

        // Switch for changing the units.
        Switch mToggleImperial = rootView.findViewById(R.id.switch_toggle_imperial);

        // Change the settings in sharedPreferences.
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        boolean imperial = sharedPreferences.getBoolean(IMPERIAL_SETTING, false);
        mToggleImperial.setChecked(imperial);
        mToggleImperial.setOnCheckedChangeListener((v, isChecked) -> onSwitchToggled(isChecked));

        // Setup recycler view.
        mIngredientList = rootView.findViewById(R.id.rv_ingredient_list);
        mIngredientList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Prepare parts for amount of people
        mAmountTextView = rootView.findViewById(R.id.tv_amount_people);

        ImageButton addButton = rootView.findViewById(R.id.btn_add);
        addButton.setOnClickListener(view -> mRecipe.incrementPeople());

        ImageButton minusButton = rootView.findViewById(R.id.btn_minus);
        minusButton.setOnClickListener(view -> mRecipe.decrementPeople());

        mRecipe = ViewModelProviders.of(requireActivity()).get(RecipeViewModel.class);
        mRecipe.getRecipe().observe(this, (Recipe recipe) -> {
            if (recipe == null) {
                return;
            }
            // Feed Adapter
            mIngredientAdapter = new IngredientAdapter(recipe.getIngredients(), recipe.getNumberOfPeople());
            mIngredientList.setAdapter(mIngredientAdapter);
        });
        mRecipe.getNumberOfPeople().observe(this, (Integer numberOfPeople) -> {
                    if (numberOfPeople == null) {
                        return;
                    }

                    mAmountTextView.setText(String.valueOf(numberOfPeople));
                    if (mIngredientAdapter != null) {
                        mIngredientAdapter.setChoseAmountOfServings(numberOfPeople);
                        mIngredientAdapter.notifyDataSetChanged();
                    }
                }
        );
        return rootView;
    }

    /**
     * Handle what happens on clicking the settings-FAB.
     */
    private void onSettingsClicked() {
        if (mSettingsCard.getVisibility() == View.VISIBLE) {
            mSettingsCard.setVisibility(View.GONE);
        } else {
            mSettingsCard.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Change the sharedPreferences on toggling the switch to imperial and back.
     *
     * @param isChecked the toggled state
     */
    private void onSwitchToggled(boolean isChecked) {
        SharedPreferences sharedPreferences = requireActivity().
                getSharedPreferences(SETTINGS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IMPERIAL_SETTING, isChecked);
        editor.apply();
    }
}
