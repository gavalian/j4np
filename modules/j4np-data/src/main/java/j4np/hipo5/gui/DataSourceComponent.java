/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.hipo5.gui;

import j4np.data.base.DataActor;
import j4np.data.base.DataSource;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author gavalian
 */
public class DataSourceComponent extends JPanel implements ActionListener {
    
    private List<DataActor> dataActors = new ArrayList<>();
    private DataSource      dataSource = null;
    
    private JButton[]       buttons = null;
    private JLabel          statusLabel = null;
    private JPanel          mainPane = null;
    
    
    private String[] unicodeMedia = new String[]{"\u23F9","\u23F4","\u23F5","\u23E9","\u23F8"};
    public DataSourceComponent(){
        super();
        this.initUI();
    }
    
    public DataSourceComponent addActor(DataActor... actors){
        for(int i = 0 ; i < actors.length; i++) dataActors.add(actors[i]);
        return this;
    }

    public DataSourceComponent setDataSource(DataSource src){
        this.dataSource = src; return this;
    }
            
    private void initUI(){
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPane = new JPanel();
        mainPane.setLayout(new BorderLayout());
        mainPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        JPanel btnPane = new JPanel();        
        btnPane.setLayout(new FlowLayout());
        btnPane.setBorder(BorderFactory.createEtchedBorder());
        buttons = new JButton[unicodeMedia.length];
        for(int i = 0; i < buttons.length; i++){
            buttons[i] = new JButton(unicodeMedia[i]);
            buttons[i].addActionListener(this);
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 25));
            buttons[i].setPreferredSize(new Dimension(30, 30));
            btnPane.add(buttons[i]);            
        }
        
        JPanel statusPane = new JPanel();        
        //statusPane.setLayout(new FlowLayout());
        statusLabel = new JLabel("Status: Idle");
        statusPane.add(statusLabel);
        statusPane.setBorder(BorderFactory.createEtchedBorder());
        JPanel controlsPane = new JPanel(); 
                
        controlsPane.setLayout(new FlowLayout());
        JButton openBtn = new JButton("Open");
        openBtn.setText("\u23CF");
        openBtn.setFont(new Font("Arial", Font.PLAIN, 25));
        openBtn.setPreferredSize(new Dimension(30, 30));
        openBtn.addActionListener(this);
        controlsPane.add(openBtn);
        controlsPane.setBorder(BorderFactory.createEtchedBorder());
        mainPane.add(controlsPane,BorderLayout.LINE_END);
        mainPane.add(statusPane,BorderLayout.CENTER);
        mainPane.add(btnPane, BorderLayout.LINE_START);
        
        this.add(mainPane, BorderLayout.CENTER);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(" Wll something was clicked : " + e.getActionCommand());
    }
    
    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DataSourceComponent view = new DataSourceComponent();
        
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(new JLabel("Center"),BorderLayout.CENTER);
        p.add(view,BorderLayout.PAGE_END);
        frame.add(p);
        frame.pack();
        frame.setSize(800, 500);
        frame.setVisible(true);
    }
    
}
