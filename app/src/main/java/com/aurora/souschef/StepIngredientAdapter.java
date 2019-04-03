package com.aurora.souschef;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.souschef.utilities.StringUtilities;
import com.aurora.souschefprocessor.recipe.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Adapter for populating the ingredient list.
 */
public class StepIngredientAdapter extends RecyclerView.Adapter<StepIngredientAdapter.CardIngredientViewHolder> {
    private final List<Ingredient> ingredients;


    /**
     * Constructs the adapter with a list
     *
     * @param ingredients list for construction
     */
    public StepIngredientAdapter(Set<Ingredient> ingredients) {
        List<Ingredient> tempIngredients = null;
        if (ingredients != null) {
            tempIngredients = new ArrayList<>(ingredients);
        }
        this.ingredients = tempIngredients;
    }

    @NonNull
    @Override
    public CardIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        // TODO: change to R.layout.ingredient_item if a checkbox needs to be added!
        View view = LayoutInflater.from(context).inflate(R.layout.step_ingredient_item, viewGroup, false);

        return new CardIngredientViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull CardIngredientViewHolder cardIngredientViewHolder, int i) {
        cardIngredientViewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        if (ingredients == null) {
            return 0;
        } else {
            return ingredients.size();
        }
    }

    public class CardIngredientViewHolder extends RecyclerView.ViewHolder {
        private int index;
        private TextView mIngredientName;
        private TextView mIngredientAmount;
        private TextView mIngredientUnit;

        /**
         * Initialises views inside the layout.
         *
         * @param itemView containing view
         * @param index    index in the array of ingredients
         */
        public CardIngredientViewHolder(@NonNull View itemView, final int index) {
            super(itemView);
            mIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            mIngredientAmount = itemView.findViewById(R.id.tv_ingredient_amount);
            mIngredientUnit = itemView.findViewById(R.id.tv_ingredient_unit);
        }

        /**
         * populate individual views with the correct data
         *
         * @param i what value in the list of ingredients to bind to this card.
         */
        private void bind(int i) {
            this.index = i;
            Ingredient ingredient = ingredients.get(this.index);

            String nameWithoutQuantityAndUnit = ingredient.getName();
            // if it is possible to capitalize the first letter, capitalize.
            if (nameWithoutQuantityAndUnit.length() > 1) {
                nameWithoutQuantityAndUnit = nameWithoutQuantityAndUnit.substring(0, 1).toUpperCase(Locale.getDefault())
                        + nameWithoutQuantityAndUnit.substring(1);
            }

            mIngredientName.setText(nameWithoutQuantityAndUnit);
            mIngredientAmount.setText(StringUtilities.toDisplayQuantity(ingredient.getAmount().getValue()));
            mIngredientUnit.setText(ingredient.getAmount().getUnit());
        }
    }
}
