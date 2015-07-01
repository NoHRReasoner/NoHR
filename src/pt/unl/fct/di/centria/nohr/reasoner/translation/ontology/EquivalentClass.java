package pt.unl.fct.di.centria.nohr.reasoner.translation.ontology;

import java.util.ArrayList;
import java.util.List;

//TODO remove

/**
 * The Class EquivalentClass.
 */
public class EquivalentClass {

    /**
     * The Class EquivalentRules.
     */
    public class EquivalentRules {

	/** The _variable name. */
	protected String _variableName = "X";

	/** The iterator. */
	public int iterator;

	/** The local iterator. */
	public int localIterator;

	/** The name. */
	public String name;

	/** The ontology type. */
	public OntologyType ontologyType;

	/**
	 * Instantiates a new equivalent rules.
	 *
	 * @param _name
	 *            the _name
	 * @param _localIterator
	 *            the _local iterator
	 * @param _iterator
	 *            the _iterator
	 * @param _type
	 *            the _type
	 */
	public EquivalentRules(String _name, int _localIterator, int _iterator,
		OntologyType _type) {
	    name = _name;
	    localIterator = _localIterator;
	    iterator = _iterator;
	    ontologyType = _type;
	}

	/**
	 * Gets the arguments.
	 *
	 * @return the arguments
	 */
	public String getArguments() {
	    switch (ontologyType) {
	    case ONTOLOGY:
		return "(" + _variableName + iterator + ")";
	    case RULE:
		return "(" + _variableName + localIterator + ", "
			+ _variableName + iterator + ")";
	    default:
		return "";
	    }
	}

    }

    /**
     * The Enum OntologyType.
     */
    public enum OntologyType {

	/** The ontology. */
	ONTOLOGY,

	/** The rule. */
	RULE
    }

    /** The _iterator. */
    protected int _iterator;

    /** The _rule. */
    protected String _rule;

    /** The list class names. */
    protected List<String> listClassNames = new ArrayList<String>();

    /** The list rule names. */
    protected List<String> listRuleNames = new ArrayList<String>();

    /** The rules list. */
    protected List<EquivalentRules> rulesList = new ArrayList<EquivalentRules>();

    /**
     * Instantiates a new equivalent class.
     *
     * @param iterator
     *            the iterator
     */
    public EquivalentClass(int iterator) {
	_iterator = iterator;
	_rule = "";

    }

    /**
     * Adds the rule.
     *
     * @param name
     *            the name
     * @param localIterator
     *            the local iterator
     * @param iterator
     *            the iterator
     * @param type
     *            the type
     */
    public void addRule(String name, int localIterator, int iterator,
	    OntologyType type) {
	rulesList.add(new EquivalentRules(name, localIterator, iterator, type));
    }

    /**
     * Gets the doubled rules.
     *
     * @return the doubled rules
     */
    public String getDoubledRules() {
	_rule = "";
	if (rulesList.size() > 0) {
	    for (final EquivalentRules rule : rulesList)
		_rule += "'d" + rule.name + "'" + rule.getArguments() + ", ";
	    _rule = _rule.substring(0, _rule.length() - 2);
	}
	return _rule;
    }

    public String getFinalDoubledRule() {
	return getFinalRule("d");
    }

    /**
     * @return final rule
     */
    public String getFinalRule() {
	return getFinalRule("a");
    }

    /**
     * Gets the final rule.
     * 
     * @param prefix
     * @return the final rule
     */
    public String getFinalRule(String prefix) {
	_rule = "";
	if (rulesList.size() > 0) {
	    for (final EquivalentRules rule : rulesList)
		_rule += "'" + prefix + rule.name + "'" + rule.getArguments()
			+ ", ";
	    _rule = _rule.substring(0, _rule.length() - 2) + ".";
	}
	return _rule;
    }

    /**
     * Gets the list of rules.
     *
     * @return the list of rules
     */
    public List<EquivalentRules> getListOfRules() {
	return rulesList;
    }

    /**
     * Gets the neg rules.
     *
     * @return the neg rules
     */
    public List<String> getNegRules() {
	final List<String> result = new ArrayList<String>();
	String _rule = "";
	EquivalentRules r;
	for (int i = 0; i < rulesList.size(); i++) {
	    r = rulesList.get(i);
	    _rule = "'n" + r.name + "'" + r.getArguments() + " :- ";
	    for (int j = 0; j < rulesList.size(); j++)
		if (j != i) {
		    r = rulesList.get(j);
		    _rule += "'a" + r.name + "'" + r.getArguments() + ", ";
		}
	    switch (r.ontologyType) {
	    case ONTOLOGY: {
		listClassNames.add(r.name);
		break;
	    }
	    case RULE: {
		listRuleNames.add(r.name);
		break;
	    }
	    }
	    _rule = _rule.substring(0, _rule.length() - 2) + ".";
	    result.add(_rule);
	}
	return result;
    }

    /**
     * Gets the neg rules head for tabling.
     *
     * @return the neg rules head for tabling
     */
    public List<String> getNegRulesHeadForTabling() {
	final List<String> result = new ArrayList<String>();
	String _rule = "";
	EquivalentRules r;
	for (int i = 0; i < rulesList.size(); i++) {
	    r = rulesList.get(i);
	    _rule = "'n" + r.name + "'/";
	    switch (r.ontologyType) {
	    case ONTOLOGY: {
		_rule += "1";
		break;
	    }
	    case RULE: {
		_rule += "2";
		break;
	    }
	    }
	    result.add(_rule);
	}
	return result;
    }

    /**
     * Gets the variable iterator.
     *
     * @return the variable iterator
     */
    public int getVariableIterator() {
	return _iterator;
    }

    /**
     * Increment iterator.
     *
     * @return the int
     */
    public int incrementIterator() {
	return ++_iterator;
    }

    /**
     * Update class.
     *
     * @param equivalentClass
     *            the equivalent class
     */
    public void updateClass(EquivalentClass equivalentClass) {
	_iterator = equivalentClass.getVariableIterator();
	updateRule(equivalentClass.getListOfRules());
    }

    /**
     * Update rule.
     *
     * @param rules
     *            the rules
     */
    public void updateRule(List<EquivalentRules> rules) {
	for (final EquivalentRules rule : rules)
	    rulesList.add(rule);
    }
}
