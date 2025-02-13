package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class main {

   /* public static void main(String[] args) throws Exception {
        Server server1 = new Server(8000);
    }*/
	static int count = 1;
	static int initialPort = 8000;
	static ArrayList<Server> servers = new ArrayList<Server>();
	
	public static void main(String[] args) throws Exception {
		
		
		/* (int i = 0; i <= count; i++) {
			System.out.println("starting on port " + (initialPort + i));
			HttpServer server = HttpServer.create(new InetSocketAddress(i+initialPort), 0);
	        server.createContext("/test", new MyHandler());
	        server.setExecutor(null); // creates a default executor
	        server.start();
		}*/
		for (int i = 0; i <= count; i++) {
			int activePort = i + initialPort;
			//System.out.println("starting on port " + activePort);
			servers.add(new Server(activePort));
			
		}
		
    }
	
	/*static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	requestPrinter(t);
        	System.out.println(t.getRequestURI().toString());
            String response = "This is the response";
            System.out.println(t.getRemoteAddress().toString());
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }*/
	
	private static void requestPrinter(HttpExchange t) {
		System.out.println("printing request...");
		System.out.println(t.getHttpContext().toString());
	}

}