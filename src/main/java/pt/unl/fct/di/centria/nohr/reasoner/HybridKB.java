/*
 * 
 */
package nohr.reasoner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nohr.plugin.Rules;
import nohr.reasoner.translation.Translate;

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

import other.Utils;
import utils.Tracer;
import xsb.XSBDatabase;

import com.declarativa.interprolog.TermModel;
import com.declarativa.interprolog.util.IPException;

public class HybridKB implements OWLOntologyChangeListener {

	private static final Pattern HEADER_PATTERN = Pattern
			.compile("\\((.*?)\\)");

	private boolean isOntologyChanged;

	private static Query query;

	private static Translate translator;

	private static final Pattern VARXPATTERN = Pattern.compile("Var\\d+");

	private ArrayList<ArrayList<String>> answers = new ArrayList<ArrayList<String>>();

	private String filter = "";

	private boolean hasDisjunction;
	
	private boolean isQueryForAll = true;

	private Map<String, String> labels;

	private OWLOntologyManager om;

	private OWLOntology ontology;

	private String previousQuery = "";

	private boolean queriedForAll;

	private int queryCount;

	private XSBDatabase xsb;

	private String queryString;

	public OWLReasoner reasoner;

	private ArrayList<String> variablesList = new ArrayList<String>();

	private File xsbFile;

	public HybridKB(OWLOntology ontology) {
		try {
			this.om = OWLManager.createOWLOntologyManager();
			this.ontology = ontology;
			this.isOntologyChanged = true;
			Rules.isRulesOntologyChanged = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HybridKB(OWLOntologyManager ontologyManager, OWLOntology ontolgy,
			OWLReasoner owlReasoner) throws Exception {
		this.om = ontologyManager;
		this.ontology = ontolgy;
		this.reasoner = owlReasoner;
		this.om.addOntologyChangeListener(this);
		this.isOntologyChanged = true;
		Rules.isRulesOntologyChanged = true;
	}

	public void abolishTables() {
		xsb.abolishTables();
	}

	private void clearTable() {
		variablesList = new ArrayList<String>();
		answers = new ArrayList<ArrayList<String>>();
	}

	private void loadRulesInXSB(File file) throws IPException, Exception {
		utils.Tracer.start("xsb loading");
		xsb = new XSBDatabase();
		boolean loaded = xsb.load(file);
		if (!loaded)
			throw new IPException("unsuccessful XSB loading");
		xsb.deterministicGoal(generateDetermenisticGoal("initQuery"));
		utils.Tracer.stop("xsb loading", "loading");
	}

	public void dispose() {
		if (om != null) {
			om.removeOntologyChangeListener(this);
		}
		if (translator != null) {
			translator.clear();
		}
		if (xsb != null) {
			xsb.shutdown();
		}
		Rules.dispose();
	}

	private void getVariables(String command) {
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

	@Override
	public void ontologiesChanged(List<? extends OWLOntologyChange> changes)
			throws OWLException {
		try {
			translator = new Translate(om, ontology, reasoner);
			for (OWLOntologyChange change : changes) {
				if (change.getOntology() == ontology) {
					isOntologyChanged = true;
					Rules.dispose();
					if (HybridKB.translator != null) {
						HybridKB.translator.clear();
					}
					break;
				}
			}
		} catch (IOException | CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	private void preprocessKB() throws Exception {
		try {
			boolean hasDisjunctions = this.hasDisjunction;
			utils.Tracer.start("translator initialization");
			translator = new Translate(om, ontology, reasoner);
			utils.Tracer.stop("translator initialization", "loading");
			if (isOntologyChanged) {
				utils.Tracer.start("ontology proceeding");
				translator.proceed();
				utils.Tracer.stop("ontology proceeding", "loading");
			}
			if (Rules.isRulesOntologyChanged
					|| translator.isAnyDisjointWithStatement() != hasDisjunctions) {
				utils.Tracer.start("rules parsing");
				translator.appendRules(Rules.getRules());
				utils.Tracer.stop("rules parsing", "loading");
				Rules.isRulesOntologyChanged = false;
			}
			utils.Tracer.start("file writing");
			xsbFile = translator.Finish();
			utils.Tracer.stop("file writing", "loading");
			loadRulesInXSB(xsbFile);
			isOntologyChanged = false;
			Rules.isRulesOntologyChanged = false;

			this.hasDisjunction = translator.isAnyDisjointWithStatement();
			labels = translator.getCollectionsManager().getLabels();
			query = new Query(translator.getCollectionsManager());

			utils.Tracer.start("file writing");
			File xsbFile = translator.Finish();
			utils.Tracer.stop("file writing", "loading");
			loadRulesInXSB(xsbFile);

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


	public ArrayList<ArrayList<String>> query(String command) {
		try {
			queryString = command;
			if (isOntologyChanged || Rules.isRulesOntologyChanged)
				preprocessKB();
			if (command.endsWith(".")) {
				command = command.substring(0, command.length() - 1);
			}
			command = query.prepareQuery(command, hasDisjunction);
			// previousQuery="";

			if ((!command.equals(previousQuery) || !queriedForAll)) {
				Tracer.info("You queried: " + queryString);
				previousQuery = command;
				queriedForAll = isQueryForAll;
				getVariables(command);
				String detGoal = generateDetermenisticGoal(command);
				String subDetGoal;
				utils.Tracer.start("query" + queryCount);
				Object[] bindings = xsb.deterministicGoal(detGoal);

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
								subValue = getLabelByHash(element.getChild(j)
										.toString());
								subValue = VARXPATTERN.matcher(subValue).find() ? "all values"
										: subValue;
								row.add(subValue);
							}
							if (!hasDisjunction) {
								answers.add(row);
							} else {
								if (value.equals("true")
										|| value.equals("undefined")) {
									subDetGoal = generateDetermenisticGoal(generateSubQuery(
											Utils._dAllrule(command), element));

									Object[] subBindings = xsb
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
												|| subAnswer.equals("false")) {
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
						loadRulesInXSB(translator.Finish());
					} catch (IOException e) {
						Tracer.err(e.getMessage());
					} catch (Exception e) {
						Tracer.err(e.getMessage());

					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				Tracer.interrupt("query" + queryCount++, "queries");
				loadRulesInXSB(xsbFile);
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
