package com.aurora.souschefprocessor.task;


import com.aurora.auroralib.ExtractedText;
import com.aurora.souschefprocessor.recipe.Ingredient;
import com.aurora.souschefprocessor.recipe.ListIngredient;
import com.aurora.souschefprocessor.recipe.Position;
import com.aurora.souschefprocessor.task.ingredientdetector.DetectIngredientsInStepTask;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

import static org.junit.Assert.assertTrue;


/**
 * Test on a dataset for ingredients in the steps
 */
public class DetectIngredientsInRecipeStepTaskLongTest {

    // Irrelevant properties
    private static String originalIngredientText = "irrelevant";
    private static HashMap<Ingredient.PositionKeysForIngredients, Position> irrelevantPositions = new HashMap<>();
    private static ExtractedText emptyExtractedText = new ExtractedText("", Collections.emptyList());


    // Container for the detected recipes
    private static List<RecipeInProgress> rips;

    // Container for the correct recipes
    // For each RIP a list of it's steps and for each step a list of it's ingredients
    private static List<List<List<Ingredient>>> correctIngredientsPerRecipe;

    private static int totalIngredients = 0;

    @BeforeClass
    public static void initialize() {
        Position pos = new Position(0, 1);
        for (Ingredient.PositionKeysForIngredients key : Ingredient.PositionKeysForIngredients.values()) {
            irrelevantPositions.put(key, pos);
        }

        // Prepare the correct ingredients for the steps
        correctIngredientsPerRecipe = getCorrectIngredientsPerRecipe();

        rips = new ArrayList<>();

        // Set the lists of ingredients for the detection RecipeInProgress list
        // Hardcoded to avoid dependence on DetectIngredientsInListTask
        setListIngredients();

        // set the recipe steps for the detection RecipeInProgress list
        setRecipeSteps();

        // annotate the steps
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator());
        pipeline.addAnnotator(new WordsToSentencesAnnotator());
        pipeline.addAnnotator(new POSTaggerAnnotator());

        for (RecipeInProgress rip : rips) {
            List<RecipeStepInProgress> recipeSteps = rip.getStepsInProgress();
            for (RecipeStepInProgress s : recipeSteps) {
                Annotation a = new Annotation(s.getDescription());
                pipeline.annotate(a);
                s.setSentenceAnnotations(Collections.singletonList(a));
            }
        }

        // execute the detection
        for (RecipeInProgress r : rips) {
            for (RecipeStepInProgress s : r.getStepsInProgress()) {
                // Execute the detection for each recipe step in this RecipeInProgress
                DetectIngredientsInStepTask detector = new DetectIngredientsInStepTask(r,
                        r.getStepsInProgress().indexOf(s));

                detector.doTask();

            }

        }
    }

    /**
     * Get the correct ingredients by using {@link #initializeCorrectIngredientsInStepsString()}
     */
    private static List<List<List<Ingredient>>> getCorrectIngredientsPerRecipe() {
        List<List<List<Ingredient>>> correctIngredients = new ArrayList<>();
        String allStepIngredients = initializeCorrectIngredientsInStepsString();
        String[] stepIngredientsPerRecipe = allStepIngredients.split("\n\n\n");
        for (String stepIngredients : stepIngredientsPerRecipe) {
            List<List<Ingredient>> correctIngredientsPerStep = new ArrayList<>();
            String[] ingredientsPerStep = stepIngredients.split("\n\n");

            for (String ingredients : ingredientsPerStep) {
                if (ingredients.equals("NO_INGREDIENTS")) {
                    correctIngredientsPerStep.add(new ArrayList<>());
                } else {
                    List<Ingredient> correctIngredientsForStep = new ArrayList<>();
                    String[] ingredientsForStep = ingredients.split("\n");
                    for (String ingredient : ingredientsForStep) {
                        String[] ingredientProperties = ingredient.split("\t");
                        Double quantity = Double.parseDouble(ingredientProperties[0]);
                        String unit = ingredientProperties[1];
                        String name = ingredientProperties[2];
                        Ingredient correct_ingredient =
                                new Ingredient(name, unit, quantity, irrelevantPositions);
                        correctIngredientsForStep.add(correct_ingredient);
                    }
                    correctIngredientsPerStep.add(correctIngredientsForStep);
                    totalIngredients += ingredientsForStep.length;
                }
            }
            correctIngredients.add(correctIngredientsPerStep);
        }
        return correctIngredients;
    }

    /*
    Sets the ingredients list for all the recipes in progress
     */
    private static void setListIngredients() {
        String allIngredients = initializeIngredientsString();
        String[] ingredientsPerRecipe = allIngredients.split("\n\n");
        for (String ingredientsForOneRecipe : ingredientsPerRecipe) {
            RecipeInProgress rip = new RecipeInProgress(emptyExtractedText, "");
            rip.setIngredientsString(ingredientsForOneRecipe);
            List<ListIngredient> listIngredients = new ArrayList<>();

            String[] ingredients = ingredientsForOneRecipe.split("\n");
            for (String ingredient : ingredients) {
                String[] ingredientProperties = ingredient.split("\t");
                Double quantity = Double.parseDouble(ingredientProperties[0]);
                String unit = ingredientProperties[1];
                String name = ingredientProperties[2];
                ListIngredient listIngredient =
                        new ListIngredient(name, unit, quantity, originalIngredientText, irrelevantPositions);
                listIngredients.add(listIngredient);
            }
            rip.setIngredients(listIngredients);
            rips.add(rip);
        }
    }

    /**
     * Set the steps for all recipes
     */
    private static void setRecipeSteps() {
        String allRecipeSteps = initializeStepsString();
        String[] stepsPerRecipe = allRecipeSteps.split("\n\n");
        for (int i = 0; i < stepsPerRecipe.length; i++) {
            String[] recipeStepsString = stepsPerRecipe[i].split("\n");

            List<RecipeStepInProgress> recipeSteps = new ArrayList<>();
            for (String stepString : recipeStepsString) {
                RecipeStepInProgress recipeStep = new RecipeStepInProgress(stepString);
                recipeSteps.add(recipeStep);
            }
            rips.get(i).setStepsInProgress(recipeSteps);
        }
    }

    /*
    The data for the ingredients for each steps of each recipe
     */
    private static String initializeCorrectIngredientsInStepsString() {
        return ("1.0\t\tunsalted butter\n" +
                "\n" +
                "1.0\ttablespoon\tunsalted butter\n" +
                "1.0\t\tchopped onion\n" +
                "\n" +
                "1.0\t\tcornmeal\n" +
                "1.0\t\tall-purpose flour\n" +
                "1.0\t\tbaking powder\n" +
                "1.0\t\twhite sugar\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tbaking soda\n" +
                "7.0\ttablespoon\tunsalted butter\n" +
                "\n" +
                "1.0\t\tbuttermilk\n" +
                "1.0\t\teggs\n" +
                "1.0\t\tshredded pepperjack cheese\n" +
                "1.0\t\tfrozen corn kernels\n" +
                "1.0\t\tbell peppers\n" +
                "1.0\t\tchopped fresh basil\n" +
                "1.0\t\tchopped onion\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tParmesan cheese\n" +
                "1.0\t\tground black pepper\n" +
                "1.0\t\tgarlic powder\n" +
                "1.0\t\tpuff pastry\n" +
                "1.0\t\tegg white\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tmargarine\n" +
                "1.0\t\thot water\n" +
                "1.0\t\twhite sugar\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tcold water\n" +
                "1.0\t\tactive dry yeast\n" +
                "\n" +
                "3.0\tcup\tall-purpose flour\n" +
                "1.0\t\teggs\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tmargarine\n" +
                "\n" +
                "\n" +
                "1.0\t\twhite sugar\n" +
                "1.0\t\tvegetable oil\n" +
                "1.0\t\teggs\n" +
                "1.0\t\tall-purpose flour\n" +
                "1.0\t\tbaking soda\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tground cinnamon\n" +
                "1.0\t\tground nutmeg\n" +
                "1.0\t\twater\n" +
                "1.0\t\tcooked and mashed sweet potatoes\n" +
                "1.0\t\tchopped pecans\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tbutter\n" +
                "1.0\tteaspoon\twhite sugar\n" +
                "1.0\t\thot milk\n" +
                "1.0\t\tactive dry yeast\n" +
                "\n" +
                "2.0\tcup\tbread flour\n" +
                "0.5\tcup\twhite sugar\n" +
                "1.0\t\teggs\n" +
                "1.0\t\torange juice\n" +
                "1.0\t\torange zest\n" +
                "1.0\t\tsalt\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\twhite sugar\n" +
                "\n" +
                "\n" +
                "1.0\t\tactive dry yeast\n" +
                "1.0\t\tlukewarm milk\n" +
                "1.0\t\twhite sugar\n" +
                "1.0\t\tunbleached all-purpose flour\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tbutter\n" +
                "\n" +
                "1.0\t\tactive dry yeast\n" +
                "1.0\t\tunbleached all-purpose flour\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "7.0\tcup\tall-purpose flour\n" +
                "1.0\t\tshredded Cheddar cheese\n" +
                "1.0\t\tminced jalapeno peppers\n" +
                "7.0\ttablespoon\twhite sugar\n" +
                "1.0\t\tsalt\n" +
                "\n" +
                "1.0\t\thot water\n" +
                "1.0\t\tactive dry yeast\n" +
                "1.0\ttablespoon\twhite sugar\n" +
                "\n" +
                "1.0\t\tvegetable oil\n" +
                "1.0\t\tall-purpose flour\n" +
                "\n" +
                "1.0\t\tall-purpose flour\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tactive dry yeast\n" +
                "1.0\t\twhite sugar\n" +
                "0.5\tcup\twarm water\n" +
                "3.5\tcup\twarm water\n" +
                "1.0\t\thoney\n" +
                "1.0\t\tmolasses\n" +
                "1.0\t\tvegetable oil\n" +
                "1.0\t\teggs\n" +
                "1.0\t\tlemon juice\n" +
                "\n" +
                "5.0\tcup\twhole wheat flour\n" +
                "1.0\t\tflax seed\n" +
                "1.0\t\tcracked wheat\n" +
                "1.0\t\tsunflower seeds\n" +
                "\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\twhole wheat flour\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\trolled oats\n" +
                "1.0\t\tmolasses\n" +
                "1.0\t\tvegetable oil\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tboiling water\n" +
                "\n" +
                "1.0\t\tactive dry yeast\n" +
                "1.0\t\tlukewarm water\n" +
                "1.0\t\twhole wheat flour\n" +
                "2.0\tcup\tbread flour\n" +
                "1.0\t\teggs\n" +
                "\n" +
                "1.0\t\twhole wheat flour\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tall-purpose flour\n" +
                "\n" +
                "1.0\t\twhite sugar\n" +
                "1.0\t\tall-purpose flour\n" +
                "1.0\t\tground cinnamon\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tbaking soda\n" +
                "\n" +
                "1.0\t\teggs\n" +
                "1.0\t\tvegetable oil\n" +
                "1.0\t\tchopped pecans\n" +
                "1.0\t\tfrozen strawberries\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tplain flour\n" +
                "1.0\t\tolive oil\n" +
                "1.0\t\twater\n" +
                "1.0\t\tsea salt\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\ttomato purée\n" +
                "\n" +
                "1.0\t\tshiitake mushrooms\n" +
                "1.0\t\ttomato purée\n" +
                "1.0\t\tprosciutto\n" +
                "1.0\t\tgorgonzola cheese\n" +
                "\n" +
                "1.0\t\tfree-range egg\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tpenne pasta\n" +
                "\n" +
                "1.0\t\tParma ham\n" +
                "1.0\t\tsmall brown chestnut mushrooms\n" +
                "1.0\t\tfull-fat crème fraîche\n" +
                "1.0\t\tpenne pasta\n" +
                "1.0\t\tParmesan\n" +
                "1.0\t\tchopped parsley\n" +
                "1.0\t\tpepper\n" +
                "\n" +
                "1.0\t\tgreen salad\n" +
                "1.0\t\tcrunchy bread\n" +
                "\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tunsalted soft butter\n" +
                "1.0\t\ticing sugar\n" +
                "1.0\t\tvanilla essence\n" +
                "1.0\t\tfree-range egg white\n" +
                "\n" +
                "1.0\t\tplain flour\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tfree-range egg white\n" +
                "1.0\t\ticing sugar\n" +
                "\n" +
                "1.0\t\tready-to-roll icing\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tready-to-roll icing\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tturkey crown\n" +
                "1.0\t\tblack pepper\n" +
                "1.0\t\trashers smoked streaky bacon\n" +
                "\n" +
                "1.0\t\tturkey crown\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tmedium leeks\n" +
                "\n" +
                "1.0\t\tmedium leeks\n" +
                "1.0\t\tthick slices white bread\n" +
                "1.0\t\tmixed nuts and dried fruit\n" +
                "1.0\t\tthyme\n" +
                "1.0\t\tpork sausagemeat\n" +
                "1.0\t\tblack pepper\n" +
                "1.0\t\tbread sauce mix\n" +
                "\n" +
                "1.0\t\tturkey crown\n" +
                "\n" +
                "1.0\t\tpotatoes\n" +
                "\n" +
                "1.0\t\tpotatoes\n" +
                "1.0\t\tsunflower oil\n" +
                "\n" +
                "1.0\t\trosemary\n" +
                "1.0\t\trashers smoked streaky bacon\n" +
                "1.0\t\tchipolata sausages\n" +
                "1.0\t\tpotatoes\n" +
                "1.0\t\trunny honey\n" +
                "\n" +
                "1.0\t\tturkey crown\n" +
                "\n" +
                "1.0\t\tbread sauce mix\n" +
                "125.0\t\tmilk\n" +
                "1.0\t\tdouble cream\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tgrated nutmeg\n" +
                "\n" +
                "1.0\t\tready-prepared carrot sticks\n" +
                "1.0\t\torange juice\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tfreshly chopped parsley\n" +
                "1.0\t\tpinches caster sugar\n" +
                "1.0\t\tblack pepper\n" +

                "\n" +
                "1.0\t\tready-trimmed baby Brussels sprouts\n" +
                "1.0\t\trashers smoked streaky bacon\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tcooked chestnuts\n" +
                "1.0\t\tblack pepper\n" +
                "\n" +
                "1.0\t\treadymade chicken gravy\n" +
                "1.0\t\trashers smoked streaky bacon\n" +
                "1.0\t\truby port red wine\n" +
                "1.0\t\tcranberry sauce\n" +
                "1.0\t\tmixed nuts and dried fruit\n" +
                "1.0\t\tdried sage\n" +
                "1.0\t\tmedium leeks\n" +
                "1.0\t\tblack pepper\n" +
                "\n" +
                "1.0\t\treadymade chicken gravy\n" +
                "1.0\t\tpotatoes\n" +
                "1.0\t\tready-prepared carrot sticks\n" +
                "1.0\t\tbread sauce mix\n" +
                "1.0\t\tturkey crown\n" +
                "1.0\t\tthyme\n" +
                "\n" +
                "\n" +
                "1.0\t\trib-eye of beef\n" +
                "\n" +
                "1.0\t\tred wine\n" +
                "1.0\t\tred wine vinegar\n" +
                "1.0\t\tsugar\n" +
                "1.0\t\tground allspice\n" +
                "1.0\t\tbay leaves\n" +
                "0.5\t\tchopped fresh thyme\n" +
                "\n" +
                "1.0\t\trib-eye of beef\n" +
                "\n" +
                "1.0\t\trib-eye of beef\n" +
                "\n" +
                "1.0\t\tblack peppercorns\n" +
                "1.0\t\tEnglish mustard\n" +
                "1.0\t\trib-eye of beef\n" +
                "1.0\t\tchopped fresh thyme\n" +
                "\n" +
                "1.0\t\trib-eye of beef\n" +
                "\n" +
                "1.0\t\tready-made creamed horseradish\n" +
                "1.0\t\tcrème frâiche\n" +
                "1.0\t\tEnglish mustard\n" +
                "1.0\t\tchopped fresh chives\n" +
                "1.0\t\tground allspice\n" +
                "1.0\t\tsea black pepper\n" +
                "\n" +
                "1.0\t\trib-eye of beef\n" +
                "\n" +
                "1.0\t\trib-eye of beef\n" +
                "1.0\t\troot vegetables\n" +
                "1.0\t\tready-made creamed horseradish\n" +
                "\n" +
                "\n" +
                "1.0\t\tstrong plain flour\n" +
                "1.0\t\teasy-blend dried yeast\n" +
                "1.0\t\tsoft light brown sugar\n" +
                "1.0\t\tsea salt flakes\n" +
                "\n" +
                "1.0\t\twarm water\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tlard\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tlard\n" +
                "\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tlard\n" +
                "\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tlard\n" +
                "\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tlard\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tbutter\n" +
                "1.0\t\tready-made jam\n" +
                "\n" +
                "\n" +
                "1.0\t\tdried goji berries\n" +
                "1.0\t\tswede\n" +
                "\n" +
                "1.0\t\tdried goji berries\n" +
                "\n" +
                "1.0\t\tswede\n" +
                "1.0\t\tdried goji berries\n" +
                "\n" +
                "1.0\t\twhole sea bass\n" +
                "1.0\t\tcracked sea salt\n" +
                "1.0\t\twhite pepper\n" +
                "\n" +
                "1.0\t\tginger ginger\n" +
                "1.0\t\tpalourde clams\n" +
                "\n" +
                "1.0\t\tlight Chinese lager\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tpalourde clams\n" +
                "1.0\t\tcracked sea salt\n" +
                "1.0\t\twhite pepper\n" +
                "1.0\t\tginger ginger\n" +
                "1.0\t\tlight Chinese lager\n" +
                "\n" +
                "1.0\t\tcracked sea salt\n" +
                "1.0\t\twhole sea bass\n" +
                "\n" +
                "1.0\t\tcracked sea salt\n" +
                "1.0\t\twhole sea bass\n" +
                "1.0\t\tpeanut oil\n" +
                "1.0\t\tspring onions\n" +
                "1.0\t\tcoriander cress\n" +
                "1.0\t\tlow-sodium light soy sauce\n" +
                "1.0\t\ttoasted sesame oil\n" +
                "1.0\t\tmicro herbs\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tcracked sea salt\n" +
                "1.0\t\twhole sea bass\n" +
                "1.0\t\tpalourde clams\n" +
                "1.0\t\tdried goji berries\n" +
                "1.0\t\tswede\n" +
                "1.0\t\tblanched baby pak choi halves\n" +
                "1.0\t\tspring onions\n" +
                "1.0\t\tcoriander cress\n" +
                "1.0\t\tmicro herbs\n" +
                "\n" +
                "1.0\t\twhole sea bass\n" +
                "1.0\t\tpalourde clams\n" +
                "1.0\t\tdried goji berries\n" +

                "1.0\t\tcracked sea salt\n" +

                "1.0\t\tswede\n" +
                "1.0\t\tblanched baby pak choi halves\n" +
                "1.0\t\tspring onions\n" +
                "1.0\t\tcoriander cress\n" +
                "1.0\t\tmicro herbs\n" +
                "\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tself-raising flour\n" +
                "1.0\t\tbaking powder\n" +
                "1.0\t\ticing sugar\n" +
                "1.0\t\tvery soft butter\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tpassion fruit\n" +
                "\n" +
                "1.0\t\tmascarpone\n" +
                "1.0\t\tfromage frais\n" +
                "1.0\t\tgolden caster sugar\n" +
                "1.0\t\tvanilla extract\n" +
                "1.0\t\tpassion fruit\n" +
                "\n" +
                "1.0\t\tpassion fruit\n" +
                "1.0\t\ticing sugar\n" +
                "\n" +
                "\n" +
                "1.0\t\tlemon curd\n" +
                "1.0\t\tdouble cream\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tdouble cream\n" +
                "1.0\t\tvanilla ice cream\n" +
                "\n" +
                "1.0\t\tlarge meringues\n" +
                "1.0\t\tdouble cream\n" +
                "1.0\t\thandful blackberries\n" +
                "1.0\t\tpistachio nuts\n" +
                "1.0\t\tvanilla ice cream\n" +
                "\n" +
                "1.0\t\tvanilla ice cream\n" +
                "1.0\t\tdouble cream\n" +
                "1.0\t\tsugar\n" +
                "\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "1.0\t\tsoftened butter\n" +
                "1.0\t\tflour\n" +
                "\n" +
                "1.0\t\tflour\n" +
                "1.0\t\tcake flour cake flour all-purpose flour can\n" +
                "\n" +
                "3.0\t\tflour\n" +
                "1.0\t\tdouble-acting baking powder\n" +
                "1.0\t\tsalt\n" +

                "1.0\tcup\tcake flour cake flour all-purpose flour can\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "1.0\t\tsoftened butter\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "2.0\tcup\tgranulated sugar\n" +
                "1.0\t\tsoftened butter\n" +
                "\n" +
                "1.0\t\teggs\n" +
                "\n" +
                "1.0\t\tvanilla extract\n" +
                "1.0\t\tflour\n" +
                "1.0\t\tmilk\n" +
                "\n" +
                "1.0\t\teggs\n" +
                "\n" +
                "1.0\t\tcake flour cake flour all-purpose flour can\n" +
                "1.0\t\teggs\n" +
                "\n" +
                "1.0\t\tcake flour cake flour all-purpose flour can\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tcake flour cake flour all-purpose flour can\n" +
                "\n" +
                "1.0\t\torange rind\n" +
                "1.0\t\tlemon juice\n" +
                "1.0\t\torange juice\n" +
                "1.0\t\tgranulated sugar\n" +
                "1.0\t\tcake flour cake flour all-purpose flour can\n" +
                "\n" +
                "1.0\t\tlemon juice\n" +
                "1.0\t\tcake flour cake flour all-purpose flour can\n" +
                "\n" +
                "1.0\t\tcake flour cake flour all-purpose flour can\n" +
                "\n" +
                "\n" +
                "1.0\t\tgarlic\n" +
                "1.0\t\tscallions\n" +
                "1.0\t\tchopped fresh Italian parsley\n" +
                "1.0\t\tsalami\n" +
                "1.0\t\tItalian Fontina\n" +
                "1.0\t\ttoasted bread crumbs\n" +
                "0.25\tcup\textra virgin olive oil\n" +
                "\n" +

                "1.0\t\ttenderloin beef\n" +
                "1.0\t\ttoasted bread crumbs\n" +
                "1.0\t\tKosher black pepper\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\ttenderloin beef\n" +
                "3.0\ttablespoon\textra virgin olive oil\n" +
                "1.0\t\tKosher black pepper\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tcumin seeds\n" +
                "1.0\t\tcoriander seeds\n" +
                "1.0\t\tmedium eggs\n" +
                "\n" +
                "1.0\t\tcanola oil\n" +
                "1.0\t\tmedium eggs\n" +
                "1.0\t\tlarge onions\n" +
                "1.0\t\tgarlic\n" +
                "1.0\t\tginger\n" +
                "1.0\t\tjuicy ripe tomatoes\n" +
                "\n" +
                "1.0\t\tjuicy ripe tomatoes\n" +
                "1.0\t\ttomato paste\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tsugar\n" +
                "1.0\t\tchili powder\n" +
                "1.0\t\tground turmeric\n" +
                "1.0\t\tspinach\n" +
                "\n" +
                "1.0\t\tmedium eggs\n" +
                "1.0\t\ttomato paste\n" +
                "\n" +
                "1.0\t\tGround black pepper\n" +
                "1.0\t\tA small bunch of cilantro\n" +
                "1.0\t\thomemade Greek yogurt\n" +
                "\n" +
                "\n" +
                "1.0\t\tbacon\n" +
                "1.0\t\tgarlic cloves\n" +
                "1.0\t\tlarge eggs\n" +
                "1.0\t\tdried bread crumbs\n" +
                "1.0\tteaspoon\tsalt\n" +
                "1.0\t\tground pork\n" +
                "1.0\t\tmint leaves\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tdiced tomatoes in juice\n" +
                "1.0\t\tchipotle canning sauce\n" +
                "1.0\t\tdried oregano\n" +
                "2.0\t\tgarlic cloves\n" +
                "0.5\tteaspoon\tsalt\n" +
                "\n" +
                "1.0\t\tchipotle canning sauce\n" +
                "\n" +

                "1.0\t\tbeef chicken broth\n" +
                "1.0\t\tchipotle canning sauce\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tmint leaves\n" +
                "\n" +
                "1.0\t\tground pork\n" +
                "1.0\t\tbeef chicken broth\n" +
                "1.0\t\tbacon\n" +
                "1.0\t\tdiced tomatoes in juice\n" +
                "1.0\t\tmint leaves\n" +
                "\n" +
                "\n" +
                "1.0\t\trecipe Chocolate Génoise\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "1.0\t\tsweetened chestnut spread\n" +
                "1.0\t\twhite rum\n" +
                "1.0\t\tvanilla extract\n" +
                "\n" +
                "1.0\t\trecipe Chocolate Génoise\n" +
                "\n" +
                "1.0\t\tconfectioners sugar\n" +
                "1.0\t\talmond paste\n" +
                "2.0\ttablespoon\tlight corn syrup\n" +
                "\n" +
                "1.0\t\tcocoa powder\n" +
                "\n" +
                "1.0\t\tconfectioners sugar\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tactive dry yeast\n" +
                "1.0\t\twarm whole milk\n" +
                "0.5\tteaspoon\tgranulated sugar\n" +
                "1.0\tcup\tall purpose flour\n" +
                "1.0\t\tsalt\n" +
                "\n" +
                "2.0\tcup\tall purpose flour\n" +
                "6.0\ttablespoon\tgranulated sugar\n" +
                "2.0\t\tlarge eggs\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tconch meat\n" +
                "\n" +
                "1.0\t\tJuice of lemon\n" +
                "1.0\t\tJuice of orange\n" +
                "1.0\t\tonion\n" +
                "1.0\t\ttomato\n" +
                "1.0\t\tred green bell pepper\n" +
                "1.0\t\tminced habanero chile\n" +
                "1.0\t\tsea salt\n" +
                "\n" +
                "1.0\t\tsea salt\n" +
                "1.0\t\tconch meat\n" +
                "1.0\t\tred green bell pepper\n" +
                "1.0\t\tonion\n" +
                "1.0\t\tJuice of lemon\n" +
                "1.0\t\tJuice of orange\n" +
                "1.0\t\tminced habanero chile\n" +
                "1.0\t\tCaribbean bird chile\n" +
                "\n" +
                "1.0\t\tred green bell pepper\n" +
                "1.0\t\tminced habanero chile\n" +
                "1.0\t\tCaribbean bird chile\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tconch meat\n" +
                "\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\tears\tsweet corn\n" +
                "\n" +
                "1.0\t\tunsalted butter\n" +
                "1.0\t\tchipotle chile powder\n" +
                "\n" +
                "1.0\t\tolive oil\n" +
                "1.0\tears\tsweet corn\n" +
                "\n" +
                "1.0\tears\tsweet corn\n" +
                "1.0\t\tchipotle chile powder\n" +
                "1.0\t\tunsalted butter\n" +
                "1.0\t\tfreshly grated Parmesan cheese\n" +
                "\n" +
                "\n" +
                "1.0\t\tgood quality balsamic vinegar\n" +
                "1.0\t\tsugar\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tGorgonzola cheese\n" +
                "\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\tchopped cilantro\n" +
                "1.0\t\tchopped parsley\n" +
                "1.0\t\tgreen chilies\n" +
                "1.0\t\tginger\n" +
                "1.0\t\tgarlic\n" +
                "1.0\t\tred onion\n" +
                "1.0\t\tground cumin\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tlimes\n" +
                "1.0\t\tgrated jaggery dark brown sugar\n" +
                "\n" +
                "1.0\t\tchicken\n" +
                "\n" +
                "1.0\t\tchicken\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tyellow mustard seeds\n" +
                "1.0\t\tcoriander seeds\n" +
                "1.0\t\tbrown mustard seeds\n" +
                "1.0\t\tapple cider vinegar\n" +
                "1.0\t\tkosher salt\n" +
                "1.0\t\tsugar\n" +
                "1.0\t\tdill\n" +
                "\n" +
                "1.0\t\tboneless chicken thighs\n" +
                "1.0\t\tcoriander seeds\n" +
                "\n" +
                "1.0\t\tA deep-fry thermometer\n" +
                "1.0\t\tVegetable oil\n" +
                "\n" +
                "1.0\t\tbuttermilk\n" +
                "1.0\t\tall-purpose flour\n" +
                "1.0\t\tKosher salt\n" +
                "1.0\t\tboneless chicken thighs\n" +
                "\n" +
                "1.0\t\tVegetable oil\n" +
                "1.0\t\tboneless chicken thighs\n" +
                "\n" +
                "1.0\t\tboneless chicken thighs\n" +
                "1.0\t\tHoney\n" +
                "1.0\t\tKosher salt\n" +
                "1.0\t\tcoriander seeds\n" +
                "\n" +
                "\n" +
                "1.0\t\tsmall-leaved bulk spinach\n" +
                "1.0\t\tlukewarm water\n" +
                "1.0\t\tsalt\n" +
                "\n" +
                "1.0\t\tdark seedless raisins\n" +
                "1.0\t\tlukewarm water\n" +
                "\n" +
                "1.0\t\tolive oil\n" +
                "1.0\t\tsmall onion\n" +
                "1.0\t\tdark seedless raisins\n" +
                "1.0\t\tpignoli\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tFreshly ground black pepper\n" +
                "1.0\t\tsmall-leaved bulk spinach\n" +
                "1.0\t\tDash nutmeg\n" +
                "\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\thoney\n" +
                "\n" +
                "1.0\t\tall-purpose flour\n" +
                "1.0\t\tbaking powder\n" +
                "1.0\t\tbaking soda\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tvegetable oil\n" +
                "1.0\t\thoney\n" +
                "1.0\t\tgranulated sugar\n" +
                "1.0\t\teggs\n" +
                "1.0\t\tvanilla extract\n" +
                "1.0\t\twarm coffee\n" +
                "1.0\t\torange juice\n" +
                "1.0\t\trye\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\t\talmonds\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "\n" +
                "1.0\t\tripe avocado\n" +
                "1.0\t\tlemon juice\n" +
                "1.0\t\tblack pepper\n" +
                "1.0\t\tbagels\n" +
                "1.0\t\tred onion\n" +
                "1.0\t\ttomato\n" +
                "1.0\t\tcapers\n" +
                "\n" +
                "\n" +
                "1.0\t\ttomatoes\n" +
                "1.0\t\tgarlic\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tsweet paprika\n" +
                "1.0\t\ttomato paste\n" +
                "1.0\t\tvegetable oil\n" +
                "\n" +
                "1.0\t\ttomato paste\n" +
                "1.0\t\tlarge eggs\n" +
                "1.0\t\ttomatoes\n" +
                "\n" +
                "\n" +
                "1.0\t\tcooked rice\n" +
                "1.0\t\theavy cream\n" +
                "1.0\t\tunsalted butter\n" +
                "1.0\t\teggs\n" +
                "\n" +
                "1.0\t\tsifted all-purpose flour\n" +
                "1.0\t\tground cinnamon\n" +
                "1.0\t\tground nutmeg\n" +
                "1.0\t\tsalt\n" +
                "1.0\t\tcooked rice\n" +
                "\n" +
                "NO_INGREDIENTS\n" +
                "\n" +
                "1.0\ttablespoon\tunsalted butter\n" +
                "\n" +
                "1.0\t\tcooked rice\n" +
                "1.0\t\tunsalted butter\n" +
                "\n" +
                "1.0\t\tcooked rice\n" +
                "1.0\t\tSugar\n" +
                "1.0\t\tOrange\n" +
                "\n" +
                "\n" +
                "1.0\t\trecipe Chocolate Cake Batter\n" +
                "1.0\t\thalf-sphere cake pans\n" +
                "1.0\t\twaxed paper\n" +
                "1.0\t\tflour\n" +
                "1.0\t\tcake plate\n" +
                "1.0\t\ttoothpicks\n" +
                "1.0\t\tWire cooling racks\n" +
                "\n" +
                "1.0\t\trecipe Chocolate Cake Batter\n" +
                "1.0\t\tGlass measuring cup\n" +
                "1.0\t\trecipe Creamy White Frosting\n" +
                "1.0\t\tOrange gel\n" +
                "\n" +
                "1.0\t\tSmall offset spatula\n" +
                "1.0\t\trecipe Creamy White Frosting\n" +
                "1.0\t\trecipe Chocolate Cake Batter\n" +
                "1.0\t\thalf-sphere cake pans\n" +
                "1.0\t\tcake plate\n" +
                "\n" +
                "1.0\t\tPlastic straw\n" +
                "1.0\t\trecipe Chocolate Cake Batter\n" +
                "1.0\t\thalf-sphere cake pans\n" +
                "\n" +
                "1.0\t\twaffle ice cream cone\n" +
                "1.0\t\trecipe Creamy White Frosting\n" +
                "1.0\t\tSmall offset spatula\n" +
                "1.0\t\trecipe Chocolate Cake Batter\n" +
                "\n" +
                "1.0\t\tbags\n" +
                "1.0\t\tGlass measuring cup\n" +
                "1.0\t\trecipe Creamy White Frosting\n" +
                "\n" +
                "1.0\t\trecipe Creamy White Frosting\n" +
                "\n" +
                "1.0\t\tRolling pin\n" +
                "1.0\t\tblack fondant\n" +
                "1.0\t\tSmall sharp knife\n" +
                "1.0\t\twaxed paper\n" +
                "\n" +
                "1.0\t\tBlack decorating sugar\n" +
                "1.0\t\tSmall plate\n" +
                "1.0\t\trecipe Creamy White Frosting\n" +
                "1.0\t\trecipe Chocolate Cake Batter\n" +
                "\n" +
                "\n" +
                "1.0\t\tsugar\n" +
                "1.0\t\tcold water\n" +
                "1.0\t\tegg whites\n" +
                "1.0\t\tcream light-colored corn syrup\n" +
                "1.0\t\tDouble boiler\n" +
                "1.0\t\tElectric mixer\n" +
                "\n" +
                "1.0\t\tcold water\n" +
                "1.0\t\tDouble boiler\n" +
                "1.0\t\tElectric mixer\n" +
                "1.0\t\tSpoon rubber spatula\n" +
                "1.0\t\tvanilla extract\n" +

                "\n" +
                "\n" +
                "1.0\t\tvegetable shortening\n" +
                "1.0\t\tElectric mixer\n" +
                "1.0\t\tvanilla extract\n" +
                "1.0\t\tlemon extract\n" +
                "\n" +
                "0.5\t\tconfectioners ' sugar\n" +
                "2.0\ttablespoon\tmilk\n" +
                "\n" +
                "\n" +
                "1.0\t\tangel food cake\n" +
                "1.0\t\tcake plate\n" +
                "1.0\tcup\tSeven Minute Frosting\n" +
                "1.0\t\tSmall offset spatula\n" +
                "\n" +
                "1.0\t\tSeven Minute Frosting\n" +
                "1.0\t\tDisposable decorating bag\n" +
                "\n" +
                "1.0\t\tangel food cake\n" +
                "1.0\t\tAssorted eggcup\n" +
                "1.0\t\tSeven Minute Frosting\n" +
                "1.0\t\tDisposable decorating bag\n" +
                "1.0\t\tSmall sugar candy eyes\n" +
                "\n" +
                "\n"
        );
    }

    /*
    Creates the ingredients for the ingredients list
     */
    private static String initializeIngredientsString() {
        return ("0.5\tcup\tunsalted butter\n" +
                "1.0\tcup\tchopped onion\n" +
                "1.75\tcup\tcornmeal\n" +
                "1.25\tcup\tall-purpose flour\n" +
                "0.25\tcup\twhite sugar\n" +
                "1.0\ttablespoon\tbaking powder\n" +
                "1.5\tteaspoon\tsalt\n" +
                "0.5\tteaspoon\tbaking soda\n" +
                "1.5\tcup\tbuttermilk\n" +
                "3.0\t\teggs\n" +
                "1.5\tcup\tshredded pepperjack cheese\n" +
                "1.3333333333333333\tcup\tfrozen corn kernels\n" +
                "2.0\tounce\tbell peppers\n" +
                "0.5\tcup\tchopped fresh basil\n" +
                "\n" +
                "0.5\tcup\tParmesan cheese\n" +
                "0.75\tteaspoon\tground black pepper\n" +
                "0.5\tteaspoon\tgarlic powder\n" +
                "1.0\tpackage\tpuff pastry\n" +
                "1.0\t\tegg white\n" +
                "\n" +
                "2.0\tcup\thot water\n" +
                "0.5\tcup\tmargarine\n" +
                "0.3333333333333333\tcup\twhite sugar\n" +
                "2.0\tteaspoon\tsalt\n" +
                "0.5\tcup\tcold water\n" +
                "2.0\tpackages\tactive dry yeast\n" +
                "5.5\tcup\tall-purpose flour\n" +
                "2.0\t\teggs\n" +
                "\n" +
                "1.5\tcup\twhite sugar\n" +
                "0.5\tcup\tvegetable oil\n" +
                "2.0\t\teggs\n" +
                "1.75\tcup\tall-purpose flour\n" +
                "1.0\tteaspoon\tbaking soda\n" +
                "0.25\tteaspoon\tsalt\n" +
                "0.5\tteaspoon\tground cinnamon\n" +
                "0.5\tteaspoon\tground nutmeg\n" +
                "0.3333333333333333\tcup\twater\n" +
                "1.0\tcup\tcooked and mashed sweet potatoes\n" +
                "0.5\tcup\tchopped pecans\n" +
                "\n" +
                "0.25\tcup\tbutter\n" +
                "1.0\tteaspoon\twhite sugar\n" +
                "1.0\tcup\thot milk\n" +
                "2.0\ttablespoon\tactive dry yeast\n" +
                "6.0\tcup\tbread flour\n" +
                "0.5\tcup\twhite sugar\n" +
                "2.0\t\teggs\n" +
                "1.0\tcup\torange juice\n" +
                "1.0\ttablespoon\torange zest\n" +
                "1.0\tteaspoon\tsalt\n" +
                "\n" +
                "1.0\tteaspoon\tactive dry yeast\n" +
                "1.25\tcup\tlukewarm milk\n" +
                "1.0\ttablespoon\twhite sugar\n" +
                "3.0\tcup\tunbleached all-purpose flour\n" +
                "0.25\tteaspoon\tsalt\n" +
                "2.0\ttablespoon\tbutter\n" +
                "\n" +
                "8.0\tcup\tall-purpose flour\n" +
                "4.0\tcup\tshredded Cheddar cheese\n" +
                "0.75\tcup\tminced jalapeno peppers\n" +
                "0.5\tcup\twhite sugar\n" +
                "1.5\tteaspoon\tsalt\n" +
                "2.0\tcup\thot water\n" +
                "3.0\tpackages\tactive dry yeast\n" +
                "4.0\ttablespoon\tvegetable oil\n" +
                "\n" +
                "2.0\ttablespoon\tactive dry yeast\n" +
                "1.0\tteaspoon\twhite sugar\n" +
                "0.5\tcup\twarm water\n" +
                "3.5\tcup\twarm water\n" +
                "0.25\tcup\thoney\n" +
                "0.25\tcup\tmolasses\n" +
                "0.5\tcup\tvegetable oil\n" +
                "2.0\t\teggs\n" +
                "2.0\ttablespoon\tlemon juice\n" +
                "7.0\tcup\twhole wheat flour\n" +
                "0.25\tcup\tflax seed\n" +
                "0.25\tcup\tcracked wheat\n" +
                "0.25\tcup\tsunflower seeds\n" +
                "4.0\tteaspoon\tsalt\n" +
                "4.0\tcup\tbread flour\n" +
                "\n" +
                "1.0\tcup\trolled oats\n" +
                "0.5\tcup\tmolasses\n" +
                "0.3333333333333333\tcup\tvegetable oil\n" +
                "1.0\tteaspoon\tsalt\n" +
                "1.5\tcup\tboiling water\n" +
                "2.0\ttablespoon\tactive dry yeast\n" +
                "0.5\tcup\tlukewarm water\n" +
                "1.0\tcup\twhole wheat flour\n" +
                "5.0\tcup\tbread flour\n" +
                "2.0\t\teggs\n" +
                "\n" +
                "3.0\tcup\tall-purpose flour\n" +
                "2.0\tcup\twhite sugar\n" +
                "1.0\tteaspoon\tground cinnamon\n" +
                "1.0\tteaspoon\tsalt\n" +
                "1.0\tteaspoon\tbaking soda\n" +
                "4.0\t\teggs\n" +
                "1.25\tcup\tvegetable oil\n" +
                "1.0\tcup\tchopped pecans\n" +
                "1.0\tpackage\tfrozen strawberries\n" +
                "\n" +
                "250.0\tg\tplain flour\n" +
                "8.0\ttbsp\tolive oil\n" +
                "2.0\ttsp\twater\n" +
                "1.0\ttsp\tsea salt\n" +
                "4.0\ttbsp\ttomato purée\n" +
                "60.0\tg\tshiitake mushrooms\n" +
                "4.0\tslices\tprosciutto\n" +
                "100.0\tg\tgorgonzola cheese\n" +
                "1.0\t\tfree-range egg\n" +
                "\n" +
                "350.0\tg\tpenne pasta\n" +
                "160.0\tg\tParma ham\n" +
                "250.0\tg\tsmall brown chestnut mushrooms\n" +
                "200.0\tg\tfull-fat crème fraîche\n" +
                "100.0\tg\tParmesan\n" +
                "2.0\ttbsp\tchopped parsley\n" +
                "1.0\tand\tpepper\n" +
                "1.0\t\tgreen salad\n" +
                "1.0\t\tcrunchy bread\n" +
                "\n" +
                "200.0\tg\tunsalted soft butter\n" +
                "200.0\tg\tcaster sugar\n" +
                "1.0\ttsp\tvanilla essence\n" +
                "1.0\t\tfree-range egg\n" +
                "400.0\tg\tplain flour\n" +
                "1.0\t\tfree-range egg white\n" +
                "250.0\tg\ticing sugar\n" +
                "1.0\t\tcolouring\n" +
                "1.0\t\tready-to-roll icing\n" +
                "\n" +
                "2.5\tkg\tturkey crown\n" +
                "6.0\t\trashers smoked streaky bacon\n" +
                "1.0\t\tlarge knob of butter\n" +
                "2.0\t\tmedium leeks\n" +
                "5.0\t\tthick slices white bread\n" +
                "120.0\tg\tmixed nuts and dried fruit\n" +
                "1.0\t\tthyme\n" +
                "1.0\t\tlemon\n" +
                "450.0\tg\tpork sausagemeat\n" +
                "1.0\t\tblack pepper\n" +
                "1.2\tkg\tpotatoes\n" +
                "4.0\ttablespoon\tsunflower oil\n" +
                "8.0\t\tchipolata sausages\n" +
                "1.0\t\trosemary\n" +
                "2.0\ttbsp\trunny honey\n" +
                "750.0\tml\treadymade chicken gravy\n" +
                "4.0\ttbsp\truby port red wine\n" +
                "2.0\ttbsp\tcranberry sauce\n" +
                "0.5\ttsp\tdried sage\n" +
                "2.0\tg packets\tbread sauce mix\n" +
                "500.0\tml\tmilk\n" +
                "4.0\ttbsp\tdouble cream\n" +
                "25.0\tg\tbutter\n" +
                "1.0\t\tgrated nutmeg\n" +
                "600.0\tg\tready-prepared carrot sticks\n" +
                "2.0\ttablespoon\torange juice\n" +
                "2.0\ttablespoon\tfreshly chopped parsley\n" +
                "2.0\tgood\tpinches caster sugar\n" +
                "300.0\tg\tready-trimmed baby Brussels sprouts\n" +
                "200.0\tg\tcooked chestnuts\n" +
                "1.0\t\tsea black pepper\n" +
                "\n" +
                "2.25\tkg\trib-eye of beef\n" +
                "450.0\tml\tred wine\n" +
                "150.0\tml\tred wine vinegar\n" +
                "1.0\ttbsp\tsugar\n" +
                "1.0\ttsp\tground allspice\n" +
                "2.0\t\tbay leaves\n" +
                "1.0\ttbsp\tchopped fresh thyme\n" +
                "2.0\ttbsp\tblack peppercorns\n" +
                "2.0\ttbsp\tEnglish Dijon mustard\n" +
                "200.0\tml\tcrème frâiche\n" +
                "4.0\ttbsp\tready-made creamed horseradish\n" +
                "1.0\ttsp\tEnglish mustard\n" +
                "1.0\ttbsp\tchopped fresh chives\n" +
                "1.0\t\tsea black pepper\n" +
                "1.0\t\troot vegetables\n" +
                "\n" +
                "500.0\tg\tstrong plain flour\n" +
                "1.0\tg\teasy-blend dried yeast\n" +
                "1.0\ttbsp\tsoft light brown sugar\n" +
                "1.0\ttbsp\tsea salt flakes\n" +
                "350.0\tml\twarm water\n" +
                "1.0\t\tvegetable oil\n" +
                "275.0\tg\tbutter\n" +
                "100.0\tg\tlard\n" +
                "1.0\t\tready-made jam\n" +
                "\n" +
                "1.0\tpurple\tswede\n" +
                "2.0\ttbsp\tdried goji berries\n" +
                "2.0\ttbsp\tmirin\n" +
                "2.0\ttbsp\trice vinegar\n" +
                "2.0\tpinches\tcracked sea salt\n" +
                "2.0\tpinches\tcaster sugar\n" +
                "1.0\tlb\twhole sea bass\n" +
                "1.0\t\twhite pepper\n" +
                "2.5\tcm\tginger ginger\n" +
                "4.0\ttbsp\tlight Chinese lager\n" +
                "100.0\tg\tpalourde clams\n" +
                "3.0\ttbsp\tpeanut oil\n" +
                "2.0\t\tspring onions\n" +
                "1.0\tpinches\tcoriander cress\n" +
                "1.0\ttbsp\tlow-sodium light soy sauce\n" +
                "2.0\ttbsp\ttoasted sesame oil\n" +
                "1.0\t\tblanched baby pak choi halves\n" +
                "1.0\t\tmicro herbs\n" +
                "\n" +
                "175.0\tg\tself-raising flour\n" +
                "1.0\trounded teaspoon\tbaking powder\n" +
                "3.0\t\teggs\n" +
                "175.0\tg\tvery soft butter\n" +
                "175.0\tg\tgolden caster sugar\n" +
                "0.5\ttsp\tvanilla extract\n" +
                "1.0\t\ticing sugar\n" +
                "6.0\t\tpassion fruit\n" +
                "250.0\tg\tmascarpone\n" +
                "1.0\tdessertspoon\tgolden caster sugar\n" +
                "1.0\ttsp\tvanilla extract\n" +
                "200.0\tml\tfromage frais\n" +
                "\n" +
                "290.0\tml\tdouble cream\n" +
                "1.0\tjar\tlemon curd\n" +
                "0.5\t\tlemon\n" +
                "\n" +
                "1.0\tpot\tdouble cream\n" +
                "1.0\t\tlarge meringues\n" +
                "1.0\tgood\thandful blackberries\n" +
                "1.0\t\tpistachio nuts\n" +
                "1.0\t\tvanilla ice cream\n" +
                "1.0\t\tsugar\n" +
                "\n" +
                "1.0\ttablespoon\tsoftened butter\n" +
                "2.0\ttablespoon\tflour\n" +
                "3.0\tcup\tcake flour cake flour all-purpose flour can\n" +
                "4.0\tteaspoon\tdouble-acting baking powder\n" +
                "0.5\tteaspoon\tsalt\n" +
                "8.0\tounce\tunsalted butter\n" +
                "2.0\tcup\tgranulated sugar\n" +
                "4.0\t\teggs\n" +
                "1.0\tcup\tmilk\n" +
                "1.0\tteaspoon\tvanilla extract\n" +
                "0.75\tcup\torange juice\n" +
                "2.0\ttablespoon\tlemon juice\n" +
                "1.0\ttablespoon\torange rind\n" +
                "\n" +
                "2.0\tcloves\tgarlic\n" +
                "4.0\t\tscallions\n" +
                "0.25\tcup\tchopped fresh Italian parsley\n" +
                "4.0\tounce\tsalami\n" +
                "8.0\tounce\tItalian Fontina\n" +
                "0.5\tcup\tfreshly grated Parmigiano-Reggiano\n" +
                "0.5\tcup\ttoasted bread crumbs\n" +
                "0.25\tcup\textra virgin olive oil\n" +
                "1.0\t\ttenderloin beef\n" +
                "1.0\t\tKosher black pepper\n" +
                "\n" +
                "1.0\tteaspoon\tcumin seeds\n" +
                "2.0\tteaspoon\tcoriander seeds\n" +
                "4.0\ttablespoon\tcanola oil\n" +
                "1.5\t\tlarge onions\n" +
                "4.0\tcloves\tgarlic\n" +
                "1.25\t\tginger\n" +
                "2.0\tpound\tjuicy ripe tomatoes\n" +
                "1.0\ttablespoon\ttomato paste\n" +
                "1.0\tteaspoon\tsalt\n" +
                "1.0\tteaspoon\tsugar\n" +
                "0.5\tteaspoon\tchili powder\n" +
                "0.25\tteaspoon\tground turmeric\n" +
                "1.0\tounce\tspinach\n" +
                "6.0\t\tmedium eggs\n" +
                "1.0\t\tGround black pepper\n" +
                "1.0\t\tA small bunch of cilantro\n" +
                "1.0\tcup\thomemade Greek yogurt\n" +
                "\n" +
                "3.0\tslices\tbacon\n" +
                "3.0\t\tgarlic cloves\n" +
                "2.0\t\tlarge eggs\n" +
                "0.5\tcup\tdried bread crumbs\n" +
                "1.0\t\tsalt\n" +
                "1.25\tpound\tground pork\n" +
                "0.5\tcup\tmint leaves\n" +
                "1.0\tcan\tdiced tomatoes in juice\n" +
                "1.0\t\tcanned chipotle chiles\n" +
                "1.0\ttablespoon\tchipotle canning sauce\n" +
                "1.0\tteaspoon\tdried oregano\n" +
                "1.5\tcup\tbeef chicken broth\n" +
                "\n" +
                "1.0\t\trecipe Chocolate Génoise\n" +
                "0.75\tpound\tunsalted butter\n" +
                "0.5\tcup\tsweetened chestnut spread\n" +
                "2.0\ttablespoon\twhite rum\n" +
                "2.0\tteaspoon\tvanilla extract\n" +
                "0.25\tpound\talmond paste\n" +
                "1.0\tcup\tconfectioners sugar\n" +
                "2.0\ttablespoon\tlight corn syrup\n" +
                "1.0\t\tcocoa powder\n" +
                "\n" +
                "2.0\tpackages\tactive dry yeast\n" +
                "1.0\tcup\twarm whole milk\n" +
                "1.5\tteaspoon\tgranulated sugar\n" +
                "5.5\tcup\tall purpose flour\n" +
                "2.0\tteaspoon\tsalt\n" +
                "6.0\ttablespoon\tgranulated sugar\n" +
                "6.0\t\tlarge eggs\n" +
                "1.5\tcup\tunsalted butter\n" +
                "1.0\tegg\tlightly beaten milk\n" +
                "\n" +
                "1.0\tpound\tconch meat\n" +
                "1.0\t\tJuice of orange\n" +
                "1.0\t\tJuice of lime\n" +
                "1.0\t\tJuice of lemon\n" +
                "1.0\t\tonion\n" +
                "1.0\t\ttomato\n" +
                "0.5\t\tred green bell pepper\n" +
                "0.5\tteaspoon\tminced habanero chile\n" +
                "1.0\t\tCaribbean bird chile\n" +
                "0.5\tteaspoon\tsea salt\n" +
                "\n" +
                "4.0\tears\tsweet corn\n" +
                "4.0\ttablespoon\tunsalted butter\n" +
                "1.0\tteaspoon\tchipotle chile powder\n" +
                "1.0\t\tVegetable oil\n" +
                "0.25\tcup\tolive oil\n" +
                "0.5\tcup\tfreshly grated Parmesan cheese\n" +
                "\n" +
                "0.6666666666666666\tcup\tgood quality balsamic vinegar\n" +
                "2.0\ttablespoon\tsugar\n" +
                "2.0\tteaspoon\tfreshly ground rose other peppercorns\n" +
                "2.0\t\tfreestone peaches\n" +
                "2.0\tounce\tGorgonzola cheese\n" +
                "\n" +
                "0.75\tcup\tchopped cilantro\n" +
                "0.5\tcup\tchopped parsley\n" +
                "3.0\t\tgreen chilies\n" +
                "1.0\tpiece\tginger\n" +
                "2.0\tcloves\tgarlic\n" +
                "1.0\t\tred onion\n" +
                "1.0\tteaspoon\tground cumin\n" +
                "1.5\tteaspoon\tsalt\n" +
                "2.0\t\tlimes\n" +
                "1.0\ttablespoon\tgrated jaggery dark brown sugar\n" +
                "1.0\t\tchicken\n" +
                "2.0\ttablespoon\tbutter\n" +
                "\n" +
                "1.0\ttablespoon\tyellow mustard seeds\n" +
                "1.0\ttablespoon\tbrown mustard seeds\n" +
                "1.5\tteaspoon\tcoriander seeds\n" +
                "1.0\tcup\tapple cider vinegar\n" +
                "0.6666666666666666\tcup\tkosher salt\n" +
                "0.3333333333333333\tcup\tsugar\n" +
                "0.25\tcup\tdill\n" +
                "8.0\t\tboneless chicken thighs\n" +
                "1.0\t\tVegetable oil\n" +
                "2.0\tcup\tbuttermilk\n" +
                "2.0\tcup\tall-purpose flour\n" +
                "1.0\t\tHoney\n" +
                "1.0\t\tA deep-fry thermometer\n" +
                "\n" +
                "3.0\tpound\tsmall-leaved bulk spinach\n" +
                "1.0\t\tsalt\n" +
                "0.5\tcup\tdark seedless raisins\n" +
                "1.0\tcup\tlukewarm water\n" +
                "6.0\ttablespoon\tolive oil\n" +
                "0.5\t\tsmall onion\n" +
                "0.25\tcup\tpignoli\n" +
                "1.0\t\tFreshly ground black pepper\n" +
                "1.0\t\tDash nutmeg\n" +
                "\n" +
                "3.5\tcup\tall-purpose flour\n" +
                "1.0\ttablespoon\tbaking powder\n" +
                "1.0\tteaspoon\tbaking soda\n" +
                "0.5\tteaspoon\tsalt\n" +
                "4.0\tteaspoon\tground cinnamon\n" +
                "0.5\tteaspoon\tground cloves\n" +
                "0.5\tteaspoon\tground allspice\n" +
                "1.0\tcup\tvegetable oil\n" +
                "1.0\tcup\thoney\n" +
                "1.5\tcup\tgranulated sugar\n" +
                "0.5\tcup\tbrown sugar\n" +
                "3.0\t\teggs\n" +
                "1.0\tteaspoon\tvanilla extract\n" +
                "1.0\tcup\twarm coffee\n" +
                "0.5\tcup\torange juice\n" +
                "0.25\tcup\trye\n" +
                "0.5\tcup\talmonds\n" +
                "\n" +
                "1.0\t\tripe avocado\n" +
                "1.0\tteaspoon\tlemon juice\n" +
                "1.0\t\tblack pepper\n" +
                "2.0\t\tbagels\n" +
                "2.0\tslices\tsmoked salmon\n" +
                "2.0\tslices\tred onion\n" +
                "4.0\tslices\ttomato\n" +
                "1.0\tteaspoon\tcapers\n" +
                "\n" +
                "2.0\tpound\ttomatoes\n" +
                "6.0\tcloves\tgarlic\n" +
                "2.0\tteaspoon\tsalt\n" +
                "1.0\tteaspoon\tsweet paprika\n" +
                "2.0\tteaspoon\ttomato paste\n" +
                "0.25\tcup\tvegetable oil\n" +
                "6.0\t\tlarge eggs\n" +
                "\n" +
                "1.5\tcup\tcooked rice\n" +
                "2.0\tcup\theavy cream\n" +
                "2.0\ttablespoon\tunsalted butter\n" +
                "2.0\t\teggs\n" +
                "0.5\tcup\tsifted all-purpose flour\n" +
                "1.0\tteaspoon\tground cinnamon\n" +
                "0.5\tteaspoon\tground nutmeg\n" +
                "0.5\tteaspoon\tsalt\n" +
                "1.0\t\tSugar\n" +
                "1.0\tslices\tOrange\n" +
                "\n" +
                "2.0\ttablespoon\tshortening\n" +
                "2.0\ttablespoon\tflour\n" +
                "1.0\t\trecipe Chocolate Cake Batter\n" +
                "1.0\t\trecipe Creamy White Frosting\n" +
                "1.0\t\tGreen gel\n" +
                "1.0\t\tOrange gel\n" +
                "1.0\tsmall\twaffle ice cream cone\n" +
                "4.0\tounce\tblack fondant\n" +
                "1.0\t\tBlack decorating sugar\n" +
                "2.0\t6-inch\thalf-sphere cake pans\n" +
                "1.0\t\tbowls\n" +
                "1.0\t\twaxed paper\n" +
                "1.0\t\tToothpicks\n" +
                "1.0\t\tWire cooling racks\n" +
                "1.0\t\tGlass measuring cup\n" +
                "1.0\t\tSmall offset spatula\n" +
                "1.0\t\tcake plate\n" +
                "1.0\t\tPlastic straw\n" +
                "2.0\tdecorating\tbags\n" +
                "1.0\t\tRolling pin\n" +
                "1.0\t\tSmall sharp knife\n" +
                "1.0\t\tSmall plate\n" +
                "1.0\t\tCompote\n" +
                "\n" +
                "1.5\tcup\tsugar\n" +
                "0.3333333333333333\tcup\tcold water\n" +
                "2.0\t\tegg whites\n" +
                "0.25\tteaspoon\tcream light-colored corn syrup\n" +
                "1.0\tteaspoon\tvanilla extract\n" +
                "1.0\t\tDouble boiler\n" +
                "1.0\t\tElectric mixer\n" +
                "1.0\t\tSpoon rubber spatula\n" +
                "\n" +
                "1.0\tcup\tvegetable shortening\n" +
                "1.5\tteaspoon\tvanilla extract\n" +
                "0.5\tteaspoon\tlemon extract\n" +
                "1.0\tpound\tconfectioners ' sugar\n" +
                "3.0\ttablespoon\tmilk\n" +
                "1.0\t\tElectric mixer\n" +
                "\n" +
                "1.0\t\tangel food cake\n" +
                "2.0\trecipes\tSeven Minute Frosting\n" +
                "1.0\t\tSmall sugar candy eyes\n" +
                "1.0\t\tcake plate\n" +
                "1.0\t\tSmall offset spatula\n" +
                "1.0\t\tDisposable decorating bag\n" +
                "1.0\t\tAssorted eggcup");
    }

    /*
    gives the descriptions of the steps for this dataset
     */
    private static String initializeStepsString() {
        return ("Preheat oven to 400 degrees F (205 degrees C). Butter a 9x9x2 inch baking pan.\n" +
                "Melt 1 tablespoon butter in medium nonstick skillet over medium-low heat. " +
                "Add onion and saute until tender, about 10 minutes. Cool.\n" +
                "Mix cornmeal with the flour, baking powder, sugar, salt, and baking soda in large bowl. " +
                "Add 7 tablespoons butter and rub with fingertips until mixture resembles coarse meal.\n" +
                "Whisk buttermilk and eggs in medium bowl to blend. " +
                "Add buttermilk mixture to dry ingredients and stir until blended. " +
                "Mix in cheese, corn, red peppers, basil, and onion. Transfer to prepared pan.\n" +
                "Bake cornbread until golden and tester inserted comes out clean, about 45 minutes. " +
                "Cool 20 minutes in pan. Cut cornbread into squares.\n" +
                "\n" +
                "Combine parmesan cheese, pepper and garlic powder. Unfold pastry sheets onto cutting board. " +
                "Brush lightly with egg white; sprinkle each sheet with 1/4 of the cheese mixture. " +
                "Lightly press into pastry, turn over; repeat. Cut each sheet into 12 (1-inch) strips; twist.\n" +
                "Place on ungreased cookie sheet and bake in 350 degrees F (175 degrees C) oven for 15 minutes or " +
                "until golden brown.\n" +
                "\n" +
                "Melt margarine in hot water. Add sugar and salt and stir. " +
                "Add cold water and yeast. Stir to dissolve yeast.\n" +
                "Add 3 cups flour and mix. Add eggs and 2 1/2 - 3 cups more flour. " +
                "Mix, cover and let rise until dough doubles in size.\n" +
                "Punch down and let rise 30 more minutes or until doubles.\n" +
                "Make walnut size balls of dough. Place about 2 inches apart in well-buttered 9 x 13 inch pan. " +
                "Bake in a preheated 350 degrees F (175 degrees C) oven for 30-45 minutes. " +
                "Brush top of rolls with margarine while hot.\n" +
                "\n" +
                "Combine sugar and oil; beat well. Add eggs and beat. Combine flour, baking soda, salt, cinnamon and " +
                "nutmeg. " +
                "Stir flour mixture into egg mixture alternately with water. Stir in sweet potatoes and chopped nuts" +
                ".\n" +
                "Pour batter into greased 9x5 inch loaf pan (or 2 small loaf pans). " +
                "Bake at 350 degrees F (175 degrees C) for about one hour.\n" +
                "\n" +
                "Stir butter and 1 teaspoon sugar into the hot milk until butter is melted. " +
                "When mixture is lukewarm, stir in yeast and set aside for 5 minutes.\n" +
                "When mixture is creamy, transfer to a large mixing bowl. Mix in 2 cups of bread flour. " +
                "Add 1/2 cup sugar, eggs, orange juice, orange zest, and salt and beat until combined. " +
                "Add remaining flour, mixing well after each addition, until it pulls away from the sides of the bowl" +
                ".\n" +
                "Knead for about 10 minutes. Transfer dough to a greased bowl, " +
                "cover with plastic wrap, and let rise until doubled, about 1 hour. " +
                "If time permits, you can punch dough down, cover it, and let it rise again.\n" +
                "Transfer dough to a lightly floured work surface. Cut dough into 3 balls, " +
                "cover with plastic wrap, and let rest for 10 minutes.\n" +
                "Preheat oven to 375 degrees F (190 degrees C). Lightly grease two baking sheets or line with " +
                "parchment paper.\n" +
                "Shape each piece of dough into braided loaves or buns; place on baking sheets. " +
                "Let rise until doubled, about 30 minutes.\n" +
                "Bake until rolls or loaves are golden brown, 10 to 12 minutes for individual buns, about 20 minutes " +
                "for braids. " +
                "Frost rolls, if desired (see Cook's Note) or sprinkle with confectioners' sugar.\n" +
                "\n" +
                "In a small bowl, dissolve the yeast in the milk and add the sugar. " +
                "In another bowl, sift the flour and salt together and add the cooled melted butter.\n" +
                "Add the yeast mixture to the flour mixture, and turn out onto a floured counter " +
                "and knead until the dough is smooth and elastic. Place the dough in an oiled bowl, " +
                "cover with a clean towel and let rise in a warm, draft free place to 45 minutes.\n" +
                "Turn dough out onto the freshly floured board and shape into 9 balls. " +
                "Place dough balls into a buttered and floured 9 inch square pan. " +
                "Let them sit, covered for another 15 minutes to rise again. Preheat the oven to 425 degrees F (220 " +
                "degrees C).\n" +
                "Bake for 15-20 minutes until browned and puffed. Split open and serve warm.\n" +
                "\n" +
                "In a very large bowl, combine 7 cups of flour, cheese, jalapenos, 7 tablespoons sugar and the salt; " +
                "mix well.\n" +
                "In a separate bowl, combine the water, yeast and remaining 1 tablespoon sugar. " +
                "Let sit about 10 minutes; stir until all yeast is dissolved.\n" +
                "Add the oil to the liquid mixture, stirring . Add half of the liquid mixture to the flour mixture. " +
                "Mix with hands to moisten flour as much as possible. " +
                "Add remaining liquid mixture to dough and mix until flour is thoroughly incorporated.\n" +
                "Turn onto a lightly floured surface and knead by hand until smooth and elastic to the touch, about " +
                "15 minutes, " +
                "gradually adding only enough additional flour to keep dough from sticking.\n" +
                "Place in a large greased bowl and invert dough so top is greased; " +
                "cover with a dry towel and let stand in a warm place (90 - 100F) until doubled in size, about 1 hour" +
                ". Punch down dough.\n" +
                "To Make Bread: Divide dough into 3 equal portions. Form each into a ball, " +
                "then stretch out dough with both hands and tuck edges under to form a smooth surface. " +
                "Pop any large air bubbles by pinching them. Form into loaves. " +
                "(Note: I like to use a rolling pin and roll out dough, which pops all bubbles easily and quickly.) " +
                "Place in 3 greased 8 1/2 x 4 1/2 inch loaf pans. " +
                "Cover with towel again and allow to rise until almost doubled in size, about 45 minutes to 1 hour.\n" +
                "Bake at 325 degrees F (165 degrees C) until dark brown and done, about 1 hour, " +
                "rotating the pans after 25 minutes for more even browning. Remove from pan as soon as bread will " +
                "easily lift out, " +
                "after about 5 to 10 minutes. Let cool about 1 hour before slicing.\n" +
                "\n" +
                "In a small bowl, dissolve the yeast and sugar in 1/2 cup warm water. " +
                "In a large bowl, mix remaining 3 1/2 cups warm water, honey, molasses, oil, eggs and lemon juice. " +
                "Mix well. Add yeast mixture and stir.\n" +
                "Gradually add 5 cups whole wheat flour beating well after each addition. " +
                "Add the flax, cracked wheat and sunflower seeds, stir well.\n" +
                "Let stand for 20 minutes, until mixture is very light. " +
                "Stir in salt and the rest of the flours until dough pulls away from the sides of the bowl.\n" +
                "Knead 10 to 15 minutes until dough is smooth and elastic. " +
                "Put into a greased bowl and cover, let rise in the oven with light on until doubled, about 1 hour.\n" +
                "Punch down and shape into 6 round balls. Cover and let rest for 20 minutes.\n" +
                "Form into loaves and let rise covered in oven until doubled. " +
                "Bake at 375 degrees F (190 degrees C) 25-35 minutes.\n" +
                "\n" +
                "Combine oats, molasses, oil, salt and boiling water. Let cool to about 105 degrees F.\n" +
                "Dissolve the yeast in the warm water and let stand for 5 minutes or until creamy. " +
                "Stir the yeast into oat mixture and mix well. Add whole wheat flour, 2 cups bread flour, and the " +
                "eggs. " +
                "Mix until well combined.\n" +
                "Stir in enough of the remaining flour to make a soft dough. " +
                "Turn dough out to a floured counter and knead for about 10 minutes. " +
                "Place the dough in a well-greased bowl and cover with greased plastic wrap. " +
                "Let the dough rest in the refrigerator overnight.\n" +
                "Preheat oven to 375 degrees F (190 degrees C). Grease 3 regular loaf pans and 4 mini loaf pans.\n" +
                "Transfer the dough onto a floured surface and divide it into 4 pieces. " +
                "Shape the dough into loaves and place them in the pans, seam-side down. " +
                "Let rise until doubled, about 90 minutes. " +
                "Bake in the preheated oven until the loaves are golden brown and sound hollow when tapped, about 30 " +
                "minutes.\n" +
                "\n" +
                "Grease and flour a 9 x 5 inch pan well. Preheat oven to 350 degrees F (175 degrees C).\n" +
                "In a large mixing bowl, sift together sugar, flour, cinnamon, salt, and baking soda.\n" +
                "In a smaller bowl, beat the eggs and oil. Stir in pecans and strawberries. " +
                "Add egg mixture to the sifted ingredients, and stir until just combined.\n" +
                "Bake for 1 hour, or until tester inserted in the center comes out clean.\n" +
                "\n" +
                "Preheat the oven to 200C/400F/Gas 6.\n" +
                "For the pizza base, place the flour, oil, water and salt into a food processor and blend together " +
                "until a dough is formed. " +
                "Tip out onto a floured work surface and knead. Shape into a round base about 20cm/8in wide.\n" +
                "Place into a frying pan over a high heat and brown the base, then using a mini-blowtorch, crisp the " +
                "top of the pizza. " +
                "(Alternatively you can do this under the grill.)\n" +
                "For the topping, spread tomato purée over the top of the base.\n" +
                "Fry the mushrooms in a dry frying pan then scatter over the tomato purée. " +
                "Arrange the prosciutto and cheese on top.\n" +
                "Crack an egg into the middle, then place into the oven for five minutes to finish cooking.\n" +
                "Serve on a large plate, and slice into wedges to serve.\n" +
                "\n" +
                "Cook the pasta in a pan of boiling salted water according to the packet instructions. Drain and set " +
                "aside\n" +
                "Heat a frying pan until hot. Add the pieces of Parma ham and fry until crisp, " +
                "remove half of the ham onto a plate and set aside. Add the mushrooms to the pan and fry for two " +
                "minutes. " +
                "Add the crème fraîche and bring up to the boil. Add the pasta, Parmesan and parsley and toss " +
                "together over the heat. " +
                "Season well with salt and pepper.\n" +
                "Serve with a green salad and crunchy bread.\n" +
                "\n" +
                "To make the basic dough, line a baking tray with baking parchment (you may need two baking trays or," +
                " alternatively, " +
                "cook the biscuits in batches).\n" +
                "In a bowl, use a wooden spoon or a mixer to cream together the butter, sugar and vanilla essence " +
                "until just creamy. " +
                "Do not over work the batter at this stage. Beat in the egg until well combined.\n" +
                "Add the flour and mix on a low speed until a dough forms. In the bowl, use your hands to lightly " +
                "knead the dough into a ball. " +
                "Wrap it in cling film and refrigerate for least an hour.\n" +
                "Roll out the dough on a lightly floured work surface to a depth of about 5mm, " +
                "if possible use spacers to achieve an even thickness. " +
                "Cut the desired shapes. For the duck biscuits: use a duck-shaped cutter and a round cutter for the " +
                "base " +
                "(the base should be approximately the same size as the duck). " +
                "For the tree biscuits: cut two simple tree shapes out using either " +
                "a cutter or a template and cut one tree shape in half vertically. " +
                "For the ladybird biscuits: cut out two rounds, one slightly smaller than the other (approx. 5cm/2in " +
                "and 3cm/1¼in). " +
                "For the lamb biscuits: use a cutter or a template to cut out two lamb shapes " +
                "for each finished biscuit and cut two additional leg shapes separately " +
                "(for each finished lamb biscuit you should have two lamb shapes and two leg shapes). " +
                "For the sitting down person biscuits: cut a gingerbread person out of the dough and then cut off the" +
                " legs. " +
                "Trim about 2mm from around the edges of the legs and cut two slots out of the bottom of the " +
                "gingerbread body, " +
                "about the depth of the legs (the legs will slot in here once baked). Also cut out two small ovals " +
                "for feet.\n" +
                "Transfer the shapes onto the baking sheet using a palate knife and chill again for about 30 minutes." +
                " " +
                "(This is important to prevent the biscuits losing their shape when baked.)\n" +
                "Preheat the oven to 180C/350F/Gas 4.\n" +
                "Bake the biscuits for 6–10 minutes, depending on size, until lightly golden-brown at the edges. " +
                "Transfer to a wire rack to cool completely.\n" +
                "For the icing, lightly beat the egg white in a large bowl. " +
                "Add the icing sugar and mix slowly at first to avoid an icing sugar cloud. " +
                "Once combined, whisk or beat for about five minutes if using an electric beater or whisk, " +
                "or for longer if using a wooden spoon. " +
                "Continue whisking until the ingredients form a thick, smooth paste that is bright white in colour. " +
                "If you are using colours, separate the icing into various bowls and colour accordingly.\n" +
                "To decorate the duck biscuits, thinly roll out the yellow ready-to-roll icing and cover the duck " +
                "shapes. " +
                "Pipe on any decoration desired for the eyes, beaks and wings. " +
                "Position a wire rack over a container that will hold it at a suitable height to lean the stand up " +
                "biscuit against. " +
                "Use the flooding technique to ice the biscuit circles (see step 10 for more information) to " +
                "represent a pond. " +
                "While the icing is still wet, place the round biscuit under the edge of the wire rack. " +
                "Stand the duck biscuit in position on the wet icing on the round, using the rack as support to keep " +
                "it upright. " +
                "Allow the icing to set completely before carefully removing the support rack.\n" +
                "To flood ice the biscuits, pipe an outline around the edge of the biscuit using either a fine nozzle" +
                " " +
                "or by snipping the very end off a disposable piping bag. Let the icing set for a few minutes. " +
                "For flooding the icing needs to be slightly runnier, so add a few drops of water. Pipe or spoon " +
                "enough " +
                "of the runnier icing into the centre of the iced shape to completely fill it – " +
                "don’t worry about it being neat at this point. " +
                "Use a toothpick to spread the icing evenly inside the hard icing ‘wall’. If the surface of the icing" +
                " isn’t flat, " +
                "gently shake the biscuit back and forth until you have a smooth covering.\n" +
                "To decorate the tree biscuits, pipe a line of green icing all around the edge " +
                "and cover the biscuits using the flooding method described in step 10. Once the icing is completely " +
                "dry, " +
                "use some leftover icing to pipe a zig zag along the bare edge of each half tree " +
                "and attach them to the middle of the whole trees Repeat at the back of the biscuit with the " +
                "remaining tree halves. " +
                "You may have to hold the half trees in place while the icing sets.\n" +
                "To decorate the ladybird biscuits, brush the rounds with cooled boiled water and cover the larger " +
                "round " +
                "with a circle of pink or red ready-to-roll icing and the smaller in white ready-to-roll icing. " +
                "Attach the white round to the base using a small ball of ready-to-roll icing and tilt on an angle. " +
                "Finish decorating the ladybird by piping on a face and spots in black icing.\n" +
                "To decorate the lamb biscuits, ice half the bodies of the lambs using the flooding technique in step" +
                " 10 - " +
                "keeping the legs free of icing. Once the icing is dry, spread an undecorated lamb biscuit with icing" +
                " " +
                "and sandwich to the back of the decorated biscuit. Dot some icing on the back of the individual lamb" +
                " legs " +
                "and stick over the whole lamb’s legs to give a 3D effect. " +
                "When the icing is dry the biscuits should stand up without support. " +
                "If desired, decorate the lamb’s legs with icing.\n" +
                "To decorate the sitting down person biscuits, decorate the biscuit bodies as desired. " +
                "Slot the legs sideways into the precut slots so the person is sitting down with their legs stretched" +
                " out " +
                "in front of them. Attach the feet to the bottom of the legs using icing and leave to dry.\n" +
                "\n" +
                "Preheat the oven to 220C/200C Fan/Gas 7.\n" +
                "For the turkey and stuffing, put the turkey crown on a lightly oiled baking tray and pat dry with " +
                "kitchen paper. " +
                "Season with lots of coarsely ground black pepper. Cover the breast with bacon rashers, " +
                "then cover the turkey with lightly oiled foil and roast for 1 hour.\n" +
                "While the turkey is roasting, prepare the stuffing. Melt the butter in a large non-stick frying pan." +
                " " +
                "Thinly slice the leeks. Add to the pan and cook over a low heat for 3–4 minutes, or until softened, " +
                "stirring regularly.\n" +
                "Tip the leeks into a large, shallow ovenproof dish – a lasagne dish would work well. " +
                "Tear the bread into small cubes and don’t worry about the crusts, just add those too. " +
                "Tip the nuts and fruit onto a board and cut any large nuts in half or quarters, then add to the " +
                "bread and leeks. " +
                "Add roughly three-quarters of the thyme to the stuffing mixture and sprinkle the lemon zest on top. " +
                "Add the sausagemeat and season with a little salt and lots of black pepper. " +
                "Mix all the ingredients thoroughly with your hands, leaving it fairly chunky. " +
                "Spread loosely over the dish and set aside.\n" +
                "When the turkey has been cooking for 1 hour, take out of the oven and reduce the temperature to " +
                "200C/180C Fan/Gas 7. " +
                "Take the turkey crown carefully off the tray and place directly on top of the stuffing. " +
                "Place in the oven and roast the turkey and stuffing for 30 minutes, " +
                "then remove the foil and cook for a further 15 minutes, or until the turkey " +
                "is fully cooked and the juices run clear when the thickest part is pierced with a skewer.\n" +
                "Meanwhile, for the roast potatoes, put the potatoes in a large saucepan or flameproof casserole and " +
                "cover with cold water. " +
                "Place over a high heat and bring to the boil. Reduce the heat slightly and simmer for 3 minutes. " +
                "Drain the potatoes in a large colander and return to the saucepan. " +
                "Shake vigorously to knock the potatoes about and scuff up the surface – " +
                "this will make them much crisper when they roast.\n" +
                "Pour the oil over and toss together well. Scatter in a single layer onto a baking tray and roast for" +
                " an hour, " +
                "or until the potatoes are golden, crisp and tender in the centre.\n" +
                "Meanwhile, for the pigs in blankets, tuck a small sprig of rosemary under the bacon on each sausage." +
                " " +
                "Place on a lightly oiled baking tray with the potatoes and drizzle with the honey. Roast for 15–20 " +
                "minutes, " +
                "or until lightly browned and cooked through.\n" +
                "Remove the turkey from the oven, cover loosely with kitchen foil " +
                "and a couple of dry tea towels and leave to rest for 15 minutes.\n" +
                "For the bread sauce (if making), put the bread sauce mix " +
                "in a non-stick saucepan and stir in roughly a quarter of the milk. " +
                "Mix until well combined, then add the remaining milk and bring to a gentle simmer. Cook for 3–4 " +
                "minutes, " +
                "stirring regularly, until thick and smooth, then add the cream and butter and cook for 1–2 minutes " +
                "more. " +
                "Pour into a warmed dish, sprinkle with a little nutmeg, and keep warm until ready to serve.\n" +
                "For the carrots, put the carrots in a microwave-proof serving dish and pour over the orange juice, " +
                "dot with butter, " +
                "sprinkle with parsley, add sugar and season with a little salt and lots of black pepper. " +
                "Cover with cling film or a plate and cook on HIGH for 8 minutes, or until just tender, " +
                "stirring halfway through cooking.\n" +
                "For the sprouts, put the butter, bacon, sprouts and chestnuts in a large non-stick saucepan " +
                "or sauté pan and season well with black pepper. Add 200ml/7fl oz cold water and bring to the boil. " +
                "Cook for about 5 minutes, stirring regularly until the sprouts are tender and the liquid has " +
                "evaporated.\n" +
                "Meanwhile, for the gravy, put the gravy, bacon, port, cranberry sauce and dried sage, if using, " +
                "in a medium saucepan and bring to a simmer. Reduce the heat and cook gently for 5 minutes, stirring " +
                "occasionally. " +
                "Season with black pepper and a little salt, if needed. Keep warm until ready to serve.\n" +
                "To serve, warm your serving plates. Strain the gravy into warmed gravy boats and put " +
                "on the table along with the roast potatoes, pigs in blankets, carrots and bread sauce (if making). " +
                "Sprinkle the turkey with the remaining thyme and serve in the roasting dish along with the stuffing." +
                " " +
                "Carve at the table.\n" +
                "\n" +
                "Place the rib-eye of beef into a large non-metallic dish.\n" +
                "In a jug, mix together the red wine, vinegar, sugar, allspice, " +
                "bay leaf and half of the thyme until well combined.\n" +
                "Pour the mixture over the beef, turning to coat the joint evenly in the liquid. Cover the dish " +
                "loosely " +
                "with cling film and set aside to marinate in the fridge for at least four hours, turning " +
                "occasionally. " +
                "(The beef can be marinated for up to two days.)\n" +
                "When the beef is ready to cook, preheat the oven to 190C/375F/Gas 5. Lift the beef from the " +
                "marinade, " +
                "allowing any excess liquid to drip off, and place on a plate, loosely covered, " +
                "until the meat has returned to room temperature.\n" +
                "Sprinkle the crushed peppercorns and the remaining thyme onto a plate. Spread the mustard evenly " +
                "all over the surface of the beef, then roll the beef in the peppercorn and thyme mixture to coat.\n" +
                "Place the crusted beef into a roasting tin and roast in the oven for 1 hour 20 minutes (for " +
                "medium-rare) " +
                "or 1 hour 50 minutes (for well-done).\n" +
                "Meanwhile, for the horseradish cream, mix the crème frâiche, creamed horseradish, mustard and chives" +
                " " +
                "together in a bowl until well combined. Season, to taste, with salt and freshly ground black pepper," +
                " " +
                "then spoon into a serving dish and chill until needed.\n" +
                "When the beef is cooked to your liking, transfer to a warmed platter and cover with aluminium foil, " +
                "then set aside to rest in a warm place for 25-30 minutes.\n" +
                "To serve, carve the rib-eye of beef into slices and arrange on warmed plates. " +
                "Spoon the roasted root vegetables alongside. " +
                "Serve with the horseradish cream.\n" +
                "\n" +
                "In a large bowl, mix together the flour, yeast, sugar and salt until well combined.\n" +
                "Make a well in the centre of the mixture, then gradually add the water in a thin stream, " +
                "stirring well with a wooden spoon, until the mixture comes together as a dough. " +
                "(NB: You may not need to use all of the water.)\n" +
                "Turn the dough out onto a lightly floured work surface and knead lightly for 8-10 minutes, " +
                "or until smooth and elastic.\n" +
                "Transfer the kneaded dough to a clean, greased bowl and cover with a greased sheet of cling film. " +
                "Set aside in a warm place to rise (prove) for at least one hour, or until the dough has doubled in " +
                "size.\n" +
                "Meanwhile, in a separate bowl, cream together the butter and lard until well combined. " +
                "Divide the mixture into four equal portions.\n" +
                "When the dough has proved, turn it out onto a lightly floured surface and knead for a further 1-2 " +
                "minutes.\n" +
                "Roll out the dough into a 40cm x 20cm/16in x 8in rectangle, about 1cm/½in thick.\n" +
                "Turn the dough around so that the shortest edge is facing you. Spread one portion " +
                "of the butter and lard mixture over the bottom two-thirds of the dough rectangle.\n" +
                "Fold the remaining one-third of the dough rectangle over onto the butter and lard mixture to " +
                "cover the centre section of the dough rectangle. Fold the other end of the dough rectangle over the " +
                "folded dough, " +
                "so that the dough ends up three times its original thickness.\n" +
                "Roll the dough out again to a 40cm x 20cm/16in x 8in rectangle, about 1cm/½in thick. Repeat the " +
                "process of spreading " +
                "and folding with another portion of the butter and lard mixture.\n" +
                "Repeat the process twice more, until all of the butter and lard mixture has been used up and the " +
                "dough has been " +
                "rolled out a total of four times.\n" +
                "Preheat the oven to 200C/400F/Gas 6.\n" +
                "Roll the dough out again to a 40cm x 20cm/16in x 8in rectangle, about 1cm/½in thick. Cut the dough " +
                "into 16 pieces " +
                "and roll each into a round, flat bun shape.\n" +
                "Transfer the buns to a lightly oiled baking tray and set aside for 40-45 minutes, or until they have" +
                " doubled in size " +
                "again (leave enough space between them for expansion).\n" +
                "When the buns have risen, bake them in the oven for 15-18 minutes, " +
                "or until they have risen further and are golden-brown " +
                "and cooked through. Set aside to cool on a wire rack.\n" +
                "Serve each buttery warm, spread with butter and jam.\n" +
                "\n" +
                "For the goji berry and swede pickle, in a small pan, parboil the cubes of swede. " +
                "Drain and place on a plate to chill in the fridge for 10 minutes.\n" +
                "Place the goji berries in a heatproof bowl, pour over boiling water, leave it for one minute and " +
                "then rinse and drain.\n" +
                "In a small bowl, toss together the chilled swede and goji berries. " +
                "Before serving add all the rest of the ingredients and mix well for a quick delicious pickle to " +
                "serve with the fish.\n" +
                "For the sea bass, prepare the fish by rinsing it under cold running water and pat dry with paper " +
                "towels. " +
                "Cut two slits into both sides of the fish and season with salt and white pepper.\n" +
                "Stuff the slits and inside the belly cavity with the sliced ginger. Place the fish on a heatproof " +
                "plate " +
                "(making sure that the plate and fish fit inside the bamboo steamer without covering up all of the " +
                "steam " +
                "holes around the edges of the plate). Set aside another bamboo steamer tray and plate to use for the" +
                " clams.\n" +
                "Pour half of the light Chinese lager over the fish. Place the plate of fish in a bamboo steamer, " +
                "stack and cover with the lid. Place the whole steamer basket over a wok that is three-quarters full " +
                "with boiling water, " +
                "making sure the water does not touch the base of the steamer.\n" +
                "Steam until the fish is cooked through and the flesh flakes when poked with a knife - " +
                "about 8-10 minutes (depending on the thickness of the fish. Don’t steam it too long or the skin will" +
                " separate, " +
                "not look as good and will be less tender).\n" +
                "About four minutes into steaming, put the palourde clams on a separate heatproof plate that fits " +
                "inside another bamboo steamer tray. Season with salt, ground white pepper, grated ginger, " +
                "and drizzle over the remaining light Chinese lager. Steam the clams with the fish until the flesh " +
                "of the fish flakes when poked and all the shells of the clams have opened.\n" +
                "Once the sea bass is cooked through, turn off the heat. Leave the sea bass in the steamer to stay " +
                "hot.\n" +
                "To finish the sea bass, heat a small wok, add the peanut oil and heat to high for a minute. " +
                "Take off the heat. Remove the lid of the bamboo steamer for the fish and place the spring onions " +
                "and coriander cress over the top. Pour over the low-sodium light soy sauce, followed by the toasted " +
                "sesame oil. " +
                "Pour over the hot peanut oil and it will create a sizzle as it heats up the spring onions and herbs" +
                ".\n" +
                "In the traditional Chinese way the fish is served whole in the steamer at the table because it " +
                "symbolises " +
                "unity and completeness, but you can also serve individual portions for a more modern presentation.\n" +
                "To serve, spoon a fillet of the sea bass onto a warm plate, decorate with some palourde clams, " +
                "the goji berry and swede quick ‘pickle’, blanched baby pak choi leaves, spring onion curls and the " +
                "coriander micro herbs. " +
                "Spoon the fish juices over the fish and clams and serve immediately.\n" +
                "\n" +
                "Preheat the oven to 170C/325F/Gas 3.\n" +
                "Take a very large mixing bowl, put the flour and baking powder in a sieve and sift it into the bowl," +
                " " +
                "holding the sieve high to give it a good airing as it goes down. " +
                "Now all you do is simply add all the other cake ingredients (except the icing sugar) to the bowl " +
                "and, " +
                "provided the butter is really soft, " +
                "just go in with an electric hand whisk and whisk everything together until you have a smooth, " +
                "well-combined mixture, which will take about one minute. If you do not have an electric hand whisk, " +
                "you can use a wooden spoon, using a little bit more effort. " +
                "What you will now end up with is a mixture that drops off a spoon when you give it a tap on the side" +
                " of the bowl. " +
                "If it seems a little too stiff, add a little water and mix again.\n" +
                "Now divide the mixture between the two tins, level it out and place the tins on the centre shelf of " +
                "the oven. " +
                "The cakes will take 30-35 minutes to cook, but do not open the oven door until 30 minutes have " +
                "elapsed.\n" +
                "To test whether the cakes are cooked or not, touch the centre of each lightly with a finger: " +
                "if it leaves no impression and the sponges spring back, they are ready.\n" +
                "Next, remove them from the oven, then wait about five minutes before turning them out on to a wire " +
                "cooling rack. " +
                "Carefully peel off the base papers, which is easier if you make a fold in the paper first, " +
                "then pull it gently away without trying to lift it off. " +
                "Now leave the sponges to get completely cold, then add the filling.\n" +
                "To make this, first slice the passion fruit into halves and, using a tsp, " +
                "scoop all the flesh, juice and seeds into a bowl.\n" +
                "Next, in another bowl, combine the mascarpone, fromage frais, sugar and vanilla extract, " +
                "using a balloon whisk, which is the quickest way to blend them all together. After that, " +
                "fold in about two-thirds of the passion fruit.\n" +
                "Now place the first sponge cake on the plate or cake stand you are going to serve it on, " +
                "then spread half the filling over it, drizzle the rest of the passion fruit over that, " +
                "then spread the remaining filling over the passion fruit. Lastly, place the other cake on top, " +
                "press it gently so that the filling oozes out at the edges, " +
                "then dust the surface with a little sifted icing sugar.\n" +
                "\n" +
                "Whisk the cream until floppy, then whisk in the lemon curd along with the lemon juice and zest.\n" +
                "Spread in a serving dish and freeze for at least four hours.\n" +
                "\n" +
                "Whisk the double cream in a bowl. I like to do this by hand as I have complete control over it " +
                "and I beat the cream until it just starts to feel heavy on my whisk - but don't over-whisk it.\n" +
                "Break the meringues into the cream - rough chunks are good. Scatter in the blackberries " +
                "(they work perfectly with the meringue as they cut through the sweetness). " +
                "To break up the smoothness of this sundae I add some pistachio nuts. " +
                "Gently fold together so that the fruits burst through the cream.\n" +
                "Place a large scoop of vanilla ice cream in the bottom of two sundae glasses and top with spoonfuls " +
                "of the blackberry cream and dust with icing sugar. This dessert has something soft, " +
                "something crisp, something sweet and something sharp, which makes it perfection.\n" +
                "\n" +
                "Preheat the oven to 350°. Butter the bottom and sides of each layer-cake pan with the softened " +
                "butter, " +
                "using your hands, then sprinkle the flour inside and shake this around so you get a thin coating on " +
                "the butter. " +
                "Tip out any excess.\n" +
                "Now to sift your flour. Lay a large piece of waxed paper on a board, " +
                "put a dry measuring cup in the center, hold a sifter directly over it, " +
                "scoop cake flour from the package into the sifter, and sift the flour directly into the cup. " +
                "When the cup is full, draw the back of a knife blade lightly across the top of the cup " +
                "(don’t shake the flour down, or it will become dense) and then tip the measured flour into a mixing " +
                "bowl. " +
                "Repeat with the other 2 cups of flour (you can put any flour that spilled onto the waxed paper back " +
                "in the sifter).\n" +
                "When you have 3 cups of sifted flour in the bowl, " +
                "put the baking powder and salt in the sifter, holding it over the mixing bowl, " +
                "and sift it over the flour. Then mix the baking powder and salt lightly with the flour, using your " +
                "hands.\n" +
                "Next, Put the butter into a second, large mixing bowl. If it is very firm " +
                "(it shouldn’t be, if you have left it out of the refrigerator), squeeze it through your fingers " +
                "until it softens up. " +
                "When it is soft enough to work, form your right hand into a big fork, " +
                "as it were, and cream the butter—which means that you beat it firmly and quickly with your hand, " +
                "beating and aerating it, until it becomes light, creamy, and fluffy.\n" +
                "Then whirl your fingers around like a whisk so the butter forms a circle in the bowl. " +
                "Gradually cream the 2 cups of sugar into the butter with the same fork-like motion, " +
                "beating until the mixture is very light and fluffy. As the sugar blends in it " +
                "will change the color of the butter to a much lighter color, almost white.\n" +
                "Now wash and dry your hands thoroughly and separate the eggs, as you would for a souffle, " +
                "letting the whites slip through your slightly parted fingers into " +
                "a small bowl and dropping the yolks into a second, larger bowl. " +
                "Beat the yolks for a few minutes with a whisk until they are well blended.\n" +
                "Then, again with your hand, beat them very thoroughly into the butter-sugar mixture. " +
                "Now beat in the milk alternately with the sifted flour—first one, then the other—this time " +
                "keeping your fingers close together as if your hand were a wooden spatula. Beat, beat, " +
                "beat until the batter is well mixed, then add the vanilla and beat that in for a minute or two.\n" +
                "Put the egg whites in your beating bowl and beat them with a large whisk " +
                "or an electric hand beater until they mount first to soft, drooping peaks and then to firm, glossy " +
                "peaks. " +
                "Do not overbeat until they are stiff and dry.\n" +
                "Tip the beaten whites onto the cake batter and fold them in with your hand. " +
                "Slightly cup your hand and use the side like a spatula to cut down through the whites " +
                "and batter to the bottom of the bowl and then flop them over with your cupped hand, " +
                "rotating the bowl with your other hand as you do so—exactly the technique " +
                "you use when folding egg whites into a soufflé mixture with a rubber spatula.\n" +
                "Repeat this very lightly and quickly until the whites and batter are thoroughly folded and blended. " +
                "Once you have mastered this hand technique you can use it for a soufflé, too.\n" +
                "Again using your hand like a spatula, pour and scrape the batter into the three prepared pans, " +
                "dividing it equally between them. Give the filled pans a little knock on the countertop to level the" +
                " batter.\n" +
                "Put the three pans in the center of the oven, or, if you have to use more than one rack, " +
                "stagger them on the two middle racks of the oven so they do not overlap.\n" +
                "Bake for 25 minutes, then touch the center of each cake lightly with your fingertip. " +
                "If it springs back, it is done. Remove the pans from the oven and " +
                "put them on wire cake racks to cool for a few minutes, then loosen the layers " +
                "by running the flat of a knife blade around the sides of the pans, put a rack on top of each pan, " +
                "and invert so the cake comes out onto the rack, top side down. Then reverse the layers so they are " +
                "top side up.\n" +
                "Mix the orange juice, lemon juice, sugar, and orange rind together and drizzle the mixture " +
                "over the still-warm cake layers, being careful not to let it all soak into one spot; " +
                "then pile the layers on top of each other.\n" +
                "The juice mixture will give the cake a lovely, fresh, fruity flavor and it is not rich like an icing" +
                ".\n" +
                "Leave the cake to cool.\n" +
                "\n" +
                "In a medium bowl, combine the garlic, scallions, parsley, salami, Fontina, Parmigiano, " +
                "and bread crumbs and mix well. Add 1/4 cup of the olive oil and mix well with your hands or a spoon." +
                " " +
                "Set aside.\n" +
                "Cut six 15-inch-long pieces of kitchen twine. Open out the beef, season on both sides with salt and " +
                "pepper, " +
                "and place it on a work surface so a long side is toward you. " +
                "Spread the bread crumb mixture evenly over the beef, leaving a ½-inch border along the side farthest" +
                " from you; " +
                "press and gently pack the stuffing mixture onto the beef to keep it in place " +
                "(you may have a little stuffing left over–it makes a great panini filling). " +
                "Starting from the side nearest you, roll up the meat like a jelly roll, " +
                "pressing any stuffing that falls out of the ends back into the roll, " +
                "and tie tightly with the twine, spacing the ties evenly " +
                "(it’s easier if you have a friend to tie the beef while you hold the roll together). " +
                "Wrap tightly in plastic wrap to make a compact roll, " +
                "and place in the refrigerator for at least 2 hours, or as long as overnight.\n" +
                "Preheat a gas grill or prepare a fire in a charcoal grill.\n" +
                "Carefully unwrap the beef roll and, using a very sharp knife, " +
                "cut it between the ties into six thick pinwheels. " +
                "Brush gently on both sides with the remaining 3 tablespoons olive oil and season with salt and " +
                "pepper.\n" +
                "Gently lay the pinwheels on the hottest part of the grill and cook, unmoved, " +
                "for 5 to 7 minutes. Using a spatula, " +
                "carefully turn each pinwheel over and cook for about 4 minutes longer for medium-rare. " +
                "(Don’t be alarmed if some of the cheese in the stuffing starts to melt and char on the grill, " +
                "making kind of a savory Florentine-cookie-like thing; but if you find it charring too much, " +
                "move the pinwheels to a slightly cooler part of the grill.) Transfer to a platter and serve.\n" +
                "\n" +
                "In a large, lidded saucepan, toast the cumin and coriander seeds over a medium to high heat, " +
                "swirling the pan around until the spices are a pale golden brown. It should take 2 to 3 minutes. " +
                "Then lightly grind them in a mortar and pestle.\n" +
                "Pour the oil into the pan on a medium heat and, when it's hot, put the spices back into the pan. " +
                "Stir-fry for a minute, then add the onions. Cook for 6 to 8 minutes, until they're starting to turn " +
                "golden, " +
                "then add the garlic and ginger. Cook for another couple of minutes before adding the chopped " +
                "tomatoes.\n" +
                "Let the tomatoes cook and reduce for around 15 minutes, stirring occasionally, " +
                "until they have thickened into a rich, bright sauce. " +
                "Then add the tomato paste, salt, sugar, chili powder, and turmeric, " +
                "mix well, and leave for a minute. Add the spinach, handful by handful, " +
                "mix again, and leave the spinach to wilt in the sauce.\n" +
                "To bake the eggs, have them all ready to crack and put into the pan in quick succession. " +
                "Make your first egg-sized well in the tomato sauce using the back of a wooden spoon and crack an egg" +
                " into it. " +
                "Then repeat as quickly as you can for the other eggs and put the lid on the pan. " +
                "Turn the heat down really low and cook for 10 minutes, " +
                "or until the whites of the eggs are set but the yolks still creamy.\n" +
                "Serve immediately with a sprinkling of pepper, the cilantro, a dollop of yogurt, and some hunks of " +
                "bread.\n" +
                "\n" +
                "Turn on the oven to 450 degrees. In a food processor, combine the bacon and 1 garlic clove. " +
                "Process until finely chopped. Add the eggs, bread crumbs and 1 teaspoon salt. " +
                "Pulse several times to combine thoroughly, then add the pork and mint. " +
                "Pulse the machine, a few more times until everything is well combined – " +
                "but not at all processed into a paste. Remove the blade.\n" +
                "With wet hands, form the meat into 16 plum-size spheres, " +
                "spacing them out in a 13 x 9-inch baking dish. Bake until lightly browned " +
                "(they’ll be browned more underneath than on top), about 15 minutes.\n" +
                "While the meatballs are baking, combine the tomatoes, with their juice, " +
                "chipotles, canning sauce, oregano, the remaining 2 garlic cloves (cut in half) " +
                "and ½ teaspoon salt in a blender or food processor. Process to a smooth puree.\n" +
                "When the meatballs are ready, spoon off any rendered fat from the baking dish, " +
                "then pour on the tomato mixture, covering the meatballs evenly. " +
                "Bake until the sauce looks like tomato paste, 15 to 20 minutes.\n" +
                "Microwave the broth for about a minute to heat it (or heat in a small saucepan). " +
                "Divide the meatballs among four dinner plates, leaving most of the sauce behind. " +
                "Stir enough broth into sauce to give it an easily spoonable consistency. " +
                "Taste and season with additional salt if you think the sauce needs it. " +
                "Spoon the sauce over the meatballs, decorate with extra mint leaves, if you wish, and carry to the " +
                "table.\n" +
                "Though pork is most common in Mexico, you can make these from beef " +
                "or a combination of beef and pork. Lamb can be worked into the mix too. " +
                "Turkey is what will appeal to most people looking for the leanest dish. " +
                "The bacon adds just the right tough to succulence and savor, " +
                "so I’d fight to keep it in, even though the meatballs will turn out fine without it. " +
                "Mint adds the traditional touch, but parsley, " +
                "thyme (not too much), sage and basil are all delicious in meatballs.\n" +
                "\n" +
                "Cool the génoise layer.\n" +
                "To make the buttercream, in a bowl, beat the butter by machine until soft and light, " +
                "then beat in the chestnut spread until smooth. Gradually beat in the rum and vanilla " +
                "and continue beating until the buttercream is very light and smooth, 4 or 5 minutes.\n" +
                "Turn the génoise layer over and peel away the paper. Cover with a piece of fresh paper, " +
                "turn over again so the cake is on the clean paper, long sides top and bottom, " +
                "and spread with half the buttercream. " +
                "Use the paper to roll from the long side nearest you into a tight cylinder. " +
                "Wrap the paper tightly around the roll and twist the ends together. " +
                "Refrigerate the cake while preparing the marzipan. " +
                "Reserve the remaining buttercream refrigerated for the outside of the bûlche.\n" +
                "To make the marzipan, combine the almond paste, confectioners’ sugar, and 2 tablespoons " +
                "of the corn syrup in a food processor and pulse only 5 or 6 times, until the mixture " +
                "is coarse and crumbly in appearance. Do not ouerprocess. Remove from the work bowl, " +
                "and knead smooth, adding up to 1 tablespoon more corn syrup if the mixture seems dry. " +
                "Wrap in plastic until needed.\n" +
                "To make marzipan mushrooms, roll the marzipan into a cylinder and slice into l-inch lengths. " +
                "Roll half the lengths into balls. Press the cylinders against the balls to attach them, " +
                "flatten one side of the balls slightly, and form mushrooms. Smudge with cocoa powder.\n" +
                "Remove the rolled cake from the refrigerator and unwrap. Trim the edges diagonally, " +
                "cutting one of the edges about 2 inches away from the end. " +
                "Position the roll on a platter and place the uncut end " +
                "of the 2-inch piece about two thirds along the top side of the roll. " +
                "Cover the buche with the remaining buttercream, making sure to curve up the protruding branch on the" +
                " top, " +
                "leaving the three cut ends unfrosted. Streak the buttercream with a fork or decorating comb. " +
                "Decorate with the mushrooms. Dust the platter and buche sparingly with confectioners’ sugar “snow" +
                ".”\n" +
                "<p><b>Serving</b>: Cut diagonal slices to serve.</p>\n" +
                "<p><b>Storage</b>: Refrigerate the cake until immediately before serving.</p>\n" +
                "\n" +
                "In the bowl of a stand mixer, or a large mixing bowl if preparing the dough by hand, " +
                "sprinkle the yeast over the warm milk. Stir in the 1½ teaspoons " +
                "of sugar and let the yeast proof for 5 to 7 minutes, until foamy and bubbling. " +
                "Add 1 cup flour and the salt, beating with the paddle attachment or a wooden spoon to make a soft, " +
                "smooth batter, about 5 minutes.\n" +
                "Beat in the remaining 4½ cups flour and the 6 tablespoons sugar in three additions, " +
                "beating in 2 eggs after each addition of flour. Mix well between additions, " +
                "making sure you scrape down the sides and bottom of the bowl. Beat until a smooth, " +
                "soft and elastic dough is formed, about 10 minutes with the dough hook attachment, " +
                "or 20 to 25 minutes by hand. I actually like to turn the dough out of the mixer at this point and " +
                "hand-knead. " +
                "The technique is simple (and great for relieving stress—cheaper than a therapist!). " +
                "Slap the dough onto the counter, then with the heel of one hand push it through the middle into the " +
                "counter. " +
                "With the other hand, fold the far side of the blob towards you, over onto itself, " +
                "and rotate the dough about 90 degrees. Repeat this pushing, folding and rotating cycle several " +
                "times, " +
                "then give the dough another good hard slap onto the counter. (Don’t add any extra flour at this " +
                "point, " +
                "even if the dough feels sticky; just use a dough scraper to gather the dough off the work surface. " +
                "As the dough becomes more elastic, it will lose its gumminess and become smooth.) Do a stretch test:" +
                " " +
                "pinch some of the dough and pull it upwards. When the dough is ready, it should feel springy and " +
                "elastic.\n" +
                "If you have removed your dough from the stand mixer, return it to a clean mixer bowl. " +
                "If you are preparing the dough entirely by hand, you may find the next step easier " +
                "in a large mixing bowl with a wooden spoon. Knead or beat the butter into the dough in small " +
                "portions, " +
                "fully incorporating each addition before adding the next. This process is definitely easier with a " +
                "dough hook, " +
                "but it is not impossible by hand. Squeeze the first few additions of butter through the dough, " +
                "kneading until it is absorbed, then use a wooden spoon to beat in the remaining additions " +
                "to prevent the dough from getting greasy and slippery, " +
                "a sign that the butter is melting and not getting properly incorporated. " +
                "The butter should be malleable, but not overly soft, and the dough should not get too warm during " +
                "this process.\n" +
                "When the last of the butter has been beaten in, continue beating the dough until it is very smooth, " +
                "glossy and elastic, about 5 minutes with the dough hook, or 10 minutes by hand." +
                "The dough should now have reached the “clean-up stage”—it should come away " +
                "from the sides of the bowl in a smooth entity, or should neatly roll on the counter, no longer " +
                "sticky or tacky. " +
                "Transfer the dough to a large, lightly buttered bowl and cover the bowl with a sheet of plastic wrap" +
                ". " +
                "Leave to rise at warm room temperature, away from any draughts (about 75 degrees is perfect), " +
                "until doubled in bulk, about 1¼ to 2 hours.\n" +
                "Punch the dough down and flip it over, deflating it completely. " +
                "The buttery side should now be facing up. Cover again with plastic wrap and refrigerate for 2 to 3 " +
                "hours. " +
                "Punch the dough down, flip it once more, cover the bowl with plastic and weigh it down with a plate " +
                "or dish, " +
                "making sure the dough won’t be able to creep around the sides of the weight and escape the confines " +
                "of its bowl. " +
                "(I have seen renegade doughs running rampant over the bottles and jars on refrigerator shelves more " +
                "than once, " +
                "and it’s not a mess that you will forget having to clean up! Not to mention the waste of time, " +
                "energy " +
                "and perfectly good butter.) Return the bowl to the coldest part of your refrigerator and leave there" +
                " overnight. " +
                "If the dough is left longer than 8 hours, check periodically to make sure it has not risen above the" +
                " bowl; " +
                "gently punch it down as necessary. The brioche dough may be frozen at this point, wrapped very " +
                "securely. " +
                "Allow the dough to thaw in the refrigerator for 4 to 7 hours, then proceed with the shaping, final " +
                "rising and baking.\n" +
                "Several hours before you plan to bake the brioche, remove the dough from the refrigerator, " +
                "punch it down and turn it out onto a very lightly floured surface. " +
                "Invert the bowl over the dough and let the dough rest for 10 minutes. " +
                "(At this point, the dough can be shaped for other pastries as well.) " +
                "Meanwhile, butter two 9 × 5-inch metal loaf pans, " +
                "or two 8-inch fluted brioche moulds (or one of each!) and set aside.\n" +
                "To form two 9 × 5-inch loaves: divide the dough into 6 roughly equal " +
                "parts and cover 5 of these with plastic wrap to prevent them from drying out. " +
                "Roll the remaining portion on the counter into a smooth ball, plumping it by caressing " +
                "the sides of it with your hands from top to bottom, pulling the surface taut and smooth " +
                "and tucking the excess dough underneath the ball. Set the ball in the end of one of " +
                "the prepared loaf pans and cover it with plastic while you repeat the process " +
                "with the remaining pieces of dough. Place 3 balls in each pan, In a row.\n" +
                "To form two 8-inch brioche a tetes: divide the dough into 2 equal portions. " +
                "Cover 1 portion with plastic wrap while you work with the other. " +
                "Divide this half into 8 relatively equal portions, then cover 7 of them with plastic. " +
                "Roll the remaining portion on the counter into a smooth ball as for the 9 × 5-inch loaves above. " +
                "Place this ball in the bottom of one of the prepared fluted moulds. " +
                "Repeat the rolling process with 6 of the remaining pieces of dough, " +
                "setting them around the outside of the mould over the first ball. " +
                "Roll the last piece into a smooth ball and nestle this into the centre of the circle of balls. " +
                "Cover the pan loosely with plastic wrap and repeat with the second half of the dough.\n" +
                "Cover the pans loosely with plastic and drape a dishtowel over top. " +
                "Let the shaped loaves rise in a draught-free place until plumped and risen, " +
                "but not quite doubled. This last rising can take between 1½ to 2½ hours, " +
                "as the dough is still chilled from the refrigerator. Keep an eye on the dough " +
                "and make sure that it does not rise too much, or you will not get " +
                "as good an oven-spring (the big initial rising in the oven).\n" +
                ". During the last 20 minutes of the final rising, preheat the oven to between 375° and 400°. " +
                "Brush the plump loaves with the egg-milk glaze and bake them for 35 to 40 minutes, " +
                "or until they sound hollow when tipped out of their pans and tapped on the bottom. " +
                "Be careful when handling the hot loaves, as the joints between the sections " +
                "are still very soft and fragile. As the brioche cools, the loaves will become firm and less delicate" +
                ".\n" +
                ". Turn the loaves out and cool completely on wire racks before serving " +
                "or wrapping tightly in plastic wrap and aluminum foil and storing or freezing. " +
                "The baked brioche can be frozen for up to 2 months, well-wrapped. " +
                "Thaw at room temperature for about 4 hours.\n" +
                "\n" +
                "Slice the thin side of the conch meat from the fatter trunk and cut it into ¼-inch dice. " +
                "Slice the trunk into strips, cut the strips in half, and cut into ¼-inch dice. " +
                "Place the conch (there should be about 3 cups) in a large glass bowl.\n" +
                "Add the citrus juices, onion, tomato, bell pepper, chiles, and salt and toss to combine. " +
                "Cover with plastic wrap placed flush against the surface and chill for 1 hour before serving.\n" +
                "If you go under the bridge that takes you from Nassau to Paradise Island, on the Nassau side, " +
                "you will encounter many vendors selling fruits, vegetables, and seafood. " +
                "Some are set up in little shacks where they cook local foods and sell beer. " +
                "Each little restaurant can seat no more than ten or twelve people, making eating " +
                "there a fun and intimate experience. I have tried a few of the places, " +
                "but my favorite is #8, also known as The Burning Spot. You can get a beautiful conch salad there, " +
                "but nine out of ten patrons (almost all local) sit down and order a beer and “scorch”: " +
                "similar to conch salad but simpler—no tomatoes or peppers, just conch, onion, juices, salt, " +
                "and hot chiles—lots of hot chiles. This dish definitely falls into the “hotter than hell” category" +
                ".\n" +
                "To make Scorch, omit the tomatoes and bell peppers " +
                "from the salad and double the amount of habanero and bird chiles.\n" +
                "Working Ahead: You can prepare the ingredients for this salad a couple " +
                "of hours before you plan to eat it, but don’t combine them until 1 hour before serving. " +
                "This salad is best eaten very fresh.\n" +
                "How to crack conch: To crack conch, you will need a small hammer and a small sharp knife. " +
                "Find the spot where the abductor mussel is fastened to the shell almost directly behind the opening," +
                " " +
                "and tap with the hammer to crack a hole there. Insert the knife and cut the conch away from the " +
                "shell. " +
                "It will slide right out. Cut away the soft viscera and discard. Wash the meaty part " +
                "of the conch in saltwater or salted water. Drain well.\n" +
                "\n" +
                "Preheat a barbecue grill to high heat.\n" +
                "While the grill is heating, bring a large pot of salted water to a boil. " +
                "Add the ears of corn and cook for 2 minutes. Remove the corn and dry with a towel. Set aside.\n" +
                "Stir the butter and the chile powder together in a small bowl, and set it aside.\n" +
                "Oil the grill grate well, and spread the olive oil over the ears of corn. " +
                "Arrange the corn on the grate and grill, turning the ears, " +
                "until they have nice grill marks, about 3 minutes.\n" +
                "Transfer the ears of corn to a plate, and brush the chipotle butter over them. " +
                "Sprinkle on the Parmesan cheese, and serve immediately.\n" +
                "\n" +
                "Early in the day, simmer balsamic vinegar in a small non-reactive saucepan " +
                "with sugar and pepper until reduced by about half and slightly thickened for a glaze.\n" +
                "Preheat grill to medium-high or 350 to 400 F. if not already hot.\n" +
                "Place peach halves on grill, cut side down, " +
                "and grill about 5 minutes or until flesh has slightly charred.\n" +
                "Brush top sides with glaze and cook 1 to 2 minutes.\n" +
                "Turn, brush with glaze and cook another 2 to 3 minutes.\n" +
                "Transfer to individual serving dishes and spoon any remaining glaze of tops.\n" +
                "Serve with crumbled cheese on top.\n" +
                "\n" +
                "Heat the oven to 325°F.\n" +
                "Put the cilantro, parsley, chilies, ginger, garlic, onion, ground cumin, and salt in a blender. " +
                "Squeeze in the lime juice and blend the mixture to a smooth paste. " +
                "Stir in the jaggery or dark brown sugar.\n" +
                "Rub this mixture all over the chicken, taking care to enter " +
                "all the cracks and crevices and well under the skin. Lightly baste with the ghee.\n" +
                "Place the chicken in a deep baking dish and roast for about 3 to 4 hours, " +
                "basting halfway through the roasting time.\n" +
                "Remove from oven and let rest about 15 minutes. Slice into pieces and serve.\n" +
                "\n" +
                "Toast mustard and coriander seeds in a dry medium saucepan over medium heat, " +
                "tossing often, until mustard seeds begin to pop, about 3 minutes. " +
                "Add vinegar, salt, and sugar and bring to a boil. Reduce heat and simmer, " +
                "stirring often, until salt and sugar are dissolved, about 4 minutes. " +
                "Remove from heat; stir in dill and 4 cups water. Let cool.\n" +
                "Place chicken and brine in a large resealable plastic bag; chill 3 hours. " +
                "Remove chicken from brine, scraping off seeds, cover, and chill until ready to fry.\n" +
                "Fit a large pot with thermometer and pour in oil to measure 2\". " +
                "Heat over medium-high heat until thermometer registers 350°F.\n" +
                "Meanwhile, place buttermilk in a large bowl. Place flour in another " +
                "large bowl; season with kosher salt. Working in batches, coat chicken in buttermilk, " +
                "then dredge in flour, dipping your fingers in buttermilk as you pack flour on to help create " +
                "moistened, " +
                "shaggy bits (the makings of a super-crisp crust); transfer to a baking sheet.\n" +
                "Working in batches and returning oil to 350\u0081° between batches, " +
                "fry chicken, turning occasionally, until skin is deep golden brown and crisp " +
                "and chicken is cooked through, 6–8 minutes. Transfer to a wire rack set inside a baking sheet.\n" +
                "Drizzle chicken with honey; sprinkle with sea salt and benne seeds. " +
                "Serve with hot sauce alongside.\n" +
                "\n" +
                "Remove the stems and roots from the spinach. Rinse in many " +
                "changes of cold water until any trace of sand is removed. Place in a large pot " +
                "with a pinch of salt and no water other than that retained from washing. " +
                "Cook over moderately high heat, covered, for 5 minutes. Drain.\n" +
                "Soak the raisins in the lukewarm water for a couple of minutes, then drain.\n" +
                "Meanwhile, heat the oil in a large skillet, add the onion and sauté until " +
                "the onion is soft and translucent; add the raisins, pignoli, " +
                "and small amounts of salt and pepper. Sauté, stirring, 1 minute. " +
                "Add the spinach and nutmeg and sauté, stirring frequently, until the spinach looks dry and crisp.\n" +
                "\n" +
                "I like this cake best baked in a 9-inch angel food cake pan, " +
                "but you can also make it in a 10-inch tube or bundt cake pan, a 9 by 13-inch sheetpan, " +
                "or three 8 by 4 1/2-inch loaf pans.\n" +
                "Preheat the oven to 350°F. Lightly grease the pan(s). For tube and angel food pans, " +
                "line the bottom with lightly greased parchment paper. " +
                "For gift honey cakes, I use \"cake collars\" (available from Sweet Celebrations) " +
                "designed to fit a specific loaf pan. These give the cakes an appealing, professional look.\n" +
                "In a large bowl, whisk together the flour, baking powder, baking soda, salt, and spices. " +
                "Make a well in the center and add the oil, honey, sugars, eggs, vanilla, " +
                "coffee, orange juice, and rye or whisky.\n" +
                "Using a strong wire whisk or an electric mixer on slow speed, " +
                "combine the ingredients well to make a thick batter, " +
                "making sure that no ingredients are stuck to the bottom of the bowl.\n" +
                "Spoon the batter into the prepared pan(s) and sprinkle the top of the cake(s) " +
                "evenly with the almonds. Place the cake pan(s) on 2 baking sheets stacked together " +
                "and bake until the cake springs back when you touch it gently in the center. " +
                "For angel and tube cake pans, bake for 60 to 70 minutes; loaf cakes, 45 to 55 minutes. " +
                "For sheet-style cakes, the baking time is 40 to 45 minutes. This is a liquidy batter and, " +
                "depending on your oven, it may need extra time. Cake should spring back when gently pressed.\n" +
                "Let the cake stand for 15 minutes before removing it from the pan. " +
                "Then invert it onto a wire rack to cool completely.\n" +
                "\n" +
                "A short time before serving, mash avocado and add lemon juice. " +
                "Season with pepper and only a bit of salt, as there will be enough in the lox. " +
                "Split bagels and spread each half with avocado. Top with lox. " +
                "Put onion, tomato, and capers (if using) on bottom half, " +
                "then set top half of sandwich in place. Serve at once.\n" +
                "\n" +
                "1. Place the tomatoes, garlic, salt, paprika, tomato paste, " +
                "and vegetable oil in a small saucepan. Bring to a simmer and cook, " +
                "uncovered, over low heat until thick, for about 30 minutes, stirring occasionally.\n" +
                "2. Ladle the tomato sauce into a greased 12-inch frying pan. " +
                "Bring to a simmer and break the eggs over the tomatoes. " +
                "Gently break the yolks with a fork. Cover and continue to cook for about 3 to 4 minutes, " +
                "until the eggs are set. Bring the frying pan directly to the table. " +
                "Set it on a trivet and spoon out the shakshuka.\n" +
                "\n" +
                "1. Combine the rice, cream, and butter. Add the eggs, stirring together until well blended.\n" +
                "2. Sift the flour with the cinnamon, nutmeg, and salt, " +
                "and blend thoroughly into the rice mixture. " +
                "Cover the batter and refrigerate for at least 2 hours, or up to 8 hours.\n" +
                "3. Preheat the oven to 200°F.\n" +
                "4. When you are ready to cook the pancakes, " +
                "remove the batter from the refrigerator and whisk together well. " +
                "Melt about 1 tablespoon of butter in a skillet set over medium-high heat. " +
                "When the butter is sizzling, add a small amount of batter to the pan to test the heat level. " +
                "If necessary, reduce the heat to medium before cooking the pancakes.\n" +
                "5. For each rice pancake, pour about 1/4 cup of the batter into the prepared pan. " +
                "Cook for 2 to 3 minutes, " +
                "or until bubbles appear on the surfaces and the edges of the pancakes are lightly browned. " +
                "Using a spatula, carefully turn the pancakes over and cook about 2 minutes more, until done. " +
                "Transfer the finished pancakes, separated by parchment paper, to an ovenproof platter, " +
                "and set them in the oven to keep warm. Prepare the remaining pancakes, " +
                "adding more butter to the pan as needed.\n" +
                "6. To serve, lightly sprinkle the rice pancakes with sugar, " +
                "if desired, and garnish with orange slices.\n" +
                "\n" +
                "1. Preheat the oven to 350°F. Lightly grease the bottom of each cake pan, " +
                "then line it with waxed paper or parchment paper and lightly grease and flour the bottom and sides. " +
                "Divide the batter between the pans and bake the cakes " +
                "until a toothpick inserted in the center comes out clean, " +
                "30 minutes. Cool the cakes completely in the pans on cooling racks and then turn them out onto the " +
                "racks.\n" +
                "2. While the cakes are cooling, transfer 1 cup of the frosting to a glass measuring cup. " +
                "Tint it green (see Decorating Tips). Tint the remaining frosting orange.\n" +
                "3. Using an offset spatula, spread a thin layer of the orange frosting over the flat surface of 1 " +
                "cake. " +
                "Invert the second cake on top of the first, joining the flat surfaces of both domes together. " +
                "Transfer the assembled cake to the cardboard base, if using, or a cake plate. " +
                "Cover the cake completely with the rest of the orange frosting.\n" +
                "4. To make rib indentations as shown in the photo, hold a plastic straw against the cake, " +
                "curving it from bottom to top. Lift it off, reposition it, and press again. Repeat all around the " +
                "cake. " +
                "(Gently incise the ribs with a skewer if you don't have a straw.)\n" +
                "5. For the stem, invert the ice cream cone and, using a clean spatula, " +
                "cover it with the green frosting; add more frosting at the top to give the stem a curved tip. " +
                "Place the stem on the top of the cake.\n" +
                "6. Fit a decorating bag with a coupler and the leaf tip; " +
                "add 1 cup of the green frosting to the bag. Referring to the photo, " +
                "pipe several leaves around the base of the stem. Hold the bag at an angle next to the stem. " +
                "Squeeze out some frosting, allowing it to fan into a wide base, " +
                "then decrease the pressure and slowly pull the tip away, lifting slightly, to form a point.\n" +
                "7. Remove the leaf tip and replace with the round tip. Pipe tendrils around the stem and leaves. " +
                "Place the tip where you want the tendril to begin. " +
                "Using even pressure, squeeze out some frosting and move the tip to draw the tendril. " +
                "Release the pressure and lift the tip when the tendril is the desired length.\n" +
                "8. Using a rolling pin, roll the fondant to 1/8 inch thick. " +
                "Referring to the photo and using a small, sharp knife, " +
                "cut out the features. For the eyes, cut 2 triangles about " +
                "1 1/2 inches wide at the base and 1 1/2 inches tall at the center. " +
                "For the nose, cut another triangle a little smaller than the eyes. " +
                "Cut the smile to be about 4 inches from tip to tip. You can draw a paper pattern first if you like" +
                ".\n" +
                "9. Spread the black sugar on a small plate (large enough to hold the smile) " +
                "using the back of the spoon. Press each cutout into the sugar " +
                "and then invert the coated cutout onto the pumpkin to form the face, " +
                "pressing it into the frosting. Transfer the cake to the serving dish if you have not already done so" +
                ".\n" +
                "\n" +
                "1. Combine the sugar, water, egg whites, and cream of tartar in the top of a double boiler. " +
                "Using an electric mixer on low speed, beat the ingredients for 30 seconds to combine.\n" +
                "2. Boil a small amount of water in the bottom of the double boiler. " +
                "Place the top of the double boiler, with the frosting ingredients, on top. " +
                "Cook the frosting on medium heat, beating constantly with the mixer on high speed, " +
                "for about 7 minutes, or until the frosting forms stiff peaks when the beaters are lifted. " +
                "Remove the top of the double boiler from the bottom and, using a spoon or rubber spatula, " +
                "stir in the vanilla extract. Beat the frosting for 2 to 3 minutes more, or until it is spreadable. " +
                "Allow the frosting to cool; it should be slightly warm or at room temperature when you use it. " +
                "Discard any leftovers. Store frosted cake in a cool, dry place until ready to serve.\n" +
                "\n" +
                "1. With a mixer on medium speed, beat together the shortening, vanilla extract, " +
                "and lemon extract in a medium bowl for 30 seconds.\n" +
                "2. Slowly add half the confectioners' sugar, beating well. Beat in 2 tablespoons of the milk. " +
                "Gradually beat in the remaining powdered sugar and enough of the remaining milk " +
                "until the icing reaches a spreadable consistency. " +
                "Store in an airtight container in the refrigerator for 2 or 3 days\n" +
                "\n" +
                "1. Place the cake on the cake plate. Reserve 1 cup of frosting to make additional ghost figures. " +
                "Using an offset spatula, fill the hole in the center of the cake with " +
                "frosting and then spread frosting over the top and sides in a wavy pattern.\n" +
                "2. Add the reserved frosting to the decorating bag; snip off the tip " +
                "of the bag to create a 1/8- to 1/4-inch opening.\n" +
                "3. Referring to the photo, pipe as many ghosts as you like onto " +
                "and around the cake and into the eggcups. For each, apply pressure " +
                "to squeeze out some frosting for a base, then lift the bag slowly, " +
                "decreasing the pressure until the ghost is the desired size; " +
                "stop the pressure and lift off the bag. Add 2 candy eyes to each ghost.\n" +
                "\n");
    }

    @Test
    public void DetectIngredientsInStepTask_doTask_AccuracyForQuantityThreshold() {
        /*
        Check that the quantity is correct for x % of the time
         */
        // Arrange
        int correctQuantities = 0;
        for (int r = 0; r < rips.size(); r++) {
            List<List<Ingredient>> correctRecipe = correctIngredientsPerRecipe.get(r);
            System.out.println("-----------------------------------------------");
            System.out.println("RECIPENUMBER " + r + "\n");


            for (int s = 0; s < rips.get(r).getStepsInProgress().size(); s++) {
                System.out.println(rips.get(r).getStepsInProgress().get(s));

                System.out.println("-----");
                List<Ingredient> correctStepIngredients = correctRecipe.get(s);
                Collection<Ingredient> detectedStepIngredients =
                        rips.get(r).getStepsInProgress().get(s).getIngredients();


                // Compare detected ingredients with correct ingredients
                correctQuantities += equalQuantities(correctStepIngredients, detectedStepIngredients);

            }
        }

        // Assert
        double accuracy = (double) correctQuantities / totalIngredients;
        double threshold = 0.85;
        System.out.println(accuracy);
        assertTrue("Accuracy: " + accuracy + ", threshold" + threshold, accuracy > threshold);

    }

    /*
    helper function that checks how many ingredients in the recipe are correctly detect in terms of their quantity
     */
    private static int equalQuantities(List<Ingredient> correctIngredients,
                                       Collection<Ingredient> detectedIngredients) {

        int equalQuantities = 0;


        for (Ingredient detected : detectedIngredients) {
            for (Ingredient correct : correctIngredients) {
                if (detected.getName().equals(correct.getName())) {
                    if ((int) (detected.getQuantity() * 1000) == (int) (correct.getQuantity() * 1000)) {
                        equalQuantities++;
                    }
                    else{
                        System.err.println("CORRECT: " +correct.getQuantity()+ ", DETECTED: "+ detected.getQuantity());
                    }
                }

            }
        }

        return equalQuantities;
    }

    @Test
    public void DetectIngredientsInStepTask_doTask_AccuracyForUnitThreshold() {
        // Arrange
        int correctUnits = 0;
        for (int r = 0; r < rips.size(); r++) {
            List<List<Ingredient>> correctRecipe = correctIngredientsPerRecipe.get(r);

            for (int s = 0; s < rips.get(r).getStepsInProgress().size(); s++) {

                List<Ingredient> correctStepIngredients = correctRecipe.get(s);

                Collection<Ingredient> detectedStepIngredients =
                        rips.get(r).getStepsInProgress().get(s).getIngredients();

                System.out.println("Recipe: " + r + ", step: " + s);
                System.out.println(detectedStepIngredients);
                System.out.println(correctStepIngredients);
                System.out.println(rips.get(r).getStepsInProgress().get(s));
                // Compare detected ingredients with correct ingredients
                correctUnits += equalUnits(correctStepIngredients, detectedStepIngredients);
                System.out.println("Not detected ingredients ");
                for(Ingredient i: correctStepIngredients){
                    if(!detectedStepIngredients.contains(i)){
                        System.out.println("NOT DETECTED " + i);
                    }
                }
                System.out.println("------------------------------------");
            }
        }

        // Assert
        double accuracy = (double) correctUnits / totalIngredients;
        System.out.println(accuracy);
        System.out.println("CORRECT "+ correctUnits +" out of " + totalIngredients + " ingredients");
        assertTrue(accuracy > 0.85);

    }

    /*
    Helper function that checks if the detected and expected unit match
     */
    private static int equalUnits(List<Ingredient> correctIngredients, Collection<Ingredient> detectedIngredients) {
        int equalUnits = 0;

        for (Ingredient detected : detectedIngredients) {
            for (Ingredient correct : correctIngredients) {
                if (detected.getName().equals(correct.getName())) {
                    if (detected.getUnit().equals(correct.getUnit())) {
                        equalUnits++;
                    }
                    else{
                        System.out.println("CORRECT: " +correct.getUnit()+ ", DETECTED: "+ detected.getUnit());
                    }
                }

            }
        }
        return equalUnits;

    }

}
