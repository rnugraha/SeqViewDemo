package security;

public class SqlParameter {
    
    public static boolean sqlInjectionResistant(String s) {
        return sqlInjectionResistant(s, EVERYTHING);    
    }
    
    public static boolean sqlInjectionResistant(String s, int mode) {
        StringBuilder classes = new StringBuilder();
        
        if ((mode & UPPERCASE) != 0) {
            classes.append("A-Z");
        }
        if ((mode & LOWERCASE) != 0) {
            classes.append("a-z");
        }
        if ((mode & NUMBERS) != 0) {
            classes.append("0-9");
        }
        if ((mode & HYPHEN) != 0) {
            classes.append("\\-");
        }
        if ((mode & SPACE) != 0) {
            classes.append(" ");
        }
        if ((mode & PARENTHESES) != 0) {
            classes.append("()");
        }
        if ((mode & BRACKETS) != 0) {
            classes.append("\\[\\]");
        }
        if ((mode & BRACES) != 0) {
            classes.append("{}");
        }
        if ((mode & COMMA) != 0) {
            classes.append(",");
        }
        
        String pattern = String.format("^[%s]+$", classes.toString());
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
    
    public static final int ALPHABET;
    public static final int ALPHANUMERIC;
    
    public static final int EVERYTHING = Integer.MAX_VALUE;
    
    static {
        int i = 1;
        
        UPPERCASE           = i;        i <<= 1;
        LOWERCASE           = i;        i <<= 1;
        NUMBERS             = i;        i <<= 1;
        HYPHEN              = i;        i <<= 1;
        SPACE               = i;        i <<= 1;
        PARENTHESES         = i;        i <<= 1;
        BRACKETS            = i;        i <<= 1;
        BRACES              = i;        i <<= 1;
        COMMA               = i;        i <<= 1;
        
        ALPHABET            = UPPERCASE | LOWERCASE;
        ALPHANUMERIC        = UPPERCASE | LOWERCASE | NUMBERS;
    }
}
