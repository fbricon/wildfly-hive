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
package org.jboss.tools.servers.wildfly.swarm.core.internal.server.wst;

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
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.tools.servers.wildfly.swarm.core.internal.MainClassDetector;
import org.jboss.tools.servers.wildfly.swarm.core.internal.SocketUtil;

/**
 * Handles WildFly Swarm server instances' behavior
 * 
 * @author Fred Bricon
 */
public class WildFlySwarmServerBehavior extends ControllableServerBehavior  {

	private static final String DEVAULT_VM_ARGS = " -noverify -server -Xms512m -Xmx512m -Djava.net.preferIPv4Stack=true -Dswarm.bind.address=127.0.0.1 ";

	@Override
	public void setupLaunchConfiguration(ILaunchConfigurationWorkingCopy workingCopy, IProgressMonitor monitor)
			throws CoreException {
		IServer server = ServerUtil.getServer(workingCopy);
		if (server == null) {
			return;
		}
		
		String projectName = workingCopy.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		if (projectName == null) {
			projectName = server.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		}
		IJavaProject proj = JavaRuntime.getJavaProject(workingCopy);
		Collection<String> mainClasses = MainClassDetector.findMainClasses(proj, monitor);
		if (mainClasses.isEmpty()) {
			return;
		}
		if ( mainClasses.size() > 1) {
			//TODO handle multiple Main classes 
		}
		String mainClass =  mainClasses.iterator().next();
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClass);
		
        StringBuilder vmArgs = new StringBuilder(DEVAULT_VM_ARGS);
        int targetPort = 8080;
		int portOffset = SocketUtil.detectPortOffset(targetPort);
		if (portOffset > 0) {
     	   targetPort += portOffset;
           vmArgs.append(" -Dswarm.port.offset=").append(portOffset);
		}
		workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs.toString());
	
		final ControllableServerBehavior behavior = (ControllableServerBehavior)JBossServerBehaviorUtils.getControllableBehavior(server);
		//TODO parse Main class AST to detect default context root?
		if (behavior != null) {
			//XXX seems weird/wrong
			behavior.putSharedData("welcomePage", "http://localhost:"+targetPort+"/");
		}
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
	public void setServerStarting() {
		super.setServerStarting();
		setServerPublishState(IServer.PUBLISH_STATE_UNKNOWN);
	}
	
	@Override
	public void stop(boolean force) {
		ILaunch launch = getServer().getLaunch();
		if (launch != null) {
			try {
				launch.terminate();
			} catch (DebugException e) {
				e.printStackTrace();
			}
		}
		setServerStopped();
	}

	
}
