package hybrid.query.model;

import org.apache.log4j.Level;

/**
 * The Class Configuration for query model using in protege.
 */
public final class Config {
    /** The is debug. */
    public static final boolean ISDEBUG = false;

    /** The log level. */
    public static final Level LOGLEVEL = Level.OFF;

    /** The nl. The new line symbol */
    public static final String NL = System.getProperty("line.separator");

    /** The temp dir prop. */
    public static final String TEMP_DIR_PROP = "java.io.tmpdir";

    /** The temp dir. */
    public static final String TEMP_DIR = System.getProperty(TEMP_DIR_PROP);

    /**
     * Instantiates a new config.
     */
    private Config() {

    }
}
