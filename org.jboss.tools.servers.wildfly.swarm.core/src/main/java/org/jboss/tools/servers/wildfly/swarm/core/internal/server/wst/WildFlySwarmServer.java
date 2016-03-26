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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.jboss.ide.eclipse.as.core.server.internal.IExtendedPropertiesProvider;
import org.jboss.ide.eclipse.as.core.server.internal.extendedproperties.ServerExtendedProperties;
import org.jboss.tools.servers.wildfly.swarm.core.internal.CoreActivator;

public class WildFlySwarmServer extends ServerDelegate implements IExtendedPropertiesProvider{
	
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		return new Status(IStatus.WARNING, CoreActivator.PLUGIN_ID, "This server doesn't support modules");
	}

	public IModule[] getChildModules(IModule[] module) {
		return new IModule[0];
	}

	public IModule[] getRootModules(IModule module) throws CoreException {
		return new IModule[] { module };
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
	}

	@Override
	public ServerExtendedProperties getExtendedProperties() {
		return new WildFlySwarmServerExtendedProperties(getServer());
	}

	
	
}
