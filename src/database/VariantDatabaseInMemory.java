package database;

public class VariantDatabaseInMemory implements VariantDatabase {

    public VariantDatabaseInMemory(GeneNameDatabase geneDb) {
        super();
        this.geneDb = geneDb;
    }

    public VariantDatabaseInMemory() {
        super();
        geneDb = null;
    }

    @Override
    public DatabaseQueryResult getHgncData(String gene) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DatabaseQueryResult getVariantById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DatabaseQueryResult getVariantsData(String gene, int skip, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setGeneNameDatabase(GeneNameDatabase geneDb) {
        this.geneDb = geneDb;
    }

    private GeneNameDatabase geneDb;
}
