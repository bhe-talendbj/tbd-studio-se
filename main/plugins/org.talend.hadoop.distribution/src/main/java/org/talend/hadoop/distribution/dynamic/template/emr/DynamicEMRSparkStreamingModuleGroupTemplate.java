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

import java.util.Set;

import org.talend.hadoop.distribution.DistributionModuleGroup;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.DynamicSparkStreamingModuleGroupTemplate;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.emr.DynamicEMRSparkStreamingModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.emr.DynamicEMRSparkStreamingKafkaAssemblyModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.emr.DynamicEMRSparkStreamingKafkaAvroModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.emr.DynamicEMRSparkStreamingKafkaClientModuleGroup;
import org.talend.hadoop.distribution.dynamic.template.modulegroup.node.sparkstreaming.emr.DynamicEMRSparkStreamingKinesisNodeModuleGroup;


/**
 * DOC cmeng  class global comment. Detailled comment
 */
public class DynamicEMRSparkStreamingModuleGroupTemplate extends DynamicSparkStreamingModuleGroupTemplate {

    public DynamicEMRSparkStreamingModuleGroupTemplate(DynamicPluginAdapter pluginAdapter) {
        super(pluginAdapter);
    }

    @Override
    protected Set<DistributionModuleGroup> buildModuleGroups4SparkStreaming(DynamicPluginAdapter pluginAdapter) throws Exception {
        return new DynamicEMRSparkStreamingModuleGroup(pluginAdapter).getModuleGroups();
    }

    @Override
    protected Set<DistributionModuleGroup> buildNodeModuleGroups4SparkStreaming4Kinesis(DynamicPluginAdapter pluginAdapter,
            String distribution, String version) throws Exception {
        return new DynamicEMRSparkStreamingKinesisNodeModuleGroup(pluginAdapter).getModuleGroups(distribution, version);
    }

    @Override
    protected Set<DistributionModuleGroup> buildNodeModuleGroups4SparkStreaming4KafkaAssembly(DynamicPluginAdapter pluginAdapter,
            String distribution, String version) throws Exception {
        return new DynamicEMRSparkStreamingKafkaAssemblyModuleGroup(pluginAdapter).getModuleGroups(distribution, version);
    }

    @Override
    protected Set<DistributionModuleGroup> buildNodeModuleGroups4SparkStreaming4KafkaAvro(DynamicPluginAdapter pluginAdapter,
            String distribution, String version) throws Exception {
        return new DynamicEMRSparkStreamingKafkaAvroModuleGroup(pluginAdapter).getModuleGroups(distribution, version);
    }

    @Override
    protected Set<DistributionModuleGroup> buildNodeModuleGroups4SparkStreaming4KafkaClient(DynamicPluginAdapter pluginAdapter,
            String distribution, String version) throws Exception {
        return new DynamicEMRSparkStreamingKafkaClientModuleGroup(pluginAdapter).getModuleGroups(distribution, version);
    }
}
