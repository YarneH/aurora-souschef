package com.aurora.souschefprocessor.facade;

import com.aurora.souschefprocessor.recipe.Recipe;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * Communicates with the kernel
 */
public class Communicator {

    private Delegator mDelegator;
    // TODO add attribute kernelCommunicator to communicate with Aurora

    // Caution! this class heavily depends on the Aurora API

    public Communicator(CRFClassifier<CoreLabel> ingredientsClassifier) {
        /* TODO load in the classifier using this code, or let it be loaded in by an activity
        TODO try { SONARCOMPLAINS
        TODO    GZIPInputStream is = SONARCOMPLAINS
        TODO            new GZIPInputStream(getResources().openRawResource(R.raw.detect_ingr_list_model))
        TODO   CRFClassifier crf = CRFClassifier.getClassifier(is) SONARCOMPLAINS
        TODO    DetectIngredientsInListTask detector = new DetectIngredientsInListTask(rip, crf)
        TODO    detector.doTask() SONARCOMPLAINS
        TODO } catch (IOException | ClassNotFoundException e) { SONARCOMPLAINS

        }*/
        mDelegator = new Delegator(ingredientsClassifier);
    }

    /**
     * Receives a string from the AuroraKernel that will be processed into a custom Recipe Object
     *
     * @param text the text to be processed
     */
    public void process(String text) {
        // for now String, should be TextObject but not yet defined by Aurora
        // for now this is independent of the tasks sent
        Recipe recipe = mDelegator.processText(text);
        sendObjectToAuroraKernel(recipe);

    }

    public void sendObjectToAuroraKernel(Object o) {
        // TODO either this method is inherited from a class that does not exist yet or implement here,
        // should I think be a function of PluginCommunicator a class defined by Aurora
    }
}
