package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aurora.souschefprocessor.facade.Communicator;
import com.aurora.souschefprocessor.recipe.Recipe;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Class defining the functionality of the overview tab.
 */
public class Tab1Overview extends Fragment {
    private Recipe mRecipe = null;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_1_overview, container, false);
        TextView textView = rootView.findViewById(R.id.recipe_string);

        textView.setText(mRecipe.getDescription());
        return rootView;
    }

    protected void setRecipe(Recipe recipe){
        mRecipe = recipe;
    }
}
