/**
 * 
 */
package de.prob.ui.stateview.statetree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.State;

/**
 * @author plagge
 * 
 */
public class StateTreeExpressionSection extends AbstractStateTreeElement {
	private final String label;
	private final List<StateTreeElement> children;

	public StateTreeExpressionSection(final String label,
			final Collection<EvaluationElement> children) {
		this(null, label, children);
	}

	public StateTreeExpressionSection(final StaticStateElement parent,
			final String label, final Collection<EvaluationElement> children) {
		super(parent);
		this.label = label;
		this.children = new ArrayList<StateTreeElement>(children.size());
		for (final EvaluationElement elem : children) {
			StateTreeElement child = new StateTreeExpression(this, elem);
			this.children.add(child);
		}
	}

	@Override
	public StaticStateElement[] getChildren() {
		return children.toArray(StateTreeElement.EMPTY_ARRAY);
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	@Override
	public boolean hasChanged(State current, State last) {
		return false;
	}

	public void addChild(final EvaluationElement staticElement) {
		final StateTreeExpression child = new StateTreeExpression(this,
				staticElement);
		this.children.add(child);
	}

	@Override
	public StateDependendElement getValue(final State state) {
		return new StateDependendElement(state, null,
				EStateTreeElementProperty.NONBOOLEAN);
	}

}
