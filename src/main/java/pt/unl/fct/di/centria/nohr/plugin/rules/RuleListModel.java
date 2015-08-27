/**
 *
 */
package pt.unl.fct.di.centria.nohr.plugin.rules;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.protege.editor.core.ui.list.MListSectionHeader;
import org.protege.editor.owl.OWLEditorKit;

import pt.unl.fct.di.centria.nohr.model.Program;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.parsing.NoHRParser;
import pt.unl.fct.di.centria.nohr.parsing.ParseException;

/**
 * An {@link ListModel list model} of {@link Rule rules}.
 */
public class RuleListModel extends AbstractListModel<Object> {

	private static final MListSectionHeader HEADER = new MListSectionHeader() {

		@Override
		public boolean canAdd() {
			return true;
		}

		@Override
		public String getName() {
			return "Rules";
		}
	};

	private static final long serialVersionUID = -5766699966244129502L;

	private Program program;

	private final RuleEditor ruleEditor;

	private final List<Object> ruleItems;

	private final NoHRParser parser;

	/**
	 *
	 */

	public RuleListModel(OWLEditorKit editorKit, NoHRParser parser, Program program) {
		super();
		this.parser = parser;
		ruleEditor = new RuleEditor(editorKit, parser);
		this.program = program;
		ruleItems = new ArrayList<Object>(program.size());
		ruleItems.add(HEADER);
		for (final Rule rule : program)
			ruleItems.add(new RuleListItem(ruleItems.size() - 1, this, rule));
	}

	boolean add(Rule rule) {
		final boolean added = program.add(rule);
		if (added) {
			final int index = ruleItems.size();
			ruleItems.add(new RuleListItem(index, this, rule));
			super.fireIntervalAdded(this, index, index);
		}
		return added;
	}

	Rule edit(int index, Rule rule) {
		final Rule newRule = ruleEditor.show();
		boolean updated = false;
		if (newRule != null)
			updated = program.update(rule, newRule);
		fireContentsChanged(this, index, index);
		return updated ? newRule : null;
	}

	@Override
	public Object getElementAt(int index) {
		return ruleItems.get(index);
	}

	@Override
	public int getSize() {
		return ruleItems.size();
	}

	public void load(File file) throws FileNotFoundException, ParseException {
		final int size = program.size();
		program = parser.parseProgram(file);
		ruleItems.clear();
		ruleItems.add(HEADER);
		for (final Rule rule : program)
			ruleItems.add(new RuleListItem(ruleItems.size() - 1, this, rule));
		super.fireContentsChanged(this, 0, Math.max(program.size() - 1, size - 1));
	}

	boolean remove(int index, Rule rule) {
		final boolean removed = program.remove(rule);
		if (removed) {
			ruleItems.remove(index);
			super.fireIntervalRemoved(this, index, index);
		}
		return removed;
	}

	public void save(File file) throws IOException {
		final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (final Rule rule : program) {
			writer.write(rule.toString());
			writer.write(".");
			writer.newLine();
		}
		writer.close();
	}

}