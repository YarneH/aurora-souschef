package com.aurora.souschef;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate fragment layout.
        View rootView = inflater.inflate(R.layout.tab_2_ingredients, container, false);

        // Setup recycler view.
        mIngredientList = rootView.findViewById(R.id.rv_ingredient_list);
        mIngredientList.setLayoutManager(new LinearLayoutManager(this.getContext()));

        // Prepare parts for amount of people
        mAmountTextView = rootView.findViewById(R.id.tv_amount_people);

        ImageButton addButton = rootView.findViewById(R.id.btn_add);
        addButton.setOnClickListener(view -> mRecipe.incrementPeople());

        ImageButton minusButton = rootView.findViewById(R.id.btn_minus);
        minusButton.setOnClickListener(view -> mRecipe.decrementPeople());

        mRecipe = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        mRecipe.getRecipe().observe(this, recipe1 -> {
            if (recipe1 == null) {
                return;
            }
            // Feed Adapter
            mIngredientAdapter = new IngredientAdapter(recipe1.getIngredients(), recipe1.getNumberOfPeople());
            mIngredientList.setAdapter(mIngredientAdapter);
        });
        mRecipe.getNumberOfPeople().observe(this, integer -> {
                    if (integer == null) {
                        return;
                    }
                    mAmountTextView.setText(String.valueOf(integer));
                    mIngredientAdapter.setChoseAmountOfServings(integer);
                    mIngredientAdapter.notifyDataSetChanged();
                }
        );
        return rootView;
    }
}
