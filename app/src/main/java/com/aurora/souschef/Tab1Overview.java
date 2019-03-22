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
    private Button mButton = null;
    private Communicator mCommunicator = null;
    private TextView mTextView = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_1_overview, container, false);
        mButton = rootView.findViewById(R.id.btn_dummy);
        mTextView = rootView.findViewById(R.id.recipe_string);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get communicator
                try (GZIPInputStream is = new GZIPInputStream(getResources().
                        openRawResource(R.raw.detect_ingr_list_model))) {

                    Log.d("LUCA", "loaded in zip");
                    mTextView.setText("loaded in zip");
                    CRFClassifier<CoreLabel> crf = CRFClassifier.getClassifier(is);
                    Log.d("LUCA", "got classifier");
                    mTextView.setText("got classifier");
                    mCommunicator = new Communicator(crf);
                    Log.d("LUCA", "made communicator");
                    mTextView.setText("made communicator");
                    mCommunicator.process("Yield\n" +
                            "    4 servings\n" +
                            "Active Time\n" +
                            "    30 minutes\n" +
                            "Total Time\n" +
                            "    35 minutes\n" +
                            "\n" +
                            "Ingredients\n" +
                            "\n" +
                            "        1 lb. linguine or other long pasta\n" +
                            "        Kosher salt\n" +
                            "        1 (14-oz.) can diced tomatoes\n" +
                            "        1/2 cup extra-virgin olive oil, divided\n" +
                            "        1/4 cup capers, drained\n" +
                            "        6 oil-packed anchovy fillets\n" +
                            "        1 Tbsp. tomato paste\n" +
                            "        1/3 cup pitted Kalamata olives, halved\n" +
                            "        2 tsp. dried oregano\n" +
                            "        1/2 tsp. crushed red pepper flakes\n" +
                            "        6 oz. oil-packed tuna\n" +
                            "\n" +
                            "Preparation\n" +
                            "\n" +
                            "        Cook pasta in a large pot of boiling salted water, stirring " +
                            "occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking " +
                            "liquid; return pasta to pot.\n" +
                            "        While pasta cooks, pour tomatoes into a fine-mesh sieve set over " +
                            "a medium bowl. Shake to release as much juice as possible, then let tomatoes " +
                            "drain in sieve, collecting juices in bowl, until ready to use.\n" +
                            "        Heat 1/4 cup oil in a large deep-sided skillet over medium-high. " +
                            "Add capers and cook, swirling pan occasionally, until they burst and are " +
                            "crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper " +
                            "towel-lined plate, reserving oil in skillet.\n" +
                            "        Combine anchovies, tomato paste, and drained tomatoes in skillet. " +
                            "Cook over medium-high heat, stirring occasionally, until tomatoes begin " +
                            "to caramelize and anchovies start to break down, about 5 minutes. Add " +
                            "collected tomato juices, olives, oregano, and red pepper flakes and bring " +
                            "to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, " +
                            "about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta " +
                            "cooking liquid to pan. Cook over medium heat, stirring and adding remaining " +
                            "1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened " +
                            "and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\n" +
                            "        Divide pasta among plates. Top with fried capers.\n");
                    Log.d("LUCA", "processed");
                    Recipe recipe = mCommunicator.getRecipe();
                    mTextView.setText(recipe.toString());
                } catch (IOException | ClassNotFoundException e) {
                    Log.e("Model", "demo ", e);
                }
            }
        });
        return rootView;
    }
}
