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

import org.talend.hadoop.distribution.constants.emr.IAmazonDistribution;
import org.talend.hadoop.distribution.dynamic.IDynamicDistributionsGroup;


/**
 * 
 * @author aparent
 *
 */
public interface IDynamicEMRDistributionsGroup extends IDynamicDistributionsGroup {

    public static final String DISTRIBUTION_NAME = IAmazonDistribution.DISTRIBUTION_NAME;

    public static final String DISTRIBUTION_DISPLAY_NAME = IAmazonDistribution.DISTRIBUTION_DISPLAY_NAME;
}
