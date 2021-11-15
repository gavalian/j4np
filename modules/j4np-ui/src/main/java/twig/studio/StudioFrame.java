/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.studio;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import twig.graphics.TGDataCanvas;

/**
 *
 * @author gavalian
 */
public class StudioFrame extends JPanel {
    
    JTree           objectTree = null;
    TreeProvider  treeProvider = null;
    JTabbedPane     tabbedPane = null;
    TGDataCanvas        canvas = null;
    
    private String unicodeSquarePlus = "\u229E";
    
    public StudioFrame(){
       super();
       System.out.println(" button = " + unicodeSquarePlus + " and + and - " );
       this.initUI();
    }
    
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
    
    public void setTreeProvider(TreeProvider provider){
        this.treeProvider = provider;
        TreeModel model = this.treeProvider.getTreeModel();
        System.out.println("UPDATING TREE MODEL : " + (model==null));
        objectTree.setModel(model);
    }
    
    private void initUI(){
        
        JSplitPane pane = new JSplitPane();
        
        objectTree = new JTree();
        
        objectTree.addMouseListener(new MouseAdapter() 
        {
             @Override
             public void mouseClicked(MouseEvent me) {
                 doMouseClicked(me);
             }
             
        });
        tabbedPane = new JTabbedPane();
        JComponent panel1 = makeTextPanel("Panel #1");
        this.canvas = new TGDataCanvas();
        canvas.region()
                .getInsets()
                .left(80).right(40).top(40).bottom(80);
        tabbedPane.addTab("canvas1", null, canvas,
                "Does nothing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("canvas2", null, panel2,
                "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        JComponent panel3 = makeTextPanel("Panel #3");
        tabbedPane.addTab("canvas3", null, panel3,
                "Still does nothing");
        pane.setDividerLocation(0.5);
        objectTree.setPreferredSize(new Dimension(200,600));
        pane.setLeftComponent(objectTree);
        pane.setRightComponent(tabbedPane);
        
        this.setLayout(new BorderLayout());
        this.add(pane,BorderLayout.CENTER);
        
        URL imageURL = StudioWindow.class.getResource("bld_open.png");
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) objectTree.getCellRenderer();
        URL imgURL = StudioWindow.class.getResource("h1m.png");
        ImageIcon imageIcon = new ImageIcon(imgURL);

        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(16, 16,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);
        renderer.setLeafIcon(imageIcon);
        //this.add(this.createInspector(),BorderLayout.LINE_END);
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
                this.treeProvider.draw(objectPath, canvas );
                canvas.repaint();
            }
            /*
                int nelements = tp.getPathCount();
                StringBuilder str = new StringBuilder();
                for(int i = 1; i < nelements; i++){
                    str.append("/");
                    str.append( tp.getPathComponent(i).toString());
                }
                String objectPath = str.toString();
                if(browserDir!=null){
                    if(browserDir.getObject(objectPath)!=null){
                        System.out.println("--> " + objectPath);
                        IDataSet data = browserDir.getObject(objectPath);
                        canvasTabbed.getCanvas().drawNext(data);
                        
                        canvasTabbed.getCanvas().update();                                                
                    }
                }
            }*/
        }
    }
    
    public JPanel createInspector(){
        JPanel panel = new JPanel();
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEtchedBorder());
        for(int i = 11; i < 25; i++){
            JButton button = new JButton("Inspector Button " + i);
            panel.add(button);
        }
        return panel;
    }
}
