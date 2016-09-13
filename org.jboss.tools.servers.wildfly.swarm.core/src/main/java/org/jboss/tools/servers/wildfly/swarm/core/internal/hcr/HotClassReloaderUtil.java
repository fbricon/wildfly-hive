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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class HotClassReloaderUtil {

	private HotClassReloaderUtil() {
	}

	public static HotClassReloaderLaunchConfigSupport getHotClassReloaderLaunchConfigSupport(
			ILaunchConfiguration launchConfig) throws CoreException {
		String hcr = launchConfig.getAttribute("hotclassreloader", "fakereplace");
		// TODO use extension points, eventually
		switch (hcr) {
			case "fakereplace":
			// case "springloaded": //Maybe one day?
			default:
				return new FakeReplaceLaunchConfigSupport(launchConfig);
		}
	}

	public static String decorateVMArgs(ILaunchConfiguration launchConfig, String vmArgs) throws CoreException {
		HotClassReloaderLaunchConfigSupport hcrLaunchConfigSupport = getHotClassReloaderLaunchConfigSupport(
				launchConfig);
		return hcrLaunchConfigSupport.appendVMArgs(vmArgs);
	}

	public static Collection<IPackageFragment> getAllTopLevelPackages() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		Set<IPackageFragment> packages = new LinkedHashSet<>();
		for (IProject project : root.getProjects()) {
			if (project.isAccessible()){
				IJavaProject jp = JavaCore.create(project);
				if (jp != null) {
					packages.addAll(getTopLevelPackages(jp));
				}
			}
		}
		return packages;
	}

	public static Collection<IPackageFragment> getTopLevelPackages(IJavaProject javaProject) {
		Set<IPackageFragment> packages = new LinkedHashSet<>();
		try {
			if (javaProject == null || !javaProject.getProject().isAccessible()) {
				return packages;
			}
			for (IPackageFragment f : javaProject.getPackageFragments()){
				if (f.getKind() != IPackageFragmentRoot.K_SOURCE || f.isDefaultPackage() ) {
				  continue;
				}
				if (f.containsJavaResources() || f.getChildren().length > 1) {
					packages.add(f);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return packages;
	}
}
