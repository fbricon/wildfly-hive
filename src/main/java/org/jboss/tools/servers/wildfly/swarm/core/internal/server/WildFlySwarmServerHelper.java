package org.jboss.tools.servers.wildfly.swarm.core.internal.server;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.launching.environments.EnvironmentsManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.ui.internal.progress.FinishedJobs;
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
		System.err.println("Creating wildfly server for project " + projectName+ " with main class " + mainClass);
		IServerType type = ServerCore.findServerType(SERVER_TYPE);
		String suffixed = ServerNamingUtility.getDefaultServerName("WildFly Swarm - "+javaProject.getProject().getName());
		IServerWorkingCopy wc = type.createServer(suffixed, null, new NullProgressMonitor());
		wc.setName(suffixed);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainClass);
		IServer server = wc.save(true, monitor);
		return server;
	}
	
}
