package database;

public interface VariantDatabaseInterface {

    abstract public DatabaseQueryResult getHgncData(String gene);

    abstract public DatabaseQueryResult getVariantById(String id);

    abstract public DatabaseQueryResult getVariantsData(String gene, 
                                                        int skip,
                                                        int limit);

    abstract public void closeConnection();

    abstract public void setGeneNameDatabase(GeneNameDatabase geneDb);

}