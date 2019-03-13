package com.aurora.souschef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.aurora.auroralib.Constants;
import com.aurora.souschef.R;
import com.aurora.souschefprocessor.facade.Communicator;
import com.aurora.souschefprocessor.recipe.Recipe;

public class MainActivity extends AppCompatActivity {
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                String inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                mTextView.setText(inputText);
                //Recipe recipe = Communicator.delegate(inputText);
                //String result = basicPluginObject.getResult();
                //mTextView.setText(result);
            }
            // TODO handle a PluginObject that was cached
            else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)){

            }
        }
    }
}
