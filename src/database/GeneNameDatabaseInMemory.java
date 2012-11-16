package database;

import java.io.File;

/**
 * This class implements an in-memory database for gene names.
 * <p>The idea is to give users a chance to use a 
 * <a href="http://www.json.org/">JSON</a> file of their own 
 * and feed it into the database engine. All this without 
 * any extensive application configuration.</p>
 * <p>The database engine used is 
 * <a href="http://www.orientdb.org/">OrientDB</a>.</p>
 * 
 * @author Tuomas Pellonperä
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
    }

    @Override
    public DatabaseQueryResult getGeneNamesAndSymbols(String filterName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HgncData getHgncData(String symbol) {
        
        return null;
    }
    
    private static String abspath(String filename) {
        return HOMEDIR.concat(SEPARATOR).concat(filename);
    }

    private static final String HOMEDIR = System.getProperty("user.dir");
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static final String DEFAULT_HGNC_DATA;
    
    private File source;
    
    static {
        DEFAULT_HGNC_DATA = abspath("genedata/hgnc_genes.json");
    }
}
