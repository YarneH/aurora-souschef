package com.aurora.souschefprocessor.facade;

import android.content.Context;
import android.util.Log;

import com.aurora.auroralib.PluginObject;
import com.aurora.souschefprocessor.R;
import com.aurora.souschefprocessor.recipe.Recipe;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Communicates with the kernel
 */
public class Communicator {

    private static AtomicInteger mProgressAnnotationPipelines = new AtomicInteger(0);

    /**
     * The recipe result of the processing
     */
    private Recipe mRecipe;
    /**
     * The delgator that executes the processing
     */
    private Delegator mDelegator;
    // TODO add attribute kernelCommunicator to communicate with Aurora

    // Caution! this class heavily depends on the Aurora API

    /**
     * Create a communicator using a CRFClassifier that was loaded in and is used to classify the
     * ingredients
     *
     * @param ingredientsClassifier
     */
    public Communicator(CRFClassifier<CoreLabel> ingredientsClassifier) {
        mDelegator = new Delegator(ingredientsClassifier, false);

    }

    public static Communicator createCommunicator(Context context) {
        try (GZIPInputStream is = new GZIPInputStream(context.getResources().
                openRawResource(R.raw.detect_ingr_list_model))) {
            // update 1:
            CRFClassifier<CoreLabel> crf = CRFClassifier.getClassifier(is);
            return new Communicator(crf);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("MODEL", "createCommunicator ", e);
        }
        return null;
    }

    public static void createAnnotationPipelines() {
        Delegator.createAnnotationPipelines();
    }

    public static int getProgressAnnotationPipelines() {
        return mProgressAnnotationPipelines.get();
    }

    static void incrementProgressAnnotationPipelines() {
        mProgressAnnotationPipelines.incrementAndGet();
    }

    /**
     * Receives a string from the AuroraKernel that will be processed into a custom Recipe Object
     *
     * @param text the text to be processed
     */
    public void process(String text) {
        // for now String, should be TextObject but not yet defined by Aurora
        // for now this is independent of the tasks sent
        mRecipe = mDelegator.processText(text);
        sendObjectToAuroraKernel(mRecipe);

    }

    public Recipe getRecipe() {
        return mRecipe;
    }

    public void sendObjectToAuroraKernel(PluginObject o) {
        // TODO either this method is inherited from a class that does not exist yet or implement here,
        // should I think be a function of PluginCommunicator a class defined by Aurora
    }
}
