/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.servers.wildfly.swarm.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.server.core.IServer;
import org.jboss.tools.servers.wildfly.swarm.core.internal.server.WildFlySwarmServerHelper;

public class WildlfySwarmDetectionJob extends Job {

	private static final long SCHEDULE_DELAY = 1000L;

	private final Set<IJavaProject> queue = new LinkedHashSet<>();

	public WildlfySwarmDetectionJob() {
		super("Widlfy Swarm detection Job");
	}

	public void analyze(IJavaProject javaProject) {
		queue(javaProject);
		schedule(SCHEDULE_DELAY);
	}

	private void queue(IJavaProject javaProject) {
		synchronized (queue) {
			queue.add(javaProject);
		}
	}

	public boolean hasWildFlySwarmDependency(IJavaProject javaProject) {
		return false;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Detecting Wildfly Swarm projects", IProgressMonitor.UNKNOWN);
		final ArrayList<IJavaProject> projects;
		synchronized (this.queue) {
			projects = new ArrayList<>(this.queue);
			this.queue.clear();
		}
		projects.forEach(p -> swarmify(p, monitor));
		if (!queue.isEmpty()) {
			schedule(SCHEDULE_DELAY);
		}
		return Status.OK_STATUS;
	}

	private void swarmify(IJavaProject p, IProgressMonitor monitor) {
		//System.err.println("analyzing " + p.getProject());
		if (isWildflySwarmProject(p)) {
			//System.err.println(p.getProject() + " is probably a wildfly swarm project");
			IServer server = WildFlySwarmServerHelper.findWildflySwarmServer(p, monitor);
			if (server == null) {
				try {
					createServer(p, monitor);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		} else {
			IServer server = WildFlySwarmServerHelper.findWildflySwarmServer(p, monitor);
			if (server != null) {
				try {
					server.delete();
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void createServer(IJavaProject p, IProgressMonitor monitor) throws CoreException {
		Collection<String> mainClasses = findMainClass(p, monitor);
		if (mainClasses.isEmpty()) {
			return;
		}
		if ( mainClasses.size() > 1) {
			//ohoh
		}
		String mainClass =  mainClasses.iterator().next();
		WildFlySwarmServerHelper.createServer(p, mainClass, monitor);
	}

	private Collection<String> findMainClass(IJavaProject p, IProgressMonitor monitor) {
		return MainClassDetector.findMainClasses(p, monitor);
	}

	private boolean isWildflySwarmProject(IJavaProject p) {
		return p.isOpen() && hasInClassPath(p, "org.wildfly.swarm.container.Container");
	}

	private boolean hasInClassPath(IJavaProject project, String className) {
		try {
			return project.findType(className) != null;
		} catch (JavaModelException ex) {
			// Ignore this
		}
		return false;
	}

}