package pt.unl.fct.di.novalincs.nohr.plugin.dbmapping;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.UIHelper;

import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.plugin.AbstractNoHRViewComponent;
import pt.unl.fct.di.novalincs.nohr.plugin.DBMappingViewComponent;

/**
 * An editor, showed in an dialog, that can be used to edit text containing
 * database mappings.
 *
 * @author Vedran Kasalica
 */
public class DBMappingEditor {

	private final DBMappingEditForm editor;

	private final  Vocabulary vocabulary;

	private final Container dbMappingViewComponent;

	/**
	 * 
	 *
	 */
	public DBMappingEditor(Vocabulary vocabulary, DBMappingViewComponent dbMappingViewComponent) {
		this.vocabulary = vocabulary;
		editor = new DBMappingEditForm(vocabulary);
		this.dbMappingViewComponent = dbMappingViewComponent;
	}

	public void clear() {
		editor.setMapping(null);
	}

	public void setDBMapping(DBMapping dbMapping) {
		editor.setMapping(dbMapping);
	}

	public DBMapping show() {

		// EventQueue.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (ClassNotFoundException | InstantiationException |
		// IllegalAccessException | UnsupportedLookAndFeelException ex) {
		// }
		//
		// final JButton okay = new JButton("Ok");
		// okay.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// JOptionPane pane = getOptionPane((JComponent)e.getSource());
		// pane.setValue(okay);
		// }
		// });
		// okay.setEnabled(false);
		// final JButton cancel = new JButton("Cancel");
		// cancel.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// JOptionPane pane = getOptionPane((JComponent)e.getSource());
		// pane.setValue(cancel);
		// }
		// });
		//
		// final JTextField field = new JTextField();
		// field.getDocument().addDocumentListener(new DocumentListener() {
		// protected void update() {
		// okay.setEnabled(field.getText().length() > 0);
		// }
		//
		// @Override
		// public void insertUpdate(DocumentEvent e) {
		// update();
		// }
		//
		// @Override
		// public void removeUpdate(DocumentEvent e) {
		// update();
		// }
		//
		// @Override
		// public void changedUpdate(DocumentEvent e) {
		// update();
		// }
		// });
		//
		// JOptionPane.showOptionDialog(
		// null,
		// field,
		// "Get",
		// JOptionPane.YES_NO_OPTION,
		// JOptionPane.QUESTION_MESSAGE,
		// null,
		// new Object[]{okay, cancel},
		// okay);
		// }
		// });
		
		DBMapping tmp = null;
		int ret;
		do {
			ret = JOptionPaneEx.showConfirmDialog(dbMappingViewComponent, "Database-Mapping Editor", editor,
					JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null);

			if (ret == JOptionPane.OK_OPTION)
				tmp = editor.getDBMapping();
		} while (tmp == null && ret == JOptionPane.OK_OPTION);

		if (ret == JOptionPane.OK_OPTION) {
			System.out.println("DBMappingEditor.show() - " + tmp.toString());
			return tmp;
		}

		return null;
	}

}
