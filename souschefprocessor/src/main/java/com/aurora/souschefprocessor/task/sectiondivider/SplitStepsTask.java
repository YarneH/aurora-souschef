package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * A AbstractProcessingTask that splits the string representing the mRecipeSteps into RecipeStep objects
 */
public class SplitStepsTask extends AbstractProcessingTask {

    public SplitStepsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    private static boolean isSentenceInDescription(CoreMap sentence, String description) {
        List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel token : tokens) {
            // only consider alpha numeric tokens
            if (token.word().matches("[A-Za-z0-9]+")) {
                if (!description.contains(token.word())) {
                    // this is not the wanted sentence skip to the next sentence
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * This will split the stepsString in the RecipeInProgress Object into mRecipeSteps and modifies the
     * recipe object so that the mRecipeSteps are set
     */
    public void doTask() {
        String text = mRecipeInProgress.getStepsString();

        List<RecipeStepInProgress> recipeStepList = divideIntoSteps(text);
        setAnnotations(recipeStepList);
        if (recipeStepList == null || recipeStepList.isEmpty()) {
            throw new RecipeDetectionException("No steps were detected, this is probably not a recipe");
        }

        this.mRecipeInProgress.setStepsInProgress(recipeStepList);
    }

    /**
     * This function splits the text, describing the mRecipeSteps, into different mRecipeSteps of the recipe
     *
     * @return A list of all mRecipeSteps in order
     */
    private List<RecipeStepInProgress> divideIntoSteps(String steps) {

        List<RecipeStepInProgress> list = new ArrayList<>();

        // TODO based on numeric
        //split based on sections (punctuation followed by newline indicates block of text)
        String[] pointAndNewLine = steps.split("([.!?])+\\n");
        if (pointAndNewLine.length > 1) {
            for (String line : pointAndNewLine) {
                if (line.charAt(line.length() - 1) != '.') {
                    // add the character that was splitted on back
                    int indexOfNextCharacter = steps.indexOf(line) + line.length();
                    if (indexOfNextCharacter < steps.length()) {
                        line += steps.charAt(steps.indexOf(line) + line.length());
                    }
                }
                list.add(new RecipeStepInProgress(line));

            }

        } else {
            // No other detection method yielded result, so just split on the sentences
            list = splitStepsBySplittingOnPunctuation(steps);
        }

        return list;
    }

    /**
     * Splits the text on punctuation
     *
     * @param steps the text to be splitted
     * @return a list with recipesteps
     */
    private List<RecipeStepInProgress> splitStepsBySplittingOnPunctuation(String steps) {
        // A boolean that indicates if this is the first char of the sentence
        // This is used to make sure that the first character is not a whitspace
        boolean firstChar = true;
        char[] characters = steps.toCharArray();
        StringBuilder bld = new StringBuilder();
        List<RecipeStepInProgress> list = new ArrayList<>();
        for (char c : characters) {
            // if this is not the first character while also being a whitespace
            if ((!firstChar || !Character.isWhitespace(c))) {
                if (c != '\n') {
                    bld.append(c);
                } else {
                    // if a new line is present
                    bld.append(" ");
                }
                // set firstChar to false
                firstChar = false;
            }
            // if this is a punctuation character end the sentence and make a step
            if (c == '.' || c == '!') {
                bld.setCharAt(0, Character.toUpperCase(bld.charAt(0)));
                list.add(new RecipeStepInProgress(bld.toString()));

                // make a new builder and set the boolean back to true
                bld = new StringBuilder();
                firstChar = true;
            }
        }

        return list;
    }

    private void setAnnotations(List<RecipeStepInProgress> list) {
        for (RecipeStepInProgress step : list) {
            fillInAnnotation(step);
        }
    }

    /**
     * A helper function for {@link #setAnnotations(List)}. It calls the {@link RecipeStepInProgress#setBeginPositionOffset(int)}
     * and {@link RecipeStepInProgress#setSentenceAnnotation(List)} methods
     *
     * @param step the step whose annotations are filled in
     */
    private void fillInAnnotation(RecipeStepInProgress step) {

        // the annotation of the section of the string
        Annotation annotation = findAnnotationForStep(step.getDescription());

        // the sentences in this annotation
        List<CoreMap> sentencesInAnnotation = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        // The sentences that are part of this step
        List<CoreMap> sentenceAnnotationsForStep = new ArrayList<>();

        // find the sentences  for this description
        for (CoreMap sentence : sentencesInAnnotation) {
            if (isSentenceInDescription(sentence, step.getDescription())) {
                sentenceAnnotationsForStep.add(sentence);
            }
        }

        // the annotations can never be empty
        if (sentenceAnnotationsForStep.isEmpty()) {
            throw new IllegalStateException("No annotations were found for this step " + step.getDescription());
        }

        // Calculate the beginPositionOffset
        CoreLabel firstToken = sentenceAnnotationsForStep.get(0).get(CoreAnnotations.TokensAnnotation.class).get(0);

        // Call the setters
        step.setBeginPositionOffset(firstToken.beginPosition());
        step.setSentenceAnnotation(sentenceAnnotationsForStep);
    }

    private Annotation findAnnotationForStep(String description) {
        String descriptionWithReplacedNewLines = description.replace("\n", " ").trim();
        ExtractedText text = mRecipeInProgress.getExtractedText();

        // first the title
        if (text.getTitle().replace("\n", " ").trim().contains
                (descriptionWithReplacedNewLines)) {
            return text.getTitleAnnotation();
        }
        // sections
        for (Section s : text.getSections()) {
            if (s.getTitle() != null && s.getTitle().replace("\n", " ").trim().contains
                    (descriptionWithReplacedNewLines)) {
                return s.getTitleAnnotation();
            }

            if (s.getBody() != null && s.getBody().replace("\n", " ").trim().contains
                    (descriptionWithReplacedNewLines)) {
                return s.getBodyAnnotation();
            }

        }
        // should not happen
        throw new IllegalStateException("no section found for step with description " + description);

    }
}
