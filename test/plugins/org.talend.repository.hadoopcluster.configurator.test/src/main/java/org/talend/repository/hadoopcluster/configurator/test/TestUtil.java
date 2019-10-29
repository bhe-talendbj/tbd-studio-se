// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.hadoopcluster.configurator.test;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.talend.repository.hadoopcluster.configurator.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * created by bchen on Nov 26, 2015 Detailled comment
 *
 */
public class TestUtil {

    public static HadoopConfigurator getConfigurator(String url, String trustStoreFile, String trustStoreType,
            String trustStorePwd) throws IOException {

        Bundle bundle = Platform.getBundle("org.talend.repository.hadoopcluster.configurator.test"); //$NON-NLS-1$
        URL confEntry = bundle.getEntry("/resources/conf/" + trustStoreFile);
        File configFile = new File(FileLocator.toFileURL(confEntry).getFile());

        HadoopConfigurator configurator = new HadoopConfiguratorBuilder().withVendor(HadoopConfigurationManager.CLOUDERA_MANAGER)
                .withBaseURL(new URL(url)).withUsernamePassword("admin", "admin")
                .withTrustManagers(configFile.getAbsolutePath(), trustStoreType, trustStorePwd).build();
        return configurator;
    }

    public static String getClusterDisplayName(String clusterNameWithDisplayName) {
        String[] clusterNameArray = clusterNameWithDisplayName.split(HadoopConfigurator.NAME_SEPARATOR_PATTERN);
        return clusterNameArray[0];
    }

    public static void checkCluster(HadoopConfigurator configurator, String... clusterNames) throws Exception {
        List<String> allClusters = configurator.getAllClusters();

        String[] displayNames = new String[allClusters.size()];
        for (int i = 0; i < allClusters.size(); i++) {
            displayNames[i] = getClusterDisplayName(allClusters.get(i));
        }

        Arrays.sort(displayNames);
        Arrays.sort(clusterNames);
        assertArrayEquals(clusterNames, displayNames);
    }

    public static void checkService(Map<HadoopHostedService, HadoopClusterService> services,
            HadoopHostedService... expectedServices) throws Exception {
        assertEquals(expectedServices.length, services.size());
        Arrays.sort(expectedServices);
        HadoopHostedService[] array = services.keySet().toArray(new HadoopHostedService[services.keySet().size()]);
        Arrays.sort(array);
        assertArrayEquals(expectedServices, array);
    }

    public static void checkServiceConf(HadoopClusterService service, String... confFileNames) throws Exception {
        assertEquals(true, service.hasConfigurations());
        assertEquals(confFileNames.length, service.getConfFiles().size());
        String[] hdfsConfs = service.getConfFiles().toArray(new String[service.getConfFiles().size()]);
        Arrays.sort(confFileNames);
        Arrays.sort(hdfsConfs);
        assertArrayEquals(confFileNames, hdfsConfs);
    }
}
