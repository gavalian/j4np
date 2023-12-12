/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.config;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gavalian
 */
public class TwigFontLoader {
    
    private Map<Integer,Font>     fontMap = new HashMap<>();
    private Map<Integer,String> fontFiles = new HashMap<>();
    public static TwigFontLoader twigFontLoader = new TwigFontLoader();
    
    
    public TwigFontLoader(){
        fontFiles.put(   1, "fonts/dejavu/DejaVuSans.ttf");
        fontFiles.put( 101, "fonts/dejavu/DejaVuSans-Oblique.ttf");
        fontFiles.put( 201, "fonts/dejavu/DejaVuSans-Bold.ttf");
        fontFiles.put( 301, "fonts/dejavu/DejaVuSans-BoldOblique.ttf");
        
        fontFiles.put(   2, "fonts/dejavu/DejaVuSerif.ttf");
        fontFiles.put( 102, "fonts/dejavu/DejaVuSerif-Italic.ttf");
        fontFiles.put( 202, "fonts/dejavu/DejaVuSerif-Bold.ttf");
        fontFiles.put( 302, "fonts/dejavu/DejaVuSerif-BoldItalic.ttf");
        
        /*fontFiles.put(   3, "fonts/PT-Serif/pt-serif_regular.ttf");
        fontFiles.put( 103, "fonts/PT-Serif/pt-serif_italic.ttf");
        fontFiles.put( 203, "fonts/PT-Serif/pt-serif_bold.ttf");
        fontFiles.put( 303, "fonts/PT-Serif/pt-serif_bold-italic.ttf");
        
        fontFiles.put(   4, "fonts/crimson-times/Crimson-Roman.ttf");
        fontFiles.put( 104, "fonts/crimson-times/Crimson-Italic.ttf");
        fontFiles.put( 204, "fonts/crimson-times/Crimson-Bold.ttf");
        fontFiles.put( 304, "fonts/crimson-times/Crimson-BoldItalic.ttf");
        */
        this.loadFonts();
    }
    
    public static TwigFontLoader getInstance(){ return twigFontLoader;}
    
    public final void loadFonts(){
        for(Map.Entry<Integer,String>  entry : fontFiles.entrySet()){
            //System.out.println("[fonts] -> loading : " + entry.getValue());
            Font f = this.getFont(entry.getValue());
            fontMap.put(entry.getKey(), f);
        }
        System.out.println("[fonts] loaded " + fontFiles.size() + " fonts");
    }
    
    public void register(){
        GraphicsEnvironment ge = 
         GraphicsEnvironment.getLocalGraphicsEnvironment();
        for(Map.Entry<Integer,Font>  entry : fontMap.entrySet()){
            ge.registerFont(entry.getValue());
        }
    }
    public Font  getFont(int font, int size){
        if(fontMap.containsKey(font)==true){
            
            //return fontMap.get(font).deriveFont(Font.PLAIN,size);
            int fontStyle = font/100;
            Font f = fontMap.get(font);
            System.out.println(f);
            System.out.println("Style = " + f.getStyle());
            
            switch(fontStyle){
                case 0 : return fontMap.get(font).deriveFont(Font.PLAIN,size);
                case 1 : return fontMap.get(font).deriveFont(Font.ITALIC,size); 
                case 2 : return fontMap.get(font).deriveFont(Font.BOLD,size); 
                case 3 : return fontMap.get(font).deriveFont(Font.BOLD|Font.ITALIC,size);
            }            
        }
        return null;
    }
    
    public Font getFontFromFile(int font, int style, int size){
        Font f = this.getFont(fontFiles.get(font));
        Font fn = f.deriveFont(style, size); return fn;
    }
    
    public List<String> getFontList(){
        List<String> fonts = new ArrayList<>();//String[fontMap.size()];
        Set<Integer> keys = fontMap.keySet();
        for(Integer key : keys) fonts.add(key.toString());
        return fonts;
    }
    
    public String[] getFontArray(){
        List<String> list = this.getFontList();
        String[] fonts = new String[list.size()];
        Collections.sort(list);
        for(int k = 0; k < list.size(); k++){ fonts[k] = list.get(k);}
        return fonts;
    }
    
    public final Font getFont(String resource){
        try {
            InputStream is = TwigFontLoader.class.getResourceAsStream(resource);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is); 
            return font;
        } catch (FontFormatException | IOException ex) {
            System.out.println("ERROR: can't load font file : " + resource);
            //Logger.getLogger(TwigFontLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
