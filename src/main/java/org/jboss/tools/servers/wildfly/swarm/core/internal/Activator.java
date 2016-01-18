package org.jboss.tools.servers.wildfly.swarm.core.internal;

import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "org.jboss.tools.servers.wildfly.swarm.core";

	public ClasspathChangeListener classpathChangeListener;
	
	public WildlfySwarmDetectionJob detectionJob;
	
	@Override
	public void start(BundleContext context) throws Exception {
		detectionJob = new WildlfySwarmDetectionJob();
		classpathChangeListener = new ClasspathChangeListener(detectionJob);
		JavaCore.addElementChangedListener(classpathChangeListener);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		JavaCore.removeElementChangedListener(classpathChangeListener);
		classpathChangeListener = null;
		detectionJob = null;
	}
}
