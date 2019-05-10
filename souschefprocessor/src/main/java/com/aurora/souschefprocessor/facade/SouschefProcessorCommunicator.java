package com.aurora.souschefprocessor.facade;

import android.content.Context;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.ProcessorCommunicator;
import com.aurora.souschefprocessor.PluginConstants;
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
public class SouschefProcessorCommunicator extends ProcessorCommunicator {
    /**
     * An atomicInteger to showcase the update of the creating of the pipelines
     */
    private static AtomicInteger mProgressAnnotationPipelines = new AtomicInteger(0);

    /**
     * The delgator that executes the processing
     */
    private Delegator mDelegator;

    /**
     * Create a communicator using a CRFClassifier that was loaded in and is used to classify the
     * ingredients
     *
     * @param context    Context required by {@link com.aurora.auroralib.ProcessorCommunicator}
     * @param classifier the classifier for the
     *                   {@link com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInListTask} task
     */
    SouschefProcessorCommunicator(Context context, CRFClassifier<CoreLabel> classifier) {
        /*
         * A UNIQUE_PLUGIN_NAME needs to be passed to the constructor of ProcessorCommunicator for
         * proper configuration of the cache
         */
        super(PluginConstants.UNIQUE_PLUGIN_NAME, context);
        mDelegator = new Delegator(classifier, true);
    }

    /**
     * Creates a SouschefProcessorCommunicator object by loading in the model for the detection of ingredients i
     * the list. It also calls the {@link #createAnnotationPipelines()} to create the pipeline if
     * this has not been done yet
     *
     * @param context The context to access the resources to load in the model
     * @return A communicator object that has the model loaded in
     */
    public static SouschefProcessorCommunicator createCommunicator(Context context) {
        createAnnotationPipelines();
        try (GZIPInputStream is = new GZIPInputStream(context.getResources().
                openRawResource(R.raw.detect_ingr_list_model))) {
            // log for the opening
            incrementProgressAnnotationPipelines(); // 1
            Log.d("COMMUNICATOR", "start loading model");
            CRFClassifier<CoreLabel> crf = CRFClassifier.getClassifier(is);
            incrementProgressAnnotationPipelines(); // 2
            return new SouschefProcessorCommunicator(context, crf);
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
     * Increment the progress of the creation of the pipelines
     */
    static void incrementProgressAnnotationPipelines() {
        mProgressAnnotationPipelines.incrementAndGet();
        Log.d("STEP", "" + mProgressAnnotationPipelines);
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
     * Receives an extractedText object from the AuroraKernel that will be processed into a custom Recipe Object
     *
     * @param extractedText the text to be processed
     * @return A Recipe object (which extends PLuginObject) that is the result of the processed text
     */
    @Override
    protected PluginObject process(ExtractedText extractedText) {

        if (extractedText == null) {
            throw new RecipeDetectionException("No text was extracted. Something went wrong in Aurora!");
        }
        Recipe recipe = null;
        try {
            recipe = mDelegator.processText(extractedText);
        } catch (RecipeDetectionException rde) {
            Log.e("DETECTION", "process text", rde);
            // if something went wrong with the detection rethrow the error and let the
            // environment decide what to do in this case
            throw new RecipeDetectionException(rde.getMessage());
        } catch (IllegalArgumentException iae) {
            // This means something is programmatically wrong, so let the programmer know extra
            // checks are needed somewhere in the code
            Log.e("ILLEGAL", "processText", iae);

        } catch (Exception e) {
            // something else went wrong
            Log.e("COMMUNICATOR", "unexpected exception", e);
            throw new RecipeDetectionException("Something unexpected happened: " + e.getMessage());
        }

        return recipe;

    }

}
