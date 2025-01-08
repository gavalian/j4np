/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.studio;

import j4np.hipo5.gui.DataSourceComponent;
import java.util.Random;
import javax.swing.JFrame;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.data.TDirectory;

/**
 *
 * @author gavalian
 */
public class StudioDebug {
    
    public static H1F create(String name){
        Random r = new Random();
        int    n = 140*(r.nextInt(100)+5);
        H1F    h = TDataFactory.createH1F(n);
        h.setName(name);
        h.attr().setTitleX("X axis");
        h.attr().setTitleY("Y axis");
        h.attr().setLineColor(r.nextInt(12)+1);
        return h;
    }
    
    public static void debug1(){
        
        StudioWindow.changeLook();
        
        StudioWindow window = new StudioWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        window.setSize(800, 500);
        window.setVisible(true);
        
        TDirectory dir = new TDirectory();
        

        dir.add("/rec/b",  StudioDebug.create("h5"));
        dir.add("/rec/b",  StudioDebug.create("h6"));
        dir.add("/rec/a",  StudioDebug.create("h4"));
        dir.add("/mc/gen/t/b", StudioDebug.create("h1"));
        dir.add("/mc/gen/t/c", StudioDebug.create("h2"));
        dir.add("/mc/gen/y/z/v", StudioDebug.create("h3"));
        
        window.getStudioFrame().setTreeProvider(dir);
    }
    
    public static void main(String[] args){
        //StudioDebug.debug1();
        StudioWindow.changeLook();
        DataSourceComponent.debug();
    }

}
