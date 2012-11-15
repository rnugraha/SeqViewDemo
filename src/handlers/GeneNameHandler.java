package handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import network.MimeType;

import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.server.Request;

import database.DatabaseQueryResult;
import database.GeneNameDatabaseInterface;

public class GeneNameHandler extends BaseHandler {

    public GeneNameHandler() {
        super();
    }
    
    @Override
    public void handle(String target, 
                       Request baseRequest,
                       HttpServletRequest request, 
                       HttpServletResponse response)
                throws IOException, ServletException {
        
        if (baseRequest.isHandled()) {
            return;
        }
        
        String method = request.getMethod();
        
        if (HttpMethods.GET.equals(method)) {
            
//            String path = request.getRequestURI();
//            System.err.println("Servlet path: " + request.getServletPath() + ".");
//            System.err.println("Requested URL: " + request.getRequestURL() + ".");
//            System.err.println("Requested path: " + path + ".");
//            System.err.println("Query string: " + URIUtil.decodePath(request.getQueryString()) + ".");
//            System.err.println("Parameter 'filter': " + request.getParameter("filter"));
//            System.err.println("Matches: " + this.getQueryParam(request.getParameter("filter")));
//            System.err.println("Parameter 'query': " + request.getParameter("query"));
//            System.err.println("Matches: " + this.getQueryParam(request.getParameter("query")));
            
            String query = this.getQueryParam(request.getParameter("query"));
//            System.err.println(String.format("Filtering with '%s'.", query));
            DatabaseQueryResult r = db.getGeneNamesAndSymbols(query);
            @SuppressWarnings("unchecked")
            ArrayList<String> names = (ArrayList<String>) r.get("names");
            @SuppressWarnings("unchecked")
            ArrayList<String> symbols = (ArrayList<String>) r.get("symbols");
            
            if (names.isEmpty()) {
                sendString("[]", MimeType.JSON, baseRequest, request, response);
                this.setRequestHandled(baseRequest);
                return;
            }
            
            StringBuilder sbuf = new StringBuilder();
            int index = 0;
            String objFormat = "{\"name\":\"%s\"}";
            String nameFormat = "%s (%s)";
            String name, symbol, elem, obj;
            
            // First loop iteration.
            name = names.get(index);
            symbol = symbols.get(index);
            elem = String.format(nameFormat, symbol, name);
            obj = String.format(objFormat, elem);
            sbuf.append(obj);
            
            // The rest of the loop iteration.
            for (index = 1; index < names.size(); ++index) {
                sbuf.append(",");
                name = names.get(index);
                symbol = symbols.get(index);
                elem = String.format(nameFormat, symbol, name);
                obj = String.format(objFormat, elem);
                sbuf.append(obj);
            }
            
            // First loop iteration.
//            sbuf.append(String.format(objFormat, symbols.get(index)));
//            sbuf.append(",");
//            sbuf.append(String.format(objFormat, names.get(index)));
            
            // The rest of the loop iteration.
//            for (index = 1; index < names.size(); ++index) {
//                sbuf.append(",");
//                sbuf.append(String.format(objFormat, symbols.get(index)));
//                sbuf.append(",");
//                sbuf.append(String.format(objFormat, names.get(index)));
//            }
            
//            Util util = new Util();
//            String json = util.toJSONString(formattedNames);
            String json = String.format("[%s]", sbuf.toString()); 
//            System.err.println(json.substring(0, 1024));
            sendString(json, MimeType.JSON, baseRequest, request, response);
            this.setRequestHandled(baseRequest);

        }
    }
    
    public void setDatabase(GeneNameDatabaseInterface db) {
        this.db = db;
    }
    
    private String getQueryParam(String param) {
        // [{"property":"name","value":"gamm"}]
        // [{"property":"name","value":"gamma-aminobutyric acid (GABA) B receptor, 1"}]
        
//        if (param != null) {
//            System.err.println("Param is " + param);
//            Matcher m = filterPattern.matcher(param);
//            if (m.matches()) {
//                return m.group(1);
//            }
//        }
        if (param == null || param.trim().length() == 0) {
            return "";
        }
        return param.trim();
    }
    
    private GeneNameDatabaseInterface db;
    private static Pattern filterPattern = Pattern.compile("^\\[\\{\"property\":\"name\",\"value\":\"([^\"]+)\"\\}\\]$");
    
}
