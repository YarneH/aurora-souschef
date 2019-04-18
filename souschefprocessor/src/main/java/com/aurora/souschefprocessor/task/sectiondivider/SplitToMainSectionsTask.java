package com.aurora.souschefprocessor.task.sectiondivider;


import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.util.CoreMap;


/**
 * A AbstractProcessingTask that divides the original text into usable sections
 */
public class SplitToMainSectionsTask extends AbstractProcessingTask {

    /**
     * A regex that covers most commonly used words that indicate the instructions of the recipe are
     * following
     */
    private static final String STEP_STARTER_REGEX = ".*((prep(aration)?[s]?)|instruction[s]?|method|description|" +
            "make it|step[s]?|direction[s])[: ]?$";
    /**
     * A regex that covers most commonly used words that indicate the ingredients of the recipe are
     * following
     */
    private static final String INGREDIENT_STARTER_REGEX = "([iI]ngredient[s]?)[: ]?$";
    /**
     * A constant needed for the creation of the parser (should be moved to Aurora)
     */
    private static final int MAX_SENTENCES_FOR_PARSER = 100;
    /**
     * An array of strings that are clutter in the description of a recipe. These lines would confuse
     * a user and must thus be removed
     */
    private static final String[] CLUTTER_STRINGS = {"print recipe", "shopping list"};
    /**
     * An annotation pipeline specific for parsing of sentences
     */
    private static AnnotationPipeline sAnnotationPipeline;
    /**
     * The list of bodies from the list of sections that was included in the {@link ExtractedText}
     * received from Aurora
     */
    private List<String> mSectionsBodies = new ArrayList<>();
    /**
     * The original text of this recipe
     */
    private String mOriginalText;

    /**
     * A list of basicAnnotators that were given by {@link com.aurora.souschefprocessor.facade.Delegator}
     */
    private List<Annotator> basicAnnotators;

    public SplitToMainSectionsTask(RecipeInProgress recipeInProgress, List<Annotator> basicAnnotators) {
        super(recipeInProgress);
        this.basicAnnotators = basicAnnotators;
    }


    /**
     * This trims each line (via split on new line character) of a block of text
     *
     * @param text The text to trim
     * @return The trimmed text
     */
    private static String trimNewLines(String text) {
        if (text.length() == 0) {
            return text;
        }

        StringBuilder bld = new StringBuilder();
        String[] lines = text.split("\n");
        for (String line : lines) {
            bld.append(line.trim());
            bld.append("\n");

        }
        // Remove last new line
        if (bld.length() == 0) {
            return text;
        } else {
            return bld.toString().trim().replace("\n\n\n", "\n\n");
        }
    }

    /**
     * Finds the mDescription of the recipe in a text
     *
     * @param text the text in which to search for the mDescription of the recipe
     * @return The string representing the mDescription of the recipe
     */
    private static String findDescription(String text) {
        return text;
    }

    /**
     * Checks if a section contains one of the {@link #CLUTTER_STRINGS}
     *
     * @param section the section to check
     * @return a boolean indicating whether the section is clutter
     */
    private static boolean sectionIsClutter(String section) {
        for (String s : CLUTTER_STRINGS) {
            if (section.toLowerCase(Locale.ENGLISH).contains(s)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Creates the {@link #sAnnotationPipeline}
     */
    private void createAnnotationPipeline() {
        AnnotationPipeline pipeline = new AnnotationPipeline();
        if (basicAnnotators.isEmpty()) {
            pipeline.addAnnotator(new TokenizerAnnotator(false));

            pipeline.addAnnotator(new WordsToSentencesAnnotator(false));

            pipeline.addAnnotator(new POSTaggerAnnotator(false));

        } else {
            for (Annotator a : basicAnnotators) {
                pipeline.addAnnotator(a);
            }
        }

        sAnnotationPipeline = pipeline;
    }

    /**
     * Finds the steps in this recipe based on NLP. It uses the domain knowledge that recipes contain
     * instructions and instructions always use the imperative sense. It uses the {@link #mSectionsBodies}
     * list. This list is altered during the method, in that the steps are removed from the list
     *
     * @return A string representing the steps
     */
    private String findStepsNLP() {
        StringBuilder bld = new StringBuilder();
        List<String> sectionsToRemove = new ArrayList<>();
        boolean alreadyFound = false;
        for (String section : mSectionsBodies) {
            if (!alreadyFound) {
                boolean verbDetected = verbDetected(section, false);
                if (!verbDetected) {
                    verbDetected = verbDetected(section, true);
                }
                alreadyFound = verbDetected;
            }
            if (alreadyFound) {
                // remove this section
                sectionsToRemove.add(section);
                // append it to the builder
                bld.append(section);
                bld.append("\n\n");

            }
        }

        mSectionsBodies.removeAll(sectionsToRemove);
        return bld.toString();
    }

    private String findStepsOrIngredientsRegexBasedTitles(boolean steps) {
        String regex = INGREDIENT_STARTER_REGEX;
        if (steps) {
            regex = STEP_STARTER_REGEX;
        }
        ExtractedText extractedText = mRecipeInProgress.getExtractedText();
        if (extractedText != null) {
            for (Section s : extractedText.getSections()) {
                String title = s.getTitle();
                if (title != null && Pattern.compile(regex).matcher(
                        title.toLowerCase(Locale.ENGLISH)).find()) {
                    return s.getBody();
                }

            }
        }
        return "";

    }

    /**
     * Finds the steps based on a regex. It checks whether some common names that start
     * the instruction section are present. This is based on the {@link #STEP_STARTER_REGEX}. It uses
     * the {@link #mSectionsBodies} list and this list is altered during the method, in that the steps
     * are removed from the list.
     *
     * @return A string representing the steps of this recipe
     */
    private String findStepsRegexBased() {
        // first try with the titles
        String result = findStepsOrIngredientsRegexBasedTitles(true);
        if (result.length() > 0) {
            mSectionsBodies.remove(result);
            return result;
        }

        boolean found = false;
        int sectionIndex = -1;
        StringBuilder bld = new StringBuilder();


        for (String section : mSectionsBodies) {
            String[] lines = section.split("\n");

            for (String line : lines) {

                String lowerCaseLine = line.toLowerCase(Locale.ENGLISH);
                Matcher match = Pattern.compile(STEP_STARTER_REGEX).matcher(lowerCaseLine);

                if (found) {
                    bld.append(line);
                    bld.append("\n");

                } else {
                    if (match.find()) {
                        found = true;
                        sectionIndex = mSectionsBodies.indexOf(section);
                        // remove this line and any following of this section from the body
                        mSectionsBodies.set(sectionIndex, section.substring(0, section.indexOf(line)));
                    }
                }
            }
            // make sure the bodies (steps) are split up by \n\n
            bld.append("\n\n");

        }
        if (sectionIndex >= 0) {
            // remove the section containing steps from the list, only remove if some steps were found
            mSectionsBodies = mSectionsBodies.subList(0, sectionIndex + 1);
        }


        return bld.toString().trim();

    }

    /**
     * Finds the steps based on a regex. It checks whether some common names that start
     * the instruction section are present. This is based on the {@link #STEP_STARTER_REGEX}
     *
     * @param text the text in which to search for mIngredients
     * @return A pair with the detected ingredientlist and the altered text so that the detected
     * ingredientlist is not in the text anymore
     */
    private ResultAndAlteredTextPair findStepsRegexBased(String text) {
        mSectionsBodies.clear();
        mSectionsBodies.add(text);

        String steps = findStepsRegexBased();
        if (steps.length() == 0) {
            // nothing found
            return new ResultAndAlteredTextPair("", text);

        }
        text = text.substring(0, text.indexOf(steps));

        // remove the last line because that one matched the STEP_STARTER_REGEX
        String[] lines = text.split("\n");
        List<String> linesList = new ArrayList<>(Arrays.asList(lines));
        Collections.reverse(linesList);
        String toReplace = "";
        boolean found = false;
        for (String line : linesList) {
            if (!found && !line.trim().isEmpty()) {
                toReplace = line;
                found = true;
            }
        }
        text = text.replace(toReplace, "");


        return new ResultAndAlteredTextPair(steps, text);
    }

    /**
     * Finds the ingredients based on the fact that for most recipes at least one of the ingredients
     * will start with a digit. It uses the {@link #mSectionsBodies} list and this list is altered during
     * the method, in that the ingredients are removed from the list.
     *
     * @return the index that is the index of the ingredientssection of the recipe, if no ingredients
     * are found -1 is returned
     */
    private int findIngredientsDigit() {
        boolean found = false;
        String ingredientsSection = "";
        for (String section : mSectionsBodies) {
            String[] lines = section.split("\n");
            // at least two ingredients needed
            if (lines.length > 1) {
                for (String line : lines) {
                    // only do this of not found already and the line has at least two characters
                    // (one character can never be an ingredient)
                    boolean notFoundAndAtLeastTwoCharacters = !found && line.length() > 1;
                    if (notFoundAndAtLeastTwoCharacters) {
                        // look at the first actural character and not a whitespace
                        line = line.trim();
                        char c = line.charAt(0);
                        found = Character.isDigit(c);
                        // if found  this is set to the correct section
                        ingredientsSection = section;

                    }

                }
            }
        }
        if (found) {
            return mSectionsBodies.indexOf(ingredientsSection);
        }
        // let caller know nothing was found
        return -1;
    }

    /**
     * Finds the ingredients based on the fact that for most recipes at least one of the ingredients
     * will start with a digit.
     *
     * @param text the text in which to search for mIngredients
     * @return A pair with the detected ingredientlist and the altered text so that the detected
     * ingredientlist is not in the text anymore
     */
    private ResultAndAlteredTextPair findIngredientsDigit(String text) {

        String[] sections = text.split("\n\n");
        mSectionsBodies.clear();
        mSectionsBodies.addAll(Arrays.asList(sections));


        int indexOfSection = findIngredientsDigit();
        if (indexOfSection < 0) {
            return new ResultAndAlteredTextPair("", text);
        }

        String ingredientsSection = sections[indexOfSection];
        text = text.replace(ingredientsSection, "");
        return new ResultAndAlteredTextPair(ingredientsSection, text);
    }

    /**
     * Divides the original text into a string representing list of mIngredients, string representing
     * a list of mRecipeSteps, string representing the mDescription of the recipe (if present) and an integer
     * representing the amount of people the original recipe is for. It will then modify the recipe
     * with these fields
     */
    public void doTask() {

        String ingredients;
        String steps;
        String description;
        if (mRecipeInProgress.getExtractedText() == null) {
            mOriginalText = this.mRecipeInProgress.getOriginalText();

            if (("").equals(mOriginalText)) {
                throw new RecipeDetectionException("No original text found, this is probably not a recipe");
            }

            ResultAndAlteredTextPair ingredientsAndText = findIngredients(mOriginalText);
            ingredients = ingredientsAndText.getResult();


            ResultAndAlteredTextPair stepsAndText = findSteps(ingredientsAndText.getAlteredText());
            steps = stepsAndText.getResult();
            description = findDescription(stepsAndText.getAlteredText());

        } else {
            ExtractedText text = mRecipeInProgress.getExtractedText();
            mSectionsBodies = new ArrayList<>();
            for (Section sec : text.getSections()) {
                if (!sectionIsClutter(sec.getBody())) {
                    mSectionsBodies.add(sec.getBody());
                }
            }
            ingredients = findIngredients();
            steps = findSteps();
            description = findDescription();


        }

        modifyRecipe(trimNewLines(ingredients), trimNewLines(steps),
                trimNewLines(description));


    }

    /**
     * Finds the description of this recipe by appending the {@link ExtractedText#mTitle} and all
     * the sections remaining in the {@link #mSectionsBodies}
     *
     * @return A string that represents the description of this recipe
     */
    private String findDescription() {
        StringBuilder bld = new StringBuilder();
        // append the title
        bld.append(mRecipeInProgress.getExtractedText().getTitle());
        bld.append("\n");
        for (Section s : mRecipeInProgress.getExtractedText().getSections()) {
            String body = s.getBody();
            if (mSectionsBodies.contains(body)) {
                String title = s.getTitle();
                if (title != null) {
                    bld.append(title);
                    bld.append("\n");
                }
                bld.append(body.trim());
                // append a new line between the sections for readability
                bld.append("\n");
            }


        }
        return bld.toString();
    }

    /**
     * Finds the mRecipeSteps in a text by using the {@link #mSectionsBodies} field
     *
     * @return The string representing the mRecipeSteps
     */
    private String findSteps() {
        String steps = findStepsRegexBased();
        if (steps.length() == 0) {
            steps = findStepsNLP();
        }
        return steps;
    }

    /**
     * Finds the ingredientslist in a text, by first trying {@link #findIngredientsRegexBased()}
     * to check if a common word is present and if that fails by using the {@link #findIngredientsDigit()}
     * to check if there is a block of text that has a lot of lines starting with digits. It uses the
     * {@link #mSectionsBodies} list and this list is altered during the method, in that the ingredients
     * are removed from the list.
     *
     * @return A string representing the ingredients, if nothing is found an empty string is returned
     */
    private String findIngredients() {
        int indexOfIngredients = findIngredientsRegexBased();
        if (indexOfIngredients < 0) {
            indexOfIngredients = findIngredientsDigit();
            if (indexOfIngredients < 0) {
                // nothing found return the empty string
                return "";
            }
        }
        String ingredients = mSectionsBodies.get(indexOfIngredients);
        mSectionsBodies.remove(ingredients);
        return ingredients;

    }

    /**
     * Finds the ingredients based on a regex. It checks whether some common names that start
     * the ingredients section are present. This is based on the {@link #INGREDIENT_STARTER_REGEX}.
     * It uses the {@link #mSectionsBodies} list and this list is altered during the method, in that
     * the ingredients are removed from the list.
     *
     * @return the index of the section containing the ingredients in the {@link #mSectionsBodies} list
     * if no ingredients are found -1 is returned.
     */
    private int findIngredientsRegexBased() {
        /* first try with the titles
         */
        String result = findStepsOrIngredientsRegexBasedTitles(false);
        if (result.length() > 0) {
            return mSectionsBodies.indexOf(result);
        }
        boolean found = false;
        boolean sectionAdded = false;
        String ingredientsSection = "";

        for (String line : mSectionsBodies) {
            if (!found) {
                Matcher match = Pattern.compile(INGREDIENT_STARTER_REGEX).matcher(line);

                if (match.find()) {
                    found = true;
                }

            } else {
                if (!sectionAdded) {
                    ingredientsSection = line;
                    sectionAdded = true;
                }
            }
        }
        if (ingredientsSection.length() == 0) {
            // nothing found return negative value
            return -1;
        }
        return mSectionsBodies.indexOf(ingredientsSection);
    }

    /**
     * Modifies the {@link #mRecipeInProgress} so that the {@link RecipeInProgress#mIngredientsString},
     * {@link RecipeInProgress#mStepsString}, and {@link RecipeInProgress#mDescription} fields are set
     *
     * @param ingredients The string representing the mIngredients
     * @param steps       The string representing the mRecipeSteps
     * @param description The string representing the desription
     */
    private void modifyRecipe(String ingredients, String steps, String
            description) {
        mRecipeInProgress.setIngredientsString(ingredients);
        mRecipeInProgress.setStepsString(steps);
        mRecipeInProgress.setDescription(description);
    }

    /**
     * Finds the ingredientslist in a text, by first trying {@link #findIngredientsRegexBased(String)}
     * to check if a common word is present and if that fails by using the {@link #findIngredientsDigit(String)}
     * to check if there is a block of text that has a lot of lines starting with digits
     *
     * @param text the text in which to search for mIngredients
     * @return A pair with the detected ingredientlist and the altered text so that the detected
     * ingredientlist is not in the text anymore
     */
    private ResultAndAlteredTextPair findIngredients(String text) {
        // dummy
        ResultAndAlteredTextPair ingredientsAndText = findIngredientsRegexBased(text);
        if ("".equals(ingredientsAndText.getResult())) {
            ingredientsAndText = findIngredientsDigit(text);
        }

        return ingredientsAndText;
    }

    /**
     * Finds the ingredients based on a regex. It checks whether some common names that start
     * the ingredients section are present. This is based on the {@link #INGREDIENT_STARTER_REGEX}
     *
     * @param text the text in which to search for mIngredients
     * @return A pair with the detected ingredientlist and the altered text so that the detected
     * ingredientlist is not in the text anymore
     */
    private ResultAndAlteredTextPair findIngredientsRegexBased(String text) {
        String[] lines = text.split("\n\n");
        mSectionsBodies.clear();
        mSectionsBodies.addAll(Arrays.asList(lines));

        int indexOfIngredients = findIngredientsRegexBased();
        if (indexOfIngredients < 0) {
            // nothing found
            return new ResultAndAlteredTextPair("", text);
        }
        // remove both the section that indicated the ingredients as the ingredients
        text = text.replace(mSectionsBodies.get(indexOfIngredients - 1), "")
                .replace(mSectionsBodies.get(indexOfIngredients), "");


        return new ResultAndAlteredTextPair(mSectionsBodies.get(indexOfIngredients), text);

    }

    /**
     * Finds the mRecipeSteps in a text
     *
     * @param text the text in which to search for mRecipeSteps
     * @return The string representing the mRecipeSteps
     */
    private ResultAndAlteredTextPair findSteps(String text) {
        //first try rule based
        ResultAndAlteredTextPair pair = findStepsRegexBased(text);
        if (("").equals(pair.getResult())) {
            pair = findStepsNLP(text);
        }


        return pair;

    }

    /**
     * This checks if a text starts with a verb.
     *
     * @param text      The text
     * @param lowercase indicates wheter the detection should be done on a lowercase text. Since corenlp
     *                  can be better at detecting sentences starting with a verb when it is lowercase
     * @return a boolean that indicates if a verb was detectec
     */
    private boolean verbDetected(String text, boolean lowercase) {
        // TODO adapt this method to new input of aurora
        Annotation annotatedTextLowerCase = createAnnotatedText(text, lowercase);
        List<CoreMap> sentences = annotatedTextLowerCase.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {

            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            if (tokens.size() > 1) {
                CoreLabel startToken = tokens.get(0);
                CoreLabel secondToken = tokens.get(1);
                if ("VB".equals(startToken.tag()) && !"CD".equals(secondToken.tag())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds the steps in this recipe based on NLP. It uses the domain knowledge that recipes contain
     * instructions and instructions always use the imperative sense.
     *
     * @param text the text in which to search for mIngredients
     * @return A pair with the detected steplist and the altered text so that the detected
     * ingredientlist is not in the text anymore
     */
    private ResultAndAlteredTextPair findStepsNLP(String text) {
        String[] sections = text.split("\n\n");
        mSectionsBodies = new ArrayList<>();
        mSectionsBodies.addAll(Arrays.asList(sections));
        String steps = findStepsNLP();
        if (steps.length() == 0) {
            // nothing found return empty string and unaltered text
            return new ResultAndAlteredTextPair("", text);
        } else {
            return new ResultAndAlteredTextPair(steps, text.replace(steps, ""));
        }
    }

    /**
     * Creates annotation pipeline and parses the text
     * (this should be in Aurora)
     *
     * @return the annotated text
     */
    private Annotation createAnnotatedText(String text, boolean lowercase) {
        if (sAnnotationPipeline == null) {
            createAnnotationPipeline();
        }
        // The parser could perform better on imperative sentences (instructions) when the
        // first word is decapitalize see: https://stackoverflow.com/questions/35872324/stanford-nlp-vp-vs-np
        Annotation annotation;
        if (lowercase) {
            annotation = new Annotation(text.toLowerCase(Locale.ENGLISH));
        } else {
            annotation = new Annotation(text);
        }

        sAnnotationPipeline.annotate(annotation);
        return annotation;

    }

    /**
     * A helper class for the SplitToMainSectionsTask, it is a dataclass that stores two strings:
     * {@link #mResult} = the detected result
     * {@link #mAlteredText} = the original text without the detected result
     */
    private static class ResultAndAlteredTextPair {
        private String mResult;
        private String mAlteredText;

        ResultAndAlteredTextPair(String result, String alteredText) {
            this.mResult = result;
            this.mAlteredText = alteredText;
        }

        String getResult() {
            return mResult;
        }

        String getAlteredText() {
            return mAlteredText;
        }
    }

}
