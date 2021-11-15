/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.graphics;

import twig.studio.StudioWindow;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;

/**
 *
 * @author gavalian
 */
public class ResourceManager {
    
    private Map<String,ImageIcon> icons = new HashMap<>();
    
    public ResourceManager(){
        
    }
    
    public ImageIcon getIcon(String name){
        return icons.get(name);
    }
    
    public ImageIcon getIcon(String name, int x, int y){
        ImageIcon icon = getIcon(name);
        Image image = icon.getImage();
        Image imageScaled = image.getScaledInstance(x, y,  java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(imageScaled);
    }
    
    public void load(String key,String iconName){
        URL   imageURL = ResourceManager.class.getResource(iconName);
        ImageIcon icon = null;
        try{
            icon = new ImageIcon(imageURL);
        } catch (Exception e){
            System.out.printf("[resources] : error loading icon : %s\n",iconName);
        }
        if(icon!=null){
         System.out.printf("[resources] : successfully loaded icon : %s\n",iconName);
         icons.put(key, icon);
        } 
        
    }
}
