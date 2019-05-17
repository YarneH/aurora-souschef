package com.aurora.souschef;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the overview tab.
 */
public class Tab1Overview extends Fragment {

    /**
     * Define the preferences category for the settings
     */
    public static final String SETTINGS_PREFERENCES = "Settings";
    /**
     * The key value for storing whether the recipe is displayed in imperial or metric units.
     */
    public static final String IMPERIAL_SETTING = "imperial";

    /**
     * Contains the description of a recipe.
     */
    private TextView mDescriptionTextView = null;
    /**
     * The card containing the settings. Is GONE by default.
     */
    private CardView mSettingsCard = null;


    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_1_overview, container, false);
        mDescriptionTextView = rootView.findViewById(R.id.tv_recipe_description);

        // Listen for recipe changes.
        RecipeViewModel mRecipe = ViewModelProviders.of(requireActivity()).get(RecipeViewModel.class);
        mRecipe.getRecipe().observe(this, (Recipe recipe) -> {
            if (recipe == null) {
                return;
            }
            mDescriptionTextView.setText(recipe.getDescription());
            mDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
        });
        return rootView;
    }
}
