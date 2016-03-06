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

import org.eclipse.wst.server.core.ServerCore;
import org.jboss.tools.servers.wildfly.swarm.core.internal.CoreActivator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class UIActivator implements BundleActivator {

	public static final String PLUGIN_ID =  CoreActivator.ROOT_PLUGIN_ID+".ui";
	private static UIActivator instance;
	private WildFlyServerListener serverListener;

	@Override
	public void start(BundleContext context) throws Exception {
		//Force Core plugin activation
		CoreActivator.getInstance();
		instance = this;
		serverListener = new WildFlyServerListener();
		ServerCore.addServerLifecycleListener(serverListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		ServerCore.removeServerLifecycleListener(serverListener);
		serverListener = null;
		instance = null;
	}
	

	public static UIActivator getInstance() {
		return instance;
	}
}
