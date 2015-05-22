package nohr.standalone;
import java.io.File;
import java.util.Date;
import java.util.Scanner;

import nohr.reasoner.translation.Translate;

public class Console {
    /**
     * @param args
     * @throws org.semanticweb.owlapi.model.OWLOntologyCreationException
     * @throws java.io.IOException
     * @throws org.semanticweb.owlapi.expression.ParserException
     * @throws org.semanticweb.owlapi.model.OWLOntologyStorageException
     */
    public static void main(String[] args) throws Exception {

        Date timeStart;
        Date timeEnd;
        Date timeProceedStart;
        Date timeProceedEnd;

        timeStart = new Date();
        Scanner inp = new Scanner( System.in );
        String ontologyPath;
        if(args.length > 0){
            //            System.out.println("Please specify arguments, at least ontology file path");
            //            System.exit(0);
            ontologyPath = args[0];
        } else {
            System.out.print("enter ontology file path: ");
            ontologyPath = inp.next();
        }

        File file = new File(ontologyPath);

        if(!file.exists()){
            System.out.println("Please specify correct path for ontology file");
            System.exit(0);
        }
        System.out.println("Initialization started, ontology is: "+file.getName());
        //        OntologyLogger.log.setLevel(Level.OFF);
        Translate translate = new Translate(ontologyPath);

        timeProceedStart = new Date();
        translate.proceed();

        String resultPath;
        if (args.length > 1 && args[1] != null) {
            resultPath = args[1];
        } else {
            System.out.print("enter result file path: ");
            resultPath = inp.next();
        }

        //        ontology.setResultFileName("result/"+file.getName().replace(".owl",".p"));
        translate.setResultFileName(resultPath);

        if (args.length > 2 && args[2] != null){
            String rulePath = args[2];
            file = new File(rulePath);
            if(file.exists()){
                translate.appendRules(rulePath);
            }
            System.out.println("Additional rule file: "+file.getName());
            //            ontology.setResultFileName(file.getName());
            file =null;
        } else if (args.length != 2){
            System.out.print("enter rule file path, 'n' for no file: ");
            String rulePath = inp.next();

            if (!rulePath.toLowerCase().trim().equals("n")) {
                file = new File(rulePath);
                if(file.exists()){
                    translate.appendRules(rulePath);
                }
                System.out.println("Additional rule file: "+file.getName());
            }
        }
        timeProceedEnd = new Date();

        translate.Finish();
        translate.clear();
        translate = null;
        timeEnd = new Date();
        System.out.println("Total time is "+(timeEnd.getTime()-timeStart.getTime())+" milisec");
        System.out.println("====================================================================================");
        System.exit(0);

    }
}
