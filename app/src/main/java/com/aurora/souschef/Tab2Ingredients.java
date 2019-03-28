package com.aurora.souschef;

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

import com.aurora.souschefprocessor.recipe.Recipe;

/**
 * Class defining the functionality of the ingredients tab.
 */
public class Tab2Ingredients extends Fragment {
    private Recipe mRecipe = null;
    private TextView mAmountTextView = null;
    private IngredientAdapter mIngredientAdapter = null;
    private OnAmountOfPeopleChangedListener mOnAmountOfPeopleChangedListener = null;

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
        mIngredientAdapter = new IngredientAdapter(mRecipe.getIngredients(), mActualNumberOfPeople);
        mIngredientList.setAdapter(mIngredientAdapter);

        // Prepare parts for amount of people
        mAmountTextView = rootView.findViewById(R.id.tv_amount_people);
        mAmountTextView.setText(String.valueOf(mActualNumberOfPeople));

        ImageButton addButton = rootView.findViewById(R.id.btn_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAmountOfServings(+1);
            }
        });

        ImageButton minusButton = rootView.findViewById(R.id.btn_minus);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAmountOfServings(-1);
            }
        });

        return rootView;
    }

    protected void setRecipe(Recipe recipe) {
        mRecipe = recipe;
        mActualNumberOfPeople = recipe.getNumberOfPeople();
    }

    private void changeAmountOfServings(int difference) {
        if (mActualNumberOfPeople + difference > 0) {
            mActualNumberOfPeople += difference;
            mAmountTextView.setText(String.valueOf(mActualNumberOfPeople));
            mIngredientAdapter.setChoseAmountOfServings(mActualNumberOfPeople);
            mIngredientAdapter.notifyDataSetChanged();
        }
        mOnAmountOfPeopleChangedListener.onAmountOfPeopleChanged(mActualNumberOfPeople);
    }

    protected interface OnAmountOfPeopleChangedListener {
        void onAmountOfPeopleChanged(int newAmount);
    }

    protected void setmOnAmountOfPeopleChangedListener(OnAmountOfPeopleChangedListener listener) {
        mOnAmountOfPeopleChangedListener = listener;
    }
}
