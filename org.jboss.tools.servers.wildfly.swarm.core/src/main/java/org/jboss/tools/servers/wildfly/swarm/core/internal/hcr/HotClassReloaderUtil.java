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

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.jboss.tools.servers.wildfly.swarm.core.internal.CoreActivator;
import org.osgi.framework.Bundle;

public class HotClassReloaderUtil {
	
	private HotClassReloaderUtil() {
	}

	public static String getJavaAgentUrl(String agentFileName) throws CoreException {
		Bundle bundle = Platform.getBundle(CoreActivator.PLUGIN_ID);
		IPath hcrPath = new Path("extras").append(agentFileName);
		URL hcrEmbeddedUrl = FileLocator.find(bundle, hcrPath, null);
		URL hcrUrl;
		try {
			hcrUrl = FileLocator.resolve(hcrEmbeddedUrl);
			URI uri = new URI(hcrUrl.toString().replace(" ", "%20"));
			return Paths.get(uri).toAbsolutePath().toString();
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, CoreActivator.PLUGIN_ID, "Unable to determine javaagent url for "+agentFileName, e);
			throw new CoreException(status);
		}
	}
	
	public static HotClassReloaderLaunchConfigSupport getHotClassReloaderLaunchConfigSupport(ILaunchConfiguration launchConfig) throws CoreException {
		String hcr = launchConfig.getAttribute("hotclassreloader", "fakereplace");
		//TODO use extension points eventually
		switch (hcr) {
			case "fakereplace":
			//case "springloaded": //Maybe one day?
			default:
				return new FakeReplaceLaunchConfigSupport(launchConfig);
		}
	}

	public static String decorateVMArgs(ILaunchConfiguration launchConfig, String vmArgs) throws CoreException {
		HotClassReloaderLaunchConfigSupport hcrLaunchConfigSupport = getHotClassReloaderLaunchConfigSupport(launchConfig);
		return hcrLaunchConfigSupport.appendVMArgs(vmArgs);
	}
	
}
