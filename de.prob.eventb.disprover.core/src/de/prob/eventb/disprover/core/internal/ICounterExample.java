package de.prob.eventb.disprover.core.internal;

public interface ICounterExample {

	public boolean counterExampleFound();

	public boolean timeoutOccured();

	public boolean isProof();

	public boolean onlySelectedHypotheses();

	public String getReason();

	public boolean doubleCheckFailed();

}