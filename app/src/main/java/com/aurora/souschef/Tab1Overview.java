package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the overview tab.
 */
public class Tab1Overview extends Fragment {
    private Recipe mRecipe = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_1_overview, container, false);
        TextView textView = rootView.findViewById(R.id.tv_recipe_description);

        textView.setText(mRecipe.getDescription());
        return rootView;
    }

    protected void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }
}
