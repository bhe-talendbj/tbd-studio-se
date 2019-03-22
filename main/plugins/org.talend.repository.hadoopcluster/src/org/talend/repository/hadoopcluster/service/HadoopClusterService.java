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
package org.talend.repository.hadoopcluster.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EMap;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.database.conn.ConnParameterKeys;
import org.talend.core.hadoop.HadoopConstants;
import org.talend.core.hadoop.IHadoopClusterService;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.ContextItem;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryTypeProcessor;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.components.EParameterName;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.model.utils.emf.talendfile.ContextType;
import org.talend.designer.runprocess.ProcessorUtilities;
import org.talend.metadata.managment.ui.utils.ConnectionContextHelper;
import org.talend.repository.ProjectManager;
import org.talend.repository.hadoopcluster.conf.HadoopConfsUtils;
import org.talend.repository.hadoopcluster.node.model.HadoopClusterRepositoryNodeType;
import org.talend.repository.hadoopcluster.ui.viewer.HadoopSubMultiRepTypeProcessor;
import org.talend.repository.hadoopcluster.ui.viewer.HadoopSubnodeRepositoryContentManager;
import org.talend.repository.hadoopcluster.ui.viewer.handler.IHadoopSubnodeRepositoryContentHandler;
import org.talend.repository.hadoopcluster.util.HCRepositoryUtil;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnection;
import org.talend.repository.model.hadoopcluster.HadoopClusterConnectionItem;
import org.talend.repository.model.hadoopcluster.HadoopClusterPackage;
import org.talend.repository.model.hadoopcluster.HadoopSubConnection;
import org.talend.repository.model.hadoopcluster.HadoopSubConnectionItem;

/**
 * created by ycbai on 2013-1-28 Detailled comment
 *
 */
public class HadoopClusterService implements IHadoopClusterService {

    private static final String MR_PROPERTY_PREFIX = "MR_PROPERTY:"; //$NON-NLS-1$

    @Override
    public boolean containedByCluster(Connection hadoopClusterConnection, Connection hadoopSubConnection) {
        if (hadoopClusterConnection != null && hadoopSubConnection != null
                && hadoopClusterConnection instanceof HadoopClusterConnection) {
            HadoopClusterConnection hcConnection = (HadoopClusterConnection) hadoopClusterConnection;
            HadoopClusterConnection relativeHCConnection = HCRepositoryUtil
                    .getRelativeHadoopClusterConnection(hadoopSubConnection);
            if (hcConnection.equals(relativeHCConnection)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ERepositoryObjectType getHadoopClusterType() {
        return HadoopClusterRepositoryNodeType.HADOOPCLUSTER;
    }

    @Override
    public boolean isHadoopClusterNode(IRepositoryNode node) {
        return HCRepositoryUtil.isHadoopClusterNode(node);
    }

    @Override
    public boolean isHadoopSubnode(IRepositoryNode node) {
        return HCRepositoryUtil.isHadoopSubnode(node);
    }

    @Override
    public boolean isHadoopFolderNode(IRepositoryNode node) {
        return HCRepositoryUtil.isHadoopFolderNode(node);
    }

    @Override
    public boolean isHadoopClusterItem(Item item) {
        return item.eClass() == HadoopClusterPackage.Literals.HADOOP_CLUSTER_CONNECTION_ITEM;
    }

    @Override
    public boolean isHadoopSubItem(Item item) {
        if (isHadoopClusterItem(item)) {
            return false;
        }
        for (IHadoopSubnodeRepositoryContentHandler handler : HadoopSubnodeRepositoryContentManager.getHandlers()) {
            if (handler.isProcess(item)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isValidHadoopSubItem(Item item) {
        if (isHadoopSubItem(item)) {
            HadoopSubConnectionItem hadoopSubConnectionItem = (HadoopSubConnectionItem) item;
            Connection connection = hadoopSubConnectionItem.getConnection();
            if (connection instanceof HadoopSubConnection) {
                return ((HadoopSubConnection) connection).getRelativeHadoopClusterId() != null;
            }
        }

        return false;
    }

    @Override
    public boolean isHadoopSubItem2(Item item) {
        return getHadoopClusterBySubitemId(item.getProperty().getId()) != null;
    }

    @Override
    public boolean isInContextMode(Connection connection) {
        if (connection == null) {
            return false;
        }
        boolean isContextMode = false;
        if (connection instanceof HadoopClusterConnection) {
            isContextMode = connection.isContextMode();
        } else {
            Connection hcConnection = getHadoopClusterConnectionBySubConnection(connection);
            isContextMode = connection.isContextMode() || (hcConnection != null && hcConnection.isContextMode());
        }
        return isContextMode;
    }

    @Override
    public List<String> getSubitemIdsOfHadoopCluster(Item item) {
        if (!isHadoopClusterItem(item)) {
            return new ArrayList<>();
        }
        HadoopClusterConnectionItem clusterConnectionItem = (HadoopClusterConnectionItem) item;
        HadoopClusterConnection clusterConnection = (HadoopClusterConnection) clusterConnectionItem.getConnection();

        return clusterConnection.getConnectionList();
    }

    @Override
    public Item getHadoopClusterBySubitemId(Project project, String subItemId) {
        return HCRepositoryUtil.getHadoopClusterItemBySubitemId(project, subItemId);
    }

    @Override
    public Item getHadoopClusterBySubitemId(String subItemId) {
        return HCRepositoryUtil.getHadoopClusterItemBySubitemId(subItemId);
    }

    @Override
    public void refreshCluster(String clusterId) {
        HadoopClusterConnectionItem clusterItem = HCRepositoryUtil.getRelativeHadoopClusterItem(clusterId);
        if (clusterItem != null) {
            try {
                ProxyRepositoryFactory.getInstance().save(clusterItem.getProperty());
            } catch (PersistenceException e) {
                ExceptionHandler.process(e);
            }
        }
    }

    @Override
    public Map<String, String> getHadoopDbParameters(String clusterId) {
        return HCRepositoryUtil.getHadoopDbParameters(clusterId);
    }

    @Override
    public void removeHadoopDbParameters(DatabaseConnection connection) {
        HCRepositoryUtil.removeHadoopDbParameters(connection);
    }

    @Override
    public void copyHadoopCluster(final Item sourceItem, final IPath path) throws PersistenceException, BusinessException {
        copyHadoopCluster(sourceItem, path, null);
    }

    @Override
    public void copyHadoopCluster(final Item sourceItem, final IPath path, String newName) throws PersistenceException,
            BusinessException {
        if (isHadoopClusterItem(sourceItem)) {
            IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
            HadoopClusterConnectionItem sourceClusterItem = (HadoopClusterConnectionItem) sourceItem;
            HadoopClusterConnectionItem targetClusterItem = null;
            if (StringUtils.isNotBlank(newName)) {
                targetClusterItem = (HadoopClusterConnectionItem) factory.copy(sourceClusterItem, path, newName);
            } else {
                targetClusterItem = (HadoopClusterConnectionItem) factory.copy(sourceClusterItem, path, true);
            }
            HadoopClusterConnection targetClusterConnection = (HadoopClusterConnection) targetClusterItem.getConnection();
            targetClusterConnection.getConnectionList().clear();
            String targetClusterId = targetClusterItem.getProperty().getId();
            Set<Item> sourceSubitems = HCRepositoryUtil.getSubitemsOfHadoopCluster(sourceClusterItem);
            for (Item subitem : sourceSubitems) {
                Item newSubitem = factory.copy(subitem, path, true);
                if (newSubitem instanceof HadoopSubConnectionItem) {
                    ((HadoopSubConnection) ((HadoopSubConnectionItem) newSubitem).getConnection())
                            .setRelativeHadoopClusterId(targetClusterId);
                    targetClusterConnection.getConnectionList().add(newSubitem.getProperty().getId());
                } else if (subitem instanceof DatabaseConnectionItem) {
                    ((DatabaseConnection) ((DatabaseConnectionItem) newSubitem).getConnection()).getParameters().put(
                            ConnParameterKeys.CONN_PARA_KEY_HADOOP_CLUSTER_ID, targetClusterId);
                }
                factory.save(newSubitem);
            }
            factory.save(targetClusterItem);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.hadoop.IHadoopClusterService#getHadoopCustomLibraries()
     */
    @Override
    public Map<String, String> getHadoopCustomLibraries(String clusterId) {
        Map<String, String> customLibraries = new HashMap<>();
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil.getRelativeHadoopClusterConnection(clusterId);
        if (hadoopClusterConnection != null) {
            EMap<String, String> parameters = hadoopClusterConnection.getParameters();
            for (String key : parameters.keySet()) {
                customLibraries.put(key, parameters.get(key));
            }
        }
        return customLibraries;
    }

    @Override
    public IRepositoryTypeProcessor getHadoopSubMultiRepTypeProcessor(String[] repositoryTypes) {
        return new HadoopSubMultiRepTypeProcessor(repositoryTypes);
    }

    @Override
    public boolean hasDiffsFromClusterToProcess(Item item, IProcess process) {
        if (item == null || process == null) {
            return false;
        }

        // if (hcConnection instanceof HadoopClusterConnection) {
        // return HCRepositoryUtil.detectClusterChangeOfProcess((HadoopClusterConnection) hcConnection, process);
        // }
        String propertyParamName = MR_PROPERTY_PREFIX + EParameterName.PROPERTY_TYPE.getName();
        String propertyRepTypeParamName = MR_PROPERTY_PREFIX + EParameterName.REPOSITORY_PROPERTY_TYPE.getName();

        IElementParameter elementParameter = process.getElementParameter(propertyParamName);
        if (elementParameter != null && EmfComponent.REPOSITORY.equals(elementParameter.getValue())) {
            IElementParameter repositoryParam = process.getElementParameter(propertyRepTypeParamName);
            if (item.getProperty().getId().equals(repositoryParam.getValue())) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    @Override
    public Connection getHadoopClusterConnectionBySubConnection(Connection hadoopSubConnection) {
        return HCRepositoryUtil.getRelativeHadoopClusterConnection(hadoopSubConnection);
    }

    @Override
    public String getHadoopClusterProperties(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.getHadoopProperties();
        }

        return null;
    }

    @Override
    public Item getHadoopClusterItemById(String id) {
        HadoopClusterConnectionItem connectionItem = HCRepositoryUtil.getRelativeHadoopClusterItem(id);
        if (connectionItem == null) { // In case it is a hadoopsubconnection.
            connectionItem = HCRepositoryUtil.getHadoopClusterItemBySubitemId(id);
            if (connectionItem == null) {
                Item item = HCRepositoryUtil.getRelativeItem(id);
                if (item != null) {
                    Project project = new Project(ProjectManager.getInstance().getProject(item.getProperty()));
                    connectionItem = HCRepositoryUtil.getHadoopClusterItemBySubitemId(project, id);
                }
            }
        }
        return connectionItem;
    }

    @Override
    public String getCustomConfsJarName(String id) {
        return getCustomConfsJarName(id, true, true);
    }

    @Override
    public String getCustomConfsJarName(String id, boolean createJarIfNotExist, boolean addExtraIds) {
        HadoopClusterConnectionItem connectionItem = (HadoopClusterConnectionItem) getHadoopClusterItemById(id);
        if (connectionItem != null) {
            return getCustomConfsJarName(connectionItem, createJarIfNotExist, addExtraIds);
        }
        return null;
    }

    @Override
    public String getCustomConfsJarName(ConnectionItem connectionItem, boolean createJarIfNotExist, boolean addExtraIds) {
        if (connectionItem instanceof HadoopClusterConnectionItem) {
            HadoopClusterConnectionItem item = (HadoopClusterConnectionItem) connectionItem;
            HadoopClusterConnection connection = (HadoopClusterConnection) item.getConnection();
            if (connection != null && connection.isUseCustomConfs()) {
                String extraIds = null;
                if (addExtraIds && connection.isContextMode()) {
                    ContextType contextType = ConnectionContextHelper.getContextTypeForContextMode(connection, true);
                    extraIds = contextType.getName();
                }
                if (extraIds == null) {
                    return HadoopConfsUtils.getConfsJarDefaultName(item, createJarIfNotExist);
                } else {
                    return HadoopConfsUtils.getConfsJarDefaultName(item, createJarIfNotExist, extraIds);
                }
            }
        }

        return null;
    }

    @Override
    public void useCustomConfsJarIfNeeded(List<ModuleNeeded> modulesNeeded, String clusterId) {
        Item item = getHadoopClusterItemById(clusterId);
        if (item instanceof HadoopClusterConnectionItem) {
            HadoopClusterConnectionItem connectionItem = (HadoopClusterConnectionItem) item;
            HadoopClusterConnection connection = (HadoopClusterConnection) connectionItem.getConnection();
            if (connection.isUseCustomConfs()) {
                Set<String> confsJarNames = HadoopConfsUtils.getConfsJarDefaultNames(connectionItem, true);
                for (String confsJarName : confsJarNames) {
                    addConfsModule(modulesNeeded, connection, confsJarName);
                }
            }
        }
    }

    private void addConfsModule(List<ModuleNeeded> modulesNeeded, HadoopClusterConnection connection, String customConfsJarName) {
        String context = customConfsJarName.substring(0, customConfsJarName.lastIndexOf(".")); //$NON-NLS-1$
        ModuleNeeded customConfsModule = new ModuleNeeded(context, customConfsJarName, null, true);
        Iterator<ModuleNeeded> moduleIterator = modulesNeeded.iterator();
        while (moduleIterator.hasNext()) {
            ModuleNeeded module = moduleIterator.next();
            String moduleName = module.getModuleName();
            if ("hadoop-conf.jar".equals(moduleName) || "hadoop-conf-kerberos.jar".equals(moduleName) //$NON-NLS-1$ //$NON-NLS-2$
                    || customConfsJarName.equals(moduleName)) {
                moduleIterator.remove();
            }
        }
        if (connection.isContextMode()) {
            customConfsModule.getExtraAttributes().put(HadoopConstants.IS_DYNAMIC_JAR, true);
        }
        modulesNeeded.add(customConfsModule);
    }

    @Override
    public boolean useClouderaNavi(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.isUseClouderaNavi();
        }
        return false;
    }

    @Override
    public String getClouderaNaviUserName(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.getClouderaNaviUserName();
        }
        return null;
    }

    @Override
    public String getClouderaNaviPassword(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.getClouderaNaviPassword();
        }
        return null;
    }

    @Override
    public String getClouderaNaviUrl(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.getClouderaNaviUrl();
        }
        return null;
    }

    @Override
    public String getClouderaNaviMetadataUrl(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.getClouderaNaviMetadataUrl();
        }
        return null;
    }

    @Override
    public String getClouderaNaviClientUrl(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.getClouderaNaviClientUrl();
        }
        return null;
    }

    @Override
    public boolean clouderaNaviAutoCommit(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.isClouderaAutoCommit();
        }
        return false;
    }

    @Override
    public boolean clouderaNaviDisableSSL(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.isClouderaDisableSSL();
        }
        return false;
    }

    @Override
    public boolean clouderaNaviDieOnError(Connection hadoopSubConnection) {
        HadoopClusterConnection hadoopClusterConnection = HCRepositoryUtil
                .getRelativeHadoopClusterConnection(hadoopSubConnection);
        if (hadoopClusterConnection != null) {
            return hadoopClusterConnection.isClouderaDieNoError();
        }
        return false;
    }

    @Override
    public String getRepositoryTypeOfHadoopSubItem(Item subItem) {
        return HCRepositoryUtil.getRepositoryTypeOfHadoopSubItem(subItem);
    }

    @Override
    public boolean isUseDynamicConfJar(String id) {
        Item item = getHadoopClusterItemById(id);
        if (item instanceof HadoopClusterConnectionItem) {
            HadoopClusterConnectionItem hcItem = (HadoopClusterConnectionItem) item;
            HadoopClusterConnection hcConnection = (HadoopClusterConnection) hcItem.getConnection();
            return hcConnection.isUseCustomConfs() && hcConnection.isContextMode() && !ProcessorUtilities.isExportAsOSGI();
        }
        return false;
    }

    @Override
    public boolean updateConfJarsByContextGroup(ContextItem contextItem, Map<String, String> contextGroupRanamedMap) {
        boolean updated = false;
        try {
            List<IRepositoryViewObject> allHadoopClusterRepObjs = ProxyRepositoryFactory.getInstance()
                    .getAll(ProjectManager.getInstance().getCurrentProject(), getHadoopClusterType());
            for (IRepositoryViewObject repObj : allHadoopClusterRepObjs) {
                HadoopClusterConnectionItem hcItem = (HadoopClusterConnectionItem) repObj.getProperty().getItem();
                HadoopClusterConnection hcConnection = (HadoopClusterConnection) hcItem.getConnection();
                if (hcConnection.isUseCustomConfs()) {
                    String contextId = hcConnection.getContextId();
                    if (contextId != null && contextId.equals(contextItem.getProperty().getId())) {
                        boolean renamed = HadoopConfsUtils.renameContextGroups(hcConnection, contextGroupRanamedMap);
                        if (renamed) {
                            HadoopConfsUtils.getConfsJarDefaultNames(hcItem, true);
                        }
                    }
                }
            }
        } catch (PersistenceException e) {
            ExceptionHandler.process(e);
        }
        return updated;
    }

    @Override
    public boolean hideSensitiveValues(Item item) {
        if(!(item instanceof HadoopClusterConnectionItem)) {
            return false;
        }
        HadoopClusterConnection hadoopConn = (HadoopClusterConnection) ((HadoopClusterConnectionItem)item).getConnection();
        hadoopConn.setClouderaNaviClientUrl("");
        hadoopConn.setClouderaNaviMetadataUrl("");
        hadoopConn.setClouderaNaviPassword("");
        hadoopConn.setClouderaNaviUrl("");
        hadoopConn.setClouderaNaviUserName("");
        hadoopConn.setComment("");
        hadoopConn.setJobTrackerURI("");
        hadoopConn.setJtOrRmPrincipal("");
        hadoopConn.setMaprTPassword("");
        hadoopConn.setNameNodeURI("");
        hadoopConn.setServer("");
        return true;
    }

}
