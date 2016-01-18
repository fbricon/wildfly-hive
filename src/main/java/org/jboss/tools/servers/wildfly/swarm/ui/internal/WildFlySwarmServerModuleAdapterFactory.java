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
package org.jboss.tools.servers.wildfly.swarm.ui.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.IServerModule;
import org.jboss.tools.servers.wildfly.swarm.core.internal.server.wst.WildFlySwarmServerDelegate;

public class WildFlySwarmServerModuleAdapterFactory implements IAdapterFactory {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adaptableObject instanceof IServer && IServerModule.class.equals(adapterType)) {
			IServer server = (IServer)adaptableObject;
			WildFlySwarmServerDelegate wssd = (WildFlySwarmServerDelegate)server.loadAdapter(WildFlySwarmServerDelegate.class, new NullProgressMonitor());
			if (wssd != null) {
				return (T) new WildFlySwarmServerModuleAdapter(server);
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] {IServerModule.class};
	}

	private static class WildFlySwarmServerModuleAdapter implements IServerModule {

		private IServer server;
		private RootModule module;

		private WildFlySwarmServerModuleAdapter(IServer server) {
			this.server = server;
			module = new RootModule(server.toString());
		}

		@Override
		public IServer getServer() {
			return server;
		}

		@Override
		public IModule[] getModule() {
			return new IModule[]{module};
		}

	}

	//TODO we're not supposed to implement IModule. That'll do until we find a better way to integrate with LiveReload
	private static class RootModule implements IModule {

		private String id;

		private RootModule(String id) {
			this.id = id;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public String getName() {
			return "";
		}

		@Override
		public IModuleType getModuleType() {
			return null;
		}

		@Override
		public IProject getProject() {
			return null;
		}

		@Override
		public boolean isExternal() {
			return false;
		}

		@Override
		public boolean exists() {
			return true;
		}

		@Override
		public Object getAdapter(Class adapter) {
			return null;
		}

		@Override
		public Object loadAdapter(Class adapter, IProgressMonitor monitor) {
			return null;
		}

	}

}
