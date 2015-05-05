package hybrid.query.views;

import hybrid.query.model.NoHR;
import org.protege.editor.owl.ui.OWLWorkspaceViewsTab;


public class HybridQueryTab extends OWLWorkspaceViewsTab {
    private static final long serialVersionUID = -4896884982262745722L;

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        Rules.recollectRules(true);
    }

    @Override
    public void dispose() {
//        NoHR.dispose();
    	HybridQueryViewComponent.clear();
        super.dispose();
    }

}
