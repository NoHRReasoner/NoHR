/**
 *
 */
package benchmark.data;

import static pt.unl.fct.di.centria.nohr.model.concrete.Model.atom;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.cons;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.negLiteral;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.rule;
import static pt.unl.fct.di.centria.nohr.model.concrete.Model.var;
import static pt.unl.fct.di.centria.nohr.model.predicates.Predicates.pred;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;

import com.lexicalscope.jewel.cli.ArgumentValidationException;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import pt.unl.fct.di.centria.nohr.model.Atom;
import pt.unl.fct.di.centria.nohr.model.Constant;
import pt.unl.fct.di.centria.nohr.model.Literal;
import pt.unl.fct.di.centria.nohr.model.Predicate;
import pt.unl.fct.di.centria.nohr.model.Rule;
import pt.unl.fct.di.centria.nohr.model.Term;
import pt.unl.fct.di.centria.nohr.model.Variable;
import pt.unl.fct.di.centria.nohr.model.concrete.Model;

/**
 * @author Nuno Costa
 */
public class ProgramGenerator {

	interface ProgramGenerationSettings {

		@Option(longName = "arity-deviation", defaultValue = "1", description = "The standard deviation of the arity of the predicates.")
		double getArityDeviation();

		@Option(longName = "arity-mean", defaultValue = "3", description = "The mean of the arity of the predicates.")
		double getArityMean();

		@Option(longName = "constants", defaultToNull = true, description = "The number of (new) constants.")
		Integer getConstants();

		@Option(longName = "facts", defaultToNull = true, description = "The number of facts in the first program.")
		Integer getFacts();

		@Option(longName = "facts-step", defaultToNull = true, description = "The number of facts that will be added to each program i to obtain the program i+1.")
		Integer getFactsStep();

		@Option(helpRequest = true)
		boolean getHelp();

		@Option(longName = "neg-len-deviation", defaultValue = "0.5", description = "The standard deviation of the negative body length (i.e. of the number of default negated literals in the body of the rules).")
		double getNegativeLengthDeviation();

		@Option(longName = "neg-len-mean", defaultValue = "1", description = "The mean of the negative body length (i.e. of the number of default negated literals in the body of the rules).")
		double getNegativeLengthMean();

		@Option(longName = "ontology", description = "The ontology.")
		File getOntology();

		@Option(longName = "output", description = "The output files prefix. The program files will have the path <prefix><i>.p index be concatenated, for each program <i>.")
		String getOutput();

		@Option(longName = "pos-len-deviation", defaultValue = "8", description = "The standard deviation of the positive body length (i.e. of the number of non negated literals in the body of the rules).")
		double getPositiveLengthDeviation();

		@Option(longName = "pos-len-mean", defaultValue = "3", description = "The mean of the positive body length (i.e. of the number of non negated literals in the body of the rules).")
		double getPositiveLenthMean();

		@Option(longName = "predicates", defaultToNull = true, description = "The number of new predicates")
		Integer getPredicates();

		@Option(longName = "programs", defaultValue = "10", description = "The number of programs to generate. The programs will be generated cumulatively, i.e. each program i+1 will contain the set of rules of the program i.")
		int getPrograms();

		@Option(longName = "rules", defaultToNull = true, description = "The number of (non fact) rules in the first program.")
		Integer getRules();

		@Option(longName = "rules-step", defaultToNull = true, description = "The number of rules added to each program i to obtain the program i+1.")
		Integer getRulesStep();

		@Option(longName = "seed", defaultValue = "0")
		Long getSeed();

		@Option(longName = "vars-ratio", defaultValue = "0.75", description = "The proportion of variables by positive literal's arguments. The remaining will be constants.")
		double getVariableRatio();

		@Option(longName = "vars-rep-ratio", defaultValue = "0.25", description = "The proportion of repeated variables in the positive literals.")
		double getVariableRepetionRation();

		boolean isOutput();

	}

	private static final String PRED_PREFIX = "p";
	private static final String CONST_PREFIX = "c";
	private static final String VAR_PREFIX = "X";

	public static void main(String args[]) {
		ProgramGenerationSettings conf;
		try {
			conf = CliFactory.parseArguments(ProgramGenerationSettings.class, args);
		} catch (final ArgumentValidationException e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}
		final OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology;
		try {
			ontology = ontologyManager.loadOntologyFromOntologyDocument(conf.getOntology());
		} catch (final OWLOntologyCreationException e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}
		final DiscreteRandomVariable arity = new NormalDiscreteVariable(conf.getSeed(), conf.getArityMean(),
				conf.getArityDeviation());
		final DiscreteRandomVariable positiveBodyLength = new NormalDiscreteVariable(conf.getSeed(),
				conf.getPositiveLenthMean(), conf.getPositiveLengthDeviation());
		final DiscreteRandomVariable negativeBodyLength = new NormalDiscreteVariable(conf.getSeed(),
				conf.getNegativeLengthMean(), conf.getNegativeLengthDeviation());
		final ProgramGenerator programGenerator = new ProgramGenerator(ontology, conf.getSeed(), conf.getPredicates(),
				arity, conf.getConstants());
		Integer facts = conf.getFacts();
		if (facts == null)
			facts = ontology.getAxiomCount(AxiomType.CLASS_ASSERTION)
					+ ontology.getAxiomCount(AxiomType.OBJECT_PROPERTY_ASSERTION)
					+ ontology.getAxiomCount(AxiomType.DATA_PROPERTY_ASSERTION);
		Integer rules = conf.getRules();
		if (rules == null)
			rules = (ontology.getLogicalAxiomCount() - facts) * 10;
		Integer factsStep = conf.getFactsStep();
		if (factsStep == null)
			factsStep = (int) Math.round(facts * 0.1);
		Integer rulesStep = conf.getRulesStep();
		if (rulesStep == null)
			rulesStep = (int) Math.round(rules * 0.1);
		final int programs = Math.max(1, conf.getPrograms());
		final String programName = conf.getOntology().getName().replaceAll(".owl\\z", "");
		for (int i = 1; i <= programs; i++) {
			final int f = i == 1 ? facts : factsStep;
			final int r = i == 1 ? rules : rulesStep;
			System.out.println("Generating program " + i + " (" + facts + (i - 1) * factsStep + " facts and " + rules
					+ (i - 1) * rulesStep + " rules)");
			programGenerator.generate(f, r, positiveBodyLength, negativeBodyLength, conf.getVariableRatio(),
					conf.getVariableRepetionRation());
			System.out.println("Writing");
			final File file;
			if (conf.isOutput())
				file = new File(conf.getOutput() + i + ".p");
			else
				file = new File(programName + i + ".p");
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
				for (final Rule rule : programGenerator.getProgram()) {
					writer.write(rule.toString());
					writer.write(".");
					writer.newLine();
				}
			} catch (final IOException e) {
				System.err.println(e.getMessage());
				System.exit(1);
				return;
			}
		}
	}

	private final List<Predicate> predicates;
	private final List<Constant> constants;
	private final Random random;
	private final Set<Rule> program;

	public ProgramGenerator(OWLOntology ontology, Long seed, Integer newPredicates, DiscreteRandomVariable arity,
			Integer newConstants) {
		random = new Random(seed);
		predicates = new ArrayList<>();
		constants = new ArrayList<>();
		program = new HashSet<>();
		for (final OWLClass concept : ontology.getClassesInSignature())
			predicates.add(pred(concept));
		for (final OWLProperty<?, ?> role : ontology.getObjectPropertiesInSignature())
			predicates.add(pred(role));
		for (final OWLProperty<?, ?> data : ontology.getDataPropertiesInSignature())
			predicates.add(pred(data));
		for (final OWLIndividual individual : ontology.getIndividualsInSignature())
			constants.add(Model.cons(individual));
		if (newPredicates == null)
			newPredicates = predicates.size();
		if (newConstants == null)
			newConstants = constants.size();
		for (int i = 1; i <= newPredicates; i++)
			predicates.add(pred(PRED_PREFIX + i, Math.max(0, arity.next())));
		for (int i = 1; i <= newConstants; i++)
			constants.add(cons(CONST_PREFIX + i));
	}

	public Set<Rule> generate(int facts, int rules, DiscreteRandomVariable positiveBodyLength,
			DiscreteRandomVariable negativeBodyLength, double variablesRate, double variablesRepetitionRate) {
		for (int i = 0; i < facts; i++)
			program.add(generateFact());
		for (int i = 0; i < rules; i++)
			program.add(generateRule(positiveBodyLength, negativeBodyLength, variablesRate, variablesRepetitionRate));
		return program;
	}

	private Rule generateFact() {
		final Predicate functor = nextPredicate();
		final List<Term> args = new ArrayList<>(functor.getArity());
		for (int i = 0; i < functor.getArity(); i++)
			args.add(nextConstant());
		return rule(atom(functor, args));
	}

	private Rule generateRule(DiscreteRandomVariable positiveBodyLength, DiscreteRandomVariable negativeBodyLength,
			double variablesRatio, double variablesRepetitionRatio) {
		if (variablesRatio > 1 || variablesRatio < 0)
			throw new IllegalArgumentException("variablesRate: must be a real in [0, 1]");
		if (variablesRepetitionRatio > 1 || variablesRepetitionRatio < 0)
			throw new IllegalArgumentException("variabesRepetitionRate: must be a real in [0, 1]");
		// lengths:
		final int posLen = Math.max(1, positiveBodyLength.next());
		final int negLen = Math.max(0, negativeBodyLength.next());
		// positive body functors:
		final List<Predicate> posFunctors = new ArrayList<>(posLen);
		int posArgs = 0;
		for (int i = 0; i < posLen; i++) {
			final Predicate pred = nextPredicate();
			posFunctors.add(pred);
			posArgs += pred.getArity();
		}
		// variables:
		final int n = (int) Math.floor(posArgs * variablesRatio * (1 - variablesRepetitionRatio));
		final List<Variable> vars = new ArrayList<>(n);
		for (int i = 1; i <= n; i++)
			vars.add(var(VAR_PREFIX + i));
		// body:
		final List<Literal> body = new ArrayList<>(posLen + negLen);
		// positive body:
		final List<Variable> safeVars = new ArrayList<>(n);
		for (final Predicate pred : posFunctors) {
			final List<Term> args = new ArrayList<Term>(pred.getArity());
			for (int i = 0; i < pred.getArity(); i++)
				if (succeed(variablesRatio) && !vars.isEmpty()) {
					final Variable var = select(vars);
					args.add(var);
					safeVars.add(var);
				} else
					args.add(nextConstant());
			body.add(atom(pred, args));
		}
		// negative body:
		for (int i = 0; i < negLen; i++) {
			final Predicate pred = nextPredicate();
			final List<Term> args = new ArrayList<Term>(pred.getArity());
			for (int j = 0; j < pred.getArity(); j++)
				if (succeed(variablesRatio) && !safeVars.isEmpty())
					args.add(select(safeVars));
				else
					args.add(nextConstant());
			body.add(negLiteral(pred, args));
		}
		// head:
		final Predicate pred = nextPredicate();
		final List<Term> args = new ArrayList<Term>(pred.getArity());
		for (int i = 0; i < pred.getArity(); i++)
			if (succeed(variablesRatio) && !safeVars.isEmpty())
				args.add(select(safeVars));
			else
				args.add(nextConstant());
		final Atom head = atom(pred, args);
		// rule:
		return rule(head, body);
	}

	public Set<Rule> getProgram() {
		return program;
	}

	private Constant nextConstant() {
		final int i = random.nextInt(constants.size());
		return constants.get(i);
	}

	private Predicate nextPredicate() {
		final int i = random.nextInt(predicates.size());
		return predicates.get(i);
	}

	private <E> E select(List<E> list) {
		return list.get(random.nextInt(list.size()));
	}

	private boolean succeed(double p) {
		return random.nextDouble() <= p;
	}

}
