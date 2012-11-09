package handlers;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import network.QueryStringBuilder;
import network.Routes;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Request;

public class NcbiHandler extends BaseHandler {

    @Override
    public void handle(String target, 
                       Request baseRequest,
                       HttpServletRequest request, 
                       HttpServletResponse response)
                throws IOException, 
                       ServletException {
        
        String route = this.getRoute();
        String path = request.getRequestURI();
        
        @SuppressWarnings("unchecked")
        Map<String, String[]> map = request.getParameterMap();
        String query = QueryStringBuilder.build(map);
        String url = "";
        HttpResponse res;
        
        if (route.equals(Routes.NCBI_IMAGES)) {
            
            url = makeUrl(path, route, IMAGES_URL);
            
        } else if (route.equals(Routes.NCBI_EXTJS)) {
            
            url = makeUrl(path, route, EXTJS_URL);
            
        } else if (route.equals(Routes.NCBISV)) {
            
            String cgi = path.split("/")[2];
            url = SVIEWER_URL.concat(cgi);
            
            // Avoid URISyntaxException. (http://perishablepress.com/how-to-write-valid-url-query-string-parameters/)
            query = query.replace(" ", "+"); 
            query = query.replace("|", "%7C");
            url = url.concat(query);
            
        }
        
//        String msg = String.format("Route: %s\nPath: %s\nURL: %s", route, path, url);
//        System.err.println(msg);
//        
//        System.err.println("Query string: " + query + ".");
//        System.err.println("Encoded query string:\n\t" + java.net.URLEncoder.encode(query, "UTF8") + ".");
//        System.err.println("Servlet path: " + request.getServletPath() + ".");
//        System.err.println("Requested URL: " + request.getRequestURL() + ".");
//        System.err.println("Requested path: " + path + ".");
//        System.err.println("Path info: " + request.getPathInfo() + ".");
//        System.err.println("Query string: " + request.getQueryString() + ".");
//        System.err.println("Query string (baseRequest): " + baseRequest.getQueryString() + ".");
//        Set<String> keys = map.keySet();
//        Iterator<String> it = keys.iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            String value = map.get(key)[0];
//            String s = String.format("\t%s = %s", key, value);
//            System.err.println(s);
//        }
//        System.err.println("Query string: " + QueryStringBuilder.build(map) + ".");
        
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        res = httpclient.execute(httpget);
        
        Header[] headers = res.getAllHeaders();
        HttpEntity entity = res.getEntity();
        long length = entity.getContentLength();
        String mime = "";
        for (Header h : headers) {
            response.setHeader(h.getName(), h.getValue());
            if (h.getName().equals("Content-Type")) {
                mime = h.getValue();
            }
        }
        this.sendStream(entity.getContent(), mime, length, 
                        baseRequest, request, response);

    }
    
    private String makeUrl(String path, String route, String url) {
        String file = path.replaceFirst(route, "");
        
        // Now FILE begins with '/'. Let's fix that.
        file = file.substring(1);
        return url.concat(file);
    }
    
    private final String BASE_URL = "http://www.ncbi.nlm.nih.gov";
    private final String SVIEWER_URL = BASE_URL.concat("/projects/sviewer/");
    private final String IMAGES_URL = BASE_URL.concat("/projects/sviewer/images/");
    private final String EXTJS_URL = BASE_URL.concat("/core/extjs/ext-3.4.0/");

}
