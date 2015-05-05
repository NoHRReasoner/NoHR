/*
 * 
 */
package hybrid.query.model;

import hybrid.query.views.Rules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import local.translate.Query;
import local.translate.Translate;
import local.translate.Utils;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import utils.Tracer;

import com.declarativa.interprolog.TermModel;

public class NoHR implements OWLOntologyChangeListener {

	private static final Pattern HEADER_PATTERN = Pattern
			.compile("\\((.*?)\\)");

	private static boolean isOntologyChanged;

	private static Query query;

	private static Translate translator;

	private static final Pattern VARXPATTERN = Pattern.compile("Var\\d+");

	private ArrayList<ArrayList<String>> answers = new ArrayList<ArrayList<String>>();

	private String filter = "";

	private boolean gc;

	private boolean hasDisjunction;

	private boolean isCompiled = false;

	private boolean isQueryForAll = true;

	private Map<String, String> labels;

	private OWLOntologyManager om;

	private OWLOntology ontology;

	private String previousQuery = "";

	private boolean queriedForAll;

	private int queryCount;

	private QueryEngine queryEngine;

	private String queryString;

	private OWLReasoner reasoner;

	private ArrayList<String> variablesList = new ArrayList<String>();

	private File xsbFile;

	public NoHR(OWLOntology owlOntology) {
		this(owlOntology, true);
	}

	public NoHR(OWLOntology ontology, boolean gc) {
		try {
			this.om = OWLManager.createOWLOntologyManager();
			this.ontology = ontology;
			this.gc = gc;
			if (gc) {
				utils.Tracer.start("loading");
				preprocessKb();

				utils.Tracer.stop("loading", "loading");
				Translate.collectionsManager = null;
				translator = null;
				ontology = null;
				System.gc();
				queryCount = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public NoHR(OWLOntologyManager ontologyManager, OWLOntology ontolgy,
			OWLReasoner owlReasoner) throws Exception {
		this.gc = false;
		this.om = ontologyManager;
		this.ontology = ontolgy;
		this.reasoner = owlReasoner;
		this.om.addOntologyChangeListener(this);
	}

	public void abolishTables() {
		queryEngine.abolishTables();
	}

	private void clearTable() {
		variablesList = new ArrayList<String>();
		answers = new ArrayList<ArrayList<String>>();
	}

	private boolean compileFile(File file) throws Exception {
		utils.Tracer.start("xsb loading");
		queryEngine = new QueryEngine();
		isCompiled = false;
		if (queryEngine.isEngineStarted() && queryEngine.load(file)) {
			isCompiled = true;
		}
		if (isQueriable()) {
			queryEngine
					.deterministicGoal(generateDetermenisticGoal("initQuery"));
		}
		utils.Tracer.stop("xsb loading", "loading");
		return isCompiled;
	}

	public void dispose() {
		if (om != null) {
			om.removeOntologyChangeListener(this);
		}
		if (translator != null) {
			translator.clear();
		}
		if (queryEngine != null) {
			queryEngine.shutdown();
		}
		Rules.dispose();
	}

	private void fillTableHeader(String command) {
		clearTable();
		try {
			Matcher m = HEADER_PATTERN.matcher(command);
			StringBuffer sb = new StringBuffer();
			String rule;
			while (m.find()) {
				m.appendReplacement(sb, m.group());
				rule = m.group();
				rule = rule.substring(1, rule.length() - 1);
				for (String s : rule.split(",")) {
					s = s.trim();
					if ((Character.isUpperCase(s.charAt(0)))
							&& !variablesList.contains(s)) {
						variablesList.add(s);
					}
				}
			}
			sb.setLength(0);

		} catch (Exception e) {
			Tracer.err("fillTableHeader: " + e.toString());
		}
	}

	private String generateDetermenisticGoal(String command) {
		String detGoal = "findall(myTuple(TV";
		if (variablesList.size() > 0) {
			detGoal += ", ";
			detGoal += variablesList.toString().replace("[", "")
					.replace("]", "");
		}
		// detGoal+="), call_tv(("+command+"), TV), List), buildTermModel(List,TM)";
		detGoal += "), call_tv((" + command
				+ "), TV), List), buildInitiallyFlatTermModel(List,TM)";
		return detGoal;
	}

	/**
	 * Generate sub query.
	 *
	 * @param command
	 *            the command
	 * @param model
	 *            the model
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
						vars = vars.replace(variablesList.get(j - 1), model
								.getChild(j).toString());
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
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<String>> rows = (ArrayList<ArrayList<String>>) answers
				.clone();
		rows.add(0, variablesList);
		return rows;
	}

	private String getLabelByHash(String hash) {
		String originalHash = hash;
		hash = hash.substring(1, hash.length());
		if (labels.containsKey(hash)) {
			return labels.get(hash);
		}
		return originalHash;
	}

	private boolean isChanged() {
		return Rules.isRulesOntologyChanged || isOntologyChanged;
	}

	private boolean isQueriable() {
		return (queryEngine != null) && queryEngine.isEngineStarted()
				&& isCompiled;
	}

	@Override
	public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
			throws OWLException {
		try {
			translator = new Translate(om, ontology, reasoner);
			for (OWLOntologyChange change : changes) {
				if (change.getOntology() == ontology) {
					isOntologyChanged = true;
					Rules.dispose();
					if (NoHR.translator != null) {
						NoHR.translator.clear();
					}
					break;
				}
			}
		} catch (IOException | CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private void preprocessKb() throws Exception {
		if (!isCompiled) {
			utils.Tracer.start("translator initialization");
			translator = new Translate(om, ontology, reasoner);
			utils.Tracer.stop("translator initialization", "loading");
			utils.Tracer.start("ontology proceeding");
			translator.proceed();
			utils.Tracer.stop("ontology proceeding", "loading");
			utils.Tracer.start("rules parsing");
			translator.appendRules(Rules.getRules());
			utils.Tracer.stop("rules parsing", "loading");
			utils.Tracer.start("file writing");
			xsbFile = translator.Finish();
			utils.Tracer.stop("file writing", "loading");
			compileFile(xsbFile);
			isOntologyChanged = false;
			Rules.isRulesOntologyChanged = false;
		} else if (isChanged()) {
			Tracer.info("ontology changed");
			try {
				boolean disjointStatement = translator
						.isAnyDisjointWithStatement();
				if (isOntologyChanged) {
					translator.PrepareForTranslating();
					translator.proceed();
					isOntologyChanged = false;
				}
				if ((disjointStatement != translator
						.isAnyDisjointWithStatement())
						|| Rules.isRulesOntologyChanged) {
					utils.Tracer.start("rules parsing");
					translator.appendRules(Rules.getRules());
					utils.Tracer.stop("rules parsing", "loading");
					Rules.isRulesOntologyChanged = false;
				}
				utils.Tracer.start("file writing");
				File xsbFile = translator.Finish();
				utils.Tracer.stop("file writing", "loading");
				compileFile(xsbFile);
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
		hasDisjunction = translator.isAnyDisjointWithStatement();
		labels = translator.getCollectionsManager().getLabels();
		query = new local.translate.Query(translator.getCollectionsManager());
	}

	public ArrayList<ArrayList<String>> query(String command) {
		queryString = command;
		try {
			// String command = queryString;
			 if (!gc)
				 preprocessKb();
			if (isQueriable()) {
				if (command.endsWith(".")) {
					command = command.substring(0, command.length() - 1);
				}
				command = query.prepareQuery(command, hasDisjunction);
				// previousQuery="";
				if ((!command.equals(previousQuery) || !queriedForAll)) {
					Tracer.info("You queried: " + queryString);
					previousQuery = command;
					queriedForAll = isQueryForAll;
					fillTableHeader(command);
					String detGoal = generateDetermenisticGoal(command);
					String subDetGoal;
					utils.Tracer.start("query" + queryCount);
					Object[] bindings = queryEngine.deterministicGoal(detGoal);

					ArrayList<String> row = new ArrayList<String>();
					String value;
					String subValue;
					if (bindings != null) {

						TermModel list = (TermModel) bindings[0]; // this gets
																	// you
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
									subValue = getLabelByHash(element.getChild(
											j).toString());
									subValue = VARXPATTERN.matcher(subValue)
											.find() ? "all values" : subValue;
									row.add(subValue);
								}
								if (!hasDisjunction) {
									answers.add(row);
								} else {
									if (value.equals("true")
											|| value.equals("undefined")) {
										subDetGoal = generateDetermenisticGoal(generateSubQuery(
												Utils._dAllrule(command),
												element));

										Object[] subBindings = queryEngine
												.deterministicGoal(subDetGoal);
										// this gets you the list as a binary
										// tree
										TermModel subList = (TermModel) subBindings[0];
										TermModel[] subFlattted = subList
												.flatList();

										if (subFlattted.length > 0) {
											String subAnswer = subFlattted[0]
													.getChild(0).toString();
											if (subAnswer.equals("no")
													|| subAnswer
															.equals("false")) {
												if (value.equals("true")) {
													row.set(0, "inconsistent");
													answers.add(row);
												} else if (value
														.equals("undefined")) {
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
						if ((flattted.length == 0) || (answers.size() == 0)) {
							row = new ArrayList<String>();
							row.add(variablesList.size() > 0 ? "no answers found"
									: "false");
							clearTable();
							answers.add(row);
						}
						utils.Tracer.stop("query" + queryCount++, "queries");
					} else {
						clearTable();
						row = new ArrayList<String>();
						row.add("no answers found");
						answers.add(row);
						Tracer.err("Query was interrupted by engine.");
						try {
							compileFile(translator.Finish());
						} catch (IOException e) {
							Tracer.err(e.getMessage());
						} catch (Exception e) {
							Tracer.err(e.getMessage());

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Tracer.interrupt("query" + queryCount++, "queries");
				compileFile(xsbFile);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return new ArrayList<ArrayList<String>>();
		}
		return getData();
	}

	public void resetQueryCount() {
		queryCount = 1;
	}

	public void setFilter(String f) {
		if (!filter.equals(f)) {
			filter = f;
		}
	}

	public void setIsQueryForAll(boolean flag) {
		isQueryForAll = flag;
	}

}
