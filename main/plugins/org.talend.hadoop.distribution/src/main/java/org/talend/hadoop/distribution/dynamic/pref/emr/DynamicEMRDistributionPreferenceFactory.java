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
package org.talend.hadoop.distribution.dynamic.pref.emr;

import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.talend.hadoop.distribution.dynamic.pref.AbstractDynamicDistributionPreferenceFactory;
import org.talend.hadoop.distribution.dynamic.pref.IDynamicDistributionPreference;


/**
 * 
 * @author aparent
 *
 */
public class DynamicEMRDistributionPreferenceFactory extends AbstractDynamicDistributionPreferenceFactory {

    private static DynamicEMRDistributionPreferenceFactory instance;

    private DynamicEMRDistributionPreferenceFactory() {
        // nothing to do
    }

    public static DynamicEMRDistributionPreferenceFactory getInstance() {
        if (instance == null) {
            instance = new DynamicEMRDistributionPreferenceFactory();
        }
        return instance;
    }

    @Override
    protected IDynamicDistributionPreference newPreferenceInstance(ScopedPreferenceStore store) {
        return new DynamicEMRDistributionPreference(store);
    }

}
