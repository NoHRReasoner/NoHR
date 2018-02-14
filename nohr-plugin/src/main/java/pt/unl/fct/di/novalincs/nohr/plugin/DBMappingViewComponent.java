package pt.unl.fct.di.novalincs.nohr.plugin;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.view.ViewComponent;

import com.igormaznitsa.prologparser.exceptions.PrologParserException;

import pt.unl.fct.di.novalincs.nohr.parsing.ParseException;
import pt.unl.fct.di.novalincs.nohr.plugin.dbmapping.DBMappingEditor;
import pt.unl.fct.di.novalincs.nohr.plugin.dbmapping.DBMappingList;
import pt.unl.fct.di.novalincs.nohr.plugin.dbmapping.DBMappingListModel;
import pt.unl.fct.di.novalincs.nohr.plugin.rules.RuleEditor;
import pt.unl.fct.di.novalincs.nohr.plugin.rules.RuleListModel;
import pt.unl.fct.di.novalincs.nohr.plugin.rules.RulesList;

/**
 * The {@link ViewComponent} where the mappings are defined.
 *
 * @author Vedran Kasalica
 */
public class DBMappingViewComponent extends AbstractNoHRViewComponent {

	private static final long serialVersionUID = 6087261708132206489L;

    private DBMappingList dbMappingList;

    private DBMappingEditor dbMappingEditor;

    @Override
    protected void disposeOWLView() {
    }

    
    public DBMappingListModel getDBMappingListModel() {
//    	trying to fetch the current DBMappingListModel if it exists
        DisposableObject<DBMappingListModel> dbMappingListModel = getOWLModelManager().get(DBMappingListModel.class);
//        create a new one otherwise
        if (dbMappingListModel == null) {
        	dbMappingListModel = new DisposableObject<DBMappingListModel>(
                    new DBMappingListModel(getOWLEditorKit(), dbMappingEditor, getDBMappingSetPersistenceManager(), getDBMappingSet()));
//        	after creating put the DBMappingListModel to be current
            getOWLModelManager().put(DBMappingListModel.class, dbMappingListModel);
        }
        return dbMappingListModel.getObject();
    }

    @Override
    public void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(10, 10));
       
        dbMappingEditor = new DBMappingEditor( getVocabulary(),this);
//        define the object that represents the tab
        final DBMappingListModel dbMappingListModel = getDBMappingListModel();
//        reset();
        dbMappingList = new DBMappingList(dbMappingEditor, dbMappingListModel);
        dbMappingList.setFont(new Font(this.getFont().getFontName(), Font.BOLD, 14));
        final JScrollPane jScrollPane = new JScrollPane(dbMappingList);
        add(jScrollPane, BorderLayout.CENTER);
        //final JPanel buttonHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final Box buttonHolder = new Box(BoxLayout.X_AXIS);

//        TODO
        final JButton openButton = new JButton(new AbstractAction("Open") {

            private static final long serialVersionUID = -2176187025244957420L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final JFileChooser fc = new JFileChooser(UIUtil.getCurrentFileDirectory());
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                final int returnVal = fc.showOpenDialog(DBMappingViewComponent.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        final File file = fc.getSelectedFile();
                        DBMappingViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        dbMappingListModel.load(file);
                    } catch (final Exception e) {
                        Messages.invalidmappingFile(DBMappingViewComponent.this, e);
                    } finally {
                    	DBMappingViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
            }
        });

//      TODO
        final JButton saveButton = new JButton(new AbstractAction("Save") {

            private static final long serialVersionUID = -2176187025244957420L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                final JFileChooser fc = new JFileChooser(UIUtil.getCurrentFileDirectory());
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                final int returnVal = fc.showSaveDialog(DBMappingViewComponent.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        final File file = fc.getSelectedFile();
                        DBMappingViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        dbMappingListModel.save(file);
                    } catch (final IOException e) {
                        Messages.unsucceccfulSave(DBMappingViewComponent.this, e);
                    } finally {
                    	DBMappingViewComponent.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }

            }
        });
        
//      TODO
        final JButton clearButton = new JButton(new AbstractAction("Clear") {

            private static final long serialVersionUID = -2176187025244957420L;

            @Override
            public void actionPerformed(ActionEvent arg0) {
                dbMappingListModel.clear();
            }
        });

//        final JCheckBox showIRIsCheckBox = new JCheckBox("Show IRIs");
//        showIRIsCheckBox.setSelected(showIRI);
//
//        showIRIsCheckBox.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//            	dbMappingList.setShowIRIs(showIRIsCheckBox.isSelected());
//            }
//        });

        buttonHolder.add(Box.createHorizontalStrut(5));
        buttonHolder.add(openButton);
        buttonHolder.add(Box.createHorizontalStrut(5));
        buttonHolder.add(saveButton);
        buttonHolder.add(Box.createHorizontalStrut(5));
        buttonHolder.add(clearButton);
        buttonHolder.add(Box.createHorizontalStrut(5));
        buttonHolder.add(Box.createHorizontalStrut(5));
        add(buttonHolder, BorderLayout.SOUTH);

    }
}
