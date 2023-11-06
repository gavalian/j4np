/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author gavalian
 */
public class TTabDataCanvas extends JPanel {
    
    JTabbedPane       tabbedPane = null;
    List<TGDataCanvas>  canvases = new ArrayList<>();
    
    protected List<CanvasPublisher> publishers = new ArrayList<>();
    
    public TTabDataCanvas(){
        super();
        init(null,new String[]{"default"});
    }
    
    public TTabDataCanvas(JPanel controls){
        super();
        init(null,new String[]{"default"});
    }
    
    public TTabDataCanvas(String[] names){
        super();
        init(null,names);
    }
    
    public int getSelected(){
        return tabbedPane.getSelectedIndex();
    }
    
    public void addPublisher(CanvasPublisher cpb){
        this.publishers.add(cpb);
    }
    
    public void setSelected(int index){
        tabbedPane.setSelectedIndex(index);
    }
    
    public List<BufferedImage> getScreenShots(){
        List<BufferedImage> shots = new ArrayList<>();
        for(TGDataCanvas c : this.canvases) shots.add(c.getScreenShot());
        return shots;
    }
    
    
    public void publish(){
        List<BufferedImage> imgList = this.getScreenShots();
        for(CanvasPublisher pb : this.publishers){
            pb.publish(imgList);
        }
    }
    
    public final void init(JPanel controls, String[] names){
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        for(int i = 0; i < names.length; i++){
            TGDataCanvas c = new TGDataCanvas();
            this.canvases.add(c);
            tabbedPane.addTab(names[i], null, c, "canvas("+names[i]+")");
        }
        add(tabbedPane,BorderLayout.CENTER);
        if(controls!=null) add(controls,BorderLayout.PAGE_END);
        
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                System.out.println("Tab: " + tabbedPane.getSelectedIndex());
                System.out.println(">>> calling : update timer status method");
                updateTimers();
                // Prints the string 3 times if there are 3 tabs etc
            }
        });
    }
    
    public void addCanvas(String name, boolean focused){
        TGDataCanvas c = new TGDataCanvas();
        this.canvases.add(c);
        tabbedPane.addTab(name, null, c, "canvas("+name+")");
        if(focused==true){
            tabbedPane.setSelectedIndex(canvases.size()-1);
        }
    }
    
    public TGDataCanvas activeCanvas(){ return this.canvases.get(tabbedPane.getSelectedIndex());}
    public List<TGDataCanvas> getCanvases(){
        return canvases;
    } 
    
    public void initTimers(int interval){
        for(int i = 0; i < this.canvases.size(); i++){
            this.canvases.get(i).initTimer(interval);
        }
    }
    
    public void updateTimers(){
        for(int i = 0; i < this.canvases.size(); i++){
            this.canvases.get(i).setTimerStatus(true);
        }        
        this.activeCanvas().setTimerStatus(false);
    }    
    
    public static abstract class CanvasPublisher {
        public abstract void publish(List<BufferedImage> imgList);
    }
}
