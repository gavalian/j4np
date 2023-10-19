/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import j4np.utils.base.ArchiveUtils;
import java.util.ArrayList;
import java.util.List;
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
public class TDirectoryStructure implements TreeProvider {
    
    public List<String>  dirObjects = new ArrayList<>();
    private String      archiveFile = null;
    
    public TDirectoryStructure(){ }
    public TDirectoryStructure(String file){ this.open(file); }
    
    public final void open(String file){
        List<String> objList = ArchiveUtils.getList(file, "dataset");
        if(!objList.isEmpty()) archiveFile = file;
        dirObjects.addAll(objList);
    }

    @Override
    public void draw(String path, TGDataCanvas canvas) {
        System.out.println(" drawing : " + path);
        path = path.replaceFirst("/", "");
        System.out.println(" drawing : " + path);
        DataSet ds = TDirectory.loadData(this.archiveFile, path);
        if(ds!=null){
            canvas.region().draw(ds); canvas.next();
        }
    }

    @Override
    public void configure() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        System.out.println("----------");
    }

    @Override
    public TreeModel getTreeModel() {
        TreeModelMaker tm = new TreeModelMaker();
        tm.setList(dirObjects);
        DefaultMutableTreeNode root = tm.getTreeModel();
        return new DefaultTreeModel(root);
    }
    
    
}
