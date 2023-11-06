/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

/**
 *
 * @author gavalian
 */
public class StudioTools {
    
    public static JMenu createMenu(String menuName, ActionListener al, String[] names, String[] actions){
        JMenu menu = new JMenu(menuName);
        for(int i = 0; i < names.length; i++){
            if(names[i].startsWith("----")==false){
                JMenuItem mi = new JMenuItem(names[i]);
                mi.setActionCommand(actions[i]);
                if(al!=null) mi.addActionListener(al);
                menu.add(mi);
            } else {
                menu.addSeparator();
            }
        }
        return menu;
    }
    
    public static JPanel createActionBar(String[] icons, String[] actions){
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        return panel;
    }
}
