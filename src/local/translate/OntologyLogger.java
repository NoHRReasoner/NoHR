package local.translate;

import java.util.Date;

import org.apache.log4j.Logger;

import union.logger.UnionLogger;

public class OntologyLogger {
	private static final Logger log = Logger.getLogger(OntologyLogger.class);
//    public OntologyLogger(){
//    	log.setLevel(Config.logLevel);
//    }
    public static void log(String message){
    	//log.info(message);
    	UnionLogger.logger.log(message);
	}
    public static void getDiffTime(Date startDate, String message){
        Date stoped=new Date();
        long diff=stoped.getTime() - startDate.getTime();
        log(message+" "+diff+" milisec");
    }
}
