/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 *
 * @author gavalian
 */
public class StudioComponent extends JPanel {
    
    List<ActionListener>  listeners = new ArrayList<>();
    public StudioComponent(){}
    public void addActionListener(ActionListener al){ this.listeners.add(al);}
    public void fireEvent(ActionEvent event){
        for(ActionListener l : listeners) l.actionPerformed(event);
    }
}
