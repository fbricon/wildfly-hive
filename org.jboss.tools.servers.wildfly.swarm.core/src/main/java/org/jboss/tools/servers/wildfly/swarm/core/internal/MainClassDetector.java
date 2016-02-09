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
package org.jboss.tools.servers.wildfly.swarm.core.internal;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.debug.ui.launcher.MainMethodSearchEngine;

@SuppressWarnings("restriction")
public class MainClassDetector {

	public static Collection<String> findMainClasses(IJavaProject p, IProgressMonitor monitor) {
		MainMethodSearchEngine engine = new MainMethodSearchEngine();
		int constraints = IJavaSearchScope.SOURCES;
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[]{p}, constraints);
		IType[] types = engine.searchMainMethods(monitor, scope, false);
		List<String> mains = Stream.of(types)
				.filter(t -> hasContainerImport(t))
				.map(IType::getFullyQualifiedName)
				.collect(Collectors.toList());
		
		mains.add(IWildFlySwarmConstants.DEFAULT_MAIN_CLASS);
		return mains;
	}

	private static boolean hasContainerImport(IType t) {
		return t.getCompilationUnit() != null && 
				t.getCompilationUnit().getImport("org.wildfly.swarm.container.Container")
				.exists();
	}

}
