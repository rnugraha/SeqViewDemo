package network;
import handlers.GeneNameHandler;
import handlers.NcbiHandler;
import handlers.RoutingHandler;
import handlers.StaticFileHandler;
import handlers.VariantHandler;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import app.AppProperties;
import database.DatabaseConfig;
import database.DatabaseConnectionException;
import database.GeneNameDatabaseInterface;
import database.GeneNameDatabaseMongoDB;
import database.VariantDatabaseInterface;
import database.VariantDatabaseMongoDB;

public class ServerApplication {
    public static void main(String[] args) {
        
        AppProperties config = new AppProperties();
        
        // Parse command-line arguments.
        for (String arg : args) {
            if (arg.startsWith("-")) {
                arg = arg.substring(1);
                int index = arg.indexOf('=');
                if (index != -1) {
                    String key = arg.substring(0, index);
                    String value = arg.substring(index + 1);
                    config.set(key, value);
                }
            }
        }
        
        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(new Integer(config.get(AppProperties.PORT)));
        server.addConnector(connector);
        
        RoutingHandler router = new RoutingHandler();
        StaticFileHandler staticHandler = new StaticFileHandler();
        VariantHandler variantHandler = new VariantHandler();
        GeneNameHandler geneHandler = new GeneNameHandler();
        NcbiHandler ncbiHandler = new NcbiHandler();
        
        try {
            
            VariantDatabaseInterface variantDb = 
                new VariantDatabaseMongoDB(
                        new DatabaseConfig()
                            .set("host", "localhost")
                            .set("database", "variodb")
                            .set("collection", "variants"));
            variantHandler.setDatabase(variantDb);

            GeneNameDatabaseInterface geneDb = 
                new GeneNameDatabaseMongoDB(
                        new DatabaseConfig()
                            .set("host", "localhost")
                            .set("database", "variodb")
                            .set("collection", "hgnc_genes"));
            geneHandler.setDatabase(geneDb);
            variantHandler.setGeneNameDatabase(geneDb);

        } catch (DatabaseConnectionException e) {
            System.err.println("Could not initialize the Mongo database.");
            e.printStackTrace();
            System.exit(1);
        }

        router.bind(Routes.GENES,               geneHandler);
        router.bind(Routes.NCBISV,              ncbiHandler);
        router.bind(Routes.NCBI_IMAGES,         ncbiHandler);
        router.bind(Routes.NCBI_EXTJS,          ncbiHandler);
        router.bind(Routes.SVIEW_DATA,          variantHandler);
        router.bind(Routes.VARIANTS,            variantHandler);
        
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { router, staticHandler });
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            System.err.println("Could not start the webserver.");
            e.printStackTrace();
        }
    }
    
}