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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.launching.environments.EnvironmentsManager;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.tools.servers.wildfly.swarm.core.internal.Activator;

/**
 * @author Fred Bricon
 * 
 */
public class WildFlySwarmLaunchConfiguration extends JavaLaunchDelegate {

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		final ControllableServerBehavior behavior = (ControllableServerBehavior)JBossServerBehaviorUtils.getControllableBehavior(configuration);
		if (behavior == null) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to start wildfly swarm process, ControllableServerBehavior is missing"));
		}
		try {
			behavior.setServerStarting();
			behavior.setRunMode(mode);
			super.launch(configuration, mode, launch, monitor);
			behavior.setServerStarted();
		} catch (Exception e ) {
			behavior.setServerStopped();
			if (e instanceof CoreException) {
				throw (CoreException)e;
			}
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Unable to start wildfly swarm process", e));
		}
	}
	
	@Override
	public IVMInstall verifyVMInstall(ILaunchConfiguration configuration) throws CoreException {
		IVMInstall vm = super.verifyVMInstall(configuration);
		IVMInstall compatibleVM = null;
		IExecutionEnvironment env = EnvironmentsManager.getDefault().getEnvironment("JavaSE-1.8");
		List<IVMInstall> vms = Arrays.asList(env.getCompatibleVMs());
		if (vms.isEmpty() || vms.contains(vm)) {
			compatibleVM = vm;
		} else {
			compatibleVM = vms.get(0);
		}
		return compatibleVM;
	}

}
