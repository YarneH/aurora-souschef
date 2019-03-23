package com.aurora.souschef;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.ListIngredient;

import java.util.List;

/**
 * Adapter for populating the ingredient list.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.CardIngredientViewHolder> {
    private final List<ListIngredient> ingredients;

    /**
     * Constructs the adapter with a list
     *
     * @param ingredients list for construction
     */
    public IngredientAdapter(List<ListIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public CardIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.ingredient_item, viewGroup, false);
        return new CardIngredientViewHolder(view, i);
    }

    @Override
    public void onBindViewHolder(@NonNull CardIngredientViewHolder cardIngredientViewHolder, int i) {
        cardIngredientViewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
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

            ListIngredient ingredient = ingredients.get(this.index);
            mIngredientName.setText(ingredient.getName());
            mIngredientAmount.setText("" + ingredient.getAmount().getValue());
            mIngredientUnit.setText(ingredient.getAmount().getUnit());
        }
    }
}
