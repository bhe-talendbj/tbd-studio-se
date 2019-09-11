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
import org.talend.hadoop.distribution.ESparkVersion;
import org.talend.hadoop.distribution.condition.BasicExpression;
import org.talend.hadoop.distribution.condition.BooleanOperator;
import org.talend.hadoop.distribution.condition.ComponentCondition;
import org.talend.hadoop.distribution.condition.EqualityOperator;
import org.talend.hadoop.distribution.condition.MultiComponentCondition;
import org.talend.hadoop.distribution.condition.SimpleComponentCondition;
import org.talend.hadoop.distribution.constants.SparkBatchConstant;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicModuleGroupConstant;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.DynamicSparkBatchModuleGroup;


/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicEMRSparkBatchModuleGroup extends DynamicSparkBatchModuleGroup {

    public DynamicEMRSparkBatchModuleGroup(DynamicPluginAdapter pluginAdapter) {
        super(pluginAdapter);
    }

    @Override
    protected void initConditions() {
        conditionSpark1 = new MultiComponentCondition(
                new BasicExpression(SparkBatchConstant.SPARK_LOCAL_MODE_PARAMETER, EqualityOperator.EQ, "false"), //$NON-NLS-1$
                BooleanOperator.AND,
                new BasicExpression("SUPPORTED_SPARK_VERSION", EqualityOperator.EQ, ESparkVersion.SPARK_1_6.getSparkVersion())); //$NON-NLS-1$

        conditionSpark2 = new MultiComponentCondition(
                new BasicExpression(SparkBatchConstant.SPARK_LOCAL_MODE_PARAMETER, EqualityOperator.EQ, "false"), //$NON-NLS-1$
                BooleanOperator.AND,
                new MultiComponentCondition(
                		new BasicExpression("SUPPORTED_SPARK_VERSION", EqualityOperator.EQ, ESparkVersion.SPARK_2_2.getSparkVersion()), //$NON-NLS-1$
                        BooleanOperator.OR,
                        new BasicExpression("SUPPORTED_SPARK_VERSION", EqualityOperator.EQ, ESparkVersion.SPARK_2_4.getSparkVersion())) //$NON-NLS-1$
        		);
    }

    @Override
    public Set<DistributionModuleGroup> getModuleGroups() throws Exception {
        Set<DistributionModuleGroup> moduleGroups = new HashSet<>();
        Set<DistributionModuleGroup> moduleGroupsFromSuper = super.getModuleGroups();
        if (moduleGroupsFromSuper != null && !moduleGroupsFromSuper.isEmpty()) {
            moduleGroups.addAll(moduleGroupsFromSuper);
        }
        DynamicPluginAdapter pluginAdapter = getPluginAdapter();

        String sparkMrRequiredRuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.SPARK_MRREQUIRED_MODULE_GROUP.getModuleName());
        String hdfsSpark1_6RuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.HDFS_MODULE_GROUP_SPARK1_6.getModuleName());
        String hdfsSpark2_1RuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.HDFS_MODULE_GROUP_SPARK2_1.getModuleName());
        String hdfsCommonRuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.HDFS_MODULE_GROUP_COMMON.getModuleName());
        String mrRuntimeId = pluginAdapter
                .getRuntimeModuleGroupIdByTemplateId(DynamicModuleGroupConstant.MAPREDUCE_MODULE_GROUP.getModuleName());
        String talendClouderaNaviRuntimeId = pluginAdapter.getRuntimeModuleGroupIdByTemplateId(
                DynamicEMRModuleGroupConstant.TALEND_CLOUDERA_CDH_NAVIGATOR.getModuleName());

        checkRuntimeId(sparkMrRequiredRuntimeId);
        checkRuntimeId(hdfsSpark1_6RuntimeId);
        checkRuntimeId(hdfsSpark2_1RuntimeId);
        checkRuntimeId(hdfsCommonRuntimeId);
        checkRuntimeId(mrRuntimeId);
        checkRuntimeId(talendClouderaNaviRuntimeId);

        if (StringUtils.isNotBlank(sparkMrRequiredRuntimeId)) {
            moduleGroups.add(new DistributionModuleGroup(sparkMrRequiredRuntimeId, true, conditionSpark1));
            moduleGroups.add(new DistributionModuleGroup(sparkMrRequiredRuntimeId, true, conditionSpark2));
        }
        if (StringUtils.isNotBlank(hdfsSpark1_6RuntimeId)) {
            moduleGroups.add(new DistributionModuleGroup(hdfsSpark1_6RuntimeId, false, conditionSpark1));
        }
        if (StringUtils.isNotBlank(hdfsSpark2_1RuntimeId)) {
            moduleGroups.add(new DistributionModuleGroup(hdfsSpark2_1RuntimeId, false, conditionSpark2));
        }
        if (StringUtils.isNotBlank(hdfsCommonRuntimeId)) {
            moduleGroups.add(new DistributionModuleGroup(hdfsCommonRuntimeId, false, conditionSpark1));
            moduleGroups.add(new DistributionModuleGroup(hdfsCommonRuntimeId, false, conditionSpark2));
        }
        if (StringUtils.isNotBlank(mrRuntimeId)) {
            moduleGroups.add(new DistributionModuleGroup(mrRuntimeId, false, conditionSpark1));
            moduleGroups.add(new DistributionModuleGroup(mrRuntimeId, false, conditionSpark2));
        }
        if (StringUtils.isNotBlank(talendClouderaNaviRuntimeId)) {
            ComponentCondition conditionUseNavigator = new SimpleComponentCondition(
                    new BasicExpression(SparkBatchConstant.USE_CLOUDERA_NAVIGATOR));
            moduleGroups.add(new DistributionModuleGroup(talendClouderaNaviRuntimeId, true, conditionUseNavigator));
        }

        return moduleGroups;
    }
}
