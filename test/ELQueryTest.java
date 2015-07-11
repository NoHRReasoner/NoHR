import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslator;
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
	AbstractOntologyTranslator.translationAlgorithm = TranslationAlgorithm.EL;
    }

    @Override
    public void tearDown() throws Exception {
	super.tearDown();
	AbstractOntologyTranslator.translationAlgorithm = TranslationAlgorithm.DL_LITE_R;
    }

}
