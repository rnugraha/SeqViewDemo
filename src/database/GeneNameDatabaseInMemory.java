package database;

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

    @Override
    public DatabaseQueryResult getGeneNamesAndSymbols(String filterName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HgncData getHgncData(String symbol) {
        // TODO Auto-generated method stub
        return null;
    }

}
