package hybrid.query.views;

import javax.swing.*;
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

import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.protege.owl.example.Metrics;

public class RulesManagerViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    
    private static final Logger log = Logger.getLogger(RulesManagerViewComponent.class);
    
    private Metrics metricsComponent;
    private JTextArea _textArea;
    private JTextArea _rulesTextArea;
    private JFileChooser _fileChooser;
	private File _ruleFile;
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(12,12));
        
        GridBagConstraints subC = new GridBagConstraints();
        subC.fill = GridBagConstraints.BOTH;
        subC.gridx = 0;
        subC.gridy = 0;
        subC.gridwidth=1;
        subC.gridheight=1;
        subC.weightx = 1;
        JScrollPane scrollPane;
        
        
        JPanel rulesPanel = new JPanel(new GridBagLayout());
        rulesPanel.setBorder(BorderFactory.createTitledBorder("Rules"));
        
        subC.gridx=0;
        subC.gridy=0;
        subC.gridwidth=4;
        subC.gridheight=1;
        subC.weighty = 3;
        _rulesTextArea = new JTextArea();
        _rulesTextArea.setLineWrap(true);
        scrollPane = new JScrollPane(_rulesTextArea);
        rulesPanel.add(scrollPane, subC);
        subC.fill = GridBagConstraints.BASELINE;//GridBagConstraints.HORIZONTAL;
        subC.gridy=1;
        subC.gridx=0;
        subC.gridwidth=1;
        subC.weighty = 0;
        subC.anchor = GridBagConstraints.EAST;
        JPanel panelButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        panelButton.setBorder(BorderFactory.createTitledBorder("asd"));
        panelButton.add(addLoadRulesButton());
        panelButton.add(addSaveRulesButton());
//        rulesPanel.add(addLoadRulesButton(),subC);
//        subC.gridx=1;
//        rulesPanel.add(addSaveRulesButton(),subC);
    	rulesPanel.add(panelButton,subC);
        add(rulesPanel, BorderLayout.CENTER);
        Rules.addListener(_rulesTextArea);
        _fileChooser = new JFileChooser();
        log.info("Example View Component initialized");
    }

	@Override
	protected void disposeOWLView() {
		metricsComponent.dispose();
		Rules.dispose();
	}

	protected JButton addLoadRulesButton(){
		JButton button = new JButton("Load");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int val = _fileChooser.showDialog(null, "Open");
				if(val==JFileChooser.APPROVE_OPTION){
//					_rules = new ArrayList<String>();
					_rulesTextArea.setText("");
					try {
						_ruleFile = _fileChooser.getSelectedFile();
						FileInputStream fstream = new FileInputStream(_ruleFile);
						DataInputStream in = new DataInputStream(fstream);
					    BufferedReader br = new BufferedReader(new InputStreamReader(in));
						String strLine;
						//Read File Line By Line
						while ((strLine = br.readLine()) != null)   {
			                if(strLine.length()>0){
			                	_rulesTextArea.append(strLine+"\n");
			                }
						}
						in.close();
						Rules.setCurrentTextArea(_rulesTextArea);
						Rules.isRulesChanged = true;
//						Rules.recollectRules(false);
						
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						_textArea.append(e1.toString()+"\n");
					}
					// Get the object of DataInputStream
					catch (IOException e1) {
						e1.printStackTrace();
						_textArea.append(e1.toString()+"\n");
					}
				    
				}
			}
		});
		return button;
	}
	protected JButton addSaveRulesButton(){
		JButton button = new JButton("Save");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(_ruleFile==null)
					_ruleFile = new File("rules.p");
				_fileChooser.setSelectedFile(_ruleFile);
				int val = _fileChooser.showSaveDialog(_fileChooser.getParent());
				if(val == JFileChooser.APPROVE_OPTION){
					_ruleFile = _fileChooser.getSelectedFile();
					try {
						BufferedWriter bw = new BufferedWriter(new FileWriter(_ruleFile));
						bw.write(_rulesTextArea.getText());
		                bw.flush();
		                bw.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
				}
			}
		});
		return button;
	}
	
	
}
