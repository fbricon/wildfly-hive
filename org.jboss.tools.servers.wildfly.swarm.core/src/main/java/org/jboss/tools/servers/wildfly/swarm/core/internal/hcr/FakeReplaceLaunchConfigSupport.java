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

import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.launching.JavaRuntime;
import org.jboss.tools.servers.wildfly.swarm.core.internal.ResourceUtil;

public class FakeReplaceLaunchConfigSupport implements HotClassReloaderLaunchConfigSupport {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("/agents");
	
	private static final String FAKEREPLACE_FILENAME = BUNDLE.getString("fakereplace");

	private IJavaProject javaProject; 
	
	public FakeReplaceLaunchConfigSupport(ILaunchConfiguration launchConfig) throws CoreException {
		javaProject = JavaRuntime.getJavaProject(launchConfig);
	}

	@Override
	public String appendVMArgs(String vmArgs) throws CoreException {
		String javaAgentUrl = ResourceUtil.getEmbeddedFileUrl(FAKEREPLACE_FILENAME);
		String fakeReplaceArgs = " -Xbootclasspath/a:\""+javaAgentUrl+"\"";
		fakeReplaceArgs += " -javaagent:\""+javaAgentUrl+"=";
		fakeReplaceArgs += "packages="+getPackages();
		fakeReplaceArgs += "\"";
		
		//System.err.println("Fakereplace args: "+ fakeReplaceArgs);
		return vmArgs+fakeReplaceArgs;
	}

	private String getPackages() {
		return HotClassReloaderUtil.getTopLevelPackages(javaProject)
				.stream()
				.map(IPackageFragment::getElementName)
				.collect(Collectors.joining(";"));
	}
}
