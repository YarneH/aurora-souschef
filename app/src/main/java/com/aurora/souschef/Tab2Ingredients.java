package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the ingredients tab.
 */
public class Tab2Ingredients extends Fragment {
    private Recipe mRecipe = null;
    // The number of people the user picked.
    private int mActualNumberOfPeople = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate fragment layout.
        View rootView = inflater.inflate(R.layout.tab_2_ingredients, container, false);

        // Setup recycler view.
        RecyclerView mIngredientList = rootView.findViewById(R.id.rv_ingredient_list);
        mIngredientList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        // Feed Adapter
        IngredientAdapter ingredientAdapter = new IngredientAdapter(mRecipe.getIngredients());
        mIngredientList.setAdapter(ingredientAdapter);
        return rootView;
    }

    protected void setRecipe(Recipe recipe) {
        mRecipe = recipe;
        mActualNumberOfPeople = recipe.getNumberOfPeople();

    }
}
