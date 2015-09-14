package info.programmerflow.remote;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class ResourceListener implements IResourceChangeListener {
	public static ResourceListener instance;
	public static void registerListener() {
		if (instance == null) {
			instance = new ResourceListener();
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(instance);
	}
	private ResourceListener() {
	}
	private class ResourceVisitor extends Job implements IResourceDeltaVisitor {
		IResourceDelta change;
		public ResourceVisitor(IResourceDelta d) {
			super("Resource visitor");
			setSystem(true);
			setPriority(Job.DECORATE);
			change = d;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			try {
				if (delta.getAffectedChildren().length == 0) {
					if (delta.getFullPath().getFileExtension() == null) return true;
					if (delta.getFullPath().getFileExtension().equals("class")) return true;
					if (delta.getFullPath().getFileExtension().equals("png")) return true;
					if (delta.getFullPath().getFileExtension().equals("gif")) return true;
					if (delta.getFullPath().getFileExtension().equals("jpg")) return true;
					if (delta.getFullPath().getFileExtension().equals("jpg")) return true;
					if (delta.getFullPath().getFileExtension().equals("jar")) return true;
					StringBuffer b = new StringBuffer();
					int kind = delta.getKind();
					int flags = delta.getFlags();
					if ((kind ^ IResourceDelta.NO_CHANGE) == 0) b.append("no_change ");
					if ((kind ^ IResourceDelta.ADDED) == 0) b.append("added_to_parent ");
					if ((kind ^ IResourceDelta.CHANGED) == 0) b.append("changed ");
					if ((kind ^ IResourceDelta.REMOVED) == 0) b.append("removed ");
					if ((kind ^ IResourceDelta.ADDED_PHANTOM) == 0) b.append("added_phantom ");
					if ((kind ^ IResourceDelta.REMOVED_PHANTOM) == 0) b.append("removed_phantom ");
					if ((kind ^ IResourceDelta.ALL_WITH_PHANTOMS) == 0) b.append("all_with_phantoms ");

					if ((flags ^ IResourceDelta.CONTENT) == 0) b.append("content ");
					if ((flags ^ IResourceDelta.MOVED_FROM) == 0) b.append("moved_from ");
					if ((flags ^ IResourceDelta.MOVED_TO) == 0) b.append("moved_to ");
					if ((flags ^ IResourceDelta.COPIED_FROM) == 0) b.append("copied_from ");
					if ((flags ^ IResourceDelta.OPEN) == 0) b.append("open ");
					if ((flags ^ IResourceDelta.TYPE) == 0) b.append("type ");
					if ((flags ^ IResourceDelta.SYNC) == 0) b.append("sync ");
					if ((flags ^ IResourceDelta.MARKERS) == 0) b.append("markers ");
					if ((flags ^ IResourceDelta.REPLACED) == 0) b.append("replaced ");
					if ((flags ^ IResourceDelta.DESCRIPTION) == 0) b.append("description ");
					if ((flags ^ IResourceDelta.ENCODING) == 0) b.append("encoding ");

					PFISPlugin.log("Resource delta", b.toString(),delta.getFullPath().toString());
				}
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
			return true;
		}

		@Override
		protected IStatus run(IProgressMonitor arg0) {
			try {
				if (change != null) change.accept(this);
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
			return Status.OK_STATUS;
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			new ResourceVisitor(event.getDelta()).schedule(200);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
