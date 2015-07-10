package pt.unl.fct.di.centria.nohr.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Rules {
    private static JTextArea _currentTextArea;

    private static HashSet<JTextArea> _listners = new HashSet<JTextArea>();

    private static ArrayList<String> _rules = new ArrayList<String>();

    public static boolean hasChanges;

    public static boolean isRulesChanged;

    public static String rulesFilePath;// =

    // "/Users/vadimivanov/Documents/University/tests/cities/cities.p";

    private static void addEventsForTextArea(final JTextArea textarea) {
	textarea.getDocument().addDocumentListener(new DocumentListener() {

	    @Override
	    public void changedUpdate(DocumentEvent arg0) {
		// System.out.println("changed");
		warn();
	    }

	    @Override
	    public void insertUpdate(DocumentEvent arg0) {
		// System.out.println("inserted");
		warn();
	    }

	    @Override
	    public void removeUpdate(DocumentEvent arg0) {
		// System.out.println("removed");
		warn();
	    }

	    private void warn() {
		isRulesChanged = true;
		hasChanges = true;
		_currentTextArea = textarea;
	    }
	});

    }

    public static void addListener(JTextArea textarea) {
	_listners.add(textarea);
	addEventsForTextArea(textarea);
	textarea.setText("");
	for (final String rule : _rules)
	    textarea.append(rule + System.lineSeparator());
    }

    public static void addRule(String rule) {
	isRulesChanged = true;
	hasChanges = true;
	_rules.add(rule);
	for (final JTextArea textArea : _listners)
	    textArea.append(rule + System.lineSeparator());

    }

    public static void addRule(String rule, JTextArea textarea) {
	_rules.add(rule);
	for (final JTextArea textArea : _listners)
	    if (textArea != textarea)
		textArea.append(rule + System.lineSeparator());
    }

    public static void deleteAllRules() {
	resetRules();
	for (final JTextArea textArea : _listners)
	    textArea.setText("");
    }

    public static void dispose() {
	for (final JTextArea textArea : _listners)
	    textArea.setText("");
	resetRules();
    }

    public static ArrayList<String> getRules() {
	recollectRules(false);
	return _rules;
    }

    private static void printRulesToTextArea() {
	for (final JTextArea textArea : _listners)
	    if (textArea != _currentTextArea) {
		textArea.setText("");
		for (final String rule : _rules)
		    textArea.append(rule + System.lineSeparator());
	    }
    }

    public static void recollectRules(boolean isRePrint) {
	if (isRulesChanged && _currentTextArea != null) {
	    isRulesChanged = false;
	    hasChanges = true;
	    final StringReader sr = new StringReader(_currentTextArea.getText());
	    final BufferedReader br = new BufferedReader(sr);
	    String nextLine = "";
	    resetRules();
	    try {
		while ((nextLine = br.readLine()) != null) {
		    nextLine = nextLine.trim();
		    if (nextLine.length() > 0)
			_rules.add(nextLine);
		}
		br.close();
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	    sr.close();
	    if (isRePrint)
		printRulesToTextArea();
	}
    }

    public static void resetRules() {
	if (!_rules.isEmpty()) {
	    isRulesChanged = true;
	    hasChanges = true;
	}
	_rules = new ArrayList<String>();
    }

    public static void setCurrentTextArea(JTextArea textarea) {
	_currentTextArea = textarea;
    }

}
