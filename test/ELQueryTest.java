import org.junit.Test;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import other.Config;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.TranslationAlgorithm;

/**
 *
 */

/**
 * @author nunocosta
 *
 */
public class ELQueryTest extends QueryTest {

    public ELQueryTest() {
    }

    @Override
    public void setUp() throws Exception {
	super.setUp();
	Config.translationAlgorithm = TranslationAlgorithm.EL;
    }

    @Override
    public void tearDown() throws Exception {
	super.tearDown();
	Config.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
    }

}
