/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.editors;

import j4np.graphics.ResourceManager;
import j4np.graphics.settings.DataAttributes;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import twig.data.DataSet;
import twig.data.GraphErrors;
import twig.data.H2F;
import twig.graphics.TDataNode2D;
import twig.graphics.TGDataCanvas;
import twig.graphics.TGRegion;

/**
 *
 * @author gavalian
 */
public class CanvasEditorPanel extends JPanel {
    ResourceManager manager = new ResourceManager();    
    private JTabbedPane   tabbedPane = null;
    private int iconSizeX = 24;
    private int iconSizeY = 24;
    private JComponent parent = null;
    protected TGDataCanvas canvas = null;
    protected TGRegion     region = null;
    
    public CanvasEditorPanel( TGDataCanvas dc, TGRegion dr){
        initResources();
        initUI();
        canvas = dc; region = dr;
        //parent = jc;
        this.addCanvasTab(dc);
        this.addRegionTab(dr);
    }
    
    private void initUI(){
        this.initResources();
        this.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        this.add(tabbedPane,BorderLayout.CENTER);
    }
    
    private void initResources(){
        manager.load("settings",    "data/icons8-settings-30.png");
        manager.load("histogram",   "data/icons8-data-30.png");
        manager.load("histogram2d", "data/icons8-heat-map-30.png");
        manager.load("scatter",     "data/icons8-scatter-plot-30.png");
        manager.load("linechart",   "data/icons8-line-chart-30.png");
        manager.load("axis",        "data/icons8-coordinate-system-30.png");
        manager.load("dimensions",  "data/icons8-surface-24.png");
    }
    
    public final void addCanvasTab(TGDataCanvas dc){
        ImageIcon icon = manager.getIcon("dimensions",iconSizeX,iconSizeY);
        DataCanvasEditorPanel dePanel = new DataCanvasEditorPanel(dc);
        tabbedPane.addTab("", icon, dePanel);
    }
    
    public final void addRegionTab(TGRegion dr){
        List<TDataNode2D> dn = dr.getAxisFrame().getDataNodes();
        for(int i = 0; i < dn.size(); i++){
            ImageIcon icon = manager.getIcon("histogram2d",iconSizeX,iconSizeY);
            DataSet ds = dn.get(i).getDataSet();
            if(ds instanceof GraphErrors) icon = manager.getIcon("linechart",iconSizeX,iconSizeY);
            if(ds instanceof H2F) icon = manager.getIcon("scatter",iconSizeX,iconSizeY);
            DataAttributesEditorPanel daep = new DataAttributesEditorPanel(canvas,dn.get(i).getDataSet().attr());
            tabbedPane.addTab("", icon, daep);
        }
        //ImageIcon icon = manager.getIcon("dimensions",iconSizeX,iconSizeY);
        //DataCanvasEditorPanel dePanel = new DataCanvasEditorPanel(dc);
        //tabbedPane.addTab("", icon, dePanel);
    }
    
    public void addEditors(){
        String[] labels = new String[]{"settings", 
            "dimensions","axis", "histogram2d",
            "scatter","linechart"};
        for(int i = 0; i < labels.length; i++){
            JPanel panel = new JPanel();
            DataAttributes attr = new DataAttributes();
            ImageIcon icon = manager.getIcon(labels[i],24,24);
            tabbedPane.addTab("",icon, new DataAttributes.DataAttributesPanel(attr));
        }
    }
    
    public static JFrame  openOptionsPanel(TGDataCanvas canvas, TGRegion region){        
        
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        CanvasEditorPanel dialog = new CanvasEditorPanel(canvas,region);
        
        frame.add(dialog);
        frame.setSize(500, 500);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
}
