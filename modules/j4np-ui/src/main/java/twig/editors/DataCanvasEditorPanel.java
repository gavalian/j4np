/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.editors;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;
import org.drjekyll.fontchooser.FontDialog;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;
import twig.studio.StudioWindow;

/**
 *
 * @author gavalian
 */
public class DataCanvasEditorPanel extends JPanel {
    TGDataCanvas canvas = null;
    
    public DataCanvasEditorPanel(TGDataCanvas c){
        canvas = c;
        this.initUI();
    }
    
    private JSpinner makeSpinner(int value){
        SpinnerModel model =
        new SpinnerNumberModel(value,0,500,1);
        JSpinner spinner = new JSpinner(model);
        return spinner;
    }
    
    private JSpinner makeSpinner(int value, int min, int max){
        SpinnerModel model =
        new SpinnerNumberModel(value,min,max,1);
        JSpinner spinner = new JSpinner(model);
        return spinner;
    }
    
    private void initUI(){
        this.setLayout(new MigLayout("","[]50[]20[]20[]20[]","[]20[]"));

        JSpinner  spinnerLeft  = makeSpinner((int) canvas.region().getInsets().getLeft());
        JSpinner  spinnerRight = makeSpinner((int) canvas.region().getInsets().getRight());
        JSpinner  spinnerTop   = makeSpinner((int) canvas.region().getInsets().getTop());
        JSpinner  spinnerBott  = makeSpinner((int) canvas.region().getInsets().getBottom());
        
        spinnerLeft.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.left((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerRight.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.right((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerTop.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.top((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerBott.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.bottom((int) sp.getValue());
                canvas.repaint();
            }
        });

        this.add(new JLabel(" "));
        this.add(new JLabel(" "));
        this.add(new JLabel("Left"));
        this.add(new JLabel("Rigth"));
        this.add(new JLabel("Top"));
        this.add(new JLabel("Bottom"),"wrap");
        
        this.add(new JLabel("Insets"));
        this.add(new JLabel(" "));
        this.add(spinnerLeft);
        this.add(spinnerRight);
        this.add(spinnerTop);
        this.add(spinnerBott,"wrap");
        
        JSpinner  spinnerDivX  = makeSpinner((int) canvas.region().getAxisFrame().getAxisX().getAttributes().getAxisTickMarkCount(),2,10);
        JSpinner  spinnerDivY  = makeSpinner((int) canvas.region().getAxisFrame().getAxisX().getAttributes().getAxisTickMarkCount(),2,10);

        JSpinner  spinnerTicksSizeX  = makeSpinner((int) canvas.region().getAxisFrame().getAxisX().getAttributes().getAxisTickMarkSize(),-20,20);
        JSpinner  spinnerTicksSizeY  = makeSpinner((int) canvas.region().getAxisFrame().getAxisY().getAttributes().getAxisTickMarkSize(),-20,20);
        
        
        JSpinner  spinnerLOffX  = makeSpinner((int) canvas.region().getAxisFrame().getAxisX().getAttributes().getAxisLabelOffset(),-40,40);
        JSpinner  spinnerLOffY  = makeSpinner((int) canvas.region().getAxisFrame().getAxisY().getAttributes().getAxisLabelOffset(),-40,40);

        JSpinner  spinnerTOffX  = makeSpinner((int) canvas.region().getAxisFrame().getAxisX().getAttributes().getAxisTitleOffset(),-40,40);
        JSpinner  spinnerTOffY  = makeSpinner((int) canvas.region().getAxisFrame().getAxisY().getAttributes().getAxisTitleOffset(),-40,40);
        
        spinnerDivX.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.divisionsX((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerDivY.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.divisionsY((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerTicksSizeY.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.ticksSizeY((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerTicksSizeX.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.ticksSizeX((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerLOffX.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.labelOffsetX((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerLOffY.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.labelOffsetY((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerTOffX.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.titleOffsetX((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerTOffY.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.titleOffsetY((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        JCheckBox drawCheckX = new JCheckBox("Enabled");
        JCheckBox drawCheckY = new JCheckBox("Enabled");
        
        
        this.add(new JLabel(""));
        this.add(new JLabel("Draw"));
        this.add(new JLabel("Divisions"));
        this.add(new JLabel("Tick Size"));
        this.add(new JLabel("Label Offset"));
        this.add(new JLabel("Title Offset"),"wrap");
        
        this.add(new JLabel("X-axis"));
        this.add(drawCheckX);
        this.add(spinnerDivX);
        this.add(spinnerTicksSizeX);        
        this.add(spinnerLOffX);
        this.add(spinnerTOffX,"wrap");
        
        this.add(new JLabel("Y-axis"));
        this.add(drawCheckY);
        this.add(spinnerDivY);
        this.add(spinnerTicksSizeY);
        this.add(spinnerLOffY);
        this.add(spinnerTOffY,"wrap");
        
        
        JButton fontButton = new JButton("...");
        
        fontButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //JTextArea textArea = new JTextArea();
                //FontDialog.showDialog(textArea);
                FontDialog dialog = new FontDialog((Frame)null, "Font Dialog Example", true);             
                dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);                                      
                dialog.setVisible(true);                                                                                
                if (!dialog.isCancelSelected()) { 
                    Font f = dialog.getSelectedFont();
                    System.out.println("Selected font is: " + dialog.getSelectedFont()); 
                    canvas.setAxisFont(f);
                    canvas.repaint();
                }                 
            }
            
        });
                
        
        
        JSpinner  spinnerLineWidth  = makeSpinner((int) canvas.region().getAxisFrame().getAxisX().getAttributes().getAxisLineWidth(),1,10);
        JSpinner  spinnerTicksLineWidth  = makeSpinner((int) canvas.region().getAxisFrame().getAxisX().getAttributes().getAxisLineWidth(),1,10);
        
        spinnerLineWidth.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.axisLineWidth((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        spinnerTicksLineWidth.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner sp = (JSpinner) e.getSource();
                canvas.axisTicksLineWidth((int) sp.getValue());
                canvas.repaint();
            }
        });
        
        this.add(new JLabel("Chose Font"));
        this.add(fontButton);
        this.add(spinnerLineWidth);
        this.add(spinnerTicksLineWidth);

    }
    
    public static void main(String[] args){
        
        StudioWindow.changeLook("Flat Light");
        TGCanvas c = new TGCanvas();
        c.view().divide(2, 2);
        
        DataCanvasEditorDialog.openOptionsPanel(c.view());
        /*JFrame frame = new JFrame();

        DataCanvasEditorPanel panel = new DataCanvasEditorPanel(c.view());
        frame.add(panel);
        frame.setSize(500,500);
        frame.setVisible(true);*/
    }
}
