/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import j4np.utils.FileUtils;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import j4np.utils.json.JsonValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import twig.graphics.TGDataCanvas;
import twig.studio.StudioWindow;
import twig.studio.TreeProvider;

/**
 *
 * @author gavalian
 */
public class HttpDataConfig implements TreeProvider {
    
    private Map<String,HttpDataGroup>  dataGroups = new HashMap<>();
    private TreeModelMaker             modelMaker = new TreeModelMaker();
    private HttpDataGroup             activeGroup = null;
    
    public List<String>  getGroupList(){
        List<String> result = new ArrayList<>();
       for(String item :dataGroups.keySet()){
           result.add(item);
       }
       return result;
    }
    
    @Override
    public TreeModel getTreeModel() {
        System.out.println("****************");
        System.out.println(Arrays.toString(this.getGroupList().toArray()));
        System.out.println("****************");
        
        modelMaker.setList(this.getGroupList());
        DefaultMutableTreeNode root = modelMaker.getTreeModel();
        return new DefaultTreeModel(root); 
    }

    @Override
    public void draw(String path, TGDataCanvas c) {

        /*String fullpath = path;
        if(path.startsWith("/")==false){
            fullpath = "/" + path;
        }*/
        
        System.out.println("examine path : " + path);
        if(dataGroups.containsKey(path)==true){
            System.out.println("activating group : " + path);
            if(activeGroup!=null) activeGroup.stopTimer();
            activeGroup = dataGroups.get(path);
            activeGroup.setCanvas(c);
            activeGroup.startTimer();
        }
    }
    
    public void load(String filename){
        String jsonFile = FileUtils.readFileAsString(filename);
        JsonArray jsonList = (JsonArray) Json.parse(jsonFile);
        for(JsonValue value : jsonList.values()){
            JsonObject obj = (JsonObject) value;
            String    name = obj.get("group").asString();
            int       cols = obj.getInt("cols", 1);
            int       rows = obj.getInt("rows", 1);
            
            JsonArray list = (JsonArray) obj.get("data");
            System.out.println("name => " + name);
            List<String>  dataItems = new ArrayList<>();            
            for(JsonValue item : list.values()){
                System.out.println("\t : " + item.asString());
                dataItems.add(item.asString());
            }
            HttpDataGroup group = new HttpDataGroup();
            group.setName(name);
            group.setLayout(cols, rows);
            group.setDataList(dataItems);
            this.dataGroups.put(name, group);
        }
    }
    
    public static void main(String[] args){
        
        
        HttpDataConfig client = new HttpDataConfig();
        client.load("default.json");
        
        HttpServerConfig conf = new HttpServerConfig();
        conf.serverHost = "localhost";
        conf.serverPort = 8020;        
        
        StudioWindow.changeLook();
        
        StudioWindow frame = new StudioWindow();
        frame.getStudioFrame().setTreeProvider(client);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setVisible(true);
    }
    
}
