package pt.unl.fct.di.novalincs.nohr.plugin;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.view.ViewComponent;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.novalincs.nohr.plugin.rules.RuleEditor;
import pt.unl.fct.di.novalincs.nohr.plugin.rules.RuleListModel;
import pt.unl.fct.di.novalincs.nohr.plugin.rules.RulesList;

/**
 * The {@link ViewComponent} where the rules are edited.
 *
 * @author Nuno Costa
 */
public class RulesViewComponent extends AbstractNoHRViewComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 6087261708132206489L;

	private RulesList ruleList;

	private RuleEditor ruleEditor;

	@Override
	protected void disposeOWLView() {
	}

	public RuleListModel getRuleListModel() {
		DisposableObject<RuleListModel> ruleListModel = getOWLModelManager().get(RuleListModel.class);
		if (ruleListModel == null) {
			ruleListModel = new DisposableObject<RuleListModel>(
					new RuleListModel(getOWLEditorKit(), ruleEditor, getProgramPresistenceManager(), getProgram()));
			getOWLModelManager().put(RuleListModel.class, ruleListModel);
		}
		return ruleListModel.getObject();
	}

	@Override
	public void initialiseOWLView() throws Exception {
		setLayout(new BorderLayout());
		ruleEditor = new RuleEditor(getOWLEditorKit(), getParser());
		final RuleListModel ruleListModel = getRuleListModel();
		reset();
		ruleList = new RulesList(ruleEditor, ruleListModel);
		final JScrollPane jScrollPane = new JScrollPane(ruleList);
		add(jScrollPane, BorderLayout.CENTER);
		final JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JButton openButton = new JButton(new AbstractAction("Open") {

			private static final long serialVersionUID = -2176187025244957420L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				final int returnVal = fc.showOpenDialog(RulesViewComponent.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					try {
						final File file = fc.getSelectedFile();
						RulesViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						ruleListModel.load(file);
					} catch (final PrologParserException e) {
						Messages.invalidExpression(RulesViewComponent.this, e);
					} catch (final IOException e) {
					} finally {
						RulesViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
			}
		});

		final JButton saveButton = new JButton(new AbstractAction("Save") {

			private static final long serialVersionUID = -2176187025244957420L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				final int returnVal = fc.showSaveDialog(RulesViewComponent.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					try {
						final File file = fc.getSelectedFile();
						RulesViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						ruleListModel.save(file);
					} catch (final IOException e) {
						Messages.unsucceccfulSave(RulesViewComponent.this, e);
					} finally {
						RulesViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}

			}
		});
		final JButton clearButton = new JButton(new AbstractAction("Clear") {

			private static final long serialVersionUID = -2176187025244957420L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ruleListModel.clear();
			}
		});
		buttonHolder.add(openButton);
		buttonHolder.add(saveButton);
		buttonHolder.add(clearButton);
		add(buttonHolder, BorderLayout.SOUTH);

	}

}
