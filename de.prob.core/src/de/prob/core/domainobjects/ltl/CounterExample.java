package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.command.LtlCheckingCommand.Result;
import de.prob.core.domainobjects.Operation;
import de.prob.logging.Logger;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Provides a counter-example.
 * 
 * @author Andriy Tolstoy
 * 
 */
public class CounterExample {
	private final static PrologTerm NONE = new CompoundPrologTerm("none");

	private final CounterExampleProposition propositionRoot;
	private final List<CounterExampleProposition> propositions = new ArrayList<CounterExampleProposition>();
	private final List<CounterExampleState> states = new ArrayList<CounterExampleState>();
	private final int loopEntry;
	private final List<Operation> initPath;

	private final PathType pathType;
	private final int ceSize; // the length of the counterexample (number of
								// states without the initialisation)

	protected CounterExample(CounterExampleProposition propositionRoot,
			int loopEntry, List<Operation> initPath, PathType pathType,
			int ceSize) {
		super();
		this.propositionRoot = propositionRoot;
		this.loopEntry = loopEntry;
		this.initPath = initPath;
		this.pathType = pathType;
		this.ceSize = ceSize;
	}

	public CounterExample(final Result modelCheckingResult) {
		loopEntry = modelCheckingResult.getLoopEntry();
		pathType = modelCheckingResult.getPathType();
		initPath = Collections.unmodifiableList(Arrays
				.asList(modelCheckingResult.getInitPathOps()));
		ceSize = modelCheckingResult.getCounterexample().size();

		final List<ArrayList<Boolean>> predicateValues = createStates(modelCheckingResult
				.getCounterexample());

		final String[] atomicFormulaNames = createAtomicNames(modelCheckingResult);
		propositionRoot = createExample(modelCheckingResult.getStructure(),
				atomicFormulaNames, predicateValues);
		propositionRoot.setVisible(true);
		Collections.reverse(propositions);

	}

	private String[] createAtomicNames(final Result modelCheckingResult) {
		String[] res = new String[modelCheckingResult.getAtomics().size()];
		int i = 0;
		for (final PrologTerm term : modelCheckingResult.getAtomics()) {
			res[i] = PrologTerm.atomicString(((CompoundPrologTerm) term)
					.getArgument(1));
		}
		return res;
	}

	private List<ArrayList<Boolean>> createStates(final ListPrologTerm example) {
		List<ArrayList<Boolean>> predicateValues = new ArrayList<ArrayList<Boolean>>();

		for (int i = 0; i < example.size(); i++) {
			predicateValues.add(new ArrayList<Boolean>());
		}

		int index = 0;
		for (PrologTerm exampleElement : example) {
			CompoundPrologTerm state = (CompoundPrologTerm) exampleElement;
			final PrologTerm stateId = state.getArgument(1);
			final ListPrologTerm values = ((ListPrologTerm) state
					.getArgument(2));
			final CompoundPrologTerm operationTerm = (CompoundPrologTerm) state
					.getArgument(3);

			for (int i = 0; i < values.size(); i++) {
				int value = ((IntegerPrologTerm) values.get(i)).getValue()
						.intValue();
				predicateValues.get(i).add(value == 0 ? false : true);
			}

			final Operation operation = NONE.equals(operationTerm) ? null
					: Operation.fromPrologTerm(operationTerm);
			final CounterExampleState ceState = new CounterExampleState(index,
					stateId, operation);
			states.add(ceState);
			index++;
		}

		return predicateValues;
	}

	private CounterExampleProposition createExample(final PrologTerm structure,
			final String[] atomicFormulaNames,
			List<ArrayList<Boolean>> predicateValues) {
		final CounterExampleProposition proposition;

		CompoundPrologTerm term = (CompoundPrologTerm) structure;
		String functor = term.getFunctor();
		int arity = term.getArity();

		CounterExampleValueType[] values = new CounterExampleValueType[states
				.size()];

		if (arity == 0) {
			if (functor.equals("true")) {
				Arrays.fill(values, CounterExampleValueType.TRUE);
			} else if (functor.equals("false")) {
				Arrays.fill(values, CounterExampleValueType.FALSE);
			}

			proposition = new CounterExamplePredicate(functor, this,
					Arrays.asList(values));
		} else if (arity == 1) {
			if (functor.equals("ap") || functor.equals("tp")) {
				IntegerPrologTerm atomic = (IntegerPrologTerm) term
						.getArgument(1);
				int atomicId = atomic.getValue().intValue();

				final String name = atomicFormulaNames[atomicId];

				Logger.assertProB("CounterExample invalid",
						values.length == predicateValues.get(atomicId).size());

				for (int i = 0; i < predicateValues.get(atomicId).size(); i++) {
					values[i] = predicateValues.get(atomicId).get(i) ? CounterExampleValueType.TRUE
							: CounterExampleValueType.FALSE;
				}

				proposition = functor.equals("ap") ? new CounterExamplePredicate(
						name, this, Arrays.asList(values))
						: new CounterExampleTransition(name, this,
								Arrays.asList(values));
			} else {
				proposition = createUnaryOperator(atomicFormulaNames,
						predicateValues, term, functor);
			}
		} else if (arity == 2) {
			proposition = createBinaryOperator(atomicFormulaNames,
					predicateValues, term, functor);
		} else {
			throw new IllegalArgumentException("Unexpected Prolog LTL " + arity
					+ "-ary operator " + functor);
		}

		propositions.add(proposition);

		return proposition;
	}

	private CounterExampleProposition createBinaryOperator(
			final String[] atomicFormulaNames,
			List<ArrayList<Boolean>> predicateValues, CompoundPrologTerm term,
			String functor) {
		final CounterExampleProposition proposition;
		final CounterExampleProposition firstArgument = createExample(
				term.getArgument(1), atomicFormulaNames, predicateValues);
		final CounterExampleProposition secondArgument = createExample(
				term.getArgument(2), atomicFormulaNames, predicateValues);

		if (functor.equals("and")) {
			proposition = new CounterExampleConjunction(this, firstArgument,
					secondArgument);
		} else if (functor.equals("or")) {
			proposition = new CounterExampleDisjunction(this, firstArgument,
					secondArgument);
		} else if (functor.equals("implies")) {
			proposition = new CounterExampleImplication(this, firstArgument,
					secondArgument);
		} else if (functor.equals("until")) {
			proposition = new CounterExampleUntil(this, firstArgument,
					secondArgument);
		} else if (functor.equals("weakuntil")) {
			proposition = new CounterExampleWeakUntil(this, firstArgument,
					secondArgument);
		} else if (functor.equals("release")) {
			proposition = new CounterExampleRelease(this, firstArgument,
					secondArgument);
		} else if (functor.equals("since")) {
			proposition = new CounterExampleSince(this, firstArgument,
					secondArgument);
		} else if (functor.equals("trigger")) {
			proposition = new CounterExampleTrigger(this, firstArgument,
					secondArgument);
		} else {
			throw new IllegalArgumentException(
					"Unexpected Prolog LTL binary operator " + functor);
		}

		firstArgument.setParent(proposition);
		secondArgument.setParent(proposition);
		return proposition;
	}

	private CounterExampleProposition createUnaryOperator(
			final String[] atomicFormulaNames,
			List<ArrayList<Boolean>> predicateValues, CompoundPrologTerm term,
			String functor) {
		final CounterExampleProposition proposition;
		final CounterExampleProposition argument = createExample(
				term.getArgument(1), atomicFormulaNames, predicateValues);
		if (functor.equals("globally")) {
			proposition = new CounterExampleGlobally(this, argument);
		} else if (functor.equals("finally")) {
			proposition = new CounterExampleFinally(this, argument);
		} else if (functor.equals("next")) {
			proposition = new CounterExampleNext(this, argument);
		} else if (functor.equals("not")) {
			proposition = new CounterExampleNegation(this, argument);
		} else if (functor.equals("once")) {
			proposition = new CounterExampleOnce(this, argument);
		} else if (functor.equals("yesterday")) {
			proposition = new CounterExampleYesterday(this, argument);
		} else if (functor.equals("historically")) {
			proposition = new CounterExampleHistory(this, argument);
		} else {
			throw new IllegalArgumentException(
					"Unexpected Prolog LTL unary operator " + functor);
		}

		argument.setParent(proposition);
		return proposition;
	}

	public CounterExampleProposition getPropositionRoot() {
		return propositionRoot;
	}

	public List<CounterExampleProposition> getPropositions() {
		return propositions;
	}

	public List<CounterExampleState> getStates() {
		return states;
	}

	public PathType getPathType() {
		return pathType;
	}

	public int getLoopEntry() {
		return loopEntry;
	}

	public List<Operation> getFullPath() {
		List<Operation> fullPath = new ArrayList<Operation>(initPath);
		for (final CounterExampleState ceState : states) {
			final Operation operation = ceState.getOperation();
			if (operation != null) {
				fullPath.add(operation);
			}
		}
		return fullPath;
	}

	public List<Operation> getInitPath() {
		return initPath;
	}

	public int getCounterExampleSize() {
		return ceSize;
	}
}
