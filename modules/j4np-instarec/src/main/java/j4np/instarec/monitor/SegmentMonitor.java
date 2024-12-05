/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.monitor;

import j4np.data.base.DataEvent;
import j4np.data.base.DataSource;
import j4np.data.base.DataWorker;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.data.Leaf;
import j4np.hipo5.gui.NodeTable;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.core.DriftChamber;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;
import twig.graphics.TTabDataCanvas;
import twig.studio.AppWindow;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class SegmentMonitor extends DataWorker implements ActionListener {

    Bank[] b = null;
    Leaf leaf = new Leaf(2*1024);
    
    DriftChamber drift = new DriftChamber();
    
    DataStore    store = new DataStore();
    
    boolean initialized = false;
    int sector = 1;
    
    boolean useDenoise = true;
    
    public JPanel actionPanel = null;
    public TGDataCanvas canvas = new TGDataCanvas();
    public TTabDataCanvas canvasTab = new TTabDataCanvas(new String[]{"TimeBased-vs-DC","DC-vs-Segment","DC-vs-RAW"});
     HipoReader r = null;
     Event event = new Event();

        
     public SegmentMonitor(String file){
         r = new HipoReader(file);
         b = r.getBanks("TimeBasedTrkg::TBClusters","TimeBasedTrkg::TBTracks","DC::tdc");
         init(r);
     }
     
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Next")==0){
            r.next(event);
            this.execute(event);
            this.drawSector();
        }
    }
    
    
    
    public static class DataStore {
        H1F[] hTBCLS = null;
        H1F[] hDCSEG = null;
        
        H1F[] hDCPOS = null;
        H2F[] hDCRAW = null;
        H2F[] hDCRAWUN = null;
    }
    
    @Override
    public boolean init(DataSource src) {
        this.actionPanel = new JPanel();
        //panel.setLayout(new GridLayout(3, 1)); // 3 rows, 1 column

        JComboBox comboBox = new JComboBox(new String[]{"1","2","3","4","5","6"});
        
        comboBox.setPreferredSize(new java.awt.Dimension(120, 30)); // Set preferred size
        comboBox.setMaximumSize(new java.awt.Dimension(120, 30));   // Enforce max size
        comboBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected item
                String selectedItem = (String) comboBox.getSelectedItem();
                // Display the selected item
                sector = Integer.parseInt(selectedItem);
                System.out.println("Selected Item: " + selectedItem);
                drawSector();
            }
        });
        JCheckBox checkbox1 = new JCheckBox("use Denoise");
        checkbox1.addActionListener(
                //this);ActionListener listener = 
                        new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                if (source.isSelected()) {
                    System.out.println(source.getText() + " is selected");
                    useDenoise = true;
                    drawSector();
                } else {
                    System.out.println(source.getText() + " is deselected");
                    useDenoise = false;
                    drawSector();
                }
            }
        });
       
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        
        //this.actionPanel.setLayout(new BoxLayout(this.actionPanel, BoxLayout.Y_AXIS));
        this.actionPanel.setLayout(new GridLayout(3, 2));
        this.actionPanel.add(comboBox);
        this.actionPanel.add(checkbox1);
        this.actionPanel.add(nextButton);
        for(int i = 0; i < 3; i++) canvasTab.getCanvases().get(i).set("bc=#1C2739");

        return true;
    }

    
    public void drawSector(){
        if(initialized==false) return;
        this.canvas.divide(1,12);
        canvas.set("bc=#555566;margins=[5,5,5,5];font=null");
        canvas.set("axiscolor=#00AA00;insets=[10,10,10,20]");
        canvasTab.getCanvases().get(0).divide(1,12);
        canvasTab.getCanvases().get(0).set("bc=#1C2739;margins=[10,10,5,5];font=null");
        canvasTab.getCanvases().get(0).set("axiscolor=#666666;insets=[10,20,10,20]");
        canvasTab.getCanvases().get(1).divide(1,12);

        canvasTab.getCanvases().get(2).divide(1,12);
        canvasTab.getCanvases().get(1).set("bc=#1C2739;margins=[10,10,5,5];font=null");
        canvasTab.getCanvases().get(1).set("axiscolor=#999999;insets=[10,20,10,20]");
        canvasTab.getCanvases().get(2).set("bc=#1C2739;margins=[10,10,5,5];font=null");
        canvasTab.getCanvases().get(2).set("axiscolor=#1C2739;insets=[10,20,10,20]");
        
        int offset = (sector-1)*6;
        for(int i = 0; i < 6; i++)
            canvasTab.getCanvases().get(0).cd(i*2).region().draw(store.hTBCLS[offset+i]);
        for(int i = 0; i < 6; i++)
            canvasTab.getCanvases().get(0).cd(i*2+1).region().draw(store.hDCSEG[offset+i]);
        
        for(int i = 0; i < 6; i++)
            canvasTab.getCanvases().get(1).cd(i*2).region().draw(store.hDCPOS[offset+i]);
        for(int i = 0; i < 6; i++)
            canvasTab.getCanvases().get(1).cd(i*2+1).region().draw(store.hDCSEG[offset+i]);
        
        for(int i = 0; i < 6; i++)
            canvasTab.getCanvases().get(2).cd(i*2).region().draw(store.hDCPOS[offset+i]);
        //store.hDCRAW[0].
        for(int i = 0; i < 6; i++)
            if(useDenoise) canvasTab.getCanvases().get(2).cd(i*2+1).region().draw(store.hDCRAW[offset+i],"F");
            else {
                System.out.println("-- drawing undenoised");
                canvasTab.getCanvases().get(2).cd(i*2+1).region().draw(store.hDCRAWUN[offset+i],"F");
            }
    }
    
    @Override
    public void execute(DataEvent e) {
        
        ((Event)e).read(b);
        ((Event)e).read(leaf,32101,10);
        store.hTBCLS = tbc(b[0]);        
        store.hDCSEG = fromLeaf(leaf);        
        store.hDCRAW = h2d(b[2],true);
        store.hDCRAWUN = h2d(b[2],false);
        
        
        drift.fillBank(b[2], useDenoise);
        int[] pos = new int[112];
        store.hDCPOS = new H1F[36];
        int counter = 0;
        for(int s = 0 ; s < 6; s++){
            for(int i = 0; i < 6; i++){
                drift.analyze(s+1, i+1, pos);
                System.out.println(Arrays.toString(pos));
                store.hDCPOS[counter] = new H1F("h",0.5,112.5,pos);
                store.hDCPOS[counter].attr().set("fc=5");
                counter++;
            }
        }
        
        initialized = true;
        for(int i = 0; i < store.hTBCLS.length;i++) store.hTBCLS[i].attr().set("fc=#9E60FF,lc=#9E60FF");
        for(int i = 0; i < store.hDCSEG.length;i++) store.hDCSEG[i].attr().set("fc=#FFE970,lc=#FFE970");
        for(int i = 0; i < store.hDCPOS.length;i++) store.hDCPOS[i].attr().set("fc=#FF408D,lc=#FF408D");
        /*
        TGCanvas c = new TGCanvas("TIME",600,600);
        c.view().divide(6,6);
        for(int i = 0; i < hL.length; i++) {
            hL[i].attr().set("lc=3,fc=3");
            c.cd(i).draw(hL[i]);
            
        }
        
        c.view().set("bc=#555566;margins=[5,5,5,5];font=null");
        c.view().set("axiscolor=#00AA00;insets=[10,10,10,20]");
        */
        
        //c.view().set("font=null");
        //c.repaint();
        /*
        H1F[]  hL2 = fromLeaf(leaf);
        TGCanvas c2 = new TGCanvas("SEGMENT FINDER",700,700);
        c2.view().divide(6,6);
        for(int i = 0; i < hL2.length; i++) 
            c2.cd(i).draw(hL2[i]);
        
        
        H2F[] h2 = h2d(b[2]);
        TGCanvas c3 = new TGCanvas();
        c3.view().divide(1,6);
        for(int i = 0; i < h2.length; i++){            
            c3.cd(i).draw(h2[i]);
        }
        c3.view().set("margins=[5,5,5,5]");
        */
        
        /*
        drift.fillBank(b[2], true);
        int[] pos = new int[112];
        H1F[] sf = new H1F[36];
        int counter = 0;
        for(int s = 0 ; s < 6; s++){
            for(int i = 0; i < 6; i++){
                drift.analyze(s+1, i+1, pos);
                System.out.println(Arrays.toString(pos));
                sf[counter] = new H1F("h",0.5,112.5,pos);
                sf[counter].attr().set("fc=5");
                counter++;
            }
        }
        
        
        TGCanvas c4 = new TGCanvas("POSITIONS",700,700);
        c4.view().divide(6,6);
        for(int i = 0; i < sf.length; i++)
            c4.cd(i).draw(sf[i]);*/
    }
    
    public H2F[] h2d(Bank b, boolean denoise){
        H2F[] h = H2F.duplicate(36, "", 112, 0.5, 112.5, 6, 0.5, 6.5);
        for(int i = 0; i <b.getRows(); i++){
            int sector = b.getInt("sector", i);
            int layer = b.getInt("layer", i);
            int wire = b.getInt("component", i);
            int order = b.getInt("order", i);
            int index = (sector-1)*6 + ((layer-1)/6);
            int local = (layer-1)%6;
            if(denoise==false){
                if(order==0||order==40||order==50)
                    h[index].setBinContent(wire-1,local, 1.0); else
                    h[index].setBinContent(wire-1,local, 0.7);
            } else {
                if(order==0||order==40||order==50)
                    h[index].setBinContent(wire-1,local, 1.0);
            }
        }
        return h;
    }
    
    public H1F[] fromLeaf(Leaf l){
        H1F[] h = H1F.duplicate(36, "H", 112, 0.5, 112.5);
        for(int i = 0; i <l.getRows(); i++){
            int sector = l.getInt(1, i);
            int supl = l.getInt(2, i);
            double wire = l.getDouble(5, i);
            int index = (sector-1)*6+(supl-1);
            h[index].setBinContent(h[index].getAxisX().getBin(wire), 1.);
        }
        return h;
    }
    
    public H1F[] tbc(Bank b){
        H1F[] h = H1F.duplicate(36, "H", 112, 0.5, 112.5);
        for(int i = 0; i <b.getRows(); i++){
            int sector = b.getInt("sector", i);
            int supl = b.getInt("superlayer", i);
            double wire = b.getFloat("avgWire", i);
            int index = (sector-1)*6+(supl-1);
            h[index].setBinContent(h[index].getAxisX().getBin(wire), 1.);
        }
        return h;
    }
            
    public static void main(String[] args){
        StudioWindow.changeLook("DarkMaterial");        
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kDeepSea);
        Leaf leaf = Leaf.random(24, "8i6f4s4f2b");
        NodeTable table = new NodeTable(leaf);
        AppWindow frame = new AppWindow();

        SegmentMonitor mon = new SegmentMonitor("chain_output_filter_1.h5");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 650);
        frame.setVisible(true);
        
        frame.addLeft(mon.actionPanel);
        frame.addRight(mon.canvasTab);
        frame.addBottom(table);
        /*
        HipoReader r = new HipoReader("chain_output_filter_1.h5");
        Bank[] b = r.getBanks("TimeBasedTrkg::TBClusters","TimeBasedTrkg::TBTracks","DC::tdc");
        r.nextEvent(b);
        

        mon.b = b;
        Event e = new Event();
        //--- good one for(int i = 0; i < 8;i++) r.next(e);
        //-- good for(int i = 0; i < 10;i++) r.next(e);
        //--- good for(int i = 0; i < 12;i++) r.next(e);
        //for(int i = 0; i < 14;i++) r.next(e);
        //--- for(int i = 0; i < 15;i++) r.next(e);
        //---check this- for(int i = 0; i < 17;i++) r.next(e);
        for(int i = 0; i < 21;i++) r.next(e);
        mon.execute(e);
        Leaf l = new Leaf(2048);
        e.read(b);
        e.read(l,32000,1);
        
        b[1].show();
        l.print();*/
        
    }
}
