/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import twig.data.H1F;
import twig.data.H2F;
import twig.widgets.Line;

/**
 *
 * @author gavalian
 */
public class ViewPanel2D extends DatasetActionPanel implements ActionListener {
        
    H2F hist2D = null;
    private String viewType = "X";
    JPanel context = null;
    JPanel contextPane = null;
    
    public ViewPanel2D(H2F h, Frame parent, String type){
       super(parent);
       hist2D = h;
       viewType = type;       
       this.init();              
    }
    
    private void init(){
        
        int nBins = hist2D.getAxisY().getNBins();
        if(viewType.compareTo("Y")==0) nBins = hist2D.getAxisX().getNBins();
        JSlider binSlider = new JSlider(JSlider.HORIZONTAL,
                                      0, nBins-1, 1);

        binSlider.setMajorTickSpacing(10);
        binSlider.setMinorTickSpacing(1);
        binSlider.setPaintTicks(true);
        binSlider.setPaintLabels(true);
        
        binSlider.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                int bin = (( JSlider ) e.getSource()).getValue();
                updateWithBin(bin);
            }        
        });
        context = new JPanel();
        context.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        context.setLayout(new BorderLayout());
        context.add(binSlider,BorderLayout.CENTER);
        
        contextPane = new JPanel();
        contextPane.setLayout(new BorderLayout());
        contextPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        contextPane.add(context,BorderLayout.CENTER);
        setActionComponent(contextPane);
        
        initUI(new String[]{"2D-Viwer"});
        getCanvas().activeCanvas().divide(1, 2);
        getCanvas().activeCanvas().region(0).draw(hist2D);
        
        int nbin = 0;
        if(viewType.compareTo("Y")==0){
            nbin = hist2D.getAxisX().getNBins()/2;
        } else {
            nbin = hist2D.getAxisY().getNBins()/2;
        }
        
        this.updateWithBin(nbin);
    }
    
    private void updateWithBin(int bin){
        if(viewType.compareTo("Y")==0){
            H1F h = hist2D.sliceX(bin);
            
            double x = hist2D.getAxisX().getBinCenter(bin);
            double y1 = hist2D.getAxisY().min();
            double y2 = hist2D.getAxisY().max();
            Line l = new Line(x,y1,x,y2);
            l.setNDF(false);
            l.setLineColor(5);
            l.setWidth(2);
            getCanvas().activeCanvas().region(0).draw(hist2D).draw(l);
            getCanvas().activeCanvas().region(1).draw(h);
            getCanvas().activeCanvas().repaint();
        } else {
            H1F h = hist2D.sliceY(bin);
            double y = hist2D.getAxisY().getBinCenter(bin);
            double x1 = hist2D.getAxisX().min();
            double x2 = hist2D.getAxisX().max();
            Line l = new Line(x1,y,x2,y);
            l.setWidth(2);
            l.setNDF(false);
            l.setLineColor(5);
            getCanvas().activeCanvas().region(0).draw(hist2D).draw(l);
            getCanvas().activeCanvas().region(1).draw(h);
            getCanvas().activeCanvas().repaint();
        } 
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
}
