/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.widgets;

import j4np.graphics.Translation2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import twig.config.TAttributesLine;
import twig.config.TPalette;
import twig.config.TStyle;
import twig.editors.DataEditorUtils;

/**
 *
 * @author gavalian
 */
public class Line implements Widget {
    
    private double xOrigin = 0;
    private double yOrigin = 0;
    private double xEnd = 0;
    private double yEnd = 0;
    
    private boolean coordNDF = false;
    private TAttributesLine attrLine = new TAttributesLine();
    private int    lineWidth = 1;
    private int    lineStyle = 1;
    private Color  lineColor = Color.black;
    
    
    
    public Line(double x1, double y1, double x2, double y2){
        xOrigin = x1; yOrigin = y1;
        xEnd = x2; yEnd = y2;
    }
    
    public Line setLineColor(Color lc){
        lineColor = lc; return this;
    }
    
    public Line setLineColor(int lc){
         attrLine.setLineColor(lc);return this;
    }
    
    @Override
    public void draw(Graphics2D g2d, Rectangle2D r, Translation2D tr) {
        TStyle style = TStyle.getInstance();
        g2d.setStroke(style.getLineStroke(attrLine.getLineStyle(), attrLine.getLineWidth()));
        g2d.setColor(style.getPalette().getColor(attrLine.getLineColor()));
        
        int x1 = (int) tr.getX(xOrigin, r);
        int x2 = (int) tr.getX(xEnd, r);
        int y1 = (int) ( r.getY() + r.getHeight() - tr.getY(yOrigin, r) + r.getY());
        int y2 = (int) ( r.getY() + r.getHeight() - tr.getY(yEnd, r) + r.getY());
        g2d.drawLine(x1,y1,x2,y2);
        //System.out.println(xOrigin + " " + yOrigin + " " + xEnd + );
        //tr.show();
        //System.out.println("R = " + r);
        //System.out.println("Y = " + tr.getY(yOrigin, r) + "  " + tr.getY(yEnd, r));
        //System.out.println("Y = " + tr.getX(xOrigin, r));
        
        g2d.drawLine(0, 410, 0, 410);
    }

    public Line setNDF(boolean flag){
        this.coordNDF = flag; return this;
    }
    
    public Line setWidth(int width){ attrLine.setLineWidth(width); return this;}
    public Line setStyle(int style){ attrLine.setLineStyle(style); return this;}
    
    @Override
    public boolean isNDF() {
        return coordNDF;
    }

    @Override
    public void configure(JComponent parent) {
        
        JTextField posX = new JTextField();
        JTextField posY = new JTextField();
               
        JTextField endX = new JTextField();
        JTextField endY = new JTextField();
        
        posX.setText(String.format("%.3f", this.xOrigin));
        posY.setText(String.format("%.3f", this.yOrigin));
        
        endX.setText(String.format("%.3f", this.xEnd));
        endY.setText(String.format("%.3f", this.yEnd));
        
        JCheckBox drawBoxCheck = new JCheckBox();
        drawBoxCheck.setSelected(this.coordNDF);
        
        JSpinner lstyle = DataEditorUtils.makeSpinner(this.lineStyle, 0, 15);
        JSpinner lwidth = DataEditorUtils.makeSpinner(this.lineWidth, 0, 15);
        
        lstyle.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                int style = (int) ((JSpinner) e.getSource()).getValue();
                lineStyle = style;
                attrLine.setLineStyle(style);
                if(parent!=null) parent.repaint();
            }            
        });
        
        lwidth.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                int width = (int) ((JSpinner) e.getSource()).getValue();
                attrLine.setLineWidth(width);
                if(parent!=null) parent.repaint();
            }            
        });
        
        
        JButton b = new JButton();
        b.setBackground(lineColor);
        b.setPreferredSize(new Dimension(50,25));
        b.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                     null,
                     "Choose Line Color",
                     lineColor);
                if(newColor!=null){
                    lineColor = newColor;
                    attrLine.setLineColor(TPalette.createColor(newColor.getRed(), 
                            newColor.getGreen(), newColor.getBlue(), newColor.getAlpha()));
                    b.setBackground(newColor);
                    if(parent!=null) parent.repaint();
                }
            }
            
        });
        Object[] message = {
            "Start X", posX,
            "Start Y", posY,
            "End X", endX,
            "End Y", endY,
            "is NDF", drawBoxCheck,
            "Line Width", lwidth,
            "Line Style",lstyle,
            "Line Color", b
        };
        
        
        int option = JOptionPane.showConfirmDialog(null,                 
                message, "Pave Text", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            this.coordNDF = drawBoxCheck.isSelected();
            this.xOrigin = Double.parseDouble(posX.getText());
            this.yOrigin = Double.parseDouble(posY.getText());
            this.xEnd = Double.parseDouble(endX.getText());
            this.yEnd = Double.parseDouble(endY.getText());            
        }
    }
    
    
}
