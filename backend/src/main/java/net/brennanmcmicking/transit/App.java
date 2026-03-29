package net.brennanmcmicking.transit;

import net.brennanmcmicking.transit.data.*;
import net.brennanmcmicking.transit.server.TransitResource;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.CrossOriginHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

import java.util.Set;


public class App {
    public static void main(String[] args) throws Exception {
        StaticData staticData = new DefaultStaticData();
        RealtimeData realtimeData = new DefaultRealtimeData(staticData);
        TransitReader reader = new DefaultTransitReader(realtimeData, staticData);

        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        CrossOriginHandler corsHandler = new CrossOriginHandler();
        corsHandler.setAllowedOriginPatterns(Set.of("http://localhost:5173", "https://*.brennanmcmicking.net"));
        corsHandler.setAllowedMethods(Set.of("GET", "POST", "HEAD", "OPTIONS"));
        corsHandler.setAllowedHeaders(Set.of("X-Requested-With", "Content-Type", "Accept", "Origin"));
        corsHandler.setHandler(context);

        server.setHandler(corsHandler);

        ResourceConfig resourceConfig = new ResourceConfig();
        TransitResource transitResource = new TransitResource(reader);
        resourceConfig.register(transitResource);

        ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));
        context.addServlet(servletHolder, "/*");

        server.start();
        server.join();
    }
}
