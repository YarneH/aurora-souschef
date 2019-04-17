package com.aurora.souschefprocessor.facade;

/**
 * An exception that indicates that the passed text does not resemble a recipe
 */
public class RecipeDetectionException extends IllegalArgumentException {

    public RecipeDetectionException(String message) {
        super(message);
    }
}
