/**
 * 
 */
package de.prob.ui.stateview.statetree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationGetValuesCommand.EvaluationResult;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.EvaluationStateElement;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

/**
 * @author plagge
 * 
 */
public class StateTreeExpression extends AbstractStateTreeElement {
	private final EvaluationElement staticElement;
	private List<StateTreeElement> children = null;

	public StateTreeExpression(final StaticStateElement parent,
			final EvaluationElement staticElement) {
		super(parent);
		this.staticElement = staticElement;

	}

	public StaticStateElement[] getChildren() {
		checkForChildren();
		return children.toArray(StateTreeElement.EMPTY_ARRAY);
	}

	private void checkForChildren() {
		if (children == null) {
			try {
				EvaluationElement[] staticChildren = staticElement
						.getChildren();
				children = new ArrayList<StateTreeElement>(
						staticChildren.length);
				for (EvaluationElement sChild : staticChildren) {
					final StateTreeExpression dChild = new StateTreeExpression(
							this, sChild);
					children.add(dChild);
				}
			} catch (ProBException e) {
				e.notifyUserOnce();
				children = Collections.emptyList();
			}
		}
	}

	public String getLabel() {
		try {
			return staticElement.getLabel();
		} catch (ProBException e) {
			e.notifyUserOnce();
			return null;
		}
	}

	public boolean hasChildren() {
		checkForChildren();
		return !children.isEmpty();
	}

	public boolean hasChanged(final State current, final State last) {
		final String curval, lastval;
		try {
			curval = getResultString(current);
			lastval = getResultString(last);
		} catch (ProBException e) {
			e.notifyUserOnce();
			return false;
		}
		return !Objects.equals(curval, lastval);
	}

	private String getResultString(final State state) throws ProBException {
		final EvaluationStateElement dyn = EvaluationGetValuesCommand
				.getSingleValueCached(state, staticElement);
		final EvaluationResult res = dyn == null ? null : dyn.getResult();
		return res == null ? null : res.getText();
	}

	public EvaluationElement getStaticElement() {
		return staticElement;
	}

	public StateDependendElement getValue(final State state) {
		StateDependendElement sd;
		try {
			EvaluationStateElement dynamicElement = EvaluationGetValuesCommand
					.getSingleValueCached(state, staticElement);
			EStateTreeElementProperty property = EStateTreeElementProperty.INACTIVE;
			String value = "?";
			if (dynamicElement != null) {
				final EvaluationResult result = dynamicElement.getResult();
				if (result.isActive()) {
					if (result.hasError()) {
						property = EStateTreeElementProperty.ERROR;
					} else if (result.isPredicate()) {
						property = result.isPredicateTrue() ? EStateTreeElementProperty.TRUE
								: EStateTreeElementProperty.FALSE;
					} else {
						property = EStateTreeElementProperty.NONBOOLEAN;
					}
				}
				value = result.getText();
			}
			sd = new StateDependendElement(state, value, property);
		} catch (ProBException e) {
			e.notifyUserOnce();
			sd = new StateDependendElement(state, "(internal error)",
					EStateTreeElementProperty.ERROR);
		}
		return sd;
	}

}
