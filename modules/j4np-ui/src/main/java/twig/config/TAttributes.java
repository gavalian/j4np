/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.config;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author gavalian
 */
public class TAttributes {
    
    public static List<String> getBracketText(String text){
        Pattern pattern = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher = pattern.matcher(text);
        List<String> results = new ArrayList<>();
        while (matcher.find()){ 
            results.add(matcher.group(1)); }        
        return results;
    }
    
    public static Font getFont(String font){
        List<String> text = TAttributes.getBracketText(font);
        if(!text.isEmpty()){
            String[] tokens = text.get(0).split(",");
            int face = Font.PLAIN;
            if(tokens[1].trim().toUpperCase().compareTo("BOLD")==0) face = Font.BOLD;
            if(tokens[1].trim().toUpperCase().compareTo("ITALIC")==0) face = Font.ITALIC;
            
            Font f = new Font(tokens[0].trim(),face,Integer.parseInt(tokens[2].trim()));
            return f;
        }
        return null;
    }        
    
    public static int[] getIntArray(String str){
        List<String> text = TAttributes.getBracketText(str);
        System.out.println(" TEXT SIZE = " + text.size() + "  for " + str );
        if(!text.isEmpty()){

            String[] tokens = text.get(0).split(",");            
            int[] result = new int[tokens.length];
            for(int i = 0; i < result.length; i++){
                result[i] = Integer.parseInt(tokens[i].trim());
            }
            return result;
        }
        return null;
    }
    
    public static void main(String[] args){
        Font f = TAttributes.getFont("font=[Avenir Next,PLAIN,20]");
        System.out.println(f);

        int[] margins = TAttributes.getIntArray("margins=[2,5,6,7]");
        System.out.println(Arrays.toString(margins));
    }
}
