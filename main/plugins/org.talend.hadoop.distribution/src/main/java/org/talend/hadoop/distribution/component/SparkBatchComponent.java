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
package org.talend.hadoop.distribution.component;

/**
 * Interface that exposes specific Spark Batch methods.
 *
 */
public interface SparkBatchComponent extends SparkComponent {
	/**
     * @return true if the distribution does require the elasticsearch-hadoop patch (see TDQ-14395)
     */
    public boolean doRequireElasticsearchSparkPatch();
}
