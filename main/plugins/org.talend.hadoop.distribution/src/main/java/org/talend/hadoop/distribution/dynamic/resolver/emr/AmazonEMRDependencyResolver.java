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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.talend.hadoop.distribution.dynamic.resolver.AbstractDependencyResolver;

/**
 * 
 * @author aparent
 *
 */
public class AmazonEMRDependencyResolver extends AbstractDependencyResolver implements IAmazonEMRDependencyResolver {

    private List<Pattern> amazonEMRDistributionPatterns;

    @Override
    protected synchronized List<Pattern> getDistributionPatterns() {
        if (amazonEMRDistributionPatterns == null || amazonEMRDistributionPatterns.isEmpty()) {
        	amazonEMRDistributionPatterns = new ArrayList<>();
        	
            Pattern amazonEMRDistributionPattern = Pattern.compile("(?:[^m][^r]..[Cc][Dd][Hh])([\\d]+\\.[\\d]+\\.[\\d]+$)"); //$NON-NLS-1$
            amazonEMRDistributionPatterns.add(amazonEMRDistributionPattern);
        }
        return amazonEMRDistributionPatterns;
    }

    @Override
    public String getDistribution() {
        return IAmazonEMRDependencyResolver.DISTRIBUTION;
    }
}
