package database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.OStorage;

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

    public VariantDatabaseInMemory(String src) {
        super();
        source = new File(src);
        if (source.isDirectory() || !source.exists() || !source.canRead()) {
            source = new File(DEFAULT_VARIANT_DATA);
        }
        dbInMemory = false;
    }

    public VariantDatabaseInMemory() {
        this(DEFAULT_VARIANT_DATA);
    }
    
    @Override
    public DatabaseQueryResult getVariantById(String id) {
        if (dbInMemory == false) {
            initdb();
        }
        
        return null;
    }

    @Override
    public DatabaseQueryResult getVariantsData(String gene, int skip, int limit) {
        if (dbInMemory == false) {
            initdb();
        }
        
        return null;
    }

    private void initdb() {
        database = new ODatabaseDocumentTx(DATABASE_NAME).create();
        database.addCluster(CLUSTER_NAME, OStorage.CLUSTER_TYPE.MEMORY);
        try {
            ObjectMapper mapper = new ObjectMapper();
            @SuppressWarnings("unchecked")
            ArrayList<Map<String,?>> objects = mapper.readValue(source, ArrayList.class);
            int size = objects.size();
            for (int i = 0; i < size; ++i) {
                @SuppressWarnings("unchecked")
                Map<String, Object> o = (Map<String, Object>) objects.get(i);
                database.save(new ODocument(o), CLUSTER_NAME);
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
            return;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        dbInMemory = true;
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
    private boolean dbInMemory;
    private ODatabaseDocumentTx database;
    
    static {
        DEFAULT_VARIANT_DATA = abspath("genedata/variants.json");
    }
}
