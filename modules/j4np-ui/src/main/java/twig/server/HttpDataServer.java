/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import j4np.utils.json.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class HttpDataServer {
    
    private static HttpDataServer dataServer = null;
    private HttpServer httpServer = null;
    private HttpContext context = null;
    
    public HttpDataServer(int port){
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException ex) {
            Logger.getLogger(HttpDataServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static HttpDataServer getInstance(){
        return dataServer;
    }
    
    public static void create(HttpServerConfig config){
        HttpDataServer.dataServer = new HttpDataServer(config.serverPort);
        HttpDataServer.dataServer.context = HttpDataServer.dataServer.httpServer.createContext("/");
        HttpDataServer.dataServer.context.setHandler(HttpDataServer::handleRequest);
    }
    
    public static List<String>  parseJsonRequest(){
        List<String> data = new ArrayList<>();
        
        return data;
    }
    
    private static void handleRequest(HttpExchange exchange) throws IOException {
        
      String response = "Hi there!";
      DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
      LocalDateTime now = LocalDateTime.now();
      System.out.printf("[server] : recieved a request @ %s\n",dtf.format(now) );
      
      //JsonObject json = 
      StringBuilder sb = new StringBuilder();
      InputStream ios = exchange.getRequestBody();
      int i;
      while ((i = ios.read()) != -1) {
          sb.append((char) i);
      }
      System.out.println("exchange : >>> " + sb.toString());
      
      
      exchange.sendResponseHeaders(200, response.getBytes().length);//response code and length
      OutputStream os = exchange.getResponseBody();
      os.write(response.getBytes());
      os.close();
    }
    
    public void start(){
        System.out.println("[HttpDataServer] -> started twig data server....");
        this.httpServer.start();
    }
    
    public static void main(String[] args){
        HttpServerConfig config = new HttpServerConfig();
        config.serverPort = 8020;
        
        HttpDataServer.create(config);
        
        HttpDataServer.getInstance().start();
        
        System.out.println("-- headers --");
        //Headers requestHeaders = exchange.getRequestHeaders();
        //requestHeaders.entrySet().forEach(System.out::println);
        
        System.out.println("-- principle --");
        //HttpPrincipal principal = exchange.getPrincipal();
        //System.out.println(principal);
        
        System.out.println("-- HTTP method --");
        //String requestMethod = exchange.getRequestMethod();
        //System.out.println(requestMethod);
        
        System.out.println("-- query --");
        //URI requestURI = exchange.getRequestURI();
        //String query = requestURI.getQuery();
        //System.out.println(query);
    }
}