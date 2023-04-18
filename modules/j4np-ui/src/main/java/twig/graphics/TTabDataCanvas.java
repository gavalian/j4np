/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author gavalian
 */
public class TTabDataCanvas extends JPanel {
    JTabbedPane tabbedPane = null;
    List<TGDataCanvas>  canvases = new ArrayList<>();
    public TTabDataCanvas(){
        super();
        init(new String[]{"default"});
    }
    public TTabDataCanvas(String[] names){
        super();
        init(names);
    }
    
    public int getSelected(){
        return tabbedPane.getSelectedIndex();
    }
    
    public void setSelected(int index){
        tabbedPane.setSelectedIndex(index);
    }
    
    public final void init(String[] names){
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        for(int i = 0; i < names.length; i++){
            TGDataCanvas c = new TGDataCanvas();
            this.canvases.add(c);
            tabbedPane.addTab(names[i], null, c, "canvas("+names[i]+")");
        }
        add(tabbedPane,BorderLayout.CENTER);
    }
    
    public List<TGDataCanvas> getCanvases(){
        return canvases;
    } 
}
