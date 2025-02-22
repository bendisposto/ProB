/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import de.prob.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ClassicPreferences extends PreferencePage implements
		IWorkbenchPreferencePage {

	public static final class PushButton extends SelectionAdapter {

		private final Text text;
		private final Shell shell;

		public PushButton(final Shell shell, final Text text) {
			this.shell = shell;
			this.text = text;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			super.widgetSelected(e);
			FileDialog dialog = new FileDialog(shell, SWT.OPEN);
			String open = dialog.open();
			open = open.replaceAll(" ", "\\\\ ");
			text.setText(open);
		}

	}

	private Preferences prefNode;
	private Text text;

	public ClassicPreferences() {
		super();
	}

	public ClassicPreferences(final String title) {
		super(title);
	}

	public ClassicPreferences(final String title, final ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(final Composite parent) {
		Composite pageComponent = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		pageComponent.setLayout(layout);

		Label label = new Label(pageComponent, SWT.NONE);
		label.setText("Location of ProB Standalone:");
		text = new Text(pageComponent, SWT.NONE);
		String location = prefNode.get("location", "");
		// text.setLayoutData(new RowData(100, SWT.DEFAULT));
		text.setText(location);

		Button browseButton = new Button(pageComponent, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new PushButton(pageComponent.getShell(), text));
		Label versionRemark = new Label(pageComponent, SWT.WRAP);
		versionRemark.setText(
				"Note: This needs a version of ProB2-UI (jar file) that is at least 1.0.1 or a version of ProB Tcl/Tk that is at least 1.3.1.\nYou can obtain both from https://prob.hhu.de/w/index.php/Download\n"); // precisely 1.3.0-beta6

		GridData gridData2 = new GridData();
		gridData2.horizontalSpan = 3;
		versionRemark.setLayoutData(gridData2);

		return pageComponent;
	}

	@Override
	public boolean performOk() {
		prefNode.put("location", text.getText());
		try {
			prefNode.flush();
		} catch (BackingStoreException e) {
			Logger.notifyUser("Failed to save ProB Standalone preferences", e);
		}
		return super.performOk();
	}

	@Override
	public void init(final IWorkbench workbench) {
		prefNode = Platform.getPreferencesService().getRootNode().node(
				InstanceScope.SCOPE).node("prob_classic_preferences");
	}

}
