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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
public class HttpDataClient implements TreeProvider {
    
    private HttpClient           httpClient = null;
    private TreeModelMaker  clientDirectory = new TreeModelMaker();
    
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
    
    public  DataSet  getDataSet(String path){
        try {     
            String requestBody = String.format("{\"request\":\"data:%s\"}",path);
            System.out.println("[REQUEST] " + requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8020"))
                    .POST(BodyPublishers.ofString(requestBody))
                    .build();            
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            String dataJson = response.body();
            H1F h = DataSetSerializer.deserialize_H1F(dataJson);
            return h;
        }  catch (IOException | InterruptedException ex) {
            Logger.getLogger(HttpDataClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
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
            //JsonObject json = (JsonObject) Json.parse(response.body());
            JsonArray  entries = (JsonArray) Json.parse(response.body());
            for(JsonValue items : entries.values()){
                JsonObject entry = items.asObject();
                String       dir = entry.get("dir").asString();
                JsonArray   data = entry.get("data").asArray();
                for(JsonValue dataItem : data.values()){
                    String name = dataItem.asString();
                    list.add(String.format("%s/%s", dir,name));
                }
            }
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
    
    
    @Override
    public TreeModel getTreeModel() {
        List<String> dataList = getDataList();
        clientDirectory.setList(dataList);
        System.out.println("request for data list");
        
        for(String item : dataList) System.out.println(item);
        DefaultMutableTreeNode root = clientDirectory.getTreeModel();
        return new DefaultTreeModel(root);     
    }

    @Override
    public void draw(String path, TGDataCanvas c) {
        if(path.contains("h1")==true){
            System.out.println("Getting Data Set : " + path);
            DataSet d = this.getDataSet(path);
            c.region().draw(d);
            c.repaint();
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args){
        HttpServerConfig conf = new HttpServerConfig();
        conf.serverHost = "localhost";
        conf.serverPort = 8020;
        HttpDataClient client = new HttpDataClient(conf);        
        client.getTreeModel();
        
        StudioWindow.changeLook();
        
        StudioWindow frame = new StudioWindow();
        frame.getStudioFrame().setTreeProvider(client);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setVisible(true);

        //client.update(100, 3000);
    }

}
