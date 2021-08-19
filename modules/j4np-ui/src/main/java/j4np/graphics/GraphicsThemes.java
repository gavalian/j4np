/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author gavalian
 */
public class GraphicsThemes {
    
    public static void setTheme(String name){
        UIManager.put( "TabbedPane.showTabSeparators", true );
        if(name.compareTo("Flat Look Ligth")==0){
            try {
                UIManager.setLookAndFeel( new FlatLightLaf() );
                //SwingUtilities.updateComponentTreeUI(this);
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
        }
        
        if(name.compareTo("Solarized Ligth")==0){
            System.out.println("change to solarized");
            try {
                UIManager.setLookAndFeel( new FlatSolarizedLightIJTheme() );
                
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
        }
        
        if(name.compareTo("Solarized Dark")==0){
            System.out.println("change to solarized");
            try {
                UIManager.setLookAndFeel( new FlatSolarizedDarkIJTheme() );               
            } catch( Exception ex ) {
                System.err.println( "Failed to initialize LaF" );
            }
        }
    }
}
