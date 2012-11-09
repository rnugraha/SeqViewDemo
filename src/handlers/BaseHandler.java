package handlers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public abstract class BaseHandler extends AbstractHandler {
    
    public BaseHandler() {
        super();
        this.handled = false;
    }
    
    protected final String getRoute() {
        return myRoute;
    }

    protected final void setRoute(String route) {
        this.myRoute = route;
    }

    protected void setRequestHandled(org.eclipse.jetty.server.Request req) {
        req.setHandled(true);
        this.handled = true;
    }
    
    protected boolean wasRequestHandled() {
        return handled;
    }

    protected void sendStream(InputStream input, 
                              String mime, 
                              long length,
                              Request baseRequest,
                              HttpServletRequest request, 
                              HttpServletResponse response)
                    throws IOException {

        this.send(input, mime, length, baseRequest, request, response);

    }

    protected void sendFile(File file, 
                            String mime, 
                            Request baseRequest,
                            HttpServletRequest request, 
                            HttpServletResponse response)
            throws IOException {
        
        FileInputStream input = new FileInputStream(file);
        this.send(input, mime, file.length(), baseRequest, request, response);

    }

    protected void sendString(String string, 
                              String mime, 
                              Request baseRequest,
                              HttpServletRequest request, 
                              HttpServletResponse response) throws IOException {
        
        byte[] buf = string.getBytes();
        ByteArrayInputStream input = new ByteArrayInputStream(buf);
        this.send(input, mime, buf.length, baseRequest, request, response);
        
    }

    private void send(InputStream input, 
                      String mime, 
                      long length,
                      Request baseRequest,
                      HttpServletRequest request, 
                      HttpServletResponse response) throws IOException {

        OutputStream out = null;
        final int BUFFER_SIZE = 1024 * 1024;
        byte[] buf = new byte[BUFFER_SIZE];
        long counter = 0;
        int numRead = 0;

        try {
            out = response.getOutputStream();
        } catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }

        response.setContentType(mime);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(length));
        response.setStatus(HttpServletResponse.SC_OK);
        while (true) {
            numRead = input.read(buf, 0, buf.length);
            if (numRead == -1) {
                break;
            }
            counter += numRead;
//            System.err.println(String.format("counter = %d; read = %d", counter, numRead));
            out.write(buf, 0, numRead);
        }
        out.flush();
        out.close();
//        System.err.println("Wrote " + counter + " bytes in total.");
        this.setRequestHandled(baseRequest);

    }

    // Set this true when the request has been handled.
    private boolean handled;
    
    private String myRoute;
}
