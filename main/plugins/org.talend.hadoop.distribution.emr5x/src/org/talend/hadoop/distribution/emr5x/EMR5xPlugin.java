package org.talend.hadoop.distribution.emr5x;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class EMR5xPlugin extends Plugin {
	
    private static EMR5xPlugin instance;

    public static EMR5xPlugin getInstance() {
        return instance;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        instance = this;
    }

	@Override
    public void stop(BundleContext bundleContext) throws Exception {
        super.stop(bundleContext);
	}
}
