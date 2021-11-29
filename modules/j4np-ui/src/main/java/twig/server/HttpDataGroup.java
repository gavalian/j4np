/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public class HttpDataGroup extends TimerTask {
    
    private List<String>    dataList = new ArrayList<>();
    private TGDataCanvas  dataCanvas = null;    
    private Timer              timer = new Timer();
    public  long      updateInterval = 5000;
    private HttpClient           httpClient = null;
    private String                 httpHost = "localhost";
    private int                    httpPort = 8020;
    
    public HttpDataGroup(String host, int port){
        httpClient = HttpClient.newHttpClient();
    }
    
    public HttpDataGroup(){
        httpClient = HttpClient.newHttpClient();
    }
    
    public HttpDataGroup setCanvas(TGDataCanvas c){ dataCanvas = c; return this;}
    public HttpDataGroup setDataList(List<String> list){ 
        dataList.clear();
        dataList.addAll(list); 
        return this;
    }

    public void startTimer(){
        timer.schedule(this, 500, updateInterval);
    }
    
    public void stopTimer(){
        timer.cancel();
    }
    
    private String createRequestBody(){
        StringBuilder str = new StringBuilder();
        str.append("{\"request\":\"data\",\"list\":[");
        for(int i = 0 ; i < this.dataList.size(); i++){
            if(i!=0) str.append(",");
            str.append("\"").append(dataList.get(i)).append("\"");
        }
        str.append("]}");
        return str.toString();
    }
    
    @Override
    public void run() {
        String requestBody = this.createRequestBody();
        System.out.println(requestBody);
        
        String uriString = String.format("http://%s:%d", httpHost,httpPort);
        try {            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uriString))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            
            //System.out
        System.out.println("HERE : " + response.body());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(HttpDataGroup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void main(String[] args){
        List<String> histos = Arrays.asList("/server/default/h1001","/server/default/h1002",
                "/server/default/h1003","/server/default/h1004");
        
        HttpDataGroup group = new HttpDataGroup();
        group.setDataList(histos);
        group.startTimer();
    }
    
}
