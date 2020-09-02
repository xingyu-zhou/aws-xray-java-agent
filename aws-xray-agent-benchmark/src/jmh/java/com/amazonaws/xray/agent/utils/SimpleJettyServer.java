package com.amazonaws.xray.agent.utils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Enumeration;


/**
 * Utility class used in benchmarking tests to simulate a web server and negate unpredictable network interaction's
 * impact on benchmarking results.
 *
 * Adapted from the OpenTelemetry benchmarking strategy
 * https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/master/benchmark/src/jmh/java/io/opentelemetry/benchmark/classes/HttpClass.java
 */
public class SimpleJettyServer {
    private SimpleJettyServer() {
    }

    public static Server create(int port, String path) {
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
        System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
        Server jettyServer = new Server(new InetSocketAddress("localhost", port));
        ServletContextHandler servletContext = new ServletContextHandler();

        try {
            servletContext.addServlet(MyServlet.class, path);
            jettyServer.setHandler(servletContext);
            jettyServer.start();

            // Make sure the Server has started
            while (!AbstractLifeCycle.STARTED.equals(jettyServer.getState())) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jettyServer;
    }

    public static class MyServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            try {
                Thread.sleep(5);
            } catch (Exception e) {
            }

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{ \"status\": \"ok\"}");
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
            try {
                Thread.sleep(5);
            } catch (Exception e) {
            }

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println("{ \"status\": \"ok\"}");
        }
    }
}
