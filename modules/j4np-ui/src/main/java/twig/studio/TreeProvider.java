/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package twig.studio;

import javax.swing.tree.TreeModel;
import twig.graphics.TGDataCanvas;

/**
 * Interface for implementing different data providers
 * such as file, http histogram service and tuples.
 * @author gavalian
 */
public interface TreeProvider {
    
    /**
     * Tree drawing routine. Depends on implementation what
     * will this draw.
     * @param path
     * @param canvas
     */
    public  void   draw(String path, TGDataCanvas canvas);
    /**
     * Configuring the tree provider. Usually, this will open 
     * a UI to configure the specific implementation.
     */
    public  void   configure();
    /**
     * Loading different subclasses of specific implementations.
     * 
     */
    default void   load(){ 
        System.out.printf(
                "treeProvider ---> defualt load method does nothing...\n"
        );
    };
    /**
     * Provides Tree model to be displayed in the TwigStudio.
     * For easy implementation one can use :
     * ----------------------------------------------------
     * TreeModelMaker    tm = new TreeModelMaker();
     * List<String>    data = Arrays.asList("data/set1"
     *                        ,"data/set2","group/temp/set3");
     * tm.setList(nodes);
     * DefaultMutableTreeNode root = tm.getTreeModel();
     * return new DefaultTreeModel(root);
     * -----------------------------------------------------
     * This will create hierarchical tree model from list of 
     * strings
     * @return 
     */
    public   TreeModel  getTreeModel();
    /**
     * default command parsing interface
     * @param command 
     */
    default  void execute(String command){}
}
