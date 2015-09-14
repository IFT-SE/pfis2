package info.programmerflow.remote;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
/**
 * Handle package explorer events.
 * @author jalawran
 *
 */
public class PackageExplorerListener implements ITreeViewerListener {
	private static PackageExplorerListener instance;

	public static void registerListener(IPackagesViewPart p) {
		if (instance == null) {
			instance = new PackageExplorerListener();
			new PackageExplorerLister().schedule(15000);
		}
		p.getTreeViewer().addTreeListener(instance);
	}
	private PackageExplorerListener() {
	}
	
	private static class PackageExplorerLister extends Job {
		private IJavaElement model;
		public PackageExplorerLister() {
			super("Package Explorer Lister");
			setSystem(true);
			setPriority(Job.DECORATE);
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			model = JavaCore.create(root);
		}

		private void logAll(IJavaElement e) throws Exception {
			if (e instanceof JarPackageFragmentRoot) return;
			if (e instanceof IType) return;
			if (e instanceof IImportContainer) return;
			if (e instanceof IParent) {
				IParent parent = (IParent) e;
				PFISPlugin.logOnce("Package Explorer tree", e.getClass().toString(), e.getPath().toPortableString()); //, e.getElementName());
				for (IJavaElement child : parent.getChildren()) {
					logAll(child);
					Thread.sleep(10);
				}
			}
		}

		@Override
		protected IStatus run(IProgressMonitor arg0) {
			try {
				logAll(model);
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
			return Status.OK_STATUS;
		}
		
	}
	public void treeCollapsed(TreeExpansionEvent event) {
		try {
			PFISPlugin.log("Package Explorer tree collapsed", event.getElement().getClass().getName(), ((IJavaElement)event.getElement()).getPath().toPortableString());
		} catch (Exception e) {
			PFISPlugin.logException(e);
		}
	}
	public void treeExpanded(TreeExpansionEvent event) {
		try {
			PFISPlugin.log("Package Explorer tree expanded", event.getElement().getClass().getName(), ((IJavaElement)event.getElement()).getPath().toPortableString());
		} catch (Exception e) {
			PFISPlugin.logException(e);
		}
	}
}
