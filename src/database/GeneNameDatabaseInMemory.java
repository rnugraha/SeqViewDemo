package database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import security.SqlParameter;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorCluster;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.core.storage.OStorage;

/**
 * This class implements an in-memory database for gene names.
 * <p>The idea is to give users a chance to use a 
 * <a href="http://www.json.org/">JSON</a> file of their own 
 * and feed it into the database engine. All this without 
 * any extensive application configuration.</p>
 * <p><strong>Note:</strong> the JSON file must include an 
 * <em>array</em> of proper objects.
 * <p>The database engine used is 
 * <a href="http://www.orientdb.org/">OrientDB</a>.</p>
 * 
 * @author Tuomas PellonperŠ
 *
 */

public class GeneNameDatabaseInMemory implements GeneNameDatabase {
    
    public GeneNameDatabaseInMemory() {
        this(DEFAULT_HGNC_DATA);
    }

    public GeneNameDatabaseInMemory(DatabaseConfig conf) {
        this(DEFAULT_HGNC_DATA);
    }

    public GeneNameDatabaseInMemory(String src) {
        source = new File(src);
        if (source.isDirectory() || !source.exists()) {
            source = new File(DEFAULT_HGNC_DATA);
        }
        sourceIsRead = false;
    }

    @Override
    public DatabaseQueryResult getGeneNamesAndSymbols(String filterName) {
        if (sourceIsRead == false) {
            initdb();
        }
        
        DatabaseQueryResult results = new DatabaseQueryResult();
        ArrayList<String> geneSymbols = new ArrayList<String>();
        ArrayList<String> geneNames = new ArrayList<String>();
        results.set("symbols", geneSymbols);
        results.set("names", geneNames);
        
        // Prevent possible SQL injections.
//        if (sqlInjectionResistant(filterName) == false) {
//            return results;
//        }
//        
//        StringBuilder sb = new StringBuilder();
//        sb.append("SELECT * FROM cluster:%s");
//        sb.append(' ');
//        sb.append("WHERE approved_symbol like '%%%s%%'");
//        sb.append(' ');
//        sb.append("or approved_name like '%%%s%%'");
//        String fmt = sb.toString();
//        String sql = String.format(fmt, CLUSTER_NAME, filterName, filterName);
//        
//        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
//        List<ODocument> result = database.command(query).execute();
//        for (Iterator<ODocument> it = result.iterator(); it.hasNext();) {
//            ODocument d = it.next();
//            geneSymbols.add((String) d.field("approved_symbol"));
//            geneNames.add((String) d.field("approved_name"));
//        }
        ORecordIteratorCluster<ODocument> cit = database.browseCluster(CLUSTER_NAME);
        for (ODocument d : cit) {
            String symbol = d.field("approved_symbol");
            String name = d.field("approved_name");
            if (name.toLowerCase().contains(filterName) 
                    || symbol.toLowerCase().contains(filterName)) {
                geneNames.add(name);
                geneSymbols.add(symbol);
            }
        }
        return results;
    }

    @Override
    public HgncData getHgncData(String symbol) {
        HgncData hgnc = null;
        if (sourceIsRead == false) {
            initdb();
        }
        final String fmt = "SELECT * FROM cluster:%s WHERE approved_symbol = ?";
        String sql = String.format(fmt, CLUSTER_NAME);
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<ODocument>(sql);
        List<ODocument> result = database.command(query).execute(symbol);
        if (result.size() > 0) {
            ODocument doc = result.get(0);
            hgnc = new HgncData(doc2map(doc));
        }
        return hgnc;
    }
    
    private Map<String, Object> doc2map(ODocument doc) {
        String[] properties = doc.fieldNames();
        HashMap<String, Object> map = new HashMap<String, Object>(properties.length);
        for (int i = 0; i < properties.length; ++i) {
            String p = properties[i];
            map.put(p, doc.field(p));
        }
        return map;
    }
    
    private boolean sqlInjectionResistant(String s) {
        int mode = 0;
        mode |= SqlParameter.ALPHANUMERIC;
        mode |= SqlParameter.HYPHEN;
        mode |= SqlParameter.SPACE;
        mode |= SqlParameter.PARENTHESES;
        mode |= SqlParameter.COMMA;
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
        sourceIsRead = true;
    }
    
    private static String abspath(String filename) {
        return HOMEDIR.concat(SEPARATOR).concat(filename);
    }

    private static final String HOMEDIR = System.getProperty("user.dir");
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static final String DEFAULT_HGNC_DATA;
    
    private static final String DATABASE_NAME = "memory:hgnc";
    private static final String CLUSTER_NAME = "hgnc";
    
    private File source;
    private boolean sourceIsRead;
    private ODatabaseDocumentTx database;
    
    static {
        DEFAULT_HGNC_DATA = abspath("genedata/hgnc_genes.json");
    }
}
