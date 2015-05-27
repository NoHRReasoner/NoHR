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
import java.util.List;

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

import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

import pt.unl.fct.di.centria.nohr.model.Config;
import pt.unl.fct.di.centria.nohr.reasoner.HybridKB;
import union.logger.Observer;
import union.logger.UnionLogger;

public class HybridQueryViewComponent extends AbstractOWLViewComponent
implements OWLModelManagerListener {
    class QueryWorker extends SwingWorker<Void, Void> {
	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Void doInBackground() {
	    try {
		nohr.setFilter(getFilter());
		if (isNeedToQuery || !isAddEnumeration) {
		    disableValuationCheckBoxes();
		    isShowProgress = true;
		    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    int delay = 750; // milliseconds
			    ActionListener taskPerformer = new ActionListener() {
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
		    fillTable(nohr.query(textField.getText()));

		} else {
		    fillNoAnswersTable("Please check at least one valuation option!");
		    // union.logger.Logger.log("");
		    // nohr.reasoner.ontologyTranslation.Logger.log("Please check at least one valuation option!");
		    // nohr.reasoner.ontologyTranslation.Logger.log(""):w
		    ;
		}
	    } catch (Exception e) {
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

    public class ViewLogger implements Observer {
	@Override
	public void update(String log) {
	    textArea.append(log + Config.NL);
	}

    }

    public static void clear() {
	nohr.dispose();
    }

    private static final long serialVersionUID = -4515710047558710080L;
    private static HybridKB nohr;
    private static JTextArea textArea;
    private JTextField textField;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable table;
    private DefaultTableModel tableModel;
    private DefaultTableCellRenderer tableHeaderRenderer = new DefaultTableCellRenderer();
    private JPanel settingsPanel;
    private List<JCheckBox> checkBoxs = new ArrayList<JCheckBox>();
    // private JLabel progressLabel;
    private JFrame progressFrame;
    private JLabel progressLabel;
    private QueryWorker queryWorker;
    private boolean isShowAllSolutions = true;
    private boolean isAddEnumeration;

    private boolean isShowProgress;

    private boolean isNeedToQuery;

    private String filter;

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
	JButton button = new JButton("Execute");
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
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setIndeterminate(true);
		// progressBar.setValue(0);
		progressBar.setStringPainted(true);
		// progressBar.setString("Half way there!");

		JPanel progressPanel = new JPanel(new BorderLayout());
		progressPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
		// progressLabel = new
		// JLabel("Rule translation process",SwingConstants.CENTER);
		// progressPanel.add(progressLabel,BorderLayout.BEFORE_FIRST_LINE);

		GridBagConstraints c = new GridBagConstraints();
		// c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 1;
		c.fill = GridBagConstraints.BOTH;

		JPanel panel = new JPanel(new GridBagLayout());
		progressLabel = new JLabel("Processing", SwingConstants.CENTER);
		// progressLabel.setBorder(BorderFactory.createTitledBorder("Query"));
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
	JPanel settingsPanel = new JPanel(new GridBagLayout());

	settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

	JPanel panelTop = new JPanel(new GridBagLayout());
	panelTop.setBorder(BorderFactory.createTitledBorder("Solutions"));
	JPanel panelBottom = new JPanel(new GridBagLayout());
	panelBottom.setBorder(BorderFactory.createTitledBorder("Valuation"));
	JPanel panelTopBottom = new JPanel(new GridBagLayout());

	GridBagConstraints c = new GridBagConstraints();
	c.anchor = GridBagConstraints.NORTHWEST;
	c.gridx = 1;
	c.gridy = 1;
	c.gridwidth = 1;
	c.gridheight = 1;
	c.weightx = 1;

	JRadioButton oneChB = new JRadioButton("one", false);
	oneChB.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent ae) {
		isShowAllSolutions = false;
		nohr.setIsQueryForAll(false);
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

	JRadioButton allChB = new JRadioButton("all", true);
	allChB.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent ae) {
		isShowAllSolutions = true;
		nohr.setIsQueryForAll(true);
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
	ButtonGroup group = new ButtonGroup();
	group.add(oneChB);
	group.add(allChB);
	JCheckBox trueChB = new JCheckBox("true", true);
	addChbListners(trueChB);
	JCheckBox undefinedChB = new JCheckBox("undefined", true);
	addChbListners(undefinedChB);
	JCheckBox inconsistentChB = new JCheckBox("inconsistent", true);
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
	for (JCheckBox checkBox : checkBoxs)
	    checkBox.setEnabled(false);
    }

    @Override
    protected void disposeOWLView() {
	if (nohr != null)
	    nohr.dispose();
    }

    private void enableValuationCheckBoxes() {
	int delay = 500; // milliseconds
	ActionListener taskPerformer = new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent evt) {
		for (JCheckBox checkBox : checkBoxs)
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

    private void fillTable(final ArrayList<ArrayList<String>> data) {
	try {
	    isShowProgress = false;
	    isAddEnumeration = data.get(0).size() > 0;// || !(data.size() == 2
	    // &&
	    // (data.get(1).get(0).equals("no answers found")
	    // ||
	    // data.get(1).get(0).equals("no")));

	    clearTable(isAddEnumeration);
	    for (final String s : data.get(0))
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
			tableModel.addColumn(s);
		    }
		});
	    if (data.size() > 1) {
		SwingUtilities.invokeLater(new Runnable() {
		    @Override
		    public void run() {
			for (int i = 1; i < data.size(); i++) {
			    final ArrayList<String> row = new ArrayList<String>();
			    if (isAddEnumeration)
				row.add(Integer.toString(table.getRowCount() + 1));
			    row.addAll(data.get(i));
			    if (!isAddEnumeration || filter == null
				    || filter.length() == 0
				    || filter.contains(data.get(i).get(0)))
				tableModel.addRow(row.toArray());
			    if (!isShowAllSolutions && table.getRowCount() > 0)
				break;
			}
		    }
		});
		if (isAddEnumeration)
		    setFirstColumnWidth();
	    }
	    SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    if (table.getRowCount() == 0)
			fillNoAnswersTable("");
		}
	    });
	} catch (Exception e) {
	    fillNoAnswersTable("");
	} finally {

	    enableValuationCheckBoxes();
	}

    }

    private String getFilter() {
	filter = "yes|no|no answers found";
	isNeedToQuery = false;
	for (JCheckBox chb : checkBoxs)
	    if (chb.isSelected()) {
		filter += "|" + chb.getText();
		isNeedToQuery = true;
	    }
	return filter;
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
	if (event
		.isType(org.protege.editor.owl.model.event.EventType.ACTIVE_ONTOLOGY_CHANGED))
	    try {
		nohr = new HybridKB(getOWLModelManager()
			.getOWLOntologyManager(), getOWLModelManager()
			.getActiveOntology(), getOWLModelManager()
			.getReasoner());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
    }

    @Override
    protected void initialiseOWLView() {
	setLayout(new BorderLayout(12, 12));
	JPanel panel = new JPanel(new GridBagLayout());

	GridBagConstraints c = new GridBagConstraints();
	GridBagConstraints subC = new GridBagConstraints();
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

	JPanel queryPanel = new JPanel(new GridBagLayout());
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

	JPanel resultPanel = new JPanel(new GridBagLayout());

	JPanel tabPanel = new JPanel(new GridBagLayout());
	JPanel outputPanel = new JPanel(new GridBagLayout());

	JTabbedPane tabbedPane = new JTabbedPane();
	tabbedPane.setTabPlacement(SwingConstants.TOP);
	tabbedPane.setBorder(BorderFactory.createTitledBorder("Output"));
	tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
	c.gridy = 3;
	c.weighty = 3;

	subC.weighty = 1;
	textArea = new JTextArea();
	textArea.setLineWrap(true);
	textArea.setEditable(false);
	DefaultCaret caret = (DefaultCaret) textArea.getCaret();
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
	JScrollPane tableSrollPane = new JScrollPane(table);
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
	ViewLogger logger = new ViewLogger();
	UnionLogger.LOGGER.removeAllObservers();
	UnionLogger.LOGGER.registerObserver(logger);
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
		    getOWLModelManager().getActiveOntology(),
		    getOWLModelManager().getReasoner());
	} catch (Exception e) {
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
