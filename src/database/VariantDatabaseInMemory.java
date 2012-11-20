package database;

import java.io.File;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

/**
 * This class implements an in-memory database for gene variants.
 * <p>The idea is to give users a chance to use a 
 * <a href="http://www.json.org/">JSON</a> file of their own 
 * and feed it into the database engine. All this without 
 * any extensive application configuration.</p>
 * <p><strong>Note:</strong> the JSON file must include an 
 * <em>array</em> of proper objects.
 * <p>The database engine used is 
 * <a href="http://www.orientdb.org/">OrientDB</a>.</p>
 * 
 * @author Tuomas Pellonperä
 *
 */

public class VariantDatabaseInMemory extends VariantDabaseCommon {

    public VariantDatabaseInMemory(GeneNameDatabase geneDb) {
        super(geneDb);
    }

    public VariantDatabaseInMemory() {
        super();
    }

    @Override
    public DatabaseQueryResult getVariantById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DatabaseQueryResult getVariantsData(String gene, int skip, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    private static String abspath(String filename) {
        return HOMEDIR.concat(SEPARATOR).concat(filename);
    }

    private static final String HOMEDIR = System.getProperty("user.dir");
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static final String DEFAULT_VARIANT_DATA;
    
    private static final String DATABASE_NAME = "memory:variants";
    private static final String CLUSTER_NAME = "variants";
    
    private File source;
    private boolean sourceIsRead;
    private ODatabaseDocumentTx database;
    
    static {
        DEFAULT_VARIANT_DATA = abspath("genedata/variants.json");
    }
}
