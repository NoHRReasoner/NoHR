package pt.unl.fct.di.novalincs.nohr.translation.dl;

import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorImplementor;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

public class OntologyTranslator extends OntologyTranslatorImplementor {

    private DLOntologyNormalization normalizedOntology;
    private final OriginalAxiomTranslator axiomTranslator;
    private final DoubledAxiomTranslator doubledAxiomTranslator;

    public OntologyTranslator(OWLOntology ontology, Vocabulary vocabulary, DeductiveDatabase dedutiveDatabase) throws UnsupportedAxiomsException {
        super(ontology, vocabulary, dedutiveDatabase);

        axiomTranslator = new OriginalAxiomTranslator(vocabulary);
        doubledAxiomTranslator = new DoubledAxiomTranslator(vocabulary);

        RuntimesLogger.start("[NOHR DL (HermiT)] ontology normalization");

        normalizedOntology = new HermiTDLOntologyNormalization(ontology, vocabulary);

        RuntimesLogger.stop("[NOHR DL (HermiT)] ontology normalization", "loading");
    }

    @Override
    public void updateTranslation() throws UnsupportedAxiomsException {
        prepareUpdate();
        translation.clear();

        RuntimesLogger.start("[NOHR DL (HermiT)] ontology translation");

        translate(axiomTranslator);

        if (normalizedOntology.hasDisjunctions()) {
            translate(doubledAxiomTranslator);
        }

        RuntimesLogger.stop("[NOHR DL (HermiT)] ontology translation", "loading");
    }

    @Override
    public Profile getProfile() {
        return Profile.NOHR_DL;
    }

    @Override
    public boolean hasDisjunctions() {
        return normalizedOntology.hasDisjunctions();
    }

    private void prepareUpdate() throws UnsupportedAxiomsException {
        normalizedOntology = new HermiTDLOntologyNormalization(ontology, vocabulary);
    }

    private void translate(AxiomTranslator axiomTranslator) {
        for (final OWLSubPropertyChainOfAxiom axiom : normalizedOntology.chainSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLClassAssertionAxiom axiom : normalizedOntology.conceptAssertions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubClassOfAxiom axiom : normalizedOntology.conceptSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLDataPropertyAssertionAxiom axiom : normalizedOntology.dataAssertions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubDataPropertyOfAxiom axiom : normalizedOntology.dataSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLObjectPropertyAssertionAxiom axiom : normalizedOntology.roleAssertions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

        for (final OWLSubObjectPropertyOfAxiom axiom : normalizedOntology.roleSubsumptions()) {
            translation.addAll(axiomTranslator.translate(axiom));
        }

//        for (final OWLInverseObjectPropertiesAxiom axiom : normalizedOntology.inverseRoles()) {
//            for (OWLSubObjectPropertyOfAxiom i : axiom.asSubObjectPropertyOfAxioms()) {
//                translation.addAll(axiomTranslator.translate(i));
//            }
//        }

//        for (final OWLSymmetricObjectPropertyAxiom axiom : ontology.getAxioms(AxiomType.SYMMETRIC_OBJECT_PROPERTY)) {
//            for (OWLSubObjectPropertyOfAxiom i : axiom.asSubPropertyAxioms()) {
//                translation.addAll(axiomTranslator.translate(i));
//            }
//        }

//        for (final OWLObjectPropertyRangeAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_RANGE)) {
//            //translation.addAll(axiomTranslator.translate(axiom.asOWLSubClassOfAxiom()));
//            final OWLObjectPropertyExpression p = axiom.getProperty();
//             final OWLClassExpression c = axiom.getRange();
// 
//            translation.addAll(axiomTranslator.translate(DLUtils.subsumption(ontology, DLUtils.some(ontology, p.getInverseProperty()), c)));
//        }

//        for (final OWLObjectPropertyDomainAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_DOMAIN)) {
//            translation.addAll(axiomTranslator.translate(axiom.asOWLSubClassOfAxiom()));
//        }
//
//        for (final OWLDisjointClassesAxiom axiom : normalizedOntology.conceptDisjunctions()) {
//            translation.addAll(axiomTranslator.translate(axiom));
//        }
//
//        for (final OWLDisjointObjectPropertiesAxiom axiom : normalizedOntology.roleDisjunctions()) {
//            translation.addAll(axiomTranslator.translate(axiom));
//        }
//
//        for (final OWLDisjointDataPropertiesAxiom axiom : normalizedOntology.dataDisjunctions()) {
//            translation.addAll(axiomTranslator.translate(axiom));
//        }
//
    }
}
