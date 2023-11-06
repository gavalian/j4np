/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import j4np.graphics.UITools;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author gavalian
 */
public class StudioTreeView extends StudioComponent implements ActionListener {
    private TreeProvider    provider = null;
    protected JTree       objectTree = null;
    protected JPanel      controls = null;
    private static final int MIN_WIDTH = 560;
private static final int MIN_HEIGHT = 420;
    JScrollPane scrollPane = null;
       // panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
       // panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    public StudioTreeView(){}
    public StudioTreeView(TreeProvider tp){ 
        init(tp);
        
        this.setMinimumSize(new Dimension(800,600));                
    }
    
    protected void initSizeContrain(){
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                boolean resizeIt = false;
                int width = getWidth();
                int height = getHeight();
                if (width < MIN_WIDTH) {
                    width = MIN_WIDTH;
                    resizeIt = true;
                }
                if (height < MIN_HEIGHT) {
                    height = MIN_HEIGHT;
                    resizeIt = true;
                }
                if (resizeIt) {
                    setSize(width, height);
                }
            }
        });
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
        
        //scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.setLayout(new BorderLayout());
        
        this.controls = new JPanel();
        this.controls.setLayout(new FlowLayout());
        
        JButton btnExpand = new JButton("Expand");
        btnExpand.addActionListener(this);
        
        JButton btnCollapse = new JButton("Collapse");
        btnCollapse.addActionListener(this);
        
        JButton btnUndo = new JButton("Undo");
        btnUndo.addActionListener(this);
        
        this.controls.add(btnExpand);
        this.controls.add(btnCollapse);
        this.controls.add(btnUndo);
        
        JPanel container = UITools.withPanelEtched(scrollPane, 5);
        JPanel containerBtm = UITools.withPanelEtched(controls, 5);
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Tree", null, container,
                  "Tree View");
        //scrollPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        
        //this.add(scrollPane,BorderLayout.CENTER);
        this.add(tabbedPane,BorderLayout.CENTER);
        this.add(containerBtm,BorderLayout.PAGE_END);
        
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
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("will implement later (this is the way)");
    }
}
