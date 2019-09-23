package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

import de.prob.core.Animator;
import de.prob.core.LimitedLogger;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.PerspectiveFactory;
import de.prob.ui.services.ContextLoadedProvider;

public class StartAnimationHandler extends AbstractHandler implements IHandler {

	public static class ModificationListener implements IResourceChangeListener {

		private final IPath path;

		public ModificationListener(final IFile resource) {
			if (resource == null) {
				path = null;
			} else {
				this.path = resource.getProject().getFullPath();
			}
		}

		public void resourceChanged(final IResourceChangeEvent event) {
			if (path != null) {
				final IResourceDelta delta = event.getDelta();
				IResourceDelta member = delta.findMember(path);
				if (member != null) {
					Animator.getAnimator().setDirty();
				}
			}
		}
	}

	private ISelection fSelection;
	private ModificationListener listener;

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		fSelection = HandlerUtil.getCurrentSelection(event);

		// Get the Selection
		final IEventBRoot rootElement = getRootElement();
		final IFile resource = extractResource(rootElement);

		ArrayList<String> errors = new ArrayList<String>();
		boolean realError = checkErrorMarkers(resource, errors);
		if (!errors.isEmpty()) {
		    Animator.getAnimator().setRodinProjectHasErrorsOrWarnings();
			String message = "Some components in your project contain "
					+ (realError ? "errors" : "warnings")
					+ "!\n This can lead to unexpected behavior (e.g. missing variables) when animating with ProB.\nDetails:\n";
			StringBuffer stringBuffer = new StringBuffer(message);
			for (String string : errors) {
				stringBuffer.append(string);
				stringBuffer.append('\n');
			}
			if (true)
			   // TO DO: use preference whether to pop-up such errors or simply log them
			   // Unfortunately the GetPreferencesCommand gets ProBPreference object which do not contain the current value
			   // we could also change the label in StateViewPart.java, e.g. modelchangeProvider or add a new label
			   Logger.info(stringBuffer.toString());
			else if (realError)
				Logger.notifyUserWithoutBugreport(stringBuffer.toString());
			else
				Logger.notifyUserAboutWarningWithoutBugreport(stringBuffer
						.toString());
		} else {
		     Animator.getAnimator().resetRodinProjectHasErrorsOrWarnings();
		}
		;

		if (resource != null) {
			LimitedLogger.getLogger().log("user started animation",
					rootElement.getElementName(), null);
			registerModificationListener(resource);
			PerspectiveFactory.openPerspective();

			final Animator animator = Animator.getAnimator();
			try {
				LoadEventBModelCommand.load(animator, rootElement);
			} catch (ProBException e) {
				e.notifyUserOnce();
				throw new ExecutionException("Loading the machine failed", e);
			}
		}
		return null;
	}

	private boolean checkErrorMarkers(final IFile resource, List<String> errors) {
		boolean result = false;
		IProject project = resource.getProject();
		try {
			IMarker[] markers = project.findMarkers(
					"org.eclipse.core.resources.problemmarker", true,
					IResource.DEPTH_INFINITE);
			for (IMarker iMarker : markers) {
				errors.add(iMarker.getResource().getName()
						+ ": "
						+ iMarker
								.getAttribute(IMarker.MESSAGE, "unknown Error"));
				result = result
						|| (Integer) iMarker.getAttribute(IMarker.SEVERITY) == IMarker.SEVERITY_ERROR;
			}

		} catch (CoreException e1) {
		}
		return result;
	}

	private IEventBRoot getRootElement() {
		IEventBRoot root = null;
		if (fSelection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) fSelection;
			if (ssel.size() == 1) {
				final Object element = ssel.getFirstElement();
				if (element instanceof IEventBRoot) {
					root = (IEventBRoot) element;
				} else if (element instanceof IFile) {
					IRodinFile rodinFile = RodinCore.valueOf((IFile) element);
					if (rodinFile != null)
						root = (IEventBRoot) rodinFile.getRoot();
				}
			}
		}
		return root;
	}

	private void updateContextLoadedProvider(boolean isContext) {
		ISourceProviderService service = (ISourceProviderService) PlatformUI
				.getWorkbench().getService(ISourceProviderService.class);
		ContextLoadedProvider sourceProvider = (ContextLoadedProvider) service
				.getSourceProvider(ContextLoadedProvider.SERVICE);
		sourceProvider.setEnabled(isContext);
	}

	private IFile extractResource(final IEventBRoot rootElement) {
		IFile resource = null;
		if (rootElement == null) {
			resource = null;
		} else if (rootElement instanceof IMachineRoot) {
			resource = ((IMachineRoot) rootElement).getSCMachineRoot()
					.getResource();
			updateContextLoadedProvider(false);
		} else if (rootElement instanceof IContextRoot) {
			resource = ((IContextRoot) rootElement).getSCContextRoot()
					.getResource();
			updateContextLoadedProvider(true);
		}
		return resource;
	}

	private void registerModificationListener(final IFile resource) {
		if (listener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(
					listener);
		}
		listener = new ModificationListener(resource);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
	}

	public void selectionChanged(final IAction action,
			final ISelection selection) {
		fSelection = selection;
	}

}
