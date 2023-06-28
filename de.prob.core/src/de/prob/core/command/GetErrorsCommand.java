/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */
package de.prob.core.command;

import java.util.List;

import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * A command to get all the stored errors of ProB's Prolog part as a list of
 * strings.
 * 
 * @author plagge
 */
public class GetErrorsCommand implements IComposableCommand {
	private static final String ERRORS_VARIABLE = "Errors";
	private static final String ONLY_WARNINGS_VARIABLE = "OnlyWarnings";
	private List<String> errors;
	private boolean onlyWarnings;

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		errors = PrologTerm.atomicStrings((ListPrologTerm) bindings
				.get(ERRORS_VARIABLE));
		onlyWarnings = "true".equals(bindings.get(ONLY_WARNINGS_VARIABLE)
				.getFunctor());
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getErrorMessages").printVariable(ONLY_WARNINGS_VARIABLE)
				.printVariable(ERRORS_VARIABLE).closeTerm();
	}

	public List<String> getErrors() {
		return errors;
	}

	public boolean onlyWarningsOccurred() {
		return onlyWarnings;
	}

}
