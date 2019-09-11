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
package org.talend.hadoop.distribution.dynamic.template.modulegroup.emr;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicModuleGroupConstant;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicSqoopModuleGroup;


/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicEMRSqoopModuleGroup extends DynamicSqoopModuleGroup {

    public DynamicEMRSqoopModuleGroup(DynamicPluginAdapter pluginAdapter) {
        super(pluginAdapter);
    }

    @Override
    public Set<DistributionModuleGroup> getModuleGroups() throws Exception {
        Set<DistributionModuleGroup> moduleGroups = new HashSet<>();
        Set<DistributionModuleGroup> moduleGroupsFromSuper = super.getModuleGroups();
        if (moduleGroupsFromSuper != null && !moduleGroupsFromSuper.isEmpty()) {
            moduleGroups.addAll(moduleGroupsFromSuper);
        }

        DynamicPluginAdapter pluginAdapter = getPluginAdapter();

        String hdfsRuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.HDFS_MODULE_GROUP.getModuleName());
        String mrRuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.MAPREDUCE_MODULE_GROUP.getModuleName());

        checkRuntimeId(hdfsRuntimeId);
        checkRuntimeId(mrRuntimeId);

        if (StringUtils.isNotBlank(hdfsRuntimeId)) {
            moduleGroups.add(new DistributionModuleGroup(hdfsRuntimeId));
        }
        if (StringUtils.isNotBlank(mrRuntimeId)) {
            moduleGroups.add(new DistributionModuleGroup(mrRuntimeId));
        }
        return moduleGroups;
    }
}
