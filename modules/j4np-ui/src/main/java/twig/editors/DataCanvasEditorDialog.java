/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.editors;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import twig.graphics.TGDataCanvas;
import twig.graphics.TGRegion;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class DataCanvasEditorDialog extends JPanel {
    
    TGDataCanvas canvas = null;
    JTabbedPane     tabbedPane = null;
    
    public DataCanvasEditorDialog (TGDataCanvas c){ canvas = c; initUI();}
    
    
    private void initUI(){
        this.setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        DataCanvasEditorPanel panel = new DataCanvasEditorPanel(canvas);
        tabbedPane.addTab("DataCanvas", null, panel,
                "Options for DataCanvas");
        
        this.add(tabbedPane,BorderLayout.CENTER);
    }
        
    public static JFrame  openOptionsPanel(TGDataCanvas canvas){
        
        
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        DataCanvasEditorDialog dialog = new DataCanvasEditorDialog(canvas);
        frame.add(dialog);

        frame.setSize(500, 500);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
    
    public static JFrame  openOptionsAttributes(TGDataCanvas c,TGRegion r){
        
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        //DataCanvasEditorDialog dialog = new DataCanvasEditorDialog(canvas);
        DataAttributesEditorPanel p = 
                new DataAttributesEditorPanel(c,
                        r.getAxisFrame().getDataNodes().get(0).getDataSet().attr());
        frame.add(p);

        frame.setSize(500, 500);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
}
