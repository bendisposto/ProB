package de.prob.ui.ltl;

import org.eclipse.draw2d.IFigure;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;

public final class CounterExampleUnaryEditPart extends
		CounterExamplePropositionEditPart {
	@Override
	protected IFigure createFigure() {
		CounterExampleProposition model = (CounterExampleProposition) getModel();
		return new CounterExampleUnaryFigure(model);
	}
}
