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

import org.eclipse.core.runtime.IAdaptable;
import org.jboss.ide.eclipse.as.core.server.internal.extendedproperties.ServerExtendedProperties;
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;

public class WildFlySwarmServerExtendedProperties extends ServerExtendedProperties {

	public WildFlySwarmServerExtendedProperties(IAdaptable adaptable) {
		super(adaptable);
	}
	
	@Override
	public boolean allowConvenienceEnhancements() {
		return false;
	}

	@Override
	public boolean hasWelcomePage() {
		return true;
	}

	@Override
	public String getWelcomePageUrl() {
		String welcomePageUrl = null;
		final ControllableServerBehavior behavior = (ControllableServerBehavior)JBossServerBehaviorUtils.getControllableBehavior(server);
		if (behavior != null) {
			welcomePageUrl = (String) behavior.getSharedData("welcomePage");
		}
		return welcomePageUrl == null?"http://localhost:8080":welcomePageUrl;
	}

}
