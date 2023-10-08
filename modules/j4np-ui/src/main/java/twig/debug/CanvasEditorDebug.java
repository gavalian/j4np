/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.debug;

import j4np.graphics.Accordion;
import static j4np.graphics.Accordion.getDummyPanel;
import javax.swing.JFrame;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.editors.DataAttributesEditorPanel;
import twig.graphics.TGCanvas;

/**
 *
 * @author gavalian
 */
public class CanvasEditorDebug {
    public static void debugRegionEditor(){
        TGCanvas c = new TGCanvas();
        H1F h1 = TDataFactory.createH1F(25000, 80, 0., 1., 0.3, 0.05);
        H1F h2 = TDataFactory.createH1F(25000, 80, 0., 1., 0.8, 0.12);
        
        c.draw(h1).draw(h2,"same");
        
        Accordion acc = new Accordion();
        
        JFrame frame = new JFrame( "Attributes" );
        Accordion outlookBar = new Accordion();
        for(int i = 0; i < c.region().getAxisFrame().getDataNodes().size(); i++){
            
            DataAttributesEditorPanel p = 
                    new DataAttributesEditorPanel(c.view(),
                            c.region().getAxisFrame().getDataNodes().get(i));
            outlookBar.addBar( Accordion.ARROWRIGHT + " Data ("+i+")", p );
        }
        //outlookBar.addBar( Accordion.ARROWRIGHT + " Configuration", getDummyPanel( "Two" ) );
        //outlookBar.addBar( Accordion.ARROWRIGHT + " Marker Properties" , getDummyPanel( "Three" ) );
        //outlookBar.addBar( Accordion.ARROWRIGHT + " Line Properties", getDummyPanel( "Four" ) );
        //outlookBar.addBar( Accordion.ARROWRIGHT + " Fill Properties", getDummyPanel( "Five" ) );
        //outlookBar.setVisibleBar( 2 );
        frame.getContentPane().add( outlookBar );
        
        frame.setSize( 800, 600 );
        frame.pack();
        frame.setVisible(true);
    }
    
    public static void main(String[] args){
        CanvasEditorDebug.debugRegionEditor();
    }
}
