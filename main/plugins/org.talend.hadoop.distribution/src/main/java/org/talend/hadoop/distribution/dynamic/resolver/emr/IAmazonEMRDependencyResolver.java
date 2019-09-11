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
package org.talend.hadoop.distribution.dynamic.resolver.emr;

import org.talend.hadoop.distribution.constants.emr.IAmazonDistribution;
import org.talend.hadoop.distribution.dynamic.resolver.IDependencyResolver;

/**
 * 
 * @author aparent
 *
 */
public interface IAmazonEMRDependencyResolver extends IDependencyResolver {

    public static final String DISTRIBUTION = IAmazonDistribution.DISTRIBUTION_NAME;

}
