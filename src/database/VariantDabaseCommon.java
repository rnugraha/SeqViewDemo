package database;

public abstract class VariantDabaseCommon implements VariantDatabase {

    public VariantDabaseCommon() {
        super();
    }

    @Override
    public DatabaseQueryResult getHgncData(String gene) {
        HgncData hgnc = geneDb.getHgncData(gene);
        DatabaseQueryResult result = new DatabaseQueryResult();
        result.set("hgnc", hgnc);
        return result;
    }

    @Override
    public void setGeneNameDatabase(GeneNameDatabase geneDb) {
        this.geneDb = geneDb;
    }

    protected final GeneNameDatabase getGeneNameDatabase() {
        return geneDb;
    }

    private GeneNameDatabase geneDb;

}