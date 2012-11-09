package database;

public abstract class GeneNameDatabase {
    public GeneNameDatabase(DatabaseConfig config) {}
    
    abstract public DatabaseQueryResult getGeneNamesAndSymbols(String filterName);
    
    abstract public HgncData getHgncData(String symbol);
    
}
