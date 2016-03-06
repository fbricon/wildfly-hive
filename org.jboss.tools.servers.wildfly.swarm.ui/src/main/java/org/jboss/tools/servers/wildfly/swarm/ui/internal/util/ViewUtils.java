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
package org.jboss.tools.servers.wildfly.swarm.ui.internal.util;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ViewUtils {

	public static final String SERVERS_VIEW_ID = "org.eclipse.wst.server.ui.ServersView";

	public static void showServersView() {
		showView(SERVERS_VIEW_ID);
	}

	public static void showView(String viewId) {
		Display.getDefault().asyncExec(() -> {
			try {
				IWorkbenchWindow worbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (worbenchWindow == null) {
					return;
				}
				IWorkbenchPage page = worbenchWindow.getActivePage();
				if (page == null) {
					return;
				}
				page.showView(viewId);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		});
	}

}
