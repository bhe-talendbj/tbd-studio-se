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
import org.talend.hadoop.distribution.dynamic.pref.AbstractDynamicDistributionPreference;


/**
 * 
 * @author aparent
 *
 */
public abstract class AbstractDynamicEMRDistributionPreference extends AbstractDynamicDistributionPreference
        implements IDynamicEMRDistributionPreference {

    protected AbstractDynamicEMRDistributionPreference(ScopedPreferenceStore store) {
        super(store);
    }

    @Override
    protected String getPrefKeyOverrideDefaultSetup() {
        return PREF_OVERRIDE_DEFAULT_SETUP;
    }

    @Override
    protected String getPrefKeyRepository() {
        return PREF_REPOSITORY;
    }

    @Override
    protected String getPrefKeyAnonymous() {
        return PREF_ANONYMOUS;
    }

    @Override
    protected String getPrefKeyUsername() {
        return PREF_USERNAME;
    }

    @Override
    protected String getPrefKeyPassword() {
        return PREF_PASSWORD;
    }

    @Override
    protected boolean getPrefDefaultOverrideDefaultSetup() {
        return PREF_OVERRIDE_DEFAULT_SETUP_DEFAULT;
    }

    @Override
    protected String getPrefDefaultRepository() {
        return PREF_REPOSITORY_DEFAULT;
    }

    @Override
    protected boolean getPrefDefaultAnonymous() {
        return PREF_ANONYMOUS_DEFAULT;
    }

    @Override
    protected String getPrefDefaultUsername() {
        return PREF_USERNAME_DEFAULT;
    }

    @Override
    protected String getPrefDefaultPassword() {
        if (PREF_PASSWORD_DEFAULT != null && !PREF_PASSWORD_DEFAULT.isEmpty()) {
            return getCryptoHelper().decrypt(PREF_PASSWORD_DEFAULT);
        } else {
            return PREF_PASSWORD_DEFAULT;
        }
    }

}
