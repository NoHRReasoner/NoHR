/*
 *
 */
package pt.unl.fct.di.novalincs.nohr.hybridkb;

/*
 * #%L
 * nohr-reasoner
 * %%
 * Copyright (C) 2014 - 2015 NOVA Laboratory of Computer Science and Informatics (NOVA LINCS)
 * %%
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * #L%
 */
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.declarativa.interprolog.util.IPException;
import com.declarativa.interprolog.util.IPPrologError;

import pt.unl.fct.di.novalincs.nohr.deductivedb.DatabaseDBMappings;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DatabaseProgram;
import pt.unl.fct.di.novalincs.nohr.deductivedb.DeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.deductivedb.XSBDeductiveDatabase;
import pt.unl.fct.di.novalincs.nohr.model.Answer;
import pt.unl.fct.di.novalincs.nohr.model.Constant;
import pt.unl.fct.di.novalincs.nohr.model.DBMapping;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingImpl;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingsSetChangeListener;
import pt.unl.fct.di.novalincs.nohr.model.Model;
import pt.unl.fct.di.novalincs.nohr.model.Predicate;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.ProgramChangeListener;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.ModelVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateType;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.PredicateTypeVisitor;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.VocabularyChangeListener;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslator;
import pt.unl.fct.di.novalincs.nohr.translation.OntologyTranslatorFactory;
import pt.unl.fct.di.novalincs.nohr.translation.Profile;
import pt.unl.fct.di.novalincs.runtimeslogger.RuntimesLogger;

/**
 * Implementation of {@link HybridKB} according to {@link <a>A Correct EL Oracle
 * for NoHR (Technical Report)</a>} and
 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
 * Step for NoHR: OWL 2 QL</a>}.
 *
 * @author Nuno Costa
 */
public class NoHRHybridKB implements HybridKB {

    /**
     * The <i>ontology</i> component of this {@link HybridKB}
     */
    private final OWLOntology ontology;

    /**
     * The <i>program</i> component of this {@link HybridKB}
     */
    private final Program program;
    
    /**
    * The <i>database mappings</i> component of this {@link HybridKB}
    */
   private final DBMappingSet dbMappings;

    /**
     * The {@link Vocabulary} that this {@link HybridKB} applies.
     */
    private final Vocabulary vocabulary;

    /**
     * The underlying {@link DeductiveDatabase}, where the <i>ontology</i>
     * translation and the <i>program</i> rules (and double rules, when
     * necessary) are loaded for querying
     */
    private final DeductiveDatabase deductiveDatabase;

    /**
     * The underlying {@link OntologyTranslator}, that translates the
     * <i>ontology</i> component to rules.
     */
    private OntologyTranslator ontologyTranslator;

    private final OntologyTranslatorFactory ontologyTranslatorFactory;

    /**
     * The underlying {@link QueryProcessor} that mediates the queries to the
     * underlying {@link DeductiveDatabase}.
     */
    private final QueryProcessor queryProcessor;

    
    /**
     * The {@link DatabaseDBMappings} that contains the database mappings of the
     * <i>dbMappings</i> component.
     */
    private final DatabaseDBMappings databaseMappings;
    
    
    /**
     * The {@link DatabaseProgram} that contains the doubled (or only the
     * original ones, if the ontology doesn't have disjunctions) rules of the
     * <i>program</i> component.
     */
    private final DatabaseProgram doubledProgram;

    /**
     * Whether the ontology had disjunctions at last call to
     * {@link #preprocess()}.
     */
    private boolean hadDisjunctions;

    /**
     * Whether the ontology has changed since the last call to
     * {@link #preprocess()}.
     */
    private boolean hasOntologyChanges;

    /**
     * Whether the program has changed since the last call to
     * {@link #preprocess()}.
     */
    private boolean hasProgramChanges;

    /**
     * The {@link OWLOntologyChangeListener} that tracks the
     * {@link OWLOntology ontology} changes.
     */
    private final OWLOntologyChangeListener ontologyChangeListener;

    /**
     * The {@link ProgramChangeListener} that will track the tracks the
     * {@link Program} changes.
     */
    private final ProgramChangeListener programChangeListener;
    
    private final DBMappingsSetChangeListener dbMappingsSetChangeListener;

    private final VocabularyChangeListener vocabularyChangeListener;

    private final NoHRHybridKBConfiguration configuration;

    private final Profile profile;

    /**
     * Constructs a {@link NoHRHybridKB} from a given
     * {@link OWLOntology ontology} and {@link Program program}.
     *
     * @param configuration
     * @param binDirectory the directory where the Prolog system to use as
     * underlying Prolog engine is located.
     * @param ontology the <i>ontology</i> component of this {@link HybridKB}.
     * @param profile the {@link Profile OWL profile} that will be considered
     * during the ontology translation. That will determine which the
     * translation - {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>} or
	 *            {@link <a href= "http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} will be applied. If none is specified the
     * preferred one will be applied. Whenever the ontology isn't in the
     * specified profile, if some is specified, an
     * {@link OWLProfilesViolationsException} will be thrown.
     * @throws OWLProfilesViolationsException if {@code profile != null} and
     * {@code ontology} isn't in the profile {@code profile}; or
     * {@code profile == null} and the {@code ontology} isn't in any supported
     * profile.
     * @throws UnsupportedAxiomsException if {@code ontology} has some profile
     * of an unsupported type.
     * @throws PrologEngineCreationException if there was some problem during
     * the creation of the underlying Prolog engine.
     */
    public NoHRHybridKB(final NoHRHybridKBConfiguration configuration,
            final OWLOntology ontology,
            Profile profile)
            throws OWLProfilesViolationsException,
            IPException,
            UnsupportedAxiomsException,
            PrologEngineCreationException {
        this(configuration, ontology, Model.program(), Model.dbMappingSet(), null, profile);
    }

    /**
     * Constructs a {@link NoHRHybridKB} from a given
     * {@link OWLOntology ontology} and {@link Program program}.
     *
     * @param configuration
     * @param binDirectory the directory where the Prolog system to use as
     * underlying Prolog engine is located.
     * @param ontology the <i>ontology</i> component of this {@link HybridKB}.
     * @param program the <i>program</i> component of this {@link HybridKB}.
     * @param dbMappings the <i>database mappings</i> component of this {@link HybridKB}
     * @throws OWLProfilesViolationsException if {@code profile != null} and
     * {@code ontology} isn't in the profile {@code profile}; or
     * {@code profile == null} and the {@code ontology} isn't in any supported
     * profile.
     * @throws UnsupportedAxiomsException if {@code ontology} has some profile
     * of an unsupported type.
     * @throws PrologEngineCreationException if there was some problem during
     * the creation of the underlying Prolog engine.
     */
    public NoHRHybridKB(final NoHRHybridKBConfiguration configuration,
            final OWLOntology ontology,
            final Program program,
            final DBMappingSet dbMappings)
            throws OWLProfilesViolationsException,
            IPException,
            UnsupportedAxiomsException,
            PrologEngineCreationException {
        this(configuration, ontology, program, dbMappings, null, null);
    }

    /**
     * Constructs a {@link NoHRHybridKB} from a given
     * {@link OWLOntology ontology} and {@link Program program}.
     *
     * @param configuration
     * @param binDirectory the directory where the Prolog system to use as
     * underlying Prolog engine is located.
     * @param ontology the <i>ontology</i> component of this {@link HybridKB}.
     * @param program the <i>program</i> component of this {@link HybridKB}.
     * @param dbMappings the <i>database mappings</i> component of this {@link HybridKB}
     * @param profile the {@link Profile OWL profile} that will be considered
     * during the ontology translation. That will determine which the
     * translation - {@link <a>A Correct EL Oracle for NoHR (Technical
     * Report)</a>} or
	 *            {@link <a href= "http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>} will be applied. If none is specified the
     * preferred one will be applied. Whenever the ontology isn't in the
     * specified profile, if some is specified, an
     * {@link OWLProfilesViolationsException} will be thrown.
     * @param vocabulary the {@link Vocabulary} that will be used in this
     * {@link HybridKB}.
     * @throws OWLProfilesViolationsException if {@code profile != null} and
     * {@code ontology} isn't in the profile {@code profile}; or
     * {@code profile == null} and the {@code ontology} isn't in any supported
     * profile.
     * @throws UnsupportedAxiomsException if {@code ontology} has some profile
     * of an unsupported type.
     * @throws PrologEngineCreationException if there was some problem during
     * the creation of the underlying Prolog engine.
     * @throws IllegalArgumentException if {@code vocabularyMapping} doesn't
     * contains {@code ontology}.
     */
    public NoHRHybridKB(final NoHRHybridKBConfiguration configuration,
            final OWLOntology ontology,
            final Program program,
            DBMappingSet dbMappings,
            Vocabulary vocabulary, 
            Profile profile)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException, PrologEngineCreationException {

        Objects.requireNonNull(configuration);

        this.profile = profile;
        this.configuration = configuration;
        this.ontology = ontology;
        this.program = program;
        this.dbMappings = dbMappings;

        if (vocabulary != null) {
            if (!vocabulary.getOntology().equals(ontology)) {
                throw new IllegalArgumentException("vocabularyMapping: must contain the given ontology");
            }

            this.vocabulary = vocabulary;
        } else {
            this.vocabulary = new DefaultVocabulary(ontology);
        }

        assert this.vocabulary != null;
        deductiveDatabase = new XSBDeductiveDatabase(configuration.getXsbDirectory(), this.vocabulary);
        doubledProgram = deductiveDatabase.createProgram();
        databaseMappings = deductiveDatabase.createDBMappings();
        queryProcessor = new QueryProcessor(deductiveDatabase);
        ontologyTranslatorFactory = new OntologyTranslatorFactory(configuration.getOntologyTranslationConfiguration());
        ontologyTranslator = ontologyTranslatorFactory.createOntologyTranslator(ontology, this.vocabulary, deductiveDatabase, profile);
        hasOntologyChanges = true;
        hasProgramChanges = true;
        ontologyChangeListener = new OWLOntologyChangeListener() {

            @Override
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) throws OWLException {
                for (final OWLOntologyChange change : changes) {
                    if (change.getOntology() == ontology && change.isAxiomChange()
                            && change.getAxiom().isLogicalAxiom()) {
                        hasOntologyChanges = true;
                    }
                }
            }
        };
        programChangeListener = new ProgramChangeListener() {

            @Override
            public void added(Rule rule) {
                hasProgramChanges = true;
            }

            @Override
            public void cleared() {
                hasProgramChanges = true;
            }

            @Override
            public void removed(Rule rule) {
                hasProgramChanges = true;
            }

            @Override
            public void updated(Rule oldRule, Rule newRule) {
                hasProgramChanges = true;
            }
        };
        
        dbMappingsSetChangeListener = new DBMappingsSetChangeListener() {
			
			@Override
			public void updated(DBMapping oldDBMapping, DBMapping newDBMapping) {
				hasProgramChanges = true;
				
			}
			
			@Override
			public void removed(DBMapping dBMapping) {
				hasProgramChanges = true;
				
			}
			
			@Override
			public void cleared() {
				hasProgramChanges = true;
				
			}
			
			@Override
			public void added(DBMapping dBMapping) {
				hasProgramChanges = true;
				
			}
		};

        vocabularyChangeListener = new VocabularyChangeListener() {

            @Override
            public void constantChanged(Constant constant) {
                hasProgramChanges = true;
            }

            @Override
            public void predicateChanged(Predicate predicate) {
                hasProgramChanges = true;
            }
        };

        this.ontology.getOWLOntologyManager().addOntologyChangeListener(ontologyChangeListener);
        this.program.addListener(programChangeListener);
        this.dbMappings.addListener(dbMappingsSetChangeListener);
        this.vocabulary.addListener(vocabularyChangeListener);

        preprocess();
    }

    @Override
    public List<Answer> allAnswers(Query query) throws OWLProfilesViolationsException, UnsupportedAxiomsException, IPPrologError {
        return allAnswers(query, true, true, true);
    }

    @Override
    public List<Answer> allAnswers(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers) throws OWLProfilesViolationsException, UnsupportedAxiomsException, IPPrologError {
        if (hasOntologyChanges || hasProgramChanges) {
            preprocess();
        }

        RuntimesLogger.start("query");
        RuntimesLogger.info("querying: " + query);

        final List<Answer> answers = queryProcessor.allAnswers(query, hadDisjunctions, trueAnswer, undefinedAnswers, inconsistentAnswers);

        RuntimesLogger.stop("query", "queries");

        final List<Answer> result = new LinkedList<Answer>();

        for (final Answer ans : answers) {
            result.add(ans);
        }

        return result;
    }

    @Override
    public void dispose() {
        deductiveDatabase.dispose();
        ontology.getOWLOntologyManager().removeOntologyChangeListener(ontologyChangeListener);
        program.removeListener(programChangeListener);
        dbMappings.removeListener(dbMappingsSetChangeListener);
        vocabulary.removeListener(vocabularyChangeListener);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }

    @Override
    public OWLOntology getOntology() {
        return ontology;
    }

    /**
     * @return the ruleBase
     */
    @Override
    public Program getProgram() {
        return program;
    }
    
    /**
     * @return the database mappings
     */
    @Override
    public DBMappingSet getDBMappings() {
        return dbMappings;
    }

    @Override
    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    @Override
    public boolean hasAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        if (hasOntologyChanges || hasProgramChanges) {
            preprocess();
        }
        RuntimesLogger.start("query");
        RuntimesLogger.info("querying: " + query);
        final boolean hasAnswer = queryProcessor.hasAnswer(query, hadDisjunctions, trueAnswer, undefinedAnswers,
                inconsistentAnswers);
        RuntimesLogger.stop("query", "queries");
        return hasAnswer;
    }

    @Override
    public boolean hasDisjunctions() {
        return ontologyTranslator.requiresDoubling();
    }

    @Override
    public Answer oneAnswer(Query query) throws OWLOntologyCreationException, OWLOntologyStorageException,
            OWLProfilesViolationsException, UnsupportedAxiomsException {
        return oneAnswer(query, true, true, true);
    }

    @Override
    public Answer oneAnswer(Query query, boolean trueAnswer, boolean undefinedAnswers, boolean inconsistentAnswers)
            throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        if (hasOntologyChanges || hasProgramChanges) {
            preprocess();
        }
        RuntimesLogger.start("query");
        RuntimesLogger.info("querying: " + query);
        final Answer answer = queryProcessor.oneAnswer(query, hadDisjunctions, trueAnswer, undefinedAnswers,
                inconsistentAnswers);
        RuntimesLogger.stop("query", "queries");
        return answer;
    }

    /**
     * Preprocesses this {@link HybridKB} according to {@link <a>A Correct EL
     * Oracle for NoHR (Technical Report)</a>} and
	 * {@link <a href=" http://centria.di.fct.unl.pt/~mknorr/ISWC15/resources/ISWC15WithProofs.pdf">Next
     * Step for NoHR: OWL 2 QL</a>}, depending on the current ontolgy profile,
     * so that it can be queried. The translation {@link DatabaseProgram}s,
     * loaded in {@link #deductiveDatabase} are updated, if the ontology has
     * changed since the last call; {@link #doubledProgram} is updated, if they
     * were introduced disjunctions in the ontology, or if the program has
     * changed, since the last call. Also {@link #databaseMappings} is updated accordingly.
     *
     * @throws UnsupportedAxiomsException if the current version of the ontology
     * has some axioms of an unsupported type.
     * @throws OWLProfilesViolationsException if the { ontology isn't in any
     * supported OWL profile.
     */
    private void preprocess() throws OWLProfilesViolationsException, UnsupportedAxiomsException {
        if (hasOntologyChanges) {
            RuntimesLogger.start("ontology processing");

            if (profile == null && !ontologyTranslatorFactory.isPreferred(ontologyTranslator, ontology)) {
                ontologyTranslator = ontologyTranslatorFactory.createOntologyTranslator(ontology, vocabulary, deductiveDatabase, null);
            }

            ontologyTranslator.updateTranslation();

            RuntimesLogger.stop("ontology processing", "loading");
        }

        if (hasProgramChanges || ontologyTranslator.requiresDoubling() != hadDisjunctions) {
        	RuntimesLogger.start("rules parsing");
            doubledProgram.clear();
            if (ontologyTranslator.requiresDoubling()) {
                for (final Rule rule : program) {
                    doubledProgram.addAll(ProgramDoubling.doubleRule(rule));
                }
            } else {
                final ModelVisitor originalPredicates = new PredicateTypeVisitor(PredicateType.ORIGINAL);
                for (final Rule rule : program) {
                    doubledProgram.add(rule.accept(originalPredicates));
                }
            }
            
            databaseMappings.clear();
            final ModelVisitor originalEncoder = new PredicateTypeVisitor(PredicateType.ORIGINAL);
            final ModelVisitor doubleEncoder = new PredicateTypeVisitor(PredicateType.DOUBLE);
            final ModelVisitor negativeEncoder = new PredicateTypeVisitor(PredicateType.NEGATIVE);
            if (ontologyTranslator.requiresDoubling()) {
            	for(DBMapping dbMapping : dbMappings){
            		DBMapping originalMapping, doubleMapping;
            		originalMapping = new DBMappingImpl(dbMapping, originalEncoder, null);
            		doubleMapping = new DBMappingImpl(dbMapping, doubleEncoder, negativeEncoder);
            		databaseMappings.add(originalMapping);
            		databaseMappings.add(doubleMapping);
            	}
            }else{
            	for(DBMapping dbMapping : dbMappings){
            		DBMapping originalMapping;
            		originalMapping = new DBMappingImpl(dbMapping, originalEncoder, null);
            		databaseMappings.add(originalMapping);
            	}
            }
            

            RuntimesLogger.stop("rules parsing", "loading");
        }

        deductiveDatabase.commit();

        hasOntologyChanges = false;
        hasProgramChanges = false;
        hadDisjunctions = ontologyTranslator.requiresDoubling();
    }

}
