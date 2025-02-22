/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *******************************************************************************/
package de.bmotionstudio.gef.editor.command;

import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;

/**
 * A command to create a connection between two shapes. The command can be
 * undone or redone.
 * <p>
 * This command is designed to be used together with a GraphicalNodeEditPolicy.
 * To use this command properly, following steps are necessary:
 * </p>
 * <ol>
 * <li>Create a subclass of GraphicalNodeEditPolicy.</li>
 * <li>Override the <tt>getConnectionCreateCommand(...)</tt> method, to create a
 * new instance of this class and put it into the CreateConnectionRequest.</li>
 * <li>Override the <tt>getConnectionCompleteCommand(...)</tt> method, to obtain
 * the Command from the ConnectionRequest, call setTarget(...) to set the target
 * endpoint of the connection and return this command instance.</li>
 * </ol>
 * 
 * @see org.eclipse.gef.examples.shapes.parts.ShapeEditPart#createEditPolicies()
 *      for an example of the above procedure.
 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy
 * @author Elias Volanakis
 */
public class ConnectionCreateCommand extends Command {
	/** The connection instance. */
	private BConnection connection;

	/** Start endpoint for the connection. */
	private final BControl source;
	/** Target endpoint for the connection. */
	private BControl target;

	/**
	 * Instantiate a command that can create a connection between two shapes.
	 * 
	 * @param source
	 *            the source endpoint (a non-null Shape instance)
	 * @param lineStyle
	 *            the desired line style. See Connection#setLineStyle(int) for
	 *            details
	 * @throws IllegalArgumentException
	 *             if source is null
	 * @see Connection#setLineStyle(int)
	 */
	public ConnectionCreateCommand(BControl source) {
		if (source == null) {
			throw new IllegalArgumentException();
		}
		setLabel("connection creation");
		this.source = source;
	}

	@Override
	public boolean canExecute() {
		// disallow source -> source connections
		if (source.equals(target)) {
			return false;
		}
		// return false, if the source -> target connection exists already
		for (Iterator<BConnection> iter = source.getSourceConnections()
				.iterator(); iter.hasNext();) {
			BConnection conn = (BConnection) iter.next();
			if (conn.getTarget().equals(target)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void execute() {
		// create a new connection between source and target
		connection.setSource(source);
		connection.setTarget(target);
		connection.reconnect();
	}

	@Override
	public void redo() {
		connection.reconnect();
	}

	/**
	 * Set the target endpoint for the connection.
	 * 
	 * @param target
	 *            that target endpoint (a non-null Shape instance)
	 * @throws IllegalArgumentException
	 *             if target is null
	 */
	public void setTarget(BControl target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}
		this.target = target;
	}

	@Override
	public void undo() {
		connection.disconnect();
	}

	public void setConnection(BConnection con) {
		this.connection = con;
	}

	public BConnection getConnection() {
		return this.connection;
	}

	public BControl getSource() {
		return this.source;
	}

	public BControl getTarget() {
		return this.target;
	}

}
