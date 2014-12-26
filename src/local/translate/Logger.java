package local.translate;

import union.logger.UnionLogger;

import java.util.Date;

/**
 * The Class Logger.
 */
public class Logger {

    /** The Constant log. */
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Logger.class);

    /**
     * Debug.
     *
     * @param message the message
     */
    public static void debug(Object message) {
        log.info(message);
    }

    /**
     * Gets the diff time.
     *
     * @param startDate the start date
     * @param message   the message
     * @return the diff time
     */
    public static void getDiffTime(Date startDate, String message) {
        Date stoped = new Date();
        long diff = stoped.getTime() - startDate.getTime();
        log(message + " " + diff + " milisec");
    }

    /**
     * Log.
     *
     * @param message the message
     */
    public static void log(String message) {
        log.info(message);
        UnionLogger.LOGGER.log(message);
    }
    
    public static void logBool(String message, boolean bool) {
    	String ansStr = bool ? "yes" : "no";
    	log(message + ": " + ansStr);
    }
}
