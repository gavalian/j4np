/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.widgets;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author gavalian
 */
public class WidgetEditor implements ActionListener {
    List<Widget> widgets = null;
    JComponent parent = null;
    JList list = null;
    public WidgetEditor(List<Widget> w, JComponent p){
        widgets = w; parent = p;
    }
    
    public void show(){
        
        String[] objects = new String[widgets.size()];
        for(int i = 0; i < objects.length; i++) objects[i] = widgets.get(i).getClass().getName();
        list = new JList(objects);
        list.setMinimumSize(new Dimension(300,150));
        list.setPreferredSize(new Dimension(300,150));
        
        JButton button = new JButton("Edit");
        button.addActionListener(this);
        
        Object[] message = {
          "Widgets",list ,
           // "", button
        };
        
        int option = JOptionPane.showConfirmDialog(null,                 
                message, "Pave Text", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
             int index = list.getSelectedIndex();
            if(index>=0&&index<widgets.size())
                this.widgets.get(index).configure(parent);
        } else {
            System.out.println("Login canceled");
        }        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().compareTo("Edit")==0){
            int index = list.getSelectedIndex();
            if(index>=0&&index<widgets.size())
                this.widgets.get(index).configure(parent);
        }
    }
    
    public static void main(String[] args){
        List<Widget> list = Arrays.asList(
                new PaveText("text 1",0.5,0.2), 
                new PaveText("text 2",0.4,0.5),
                new PaveText("text 3", 0.6, 0.9)
                );
        
        WidgetEditor ed = new WidgetEditor(list,null);
        ed.show();
    }
}
