/*
 * 
 */
package hybrid.query.model;

import com.declarativa.interprolog.TermModel;
import hybrid.query.views.Rules;
import local.translate.Logger;
import local.translate.Translate;
import local.translate.Utils;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import union.logger.UnionLogger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class Query - the main bridge between translator, query engine and ui
 */
public class Query {

    /** The owl model manager. */
    private static OWLModelManager owlModelManager;

    /** The is ontology changed. */
    private static boolean isOntologyChanged;

    /** The is compiled. */
    private boolean isCompiled = false;

    /** The query engine. */
    private QueryEngine queryEngine;

    /** The instance of the local.translator */
    private static Translate translator;

    /**
     * Dispose.
     */
    public static void dispose() {

        if (owlModelManager != null) {
            owlModelManager.removeOntologyChangeListener(ontologyChangeListener);
            owlModelManager.removeListener(modelManagerListener);
        }
        Rules.dispose();
    }

    /**
     * Inits the translator.
     */
    private static void initTranslator() {
        try {
            translator = new Translate(owlModelManager);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /** The list of query variables. */
    private ArrayList<String> variablesList = new ArrayList<String>();

    /** The list of answers. */
    private ArrayList<ArrayList<String>> answers = new ArrayList<ArrayList<String>>();

    /** The header pattern. */
    private final Pattern headerPattern = Pattern.compile("\\((.*?)\\)");

    /** The query string. */
    private String queryString;

    /** The previous query. */
    private String previousQuery = "";

    /** The Constant log. */
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Query.class);

    /** The is query for all. */
    private boolean isQueryForAll = true;

    /** The queried for all. */
    private boolean queriedForAll;

    /** The filter. */
    private String filter = "";

    /** The var x pattern. */
    private final Pattern varXPattern = Pattern.compile("Var\\d+");

    /**
     * The ontology change listener. Fired when axioms are added and removed
     */
    private static OWLOntologyChangeListener ontologyChangeListener = new OWLOntologyChangeListener() {
        @Override
        public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
            isOntologyChanged = true;
        }
    };

    /** The model manager listener. */
    private static OWLModelManagerListener modelManagerListener = new OWLModelManagerListener() {

        @Override
        public void handleChange(OWLModelManagerChangeEvent event) {
            isOntologyChanged = true;
            if (event.isType(org.protege.editor.owl.model.event.EventType.ACTIVE_ONTOLOGY_CHANGED)) {
                Rules.dispose();
                if (translator != null) {
                    translator.clear();
                }
                initTranslator();
            }
        }
    };

    /**
     * Instantiates a new query.
     *
     * @param OwlModelManager the owl model manager
     * @throws Exception the exception
     */
    public Query(OWLModelManager OwlModelManager) throws Exception {
        owlModelManager = OwlModelManager;
        owlModelManager.addOntologyChangeListener(ontologyChangeListener);
        owlModelManager.addListener(modelManagerListener);
        // queryEngine = new QueryEngine();

        LOG.setLevel(Config.LOGLEVEL);
    }

    /**
     * Check and start engine.
     */
    private void checkAndStartEngine() {
        if (!isCompiled) {
            try {
                Date initAndTranslateTime = new Date();
                initTranslator();
                translator.proceed();
                translator.appendRules(Rules.getRules());
                File xsbFile = translator.Finish();
                Logger.log("-----------------------");
                Logger.getDiffTime(initAndTranslateTime,
                                   "Total translation time: ");
                Logger.log("");
                compileFile(xsbFile);
                isOntologyChanged = false;
                Rules.isRulesOntologyChanged = false;
                // _ontology.printAllLabels();
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            } catch (OWLOntologyStorageException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (isChanged()) {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Some changes have been made, would you like to recompile?", "Warning", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                try {
                    boolean disjointStatement = translator.isAnyDisjointWithStatement();
                    Date initAndTranslateTime = new Date();
                    if (isOntologyChanged) {
                        translator.PrepareForTranslating();
                        translator.proceed();
                        isOntologyChanged = false;
                        LOG.info("Ontology recompilation");
                    }
                    if ((disjointStatement != translator.isAnyDisjointWithStatement()) || Rules.isRulesOntologyChanged) {
                        translator.appendRules(Rules.getRules());
                        LOG.info("Rule recompilation");
                        Rules.isRulesOntologyChanged = false;
                    }
                    File xsbFile = translator.Finish();
                    Logger.getDiffTime(initAndTranslateTime, "Total translating time: ");
                    Logger.log("");
                    compileFile(xsbFile);
                    // _ontology.printAllLabels();
                } catch (OWLOntologyCreationException e) {
                    e.printStackTrace();
                } catch (OWLOntologyStorageException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    previousQuery = "";
                }
            }
        }
    }

    /**
     * Clear table.
     */
    public void clearTable() {
        variablesList = new ArrayList<String>();
        answers = new ArrayList<ArrayList<String>>();
    }

    /**
     * Compile file.
     *
     * @param file the file
     * @return true, if successful
     * @throws Exception the exception
     */
    public boolean compileFile(File file) throws Exception {
        Date loadingFileTime = new Date();
        queryEngine = new QueryEngine();
        isCompiled = false;
        if (queryEngine.isEngineStarted() && queryEngine.load(file)) {
            isCompiled = true;
        }
        if (isQueriable()) {
            queryEngine.deterministicGoal(generateDetermenisticGoal("initQuery"));
        }
        Logger.getDiffTime(loadingFileTime, "Loading XSB file: ");
        Logger.log("");
        return isCompiled;
    }

    /**
     * Dispose query.
     */
    public void disposeQuery() {
        owlModelManager.removeOntologyChangeListener(ontologyChangeListener);
        owlModelManager.removeListener(modelManagerListener);
        if (translator != null) {
            translator.clear();
        }
        if (queryEngine != null) {
            queryEngine.shutdown();
        }
        Rules.dispose();
    }

    /**
     * Fill table header.
     *
     * @param command the command
     */
    private void fillTableHeader(String command) {
        clearTable();
        try {
            Matcher m = headerPattern.matcher(command);
            StringBuffer sb = new StringBuffer();
            String rule;
            while (m.find()) {
                m.appendReplacement(sb, m.group());
                rule = m.group();
                rule = rule.substring(1, rule.length() - 1);
                for (String s : rule.split(",")) {
                    s = s.trim();
                    if (Character.isUpperCase(s.charAt(0))
                        && !variablesList.contains(s)) {
                        variablesList.add(s);
                    }
                }
            }
            sb.setLength(0);

        } catch (Exception e) {
            LOG.error("fillTableHeader: " + e.toString());
        }
    }

    /**
     * Generate determenistic goal.
     *
     * @param command the command
     * @return the string
     */
    private String generateDetermenisticGoal(String command) {
        String detGoal = "findall(myTuple(TV";
        if (variablesList.size() > 0) {
            detGoal += ", ";
            detGoal += variablesList.toString().replace("[", "").replace("]", "");
        }
        // detGoal+="), call_tv(("+command+"), TV), List), buildTermModel(List,TM)";
        detGoal += "), call_tv((" + command + "), TV), List), buildInitiallyFlatTermModel(List,TM)";
        return detGoal;
    }

    /**
     * Generate sub query.
     *
     * @param command the command
     * @param model   the model
     * @return the string
     */
    private String generateSubQuery(String command, TermModel model) {
        String result = "";
        int index;
        String vars = "";
        if (variablesList.size() > 0) {
            for (String s : command.split("\\)\\s*,")) {
                index = s.lastIndexOf("(");
                if (index > 0) {
                    result += s.substring(0, index);
                    vars = s.substring(index + 1, s.length());
                    for (int j = 1; j <= variablesList.size(); j++) {
                        vars = vars.replace(variablesList.get(j - 1), model.getChild(j).toString());
                    }
                    result += "(" + vars;
                    if (!result.endsWith(")")) {
                        result += ")";
                    }
                    result += ", ";

                } else {
                    result += s + ", ";
                }
            }
            result = result.substring(0, result.length() - 2);
            return result;
        }
        return command;
    }

    /**
     * Gets the data.
     *
     * @return the data
     */
    private ArrayList<ArrayList<String>> getData() {
        @SuppressWarnings( "unchecked" )
        ArrayList<ArrayList<String>> rows = (ArrayList<ArrayList<String>>) answers.clone();
        rows.add(0, variablesList);
        return rows;
    }

    /**
     * Checks if is changed.
     *
     * @return true, if is changed
     */
    private boolean isChanged() {
        return Rules.isRulesOntologyChanged || isOntologyChanged;
    }

    /**
     * Checks if is queriable.
     *
     * @return true, if is queriable
     */
    public boolean isQueriable() {
        return (queryEngine != null) && queryEngine.isEngineStarted() && isCompiled;
    }

    /**
     * Prints the info.
     *
     * @param text the text
     */
    public void printInfo(String text) {
        // outPutLog.append(text+Config.nl);
        LOG.info(text);
        UnionLogger.LOGGER.log(text);
    }

    /**
     * Prints the log.
     *
     * @param text the text
     */
    public void printLog(String text) {
        if (Config.ISDEBUG) {
            printInfo(text);
        }
    }

    /**
     * Query.
     *
     * @param command the command
     * @return the array list
     */
    public ArrayList<ArrayList<String>> query(String command) {
        queryString = command;
        return queryXSB();

    }

    /**
     * Query xsb.
     *
     * @return the array list
     */
    public ArrayList<ArrayList<String>> queryXSB() {
        String command = queryString;
        checkAndStartEngine();
        if (isQueriable()) {
            if (command.endsWith(".")) {
                command = command.substring(0, command.length() - 1);
            }
            command = translator.prepareQuery(command);
            // previousQuery="";
            if (!command.equals(previousQuery) || !queriedForAll) {
                printInfo("You queried: " + queryString);
                previousQuery = command;
                queriedForAll = isQueryForAll;
                printLog("prepared query: " + command);
                fillTableHeader(command);
                String detGoal = generateDetermenisticGoal(command);
                String subDetGoal;
                printLog("detGoal: " + detGoal);
                Date queryStart = new Date();
                Date subQueryTime;
                Object[] bindings = queryEngine.deterministicGoal(detGoal);
                Logger.getDiffTime(queryStart, "Main query time: ");
                ArrayList<String> row = new ArrayList<String>();
                String value;
                String subValue;
                if (bindings != null) {

                    TermModel list = (TermModel) bindings[0]; // this gets you
                    // the list as a
                    // binary tree
                    TermModel[] flattted = list.flatList();
                    for (TermModel element : flattted) {
                        // if(i==1 && !isQueryForAll)
                        // break;
                        value = element.getChild(0).toString();

                        if (value.length() > 0) {
                            row = new ArrayList<String>();
                            row.add(value);
                            for (int j = 1; j <= variablesList.size(); j++) {
                                subValue = translator.getLabelByHash(element.getChild(j).toString());
                                subValue = varXPattern.matcher(subValue).find() ? "all values" : subValue;
                                row.add(subValue);
                            }
                            if (!translator.isAnyDisjointWithStatement()) {
                                answers.add(row);
                            } else {
                                if (value.equals("true") || value.equals("undefined")) {
                                    // printLog("_dRule: "+Utils._dAllrule(command));
                                    subDetGoal = generateDetermenisticGoal(generateSubQuery(Utils._dAllrule(command), element));
                                    printLog("SubDetGoal is: " + subDetGoal);
                                    subQueryTime = new Date();
                                    Object[] subBindings = queryEngine.deterministicGoal(subDetGoal);
                                    Logger.getDiffTime(subQueryTime, "Doubled subgoal time: ");
                                    // this gets you the list as a binary tree
                                    TermModel subList = (TermModel) subBindings[0];
                                    TermModel[] subFlattted = subList.flatList();

                                    if (subFlattted.length > 0) {
                                        String subAnswer = subFlattted[0].getChild(0).toString();
                                        if (subAnswer.equals("no") || subAnswer.equals("false")) {
                                            if (value.equals("true")) {
                                                row.set(0, "inconsistent");
                                                answers.add(row);
                                            } else if (value.equals("undefined")) {
                                                row.set(0, "false");
                                                answers.add(row);
                                            }
                                        } else {
                                            answers.add(row);
                                        }
                                    } else {
                                        if (value.equals("true")) {
                                            row.set(0, "inconsistent");
                                            answers.add(row);
                                        }
                                    }
                                } else {
                                    answers.add(row);
                                }
                            }
                        }
                        if (!isQueryForAll && filter.contains(row.get(0))) {
                            break;
                        }

                    }
                    if (flattted.length == 0) {
                        row = new ArrayList<String>();
                        row.add(variablesList.size() > 0 ? "no answers found" : "false");
                        clearTable();
                        answers.add(row);
                    }
                    if (answers.size() == 0) {
                        clearTable();
                        row = new ArrayList<String>();
                        row.add(variablesList.size() > 0 ? "no answers found" : "false");
                        answers.add(row);
                    }
                } else {
                    clearTable();
                    row = new ArrayList<String>();
                    row.add("no answers found");
                    answers.add(row);
                    LOG.error("Query was interrupted by engine.");
                    try {
                        compileFile(translator.Finish());
                    } catch (IOException e) {
                        LOG.error(e);
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
                Logger.log("-----------------------");
                Logger.getDiffTime(queryStart, "Total query time: ");
                Logger.log("");
            }
        }
        return getData();
    }

    /**
     * Sets the filter.
     *
     * @param f the new filter
     */
    public void setFilter(String f) {
        if (!filter.equals(f)) {
            filter = f;
        }
    }

    /**
     * Sets the checks if is query for all. otherwise query just first answer
     *
     * @param flag the new checks if is query for all
     */
    public void setIsQueryForAll(boolean flag) {
        isQueryForAll = flag;
    }
}
