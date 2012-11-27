package database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class VariantDatabaseMongoDB extends VariantDabaseCommon implements VariantDatabase {

    public VariantDatabaseMongoDB(DatabaseConfig config) 
           throws DatabaseConnectionException {
        
        super();
        this.connection = new MongoDatabaseConnection(config);
        this.collection = this.connection.getCollection();
        
        // Make sure database connection is properly closed whtn the 
        // program terminates.
        // http://www.developerfeed.com/threads/tutorial/understanding-java-shutdown-hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                closeConnection();
            }

            private void closeConnection() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    @Override
    public DatabaseQueryResult getVariantById(String id) {
        MongoUtil util = new MongoUtil();
        DatabaseQueryResult result = new DatabaseQueryResult();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));
        DBObject d = this.collection.findOne(query);
        @SuppressWarnings("unchecked")
        Map<String, Object> m = d.toMap();
        result.set("variant", m);
        return result;
    }

    @Override
    public DatabaseQueryResult getVariantsData(String gene, int skip, int limit) {
        
        MongoUtil util = new MongoUtil();
        long count = 0;
        List<Map<String, Object>> variants = new ArrayList<Map<String, Object>>();
        DBCursor cursor = null;
        BasicDBObject query = new BasicDBObject();
        GeneNameDatabase geneDb = getGeneNameDatabase();
        HgncData hgnc = geneDb.getHgncData(gene);
        
        query.put("genes.accession", gene);
        
        try {
            cursor = this.collection.find(query);
            count = cursor.count();
            cursor = cursor.skip( skip ).limit( limit );
            while (cursor.hasNext()) {
                DBObject dobj = cursor.next();

                /*
                 * Looks like Ext JS grid widget requires an ID for every 
                 * element it displays (note that the ID field is not 
                 * necessarily displayed). For now, we will use the builtin
                 * MongoDB ID-field.
                 */
                dobj.put("id", dobj.get("_id").toString());
                String hgncId = (String) hgnc.get("ensembl_id_ensembl");
                String uri = ensemblUrl.concat(hgncId);
                dobj.put("uri", uri);
                
//                @SuppressWarnings("unchecked")
//                Map<String, Object> props = dobj.toMap();
//                Set<String> keys = props.keySet();
//                Iterator<String> it = keys.iterator();
//                while (it.hasNext()) {
//                    String key = it.next();
//                    String value = dobj.get(key).toString();
//                    System.err.println(String.format("\tKey: '%s'; value: '%s'", key, value));
//                }
//                System.err.println();
                
                variants.add(dobj.toMap());
            }
        }
        finally {
            cursor.close();
        }
        
        DatabaseQueryResult results = new DatabaseQueryResult();
        results.set("total_count", new Long(count));
        results.set("variants", variants);
        
        return results;
    }   

    private static final String ensemblUrl = "http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=";
    
    private final MongoDatabaseConnection connection;
    //private DB database;
    private final DBCollection collection;

}
