/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import javax.swing.JFrame;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class TBrowser {
    public TBrowser(String filename){
        TDirectory dir = new TDirectory();
        dir.read(filename);
        StudioWindow.changeLook();
        
        StudioWindow window = new StudioWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setSize(800, 500);
        window.setVisible(true);                       
        window.getStudioFrame().setTreeProvider(dir);
    }
}
