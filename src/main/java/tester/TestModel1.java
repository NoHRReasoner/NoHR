package tester;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestModel1 {

    /*
      Used to test NoHR with RDF2x implemented
    */
    public static void main(String[] args) throws IOException {

        File f2 = new File("files/commons-sameas-links_lang=en.ttl"); // 39999
        File f4 = new File("files/links_domain=dnb_lang=en.nt"); //233106
        File f9 = new File("files/Apertium-eo-fr_LexiconEO.ttl");//726281
        File f6 = new File("files/nl.lhd.raw.2016-04.nt"); //1661699 triples
        File f10 = new File("files/BNBLODB_202010_f01.nt");//3470070
        List<File> files = new LinkedList<>();
        files.add(f2);
        files.add(f4);
        files.add(f9);
        files.add(f6);
        files.add(f10);

        int[] values = new int[]{39999, 233106, 726281, 1661699, 3470070};
        String username = "bruno ";
        String password = "123 \"";
        String connectionName = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String connectionNameNecessity = "?useTimezone=true&serverTimezone=GMT&rewriteBatchedStatements=true ";
        String directory = "C:\\Users\\Bruno\\Desktop\\rdf2x-master\\rdf2x-master ";
        String and = "&& ";
        String maven = "mvn exec:java -Dexec.args=\"convert ";
        String inputCommand = "--input.file ";
        String start = "cmd.exe /C cd ";
        String dbcommand = "--output.target DB ";
        String dburlcommand = "--db.url ";
        String dbuser = "--db.user ";
        String dbpassword = "--db.password ";

        String fullcmdLine = start + directory + and + maven + inputCommand
                + files.get(4).getPath() + " " + dbcommand + dburlcommand +
                connectionName + connectionNameNecessity + dbuser + username + dbpassword + password;

        CommandLine cmdLine = CommandLine.parse(fullcmdLine);

        //Terminar isto e testar e verificar o connection name no NOHR;
        int[] indexes = new int[]{1, 2, 3, 4, 5};

        try {
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(0);

            long start1 = System.nanoTime();
            executor.execute(cmdLine);
            long end1 = System.nanoTime();

            long millis1 = end1 - start1;

            String s1 = String.format("%d min, %d sec",
                    TimeUnit.NANOSECONDS.toMinutes(millis1),
                    TimeUnit.NANOSECONDS.toSeconds(millis1) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(millis1))
            );


            System.out.printf("Test: %d%n ", indexes[0]);
            System.out.printf("Number of triples: %d%n ", values[4]);
            System.out.printf("Final time: %s%n", s1);
            System.out.printf("NanoSec: %dL %n ", millis1);
        } catch (ExecuteException e) {
            e.printStackTrace();
        }

        //System.out.println("Fez este tambem");


    }
}
