/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.monitor;

import j4np.graphics.Canvas2D;
import j4np.graphics.Node2D;
import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.instarec.core.ChamberData;
import j4np.instarec.core.DriftChamber2;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class DriftChamberDebug extends JFrame implements ActionListener {
    
    //TGDataCanvas c = new TGDataCanvas();
    Canvas2D canvas = new Canvas2D();
    JPanel    panel = new JPanel();
    Superlayer2D layer = null;
    Superlayer2D layerFit = null;
    DriftChamber2 drift = new DriftChamber2();
    JTextField textField = null;
    public String file = "";
    boolean useDenoise = false;
    boolean useSanitizer = false;
    
    public DriftChamberDebug(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.initUI();
    }
    
    private void initUI(){
        this.setLayout(new BorderLayout());

        this.add(canvas,BorderLayout.CENTER);
        
        layer    = new Superlayer2D(canvas);
        layerFit = new Superlayer2D(canvas);
        
        canvas.addNode(layer);
        canvas.addNode(layerFit);
        
        canvas.getGraphicsComponents().get(0).setBoundsBind(0, 0, 1.0, 0.5);
        canvas.getGraphicsComponents().get(0).alignMode(Node2D.ALIGN_RELATIVE);
        
        canvas.getGraphicsComponents().get(1).setBoundsBind(0, 0.5, 1.0, 0.5);
        canvas.getGraphicsComponents().get(1).alignMode(Node2D.ALIGN_RELATIVE);
        
        panel = new JPanel();
        JButton b1 = new JButton("do");
        JButton b2 = new JButton("reset");
        
        JButton b3 = new JButton("load");
        textField = new JTextField("1");
        
        b1.addActionListener(this);
        b2.addActionListener(this);
        b3.addActionListener(this);
        JCheckBox checkBox = new JCheckBox("use denoise");
        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBox.isSelected()) {
                    useDenoise = true;
                } else {
                    useDenoise = false;
                }
            }
        });
        
        JCheckBox checkBox2 = new JCheckBox("use sanitizer");
        checkBox2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (checkBox2.isSelected()) {
                    useSanitizer = true;
                } else {
                    useSanitizer = false;
                }
            }
        });
        panel.setLayout(new FlowLayout());
        panel.add(b1);
        panel.add(b2);
        panel.add(b3);
        panel.add(textField);
        panel.add(checkBox);
        panel.add(checkBox2);
        
        this.add(panel,BorderLayout.SOUTH);
    }
    
    public void analyze(){
        drift.reset();
        for(int l = 0; l < 6; l++){
            for(int w = 0; w < 112; w++){
                if(layer.wires[l][w]>0) drift.setWire(1, l+1, w+1);
            }
        }
        
        ChamberData data = new ChamberData();
        drift.scan(data, 1, 1);
        this.layerFit.reset();
        if(this.useSanitizer==true) drift.sanitize(data);
        System.out.println(Arrays.toString(data.type));
        System.out.println(Arrays.toString(data.scan));
        for(int i = 0; i < 112; i++){
            if(data.scan[i]>0) 
                for(int j = 0; j < data.scan[i];j++) this.layerFit.wires[5-j][111-i] = 1;
        }
        this.layerFit.update();
        /*
        //drift.show(1);
        List<int[]> result = drift.scan();
        System.out.println(">>>>>>");
        System.out.println(Arrays.toString(result.get(0)));
        System.out.println(Arrays.toString(result.get(1)));
        this.layerFit.reset();
        
        for(int i = 0; i < result.get(0).length; i++){
            if(result.get(1)[i]>3){
                for( int j = 0; j < result.get(1)[i]; j++) this.layerFit.wires[j][112-i]=1;
            }
        }*/
    }
    
    public void load(int event){
        HipoReader r = new HipoReader(file);
        Bank[] b = r.getBanks("DC::tdc");
        Event e = new Event();
        r.getEvent(e, event);
        e.read(b);
        layer.reset();
        int nrows = b[0].getRows();
        for(int i = 0; i < nrows; i++){
            int sector = b[0].getInt("sector", i);
            int  wl = b[0].getInt("layer", i);
            int wire = b[0].getInt("component", i);
            if(sector==1&&wl>=1&&wl<=6){
                if(useDenoise==false){
                    layer.wires[wl-1][wire-1] = 1;
                } else {
                    int order = b[0].getInt("order", i);
                    if(order==0||order==40||order==50) layer.wires[wl-1][wire-1] = 1;
                }
            }
        }
        layer.update();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("reset")==0) {
            this.layer.reset(); this.layerFit.reset();
        }
        if(e.getActionCommand().compareTo("do")==0) this.analyze();
        if(e.getActionCommand().compareTo("load")==0) {
            String text = textField.getText();
            int nEvent = Integer.parseInt(text);
            System.out.println(" loading event # " + nEvent);
            this.load(nEvent);
        }
    }
    
    public static void main(String[] args){
        StudioWindow.changeLook("DeepOcean");
        DriftChamberDebug frame = new DriftChamberDebug();
        frame.file = "/Users/gavalian/Work/DataSpace/decoded/clas_006595.evio.00625-00629_denoised.hipo";
        frame.pack();
        frame.setSize(1500, 250);
        frame.setVisible(true);
        
    }
}
