package security;

public class SqlParameter {
    
    public boolean sqlInjectionResistant(String s) {
        return sqlInjectionResistant(s, EVERYTHING);
    }
    
    public boolean sqlInjectionResistant(String s, int mode) {
        String pattern = "^[A-Za-z0-9- (),]+$";
        return s.matches(pattern);
    }
    
    public static final int UPPERCASE;
    public static final int LOWERCASE;
    public static final int NUMBERS;
    public static final int HYPHEN;
    public static final int SPACE;
    public static final int PARENTHESES;
    public static final int BRACKETS;
    public static final int BRACES;
    public static final int COMMA;
    
    public static final int EVERYTHING = Integer.MAX_VALUE;
    
    static {
        int i = 1;
        
        UPPERCASE           = i;
        LOWERCASE           = (i << 1);
        NUMBERS             = (i << 1);
        HYPHEN              = (i << 1);
        SPACE               = (i << 1);
        PARENTHESES         = (i << 1);
        BRACKETS            = (i << 1);
        BRACES              = (i << 1);
        COMMA               = (i << 1);
    }
}
