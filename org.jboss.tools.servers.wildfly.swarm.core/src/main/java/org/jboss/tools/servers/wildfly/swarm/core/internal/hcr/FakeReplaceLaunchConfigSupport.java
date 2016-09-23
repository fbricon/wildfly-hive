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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.jboss.tools.servers.wildfly.swarm.core.internal.ResourceUtil;

public class FakeReplaceLaunchConfigSupport implements HotClassReloaderLaunchConfigSupport {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("/agents");
	
	private static final String FAKEREPLACE_FILENAME = BUNDLE.getString("fakereplace");

	private ILaunchConfiguration launchConfig;
	
	public FakeReplaceLaunchConfigSupport(ILaunchConfiguration launchConfig) throws CoreException {
		this.launchConfig = launchConfig;
	}

	@Override
	public String appendVMArgs(String vmArgs) throws CoreException {
		String javaAgentUrl = ResourceUtil.getEmbeddedFileUrl(FAKEREPLACE_FILENAME);
		StringBuilder fullVmArgs = new StringBuilder(vmArgs)
				.append(" -Xbootclasspath/a:\"")
				.append(javaAgentUrl)
				.append("\" -javaagent:\"")
				.append(javaAgentUrl)
				.append("=log=info,")
				.append("index-file=")
				.append(getFakereplaceIndex())
				.append("\"");
		return fullVmArgs.toString();
	}

	private String getFakereplaceIndex() {
		Path parent = Paths.get(ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString(),
				".metadata",
				".fakereplace",
				launchConfig.getName().replaceAll(" ", "_"))
				.toAbsolutePath();

		if (Files.notExists(parent)) {
			try {
				Files.createDirectories(parent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return parent.resolve("fakereplace.index").toString();
	}

	/*
	private String getPackages() throws CoreException {
		IJavaProject javaProject = JavaRuntime.getJavaProject(launchConfig);
		return HotClassReloaderUtil.getTopLevelPackages(javaProject)
				.stream()
				.map(IPackageFragment::getElementName)
				.collect(Collectors.joining(";"));
	}
	*/
}
