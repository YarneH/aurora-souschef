package com.aurora.souschefprocessor.facade;


import com.aurora.auroralib.ProcessingFailedException;


/**
 * An exception that indicates that the passed text does not resemble a recipe
 */
public class RecipeDetectionException extends ProcessingFailedException {

    public RecipeDetectionException(String message) {
        super(message);
    }
}
