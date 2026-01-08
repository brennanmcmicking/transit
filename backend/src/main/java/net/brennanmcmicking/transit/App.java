package net.brennanmcmicking.transit;

import net.brennanmcmicking.transit.data.DefaultRealtimeData;
import net.brennanmcmicking.transit.data.DefaultStopData;
import net.brennanmcmicking.transit.data.RealtimeData;
import net.brennanmcmicking.transit.data.StopData;
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
        RealtimeData realtimeData = new DefaultRealtimeData();
        StopData stopData = new DefaultStopData();
        TransitReader reader = new DefaultTransitReader(realtimeData, stopData);
//        System.out.println(data
//                .getBusses()
//                .stream()
//                .map(Bus::toString)
//                .collect(Collectors.joining("\n"))
//        );

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
