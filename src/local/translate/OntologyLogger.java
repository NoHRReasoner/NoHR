package local.translate;

import org.apache.log4j.Logger;

public class OntologyLogger {
	private static final Logger log = Logger.getLogger(Ontology.class);
//    public OntologyLogger(){
//    	log.setLevel(Config.logLevel);
//    }
    public static void log(String message){
    	log.info(message);
	}
}
