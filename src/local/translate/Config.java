package local.translate;

import org.apache.log4j.Level;

/**
 * The Class Config.
 */
public class Config {

    /** The delimeter. */
    public static String delimeter = "#";

    /** The alt delimeter. */
    public static String altDelimeter = ":";

    /** The negation which should appear in the rules at the end. */
    public static String negation = "tnot";

    /** The search negation what should be replaced. */
    public static String searchNegation = "not";

    /** The equivalent symbols */
    public static String eq = ":-";

    /** The rule creation debug. */
    public static boolean ruleCreationDebug = false;

    /** The log level. */
    public static Level logLevel = Level.OFF;
    
    public static TranslationAlgorithm translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
}
