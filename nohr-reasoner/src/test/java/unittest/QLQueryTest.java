/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unittest;

import com.declarativa.interprolog.util.IPException;
import java.io.IOException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;

public class QLQueryTest extends AbstractQLQueryTest {

    public QLQueryTest() throws OWLOntologyCreationException, OWLOntologyStorageException, OWLProfilesViolationsException, IOException, CloneNotSupportedException, UnsupportedAxiomsException, IPException, PrologEngineCreationException {
        super();
    }

}
