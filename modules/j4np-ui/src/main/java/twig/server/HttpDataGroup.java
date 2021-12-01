/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.server;

import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
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
import twig.data.DataSet;
import twig.data.DataSetSerializer;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public class HttpDataGroup {
    
    private List<String>    dataList = new ArrayList<>();
    private List<DataSet>   dataSets = new ArrayList<>();
    
    private TGDataCanvas  dataCanvas = null;    
    private Timer              timer = new Timer();
    public  long      updateInterval = 5000;
    private HttpClient           httpClient = null;
    private String                 httpHost = "localhost";
    private int                    httpPort = 8020;
    private String                groupName = "/default/generic";
    private int                  layoutCols = 1;
    private int                  layoutRows = 1;
    
    public HttpDataGroup(String host, int port){
        httpClient = HttpClient.newHttpClient();
    }
    
    public HttpDataGroup(){
        httpClient = HttpClient.newHttpClient();
    }
    
    public HttpDataGroup setName(String name){
        this.groupName = name; return this;
    }
    
    public void   setLayout(int cols, int rows){
        layoutCols = cols; layoutRows = rows;
    }
    
    public String getName(){ return this.groupName;}
    
    public HttpDataGroup setCanvas(TGDataCanvas c){ dataCanvas = c; return this;}
    public HttpDataGroup setDataList(List<String> list){ 
        dataList.clear();
        dataList.addAll(list); 
        return this;
    }
    
    public void startTimer(){
        try {
            TimerTask task = new TimerTask()
            { 
                @Override
                public void run() {
                    String requestBody = createRequestBody();
                    System.out.println(requestBody);
                    
                    String uriString = String.format("http://%s:%d", httpHost,httpPort);
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(uriString))
                                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                .build();
                        HttpResponse<String> response = httpClient.send(request,
                                HttpResponse.BodyHandlers.ofString());
                        
                        System.out.println("HERE : " + response.body());
                        JsonArray  array = (JsonArray) Json.parse(response.body());
                        List<DataSet> dslist = DataSetSerializer.deserialize(array);
                        
                        //System.out
                        if(dataCanvas!=null){
                            dataCanvas.divide(layoutCols, layoutRows);
                            int pad = 0;
                            for(DataSet d : dslist) {
                                dataCanvas.region(pad).draw(d); pad++;
                            }
                            dataCanvas.repaint();
                        }
                    } catch (IOException | InterruptedException ex) {
                        Logger.getLogger(HttpDataGroup.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            timer = new Timer();
            timer.schedule(task, 500, updateInterval);
        } catch (Exception e){
            System.out.println(" exception in group : " + groupName);
            e.printStackTrace();
        }
    }
    
    public void stopTimer(){
        try {
            timer.cancel();
            timer = null;
            //timer = new Timer();
        } catch (Exception e){
            System.out.printf("\noh no.... this timer was already stopped.\n");
        }
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
          
    public static void main(String[] args){
        List<String> histos = Arrays.asList("/server/default/h1001","/server/default/h1002",
                "/server/default/h1003","/server/default/h1004");
        
        TGCanvas c = new TGCanvas();
        HttpDataGroup group = new HttpDataGroup();
        group.setDataList(histos);
        group.setCanvas(c.view());
        group.startTimer();
    }
    
}
