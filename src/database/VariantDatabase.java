package database;

abstract public class VariantDatabase implements VariantDatabaseInterface {
    
    public VariantDatabase(DatabaseConfig config) {}
    
    @Override
    abstract public DatabaseQueryResult getHgncData(String gene);
    
    @Override
    abstract public DatabaseQueryResult getVariantById(String id);
    
    @Override
    abstract public DatabaseQueryResult getVariantsData(String gene, 
                                                        int skip, 
                                                        int limit);
    
    @Override
    abstract public void closeConnection();

    @Override
    abstract public void setGeneNameDatabase(GeneNameDatabaseInterface geneDb);
    
}
