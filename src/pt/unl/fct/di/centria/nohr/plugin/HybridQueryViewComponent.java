package pt.unl.fct.di.centria.nohr.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.DefaultCaret;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import pt.unl.fct.di.centria.nohr.model.Answer;
import pt.unl.fct.di.centria.nohr.model.Config;
import pt.unl.fct.di.centria.nohr.model.Query;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.parsing.Parser;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;

public class HybridQueryViewComponent extends AbstractOWLViewComponent {
    // implements OWLModelManagerListener {

    class QueryWorker extends SwingWorker<Void, Void> {
	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Void doInBackground() {
	    try {
		if (isNeedToQuery || !hasVariables) {
		    disableValuationCheckBoxes();
		    isShowProgress = true;
		    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    final int delay = 750; // milliseconds
			    final ActionListener taskPerformer = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
				    if (isShowProgress)
					progressFrame.setVisible(true);
				}
			    };
			    new Timer(delay, taskPerformer).start();
			}
		    });
		    textField.selectAll();
		    textField.requestFocus();
		    final Query query = parser.parseQuery(textField.getText());
		    fillTable(query, nohr.queryAll(query));

		} else {
		    fillNoAnswersTable("Please check at least one valuation option!");
		    // union.logger.Logger.log("");
		    // nohr.reasoner.ontologyTranslation.Logger.log("Please check at least one valuation option!");
		    // nohr.reasoner.ontologyTranslation.Logger.log(""):w
		    ;
		}
	    } catch (final Exception e) {
		progressFrame.setVisible(false);
		e.printStackTrace();
	    }
	    return null;
	}

	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() {
	    progressFrame.setVisible(false);
	}
    }

    private static HybridKB nohr;

    private static final long serialVersionUID = -4515710047558710080L;

    private static JTextArea textArea;

    public static void clear() {
	nohr = null;
    }

    private final List<JCheckBox> checkBoxs = new ArrayList<JCheckBox>();
    private String filter;
    private boolean hasVariables;
    private boolean isNeedToQuery;
    private boolean isShowAllSolutions = true;
    private boolean isShowProgress;
    private final Parser parser = new Parser();
    // private JLabel progressLabel;
    private JFrame progressFrame;
    private JLabel progressLabel;
    private QueryWorker queryWorker;
    private JPanel settingsPanel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable table;

    private final DefaultTableCellRenderer tableHeaderRenderer = new DefaultTableCellRenderer();

    private DefaultTableModel tableModel;

    private JTextField textField;

    private void addChbListners(JCheckBox button) {
	checkBoxs.add(button);
	button.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent ae) {
		tableFilterAnswer();
	    }
	});

    }

    protected JButton addProcessButton() {
	final JButton button = new JButton("Execute");
	button.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (textField.getText().length() > 0)
		    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    queryWorker = new QueryWorker();
			    queryWorker.execute();
			}
		    });
	    }
	});

	return button;
    }

    private void addProgressFrame() {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		final JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		// progressBar.setValue(0);
		progressBar.setStringPainted(true);
		// progressBar.setString("Half way there!");

		final JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
		// progressLabel = new
		// JLabel("Rule translation process",SwingConstants.CENTER);
		// progressPanel.add(progressLabel,BorderLayout.BEFORE_FIRST_LINE);

		final GridBagConstraints c = new GridBagConstraints();
		// c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		final JPanel panel = new JPanel(new GridBagLayout());
		progressLabel = new JLabel("Processing", SwingConstants.CENTER);
		//
		progressLabel.setBorder(BorderFactory
			.createTitledBorder("Query"));
		progressLabel.setFont(new Font(progressLabel.getFont()
			.getFontName(), Font.PLAIN, progressLabel.getFont()
			.getSize() + 4));
		panel.add(progressLabel, c);
		c.gridy = 1;
		panel.add(progressBar, c);
		progressPanel.add(panel, BorderLayout.CENTER);

		progressFrame = new JFrame();
		progressFrame.setSize(300, 100);
		progressFrame.setLocationByPlatform(true);
		progressFrame.setUndecorated(true);
		progressFrame.setContentPane(progressPanel);
		progressFrame
			.setLocationRelativeTo(HybridQueryViewComponent.this);
		// progressFrame.setVisible(true);
	    }
	});

    }

    protected JTextField addQueryField() {
	textField = new JTextField();
	textField.addKeyListener(new KeyListener() {
	    @Override
	    public void keyPressed(KeyEvent e) {
		updateText(e);
	    }

	    @Override
	    public void keyReleased(KeyEvent e) {

	    }

	    @Override
	    public void keyTyped(KeyEvent e) {

	    }

	    private void updateText(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER
			&& textField.getText().length() > 0)
		    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    queryWorker = new QueryWorker();
			    queryWorker.execute();
			}
		    });
	    }
	});

	return textField;
    }

    protected JPanel addSettingsPanel() {
	final JPanel settingsPanel = new JPanel(new GridBagLayout());

	settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

	final JPanel panelTop = new JPanel(new GridBagLayout());
	panelTop.setBorder(BorderFactory.createTitledBorder("Solutions"));
	final JPanel panelBottom = new JPanel(new GridBagLayout());
	panelBottom.setBorder(BorderFactory.createTitledBorder("Valuation"));
	final JPanel panelTopBottom = new JPanel(new GridBagLayout());

	final GridBagConstraints c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridx = 1;
	c.gridy = 1;
	c.gridwidth = 1;
	c.gridheight = 1;
	c.weightx = 1;

	final JRadioButton oneChB = new JRadioButton("one", false);
	oneChB.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent ae) {
		isShowAllSolutions = false;
		if (textField.getText().length() > 0)
		    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    queryWorker = new QueryWorker();
			    queryWorker.execute();
			}
		    });
	    }
	});

	final JRadioButton allChB = new JRadioButton("all", true);
	allChB.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent ae) {
		isShowAllSolutions = true;
		if (textField.getText().length() > 0)
		    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    queryWorker = new QueryWorker();
			    queryWorker.execute();
			}
		    });
	    }
	});
	final ButtonGroup group = new ButtonGroup();
	group.add(oneChB);
	group.add(allChB);
	final JCheckBox trueChB = new JCheckBox("true", true);
	addChbListners(trueChB);
	final JCheckBox undefinedChB = new JCheckBox("undefined", true);
	addChbListners(undefinedChB);
	final JCheckBox inconsistentChB = new JCheckBox("inconsistent", true);
	addChbListners(inconsistentChB);

	panelTop.add(oneChB, c);
	c.gridy = 2;
	panelTop.add(allChB, c);

	c.gridy = 1;
	panelBottom.add(trueChB, c);
	c.gridy = 2;
	panelBottom.add(undefinedChB, c);
	c.gridy = 3;
	panelBottom.add(inconsistentChB, c);

	c.weighty = 0.1;
	c.anchor = GridBagConstraints.NORTH;
	c.fill = GridBagConstraints.BOTH;
	c.ipady = 0;
	c.gridy = 1;
	settingsPanel.add(panelTop, c);
	c.gridy = 2;
	c.ipady = 10;
	settingsPanel.add(panelBottom, c);
	c.weighty = 0.8;
	c.gridy = 3;
	settingsPanel.add(panelTopBottom, c);

	return settingsPanel;
    }

    private void clearTable(final boolean isAddEnumeration) {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		for (int i = tableModel.getRowCount() - 1; i >= 0; i--)
		    tableModel.removeRow(i);
		tableModel.setColumnCount(0);
		if (isAddEnumeration)
		    tableModel.addColumn("");
		tableModel.addColumn("valuation");
	    }
	});

    }

    private void disableValuationCheckBoxes() {
	for (final JCheckBox checkBox : checkBoxs)
	    checkBox.setEnabled(false);
    }

    @Override
    protected void disposeOWLView() {
	nohr = null;
    }

    private void enableValuationCheckBoxes() {
	final int delay = 500; // milliseconds
	final ActionListener taskPerformer = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent evt) {
		for (final JCheckBox checkBox : checkBoxs)
		    checkBox.setEnabled(true);
	    }
	};
	new Timer(delay, taskPerformer).start();

    }

    private void fillNoAnswersTable(String s) {
	clearTable(false);
	final ArrayList<String> row = new ArrayList<String>();
	s = s.length() == 0 ? "no answers found" : s;
	row.add(s);
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		tableModel.addRow(row.toArray());
	    }
	});

    }

    private void fillTable(Query query, final Collection<Answer> answers) {
	try {
	    isShowProgress = false;

	    hasVariables = !query.getVariables().isEmpty();

	    clearTable(hasVariables);

	    for (final Variable var : query.getVariables())
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
			tableModel.addColumn(var.toString());
		    }
		});
	    if (!answers.isEmpty()) {
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
			for (final Answer answer : answers) {
			    final Vector<String> row = new Vector<String>();
			    if (hasVariables)
				row.add(Integer.toString(table.getRowCount() + 1));
			    for (final Term t : answer.getValues())
				row.add(t.toString());
			    row.add(answer.getValuation().name().toLowerCase());
			    if (!hasVariables
				    || filter == null
				    || filter.length() == 0
				    || filter.contains(answer.getValuation()
					    .name().toLowerCase()))
				tableModel.addRow(row);
			    if (!isShowAllSolutions && table.getRowCount() > 0)
				break;
			}
		    }
		});
		if (hasVariables)
		    setFirstColumnWidth();
	    } else
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
			if (table.getRowCount() == 0)
			    fillNoAnswersTable("");
		    }
		});
	} catch (final Exception e) {
	    fillNoAnswersTable("");
	} finally {

	    enableValuationCheckBoxes();
	}

    }

    private String getFilter() {
	filter = "yes|no|no answers found";
	isNeedToQuery = false;
	for (final JCheckBox chb : checkBoxs)
	    if (chb.isSelected()) {
		filter += "|" + chb.getText();
		isNeedToQuery = true;
	    }
	return filter;
    }

    // @Override
    // public void handleChange(OWLModelManagerChangeEvent event) {
    // if (event
    // .isType(org.protege.editor.owl.model.event.EventType.ACTIVE_ONTOLOGY_CHANGED))
    // try {
    // nohr = new HybridKB(getOWLModelManager()
    // .getOWLOntologyManager(), getOWLModelManager()
    // .getActiveOntology(), getOWLModelManager()
    // .getReasoner());
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    @Override
    protected void initialiseOWLView() {
	setLayout(new BorderLayout(12, 12));
	final JPanel panel = new JPanel(new GridBagLayout());

	final GridBagConstraints c = new GridBagConstraints();
	final GridBagConstraints subC = new GridBagConstraints();
	c.fill = GridBagConstraints.BOTH;
	c.gridx = 0;
	c.gridwidth = 1;
	c.gridheight = 1;
	c.weightx = 1;

	subC.fill = GridBagConstraints.HORIZONTAL;
	subC.gridx = 0;
	subC.gridy = 0;
	subC.gridwidth = 1;
	subC.gridheight = 1;
	subC.weightx = 1;
	subC.ipady = 0;
	JScrollPane scrollPane;

	final JPanel queryPanel = new JPanel(new GridBagLayout());
	queryPanel.setBorder(BorderFactory.createTitledBorder("Query"));
	c.gridy = 1;
	c.weighty = 0.3;
	subC.ipady = 10;
	queryPanel.add(addQueryField(), subC);
	c.gridy = 2;
	c.weighty = 0.3;
	subC.fill = GridBagConstraints.NONE;
	subC.gridy = 1;
	subC.ipady = 0;
	subC.anchor = GridBagConstraints.WEST;
	queryPanel.add(addProcessButton(), subC);
	panel.add(queryPanel, c);
	subC.ipady = 0;
	subC.fill = GridBagConstraints.BOTH;

	final JPanel resultPanel = new JPanel(new GridBagLayout());

	final JPanel tabPanel = new JPanel(new GridBagLayout());
	final JPanel outputPanel = new JPanel(new GridBagLayout());

	final JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.setTabPlacement(SwingConstants.TOP);
	tabbedPane.setBorder(BorderFactory.createTitledBorder("Output"));
	tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
	c.gridy = 3;
	c.weighty = 3;

	subC.weighty = 1;
	textArea = new JTextArea();
	textArea.setLineWrap(true);
	textArea.setEditable(false);
	final DefaultCaret caret = (DefaultCaret) textArea.getCaret();
	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

	scrollPane = new JScrollPane(textArea);
	outputPanel.add(scrollPane, subC);

	tableModel = new DefaultTableModel();
	sorter = new TableRowSorter<DefaultTableModel>(tableModel);
	table = new JTable(tableModel);
	// table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
	table.setRowHeight(30);
	table.setRowSorter(sorter);
	table.setFillsViewportHeight(true);
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	tableHeaderRenderer.setBackground(new Color(239, 198, 46));
	final JScrollPane tableSrollPane = new JScrollPane(table);
	tabbedPane.addTab("Result", tableSrollPane);
	tabbedPane.addTab("Log", outputPanel);
	tabPanel.add(tabbedPane, subC);
	subC.gridx = 0;
	subC.gridwidth = 1;
	subC.gridheight = 1;
	subC.gridy = 0;
	subC.weightx = 0.95;
	resultPanel.add(tabPanel, subC);
	subC.anchor = GridBagConstraints.NORTHWEST;
	subC.gridx = 1;
	subC.weightx = 0.05;

	settingsPanel = addSettingsPanel();
	resultPanel.add(settingsPanel, subC);

	panel.add(resultPanel, c);

	add(panel, BorderLayout.CENTER);
	startQueryEngine();
	addProgressFrame();
	textField.requestFocus();
	textField.requestFocusInWindow();
    }

    private void setFirstColumnWidth() {
	SwingUtilities.invokeLater(new Runnable() {
	    @Override
	    public void run() {
		table.getColumnModel().getColumn(0).setMaxWidth(40);
		table.getColumnModel().getColumn(0).setResizable(false);
	    }
	});
    }

    private void startQueryEngine() {
	try {
	    nohr = new HybridKB(getOWLModelManager().getOWLOntologyManager(),
		    getOWLModelManager().getActiveOntology());
	} catch (final Exception e) {
	    textArea.append(e.getMessage() + Config.NL);
	}
    }

    /**
     * Update the row filter regular expression from the expression in the text
     * box.
     */
    private void tableFilterAnswer() {
	if (textField.getText().length() > 0)
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    queryWorker = new QueryWorker();
		    queryWorker.execute();
		}
	    });
    }
}
