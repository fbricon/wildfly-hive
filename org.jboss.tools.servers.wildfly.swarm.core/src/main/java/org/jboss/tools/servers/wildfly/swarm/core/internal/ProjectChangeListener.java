/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc. Distributed under license by Red Hat, Inc. All rights reserved. This program is made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html Contributors: Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.servers.wildfly.swarm.core.internal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.m2e.core.project.IMavenProjectChangedListener;
import org.eclipse.m2e.core.project.MavenProjectChangedEvent;

public class ProjectChangeListener implements IElementChangedListener, IMavenProjectChangedListener {

	private WildlfySwarmDetectionJob detectionJob;

	public ProjectChangeListener(WildlfySwarmDetectionJob detectionJob) {
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
				IJavaProject javaProject = (IJavaProject) el;
				if (isClasspathChanged(delta)) {
					detectionJob.analyze(javaProject);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void mavenProjectChanged(MavenProjectChangedEvent[] events, IProgressMonitor monitor) {
		for (MavenProjectChangedEvent event : events) {
			if (event.getKind() == MavenProjectChangedEvent.KIND_ADDED) {
				String packaging = event.getMavenProject().getPackaging();

				if ("jar".equals(packaging) || "war".equals(packaging)) {
					detectionJob.analyze(JavaCore.create(event.getMavenProject().getProject()));
				}
			}
		}
	}

	private boolean isClasspathChanged(IJavaElementDelta delta) {
		return delta.getKind() == IJavaElementDelta.REMOVED
				|| (0 != (delta.getFlags() & (IJavaElementDelta.F_CLASSPATH_CHANGED | IJavaElementDelta.F_RESOLVED_CLASSPATH_CHANGED)));
	}

}
