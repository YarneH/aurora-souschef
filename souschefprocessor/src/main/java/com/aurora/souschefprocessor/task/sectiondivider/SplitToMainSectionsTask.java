package com.aurora.souschefprocessor.task.sectiondivider;


import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

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

    private static final String STEP_STARTER_REGEX = ".*((prep(aration)?[s]?)|instruction[s]?|method|description|" +
            "make it|step[s]?|direction[s])[: ]?$";
    private static final String INGREDIENT_STARTER_REGEX = "([iI]ngredient[s]?)[: ]?$";
    private static final String END_TOKEN = " ENDTOKEN.";
    private static final int MAX_SENTENCES_FOR_PARSER = 100;


    public SplitToMainSectionsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    private static String makeEachNewLineASentence(String text) {
        text = text.replace("\n", END_TOKEN + "\n");
        return text;
    }

    private static String trimNewLines(String text) {
        StringBuilder bld = new StringBuilder();
        String[] lines = text.split("\n");
        for (String line : lines) {
            bld.append(line.trim() + "\n");
        }
        // Remove last new line
        bld.deleteCharAt(bld.length() - 1);
        return bld.toString();
    }

    /**
     * Divides the original text into a string representing list of mIngredients, string representing
     * a list of mRecipeSteps, string representing the mDescription of the recipe (if present) and an integer
     * representing the amount of people the original recipe is for. It will then modify the recipe
     * with these fields
     */
    public void doTask() {
        // TODO all of this could be in seperate threads
        // TODO add check that an original text is contained
        String text = this.mRecipeInProgress.getOriginalText();


        ResultAndAlteredTextPair ingredientsAndText = findIngredients(text);
        String ingredients = ingredientsAndText.getResult();


        ResultAndAlteredTextPair stepsAndText = findSteps(ingredientsAndText.getAlteredText());
        String steps = stepsAndText.getResult();
        String description = findDescription(stepsAndText.getAlteredText());

        modifyRecipe(this.mRecipeInProgress, ingredients, steps, description);

    }

    /**
     * Modifies the recipe so that the ingredientsString, stepsString, mDescription and amountOfPeople
     * fields are set.
     *
     * @param recipe      The recipe to modify
     * @param ingredients The string representing the mIngredients
     * @param steps       The string representing the mRecipeSteps
     * @param description The string representing the desription
     */
    public void modifyRecipe(RecipeInProgress recipe, String ingredients, String steps, String description) {
        recipe.setIngredientsString(ingredients);
        recipe.setStepsString(steps);
        recipe.setDescription(description);
    }

    /**
     * Finds the ingredientslist in a text
     *
     * @param text the text in which to search for mIngredients
     * @return The string representing the mIngredients
     */
    public ResultAndAlteredTextPair findIngredients(String text) {
        // dummy
        ResultAndAlteredTextPair ingredientsAndText = findIngredientsRegexBased(text);
        if ("".equals(ingredientsAndText.getResult())) {
            ingredientsAndText = findIngredientsDigit(text);
        }

        return ingredientsAndText;
    }

    private ResultAndAlteredTextPair findIngredientsRegexBased(String text) {
        text = text.toLowerCase(Locale.ENGLISH);
        String[] lines = text.split("\n\n");
        boolean found = false;
        boolean sectionAdded = false;
        StringBuilder bld = new StringBuilder();

        for (String line : lines) {
            if (!found) {
                Matcher match = Pattern.compile(INGREDIENT_STARTER_REGEX).matcher(line);

                if (match.find()) {
                    found = true;
                    text = text.replace(line, "");

                }

            } else {
                if (!sectionAdded) {
                    bld.append(line);
                    sectionAdded = true;
                }
            }
        }
        text = text.replace(bld.toString(), "");

        return new ResultAndAlteredTextPair(trimNewLines(bld.toString()), text);
    }

    private ResultAndAlteredTextPair findIngredientsDigit(String text) {
        String[] sections = text.split("\n\n");
        boolean found = false;
        String ingredientsSection = "";
        for (String section : sections) {
            if (!found) {

                String[] lines = section.trim().split("\n");

                for (String line : lines) {

                    if (line.length() > 0) {
                        line = line.trim();
                        char c = line.charAt(0);
                        if (Character.isDigit(c)) {
                            found = true;
                            ingredientsSection = section;
                        }
                    }
                }
            }
        }
        text = text.replace(ingredientsSection, "");
        return new ResultAndAlteredTextPair(trimNewLines(ingredientsSection), text);
    }

    /**
     * Finds the mRecipeSteps in a text
     *
     * @param text the text in which to search for mRecipeSteps
     * @return The string representing the mRecipeSteps
     */
    public ResultAndAlteredTextPair findSteps(String text) {
        // dummy
        // return "Put 500 gram spaghetti in boiling water for 9 minutes.\n"
        // + "Put the sauce in the Microwave for 3 minutes \n"
        //        + "Put them together."


        //first try rule based
        ResultAndAlteredTextPair pair = findStepsRuleBased(text);
        if (("").equals(pair.getResult())) {
            pair = findStepsNLP(text);
        }

        return pair;

    }

    private ResultAndAlteredTextPair findStepsRuleBased(String text) {

        String[] lines = text.split("\n");
        String steps = "";

        for (String line : lines) {
            String lowerCaseLine = line.toLowerCase();
            Matcher match = Pattern.compile(STEP_STARTER_REGEX).matcher(lowerCaseLine);

            if (match.find()) {
                int startIndexLine = text.indexOf(line);
                int startIndexSteps = startIndexLine + line.length();
                steps = text.substring(startIndexSteps);
                text = text.substring(0, startIndexLine);

            }
        }
        return new ResultAndAlteredTextPair(trimNewLines(steps), text);
    }

    private boolean verbDetected(String text, boolean lowercase) {
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

    private ResultAndAlteredTextPair findStepsNLP(String text) {
        String[] sections = text.split("\n\n");
        for (String section : sections) {
            boolean verbDetected = verbDetected(section, true);
            if (!verbDetected) {
                verbDetected = verbDetected(section, false);
            }
            if (verbDetected) {
                return new ResultAndAlteredTextPair(trimNewLines(section), text.replace(section, ""));
            }
        }

        return new ResultAndAlteredTextPair("", text);
    }

    /**
     * Finds the mDescription of the recipe in a text
     *
     * @param text the text in which to search for the mDescription of the recipe
     * @return The string representing the mDescription of the recipe
     */
    public String findDescription(String text) {
        return trimNewLines(text);
    }

    /**
     * Creates annotation pipeline for
     *
     * @return Annotation pipeline
     */
    private Annotation createAnnotatedText(String text, boolean lowercase) {
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false, "en"));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));

        pipeline.addAnnotator(new ParserAnnotator(false, MAX_SENTENCES_FOR_PARSER));
        pipeline.addAnnotator(new MorphaAnnotator(false));
        // The parser could perform better on imperative sentences (instructions) when the
        // first word is decapitalize see: https://stackoverflow.com/questions/35872324/stanford-nlp-vp-vs-np
        Annotation annotation;
        if (lowercase) {
            annotation = new Annotation(text.toLowerCase(Locale.ENGLISH));
        } else {
            annotation = new Annotation(text);
        }

        pipeline.annotate(annotation);
        return annotation;

    }


}
