/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.observer.SimpleValueDisplay;

public class WizardObserverSimpleValueDisplay extends ObserverWizard {

	private class ObserverSimpleValueDisplayPage extends
			AbstractObserverWizardPage {

		private Text txtReplacementString;
		private Text txtExpression;
		private Text txtPredicate;

		public Text getTxtExpression() {
			return txtExpression;
		}

		protected ObserverSimpleValueDisplayPage(final String pageName) {
			super(pageName, getObserver());
		}

		@Override
		public void createControl(final Composite parent) {

			super.createControl(parent);

			final DataBindingContext dbc = new DataBindingContext();

			Composite container = new Composite(parent, SWT.NONE);

			container.setLayoutData(new GridData(GridData.FILL_BOTH));
			container.setLayout(new GridLayout(2, false));

			Label lb = new Label(container, SWT.NONE);
			lb.setText("Guard:");

			txtPredicate = new Text(container, SWT.BORDER);
			txtPredicate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			txtPredicate.setFont(new Font(Display.getDefault(), new FontData(
					"Arial", 10, SWT.NONE)));

			lb = new Label(container, SWT.NONE);
			lb.setText("Expression:");
			lb.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

			txtExpression = new Text(container, SWT.BORDER | SWT.MULTI
					| SWT.WRAP);
			txtExpression.setLayoutData(new GridData(GridData.FILL_BOTH));
			// txtExpression.setFont(JFaceResources.getFontRegistry().get(
			// BMotionStudioConstants.RODIN_FONT_KEY));

			lb = new Label(container, SWT.NONE);
			lb.setText("Replacement String*:");

			txtReplacementString = new Text(container, SWT.BORDER);
			txtReplacementString.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			txtReplacementString.setFont(new Font(Display.getDefault(),
					new FontData("Arial", 10, SWT.NONE)));

			lb = new Label(container, SWT.NONE);
			lb.setLayoutData(new GridData(0,0,true,true,2,1));
			lb.setText("*String that will be replaced with the result of the expression.");
			
			initBindings(dbc);

			setControl(container);

		}

		private void initBindings(DataBindingContext dbc) {

			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(txtPredicate),
					BeanProperties.value(SimpleValueDisplay.class, "predicate")
							.observe((SimpleValueDisplay) getObserver()));

			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(txtExpression),
					BeanProperties.value(SimpleValueDisplay.class, "eval")
							.observe((SimpleValueDisplay) getObserver()));

			dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(txtReplacementString), 
					BeanProperties.value(SimpleValueDisplay.class, "replacementString")
							.observe((SimpleValueDisplay) getObserver()));

		}

	}

	public WizardObserverSimpleValueDisplay(BControl bcontrol,
			Observer bobserver) {
		super(bcontrol, bobserver);
		addPage(new ObserverSimpleValueDisplayPage(
				"ObserverSimpleValueDisplayPage"));
	}

	@Override
	protected Boolean prepareToFinish() {

		ObserverSimpleValueDisplayPage page = (ObserverSimpleValueDisplayPage) getPage("ObserverSimpleValueDisplayPage");

		String errorStr = "";

		if (page.getTxtExpression().getText().length() == 0)
			errorStr += "Please enter an expression.\n";

		if (page.getErrorMessage() != null)
			errorStr += "Please check the syntax/parser error.\n";

		if (errorStr.length() > 0) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"An Error occured", errorStr);
			return false;
		}

		return true;

	}

	@Override
	public Point getSize() {
		return new Point(600, 500);
	}

}
