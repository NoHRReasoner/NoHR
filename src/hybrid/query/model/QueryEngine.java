package hybrid.query.model;

import com.declarativa.interprolog.XSBSubprocessEngine;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * The Class QueryEngine.
 */
public class QueryEngine {

    /** The xsb engine. */
    private XSBSubprocessEngine engine;

    /** The is engine started. */
    private boolean isEngineStarted = false;

    /** The Constant log. */
    private static final Logger LOG = Logger.getLogger(Query.class);

    /**
     * Instantiates a new query engine.
     *
     * @throws Exception the exception
     */
    public QueryEngine() throws Exception {

        /**
         * Env variable which should be responsible for directory where XSB was
         * installed
         */
        String xsbBin = System.getenv("XSB_BIN_DIRECTORY");
        printLog("Starting query engine" + Config.NL);
        printLog(Config.TEMP_DIR + Config.NL);

        if (xsbBin != null) {
            xsbBin += "/xsb";
        } else {
            throw new Exception("Please, set up your XSB_BIN_DIRECTORY");
        }
        startEngine(xsbBin);
    }

    /**
     * Deterministic goal.
     *
     * @param detGoal the det goal
     * @return the object[]
     */
    public Object[] deterministicGoal(String detGoal) {
        try {
            return engine.deterministicGoal(detGoal, "[TM]");
        } catch (Exception e) {
            LOG.error(e);
            return null;
        }
    }

    /**
     * Deterministic goal bool.
     *
     * @param command the command
     * @return true, if successful
     */
    public boolean deterministicGoalBool(String command) {
        return engine.deterministicGoal(command);
    }

    /**
     * Checks if is engine started.
     *
     * @return true, if is engine started
     */
    public boolean isEngineStarted() {
        return isEngineStarted;
    }

    /**
     * Load.
     *
     * @param file the file
     * @return true, if successful
     */
    public boolean load(File file) {
        return engine.load_dynAbsolute(file);
    }

    /**
     * Prints the log.
     *
     * @param message the message
     */
    private void printLog(String message) {

    }

    /**
     * Shutdown.
     */
    public void shutdown() {
        engine.shutdown();
    }

    /**
     * Start engine.
     *
     * @param xsbBin the xsb bin
     * @throws Exception the exception
     */
    private void startEngine(String xsbBin) throws Exception {
        if (engine != null) {

            engine.shutdown();
            engine = null;
        }
        isEngineStarted = true;
        try {
            engine = new XSBSubprocessEngine(xsbBin);
            // _engine.addPrologOutputListener(this);
            printLog("Engine started" + Config.NL);
        } catch (Exception e) {
            isEngineStarted = false;
            throw new Exception("Query Engine was not started" + Config.NL + e.toString() + Config.NL);
        }
    }
}
