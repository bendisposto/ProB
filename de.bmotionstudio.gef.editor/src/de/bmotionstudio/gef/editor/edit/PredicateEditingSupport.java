/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.edit;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.util.BMotionWizardUtil;

/**
 * @author Lukas Ladenberger
 * 
 */
public class PredicateEditingSupport extends ObservableValueEditingSupport {

	private TextCellEditor cellEditor;
	private String property;

	public PredicateEditingSupport(TableViewer viewer, DataBindingContext dbc,
			String property, Visualization visualization, Shell shell) {
		super(viewer, dbc);
		this.property = property;
		this.cellEditor = new PopupCellEditor((Composite) getViewer()
				.getControl(), shell);
	}

	@Override
	protected IObservableValue doCreateCellEditorObservable(
			CellEditor cellEditor) {
		return WidgetProperties.text(SWT.Modify).observe(cellEditor.getControl());
	}

	@Override
	protected IObservableValue doCreateElementObservable(Object element,
			ViewerCell cell) {
		return BeanProperties.value(property).observe(element);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return BMotionWizardUtil.isEditElement(getViewer());
	}

}
