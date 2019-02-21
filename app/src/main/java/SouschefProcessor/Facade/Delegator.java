package SouschefProcessor.Facade;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import SouschefProcessor.Recipe.Recipe;
import SouschefProcessor.Recipe.RecipeInProgress;
import SouschefProcessor.Task.IngredientDetector.IngredientDetectorList;
import SouschefProcessor.Task.IngredientDetector.IngredientDetectorStep;
import SouschefProcessor.Task.SectionDivider.SectionDivider;
import SouschefProcessor.Task.SectionDivider.StepSplitter;
import SouschefProcessor.Task.Task;
import SouschefProcessor.Task.TimerDetector.TimerDetector;

/**
 * Implements the processing by applying the filters. This implements the order of the pipeline as
 * described in the architecture.
 */
public class Delegator {

    private static ThreadPoolExecutor threadPool;
    private SectionDivider sd;
    private StepSplitter ss;
    private IngredientDetectorStep ids;
    private IngredientDetectorList idl;
    private TimerDetector td;

    /**
     * Creates the ThreadPoolExecutor for the processing of the text, this is device-dependent
     */
    private void setUpThreadPool(){
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         */
        int NUMBER_OF_CORES =
                Runtime.getRuntime().availableProcessors();
        // A queue of Runnables
        final BlockingQueue<Runnable> decodeWorkQueue;
        // Instantiates the queue of Runnables as a LinkedBlockingQueue
        decodeWorkQueue = new LinkedBlockingQueue<Runnable>();
        // Sets the amount of time an idle thread waits before terminating
        final int KEEP_ALIVE_TIME = 1;
        // Sets the Time Unit to seconds
        final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        // Creates a thread pool manager
        threadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                decodeWorkQueue);
    }

    /**
     * This is the core function of the delegator, where the text is processed by applying the filters
     * This function should be able to at run time decide to do certain filters or not (graceful degradation)
     * @param text The text to be processed in to a recipe Object
     * @return A recipe object that was constructed from the text
     */
    public Recipe processText(String text){
        //TODO implement this function so that at runtime it is decided which tasks should be performed
        if(threadPool == null){
            setUpThreadPool();
        }
        setUpTasks();

        RecipeInProgress rip = new RecipeInProgress(text);
        doTask(rip, sd, threadPool);

        TaskThread idlThread = doTaskInThread(rip, idl, threadPool);
        TaskThread ssThread = doTaskInThread(rip, ss, threadPool);

        try {
            ssThread.join(); //later steps depend on splitting in steps
        }
        catch(InterruptedException ie){
            //TODO, maybe let a higher stage handle this
        }

        TaskThread idsThread = doTaskInThread(rip, ids, threadPool);

        try {
            idlThread.join(); //later steps depend on ingredientlist
        }
        catch(InterruptedException ie){
            //TODO
        }
        TaskThread tdThread = doTaskInThread(rip, td, threadPool);

        try{
            //wait untill all threads have finished
            idsThread.join();
            tdThread.join();
        }catch(InterruptedException ie){
            //TODO
        }

        return rip.convertToRecipe();
    }

    /**
     * The function creates all the tasks that could be used for the processing. If new tasks are added to the c
     * codebase they should be created here as well.
     */
    public void setUpTasks(){
        sd = new SectionDivider();
        ss = new StepSplitter();
        idl = new IngredientDetectorList();
        ids = new IngredientDetectorStep();
        td = new TimerDetector();
    }

    /**
     * This performs a task on the recipe.
     * @param rip The recipe on which to do the task
     * @param task The task to be pefformed
     */
    public void doTask(RecipeInProgress rip, Task task, ThreadPoolExecutor threadPool){
        task.doTask(rip, threadPool);
    }

    /**
     * This performs a task on a recipe in a seperate thread.
     * @param rip The recipe on which to do the task
     * @param task The task to be performed
     * @return The thread in which the task is being performed.
     */
    public TaskThread doTaskInThread(RecipeInProgress rip, Task task, ThreadPoolExecutor threadPool){
        TaskThread t = new TaskThread(rip, task, threadPool);
        threadPool.execute(t);
        return t;
    }

    public ThreadPoolExecutor getThreadPool(){
        if(threadPool == null){
            setUpThreadPool();
        }
        return threadPool;
    }



}

