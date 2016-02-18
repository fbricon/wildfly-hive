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
package org.jboss.tools.servers.wildfly.swarm.core.internal.server;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.ide.eclipse.as.core.util.ServerNamingUtility;

public class WildFlySwarmServerHelper {

	private static final String SERVER_TYPE = "org.jboss.tools.wildfly.swarm.serverType";

	private WildFlySwarmServerHelper() {
	}

	public static IServer createServer(IJavaProject javaProject, String mainClass, IProgressMonitor monitor) throws CoreException {
		String projectName = javaProject.getProject().getName();
		//System.err.println("Creating wildfly server for project " + projectName+ " with main class " + mainClass);
		IServerType type = ServerCore.findServerType(SERVER_TYPE);
		String suffixed = ServerNamingUtility.getDefaultServerName("WildFly Swarm - "+javaProject.getProject().getName());
		IServerWorkingCopy wc = type.createServer(suffixed, null, new NullProgressMonitor());
		wc.setName(suffixed);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClass);
		IServer server = wc.save(true, monitor);
		return server;
	}
	
	public static IServer findWildflySwarmServer(IJavaProject p, IProgressMonitor monitor) {
		// TODO Find Wildlfy Swarm Server runtimes bound to that project
		IServer[] servers = ServerCore.getServers();
		if (servers.length == 0) {
			return null;
		}
		return Arrays.stream(servers)
					 .filter(s -> s.getName().endsWith(p.getProject().getName()))
					 .findFirst().orElse(null);
	}
	
}
