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

import java.util.List;
import java.util.Locale;

/**
 * Adapter for populating the ingredient list.
 */
public class StepIngredientAdapter extends RecyclerView.Adapter<StepIngredientAdapter.CardIngredientViewHolder> {
    /**
     * A list of all the ingredients needed for the current step
     */
    private final List<Ingredient> ingredients;
    /**
     * The current amount of servings, set by the user
     */
    private int mCurrentAmount;
    /**
     * The original amount of servings, extracted from the recipe
     */
    private int mOriginalAmount;
    /**
     * The length of the description of the current step
     */
    private int mStepDescriptionLength;

    /**
     * Constructs the adapter with a list
     *
     * @param ingredients list for construction
     */
    StepIngredientAdapter(List<Ingredient> ingredients, int originalAmount, int currentAmount,
                          int descriptionLength) {
        this.ingredients = ingredients;
        this.mCurrentAmount = currentAmount;
        this.mOriginalAmount = originalAmount;
        this.mStepDescriptionLength = descriptionLength;
    }

    @NonNull
    @Override
    public CardIngredientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.step_ingredient_item, viewGroup, false);

        return new CardIngredientViewHolder(view);
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

    void setCurrentAmount(int currentAmount) {
        mCurrentAmount = currentAmount;
    }

    class CardIngredientViewHolder extends RecyclerView.ViewHolder {
        private int mIndex;
        private TextView mIngredientName;
        private TextView mIngredientAmount;
        private TextView mIngredientUnit;

        /**
         * Initialises views inside the layout.
         *
         * @param itemView containing view
         */
        CardIngredientViewHolder(@NonNull View itemView) {
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
            this.mIndex = i;
            Ingredient ingredient = ingredients.get(this.mIndex);

            String nameWithoutQuantityAndUnit = ingredient.getName();
            // if it is possible to capitalize the first letter, capitalize.
            if (nameWithoutQuantityAndUnit.length() > 1) {
                nameWithoutQuantityAndUnit = nameWithoutQuantityAndUnit.substring(0, 1).toUpperCase(Locale.getDefault())
                        + nameWithoutQuantityAndUnit.substring(1);
            }

            double newQuantity = ingredient.getQuantity() * mCurrentAmount / mOriginalAmount;

            mIngredientName.setText(nameWithoutQuantityAndUnit);

            // Only display quantity in list if the quantity is in the current step description
            if (ingredient.getQuantityPosition().getBeginIndex() != 0
                    || ingredient.getQuantityPosition().getEndIndex() != mStepDescriptionLength) {
                mIngredientAmount.setText(StringUtilities.toDisplayQuantity(newQuantity));
                mIngredientAmount.setVisibility(View.VISIBLE);
            } else {
                mIngredientAmount.setVisibility(View.GONE);
            }

            // Set TextView of unit to GONE when it has no unit
            if ("".equals(ingredient.getUnit())) {
                mIngredientUnit.setVisibility(View.GONE);
            } else {
                mIngredientUnit.setText(ingredient.getUnit());
                mIngredientUnit.setVisibility(View.VISIBLE);
            }
        }
    }
}
