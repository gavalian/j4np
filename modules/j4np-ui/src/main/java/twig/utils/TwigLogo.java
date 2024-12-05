/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.utils;

/**
 *
 * @author gavalian
 */
public class TwigLogo {
    String[] twigLogoAscii = new String[] {
        "┌┬┐┬ ┬┬┌─┐",
        " │ │││││ ┬",
        " ┴ └┴┘┴└─┘"
    };
    
    String[] twigLogoAsciiItalic = new String[] {
        "_/   '_ ",
        "/((//(/ ",
        "    _/  ",
    };
    
    public static String[] twigScreen = new String[]{
        "  +------------------------------------------------------------+",
        "  | ┌┬┐┬ ┬┬┌─┐  Wecome to twig (Java)                          |",
        "  |  │ │││││ ┬   Data Visualization and Analysis Package       |",
        "  |  ┴ └┴┘┴└─┘    Author: Gagik Gavalian (2019-2024)           |",
        "  |                Version 1.1.0                               |",
        "  +------------------------------------------------------------+"
    };       
    
    public static void printTwigScreen(){
        for(String str : twigScreen){
            System.out.println(str);
        }
    }
}
