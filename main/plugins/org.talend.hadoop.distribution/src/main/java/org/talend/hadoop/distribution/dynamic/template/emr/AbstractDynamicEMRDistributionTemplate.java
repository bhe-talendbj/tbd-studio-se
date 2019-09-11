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
package org.talend.hadoop.distribution.dynamic.template.emr;

import java.util.Map;

import org.talend.hadoop.distribution.ComponentType;
import org.talend.hadoop.distribution.component.HiveOnSparkComponent;
import org.talend.hadoop.distribution.component.ImpalaComponent;
import org.talend.hadoop.distribution.component.MRComponent;
import org.talend.hadoop.distribution.component.SparkBatchComponent;
import org.talend.hadoop.distribution.component.SparkStreamingComponent;
import org.talend.hadoop.distribution.component.SqoopComponent;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.AbstractDynamicDistributionTemplate;
import org.talend.hadoop.distribution.dynamic.template.DynamicImpalaModuleGroupTemplate;
import org.talend.hadoop.distribution.dynamic.template.IDynamicModuleGroupTemplate;


/**
 * 
 * @author aparent
 *
 */
public abstract class AbstractDynamicEMRDistributionTemplate extends AbstractDynamicDistributionTemplate
        implements IDynamicEMRDistributionTemplate {

    public AbstractDynamicEMRDistributionTemplate(DynamicPluginAdapter pluginAdapter) throws Exception {
        super(pluginAdapter);
    }

    @Override
    protected Map<ComponentType, IDynamicModuleGroupTemplate> buildModuleGroupsTemplateMap() {
        Map<ComponentType, IDynamicModuleGroupTemplate> groupTemplateMap = super.buildModuleGroupsTemplateMap();
        DynamicPluginAdapter pluginAdapter = getPluginAdapter();

        if (this instanceof HiveOnSparkComponent) {
            groupTemplateMap.put(ComponentType.HIVEONSPARK, new DynamicEMRHiveOnSparkModuleGroupTemplate(pluginAdapter));
        }
        if (this instanceof MRComponent) {
            groupTemplateMap.put(ComponentType.MAPREDUCE, new DynamicEMRMapReduceModuleGroupTemplate(pluginAdapter));
        }
        if (this instanceof SparkBatchComponent) {
            groupTemplateMap.put(ComponentType.SPARKBATCH, new DynamicEMRSparkBatchModuleGroupTemplate(pluginAdapter));
        }
        if (this instanceof SparkStreamingComponent) {
            groupTemplateMap.put(ComponentType.SPARKSTREAMING, new DynamicEMRSparkStreamingModuleGroupTemplate(pluginAdapter));
        }
        if (this instanceof SqoopComponent) {
            groupTemplateMap.put(ComponentType.SQOOP, new DynamicEMRSqoopModuleGroupTemplate(pluginAdapter));
        }
        if (this instanceof ImpalaComponent) {
            groupTemplateMap.put(ComponentType.IMPALA, new DynamicImpalaModuleGroupTemplate(pluginAdapter));
        }

        return groupTemplateMap;
    }

    @Override
    public String getDistribution() {
        return DISTRIBUTION_NAME;
    }

    @Override
    public String getDistributionName() {
        return DISTRIBUTION_DISPLAY_NAME;
    }

}
