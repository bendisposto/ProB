/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class BAttributeText extends AbstractAttribute {

	public BAttributeText(Object value) {
		super(value);
	}

	public PropertyDescriptor preparePropertyDescriptor() {
		return new TextPropertyDescriptor(getID(), getName());
	}

	@Override
	public String getName() {
		return "Text";
	}

}
