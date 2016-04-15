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
package org.jboss.tools.servers.wildfly.swarm.core.internal.hcr;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jboss.tools.servers.wildfly.swarm.core.internal.SocketUtil;

public class FakeReplaceLaunchConfigSupport implements HotClassReloaderLaunchConfigSupport {

	private ILaunchConfiguration launchConfig;

	private static final String FAKEREPLACE_FILENAME = "fakereplace-dist-1.0.0.Alpha3-SNAPSHOT.jar"; 
	
	public FakeReplaceLaunchConfigSupport(ILaunchConfiguration launchConfig) {
		this.launchConfig = launchConfig;
	}

	@Override
	public String appendVMArgs(String vmArgs) throws CoreException {
		//see https://github.com/stuartwdouglas/wildfly-swarm/commit/dc0981a0123e91782606a1585588c190510eae18
		String javaAgentUrl = HotClassReloaderUtil.getJavaAgentUrl(FAKEREPLACE_FILENAME);
		vmArgs += " -Xbootclasspath/a:\""+javaAgentUrl+"\"";
		vmArgs += " -javaagent:\""+javaAgentUrl;
		vmArgs += "=port="+SocketUtil.getNextAvailablePort(6555)+"\" ";
		vmArgs += getModuleSourceProperty();
		return vmArgs;
	}

	private String getModuleSourceProperty() throws CoreException {
		String moduleName = "myapp.war";
		IJavaProject proj = JavaRuntime.getJavaProject(launchConfig);
		String sources = Stream.of(proj.getRawClasspath())
								.filter(cpe -> cpe.getEntryKind() == IClasspathEntry.CPE_SOURCE)
								.map(cpe -> proj.getProject().getLocation().append(cpe.getPath()).toOSString())
								.collect(Collectors.joining(","));
		String arg = " \"-Dfakereplace.source-paths." + moduleName + "=" + sources+"\"";
		return arg;
	}
}
