/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.config;

import java.awt.Font;

/**
 *
 * @author gavalian
 */
public class TAttributesFont {

    public TAttributesFont(String name, int style, int size){
        font = new Font(name,style,size);
    }
    
    public TAttributesFont(TAttributesFont attrf){
        font = attrf.font;
    }
        
    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
    
    private Font font = new Font("Helvetica",Font.PLAIN,12);
    
}
