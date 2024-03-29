package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;
import com.aurora.souschefprocessor.task.RecipeStepInProgress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

/**
 * A AbstractProcessingTask that splits the string representing the mRecipeSteps into RecipeStep objects, it also
 * allocates the correct annotations received from Aurora to the correct step
 */
public class SplitStepsTask extends AbstractProcessingTask {

    public SplitStepsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    /**
     * This will split the stepsString in the RecipeInProgress Object into mRecipeSteps and modifies the
     * recipe object so that the mRecipeSteps are set
     *
     * @throws RecipeDetectionException An indication that the splitting to steps failed. This is probably not a
     *                                  recipe or something went wrong in Aurora
     */
    public void doTask() throws RecipeDetectionException {
        String text = mRecipeInProgress.getStepsString();

        List<RecipeStepInProgress> recipeStepList = divideIntoSteps(text);
        setAnnotations(recipeStepList);
        if (recipeStepList.isEmpty()) {
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
     * private helper function that calls {@link #fillInAnnotation(RecipeStepInProgress)} on all the steps
     *
     * @param list the list of steps to fill in the annotation
     * @throws RecipeDetectionException Is thrown when the annotation could not be found
     */
    private void setAnnotations(List<RecipeStepInProgress> list) throws RecipeDetectionException {
        for (RecipeStepInProgress step : list) {
            fillInAnnotation(step);
        }
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

    /**
     * A helper function for {@link #setAnnotations(List)}. It calls the
     * {@link RecipeStepInProgress#setBeginPosition(int)}
     * and {@link RecipeStepInProgress#setSentenceAnnotations(List)} methods
     *
     * @param step the step whose annotations are filled in
     * @throws RecipeDetectionException Is thrown when no annotation can be found. This is a problem in Aurora or
     *                                  the formatting of the input is not as expected
     */
    private void fillInAnnotation(RecipeStepInProgress step) throws RecipeDetectionException {

        // trim the description and replace new lines by spaces
        step.setDescription(step.getDescription().replace("\n", " ").trim());

        // the annotation of the section of the string
        Annotation annotation = findAnnotationForStep(step.getDescription());

        if (annotation == null) {
            throw new RecipeDetectionException("At least one section is not annotated. Please contact " +
                    "Aurora to resolve this problem");
        }
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

        findAllSentences(sentencesInAnnotation, sentenceAnnotationsForStep,
                step.getDescription());

        // Calculate the beginPositionOffset
        CoreLabel firstToken = sentenceAnnotationsForStep.get(0).get(CoreAnnotations.TokensAnnotation.class).get(0);

        // Call the setters
        step.setBeginPosition(firstToken.beginPosition());
        step.setSentenceAnnotations(sentenceAnnotationsForStep);
    }

    /**
     * find the Annotation object from the {@link ExtractedText} that contains the annotation for this description
     *
     * @param description the description to find the annotation for
     * @return the found annotation
     */
    private Annotation findAnnotationForStep(String description) {

        ExtractedText text = mRecipeInProgress.getExtractedText();

        // first the title
        if (text.getTitle().replace("\n", " ").trim().contains
                (description)) {
            return text.getTitleAnnotation();
        }


        // sections
        for (Section s : text.getSections()) {
            if (s.getTitle().replace("\n", " ").trim().contains
                    (description)) {
                return s.getTitleAnnotation();
            }

            if (s.getBody().replace("\n", " ").trim().contains
                    (description)) {

                return s.getBodyAnnotation();
            }

        }
        // should not happen
        throw new IllegalStateException("No section found for step with description " + description+ "\n\n This file " +
                "is too badly formatted for Souschef.");

    }

    /**
     * Checks if this sentence (CoreMap) is a part of this description
     *
     * @param sentence    the sentence to check
     * @param description the description to check
     * @return true if the sentence is in the description
     */
    private static boolean isSentenceInDescription(CoreMap sentence, String description) {
        String spacelessDescription = description.replace(" ", "");
        List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
        StringBuilder bld = new StringBuilder();
        for (CoreLabel token : tokens) {
            bld.append(token.originalText());
            if (!spacelessDescription.contains(bld.toString())) {
                return false;
            }
        }

        return true;
    }

    /**
     * finds the first token of the this description by using the already foundSentences and checking if the end of
     * the sentence before the first found sentence is still in this description
     *
     * @param allSentences   all the sentences from the section that this description was in
     * @param description    The description to find the first token of
     * @param foundSentences The already found sentences annotation that has firstToken as its first token, this list
     *                       can be altered during the executing by putting elements in the front of this list
     */
    private void findAllSentences(List<CoreMap> allSentences, List<CoreMap> foundSentences,
                                  String description) {
        // Calculate the current firstToken
        CoreLabel firstToken = foundSentences.get(0).get(CoreAnnotations.TokensAnnotation.class).get(0);

        // find the actual first token
        int firstAnnotationIndex = allSentences.indexOf(foundSentences.get(0));
        StringBuilder bld = new StringBuilder();
        // get the description without spaces == all tokens concatenated
        String spacelessDescription = description.replace(" ", "");

        // check if there is a previous sentence
        if (firstAnnotationIndex > 0) {
            // search in the description that comes before the first token
            description = description.substring(0, description.indexOf(firstToken.originalText()));
            // only if some string left to find in
            if (!description.isEmpty()) {
                //get  the sentence before
                CoreMap sentenceBefore = allSentences.get(firstAnnotationIndex - 1);
                List<CoreLabel> tokens = sentenceBefore.get(CoreAnnotations.TokensAnnotation.class);

                //search in reverse order
                Collections.reverse(tokens);
                for (CoreLabel token : tokens) {
                    bld.insert(0, token.originalText());

                    if (!spacelessDescription.contains(bld.toString())) {
                        // found one that is not in the description, no expanding possible reverse tokens again and
                        Collections.reverse(tokens);
                        // create a new sentence with all the token annotations
                        CoreMap newSentence = new Annotation("");
                        newSentence.set(CoreAnnotations.TokensAnnotation.class,
                                tokens.subList(tokens.indexOf(firstToken)
                                        , tokens.size()));
                        foundSentences.add(0, newSentence);
                        break;
                    } else {
                        // found one that is in the remaining description
                        firstToken = token;
                    }

                }
            }
        }

    }

}
