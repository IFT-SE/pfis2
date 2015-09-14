package info.programmerflow.remote;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * Log information about the source code without transmitting actual source code.
 * @author jalawran
 *
 */
public class SourceLogger extends Job {
	private static SourceLogger instance = null;
	private ConcurrentLinkedQueue<IJavaElement> queue;
	protected CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit);
		parser.setResolveBindings(true);
		return (CompilationUnit) parser.createAST(null);
	}
	public static void logEvent(ElementChangedEvent event) {
		if (instance == null) {
			instance = new SourceLogger();
			instance.schedule();
		}
		instance.queue.add(event.getDelta().getElement());
	}

	private SourceLogger() {
		super("Source logger");
		setSystem(true);
		queue = new ConcurrentLinkedQueue<IJavaElement>();
		setPriority(Job.DECORATE);
	}

	private class SourceUtil extends Util {
		private class SourceVisitor extends ASTVisitor {
			private Stack<String> stack = new Stack<String>();

			public SourceVisitor() {
				stack.push(getName());
			}
			public boolean visit(PackageDeclaration node) {
				try {
					PFISPlugin.logOnce("Package offset", node.resolveBinding().getKey(),""+node.getStartPosition());
					PFISPlugin.logOnce("Package length", node.resolveBinding().getKey(),""+node.getLength());
					PFISPlugin.logOnce("Package", stack.peek(), node.resolveBinding().getKey());
				} catch (Exception e) {
					PFISPlugin.logException(e);
					return false;
				}
				return true;				
			}
			
			public boolean visit(ImportDeclaration node) {
				try {
					PFISPlugin.logOnce("Imports offset", node.resolveBinding().getKey(),""+node.getStartPosition());
					PFISPlugin.logOnce("Imports length", node.resolveBinding().getKey(),""+node.getLength());
					PFISPlugin.logOnce("Imports", stack.peek(), node.resolveBinding().getKey());
				} catch (Exception e) {
					PFISPlugin.logException(e);
					return false;
				}
				return true;
			}
			public boolean visit(TypeDeclaration node) {
				try {
					stack.push(node.resolveBinding().getKey());
					if (node.getSuperclassType() != null)
						PFISPlugin.logOnce("Extends", stack.peek(), node.getSuperclassType().resolveBinding().getKey());
					for (Object type : node.superInterfaceTypes()) {
						PFISPlugin.logOnce("Implements", stack.peek(), ((Type)type).resolveBinding().getKey());
					}
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
				return true;
			}
			public void endVisit(TypeDeclaration node) {
				try {
					stack.pop();
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
			public boolean visit(MethodDeclaration node) {
				try {
					PFISPlugin.logOnce("Method declaration", stack.peek(), node.resolveBinding().getKey());
					PFISPlugin.logOnce("Method declaration offset", node.resolveBinding().getKey(),""+node.getStartPosition());
					PFISPlugin.logOnce("Method declaration length", node.resolveBinding().getKey(),""+node.getLength());
					PFISPlugin.logOnce("Method declaration scent", node.resolveBinding().getKey(), node.toString());
					stack.push(node.resolveBinding().getKey());
				} catch (Exception e) {
					PFISPlugin.logException(e);
					return false;
				}
				return true;
			}
			public void endVisit(MethodDeclaration node) {
				try {
					stack.pop();
				} catch (Exception e) {
					PFISPlugin.logException(e);
				}
			}
			public boolean visit(ConstructorInvocation node) {
				try {
					PFISPlugin.logOnce("Constructor invocation", stack.peek(), node.resolveConstructorBinding().getKey());
					PFISPlugin.logOnce("Constructor invocation offset", node.resolveConstructorBinding().getKey(),""+node.getStartPosition());
					PFISPlugin.logOnce("Constructor invocation length", node.resolveConstructorBinding().getKey(),""+node.getLength());
					PFISPlugin.logOnce("Constructor invocation scent", node.resolveConstructorBinding().getKey(), node.toString());
				} catch (Exception e) {
					PFISPlugin.logException(e);
					return false;
				}
				return true;
			}
			public boolean visit(MethodInvocation node) {
				try {
					PFISPlugin.logOnce("Method invocation", stack.peek(), node.resolveMethodBinding().getKey());
					PFISPlugin.logOnce("Method invocation offset", node.resolveMethodBinding().getKey(),""+node.getStartPosition());
					PFISPlugin.logOnce("Method invocation length", node.resolveMethodBinding().getKey(),""+node.getLength());
					PFISPlugin.logOnce("Method invocation scent", node.resolveMethodBinding().getKey(), node.toString());
				} catch (Exception e) {
					PFISPlugin.logException(e);
					return false;
				}
				return true;
			}
			public boolean visit(VariableDeclarationFragment node) {
				try {
					PFISPlugin.logOnce("Variable declaration", stack.peek(), node.resolveBinding().getKey());
					PFISPlugin.logOnce("Variable declaration offset", node.resolveBinding().getKey(),""+node.getStartPosition());
					PFISPlugin.logOnce("Variable declaration length", node.resolveBinding().getKey(),""+node.getLength());
					PFISPlugin.logOnce("Variable type", node.resolveBinding().getKey(), ((Type)call(node.getParent(),"getType")).resolveBinding().getKey());
				} catch (Exception e) {
					PFISPlugin.logException(e);
					return false;
				}
				return true;
			}
			public boolean visit(SingleVariableDeclaration node) {
				try {
					PFISPlugin.logOnce("Variable declaration", stack.peek(), node.resolveBinding().getKey());
					PFISPlugin.logOnce("Variable declaration offset", node.resolveBinding().getKey(),""+node.getStartPosition());
					PFISPlugin.logOnce("Variable declaration length", node.resolveBinding().getKey(),""+node.getLength());
					PFISPlugin.logOnce("Variable type", node.resolveBinding().getKey(), node.getType().resolveBinding().getKey());
				} catch (Exception e) {
					PFISPlugin.logException(e);
					return false;
				}
				return true;
			}
		}
	}

	protected IStatus run(IProgressMonitor arg0) {
		if (!queue.isEmpty()) {

			IJavaElement element = queue.remove();
			IResource resource = element.getResource();
			setName(element.getPath().toOSString());
			if (resource instanceof IFile) {
				ICompilationUnit unit = JavaCore.createCompilationUnitFrom((IFile) resource);
				ASTNode ast = parse(unit);
				ast.accept(new SourceUtil().new SourceVisitor());
			}			
		}
		schedule(3000);

		return Status.OK_STATUS;
	}

}
