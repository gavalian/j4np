/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.ui.flatlaf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
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

/**
 *
 * @author gavalian
 */
public class StudioFrame extends JPanel {
    
    JTree        objectTree = null;
    JTabbedPane  tabbedPane = null;
    
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
    
    private void initUI(){
        
        JSplitPane pane = new JSplitPane();
        
        objectTree = new JTree();
        tabbedPane = new JTabbedPane();
        JComponent panel1 = makeTextPanel("Panel #1");
        tabbedPane.addTab("canvas1", null, panel1,
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
        URL imgURL = StudioWindow.class.getResource("h1k.png");
        ImageIcon imageIcon = new ImageIcon(imgURL);

        Image image = imageIcon.getImage(); // transform it 
        Image newimg = image.getScaledInstance(16, 16,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        imageIcon = new ImageIcon(newimg);
        renderer.setLeafIcon(imageIcon);
        //this.add(this.createInspector(),BorderLayout.LINE_END);
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
