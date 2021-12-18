/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.tree;

import twig.data.H1F;

/**
 *
 * @author gavalian
 */
public class TreeDebug {
    public static void main(String[] args){
        HipoTree t = new HipoTree();
        String exp = "hx+mean(a,b)+cos(theta)>>data123(120,0.0,1.0)";
        H1F f = t.getByStringH1F("hx+mean(a,b)+cos(theta)>>data123(120,0.0,1.0)");
        System.out.println("name = " + t.getNameByExperession(exp));
        System.out.println(f);
        //t.draw("hx>>data(120,0.0,1.0)", "", "");
    }
}
