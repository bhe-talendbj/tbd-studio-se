package org.talend.repository.nosql.db.util.neo4j;

import org.eclipse.emf.common.util.EMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.repository.model.nosql.NoSQLConnection;
import org.talend.repository.model.nosql.NosqlFactory;
import org.talend.repository.nosql.db.common.neo4j.INeo4jAttributes;
import org.talend.repository.nosql.db.common.neo4j.INeo4jConstants;
import org.talend.repository.nosql.factory.NoSQLRepositoryFactory;
import org.talend.repository.nosql.metadata.IMetadataProvider;
import org.talend.utils.io.FilesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//TODO: FIXME
@Ignore("failed because workbench has not been created")
public class Neo4jMetadataProviderTest {

    NoSQLConnection localConnection;

    File tmpFolder = null;

    @Before
    public void prepare() {
        tmpFolder = org.talend.utils.files.FileUtils.createTmpFolder("neo4jLocalConnection", "test"); //$NON-NLS-1$ //$NON-NLS-2$
        localConnection = NosqlFactory.eINSTANCE.createNoSQLConnection();
    }

    @After
    public void clean() {
        if (tmpFolder != null) {
            FilesUtils.deleteFolder(tmpFolder, true);
        }
    }


    @Test
    public void testLocalRetrieveSchema() throws Exception {
        EMap<String, String> attributes = localConnection.getAttributes();
        attributes.put(INeo4jAttributes.REMOTE_SERVER, "false"); //$NON-NLS-1$

        attributes.put(INeo4jAttributes.DATABASE_PATH, tmpFolder.getCanonicalPath());

        attributes.put(INeo4jAttributes.DB_VERSION, INeo4jConstants.NEO4J_2_3_X);
        localConnection.setDbType("NEO4J");

        ExecutorService threadExecutor = null;
        try {
            threadExecutor = Executors.newSingleThreadExecutor();
            Future future = threadExecutor.submit(new Runnable() {

                public void run() {
                    try {
                        Neo4jConnectionUtil.checkConnection(localConnection);
                        List<MetadataColumn> metadataColumns = new ArrayList<MetadataColumn>();
                        String cypher = "create (n:Test {first_name : 'Peppa', last_name : 'Pig'})\r\nreturn n;";
                        IMetadataProvider metadataProvider = NoSQLRepositoryFactory.getInstance()
                                .getMetadataProvider(localConnection.getDbType());
                        metadataColumns = metadataProvider.extractColumns(localConnection, cypher);

                        String lastName = metadataColumns.get(0).getName();
                        assertEquals("last_name", lastName);
                        String firstName = metadataColumns.get(1).getName();
                        assertEquals("first_name", firstName);
                    } catch (Exception e) {
                        fail(e.getMessage());
                    }
                }
            });

            while (true) {
                if (future.get() == null) {
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception exception) {
            fail(exception.getMessage());
        } finally {
            if (threadExecutor != null) {
                threadExecutor.shutdown();
                threadExecutor = null;
            }
        }
    }

}
