/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author gavalian
 */
public class StudioTreeView extends StudioComponent {
    private TreeProvider    provider = null;
    protected JTree       objectTree = null;
    JScrollPane scrollPane = null;
       // panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
       // panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    public StudioTreeView(){}
    public StudioTreeView(TreeProvider tp){ 
        init(tp);
        
        this.setMinimumSize(new Dimension(800,600));
    }
    
    public TreeProvider getTreeProvider(){ return this.provider;}
    
    
    
    private void init(TreeProvider tp){
        this.provider = tp;
        TreeModel model = this.provider.getTreeModel();
        objectTree = new JTree(model);
        scrollPane = new JScrollPane(objectTree);
        
        objectTree.setBackground(new Color(250,250,255));
        objectTree.addMouseListener(new MouseAdapter() 
        {
             @Override
             public void mouseClicked(MouseEvent me) {
                 doMouseClicked(me);
             }
             
        });
        
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.setLayout(new BorderLayout());
        this.add(scrollPane,BorderLayout.CENTER);
        
        this.initTree();
        
    }        
    
    private ImageIcon getImageIcon(String path, int xsize, int ysize){
        URL imgURL = StudioWindow.class.getResource(path);
        ImageIcon imageIcon = new ImageIcon(imgURL);

        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(xsize, ysize,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);
        return imageIcon;
    }
    
    private void initTree(){
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) objectTree.getCellRenderer();
        ImageIcon iconFolderClosed = this.getImageIcon("icons/icons8-folder-64.png", 16, 16);
        ImageIcon   iconFolderOpen = this.getImageIcon("icons/icons8-opened-folder-64.png", 16, 16);
        ImageIcon         iconLeaf = this.getImageIcon("icons/icons8-histogram-64.png", 16, 16);
        
        renderer.setOpenIcon(iconFolderOpen);
        renderer.setClosedIcon(iconFolderClosed);
        renderer.setLeafIcon(iconLeaf);
    }
    public void doMouseClicked(MouseEvent me){
        if(me.getClickCount()==2){
            TreePath tp = objectTree.getPathForLocation(me.getX(), me.getY());
            if (tp != null){
                System.out.println(" path -> : " + tp.toString());
                StringBuilder str = new StringBuilder();
                int nelements = tp.getPathCount();
                for(int i = 1; i < nelements; i++){
                    str.append("/");
                    str.append( tp.getPathComponent(i).toString());
                }
                String objectPath = str.toString();                
                ActionEvent event = new ActionEvent(this,12,objectPath);
                this.fireEvent(event);
                /*ActionListener[] list = getListeners(ActionListener.class);
                
                System.out.println("size = " + list.length);
                for(int k = 0; k < list.length; k++){
                    System.out.println("object = " + list[k].getClass().getName());
                }*/
            }
        }
    }
}
