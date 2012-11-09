package network;

import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import org.eclipse.jetty.http.HttpMethods;

public class UriRewriter {
    
    public UriRewriter() {
        super();
        staticRules = new HashMap<String,   /* method */ 
                                  HashMap<String, String> /* rules */>();
        prefixRules = new HashMap<String, Vector<String>>();
        prefixRuleMap = new HashMap<String, String>();
        salts = new HashMap<String, String>();
        random = new Random(708833134193L);
    }
    
    public String rewrite(String uri) {
        return rewrite(HttpMethods.GET, uri);
    }
    
    public String rewrite(String method, String uri) {

        // First, check if there's a static rule.
        // Static rules take precedence over dynamic rules.
        if (staticRules.containsKey(method)) {
            HashMap<String, String> map = staticRules.get(method);
            if (map.containsKey(uri)) {
                return map.get(uri);
            }
        }

        // No static rule, so check if there's a dynamic one.
        if (prefixRules.containsKey(method)) {
            Vector<String> v = prefixRules.get(method);
            String prefix = "";    // The longest prefix.
            
            // Find the longest matching prefix of uri.
            for (int i = 0; i < v.size(); ++i) {
                String s = v.get(i);
                if (uri.startsWith(s)) {
                    if (s.length() > prefix.length()) {
                        prefix = s;
                    }
                }
            }
            if (prefix.length() > 0) {
                String salt = salts.get(method);
                String key = salt.concat(prefix);
                String replacement = prefixRuleMap.get(key);
                return uri.replaceFirst(prefix, replacement);
            }
        }

        // No rewrite rule found. Return the uri itself.
        return uri;
     }
    
    public void addPrefixRule(String from, String to) {
        addPrefixRule(HttpMethods.GET, from, to);
    }

    public void addPrefixRule(String method, String from, String to) {
        if (! prefixRules.containsKey(method)) {
            prefixRules.put(method, new Vector<String>());
            salts.put(method, Long.toString(random.nextLong()));
        }
        if (prefixRuleMap.containsKey(from)) {
            // Simply replace the old value.
            prefixRuleMap.put(from, to);
        } else {
            Vector<String> v = prefixRules.get(method);
            String salt = salts.get(method);
            v.add(from);
            prefixRuleMap.put(salt.concat(from), to);
        }
    }

    public void addStaticRule(String from, String to) {
        addStaticRule(HttpMethods.GET, from, to);
    }

    public void addStaticRule(String method, String from, String to) {
        if (! staticRules.containsKey(method)) {
            staticRules.put(method, new HashMap<String, String>());
        }
        staticRules.get(method).put(from, to);
    }

    private final HashMap<String, HashMap<String, String>> staticRules;
    private final HashMap<String, Vector<String>> prefixRules;
    private final HashMap<String, String> prefixRuleMap;
    
    // For dynamic rules, different key may appear in several methods.
    // Hence, use a per-method salt.
    private final HashMap<String, String> salts;
    
    private static Random random;
    
}
