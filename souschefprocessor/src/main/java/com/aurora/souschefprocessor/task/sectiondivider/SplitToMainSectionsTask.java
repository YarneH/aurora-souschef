package com.aurora.souschefprocessor.task.sectiondivider;


import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
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
    private List<String> mSectionsBodies = new ArrayList<>();
    /**
     * The original text of this recipe
     */
    private String mOriginalText;

    public SplitToMainSectionsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
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
            bld.deleteCharAt(bld.length() - 1);
            return bld.toString();
        }
    }

    /**
     * Finds the mDescription of the recipe in a text
     *
     * @param text the text in which to search for the mDescription of the recipe
     * @return The string representing the mDescription of the recipe
     */
    private static String findDescription(String text) {
        return trimNewLines(text);
    }

    /**
     * Creates the {@link #sAnnotationPipeline}
     */
    private static void createAnnotationPipeline() {
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false, "en"));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));

        pipeline.addAnnotator(new ParserAnnotator(false, MAX_SENTENCES_FOR_PARSER));
        pipeline.addAnnotator(new MorphaAnnotator(false));
        sAnnotationPipeline = pipeline;
    }

    private String findStepsNLP() {
        StringBuilder bld = new StringBuilder();
        List<String> sectionsToRemove = new ArrayList<>();
        boolean allreadyFound = false;
        for (String section : mSectionsBodies) {
            if (!allreadyFound) {
                boolean verbDetected = verbDetected(section, false);
                if (!verbDetected) {
                    verbDetected = verbDetected(section, true);
                }
                allreadyFound = verbDetected;
            }
            if (allreadyFound) {
                // remove this section
                sectionsToRemove.add(section);
                // append it to the builder
                bld.append(section.trim());
                bld.append("\n\n");

            }
        }
        mSectionsBodies.removeAll(sectionsToRemove);
        return bld.toString();
    }

    /**
     * @return the index of the
     * section of the line where the steps start
     */
    private String findStepsRegexBased() {
        boolean found = false;
        StringBuilder bld = new StringBuilder();
        int sectionIndex = -1;
        for (String section : mSectionsBodies) {
            String[] lines = section.split("\n");

            for (String line : lines) {
                String lowerCaseLine = line.toLowerCase(Locale.ENGLISH);
                Matcher match = Pattern.compile(STEP_STARTER_REGEX).matcher(lowerCaseLine);

                if (found) {
                    bld.append(trimNewLines(line));

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


        String[] lines = text.split("\n");
        String steps = "";

        for (String line : lines) {

            String lowerCaseLine = line.toLowerCase(Locale.ENGLISH);
            Matcher match = Pattern.compile(STEP_STARTER_REGEX).matcher(lowerCaseLine);

            if (match.find()) {
                int lineStartIndex = text.indexOf(line);
                int stepStartIndex = lineStartIndex + line.length();
                steps = text.substring(stepStartIndex);
                text = text.substring(0, lineStartIndex);

            }

        }
        return new ResultAndAlteredTextPair(trimNewLines(steps), text);
    }

    private int findIngredientsDigit() {
        boolean found = false;
        String ingredientsSection = "";
        for (String section : mSectionsBodies) {
            if (!found) {

                String[] lines = section.trim().split("\n");
                // at least two ingredients needed
                if (lines.length > 1) {
                    for (String line : lines) {

                        // no recipe has only one ingredient
                        if (line.length() > 0) {
                            line = line.trim();
                            char c = line.charAt(0);
                            found = Character.isDigit(c);
                            // if found  this is set to the correct section
                            ingredientsSection = section;
                        }
                    }
                }
            }
        }
        return mSectionsBodies.indexOf(ingredientsSection);
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
        return new ResultAndAlteredTextPair(trimNewLines(ingredientsSection), text);
    }

    /**
     * Capitalizes sentences again
     *
     * @param text The text to capitalize
     * @return The text with the sentences capitalized again
     */
    private String capitalize(String text) {

        if (text.length() == 0) {
            // if text is empty just return text
            return text;
        }

        // get both original and argument text in lowercase
        String lowerCaseOriginal = mOriginalText.toLowerCase(Locale.ENGLISH);
        String lowerCaseText = text.toLowerCase(Locale.ENGLISH);

        // try to find the lowerCaseText in the original lower case text
        int startIndex = lowerCaseOriginal.indexOf(lowerCaseText);

        if (startIndex < 0) {
            // if the original text was not found just return the text (this should not happen)
            return text;
        }

        // get the original substring that with capitalization that matches the argument string
        // without captialization
        int endIndex = startIndex + text.length();
        return mOriginalText.substring(startIndex, endIndex);

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

        modifyRecipe(this.mRecipeInProgress, ingredients, steps, description);

    }



    private static boolean sectionIsClutter(String section) {
        for (String s : CLUTTER_STRINGS) {
            if (section.toLowerCase(Locale.ENGLISH).contains(s)) {
                return true;
            }
        }
        return false;
    }

    private String findDescription() {
        StringBuilder bld = new StringBuilder();
        // append the title
        bld.append(mRecipeInProgress.getExtractedText().getTitle());
        bld.append("\n");
        for (String section : mSectionsBodies) {
            bld.append(section.trim());
            // append a new line between the sections for readability
            bld.append("\n");

        }
        return trimNewLines(bld.toString());
    }

    private String findSteps() {
        String steps = findStepsRegexBased();
        if (steps.length() == 0) {
            steps = findStepsNLP();
        }
        return steps;
    }

    private String findIngredients() {
        int indexOfIngredients = findIngredientsRegexBased();
        if (indexOfIngredients < 0) {
            indexOfIngredients = findIngredientsDigit();
            if (indexOfIngredients < 0) {
                // nothing found return the empty string
                return "";
            }
        }
        String ingredients = trimNewLines(mSectionsBodies.get(indexOfIngredients));
        mSectionsBodies.remove(mSectionsBodies.get(indexOfIngredients));
        return ingredients;

    }

    private int findIngredientsRegexBased() {
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
     * Modifies the {@link RecipeInProgress} so that the {@link RecipeInProgress#mIngredientsString},
     * {@link RecipeInProgress#mStepsString}, and {@link RecipeInProgress#mDescription} fields are set
     *
     * @param recipe      The recipe to modify
     * @param ingredients The string representing the mIngredients
     * @param steps       The string representing the mRecipeSteps
     * @param description The string representing the desription
     */
    private void modifyRecipe(RecipeInProgress recipe, String ingredients, String steps, String
            description) {
        recipe.setIngredientsString(ingredients.trim());
        recipe.setStepsString(steps.trim());
        recipe.setDescription(description.trim());
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
        text = text.toLowerCase(Locale.ENGLISH);
        String[] lines = text.split("\n\n");
        mSectionsBodies.clear();
        mSectionsBodies.addAll(Arrays.asList(lines));

        int indexOfIngredients = findIngredientsRegexBased();
        if (indexOfIngredients < 0) {
            // nothing found
            return new ResultAndAlteredTextPair("", text);
        }
        text = text.replace(mSectionsBodies.get(indexOfIngredients - 1), "")
                .replace(mSectionsBodies.get(indexOfIngredients), "");


        return new ResultAndAlteredTextPair(trimNewLines(mSectionsBodies.get(indexOfIngredients)), text);

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
            return new ResultAndAlteredTextPair(trimNewLines(steps), text.replace(steps, ""));
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
