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

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.talend.core.runtime.dynamic.IDynamicPluginConfiguration;
import org.talend.designer.maven.aether.IDynamicMonitor;
import org.talend.hadoop.distribution.dynamic.adapter.DynamicPluginAdapter;
import org.talend.hadoop.distribution.dynamic.emr.AbstractDynamicEMRDistribution;
import org.talend.hadoop.distribution.dynamic.template.IDynamicDistributionTemplate;

/**
 * 
 * @author aparent
 *
 */
public class DynamicEMR5xDistribution extends AbstractDynamicEMRDistribution {

	public static final String TEMPLATE_FOLDER_PATH = "resources/template/emrx/"; //$NON-NLS-1$

    public static final String BUILD_IN_FOLDER_PATH = "resources/builtin/emrx/"; //$NON-NLS-1$

	@Override
	public List<String> getSupportedTemplateIds(IDynamicMonitor monitor) throws Exception {
		List<String> templateIds = new ArrayList<>();

		templateIds.add(EMR5xDistributionTemplate.TEMPLATE_ID);
		return templateIds;
	}

	@Override
	protected Bundle getBundle() {
		EMR5xPlugin emr5xPlugin = EMR5xPlugin.getInstance();
        return emr5xPlugin.getBundle();
	}

	@Override
	protected String getTemplateFolderPath() {
		return TEMPLATE_FOLDER_PATH;
	}

	@Override
	protected String getBuiltinFolderPath() {
		return BUILD_IN_FOLDER_PATH;
	}

	@Override
	protected IDynamicDistributionTemplate initTemplate(DynamicPluginAdapter pluginAdapter, IDynamicMonitor monitor) throws Exception {
		IDynamicDistributionTemplate dynamicDistributionTemplate = null;
		IDynamicPluginConfiguration pluginConfiguration = pluginAdapter.getPluginConfiguration();
		String templateId = pluginConfiguration.getTemplateId();
		switch (templateId) {
		case EMR5xDistributionTemplate.TEMPLATE_ID:
			dynamicDistributionTemplate = new EMR5xDistributionTemplate(pluginAdapter);
			break;
		default:
			throw new Exception("Unknown templateId: " + templateId);
		}
		return dynamicDistributionTemplate;
	}

}
