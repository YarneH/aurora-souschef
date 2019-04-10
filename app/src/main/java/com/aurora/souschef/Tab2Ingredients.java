package com.aurora.souschef;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the ingredients tab.
 */
public class Tab2Ingredients extends Fragment {
    private RecipeViewModel mRecipe = null;
    private TextView mAmountTextView = null;
    private IngredientAdapter mIngredientAdapter = null;
    private OnAmountOfPeopleChangedListener mOnAmountOfPeopleChangedListener = null;
    private RecyclerView mIngredientList = null;

    // The number of people the user picked.
    private int mActualNumberOfPeople = 0;

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
                    mActualNumberOfPeople = integer;
                    mAmountTextView.setText(String.valueOf(mActualNumberOfPeople));
                    mIngredientAdapter.setChoseAmountOfServings(integer);
                    mIngredientAdapter.notifyDataSetChanged();
                }
        );
        return rootView;
    }

    private void changeAmountOfServings() {
        mAmountTextView.setText(String.valueOf(mActualNumberOfPeople));
        mIngredientAdapter.setChoseAmountOfServings(mActualNumberOfPeople);
        mIngredientAdapter.notifyDataSetChanged();
        mOnAmountOfPeopleChangedListener.onAmountOfPeopleChanged(mActualNumberOfPeople);
    }

    protected interface OnAmountOfPeopleChangedListener {
        void onAmountOfPeopleChanged(int newAmount);
    }

    protected void setmOnAmountOfPeopleChangedListener(OnAmountOfPeopleChangedListener listener) {
        mOnAmountOfPeopleChangedListener = listener;
    }
}
