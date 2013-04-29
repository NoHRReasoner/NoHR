package hybrid.query.views;

import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;

public class WorkspaceTabQuery extends OWLWorkspaceViewsTab {
    private static final long serialVersionUID = -4896884982262745722L;

    @Override
    public void setVisible(boolean aFlag) {
    	super.setVisible(aFlag);
//    	Rules.addRule("openedTab" + getClass().toString()+Config.nl);
    	Rules.recollectRules(true);
    }
}
