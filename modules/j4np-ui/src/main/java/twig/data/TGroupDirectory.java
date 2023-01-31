/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import j4np.utils.base.ArchiveUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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
public class TGroupDirectory implements TreeProvider {
    
private final ConcurrentMap<String,GroupDirectory> dirList = new ConcurrentHashMap<>();
    
    public void read(String filename){
        
        List<String>  items = ArchiveUtils.getList(filename, ".*group");
        //System.out.println("size = " + items.size());
        for(String item : items){
            
            int index = item.lastIndexOf("/");
            String    dir = item.substring(0, index);
            String dsname = item.substring(index+1, item.length());
            System.out.println(" item  -> " + item);
            System.out.println(" dir   -> " + dir);
            System.out.println(" group -> " + dsname);
            if(dsname.endsWith("group")==true){
                DataGroup group = DataSetSerializer.importDataGroup(filename, dir, dsname);
                if(dir.startsWith("/")==false) dir = "/" + dir;
                this.add(dir, group);
            }
        }
    }
    
    private TGroupDirectory addToDirectory(String dir, DataGroup data){
        if(dirList.containsKey(dir)==false){
            dirList.put(dir, new GroupDirectory(dir));
        }        
        dirList.get(dir).data.add(data);
        return this;
    }
    
    protected void add(String directory, DataGroup group){
        this.addToDirectory(directory, group);
    }
    
    @Override
    public void draw(String path, TGDataCanvas canvas) {
        String  directory = path.replace("/root/", "");
        //System.out.println("\nDEBUG:");
        //System.out.println("path  = " + path);
        //System.out.println("looking for " + directory);
        /*if(directory.startsWith("/")==true){
            directory = directory.substring(1, directory.length());
            System.out.println(" now looking for " + directory);
        }*/
        DataGroup group = this.get(directory);
        if(group!=null){
            group.draw(canvas, true);
        }
    }

     public DataGroup get(String fullname){
        int   index = fullname.lastIndexOf("/");
        String  dir = fullname.substring(0, index);
        String name = fullname.substring(index+1, fullname.length());
        //System.out.printf(" dir [%s] \n name [%s]\n",dir,name);
        return get(dir,name);
    }
     
    public DataGroup get(String dir, String name){
        
        if(dirList.containsKey(dir)==false){
            System.out.println("directory not found : " + dir); 
            return null;
        }         

        for(DataGroup item : dirList.get(dir).data){
            if(item.getName().compareTo(name)==0) return item;
        }
        //System.out.printf("[dir] : %s , data not found [%s]\n" , dir,name);
        return null;
    }
    
    @Override
    public void configure() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TreeModel getTreeModel() {
         List<String> list = this.getObjects();
        TreeModelMaker tm = new TreeModelMaker();
        tm.setList(list);
        DefaultMutableTreeNode root = tm.getTreeModel();
        return new DefaultTreeModel(root);
    }
    
    public List<String>  getObjects(){
        List<String> obj = new ArrayList<>();
        for(Map.Entry<String,GroupDirectory> entry : dirList.entrySet()){
            List<String> entryObj = entry.getValue().getObjects();
            obj.addAll(entryObj);
        }
        Collections.sort(obj);
        return obj;
    }
    
    public static class GroupDirectory {
        
        public List<DataGroup>  data = new ArrayList<>();
        public String         directory = "/";
        
        public GroupDirectory(String name){
            directory = name;
        }
        
        public String list(){
            StringBuilder str = new StringBuilder();
            for(DataGroup ds : data){
                str.append(String.format("%s/%s : type = %s\n",directory, ds.getName(),ds.getClass().getName()));
            }
            return str.toString();
        }
        
        public List<String>  getObjects(){
            List<String> obj = new ArrayList<>();
            for(DataGroup item : data){
                obj.add(String.format("%s/%s", directory,item.getName()));                
            }
            return obj;
        }
        
        public void show(){
            for(DataGroup ds : this.data){
                System.out.printf("\t%12s : %s\n",ds.getName(),ds.getClass().getName());
            }
        }
               
    }
}
