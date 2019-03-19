package com.aurora.souschefprocessor.task.sectiondivider;

import com.aurora.souschefprocessor.task.AbstractProcessingTask;
import com.aurora.souschefprocessor.task.RecipeInProgress;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.LabeledScoredConstituentFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**
 * A AbstractProcessingTask that divides the original text into usable sections
 */
public class SplitToMainSectionsTask extends AbstractProcessingTask {

    private static String STEP_STARTER_REGEX = ".*(Preparation|Instruction|Instructions|Method|Steps|Directions|Make it)$";
    private Annotation annotatedText;

    public SplitToMainSectionsTask(RecipeInProgress recipeInProgress) {
        super(recipeInProgress);
    }

    /**
     * Divides the original text into a string representing list of mIngredients, string representing
     * a list of mRecipeSteps, string representing the mDescription of the recipe (if present) and an integer
     * representing the amount of people the orignal recipe is for. It will then modify the recipe
     * with these fields
     */
    public void doTask() {
        // TODO all of this could be in seperate threads
        // TODO add check that an original text is contained
        String text = this.mRecipeInProgress.getOriginalText();
        createAnnotatedText(makeEachNewLineASentence(text));

        String[] ingredientsArray = findIngredients(text);
        String ingredients = ingredientsArray[0];
        String[] stepsArray = findSteps(ingredientsArray[1]);
        String steps= stepsArray[0];
        String description = findDescription(stepsArray[1]);
        modifyRecipe(this.mRecipeInProgress, ingredients, steps, description);

    }

    private String makeEachNewLineASentence(String text) {
        text = text.replace("\n", ".\n");
        return text;
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
    public String[] findIngredients(String text) {
        // dummy
        // return "500 gram sauce \n 500 gram spaghetti";
        String [] array = new String[2];
        array[0] = "8 thin slices baguette\n" +
                "100g (3 oz) smoked salmon, sliced\n" +
                "sour cream\n" +
                "capers\n" +
                "lemon cheeks, to serve";
        int startIngredientsIndex = text.indexOf(array[0]);
        array[1] = text.substring(0, startIngredientsIndex) + text.substring(startIngredientsIndex + array[0].length());
        return array;
    }

    /**
     * Finds the mRecipeSteps in a text
     *
     * @param text the text in which to search for mRecipeSteps
     * @return The string representing the mRecipeSteps
     */
    public String[] findSteps(String text) {
        // dummy
        // return "Put 500 gram spaghetti in boiling water for 9 minutes.\n"
        // + "Put the sauce in the Microwave for 3 minutes \n"
        //        + "Put them together.";

        //first try rule based
        String[] result = findStepsRuleBased(text);
        if (result == null || ("").equals(result[0])) {
            result = findStepsNLP(text);
        }

        String[] array = new String[2];
        array[0] = "Toast baguette slices lightly on one side. Layer each round with smoked salmon, top with a dollup of sour \n" +
                "cream and sprinkle with a few capers and lots of freshly ground black pepper.";
        array[1] = "crostini with smoked salmon & sour cream\n";
        return array;

    }

    private String[] findStepsRuleBased(String text) {
        String[] array = new String[2];
        String[] lines = text.split("\n");
        boolean found = false;
        StringBuilder bldSteps = new StringBuilder();
        StringBuilder bldRest = new StringBuilder();
        for (String line : lines) {
            if (!found) {
                Matcher match = Pattern.compile(STEP_STARTER_REGEX).matcher(line);

                if (match.find()) {
                    found = true;
                    System.out.println("FOUND: " + line);
                    array[1] = bldRest.toString();
                }
                else{
                    bldRest.append(line+"\n");
                }
            } else {
                bldSteps.append(line + "\n");
            }

        }
        array[0] = bldSteps.toString();
        System.out.println(bldSteps.toString());
        return array;
    }

    private String[] findStepsNLP(String text) {

        List<CoreMap> sentences = annotatedText.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {


            Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
            Set<Constituent> treeConstituents = tree.constituents(new LabeledScoredConstituentFactory());

            for (Constituent c : treeConstituents) {
                // if the sentece starts with a verb
                if (c.start() == 0 && c.label().toString().equals("VP")) {

                    CoreLabel token = sentence.get(CoreAnnotations.TokensAnnotation.class).get(0);
                    System.out.println("Start with verb " + token);
                }

            }

            //System.out.println(tree.firstChild());
            //System.out.println(sentence + "///" + tree);

        }
        return null;
    }

    /**
     * Finds the mDescription of the recipe in a text
     *
     * @param text the text in which to search for the mDescription of the recipe
     * @return The string representing the mDescription of the recipe
     */
    public String findDescription(String text) {
        // dummy
        // return "A spaghetti recipe";
        return "crostini with smoked salmon & sour cream";
    }

    /**
     * Creates annotation pipeline for
     *
     * @return Annotation pipeline
     */
    private void createAnnotatedText(String text) {
        Properties props = new Properties();
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false, "en"));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        pipeline.addAnnotator(new ParserAnnotator(false, 100));
        //pipeline.addAnnotator(new ParserAnnotator());
        //pipeline.addAnnotator(new MorphaAnnotator(false));
        annotatedText = new Annotation(text);
        pipeline.annotate(annotatedText);

    }


}
