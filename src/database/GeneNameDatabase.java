package database;

public abstract class GeneNameDatabase implements GeneNameDatabaseInterface {
    public GeneNameDatabase(DatabaseConfig config) {}
    
    @Override
    abstract public DatabaseQueryResult getGeneNamesAndSymbols(String filterName);
    
    @Override
    abstract public HgncData getHgncData(String symbol);
    
}
