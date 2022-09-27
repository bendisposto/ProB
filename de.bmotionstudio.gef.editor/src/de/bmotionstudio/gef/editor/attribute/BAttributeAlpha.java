/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.property.SliderPropertyDescriptor;

public class BAttributeAlpha extends AbstractAttribute {

	public BAttributeAlpha(Object value) {
		super(value);
	}

	public PropertyDescriptor preparePropertyDescriptor() {
		SliderPropertyDescriptor descriptor = new SliderPropertyDescriptor(
				getID(), getName());
		return descriptor;
	}

	@Override
	public String validateValue(Object value, BControl control) {
		if (!(String.valueOf(value)).trim().matches("\\d*")) {
			return "Value must be a number";
		}

		if ((String.valueOf(value)).trim().length() == 0) {
			return "Value must not be empty string";
		}

		if (Integer.valueOf(value.toString()) > 255
				|| Integer.valueOf(value.toString()) < 0) {
			return "Only a range from 0 to 255 is allowed";
		}

		return null;
	}

	@Override
	public String getName() {
		return "Alpha";
	}

}
