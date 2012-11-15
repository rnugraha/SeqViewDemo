package database;

public interface GeneNameDatabaseInterface {

    abstract public DatabaseQueryResult getGeneNamesAndSymbols(String filterName);

    abstract public HgncData getHgncData(String symbol);

}