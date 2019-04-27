package com.aurora.souschefprocessor.facade;

import android.content.Context;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.souschefprocessor.R;
import com.aurora.souschefprocessor.recipe.Recipe;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Communicates with the kernel and the UI of souschefprocessor
 */
public class Communicator {
    /**
     * An atomicInteger to showcase the update of the creating of the pipelines
     */
    private static AtomicInteger mProgressAnnotationPipelines = new AtomicInteger(0);

    /**
     * The delgator that executes the processing
     */
    private Delegator mDelegator;
    // TODO add attribute kernelCommunicator to communicate with Aurora

    /**
     * Create a communicator using a CRFClassifier that was loaded in and is used to classify the
     * ingredients
     *
     * @param ingredientsClassifier the classifier for the
     *                              {@link com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask} task
     */
    Communicator(CRFClassifier<CoreLabel> ingredientsClassifier) {
        mDelegator = new Delegator(ingredientsClassifier, true);
    }

    /**
     * Creates a Communicator object by loading in the model for the detection of ingredients i
     * the list. It also calls the {@link #createAnnotationPipelines()} to create the pipeline if
     * this has not been done yet
     *
     * @param context The context to access the resources to load in the model
     * @return A communicator object that has the model loaded in
     */
    public static Communicator createCommunicator(Context context) {
        createAnnotationPipelines();
        try (GZIPInputStream is = new GZIPInputStream(context.getResources().
                openRawResource(R.raw.detect_ingr_list_model))) {
            Log.d("COMMUNICATOR", "start loading model");
            CRFClassifier<CoreLabel> crf = CRFClassifier.getClassifier(is);
            incrementProgressAnnotationPipelines(); // 5
            return new Communicator(crf);
        } catch (IOException | ClassNotFoundException e) {
            Log.e("COMMUNICATOR", "createCommunicator ", e);
        }
        return null;
    }

    /**
     * Function that creates the annotation pipeline, if you make this call the first function of
     * your program
     */
    public static void createAnnotationPipelines() {

    Delegator.createAnnotationPipelines();
    }

    /**
     * Get the progress of the creation of the pipelines
     *
     * @return an int that shows the status of the progress of the annotation pipelines
     */
    public static int getProgressAnnotationPipelines() {
        return mProgressAnnotationPipelines.get();
    }

    /**
     * Increment the progress of the creation of the pipelines
     */
    static void incrementProgressAnnotationPipelines() {
        mProgressAnnotationPipelines.incrementAndGet();
        Log.d("STEP", ""+mProgressAnnotationPipelines);
    }
    
    /**
     * Receives an extractedText object from the AuroraKernel that will be processed into a custom Recipe Object
     *
     * @param extractedText the text to be processed
     */
    public Recipe process(ExtractedText extractedText) {
        // for now String, should be TextObject but not yet defined by Aurora
        // for now this is independent of the tasks sent
        Recipe recipe = null;
        try {
            recipe = mDelegator.processText(extractedText);
            sendObjectToAuroraKernel(recipe);
        } catch (RecipeDetectionException rde) {
            Log.e("DETECTION", "process text", rde);
            // if something went wrong with the detection rethrow the error and let the
            // environment decide what to do in this case
            throw new RecipeDetectionException(rde.getMessage());
        } catch (IllegalArgumentException iae) {
            // This means something is programmatically wrong, so let the programmer know extra
            // checks are needed somewhere in the code
            Log.e("ILLEGAL", "processText", iae);

        }
        return recipe;

    }

    /**
     * TODO
     *
     * @param o
     */
    public void sendObjectToAuroraKernel(PluginObject o) {
        // TODO either this method is inherited from a class that does not exist yet or implement here,
        // should I think be a function of PluginCommunicator a class defined by Aurora

    }
}
