/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import de.bmotionstudio.gef.editor.library.LibraryView;

public class PerspectiveEditFactory implements IPerspectiveFactory {

	public void createInitialLayout(final IPageLayout layout) {

		final String editorArea = layout.getEditorArea();

		// Place the project explorer to left of editor area.
		final IFolderLayout left = layout.createFolder("left",
				IPageLayout.LEFT, 0.15f, editorArea);
		left.addView("fr.systerel.explorer.navigator.view");

		// Place the outline to right of editor area.
		final IFolderLayout righttop = layout.createFolder("right",
				IPageLayout.RIGHT, 0.8f, editorArea);
		righttop.addView(IPageLayout.ID_OUTLINE);

		// Library view
		final IFolderLayout rightbot = layout.createFolder("rightb",
				IPageLayout.BOTTOM, 0.6f, "right");
		rightbot.addView(LibraryView.ID);

		final IFolderLayout bottom = layout.createFolder("bottom",
				IPageLayout.BOTTOM, 0.75f, editorArea);
		// Properties view
		bottom.addView(IPageLayout.ID_PROP_SHEET);

	}

}
