/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.config;

/**
 *
 * @author gavalian
 */
public class TStyle {
    
    protected TPalette palette = new TPalette();
    protected static TStyle globalStyle = new TStyle();
    protected TAxisAttributes defaultAxisAttributes = new TAxisAttributes();
    
    public TStyle(){
        
    }
    
    public static TStyle getInstance(){ return globalStyle;}
    
    public TPalette getPalette(){ return palette; }
}
