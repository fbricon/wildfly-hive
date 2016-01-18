package org.jboss.tools.servers.wildfly.swarm.core.internal;

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;

public class ClasspathChangeListener implements IElementChangedListener {

	private WildlfySwarmDetectionJob detectionJob;

	public ClasspathChangeListener(WildlfySwarmDetectionJob detectionJob) {
		this.detectionJob = detectionJob;
	}

	@Override
	public void elementChanged(ElementChangedEvent event) {
		visit(event.getDelta());
	}

	private void visit(IJavaElementDelta delta) {
		IJavaElement el = delta.getElement();
		switch (el.getElementType()) {
		case IJavaElement.JAVA_MODEL:
			for (IJavaElementDelta c : delta.getAffectedChildren()) {
				visit(c);
			}
			break;
		case IJavaElement.JAVA_PROJECT:
			if (isClasspathChanged(delta.getFlags())) {
				System.err.println(el + " classpath changed");
				detectionJob.analyze((IJavaProject) el);
			}
			break;
		default:
			break;
		}
	}

	private boolean isClasspathChanged(int flags) {
		return 0 != (flags & (IJavaElementDelta.F_CLASSPATH_CHANGED | IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED));
	}

}
