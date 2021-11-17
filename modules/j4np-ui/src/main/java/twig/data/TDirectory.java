/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import twig.graphics.TGDataCanvas;
import twig.server.TreeModelMaker;
import twig.studio.TreeProvider;

/**
 *
 * @author gavalian
 */
public class TDirectory implements TreeProvider {
    
    private final Map<String,Directory> dirList = new HashMap<>();
    
    public TDirectory(){
        
    }
    
    public TDirectory add(String dir, DataSet data){
        if(dirList.containsKey(dir)==false){
            dirList.put(dir, new Directory(dir));
        }        
        dirList.get(dir).data.add(data);
        return this;
    }
    
    public DataSet get(String dir, String name){

        if(dirList.containsKey(dir)==false){
            System.out.println("directory not found : " + dir); 
            return null;
        }         

        for(DataSet item : dirList.get(dir).data){
            if(item.getName().compareTo(name)==0) return item;
        }
        System.out.printf("[dir] : %s , data not found [%s]\n" , dir,name);
        return null;
    }
    
    public DataSet get(String fullname){
        int  index = fullname.lastIndexOf("/");
        String  dir = fullname.substring(0, index);
        String name = fullname.substring(index+1, fullname.length());
        //System.out.printf(" dir [%s] \n name [%s]\n",dir,name);
        return get(dir,name);
    }
    
    public String list(){
        StringBuilder str = new StringBuilder();
        for(Map.Entry<String,Directory> entry : this.dirList.entrySet() ){
            str.append(entry.getValue().list());
        }
        return str.toString();
    }
        
    public List<String>  getObjects(){
        List<String> obj = new ArrayList<>();
        for(Map.Entry<String,Directory> entry : dirList.entrySet()){
            List<String> entryObj = entry.getValue().getObjects();
            obj.addAll(entryObj);
        }
        Collections.sort(obj);
        return obj;
    }
    
    public void treeModel(){
        List<String> objects = this.getObjects();
        Set<String>     root = this.getChildren(objects, "", -1);
        for(String item : root){
            for(int k = 0; k < 4; k++){
                Set<String> c = this.getChildren(objects, item, k);
                System.out.println(" NODE : " + item);
                System.out.println("\t" + Arrays.toString(c.toArray()));
            }
        }
    }
    
    private Set<String> getChildren(List<String> list, String node, int index){
        Set<String> children = new LinkedHashSet<>();
        for(int i = 0; i < list.size(); i++){
            String[] tokens = list.get(i).split("/");
            if(index>=0){
                if(tokens.length>index+1){
                    if(tokens[index].compareTo(node)==0){
                        children.add(tokens[index+1]);
                    }
                }
            } else {
                if(tokens.length>0) children.add(tokens[0]);
            }
        }
        return children;
    }
    
    public String jsonList(){
        StringBuilder str = new StringBuilder();
        int counter = 0;
        str.append("[");
        for(Map.Entry<String,Directory> entry : dirList.entrySet()){
            if(counter!=0) str.append(",");
            str.append(entry.getValue().jsonList());//.append("\n");
            counter++;
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public TreeModel getTreeModel() {
        List<String> list = this.getObjects();
        TreeModelMaker tm = new TreeModelMaker();
        tm.setList(list);
        DefaultMutableTreeNode root = tm.getTreeModel();
        return new DefaultTreeModel(root);
    }

    @Override
    public void draw(String path, TGDataCanvas c) {
        String  directory = path.replace("/root/", "/");
        DataSet ds = this.get(directory);
        if(ds!=null){
            c.region().draw(ds);
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static class Directory {
        
        public List<DataSet>  data = new ArrayList<>();
        public String         directory = "/";
        
        public Directory(String name){
            directory = name;
        }
        
        public String list(){
            StringBuilder str = new StringBuilder();
            for(DataSet ds : data){
                str.append(String.format("%s/%s : type = %s\n",directory, ds.getName(),ds.getClass().getName()));
            }
            return str.toString();
        }
        
        public List<String>  getObjects(){
            List<String> obj = new ArrayList<>();
            for(DataSet item : data){
                obj.add(String.format("%s/%s", directory,item.getName()));                
            }
            return obj;
        }
        
        public String jsonList(){
            StringBuilder str = new StringBuilder();
            str.append(String.format("{\"dir\":\"%s\",\"data\":[",directory));
            int counter = 0;
            for(DataSet ds : data){
                if(counter!=0) str.append(",");
                str.append(String.format("\"%s\"", ds.getName())); 
                counter++;
            }
            str.append("]}");
            return str.toString();
        }
    }
    
    
    public static void main(String[] args){
        
        H1F h1 = new H1F("h1",100,0.0,1.0);
        H1F h2 = new H1F("h2",100,0.0,1.0);
        H1F h3 = new H1F("h3",100,0.0,1.0);
        
        TDirectory dir = new TDirectory();
        
        dir.add("/mc", h1).add("/mc", h2).add("/data", h3);
        
        System.out.println(dir.list());
        
        H1F h = (H1F) dir.get("/mc/h1");
        
        //System.out.println(h.toString());
        
        System.out.println(dir.jsonList());
        System.out.println(Arrays.toString(dir.getObjects().toArray()));
                
        dir.treeModel();
    }
}