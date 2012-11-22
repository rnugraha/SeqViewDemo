package security;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class FileAccessControl {

    public FileAccessControl(String rootPath) {
        this.rootDir = new File(rootPath);
    }
    
    public void setDirectory(File dir) {
        if (dir.isDirectory() || dir.canRead()) {
            rootDir = dir;
        }
    }

    public int pathAccessStatus(String path) {
        URI u;
        try {
            u = new URI(path);
            path = u.normalize().toString();
            path = rootDir.getAbsolutePath().concat(path);
            File f = new File(path);
            
            if (f.exists() == false) {
                return FILE_NOT_FOUND;
            }
            if (f.isFile() == false || f.canRead() == false) {
                return ACCESS_DENIED;
            }
            return ACCESS_ALLOWED;
        } catch (URISyntaxException e) {
            return FILE_NOT_FOUND;
        }
    }
    
    private File rootDir;
    
    public static final int ACCESS_ALLOWED = 0;
    public static final int ACCESS_DENIED = 1;
    public static final int FILE_NOT_FOUND = 2;
}
