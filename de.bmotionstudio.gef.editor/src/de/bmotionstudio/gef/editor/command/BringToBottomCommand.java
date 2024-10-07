/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BControlPropertyConstants;

public class BringToBottomCommand extends AbstractBringToCommand {

	@Override
	public void execute() {
		for (BControl control : getControlList()) {
			BControl parent = control.getParent();
			Integer oldIndex = parent.getChildrenArray().indexOf(control);
			getOldIndexMap().put(control, oldIndex);
			parent.getChildrenArray().remove(control);
			parent.getChildrenArray().add(0, control);
			parent.getListeners().firePropertyChange(
					BControlPropertyConstants.PROPERTY_ADD_CHILD, null, null);
		}
	}

	@Override
	public void undo() {
		for (BControl control : getControlList()) {
			BControl parent = control.getParent();
			parent.getChildrenArray().remove(control);
			parent.getChildrenArray().add(getOldIndexMap().get(control),
					control);
			parent.getListeners().firePropertyChange(
					BControlPropertyConstants.PROPERTY_ADD_CHILD, null, null);
		}
	}

}
