/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class HttpDataClient {
        
    private HttpClient httpClient = null;
        
    private Map<String,String> valuesDirList = new HashMap<String,String>(){{
        put("request","list");
    }};
    
    private Map<String,String> valuesGetData = new HashMap<String,String>() {{
       put("request","data"); put("objects","h");
    }};
    
    public HttpDataClient(HttpServerConfig conf){
        this.create(conf);
        
    }
    
    private void create(HttpServerConfig conf){
        httpClient = HttpClient.newHttpClient();
    }
    
    public  List<String> getDataList()  {
        List<String> list = new ArrayList<>();
        
        try {            
            String requestBody = "{\"request\":\"list\"}";
            System.out.println("[REQUEST] " + requestBody);            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8020"))
                    .POST(BodyPublishers.ofString(requestBody))
                    .build();            
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());            
        } catch (IOException ex) {
            Logger.getLogger(HttpDataClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(HttpDataClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public  void update(int counter, int delay) {
        int   iter = 0;
        while(iter<counter){

            this.getDataList();

            //httpClient.sendAsync(request, BodyHandlers.ofString())
            //        .thenApply(HttpResponse::body)
            //        .thenAccept(System.out::println)
            //        .join();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Logger.getLogger(HttpDataClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            iter++;
        }
    }
    
    public static void main(String[] args){
        HttpServerConfig conf = new HttpServerConfig();
        conf.serverHost = "localhost";
        conf.serverPort = 8020;
        HttpDataClient client = new HttpDataClient(conf);        
        client.update(100, 3000);
    }
}
