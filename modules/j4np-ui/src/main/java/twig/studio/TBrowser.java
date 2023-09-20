/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import javax.swing.JFrame;
import twig.data.TDirectory;

/**
 * @author gavalian
 */
public class TBrowser {
    StudioWindow window = null;
    public TBrowser(String filename){
        TDirectory dir = new TDirectory();
        dir.read(filename);
        StudioWindow.changeLook();
        
        window = new StudioWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setSize(1200, 900);
        window.setVisible(true); 
        window.getStudioFrame().setTreeProvider(dir);
    }
    
    public TBrowser(TreeProvider tp){
        StudioWindow.changeLook();
        
        window = new StudioWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setSize(1200, 900);
        window.setVisible(true);                       
        window.getStudioFrame().setTreeProvider(tp);
    }
    
    public void update(){
        window.sFrame.setTreeProvider(window.sFrame.treeProvider);
    }
}
