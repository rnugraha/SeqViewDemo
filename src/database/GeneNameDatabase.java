package database;

public interface GeneNameDatabase {

    abstract public DatabaseQueryResult getGeneNamesAndSymbols(String filterName);

    /**
     * Get HGNC (HUGO Gene Nomenclature Committee) data about a gene.
     * 
     * @param symbol    approved symbol name of the desired gene
     * @return          HGNC data 
     */
    abstract public HgncData getHgncData(String symbol);

}