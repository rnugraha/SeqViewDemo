package database;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.varioml.jaxb.Variant;
import org.varioml.util.Util;

import security.SqlParameter;

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
        DatabaseQueryResult result = new DatabaseQueryResult();

        if (dbInMemory == false) {
            initdb();
            if (dbInMemory == false) {
                return result;
            }
        }
        
        // Protect against SQL injections.
        if (!SqlParameter.sqlInjectionResistant(id, SqlParameter.ALPHANUMERIC)) {
            System.err.println("Possibly dangerous query parameter (id): '" + id + "'");
            return result;
        }

        String fmt = "SELECT FROM cluster:%s WHERE id = ?";
        String sql = String.format(fmt, CLUSTER_NAME);
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
        List<ODocument> queryResult = database.command(query).execute(id);
        if (queryResult.size() > 0) {
            Variant v = doc2variant(queryResult.get(0));
            result.set("variant", v);
        }
        return result;
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
        
        // Protect against SQL injections.
        if (!sqlInjectionResistant(gene)) {
            System.err.println("Possibly dangerous query parameter: '" + gene + "'");
            return result;
        }

        String fmt = "SELECT FROM cluster:%s WHERE genes contains(accession = %s) SKIP %d LIMIT %d";
        String sql = String.format(fmt, CLUSTER_NAME, gene, skip, limit);
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
        List<ODocument> queryResult = database.command(query).execute(gene);
        result.set(COUNT_PROPERTY, queryResult.size());
        @SuppressWarnings("unchecked")
        ArrayList<Variant> variants = (ArrayList<Variant>) result.get(VARIANTS_PROPERTY);
        for (int i = 0; i < queryResult.size(); ++i) {
            ODocument d = queryResult.get(i);
            variants.add(doc2variant(d));
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private Variant doc2variant(ODocument d) {
        @SuppressWarnings("rawtypes")
        Class cls = Variant.class;
        String json = d.toJSON();
        AnnotationIntrospector ai1 = new JacksonAnnotationIntrospector();
        AnnotationIntrospector ai2 = new JaxbAnnotationIntrospector();
        AnnotationIntrospector ai = new AnnotationIntrospector.Pair(ai1, ai2);
        ObjectMapper mapper = new ObjectMapper();
        mapper.getDeserializationConfig().withAnnotationIntrospector(ai);
        mapper.getSerializationConfig().withAnnotationIntrospector(ai);

        Object o = null;
        try {
            StringReader in = new StringReader(json);
            o = mapper.readValue(in, cls);
            in.close();
        } catch (Exception e) {
            Util.fatal(Util.class, e);
        }
        return (Variant) o;
    }
    
    private boolean sqlInjectionResistant(String s) {
        int mode = 0;
        mode |= SqlParameter.ALPHANUMERIC;
//        mode |= SqlParameter.HYPHEN;
//        mode |= SqlParameter.SPACE;
//        mode |= SqlParameter.PARENTHESES;
//        mode |= SqlParameter.COMMA;
        return SqlParameter.sqlInjectionResistant(s, mode);
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
