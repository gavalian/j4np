/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import j4np.utils.json.JsonValue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.data.DataSet;
import twig.data.DataSetSerializer;
import twig.data.H1F;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class HttpDataServer {
    
    /**
     * Data Server Instance
     */
    private static HttpDataServer  dataServer = null;
    
    
    private HttpServer             httpServer = null;
    private HttpContext               context = null;    
    private TDirectory        serverDirectory = new TDirectory();
    
    private Timer timer = new Timer();
    private Random rand = new Random();
    
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
        HttpDataServer.dataServer.context.setHandler(HttpDataServer::handleDataRequest);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();

        System.out.printf("[HTTP::DATA] (%s) started server at port = %d\n",dtf.format(now),config.serverPort);
    }
    
    public static List<String>  parseJsonRequest(){
        List<String> data = new ArrayList<>();
        
        return data;
    }
    private static void sendMessage(HttpExchange exchange, String message){
        try {
            exchange.sendResponseHeaders(200, message.getBytes().length);//response code and length
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes());
            os.close();
        } catch (IOException ex) {
            Logger.getLogger(HttpDataServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static String extractMessage(HttpExchange exchange){
        StringBuilder sb = new StringBuilder();
        try {
            
            InputStream ios = exchange.getRequestBody();
            int i;
            while ((i = ios.read()) != -1) {
                sb.append((char) i);
            }

        } catch (IOException ex) {
            Logger.getLogger(HttpDataServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    
    private static void handleDataRequest(HttpExchange exchange) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();
        System.out.printf("[server] : recieved a request @ %s\n",dtf.format(now) );
        
        String request = HttpDataServer.extractMessage(exchange);
        //System.out.println("request was : " + request + "  " + DataRequestProtocol.isRequestData(request) );
        if(DataRequestProtocol.isRequestList(request)==true){
            String listResponse = HttpDataServer.getInstance().serverDirectory.jsonList();
            //System.out.println("[sending data] ----> " + listResponse);
            HttpDataServer.sendMessage(exchange, listResponse);
            return;
        }
        //System.out.println("request is before : " + request + "  " + DataRequestProtocol.isRequestData(request) );
        //if(DataRequestProtocol.isRequestData(request)==true){
        if(DataRequestProtocol.isRequestData(request)){
            //System.out.printf("---------> I'm Inside the if sattement\n");
            List<String>   dataList = DataRequestProtocol.getRequestDataList(request);
            
            String dataStringBase64 = DataSetSerializer.serializeDirectoryDeflateBase64(HttpDataServer.getInstance().serverDirectory, dataList);
            //System.out.println("base 64 = " + dataStringBase64);
            String dataStringJson   = DataRequestProtocol.createSendData(dataStringBase64);
            
            //System.out.println("[sending data] ----> " + dataStringJson);
            HttpDataServer.sendMessage(exchange, dataStringJson);
            //System.out.println("[sending data] ----> " + "blah-blah-blah");
            //HttpDataServer.sendMessage(exchange, "blah-blah-blah");
        }
        
    }
    
    /**
     * this method is depricated
     * @param exchange
     * @throws IOException 
     */
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
      
      JsonObject json = (JsonObject) Json.parse(sb.toString());
      JsonValue  jvalue = json.get("request");
      
      String what = jvalue.asString();
      
      
      if(what.compareTo("list")==0){
          String listResponse = HttpDataServer.getInstance().serverDirectory.jsonList();      
          exchange.sendResponseHeaders(200, listResponse.getBytes().length);//response code and length
          OutputStream os = exchange.getResponseBody();
          os.write(listResponse.getBytes());
          os.close(); return;
      }
      
      if(what.startsWith("data")==true){
          System.out.println("suppose to give him some data.");
          JsonArray dataList = json.get("list").asArray();
          //List<String> dataPath = new ArrayList<>();
          StringBuilder str = new StringBuilder();
          str.append("[");
          int counter = 0;
          for(JsonValue item : dataList.values()){
              System.out.printf("\t--> %s\n",item.asString());
              DataSet  ds = HttpDataServer.getInstance().serverDirectory.get(item.asString());
              if(counter!=0) str.append(",");
              counter++;
              str.append(DataSetSerializer.toJson(ds));
          }
          str.append("]");
          String dataJson = str.toString();
          exchange.sendResponseHeaders(200, dataJson.getBytes().length);//response code and length
          OutputStream os = exchange.getResponseBody();
          os.write(dataJson.getBytes());
          os.close(); return;
      }
      
      if(what.startsWith("data:")==true){
          
          String data = what.substring(5, what.length());
          H1F h = (H1F) HttpDataServer.getInstance().serverDirectory.get(data);
          String dataJson = DataSetSerializer.toJson(h);
          System.out.println("[server::debug] what = " + what);
          System.out.println("[server::debug] data = " + data);
          System.out.println("[server::debug] json = " + dataJson);
          exchange.sendResponseHeaders(200, dataJson.getBytes().length);//response code and length
          OutputStream os = exchange.getResponseBody();
          os.write(dataJson.getBytes());
          os.close(); return;
      }
    }
    
    public void start(){
        System.out.println("[HttpDataServer] -> started twig data server....");
        this.httpServer.start();
    }
    
    public void initDefault(){
        for(int i = 0; i < 5; i++){
            int id = 1001 + i;            
            H1F h = new H1F("h"+id,"",120,-2.,2.0);
            h.attr().setFillColor(i+2);;
            h.attr().setTitleX(String.format("gaussian (#mu = %.2f)",0.1*i));
            h.attr().setTitleY("counts");
            this.serverDirectory.add("/server/default", h);
        }
        
        timer.schedule(new TimerTask(){
            private long counter = 0;
            @Override
            public void run() {
                for(int i = 0 ; i< 5; i++){
                    String name = String.format("h%d", 1001+i);
                    H1F h = (H1F) serverDirectory.get("/server/default", name);
                    
                    for(int k = 0; k < 250; k++){
                        double value = rand.nextGaussian()+0.1*i;                        
                        h.fill(value);
                    }
                    h.attr().setTitleX(String.format("gaussian (#mu = %.2f, stats = %d)",
                            0.1*i,h.getEntries()));
                }
                counter++;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
                LocalDateTime now = LocalDateTime.now();
                //System.out.printf("[HTTP:data:server] (%s) execution counter = %8d\n",dtf.format(now),counter);
            }
        }, 100,5000);
    }
    
    public static void main(String[] args){
        HttpServerConfig config = new HttpServerConfig();
        config.serverPort = 8525;
        
        HttpDataServer.create(config);
        HttpDataServer.getInstance().initDefault();
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
