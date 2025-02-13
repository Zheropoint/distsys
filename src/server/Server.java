package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	
	private int port;
	private ArrayList<String> blocks = new ArrayList<String>();
	private Map<Integer, String> nodes;
	private HttpServer server;
	Map<String, String> params;

    public Server(int port) throws IOException {
    	this.port = port;
    	
    	/*System.out.println("starting on port " + (this.port));
    	System.out.println("with data " + text1 + " " + text2 + " " + text3);*/
		server = HttpServer.create(new InetSocketAddress(this.port), 5);
        server.createContext("/test", new MyHandler());
        
        // Gets the blocks in the node
        server.createContext("/getContent", httpExchange -> {
        	System.out.println("port " + port + " content: " + blocks.toString());
            String response = port + " Content: " + blocksToString(blocks);
            sendResponse(httpExchange, response);
        });
        
        // Adds a block to the node
        server.createContext("/addBlock", httpExchange -> {
        	System.out.println(httpExchange.getRequestURI().getQuery());
            String response;
            params = getParamMap(httpExchange.getRequestURI().getQuery());
            
            // Validity check
            if (!params.containsKey("block")) {
            	System.out.println(port + " No block found to add");
            	response = port + "Failed to find a block to add";
            } else {
            	// No further validity check atm
            	String newBlock = params.get("block");
            	System.out.println(port + " Adding block: [" + newBlock + "]");
            	blocks.add(newBlock);
            	System.out.println(port + " Blocks to String " + blocksToString(blocks));
            	response = newBlock;
            }
            sendResponse(httpExchange, response);
        });
        
     // Returns the nodes this node knows
        server.createContext("/whoIKnow", httpExchange -> {
        	
        	System.out.println("port " + port + " Who I know: " + nodes.toString());
            String response = nodes.toString();
            sendResponse(httpExchange, response);
        });
        
        server.setExecutor(null); // creates a default executor
        server.start();
	}

	// Returns a String out of the blocks array
    private String blocksToString(ArrayList<String> list) {
    	String blocksString = "[";
    	for (String block : list) {
    		blocksString = blocksString + " " + block;
    	}
    	blocksString = blocksString + "]";
    	return blocksString;
    }
    
    // Returns the parameters of a query
    private static Map<String, String> getParamMap(String query) {
        if (query == null || query.isEmpty()) return Collections.emptyMap();

        return Stream.of(query.split("&"))
                .filter(s -> !s.isEmpty())
                .map(kv -> kv.split("=", 2)) 
                .collect(Collectors.toMap(x -> x[0], x-> x[1]));

    }
    private Map<Integer, String> getNodes() {
    	
    	return nodes;
    }
    
    // sending a GET request to a recipient
    private void getRequest(String recipient) {
    	try {
    		recipient = "127.0.0.1:8000";
    		System.out.println("Trying to send request to `" + recipient + "`");
    		URL url = new URL(recipient);
    		HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(2000);
			con.setReadTimeout(2000);
		} catch (ProtocolException e) {
			System.out.println("getRequest error: protocol");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println("getRequest error: malformed url");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("getRequest error: IO");
			e.printStackTrace();
		}
    }
    
    
    // Response sending
    private void sendResponse(HttpExchange httpExchange, String response) {
    	
        try {
        	httpExchange.sendResponseHeaders(200, response.length());
        	OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (IOException e) {
			System.out.println("Response failed");
			e.printStackTrace();
		}
    }
    
    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	System.out.println(t.getRequestURI().toString());
            String response = "This is the response ";
            System.out.println(t.getRemoteAddress().toString());
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    // turns parameter map into String
    public static String getParamsString(Map<String, String> params) 
    	      throws UnsupportedEncodingException{
    	        StringBuilder result = new StringBuilder();

    	        for (Map.Entry<String, String> entry : params.entrySet()) {
    	          result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
    	          result.append("=");
    	          result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
    	          result.append("&");
    	        }

    	        String resultString = result.toString();
    	        return resultString.length() > 0
    	          ? resultString.substring(0, resultString.length() - 1)
    	          : resultString;
    	    }
    
    
}
