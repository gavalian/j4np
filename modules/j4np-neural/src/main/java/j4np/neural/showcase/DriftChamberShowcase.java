/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.showcase;

import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import twig.data.GraphErrors;
import twig.graphics.TGCanvas;
import twig.graphics.TGENode2D;
import twig.studio.StudioComponent;
import twig.studio.StudioMainWindow;
import twig.widgets.Widget;

/**
 *
 * @author gavalian
 */
public class DriftChamberShowcase implements ActionListener {
    
    //TGCanvas     c = new TGCanvas();
    StudioMainWindow mw = new StudioMainWindow();
    HipoReader  rc = null;
    HipoReader  ra = null;
    
    DriftChamberStore cStore = null;
    DriftChamberStore aStore = null;
    Event  cEvent = new Event();
    Event  aEvent = new Event();
    Timer  caTimer = null;
    Timer  aiTimer = null;
    String filename = "";
    StudioComponent component = null;
    public DriftChamberShowcase(){
       //c.view().divide(2,1);
       
       //c.setBackground(new Color(200,200,200));
      this.init();
       mw.initialize();
       mw.canvas().divide(2, 1);
       mw.setVisible(true);
       
       
    }
    
    private void init(){
        component = new StudioComponent();
       component.setLayout(new FlowLayout());
       JButton btn2 = new JButton("2");
       JButton btn5 = new JButton("5");
       JButton btn10 = new JButton("10");
       btn2.addActionListener(this);
       btn5.addActionListener(this);
       btn10.addActionListener(this);
       
       component.add(btn2);
       component.add(btn5);
       component.add(btn10);
       
       mw.setBottomPane(component);
    }
    
    public void open(String h5f){
        rc = new HipoReader(h5f);
        ra = new HipoReader(h5f);
        filename = h5f;
        cStore = new DriftChamberStore(rc);
        aStore = new DriftChamberStore(ra);
    }
    
    
    public void initTimers(int factor){
        
        
        if(this.aiTimer!=null) this.aiTimer.cancel();
        if(this.caTimer!=null) this.caTimer.cancel();
        
        TimerTask taskCA = new TimerTask(){
            @Override
            public void run() {
                if(rc.hasNext()==false) {
                    //rc.rewind();
                    System.out.println("re-opening the file...");
                    rc = new HipoReader(filename);
                }
                rc.next(cEvent);
                cStore.apply(cEvent);
                if(cStore.dcHits.getVectorX().getSize()>0){
                    try {
                        mw.canvas().region(0).getAxisFrame().getDataNodes().clear();
                        mw.canvas().region(0).getAxisFrame().addDataNode(new TGENode2D((GraphErrors)  aStore.dcHits,"P"));
                        for(GraphErrors g : aStore.dcTracks)
                            mw.canvas().region(0).getAxisFrame().addDataNode(new TGENode2D((GraphErrors)  g,"PL"));
                        if(mw.canvas().region(0).getAxisFrame().getWidgets().size()<1){
                            for(Widget w : aStore.boundaries){
                                mw.canvas().region(0).getAxisFrame().getWidgets().add(w);
                            }
                        }
                        //for(Widget w : aStore.boundaries){
                        //    c.region(0).getAxisFrame().getWidgets().add(w);
                        //}
                        /*c.cd(0).draw(cStore.dcHits);
                        c.region(0).draw(cStore.boundaries);
                        c.region(0).draw(cStore.dcTracks, "samePL");*/
                        mw.canvas().region(0).axisLimitsX(-200, 200);
                        mw.canvas().region(0).axisLimitsY(-200, 200);
                        mw.canvas().repaint();
                    } catch (Exception e){ System.out.println("oooops"); }
                }
            }
        };
        
        TimerTask taskAI = new TimerTask(){
            @Override
            public void run() {
                if(ra.hasNext()==false) {
                    //ra.rewind();
                    System.out.println("re-opening the file...");
                    ra = new HipoReader(filename);
                }
                ra.next(aEvent);
                aStore.apply(aEvent);
                if(aStore.dcHits.getVectorX().getSize()>0){
                    try {
                        mw.canvas().region(1).getAxisFrame().getDataNodes().clear();
                        mw.canvas().region(1).getAxisFrame().addDataNode(new TGENode2D((GraphErrors)  aStore.dcHits,"P"));
                        for(GraphErrors g : aStore.dcTracks)
                            mw.canvas().region(1).getAxisFrame().addDataNode(new TGENode2D((GraphErrors)  g,"PL"));
                        if(mw.canvas().region(1).getAxisFrame().getWidgets().size()<1){
                            for(Widget w : aStore.boundaries){                        
                                mw.canvas().region(1).getAxisFrame().getWidgets().add(w);
                            }
                        }
                        //c.cd(1).draw(aStore.dcHits);
                        //c.region(1).draw(aStore.boundaries);
                        //c.region(1).draw(aStore.dcTracks, "samePL");
                        
                        mw.canvas().region(1).axisLimitsX(-200, 200);
                        mw.canvas().region(1).axisLimitsY(-200, 200);
                        mw.canvas().repaint();
                    } catch (Exception e){ System.out.println("oooops"); }
                }
            }
            
        };
        System.out.println(" Define new timers with factor = " + factor);
        int scale = factor;
        this.caTimer = new Timer("Conventional");
        this.caTimer.scheduleAtFixedRate(taskCA, 1200, 700*scale);
        
        this.aiTimer = new Timer("Machine");
        //this.aiTimer.scheduleAtFixedRate(taskAI, 30, 5*scale);        
        this.aiTimer.scheduleAtFixedRate(taskAI, 30, 1*scale);  
    }
    
    public static void main(String[] args){       
        
        DriftChamberShowcase show = new DriftChamberShowcase();
        
        show.open("/Users/gavalian/Work/Software/project-10.8/study/level3/showcase_1_0.h5");
        
        show.initTimers(2);
        /*
        HipoReader r = new HipoReader("/Users/gavalian/Work/Software/project-10.8/study/level3/output_test_inbending_large.h5");
        DriftChamberStore store = new DriftChamberStore(r);
        Event e = new Event();
        
        
        r.getEvent(e, 236);
        
        store.apply(e);
        
        TGCanvas c = new TGCanvas();
        
        c.draw(store.dcHits);
        c.region().draw(store.boundaries);
        c.region().draw(store.dcTracks, "samePL");
        */
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("2")==0){
            this.initTimers(2);
        }
        if(e.getActionCommand().compareTo("5")==0){
            this.initTimers(5);
        }
        if(e.getActionCommand().compareTo("10")==0){
            System.out.println(" RESET SCALE = 10");
            this.initTimers(10);
        }
    }
}
