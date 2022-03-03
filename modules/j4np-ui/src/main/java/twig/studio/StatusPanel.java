/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author gavalian
 */
public class StatusPanel extends JPanel {
    JPanel   leftPane = null;
    JPanel centerPane = null;
    JPanel  rightPane = null;
    
    JLabel leftLabel = null;
    JLabel centerLabel = null;
    JLabel rightLabel = null;
    
    public StatusPanel(){
        super();
        initUI();
    }
    
    
    public StatusPanel setTextLeft(String text){
        leftLabel.setText(text); return this;
    }
    
    public StatusPanel setTextCenter(String text){
        centerLabel.setText(text); return this;
    }
    
    public StatusPanel setTextRight(String text){
        rightLabel.setText(text); return this;
    }
    
    private void initUI(){
        //this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPane = new JPanel();
        centerPane = new JPanel();
        rightPane = new JPanel();
        
        leftPane.setLayout(new FlowLayout());
        centerPane.setLayout(new FlowLayout());
        rightPane.setLayout(new FlowLayout());
        
        leftLabel = new JLabel("Left Label");
        centerLabel = new JLabel("Center Label");
        rightLabel = new JLabel("Right Label");
        
        leftPane.add(leftLabel);
        rightPane.add(rightLabel);
        centerPane.add(centerLabel);
        
        leftPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        centerPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        rightPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        this.setLayout(new BorderLayout());
        this.add(centerPane,BorderLayout.CENTER);
        this.add(leftPane,BorderLayout.LINE_START);
        this.add(rightPane,BorderLayout.LINE_END);
        
    }
}
