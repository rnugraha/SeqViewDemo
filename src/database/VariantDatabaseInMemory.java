package database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.varioml.jaxb.Variant;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
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
    
    public static void main(String[] args) {
        VariantDatabaseInMemory db = new VariantDatabaseInMemory("/Users/pellonpe/tmp/variants.json");
        db.getVariantsData("AGL", 100, 5);
    }

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
        final String COUNT_PROPERTY = "total_count";
        final String VARIANTS_PROPERTY = "variants";

        DatabaseQueryResult result = new DatabaseQueryResult();
        result.set(COUNT_PROPERTY, new Long(0));
        result.set(VARIANTS_PROPERTY, new ArrayList<Variant>());

        skip = (skip < 0 ? DEFAULT_SKIP : skip);
        limit = (limit < 0 ? DEFAULT_LIMIT : limit);
        
        if (dbInMemory == false) {
            initdb();

            if (dbInMemory == false) {
                // For some reason, we cannot open the database.
                // Return an empty query result.
                return result;
            }
        }
        System.err.println(database.countClusterElements(CLUSTER_NAME));
        String fmt = "SELECT FROM cluster:%s WHERE genes contains(accession = %s) SKIP %d LIMIT %d";
        String sql = String.format(fmt, CLUSTER_NAME, gene, skip, limit);
        System.err.println(sql);
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
        List<ODocument> queryResult = database.command(query).execute(gene);
        for (int i = 0; i < queryResult.size(); ++i) {
            ODocument d = queryResult.get(i);
            System.err.println(d.field("ref_seq"));
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
    
    private static final int DEFAULT_SKIP = 0;
    private static final int DEFAULT_LIMIT = 20;
    
    private File source;
    private boolean dbInMemory;
    private ODatabaseDocumentTx database;
    
    static {
        DEFAULT_VARIANT_DATA = abspath("genedata/variants.json");
    }
}
