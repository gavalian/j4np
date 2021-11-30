/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import twig.graphics.TGDataCanvas;
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
        modelMaker.setList(this.getGroupList());
        DefaultMutableTreeNode root = modelMaker.getTreeModel();
        return new DefaultTreeModel(root); 
    }

    @Override
    public void draw(String path, TGDataCanvas c) {
        System.out.println("examine path : " + path);
        if(dataGroups.containsKey(path)==true){
            if(activeGroup!=null) activeGroup.stopTimer();
            activeGroup = dataGroups.get(path);
            activeGroup.setCanvas(c);
            activeGroup.startTimer();
        }
    }
    
    public void load(String filename){
        
    }
    
    public static void main(String[] args){
        
    }
    
}
