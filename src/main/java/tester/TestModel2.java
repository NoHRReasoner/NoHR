package tester;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import rdf_converter.ODBC;
import rdf_converter.ODBCImpl;
import rdf_converter.SQLTutorials;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TestModel2 {


    public static void main(String[] args) {


        File f2 = new File("files/commons-sameas-links_lang=en.ttl"); // 39999

        File f4 = new File("files/links_domain=dnb_lang=en.nt"); //233106

        File f6 = new File("files/nl.lhd.raw.2016-04.nt"); //1661699 triples

        File f9 = new File("files/Apertium-eo-fr_LexiconEO.ttl");//726281
        File f10 = new File("files/BNBLODB_202010_f01.nt");//3470070

        List<File> files = new LinkedList<>();
        files.add(f2);
        files.add(f4);
        files.add(f9);
        files.add(f6);
        files.add(f10);

        List<String> databaseNames = new LinkedList<>();
        databaseNames.add("f2");
        databaseNames.add("f4");
        databaseNames.add("f9");
        databaseNames.add("f6");
        databaseNames.add("f10");

        /*
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("results.txt")));
            testRun(bw,files,5);
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("queryResults.txt")));
            //testQuery(bw,files,5, databaseNames);
            for (int i = 0; i < 5; i++)
                createQuery(bw, files.get(i), 1, databaseNames.get(i));
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // int nCicles = 1;
        //searchQuery(f2);
        //dummyQuery(f2);
        //  createTest(f10, nCicles);
    }

    private static void createTest(BufferedWriter bw, File f, int nCicles) throws IOException {
        List<Long> testPreProcessing = new LinkedList<>();
        List<Long> testPreProcessingWithPrefixes = new LinkedList<>();

        for (int i = 0; i < nCicles; i++) {
            testTimes(bw, f, testPreProcessing, testPreProcessingWithPrefixes, i);
        }
        getAverage(bw, testPreProcessing, "Average of preprocessing without prefixes: ");
        getAverage(bw, testPreProcessingWithPrefixes, "Average of preprocessing with prefixes: ");
    }

    private static void testTimes(BufferedWriter bw, File f, List<Long> testPreProcessing, List<Long> testPreProcessingWithPrefixes, int i) throws IOException {
        //System.out.printf("Test %d%n", i + 1);

        String testTitle = String.format("Test %d%n", i + 1);

        bw.write(testTitle);
        bw.newLine();

        long start = System.nanoTime();
        testPreProcessing(bw, f);
        long end = System.nanoTime();

        long millis = end - start;
        testPreProcessing.add(millis);
        String s = String.format("%d min, %d sec",
                TimeUnit.NANOSECONDS.toMinutes(millis),
                TimeUnit.NANOSECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(millis))
        );

        String realS = String.format("Preprocessing without prefixes: %s%n", s);
        bw.write(realS);
        bw.newLine();
        //System.out.printf("Preprocessing without prefixes: %s%n", s);

        Model m = ModelFactory.createDefaultModel();
        m.read(f.getPath());
        if (m.hasNoMappings())
            return;
        long start1 = System.nanoTime();
        testPreProcessingWithPrefixes(f);
        long end1 = System.nanoTime();

        long millis1 = end1 - start1;
        testPreProcessingWithPrefixes.add(millis1);
        String s1 = String.format("%d min, %d sec",
                TimeUnit.NANOSECONDS.toMinutes(millis1),
                TimeUnit.NANOSECONDS.toSeconds(millis1) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(millis1))
        );

        String realS1 = String.format("Preprocessing with prefixes: %s%n", s1);
        bw.write(realS1);
        bw.newLine();
        //System.out.printf("Preprocessing with prefixes: %s%n", s1 + "\n");
    }

    private static void getAverage(BufferedWriter bw, List<Long> l, String string) throws IOException {

        if (l.isEmpty())
            return;
        long sum = 0L;
        for (long s : l) {
            sum += s;
        }
        sum /= l.size();

        String s = String.format("%d min, %d sec",
                TimeUnit.NANOSECONDS.toMinutes(sum),
                TimeUnit.NANOSECONDS.toSeconds(sum) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(sum))
        );

        String writer = String.format(string + s);

        bw.write(writer);
        bw.newLine();
        //System.out.println(string + s);
    }

    public static void testPreProcessing(BufferedWriter bw, File file) throws IOException {


        Model model = ModelFactory.createDefaultModel();

        model.read(file.getPath());
        StmtIterator it = model.listStatements();
        SortedSet<String> predicates = new TreeSet<>();

        int counter = it.toList().size();


        //System.out.printf("Number of triples: %d%n ",counter);

        String tripleCounter = String.format("Number of triples: %d%n ", counter);

        bw.write(tripleCounter);
        bw.newLine();

        while (it.hasNext()) {
            Statement s = it.nextStatement();

            String predicate = s.getPredicate().toString();

            predicates.add(predicate);

        }

        int size = predicates.size();
        List<String>[] tableValues = new List[size];
        for (int i = 0; i < size; i++) {
            tableValues[i] = new ArrayList<>(3);
        }

    }

    public static void testPreProcessingWithPrefixes(File file) {
        Model model = ModelFactory.createDefaultModel();
        model.read(file.getPath());
        StmtIterator it = model.listStatements();
        SortedSet<String> predicates = new TreeSet<>();
        if (model.hasNoMappings())
            return;

        while (it.hasNext()) {
            Statement s = it.nextStatement();

            String namespace = s.getModel().getNsURIPrefix(s.getPredicate().getNameSpace());
            String localName = s.getPredicate().getLocalName();

            String string = namespace + ":" + localName;
            predicates.add(string);
        }
        int size = predicates.size();
        List<String>[] tableValues = new List[size];

        for (int i = 0; i < size; i++) {
            tableValues[i] = new ArrayList<>(3);
        }
    }

    public static void testRun(BufferedWriter bw, List<File> files, int nCicles) throws IOException {

        for (File f : files) {
            createTest(bw, f, nCicles);
        }

    }

    public static void testQuery(BufferedWriter bw, List<File> files, int nCicles, List<String> databaseNames) throws IOException {
        for (File f : files) {
            String databaseName = databaseNames.remove(0);
            createQuery(bw, f, nCicles, databaseName);
        }
    }

    private static void createQuery(BufferedWriter bw, File f, int nCicles, String databaseName) throws IOException {

        List<Long> testquery = new LinkedList<>();

        ODBC database = new ODBCImpl("mydatabase", "127.0.0.1", 3306, "user", "123");

        for (int i = 0; i < nCicles; i++) {
            timeQuery(bw, f, i, testquery, database, databaseName);
        }

        getAverage(bw, testquery, "Average of query execution and database insertion is: ");
    }

    public static void searchQuery(BufferedWriter bw, File file, ODBC database, String databaseName) throws IOException {

        Model model = ModelFactory.createDefaultModel();
        model.read(file.getPath());
        String queryV = "Select * \n" +
                "where {\n" +
                "?a ?x ?y\n" +
                "}\n";

        int counter = model.listStatements().toList().size();

        String tripleCounter = String.format("Number of triples: %d%n ", counter);

        bw.write(tripleCounter);
        bw.newLine();

        Map<String, List<String>> rdfanswers = new HashMap<>();

        Query query = QueryFactory.create(queryV);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            Iterator<QuerySolution> results2 = qexec.execSelect();
            for (; results2.hasNext(); ) {
                QuerySolution sol = results2.next();
                for (Iterator<String> it = sol.varNames(); it.hasNext(); ) {
                    String varname = it.next();
                    rdfanswers.putIfAbsent(varname, new LinkedList<>());
                    rdfanswers.get(varname).add(sol.get(varname).toString());
                }
            }
        }

        SQLTutorials.createDatabase(database.getDatabaseName(), database, databaseName, rdfanswers);

    }

    public static void dummyQuery(File file) throws FileNotFoundException {

        Model model = ModelFactory.createDefaultModel();
        model.read(file.getPath());
        String queryV = "Select *\n" +
                "where {\n" +
                "?a ?x ?y\n" +
                "}\n";


        OutputStream o = new FileOutputStream("queryResult.csv");
        Query query = QueryFactory.create(queryV);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.outputAsCSV(o, results);
        }

    }


    public static void timeQuery(BufferedWriter bw, File f, int nCicles, List<Long> testQuery, ODBC database, String databaseName) throws IOException {
        String testTitle = String.format("Test %d%n", nCicles + 1);

        bw.write(testTitle);
        bw.newLine();

        long start = System.nanoTime();
        searchQuery(bw, f, database, databaseName);
        long end = System.nanoTime();

        long millis = end - start;
        testQuery.add(millis);
        String s = String.format("%d min, %d sec",
                TimeUnit.NANOSECONDS.toMinutes(millis),
                TimeUnit.NANOSECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.NANOSECONDS.toMinutes(millis))
        );

        String realS = String.format("Query answered in %s%n", s);

        bw.write(realS);
        bw.newLine();

        // SQLTutorials.dropDatabase(database.getDatabaseName(), database, databaseName);
    }
}
