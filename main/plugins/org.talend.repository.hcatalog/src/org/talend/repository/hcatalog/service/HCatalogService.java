// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.hcatalog.service;

import org.talend.core.hadoop.IHCatalogService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.hcatalog.node.HCatalogRepositoryNodeType;
import org.talend.repository.model.hcatalog.HCatalogConnection;
import org.talend.repository.model.hcatalog.HCatalogConnectionItem;

/**
 * created by Talend on Apr 16, 2015 Detailled comment
 *
 */
public class HCatalogService implements IHCatalogService {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.hadoop.IHCatalogService#getHCatalogType()
     */
    @Override
    public ERepositoryObjectType getHCatalogType() {
        return HCatalogRepositoryNodeType.HCATALOG;
    }
    
    @Override
    public boolean hideHCatalogValues(Item item) {
        if(!(item instanceof HCatalogConnectionItem)) {
            return false;
        }
        HCatalogConnection cataConn = (HCatalogConnection) ((HCatalogConnectionItem)item).getConnection();
        cataConn.setComment("");
        cataConn.setHostName("");
        cataConn.setPassword("");
        cataConn.setUserName("");
        return true;
    }

}
