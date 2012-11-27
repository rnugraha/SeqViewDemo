package network;

public class EnsemblURL {
    
    public static String uri(String id) {
        String uri = ensemblUrl.concat(id);
        return uri;
    }
    
    private static final String ensemblUrl = "http://www.ensembl.org/Homo_sapiens/Gene/Summary?g=";
}
