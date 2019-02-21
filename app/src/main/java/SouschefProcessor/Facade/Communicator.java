package SouschefProcessor.Facade;

import SouschefProcessor.Recipe.Recipe;

/**
 * Communicates with the kernel
 */
public class Communicator {

    //Caution! this class heavily depends on the Aurora API

    /**
     * Receives a string from the AuroraKernel that will be processed into a custom Recipe Object
     * @param text the text to be processed
     */
    public void receiveTextFromAuroraKernel(String text){
        Delegator delegator = new Delegator();
        Recipe recipe = delegator.processText(text);
        sendObjectToAuroraKernel(recipe);

    }

    public void sendObjectToAuroraKernel(Object o){
        //TODO either this method is inherited from a class that does not exist yet or implement here,
        // should I think be a function of PluginCommunicator a class defined by Aurora
    }
}
