package hybrid.query.views;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
//import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
//import org.protege.owl.example.Metrics;


public class RulesViewComponent extends AbstractOWLViewComponent {
    private static final long serialVersionUID = -4515710047558710080L;
    
//    private static final Logger log = Logger.getLogger(RulesViewComponent.class);
    
//    private Metrics metricsComponent;
    private JTextArea _textArea;
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout(12,12));
        //add(metricsComponent, BorderLayout.BEFORE_FIRST_LINE);
        
        _textArea = new JTextArea();
        JScrollPane jScrollPane = new JScrollPane(_textArea);
        setBorder(new EmptyBorder(0, 10, 0, 10) );
        Rules.addListener(_textArea);
        add(jScrollPane, BorderLayout.CENTER);
    }
	@Override
	protected void disposeOWLView() {
		// TODO Auto-generated method stub
		
	}
	
}
