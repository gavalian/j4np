/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.editors;

import java.awt.BorderLayout;
import javax.swing.JComponent;
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
    
    TGDataCanvas        canvas = null;
    //JTabbedPane     tabbedPane = null;
    TGRegion            region = null;
    JComponent          parent = null;
    CanvasEditorPanel   cEditor = null;
    public DataCanvasEditorDialog (TGDataCanvas c){ 
        canvas = c; initUI();
    }
    
    public DataCanvasEditorDialog ( TGDataCanvas c, TGRegion r){ 
        canvas = c; 
        region = r;
        //parent = p;
        initUI();
    }
    
    private void initUI(){                        
        this.setLayout(new BorderLayout());
        cEditor = new CanvasEditorPanel(canvas,region);        
        this.add(cEditor,BorderLayout.CENTER);
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
    
    public static JFrame  openOptionsPanel(TGDataCanvas canvas, TGRegion region){
        
        
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        DataCanvasEditorDialog dialog = new DataCanvasEditorDialog(canvas, region);
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
                        r.getAxisFrame().getDataNodes().get(0));
        frame.add(p);

        frame.setSize(500, 500);
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
}
