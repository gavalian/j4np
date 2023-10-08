/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.config;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TAttributeUtils {
    
    public static List<ValuePair> parseOptions(String optionsString){
        List<ValuePair> vp = new ArrayList<>();
        String[] tokens = optionsString.split(",");
        for(int i = 0; i < tokens.length; i++){
            String[] pair = tokens[i].trim().split("=");
            if(pair.length==2){
                vp.add(new ValuePair(pair[0].trim(),pair[1].trim()));
            } else {
                System.out.printf("TAttributeUtils:: error parsing the option [%s]\n",
                        tokens[i]);
            }
        }
        return vp;
    }
    public static class ValuePair {
        public String option = "";
        public String  value = "";
        public ValuePair(String opt, String vl){
            option = opt; value = vl;
        }
    }
}
