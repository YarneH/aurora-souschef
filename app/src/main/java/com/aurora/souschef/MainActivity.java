package com.aurora.souschef;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.aurora.auroralib.Constants;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;

    public MainActivity() {
        // default constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Change back to the correct view
        setContentView(R.layout.activity_steps);

        mTextView = (TextView) findViewById(R.id.textView);

        Intent intent = new Intent(this, StepsActivity.class);
        startActivity(intent);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                String inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                mTextView.setText(inputText);
                // Not implemented yet Recipe recipe = Communicator.delegate(inputText)
                // Not implemented yet String result = basicPluginObject.getResult()
                // Not implemented yet mTextView.setText(result)
            } else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)) {
                // TODO: Handle a PluginObject that was cached
                //dummy
                Log.d("NO IMPLEMENTATION", "not implemented yet");

            }
        }

    }
}

