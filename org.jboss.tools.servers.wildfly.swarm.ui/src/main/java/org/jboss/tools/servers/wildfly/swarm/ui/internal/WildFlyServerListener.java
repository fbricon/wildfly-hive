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

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.util.ServerLifecycleAdapter;
import org.jboss.tools.servers.wildfly.swarm.core.internal.IWildFlySwarmConstants;
import org.jboss.tools.servers.wildfly.swarm.ui.internal.util.ViewUtils;

public class WildFlyServerListener extends ServerLifecycleAdapter {

	@Override
	public void serverAdded(IServer server) {
		if (server.getServerType() != null && IWildFlySwarmConstants.SERVER_TYPE.equals(server.getServerType().getId())) {
			System.err.println(server.getName() + " added");
			ViewUtils.showServersView();
		}
	}

}
