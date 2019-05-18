package com.aurora.souschefprocessor.retrainmodel;



import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

public class TrainModelForIngredientDetection {

    /**
     * This function will train the model on the data in ingredNEW.tsv and make a new model called
     * detect_ingr_list_model.gz. It also provides a function to test the newly created model where you can add
     * examples to see how your model performs on some examples, you can provide your own examples. Adding new data
     * can be done in ingredNEW.tsv but be careful not to remove any existing data, unless you think the annotated
     * data is wrong.
     *
     * If you want to use this new model just copy the model to the res/raw folder of souschefprocessor, this will
     * overwrite the existing model.
     *
     * Using the function is simple, just run this test
     * (maybe this should be a main but this was faster)
     * @throws Exception if the files are not found
     */
   @Ignore
   @Test
    public void retrainModel() throws Exception{


        // path to this directory
        String startPath = "src/test/java/com/aurora/souschefprocessor/retrainmodel/";
        // makes the classifier based on the annotated training file
        String[] argsArray = {"-trainFile", startPath+ "/ingredNEW.tsv", "-serializeTo", startPath + "/detect_ingr_list_model.gz",
                "-prop", startPath + "/train.prop"};


        // train the classifier
        CRFClassifier.main(argsArray);



        // The lines below will show the classification on some examples using the new model, if you are retraining
        // the model and want to know how it performs on some of your examples add them below or change the existing
        // ones
        CRFClassifier<CoreLabel> crf = CRFClassifier.getClassifier(startPath + "/detect_ingr_list_model.gz");


        List<List<CoreLabel>> list = crf.classify("5 dl milk");
        List<List<CoreLabel>> list2 = crf.classify("5 milliliter water");
        List<List<CoreLabel>> list3 = crf.classify("Olive oil (optional)");

        for (List<CoreLabel> l : list) {
            System.out.println(l);
            for (CoreLabel cl : l) {
                System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class));
            }
        }
        for (List<CoreLabel> l : list2) {
            System.out.println(l);
            for (CoreLabel cl : l) {
                System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class));
            }
        }
        for (List<CoreLabel> l : list3) {
            System.out.println(l);
            for (CoreLabel cl : l) {
                System.out.println(cl.get(CoreAnnotations.AnswerAnnotation.class));
            }
        }
    }
}
