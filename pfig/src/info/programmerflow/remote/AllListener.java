package info.programmerflow.remote;


import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.IExpressionsListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.IMemoryBlockListener;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.search.JavaSearchQuery;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResultListener;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.SearchResultEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jdt.ui.IPackagesViewPart;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.PatternQuerySpecification;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

public class AllListener extends Util  {
	// Static/Singleton listeners
	private static AllListener instance;
	private static BreakpointListener breakpointListener;
	private static DebugEventSetListener debugEventSetListener;
	private static ExpressionListener expressionListener;
	private static LaunchListener launchListener;
	private static MemoryBlockListener memoryBlockListener;
	private static ElementChangedListener elementChangedListener;
	private static WindowListener windowListener;
	private static SelectionListener selectionListener;
	private static PropertyChangeListener propertyChangeListener;
	private static PartListener partListener;
	private static PerspectiveListener perspectiveListener;
	private static PageListener pageListener;
	private static QueryListener queryListener;

	public static AllListener getInstance() {
		if (instance == null) {
			instance = new AllListener();
		}
		return instance;
	}
	private AllListener() {
		breakpointListener = new BreakpointListener();
		debugEventSetListener = new DebugEventSetListener();
		expressionListener = new ExpressionListener();
		launchListener = new LaunchListener();
		memoryBlockListener = new MemoryBlockListener();
		elementChangedListener = new ElementChangedListener();
		windowListener = new WindowListener();
		selectionListener = new SelectionListener();
		propertyChangeListener = new PropertyChangeListener();
		partListener = new PartListener();
		perspectiveListener = new PerspectiveListener();
		pageListener = new PageListener();
		queryListener = new QueryListener();
	}
	// Listener classes
	// TODO: FIXME: breakpoint, debugeventset, expression, console (in launch) listeners need tweaking
	private class BreakpointListener implements IBreakpointsListener {
		public void breakpointsAdded(IBreakpoint[] arg0) {
			for (IBreakpoint b : arg0) {
				try {
					PFISPlugin.log("Breakpoint added", b.getMarker().toString(), b.toString());
					PFISPlugin.log("Breakpoint added", b.getMarker().getType(), b.getMarker().toString());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}
		public void breakpointsChanged(IBreakpoint[] arg0,
				IMarkerDelta[] arg1) {
			for (IBreakpoint b : arg0) {
				try {
					PFISPlugin.log("Breakpoint changed", b.getModelIdentifier(), b.toString());
					PFISPlugin.log("Breakpoint changed", b.getMarker().getType(), b.getMarker().toString());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}
		public void breakpointsRemoved(IBreakpoint[] arg0,
				IMarkerDelta[] arg1) {
			for (IBreakpoint b : arg0) {
				try {
					PFISPlugin.log("Breakpoint changed", b.getModelIdentifier(), b.toString());
					PFISPlugin.log("Breakpoint changed", b.getMarker().getType(), b.getMarker().toString());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}
	}
	private class DebugEventSetListener implements IDebugEventSetListener {
		public void handleDebugEvents(DebugEvent[] arg0) {
			for (DebugEvent e : arg0) {
				String kind ="";
				String detail="";
				switch (e.getKind()) {
				case DebugEvent.CHANGE:
					kind = "change";
					break;
				case DebugEvent.CREATE:
					kind = "create";
					break;
				case DebugEvent.MODEL_SPECIFIC:
					kind = "model specific";
					break;
				case DebugEvent.RESUME:
					kind = "resume";
					break;
				case DebugEvent.SUSPEND: 
					kind = "suspend";
					break;
				case DebugEvent.TERMINATE: 
					kind = "terminate";
					break;
				case DebugEvent.UNSPECIFIED: 
					kind = "unspecified";
					break;
				}
				switch (e.getDetail()) {
				case DebugEvent.BREAKPOINT:
					detail = "breakpoint";
					break;
				case DebugEvent.CLIENT_REQUEST:
					detail = "client request";
					break;
				case DebugEvent.CONTENT: 
					detail = "content";
					break;
				case DebugEvent.EVALUATION: 
					detail = "evaluation";
					break;
				case DebugEvent.EVALUATION_IMPLICIT: 
					detail = "evaluation implicit";
					break;
				case DebugEvent.STATE: 
					detail = "state";
					break;
				case DebugEvent.STEP_END: 
					detail = "step end";
					break;
				case DebugEvent.STEP_INTO: 
					detail = "step into";
					break;
				case DebugEvent.STEP_OVER:
					detail = "step over";
					break;
				case DebugEvent.STEP_RETURN: 
					detail = "step return";
					break;
				}
				try {
					PFISPlugin.log("Debug event", e.toString(), kind + " " + detail);
				} catch (Exception g) {
					PFISPlugin.logException(g);
				}
			}
		}
	}
	private class ExpressionListener implements IExpressionsListener {
		public void expressionsAdded(IExpression[] arg0) {
			for (IExpression exp : arg0) {
				try {
					PFISPlugin.log("Expression added", exp.toString(), exp.getExpressionText());
					PFISPlugin.log("Expression added", exp.toString(), exp.getModelIdentifier());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}
		public void expressionsChanged(IExpression[] arg0) {
			for (IExpression exp : arg0) {
				try {
					PFISPlugin.log("Expression changed", exp.toString(), exp.getExpressionText());
					PFISPlugin.log("Expression changed", exp.toString(), exp.getModelIdentifier());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}
		public void expressionsRemoved(IExpression[] arg0) {
			for (IExpression exp : arg0) {
				try {
					PFISPlugin.log("Expression removed", exp.toString(), exp.getExpressionText());
					PFISPlugin.log("Expression removed", exp.toString(), exp.getModelIdentifier());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}
	}
	private class LaunchListener implements ILaunchesListener2 {
		private void launchListener(ILaunch[] arg0, String type) {
			for (ILaunch l : arg0) {
				for (IProcess p : l.getProcesses()) {
					p.getStreamsProxy().getErrorStreamMonitor().addListener(new IStreamListener() {

						public void streamAppended(String arg0, IStreamMonitor arg1) {
							try {
								PFISPlugin.log("Console error stream", arg0, arg1.getContents());
							} catch (Exception e) {
								PFISPlugin.logException(e);
							}
						}

					});
					p.getStreamsProxy().getOutputStreamMonitor().addListener(new IStreamListener() {
						public void streamAppended(String arg0, IStreamMonitor arg1) {
							try {
								PFISPlugin.log("Console output stream", arg0, arg1.getContents());
							} catch (Exception e) {
								PFISPlugin.logException(e);
							}
						}					
					});
				}
				try {
					PFISPlugin.log("Launch " + type + "(path)", l.getLaunchConfiguration().toString(), l.getLaunchConfiguration().getLocation().toString());
					PFISPlugin.log("Launch " + type + " (mode)", l.getLaunchConfiguration().toString(), l.getLaunchMode());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}
		public void launchesAdded(ILaunch[] arg0) {
			launchListener(arg0, "added");
		}
		public void launchesChanged(ILaunch[] arg0) {
			launchListener(arg0, "changed");
		}
		public void launchesRemoved(ILaunch[] arg0) {
			launchListener(arg0, "removed");
		}
		public void launchesTerminated(ILaunch[] arg0) {
			launchListener(arg0, "terminated");
		}		
	}
	private class MemoryBlockListener implements IMemoryBlockListener {

		public void memoryBlocksAdded(IMemoryBlock[] arg0) {
			for (IMemoryBlock b : arg0) {
				try {
					PFISPlugin.log("Memory block added", b.getModelIdentifier(), b.toString());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}

			}
		}

		public void memoryBlocksRemoved(IMemoryBlock[] arg0) {
			for (IMemoryBlock b : arg0) {
				try {
					PFISPlugin.log("Memory block removed", b.getModelIdentifier(), b.toString());
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
		}

	}
	private class ElementChangedListener implements IElementChangedListener {
		public void elementChanged(ElementChangedEvent event) {
			try {
				IJavaElementDelta delta = event.getDelta();
				StringBuffer b = new StringBuffer();
				b.append("Java element ");

				switch(delta.getKind()) {
				case IJavaElementDelta.CHANGED:
					b.append("changed ");
					int f = delta.getFlags();
					if ((f & IJavaElementDelta.F_AST_AFFECTED) != 0) b.append("ast_affected ");
					if ((f & IJavaElementDelta.F_FINE_GRAINED) != 0) b.append("fine_grained ");
					if ((f & IJavaElementDelta.F_CONTENT) != 0) b.append("content ");
					if ((f & IJavaElementDelta.F_CHILDREN) != 0) b.append("children ");
					break;
				case IJavaElementDelta.ADDED:
					b.append("added ");
					break;
				case IJavaElementDelta.REMOVED:
					b.append("removed ");
					break;
				}
				PFISPlugin.log(b.toString(), delta.getElement().getPath().toPortableString(), delta.getElement().getElementName());
				SourceLogger.logEvent(event);
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}		
	}
	private class WindowListener implements IWindowListener {
		public void windowActivated(IWorkbenchWindow window) {
			try {
				PFISPlugin.log("Window activated", window.getActivePage().getActivePart().getTitle(), window.getActivePage().getActivePart().getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void windowClosed(IWorkbenchWindow window) {
			try {
				PFISPlugin.log("Window closed", "","");
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}

		}
		public void windowDeactivated(IWorkbenchWindow window) {
			try {
				PFISPlugin.log("Window deactivated", window.getActivePage().getActivePart().getTitle(), window.getActivePage().getActivePart().getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void windowOpened(IWorkbenchWindow window) {
			try {
				registerListeners();
				PFISPlugin.log("Window opened", window.getActivePage().getActivePart().getTitle(), window.getActivePage().getActivePart().getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}		
	}
	private class SelectionListener implements ISelectionListener {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			try {
				if (selection instanceof TextSelection) {
					TextSelection s = (TextSelection) selection;
					PFISPlugin.log("Text selection offset", part.getTitleToolTip(), ""+s.getOffset());
					if (s.getLength() > 0) {
						PFISPlugin.log("Text selection", part.getTitleToolTip(), s.getText());
					}
				}
				else if (selection instanceof TreeSelection) {
					TreeSelection s = (TreeSelection) selection;
					PFISPlugin.log(part.getTitle() + " tree selection", s.toString(), "");
				}
				else if (selection instanceof StructuredSelection) {
					StructuredSelection s = (StructuredSelection) selection;
					PFISPlugin.log(part.getTitle() + " structured selection", s.toString(), "");
				}
				else {
					PFISPlugin.log(part.getTitle() + " selection", selection.toString(), "");
				}
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
	}
	private class PropertyChangeListener implements IPropertyChangeListener  {
		public void propertyChange(PropertyChangeEvent event) {
			try {
				PFISPlugin.log(event.getProperty() + " changed", event.getOldValue().toString(), event.getNewValue().toString());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}		
	}
	private class PartListener implements IPartListener {
		public void partActivated(IWorkbenchPart part) {
			try {
				PFISPlugin.log("Part activated", part.getTitle(), part.getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void partBroughtToTop(IWorkbenchPart part) {
			try {
				PFISPlugin.log("Brought to top", part.getTitle(), part.getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void partClosed(IWorkbenchPart part) {
			try {
			PFISPlugin.log("Part closed", part.getTitle(), part.getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void partDeactivated(IWorkbenchPart part) {
			try {
			PFISPlugin.log("Part deactivated", part.getTitle(), part.getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}

		}
		public void partOpened(IWorkbenchPart part) {
			try {
			PFISPlugin.log("Part opened", part.getTitle(), part.getTitleToolTip());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}		
	}
	private class PerspectiveListener implements IPerspectiveListener {
		public void perspectiveActivated(IWorkbenchPage arg0, IPerspectiveDescriptor arg1) {
			try {
			PFISPlugin.log("Perspective activated", arg0.getLabel(), arg1.getLabel());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}

		}

		public void perspectiveChanged(IWorkbenchPage arg0, IPerspectiveDescriptor arg1, String arg2) {
			try {
			PFISPlugin.log("Perspective changed", arg0.getLabel(), arg1.getLabel());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}

	}
	private class PageListener implements IPageListener {
		public void pageActivated(IWorkbenchPage arg0) {
			try {
			PFISPlugin.log("Page activated", arg0.getLabel(), arg0.toString());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void pageClosed(IWorkbenchPage arg0) {
			try {
			PFISPlugin.log("Page closed", arg0.getLabel(), arg0.toString());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void pageOpened(IWorkbenchPage arg0) {
			try {
			PFISPlugin.log("Page opened", arg0.getLabel(), arg0.toString());
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
	}
	private class QueryListener implements IQueryListener {
		public void queryAdded(ISearchQuery query) {
			try {
				query.getSearchResult().addListener(new ISearchResultListener() {
					public void searchResultChanged(SearchResultEvent event) {
						PFISPlugin.log("Search result", event.getSearchResult().getLabel(),event.getSearchResult().getTooltip());
					}							
				});
				// This is the only way AFAIK to get the query used in a Java search.
				// I call private methods of the query using Java reflection.
				if (query instanceof JavaSearchQuery) {
					JavaSearchQuery q = (JavaSearchQuery) query;
					QuerySpecification s = (QuerySpecification) call(q,"getSpecification");
					if (s != null) {
						if (s instanceof PatternQuerySpecification) {
							PatternQuerySpecification ps = (PatternQuerySpecification) s;
							PFISPlugin.log("Java search", s.getScopeDescription(), ps.getPattern());
						}
						else if (s instanceof ElementQuerySpecification) {
							ElementQuerySpecification es = (ElementQuerySpecification) s;
							PFISPlugin.log("Java search", s.getScopeDescription(), es.getElement().getElementName());
						}
					}
				}
				else if (query instanceof FileSearchQuery) {
					FileSearchQuery q = (FileSearchQuery)query;
					PFISPlugin.log("File Search", q.getLabel(), q.getSearchString());
				}
				else {
					PFISPlugin.log("Search",query.toString(), "");			
				}
			} catch (Exception e) {
				PFISPlugin.logException(e);
			}
		}
		public void queryFinished(ISearchQuery query) {
		}
		public void queryRemoved(ISearchQuery query) {
		}
		public void queryStarting(ISearchQuery query) {
		}
	}
	public void registerListeners() {
		try {
			/**  Register all listeners  */
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(breakpointListener);
			DebugPlugin.getDefault().addDebugEventListener(debugEventSetListener);
			DebugPlugin.getDefault().getExpressionManager().addExpressionListener(expressionListener);
			DebugPlugin.getDefault().getMemoryBlockManager().addListener(memoryBlockListener);
			DebugPlugin.getDefault().getLaunchManager().addLaunchListener(launchListener);
			JavaCore.addElementChangedListener(elementChangedListener);
			PlatformUI.getWorkbench().addWindowListener(windowListener);
			NewSearchUI.addQueryListener(queryListener);
			for (IWorkbenchWindow activeWindow : PlatformUI.getWorkbench().getWorkbenchWindows()) {
				activeWindow.addPerspectiveListener(perspectiveListener);
				IWorkbenchPage[] activePages = activeWindow.getPages();
				activeWindow.addPageListener(pageListener);
				for (IWorkbenchPage activePage: activePages) {
					activePage.findView(JavaUI.ID_CU_EDITOR);
					PackageExplorerListener.registerListener((IPackagesViewPart)activePage.findView(JavaUI.ID_PACKAGES));
					activePage.addPostSelectionListener(selectionListener);
					activePage.addPropertyChangeListener(propertyChangeListener);
					activePage.addSelectionListener(selectionListener);
					activePage.addPartListener(partListener);
				}
			}

			// Experimentation from this point on
			/*
			DebugPlugin.getDefault().getLog().addLogListener(new ILogListener() {
				public void logging(IStatus arg0, String arg1) {
					PFISPlugin.logOrException("Debug log", arg0.getMessage(), arg1);
				}				
			});
			PlatformUI.getWorkbench().getDisplay().getActiveShell(). //(lots of listeners)
			PlatformUI.getWorkbench().getDisplay().addListener(SWT.KeyDown, new Listener() {
				public void handleEvent(Event arg0) {
				}
			});
			 */
		} catch (Exception e) {
			PFISPlugin.logException(e);
		}
	}

}


