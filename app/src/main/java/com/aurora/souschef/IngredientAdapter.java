package com.aurora.souschef;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aurora.souschefprocessor.recipe.ListIngredient;

import java.util.List;
import java.util.Locale;

/**
 * Adapter for populating the ingredient list.
 */
public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.CardIngredientViewHolder> {
    private static final int MIN_DENOMINATOR_OF_FRACITONS = 2;
    private static final int MAX_DENOMINATOR_OF_FRACITONS = 10;
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

    public class CardIngredientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final double ROUND_EPSILON = 0.05;

        private int index;
        private TextView mIngredientName;
        private TextView mIngredientAmount;
        private TextView mIngredientUnit;
        private CardView mIngredientCard;

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
            mIngredientCard = itemView.findViewById(R.id.cv_ingredient_item);
            mIngredientCard.setOnClickListener(this);

        }

        /**
         * populate individual views with the correct data
         *
         * @param i what value in the list of ingredients to bind to this card.
         */
        private void bind(int i) {
            this.index = i;
            ListIngredient ingredient = ingredients.get(this.index);

            String nameWithoutQuantityAndUnit = ingredient.getOriginalLineWithoutUnitAndQuantity();
            // if it is possible to capitalize the first letter, capitalize.
            if (nameWithoutQuantityAndUnit.length() > 1) {
                nameWithoutQuantityAndUnit = nameWithoutQuantityAndUnit.substring(0, 1).toUpperCase(Locale.getDefault())
                        + nameWithoutQuantityAndUnit.substring(1);
            }


            mIngredientName.setText(nameWithoutQuantityAndUnit);
            mIngredientAmount.setText(toDisplayQuantity(ingredient.getAmount().getValue()));
            mIngredientUnit.setText(ingredient.getAmount().getUnit());
        }

        @Override
        public void onClick(View v) {
            Snackbar.make(this.itemView, ingredients.get(index).getOriginalLine(), Snackbar.LENGTH_LONG).show();
        }

        /**
         * Generates fraction from double
         *
         * @param quantity double to display
         * @return String containing the resulting quantity.
         */
        private String toDisplayQuantity(double quantity) {
            if (isAlmostInteger(quantity)) {
                return "" + ((int) Math.round(quantity));
            }
            for (int i = MIN_DENOMINATOR_OF_FRACITONS; i <= MAX_DENOMINATOR_OF_FRACITONS; i++) {
                if (isAlmostInteger(quantity * i)) {
                    return "" + ((int) Math.round(quantity * i) + "/" + i);
                }
            }
            return "" + quantity;
        }

        /**
         * returns true if the distance from the nearest int is smaller than {@value ROUND_EPSILON}
         *
         * @param quantity double to check
         * @return true when close enough.
         */
        private boolean isAlmostInteger(double quantity) {
            return Math.abs(Math.round(quantity) - quantity) < ROUND_EPSILON * quantity;
        }
    }
}
