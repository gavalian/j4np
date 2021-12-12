/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import j4np.utils.json.JsonValue;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import twig.data.DataSet;
import twig.data.DataSetSerializer;
import twig.data.H1F;
import twig.data.TDirectory;
import twig.graphics.TGDataCanvas;
import twig.studio.StudioWindow;
import twig.studio.TreeProvider;

/**
 *
 * @author gavalian
 */
public class HttpDataClient {
    
    private HttpClient              httpClient = null;
    private HttpServerConfig        httpConfig = null;
    private String               uriConnection = "http://localhost:8025";

    private long                   timeRequest  = 0L;
    private long                  requestCount  = 0L;
    private long                  dataReceived  = 0L;
    private long      dataReceivedUncompressed  = 0L;

    public HttpDataClient(HttpServerConfig conf){
        httpClient = HttpClient.newHttpClient();
        httpConfig = conf;
        this.setConfig(conf);
    }
    
    public HttpDataClient(){
        httpClient = HttpClient.newHttpClient();
        httpConfig = new HttpServerConfig();
        httpConfig.serverHost = "localhost";
        httpConfig.serverPort = 8525;
        this.setConfig(httpConfig);
    }
    
    public final void setConfig(HttpServerConfig conf){
        httpConfig = new HttpServerConfig();
        httpConfig.serverHost = conf.serverHost;
        httpConfig.serverPort = conf.serverPort;
        uriConnection = String.format("http://%s:%d", conf.serverHost,conf.serverPort);
    }
    
    public  List<DataSet>  getDataSet(List<String> data){
        try {
            String query = DataRequestProtocol.createRequestData(data);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uriConnection))
                    .POST(BodyPublishers.ofString(query))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            String dataJson = response.body();
            
            if(DataRequestProtocol.isSendData(dataJson)==true){
                long then = System.nanoTime();                
                String   jsonBase64 = DataRequestProtocol.getSendDataPayload(dataJson);
                String jsonUnBase64 = DataSetSerializer.deserializeDeflatedBase64(jsonBase64);
                JsonArray jsonArray = (JsonArray) Json.parse(jsonUnBase64);
                List<DataSet> dataSetList = DataSetSerializer.deserializeJsonArray(jsonArray);
                long now = System.nanoTime();
                
                timeRequest += (now-then);
                dataReceived += jsonBase64.length();
                dataReceivedUncompressed += jsonUnBase64.length();
                requestCount++;
                return dataSetList;
            }                        
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(HttpDataClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }       
    
    public  List<String> getDataList()  {
        try {
            String query = DataRequestProtocol.createRequestList();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uriConnection))
                    .POST(BodyPublishers.ofString(query))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            String dataJson = response.body();
            System.out.println("receivedData : " + dataJson);
            //if(DataRequestProtocol.isSendList(dataJson)==true){                
                List<String> dataSetList = DataRequestProtocol.getSendListPayload(dataJson);
                return dataSetList;
            //} 
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(HttpDataClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<>();
    }            
    
    public String getStats(){
        double   timeRate = (((double)timeRequest)/1000000.0)/requestCount;
        double   dataRate = ((double) dataReceived)/requestCount;
        double dataRateUn = ((double) dataReceivedUncompressed)/requestCount;
        NumberFormat nf = NumberFormat.getInstance(Locale.US);
        return String.format("data rate %s byte/sec, uncompressed %s byte/sec, time %.2f msec",
                nf.format((int)dataRate),nf.format((int)dataRateUn),timeRate);
    }
     
    public static void main(String[] args){
        
        HttpServerConfig   conf = new HttpServerConfig();
        HttpDataClient   client = new HttpDataClient();
        List<String> dataList = client.getDataList();
        
        for(String data : dataList){
            System.out.println("\t item : " + data);
        }
        
        for(int i = 0; i < 20; i++){
                        
            client.getDataSet(dataList);
            
            System.out.println(client.getStats());
            
            try {
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(HttpDataClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
