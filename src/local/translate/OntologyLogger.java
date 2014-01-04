package local.translate;

import org.apache.log4j.Logger;
import union.logger.UnionLogger;

import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class OntologyLogger.
 */
public class OntologyLogger {

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(OntologyLogger.class);

    /**
     * Debug.
     * 
     * @param message
     *            the message
     */
    public static void debug(Object message) {
	log.info(message);
    }

    /**
     * Gets the diff time.
     * 
     * @param startDate
     *            the start date
     * @param message
     *            the message
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
     * @param message
     *            the message
     */
    public static void log(String message) {
	log.info(message);
	UnionLogger.LOGGER.log(message);
    }
}
