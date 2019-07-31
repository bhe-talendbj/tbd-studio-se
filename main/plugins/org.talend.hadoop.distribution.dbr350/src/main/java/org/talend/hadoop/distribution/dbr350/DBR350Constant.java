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
package org.talend.hadoop.distribution.dbr350;


public enum DBR350Constant {
    SPARK_MRREQUIRED_MODULE_GROUP("SPARK-LIB-MRREQUIRED-DBR350"), //$NON-NLS-1$
    SPARK_STREAMING_MRREQUIRED_MODULE_GROUP("SPARK-STREAMING-LIB-MRREQUIRED-DBR350"), //$NON-NLS-1$
    SPARK_STREAMING_KINESIS_MODULE_GROUP("SPARK-STREAMING-LIB-KINESIS-DBR350"), //$NON-NLS-1$

    SPARK_AZURE_MRREQUIRED_MODULE_GROUP("SPARK-AZURE-LIB-MRREQUIRED-DBR350"), //$NON-NLS-1$
    SPARK_HIVE_MRREQUIRED_MODULE_GROUP("HIVEONSPARK-LIB-MRREQUIRED-DBR350"), //$NON-NLS-1$
    BIGDATALAUNCHER_MODULE_GROUP("BIGDATA-LAUNCHER-LIB-DBR350"); //$NON-NLS-1$

    private String mModuleName;

    DBR350Constant(String moduleName) {
        this.mModuleName = moduleName;
    }

    public String getModuleName() {
        return this.mModuleName;
    }
}
