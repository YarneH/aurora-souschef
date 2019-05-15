/**
 * This package is the facade between the processor and a UI. The UI can call the
 * {@link com.aurora.souschefprocessor.facade.SouschefProcessorCommunicator#process(com.aurora.auroralib.ExtractedText)}
 * method which will return a constructed {@link com.aurora.souschefprocessor.recipe.Recipe} by using the
 * {@link com.aurora.souschefprocessor.facade.Delegator}
 * to do a set of {@link com.aurora.souschefprocessor.task.AbstractProcessingTask} to construct the Recipe. If the
 * constructing fails a {@link com.aurora.souschefprocessor.facade.RecipeDetectionException} will be thrown.
 * <p>
 * For creating a communicator the UI must call
 * {@link com.aurora.souschefprocessor.facade.SouschefProcessorCommunicator#createCommunicator(android.content.Context)}
 * after that to speed up the process one might call the
 * {@link com.aurora.souschefprocessor.facade.SouschefProcessorCommunicator#createAnnotationPipelines()} method
 */
package com.aurora.souschefprocessor.facade;
