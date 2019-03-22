package com.aurora.souschef;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aurora.souschefprocessor.facade.Communicator;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask;

import java.io.IOException;
import java.util.zip.GZIPInputStream;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Class defining the functionality of the overview tab.
 */
public class Tab1Overview extends Fragment {
    private Button mButton;
    private Communicator mCommunicator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_1_overview, container, false);

        mButton = rootView.findViewById(R.id.btn_dummy);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get communicator
                try {
                    GZIPInputStream is = new GZIPInputStream(getResources().openRawResource(R.raw.detect_ingr_list_model));
                    Log.d("LUCA"   , "loaded in zip");
                    CRFClassifier<CoreLabel> crf = CRFClassifier.getClassifier(is);
                    Log.d("LUCA"   , "got classifier");
                    mCommunicator = new Communicator(crf);
                    Log.d("LUCA", "made communicator");
                    mCommunicator.process("");
                    Log.d("LUCA", "processed");

                } catch (IOException | ClassNotFoundException e) {


                }
            }
        });


        return rootView;
    }
}
