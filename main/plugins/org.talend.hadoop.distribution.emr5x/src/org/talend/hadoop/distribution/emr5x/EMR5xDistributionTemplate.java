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

package org.talend.hadoop.distribution.emr5x;

import java.util.HashSet;
import java.util.Set;

import org.talend.hadoop.distribution.ESparkVersion;
import org.talend.hadoop.distribution.component.HBaseComponent;
import org.talend.hadoop.distribution.component.HCatalogComponent;
import org.talend.hadoop.distribution.component.HDFSComponent;
import org.talend.hadoop.distribution.component.HiveComponent;
import org.talend.hadoop.distribution.component.HiveOnSparkComponent;
import org.talend.hadoop.distribution.component.ImpalaComponent;
import org.talend.hadoop.distribution.component.MRComponent;
import org.talend.hadoop.distribution.component.PigComponent;
import org.talend.hadoop.distribution.component.SparkBatchComponent;
import org.talend.hadoop.distribution.component.SparkStreamingComponent;
import org.talend.hadoop.distribution.component.SqoopComponent;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.template.emr.AbstractDynamicEMRDistributionTemplate;
import org.talend.hadoop.distribution.kudu.KuduVersion;

public class EMR5xDistributionTemplate extends AbstractDynamicEMRDistributionTemplate implements HDFSComponent, HBaseComponent,
HCatalogComponent, PigComponent, MRComponent, HiveComponent, HiveOnSparkComponent, ImpalaComponent, SqoopComponent,
SparkBatchComponent, SparkStreamingComponent, IEMR5xDistributionTemplate{

	public final static String TEMPLATE_ID = "EMR5xDistributionTemplate";

	private final static String YARN_APPLICATION_CLASSPATH = "$HADOOP_CONF_DIR,$HADOOP_COMMON_HOME/*,$HADOOP_COMMON_HOME/lib/*,$HADOOP_HDFS_HOME/*,$HADOOP_HDFS_HOME/lib/*,$HADOOP_MAPRED_HOME/*,$HADOOP_MAPRED_HOME/lib/*,$YARN_HOME/*,$YARN_HOME/lib/*,$HADOOP_YARN_HOME/*,$HADOOP_YARN_HOME/lib/*,$HADOOP_COMMON_HOME/share/hadoop/common/*,$HADOOP_COMMON_HOME/share/hadoop/common/lib/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/*,$HADOOP_HDFS_HOME/share/hadoop/hdfs/lib/*,$HADOOP_YARN_HOME/share/hadoop/yarn/*,$HADOOP_YARN_HOME/share/hadoop/yarn/lib/*"; //$NON-NLS-1$

	public EMR5xDistributionTemplate(DynamicPluginAdapter pluginAdapter) throws Exception {
		super(pluginAdapter);
	}
	
	@Override
	public String getTemplateId() {
		return TEMPLATE_ID;
	}

	@Override
    public String getYarnApplicationClasspath() {
		return YARN_APPLICATION_CLASSPATH;
	}

	@Override
	public boolean doSupportSparkStandaloneMode() {
		return false;
	}

	@Override
	public boolean doSupportSparkYarnClientMode() {
		return false;
	}

	@Override
	public boolean doSupportDynamicMemoryAllocation() {
		return false;
	}

	@Override
	public boolean doSupportCheckpointing() {
		return false;
	}

	@Override
	public boolean doSupportBackpressure() {
		return false;
	}

	@Override
	public boolean doJavaAPISupportStorePasswordInFile() {
		return false;
	}

	@Override
	public boolean doJavaAPISqoopImportSupportDeleteTargetDir() {
		return false;
	}

	@Override
	public boolean doJavaAPISqoopImportAllTablesSupportExcludeTable() {
		return false;
	}

	@Override
	public boolean doSupportHive1() {
		return false;
	}

	@Override
	public boolean doSupportHive2() {
		return false;
	}

	@Override
	public boolean doSupportTezForHive() {
		return false;
	}

	@Override
	public boolean doSupportHBaseForHive() {
		return false;
	}

	@Override
	public boolean doSupportSSL() {
		return false;
	}

	@Override
	public boolean doSupportORCFormat() {
		return false;
	}

	@Override
	public boolean doSupportAvroFormat() {
		return false;
	}

	@Override
	public boolean doSupportParquetFormat() {
		return false;
	}

	@Override
	public boolean doSupportStoreAsParquet() {
		return false;
	}

	@Override
	public boolean doSupportCrossPlatformSubmission() {
		return false;
	}

	@Override
	public boolean doSupportImpersonation() {
		return false;
	}

	@Override
	public boolean doSupportHCatalog() {
		return false;
	}

	@Override
	public boolean doSupportHBase() {
		return false;
	}

	@Override
	public boolean pigVersionPriorTo_0_12() {
		return false;
	}

	@Override
	public boolean doSupportNewHBaseAPI() {
		return false;
	}

	@Override
	public boolean doSupportSequenceFileShortType() {
		return false;
	}

	@Override
    public Set<ESparkVersion> getSparkVersions() {
        Set<ESparkVersion> version = new HashSet<>();
        Set<ESparkVersion> sparkVersions = super.getSparkVersions();
        if (sparkVersions == null || sparkVersions.isEmpty()) {
            version.add(ESparkVersion.SPARK_2_2);
        } else {
            version.addAll(sparkVersions);
        }
        return version;
    }

}
