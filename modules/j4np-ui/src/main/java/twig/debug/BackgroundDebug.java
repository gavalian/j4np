/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import j4np.graphics.Background2D;
import j4np.graphics.Node2D;
import java.awt.Color;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.graphics.TGRegion;

/**
 *
 * @author gavalian
 */
public class BackgroundDebug {
    public static void canvasBackground(){
        TGCanvas c = new TGCanvas("sample", 500,500);       
        //back.setColor(null);
        //c.view().setBackground(Background2D.createBackground(255,0, 0));
        c.view().setBackground2D(null);
        
        TGRegion pad = new TGRegion(false);
        
        c.view().addNode(pad);        
        c.view().getGraphicsComponents().get(0).setBoundsBind(0.4, 0.4, 0.3, 0.2);
        c.view().getGraphicsComponents().get(0).alignMode(Node2D.ALIGN_RELATIVE);
        
        
        c.view().set("bc=#232357");
        H1F h = TDataFactory.createH1F(1200);
        
        //pad.set("fc=#DEEEEE");
        pad.set("ac=#0000FF");
        pad.draw(h);
    }
    
    public static void main(String[] args){
        BackgroundDebug.canvasBackground();
    }
}
