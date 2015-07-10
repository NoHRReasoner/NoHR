package pt.unl.fct.di.centria.nohr.plugin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

//import org.apache.log4j.Logger;
//import org.protege.owl.example.Metrics;

public class RulesManagerViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;

    // private static final Logger log =
    // Logger.getLogger(RulesManagerViewComponent.class);

    private JFileChooser _fileChooser;
    private File _ruleFile;
    private JTextArea _rulesTextArea;
    // private Metrics metricsComponent;
    private JTextArea _textArea;

    protected JButton addLoadRulesButton() {
	final JButton button = new JButton("Load");
	button.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (Rules.rulesFilePath != null
			&& Rules.rulesFilePath.length() > 0) {
		    _ruleFile = new File(Rules.rulesFilePath);
		    _fileChooser.setSelectedFile(_ruleFile);
		}
		final int val = _fileChooser.showDialog(null, "Open");
		if (val == JFileChooser.APPROVE_OPTION) {
		    _rulesTextArea.setText("");
		    try {
			_ruleFile = _fileChooser.getSelectedFile();
			Rules.rulesFilePath = _ruleFile.getAbsolutePath();
			final FileInputStream fstream = new FileInputStream(
				_ruleFile);
			final DataInputStream in = new DataInputStream(fstream);
			final BufferedReader br = new BufferedReader(
				new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null)
			    if (strLine.length() > 0)
				_rulesTextArea.append(strLine + "\n");
			in.close();
			Rules.setCurrentTextArea(_rulesTextArea);
			Rules.isRulesChanged = true;

		    } catch (final FileNotFoundException e1) {
			e1.printStackTrace();
			_textArea.append(e1.toString() + "\n");
		    } catch (final IOException e1) {
			e1.printStackTrace();
			_textArea.append(e1.toString() + "\n");
		    }

		}
	    }
	});
	return button;
    }

    protected JButton addSaveRulesButton() {
	final JButton button = new JButton("Save");
	button.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		if (_ruleFile == null)
		    if (Rules.rulesFilePath != null
			    && Rules.rulesFilePath.length() > 0)
			_ruleFile = new File(Rules.rulesFilePath);
		    else
			_ruleFile = new File("rules.p");
		_fileChooser.setSelectedFile(_ruleFile);
		final int val = _fileChooser.showSaveDialog(_fileChooser
			.getParent());
		if (val == JFileChooser.APPROVE_OPTION) {
		    _ruleFile = _fileChooser.getSelectedFile();
		    Rules.rulesFilePath = _ruleFile.getAbsolutePath();
		    try {
			final BufferedWriter bw = new BufferedWriter(
				new FileWriter(_ruleFile));
			bw.write(_rulesTextArea.getText());
			bw.flush();
			bw.close();
		    } catch (final IOException e1) {
			e1.printStackTrace();
		    }

		}
	    }
	});
	return button;
    }

    @Override
    protected void disposeOWLView() {
	Rules.dispose();
    }

    @Override
    protected void initialiseOWLView() throws Exception {
	setLayout(new BorderLayout(12, 12));

	final GridBagConstraints subC = new GridBagConstraints();
	subC.fill = GridBagConstraints.BOTH;
	subC.gridx = 0;
	subC.gridy = 0;
	subC.gridwidth = 1;
	subC.gridheight = 1;
	subC.weightx = 1;
	JScrollPane scrollPane;

	final JPanel rulesPanel = new JPanel(new GridBagLayout());
	rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));

	subC.gridx = 0;
	subC.gridy = 0;
	subC.gridwidth = 4;
	subC.gridheight = 1;
	subC.weighty = 3;
	_rulesTextArea = new JTextArea();
	_rulesTextArea.setLineWrap(true);
	scrollPane = new JScrollPane(_rulesTextArea);
	rulesPanel.add(scrollPane, subC);
	subC.fill = GridBagConstraints.BASELINE;// GridBagConstraints.HORIZONTAL;
	subC.gridy = 1;
	subC.gridx = 0;
	subC.gridwidth = 1;
	subC.weighty = 0;
	subC.anchor = GridBagConstraints.EAST;
	final JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
	panelButton.add(addLoadRulesButton());
	panelButton.add(addSaveRulesButton());
	rulesPanel.add(panelButton, subC);
	add(rulesPanel, BorderLayout.CENTER);
	Rules.addListener(_rulesTextArea);
	_fileChooser = new JFileChooser();
    }

}
