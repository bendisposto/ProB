/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui;

import de.prob.core.IAnimationListener;
import de.prob.core.IComputationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.ListenerRegistry;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;

public class StaticListenerRegistry implements ILifecycleListener,
		IComputationListener, IAnimationListener {

	private static final ListenerRegistry registry = new ListenerRegistry();

	public static void registerListener(final ILifecycleListener listener) {
		registry.registerLifecycleListener(listener);
	}

	public static void unregisterListener(final ILifecycleListener listener) {
		registry.unregisterLifecycleListener(listener);
	}

	public static void registerListener(final IComputationListener listener) {
		registry.registerComputationListener(listener);
	}

	public static void unregisterListener(final IComputationListener listener) {
		registry.unregisterComputationListener(listener);
	}

	public static void registerListener(final IAnimationListener listener) {
		registry.registerAnimationListener(listener);
	}

	public static void unregisterListener(final IAnimationListener listener) {
		registry.unregisterAnimationListener(listener);
	}

	@Override
	public void reset() {
		registry.reset();
	}

	@Override
	public void computedState(final State state) {
		registry.computedState(state);
	}

	@Override
	public void currentStateChanged(final State currentState,
			final Operation operation) {
		registry.currentStateChanged(currentState, operation);
	}
}
