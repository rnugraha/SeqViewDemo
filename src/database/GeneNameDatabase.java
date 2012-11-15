package database;

public interface GeneNameDatabase {

    abstract public DatabaseQueryResult getGeneNamesAndSymbols(String filterName);

    abstract public HgncData getHgncData(String symbol);

}