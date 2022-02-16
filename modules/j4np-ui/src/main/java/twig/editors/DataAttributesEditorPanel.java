/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.editors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;
import twig.config.TDataAttributes;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class DataAttributesEditorPanel extends JPanel {
    
    private TDataAttributes   attr = null;
    private JComponent      parent = null;
    
        
    public DataAttributesEditorPanel(TDataAttributes __a){
        attr = __a; initUI();
    }
    
    public DataAttributesEditorPanel(JComponent jc,TDataAttributes __a){
        attr = __a; parent = jc; initUI();
    }
    
    private void initUI(){
        this.setLayout(new MigLayout("","",""));
        this.initLine();
        this.add(new JLabel("-"));
        this.add(new JSeparator(),"wrap");
        
        this.initMarker();
        this.add(new JLabel("-"));
        this.add(new JSeparator(),"wrap");
        this.initFill();
        this.add(new JLabel("-"));
        this.add(new JSeparator(),"wrap");
        this.initTF();
    }
    
    private void initLine(){
        JSpinner ls = DataEditorUtils.makeSpinner(attr.getLineStyle(), 0, 15);
        JSpinner lw = DataEditorUtils.makeSpinner(attr.getLineWidth(), 1, 15);
        JSpinner lc = DataEditorUtils.makeSpinner(attr.getLineColor(), 
                0, Integer.MAX_VALUE);
        
        ls.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setLineStyle((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        lc.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setLineColor((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        lw.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setLineWidth((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        
        this.add(new JLabel("Line Width : ",SwingConstants.RIGHT));        
        this.add(lw,"wrap");
        this.add(new JLabel("Style : ",SwingConstants.RIGHT));        
        this.add(ls,"wrap");
        this.add(new JLabel("Color : ",SwingConstants.RIGHT));
        this.add(lc,"wrap");
        
    }
    
    private void initFill(){
        JSpinner fs = DataEditorUtils.makeSpinner(attr.getFillStyle(), -2, 15);
        JSpinner fc = DataEditorUtils.makeSpinner(attr.getFillColor(), -2, Integer.MAX_VALUE);
        
        fs.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setFillStyle((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        fc.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setFillColor((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        
        this.add(new JLabel("Fill Style : ",SwingConstants.RIGHT));        
        this.add(fs,"wrap");

        this.add(new JLabel("Color : ",SwingConstants.RIGHT));
        this.add(fc,"wrap");
        
    }
    
    private void initMarker(){
        
        JSpinner mstyle = DataEditorUtils.makeSpinner(attr.getMarkerStyle(), 0, 15);
        JSpinner msize  = DataEditorUtils.makeSpinner(attr.getMarkerSize(), 1, 35);
        JSpinner mcolor = DataEditorUtils.makeSpinner(attr.getMarkerColor(), 0, Integer.MAX_VALUE);
        
        JSpinner mcolorOut = DataEditorUtils.makeSpinner(attr.getMarkerOutlineColor(), 0, Integer.MAX_VALUE);
        JSpinner msizeOut  = DataEditorUtils.makeSpinner(attr.getMarkerOutlineWidth(), 0, 35);
        
        mstyle.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setMarkerStyle((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        msize.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setMarkerSize((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        mcolor.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setMarkerColor((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        
        mcolorOut.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setMarkerOutlineColor((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        
        msizeOut.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                attr.setMarkerOutlineWidth((int) sp.getValue());
                if(parent!=null) parent.repaint();
            }
        });
        this.add(new JLabel("Marker Size : ",SwingConstants.RIGHT));        
        this.add(msize,"wrap");
        this.add(new JLabel("Style : ",SwingConstants.RIGHT));        
        this.add(mstyle,"wrap");
        this.add(new JLabel("Color : ",SwingConstants.RIGHT));
        this.add(mcolor,"wrap");
        this.add(new JLabel("Width : ",SwingConstants.RIGHT));
        this.add(msizeOut,"wrap");
        this.add(new JLabel("Outline : ",SwingConstants.RIGHT));
        this.add(mcolorOut,"wrap");
    }
    
    private void initTF(){
        JTextField tf_so = DataEditorUtils.makeTextField(attr.getStatOptions(), 7);
        tf_so.addActionListener(new ActionListener() {
      //capture the event on JTextField
            public void actionPerformed(ActionEvent e) {
                //get and display the contents of JTextField in the console
                JTextField tf = (JTextField) e.getSource();
                System.out.println("Text=" + tf.getText());
            }
        });
        this.add(new JLabel("Option Stats : ",SwingConstants.RIGHT));        
        this.add(tf_so,"wrap");
    }
    
    public static void main(String[] args){
        StudioWindow.changeLook();
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        DataAttributesEditorPanel panel = new DataAttributesEditorPanel(new TDataAttributes());
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}
