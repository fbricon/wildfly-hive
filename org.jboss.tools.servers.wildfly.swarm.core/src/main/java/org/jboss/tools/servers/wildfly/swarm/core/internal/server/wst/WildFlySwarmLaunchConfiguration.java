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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.internal.launching.environments.EnvironmentsManager;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.internal.Server;
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.tools.servers.wildfly.swarm.core.internal.CoreActivator;

/**
 * @author Fred Bricon
 * 
 */
public class WildFlySwarmLaunchConfiguration extends JavaLaunchDelegate {

	private static final String TERMINATE_LISTENER = "TERMINATE_LISTENER";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		final ControllableServerBehavior behavior = (ControllableServerBehavior)JBossServerBehaviorUtils.getControllableBehavior(configuration);
		if (behavior == null) {
			throw new CoreException(new Status(IStatus.ERROR, CoreActivator.PLUGIN_ID, "Unable to start wildfly swarm process, ControllableServerBehavior is missing"));
		}
		try {
			behavior.setServerStarting();
			behavior.setRunMode(mode);
			super.launch(configuration, mode, launch, monitor);
			List<IProcess> processes = Arrays.asList(launch.getProcesses());
			IDebugEventSetListener terminateListener = (events) -> {
				if (events != null) {
					Optional<DebugEvent> terminateEvent = Stream.of(events)
																.filter(e -> processes.contains(e.getSource()) 
																		&& e.getKind() == DebugEvent.TERMINATE)
																.findFirst();
					if (terminateEvent.isPresent()) {
						stopServer(behavior);
					}
				}
			};
			behavior.putSharedData(TERMINATE_LISTENER, terminateListener);
			DebugPlugin.getDefault().addDebugEventListener(terminateListener );
			behavior.setServerStarted();
			((Server)behavior.getServer()).setServerPublishState(IServer.PUBLISH_STATE_NONE);
		} catch (Exception e ) {
			stopServer(behavior);
			if (e instanceof CoreException) {
				throw (CoreException)e;
			}
			throw new CoreException(new Status(IStatus.ERROR, CoreActivator.PLUGIN_ID, "Unable to start wildfly swarm process", e));
		}
	}
	
	protected void stopServer(ControllableServerBehavior behavior) {
		behavior.setServerStopped();
		IDebugEventSetListener terminateListener = (IDebugEventSetListener) behavior.getSharedData(TERMINATE_LISTENER);
		if (terminateListener != null) {
			DebugPlugin.getDefault().removeDebugEventListener(terminateListener);
			behavior.putSharedData(TERMINATE_LISTENER, null);
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
