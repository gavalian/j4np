/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics.settings;

import j4np.graphics.GraphicsThemes;
import j4np.graphics.ResourceManager;
import j4np.graphics.settings.DataAttributes.DataAttributesPanel;
import java.awt.BorderLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author gavalian
 */
public class SettingsEditor extends JPanel {
    
    ResourceManager manager = new ResourceManager();
    
    private JTabbedPane   tabbedPane = null;
    
    public SettingsEditor(){
        
    }
    
    public void initUI(){
        this.initResources();
        this.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        this.add(tabbedPane,BorderLayout.CENTER);
    }
    
    public void initResources(){
        manager.load("settings",    "data/icons8-settings-30.png");
        manager.load("histogram",   "data/icons8-data-30.png");
        manager.load("histogram2d", "data/icons8-heat-map-30.png");
        manager.load("scatter",     "data/icons8-scatter-plot-30.png");
        manager.load("linechart",   "data/icons8-line-chart-30.png");
        manager.load("axis",        "data/icons8-coordinate-system-30.png");
        manager.load("dimensions",  "data/icons8-surface-24.png");
    }
    
    public void addEditors(){
        String[] labels = new String[]{"settings", 
            "dimensions","axis", "histogram2d",
            "scatter","linechart"};
        for(int i = 0; i < labels.length; i++){
            JPanel panel = new JPanel();
            DataAttributes attr = new DataAttributes();
            ImageIcon icon = manager.getIcon(labels[i],24,24);
            tabbedPane.addTab("",icon, new DataAttributesPanel(attr));
        }
    }
    
    public static void main(String[] args){
        GraphicsThemes.setTheme("Flat Look Ligth");
        SettingsEditor editor = new SettingsEditor();

        editor.initUI();
        editor.addEditors();
        JFrame frame = new JFrame();
        frame.add(editor);
        frame.setSize(600, 600);
        frame.setVisible(true);
        //editor.initResources();
    }
}
