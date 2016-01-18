package org.jboss.tools.servers.wildfly.swarm.core.internal.server.wst;
/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Fred Bricon - Initial API and implementation 
 ******************************************************************************/


import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.tools.servers.wildfly.swarm.core.internal.MainClassDetector;

/**
 * @author Fred Bricon
 * 
 */
public class WildFlySwarmServerBehaviour extends ControllableServerBehavior  {

	@Override
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor)
			throws CoreException {
		IServer server = ServerUtil.getServer(workingCopy);
		if (server == null) {
			return;
		}
		String projectName = server.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		
		IJavaProject proj = JavaRuntime.getJavaProject(workingCopy);
		Collection<String> mainClasses = MainClassDetector.findMainClasses(proj, monitor);
		if (mainClasses.isEmpty()) {
			return;
		}
		if ( mainClasses.size() > 1) {
			//ohoh
		}
		String mainClass =  mainClasses.iterator().next();
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClass);
		//if m2e project only
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, "org.jboss.tools.servers.wildfly.swarm.launchconfig.classpathProvider");
	}

	@Override
	public IStatus canStart(String launchMode) {
		return Status.OK_STATUS;
	}
	

	@Override
	public IStatus canStop() {
		return Status.OK_STATUS;
	}
	
	@Override
	public void stop(boolean force) {
		ILaunch launch = getServer().getLaunch();
		if (launch != null) {
			try {
				launch.terminate();
			} catch (DebugException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setServerStopped();
	}

}
