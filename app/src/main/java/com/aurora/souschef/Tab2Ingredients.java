package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the ingredients tab.
 */
public class Tab2Ingredients extends Fragment {
    private Recipe mRecipe = null;
    private TextView mTextViewQuantity = null;
    private TextView mTextViewName = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_2_ingredients, container, false);
        mTextViewQuantity = rootView.findViewById(R.id.tv_quantity);
        mTextViewName = rootView.findViewById(R.id.tv_name);
        setText();

        return rootView;
    }

    protected void setRecipe(Recipe recipe) {
        mRecipe = recipe;
    }

    // Takes the ingredients and puts them on the TextView
    private void setText() {
        StringBuilder builderQuantity = new StringBuilder();
        StringBuilder builderName = new StringBuilder();

        for (ListIngredient ingredient : mRecipe.getIngredients()) {
            // Build the string for the quantities
            if (ingredient.getValue() == (long) ingredient.getValue()) {
                builderQuantity.append(String.format("%d", (long) ingredient.getValue()));
            } else {
                builderQuantity.append(String.format("%.2f", ingredient.getValue()));
            }
            builderQuantity.append('\n');

            // Build the string for the units and names
            if (!"".equals(ingredient.getUnit())) {
                builderName.append(ingredient.getUnit())
                        .append(" ");
            }
            builderName
                    .append(ingredient.getName())
                    .append('\n');
        }
        mTextViewQuantity.setText(builderQuantity.toString());
        mTextViewName.setText(builderName.toString());
    }
}
