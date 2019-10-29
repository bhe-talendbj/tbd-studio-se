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
package org.talend.hadoop.distribution.test.hive;

import org.junit.Test;
import org.talend.hadoop.distribution.constants.emr.IAmazonEMRDistribution;

/**
 * DOC ggu class global comment. Detailled comment
 */
public class HiveMetadataHelper4AmazonEMRTest extends AbstractDistributionTest4HiveMetadataHelper {

    private static final String[] VERSIONS_NON_DYNAMIC = new String[] {
        "EMR 5.15.0 (Hive 2.3.2)",
        "EMR 5.8.0 (Hive 2.3.0)",
        "EMR 5.5.0 (Hive 2.1.1)",
        "EMR 5.0.0 (Hive 2.1.0)",
        "EMR 4.6.0 (Hive 1.0.0)",
        "EMR 4.5.0 (Hive 1.0.0)"};

    @Override
    protected String getDistribution() {
        return IAmazonEMRDistribution.DISTRIBUTION_NAME;
    }

    @Override
    protected String getDistributionDisplay() {
        return IAmazonEMRDistribution.DISTRIBUTION_DISPLAY_NAME;
    }

    @Override
    protected String[] getDistributionVersionsDisplay() {
        return VERSIONS_NON_DYNAMIC;
    }

    //this empty test is here to make tycho runner not failed on initialization
    //because it does not found any test in the final class
    @Test
    public void emptyTest() {
    }

}