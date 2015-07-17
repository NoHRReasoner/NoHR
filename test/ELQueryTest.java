import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.AbstractOntologyTranslation;
import pt.unl.fct.di.centria.nohr.reasoner.translation.ontology.Profiles;

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
	AbstractOntologyTranslation.profile = Profiles.OWL2_EL;
    }

    @Override
    public void tearDown() throws Exception {
	super.tearDown();
	AbstractOntologyTranslation.profile = null;
    }

}
