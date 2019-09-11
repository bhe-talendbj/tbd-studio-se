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
package org.talend.hadoop.distribution.dynamic.emr;

import org.talend.hadoop.distribution.dynamic.AbstractDynamicDistribution;


/**
 * 
 * @author aparent
 *
 */
public abstract class AbstractDynamicEMRDistribution extends AbstractDynamicDistribution implements IDynamicEMRDistribution {

    @Override
    public String getDistributionName() {
        return IDynamicEMRDistribution.DISTRIBUTION;
    }
}
