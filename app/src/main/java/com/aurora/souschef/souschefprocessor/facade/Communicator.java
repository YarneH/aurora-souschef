package com.aurora.souschef.souschefprocessor.facade;

import com.aurora.souschef.souschefprocessor.recipe.Recipe;

/**
 * Communicates with the kernel
 */
public class Communicator {

    private Delegator mDelegator = new Delegator();
    //TODO add attribute kernelCommunicator to communicate with Aurora

    //Caution! this class heavily depends on the Aurora API

    /**
     * Receives a string from the AuroraKernel that will be processed into a custom Recipe Object
     *
     * @param text the text to be processed
     */
    public void process(String text) { //for now String, should be TextObject but not yet defined by Aurora
        Recipe recipe = mDelegator.processText(text); //for now this is independent of the tasks sent
        sendObjectToAuroraKernel(recipe);

    }

    public void sendObjectToAuroraKernel(Object o) {
        //TODO either this method is inherited from a class that does not exist yet or implement here,
        // should I think be a function of PluginCommunicator a class defined by Aurora
    }
}
