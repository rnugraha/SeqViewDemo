package database;

public class VariantDatabaseInMemory extends VariantDabaseCommon {

    public VariantDatabaseInMemory(GeneNameDatabase geneDb) {
        super();
    }

    public VariantDatabaseInMemory() {
        super();
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

}
