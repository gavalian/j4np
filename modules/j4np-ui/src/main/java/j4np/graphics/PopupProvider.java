/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author gavalian
 */
public class PopupProvider {
    
    private List<ActionListener> menuActionListeners = new ArrayList<>();
    public void addActionListener(ActionListener al){menuActionListeners.add(al);}
    
    public PopupProvider(){
        
    }
    
    public JPopupMenu createMenu(Node2D node){
        JPopupMenu menu = new JPopupMenu();
        JMenuItem itemRegion = new JMenuItem("Edit Region");
        JMenuItem itemInsets = new JMenuItem("Edit Insets");
        
        return menu;
    }
}
