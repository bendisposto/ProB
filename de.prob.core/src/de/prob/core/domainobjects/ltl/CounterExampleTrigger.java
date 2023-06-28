package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a "trigger" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleTrigger extends CounterExampleBinaryOperator {
	public CounterExampleTrigger(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("T", "Trigger", counterExample, firstArgument, secondArgument);
		checkBySince(counterExample, firstArgument, secondArgument);
	}

	private void checkBySince(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		CounterExampleNegation notFirst = new CounterExampleNegation(
				counterExample, firstArgument);
		CounterExampleNegation notSecond = new CounterExampleNegation(
				counterExample, secondArgument);
		CounterExampleSince since = new CounterExampleSince(counterExample,
				notFirst, notSecond);
		addCheck(new CounterExampleNegation(counterExample, since));
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		return calculateTriggerOperator(position);
	}

	private CounterExampleValueType calculateTriggerOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		// remove all future values
		List<CounterExampleValueType> firstCheckedValues = new ArrayList<CounterExampleValueType>(
				getFirstArgument().getValues().subList(0, position + 1));
		List<CounterExampleValueType> secondCheckedValues = new ArrayList<CounterExampleValueType>(
				getSecondArgument().getValues().subList(0, position + 1));

		int secondIndex = -1;

		boolean trueOrUnknown = false;

		// look for a state with a true value in first argument
		int firstIndex = firstCheckedValues
				.lastIndexOf(CounterExampleValueType.TRUE);

		if (firstIndex != -1) {
			// look for a state with a false value in second argument
			secondIndex = secondCheckedValues.subList(firstIndex,
					secondCheckedValues.size()).lastIndexOf(
					CounterExampleValueType.FALSE);

			if (secondIndex == -1) {
				trueOrUnknown = true;

				// look for a state with an unknown value in first and
				// second argument
				int unknownStateIndex = indexOfUnknownState(
						firstCheckedValues.subList(firstIndex,
								firstCheckedValues.size()),
						secondCheckedValues.subList(firstIndex,
								secondCheckedValues.size()), true);

				if (unknownStateIndex != -1) {
					unknownStateIndex += firstIndex;
					firstCheckedValues = firstCheckedValues.subList(
							unknownStateIndex, firstCheckedValues.size());
					secondCheckedValues = secondCheckedValues.subList(
							unknownStateIndex, secondCheckedValues.size());

					firstIndex = -1;
				} else {
					secondCheckedValues = secondCheckedValues.subList(
							firstIndex, secondCheckedValues.size());

					// look for the state with an unknown value in second
					// argument
					if (!secondCheckedValues
							.contains(CounterExampleValueType.UNKNOWN)) {
						result = CounterExampleValueType.TRUE;
					} else {
						firstCheckedValues = firstCheckedValues.subList(
								firstIndex, firstCheckedValues.size());
						firstIndex = -1;
					}
				}
			}
		} else {
			// all states of first argument are invalid and all states of second
			// argument are valid on a finite or an infinite path
			if (!firstCheckedValues.contains(CounterExampleValueType.UNKNOWN)
					&& !secondCheckedValues
							.contains(CounterExampleValueType.FALSE)
					&& !secondCheckedValues
							.contains(CounterExampleValueType.UNKNOWN)) {
				trueOrUnknown = true;
				result = CounterExampleValueType.TRUE;
				firstCheckedValues.clear();
			}
		}

		if (!trueOrUnknown) {
			// look for a state with a false value in second argument
			secondIndex = secondCheckedValues
					.lastIndexOf(CounterExampleValueType.FALSE);

			if (secondIndex != -1) {
				firstCheckedValues = firstCheckedValues.subList(
						secondIndex + 1, firstCheckedValues.size());
				firstIndex = -1;

				// look for a state with an unknown value in first argument
				if (!firstCheckedValues
						.contains(CounterExampleValueType.UNKNOWN)) {
					result = CounterExampleValueType.FALSE;
				} else {
					// look for a state with an unknown value in first and
					// second argument
					int unknownStateIndex = indexOfUnknownState(
							firstCheckedValues,
							secondCheckedValues.subList(secondIndex + 1,
									secondCheckedValues.size()), true);

					if (unknownStateIndex != -1) {
						secondCheckedValues = secondCheckedValues.subList(
								secondIndex + 1, secondCheckedValues.size());

						firstCheckedValues = firstCheckedValues.subList(
								unknownStateIndex, firstCheckedValues.size());
						secondCheckedValues = secondCheckedValues.subList(
								unknownStateIndex, secondCheckedValues.size());
					} else {
						secondCheckedValues = secondCheckedValues.subList(
								secondIndex, secondCheckedValues.size());
					}

					secondIndex = -1;
				}
			} else {
				// look for a state with an unknown value in first and
				// second argument
				final int unknownStateIndex = indexOfUnknownState(
						firstCheckedValues, secondCheckedValues, true);

				if (unknownStateIndex != -1) {
					firstCheckedValues = firstCheckedValues.subList(
							unknownStateIndex, firstCheckedValues.size());
					secondCheckedValues = secondCheckedValues.subList(
							unknownStateIndex, secondCheckedValues.size());
				}
			}
		}

		fillHighlightedPositions(position, firstIndex, secondIndex,
				firstCheckedValues.size(), secondCheckedValues.size(), true);

		return result;
	}
}
