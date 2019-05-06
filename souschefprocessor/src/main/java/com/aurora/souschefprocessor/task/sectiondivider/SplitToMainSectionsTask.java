package com.aurora.souschefprocessor.task.sectiondivider;


import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.souschefprocessor.facade.RecipeDetectionException;
import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;
import edu.stanford.nlp.util.CoreMap;


/**
 * A AbstractProcessingTask that divides the original text into usable sections
 */
public class SplitToMainSectionsTask extends AbstractProcessingTask {
    /**
     * Some cooking verbs that are wrongly tagged by corenlp
     */
    private static final String[] VERBS_NOT_DETECTED_BY_NLP = {"reserve", "seal", "butter", "season",
            "place", "preheat", "toast", "layer", "beat", "heat"};
    /**
     * Words often preceded by a number but that are not ingredients needed for {@link #findIngredientsDigit()}
     */
    private static final String[] NOT_INGREDIENTS_WORDS = {"mins", "minutes", "hours", "hour", "min", "minute",
            "servings", "portions", "people", "persons"};
    /**
     * A regex that covers most commonly used words that indicate the instructions of the recipe are
     * following
     */
    private static final String STEP_STARTER_REGEX = ".*((prep(aration)?[s]?)|instruction[s]?|method|description|" +
            "make it|step[s]?|direction[s]?)[: ]?$";

    /**
     * A regex that covers most commonly used words that indicate the ingredients of the recipe are
     * following
     */
    private static final String INGREDIENT_STARTER_REGEX = "([iI]ngredient[s]?)[: ]?$";


    /**
     * An array of strings that are clutter in the description of a recipe. These lines would confuse
     * a user and must thus be removed
     */
    private static final String[] CLUTTER_STRINGS = {"print recipe", "shopping list", "you will need:"};


    /**
     * A copy of the list of sections that was included in the {@link ExtractedText} object in
     * the recipe, this list will be altered during the task
     */
    private List<Section> mSections = new ArrayList<>();


    public SplitToMainSectionsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
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
     * Removes the {@link #CLUTTER_STRINGS} from a section if they are present
     *
     * @param section the section to remove the clutter from
     * @return the cleaned up section
     */
    private static Section removeClutter(Section section) {
        for (String clutter : CLUTTER_STRINGS) {
            // remove from the title
            if (section.getTitle() != null) {
                String title = section.getTitle();
                int index = title.toLowerCase(Locale.ENGLISH).indexOf(clutter);
                // remove the clutter string
                if (index > -1) {
                    // if found remove it
                    title = title.substring(0, index) + title.substring(index + clutter.length());
                    section.setTitle(title);
                }
            }
            // remove from the body
            if (section.getBody() != null) {
                String body = section.getBody();
                int index = body.toLowerCase(Locale.ENGLISH).indexOf(clutter);
                if (index > -1) {
                    // if found remove it
                    body = body.substring(0, index) + body.substring(index + clutter.length());
                    section.setBody(body);
                }
            }
        }
        //create a copy of the cleaned section
        Section copy = new Section(section.getBody());
        copy.setTitle(section.getTitle());
        ProtobufAnnotationSerializer annotationSerializer = new ProtobufAnnotationSerializer(true);
        if (section.getTitleAnnotation() != null) {
            copy.setTitleAnnotationProto(annotationSerializer.toProto(section.getTitleAnnotation()));
        }
        if (section.getBodyAnnotation() != null) {
            copy.setBodyAnnotationProto(annotationSerializer.toProto(section.getBodyAnnotation()));
        }
        return copy;
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
     * Helper function for {@link #findIngredientsDigit()} and
     * {@link #findStepsOrIngredientsRegexBasedWithoutTitles(String)} Appends the text to the
     * stringBuilder if the section
     * starting with digits has already been found.
     *
     * @param found a boolean that indicates if the text should be added
     * @param bld   The stringBuilder to append to
     * @param text  The text to append to the stringBuilder
     */
    private static void appendTextToStringBuilderIfFound(boolean found, StringBuilder bld, String text) {
        if (found) {
            bld.append(text);
            if (bld.lastIndexOf("\n") != bld.length() - 1) {
                // append a new line if necessary
                bld.append("\n");
            }
        }
    }

    /**
     * Checks if the line doe not contain any of the {@link #NOT_INGREDIENTS_WORDS}
     *
     * @param line the line to check
     * @return a boolean
     */
    private static boolean doesNotContainNonIngredientWords(String line) {
        String lowerCase = line.toLowerCase(Locale.ENGLISH);
        for (String notIngredientWord : NOT_INGREDIENTS_WORDS) {
            if (lowerCase.contains(notIngredientWord)) {
                return false;
            }
        }
        return true;
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

        ExtractedText text = mRecipeInProgress.getExtractedText();

        mSections = new ArrayList<>();

        for (Section sec : text.getSections()) {
            mSections.add(removeClutter(sec));
        }


        steps = findSteps();
        ingredients = findIngredients();
        description = findDescription();


        modifyRecipe(trimNewLines(ingredients), trimNewLines(steps), trimNewLines(description));

    }

    /**
     * Finds the mRecipeSteps in a text by using the {@link #mSections} field
     *
     * @return The string representing the mRecipeSteps
     */
    private String findSteps() {
        String steps = findStepsRegexBased();

        if (steps.isEmpty()) {
            steps = findStepsNLP();
        }

        return steps;
    }

    /**
     * Finds the ingredientslist in a text, by first trying {@link #findIngredientsRegexBased()}
     * to check if a common word is present and if that fails by using the {@link #findIngredientsDigit()}
     * to check if there is a block of text that has a lot of lines starting with digits. It uses the
     * {@link #mSections} list and this list is altered during the method, in that the ingredients
     * are removed from the list.
     *
     * @return A string representing the ingredients, if nothing is found an empty string is returned
     */
    private String findIngredients() {
        String foundIngredients = findIngredientsRegexBased();
        if (foundIngredients.length() > 0) {
            return foundIngredients;
        }

        foundIngredients = findIngredientsDigit();
        if (foundIngredients.length() > 0) {
            // nothing found return the empty string
            return foundIngredients;
        }

        return "";

    }

    /**
     * Finds the description of this recipe by appending the {@link ExtractedText} and all
     * the sections remaining in the {@link #mSections}
     *
     * @return A string that represents the description of this recipe
     */
    private String findDescription() {
        StringBuilder bld = new StringBuilder();
        ExtractedText text = mRecipeInProgress.getExtractedText();
        // append the file name
        bld.append(text.getFilename()).append("\n");
        // append the title
        bld.append(mRecipeInProgress.getExtractedText().getTitle());
        bld.append("\n");
        for (Section s : mRecipeInProgress.getExtractedText().getSections()) {
            String body = s.getBody();
            if (mSections.contains(s)) {
                String title = s.getTitle();
                if (title != null ) {
                    bld.append(title);
                    bld.append("\n");
                }

                if (body != null) {
                    bld.append(body.trim());
                    // append a new line between the sections for readability
                    bld.append("\n");
                }
            }
        }



        return bld.toString();
    }

    /**
     * Modifies the {@link #mRecipeInProgress} so that all the strings are set
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
     * Finds the steps based on a regex. It checks whether some common names that start
     * the instruction section are present. This is based on the {@link #STEP_STARTER_REGEX}. It uses
     * the {@link #mSections} list and this list is altered during the method, in that the steps
     * are removed from the list.
     *
     * @return A string representing the steps of this recipe
     */
    private String findStepsRegexBased() {
        // first try with the titles
        String result = findStepsOrIngredientsRegexBasedTitles(STEP_STARTER_REGEX);
        if (!result.isEmpty()) {
            return result;
        }
        // try without title
        return findStepsOrIngredientsRegexBasedWithoutTitles(STEP_STARTER_REGEX);
    }

    /**
     * Finds the steps in this recipe based on NLP. It uses the domain knowledge that recipes contain
     * instructions and instructions always use the imperative sense. It uses the {@link #mSections}
     * list. This list is altered during the method, in that the steps are removed from the list
     *
     * @return A string representing the steps
     */
    private String findStepsNLP() {

        StringBuilder bld = new StringBuilder();
        List<Section> sectionsToRemove = new ArrayList<>();
        boolean alreadyFound = false;
        for (Section section : mSections) {

            if (!alreadyFound) {
                boolean verbDetected = verbDetected(section);

                alreadyFound = verbDetected;
            }

            if (alreadyFound) {
                // remove this section
                sectionsToRemove.add(section);
                // append it to the builder
                bld.append(section.getBody());
                bld.append("\n\n");
            }
        }

        mSections.removeAll(sectionsToRemove);
        return bld.toString();
    }

    /**
     * Finds the ingredients based on a regex. It checks whether some common names that start
     * the ingredients section are present. This is based on the {@link #INGREDIENT_STARTER_REGEX}.
     * It uses the {@link #mSections} list and this list is altered during the method, in that
     * the ingredients are removed from the list.
     *
     * @return the index of the section containing the ingredients in the {@link #mSections} list
     * if no ingredients are found -1 is returned.
     */

    private String findIngredientsRegexBased() {
        // first try with titles
        String result = findStepsOrIngredientsRegexBasedTitles(INGREDIENT_STARTER_REGEX);
        if (result.length() > 0) {
            // found using titles so return the result
            return result;
        }
        // try without titles
        return findStepsOrIngredientsRegexBasedWithoutTitles(INGREDIENT_STARTER_REGEX);
    }

    /**
     * Finds the ingredients based on the fact that for most recipes at least one of the ingredients
     * will start with a digit. It uses the {@link #mSections} list and this list is altered during
     * the method, in that the ingredients are removed from the list.
     *
     * @return the found ingredients, if no ingredients found return the empty string
     */
    private String findIngredientsDigit() {
        boolean found = false;
        StringBuilder bld = new StringBuilder();
        int firstSection = mSections.size();
        for (Section section : mSections) {
            String body = section.getBody();
            if (body != null) {
                String[] lines = body.split("\n");

                for (String line : lines) {
                    // only do this of not found already and the line has at least two characters
                    // (one character can never be an ingredient)
                    boolean notFoundAndAtLeastTwoCharacters = !found && line.length() > 1;
                    if (notFoundAndAtLeastTwoCharacters) {
                        // look at the first actual character and not a whitespace
                        line = line.trim();
                        char c = line.charAt(0);
                        found = Character.isDigit(c) && doesNotContainNonIngredientWords(line);
                        firstSection = mSections.indexOf(section);

                    }
                }
                appendTextToStringBuilderIfFound(found, bld, body);
            }
        }

        if (found) {
            // remove all the sections starting from the firs section
            mSections = mSections.subList(0, firstSection);
            return bld.toString();
        }
        // let caller know nothing was found
        return "";
    }

    /**
     * Find the steps or ingredients based on their regex using {@link Section#getTitle()} field.
     * If steps are searched the {@link #STEP_STARTER_REGEX} is used, if ingredients are searched
     * the {@link #INGREDIENT_STARTER_REGEX} is used
     *
     * @param regex the regex to match
     * @return the found steps, if nothing is found the empty string is returned
     */
    private String findStepsOrIngredientsRegexBasedTitles(String regex) {

        StringBuilder bld = null;
        List<Section> foundSections = new ArrayList<>();

        for (Section s : mSections) {

            if (bld != null) {
                // if bld exists it means the regex has been found
                bld.append(s.getBody());
                foundSections.add(s);
            } else {
                String title = s.getTitle();
                if (title != null && Pattern.compile(regex).matcher(
                        title.toLowerCase(Locale.ENGLISH)).find()) {
                    //only get out the part that matches..
                    bld = new StringBuilder(s.getBody());
                    foundSections.add(s);
                }
            }
        }

        if (bld != null) {
            mSections.removeAll(foundSections);
            return bld.toString();

        }

        return "";
    }

    /**
     * Find the steps or ingredients based on their regex
     * If steps are searched the {@link #STEP_STARTER_REGEX} is used, if ingredients are searched
     * the {@link #INGREDIENT_STARTER_REGEX} is used
     *
     * @param regex the regex to match
     * @return the found steps, if nothing is found the empty string is returned
     */
    private String findStepsOrIngredientsRegexBasedWithoutTitles(String regex) {

        boolean found = false;
        int sectionIndex = -1;
        StringBuilder bld = new StringBuilder();

        for (Section section : mSections) {
            String body = section.getBody();

            if (body != null) {
                String[] lines = body.split("\n");

                for (String line : lines) {

                    String lowerCaseLine = line.toLowerCase(Locale.ENGLISH);
                    Matcher match = Pattern.compile(regex).matcher(lowerCaseLine);

                    appendTextToStringBuilderIfFound(found, bld, line);

                    if (!found && match.find()) {
                        found = true;
                        sectionIndex = mSections.indexOf(section);
                        // remove this line and any following of this section from the body
                        mSections.get(sectionIndex).setBody(body.substring(0, body.indexOf(line)));
                    }
                }
                // make sure the bodies (steps) are split up by \n\n
                bld.append("\n\n");
            }
        }

        if (sectionIndex >= 0) {
            // remove the section containing steps from the list, only remove if some steps were found
            mSections = mSections.subList(0, sectionIndex + 1);
        }

        return bld.toString().trim();
    }

    /**
     * This checks if the section contains imperative sentences starting with a verb
     *
     * @param section The section to analyze
     * @return a boolean that indicates if a verb was detected
     */
    private boolean verbDetected(Section section) {
        // TODO adapt this method to new input of aurora
        Annotation annotatedText = getAnnotatedText(section);
        List<CoreMap> sentences = annotatedText.get(CoreAnnotations.SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            if (tokens.size() > 1) {
                CoreLabel startToken = tokens.get(0);
                CoreLabel secondToken = tokens.get(1);
                if ("VB".equals(startToken.tag()) && !"CD".equals(secondToken.tag())) {
                    return true;
                }
                // check the known collection of wrongly detected words
                for (String verb : VERBS_NOT_DETECTED_BY_NLP) {
                    if (startToken.word().equalsIgnoreCase(verb)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Gets the annotation of the section
     *
     * @return the annotated text
     */
    private Annotation getAnnotatedText(Section section) {
        if (section.getBodyAnnotation() == null) {
            throw new RecipeDetectionException("At least one section was not annotated for this text. " +
                    "Please contact Aurora to resolve this");
        }
        return section.getBodyAnnotation();
    }

}

