package network;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiMap;

public class URL {
    
    public URL(Request request) {
        super();
        this.uri = request.getUri();
        paths = new ArrayList<String>(Arrays.asList(uri.getPath().split("/")));
        paths.remove(0);
        params = request.getParameters();
    }
    
    public String getRoute() {
        return paths.get(0);
    }
    
    public String getPathComponent(int i) {
        if (paths.size() <= i) {
            return "";
        }
        return paths.get(i);
    }

    @Override
    public String toString() {
        return uri.toString();
    }

    private final HttpURI uri;
    ArrayList<String> paths;
    MultiMap<String> params;
}
