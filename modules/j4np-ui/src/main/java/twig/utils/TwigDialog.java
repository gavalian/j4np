/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 *
 * @author gavalian
 */
public class TwigDialog extends JDialog implements ActionListener {
    
    private String[] data;
    private JTextField descBox;
    private JComboBox<String> colorList;
    
    private JPanel  buttonPanel;
    private JButton btnOk;
    private JButton btnCancel;
   
    public TwigDialog(Frame parent, JPanel content){    
        super(parent,"List Selection",true);
        if(parent!=null){
            Point loc = parent.getLocation();
            setLocation(loc.x+80,loc.y+80);        
        }
        this.setLayout(new BorderLayout());
        /*
        data = new String[2]; // set to amount of data items
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        JLabel descLabel = new JLabel("Description:");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(descLabel,gbc);
        descBox = new JTextField(30);
        gbc.gridwidth = 2;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(descBox,gbc);
        JLabel colorLabel = new JLabel("Choose color:");
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(colorLabel,gbc);
        String[] colorStrings = {"red","yellow","orange","green","blue"};
        colorList = new JComboBox<String>(colorStrings);
        gbc.gridwidth = 1;
        gbc.gridx = 1;
      gbc.gridy = 1;
      panel.add(colorList,gbc);
      JLabel spacer = new JLabel(" ");
      gbc.gridx = 0;
      gbc.gridy = 2;
      panel.add(spacer,gbc);
      btnOk = new JButton("Ok");
      btnOk.addActionListener(this);
      gbc.gridwidth = 1;
      gbc.gridx = 0;
      gbc.gridy = 3;
      panel.add(btnOk,gbc);
      btnCancel = new JButton("Cancel");
      btnCancel.addActionListener(this);
      gbc.gridx = 1;
      gbc.gridy = 3;
      panel.add(btnCancel,gbc);*/
       this.initPanel();
        
      getContentPane().add(content);
      pack();
    }

    public static JPanel demoPane(){
        JPanel panel = new JPanel(new SpringLayout());
        for (int i = 0; i < 9; i++) {
            JTextField textField = new JTextField(Integer.toString(i));
            
            panel.add(textField);
        }

        SpringUtilities.makeGrid(panel,
                         3, 3, //rows, cols
                         5, 5, //initialX, initialY
                         5, 5);//xPad, yPad
        return panel;
    }
    private void initPanel(){
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        btnOk = new JButton("OK");
        btnOk.addActionListener(this);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        buttonPanel.add(btnOk);
        buttonPanel.add(btnCancel);
        add(buttonPanel,BorderLayout.PAGE_END);
    }
    public void showDialog(){
        this.setVisible(true);
        System.out.println("finished....");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnOk) {
            //data[0] = descBox.getText();
            //data[1] = (String)colorList.getSelectedItem();
        }
        else {
            //data[0] = null;
        }
        dispose();
    }
        
    public static JButton createButton(String label, String action, ActionListener al){
        JButton jb = new JButton(label);
        jb.setActionCommand(action);
        jb.addActionListener(al);
        return jb;
    }
    
    public static void main(String[] args){
        JFrame f = new JFrame();
        f.setVisible(true);
        JPanel p = TwigDialog.demoPane();
        TwigDialog pane = new TwigDialog(f,p);
        pane.showDialog();
    }
}
