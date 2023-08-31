/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.config;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class TPalette {
    
    private List<Color> colorPalette3D = new ArrayList<Color>();
    private List<Color> colorPalette   = new ArrayList<Color>();
    private TPalette2D  colorPalette2D = new TPalette2D();
    /**
     * Specialty color palette is used for colors of data regions and 
     * axis colors.
     */
    private List<Color> specialtyColorPalette = new ArrayList<>();
        double[] red   = {0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.,0.,0.17,0.33,0.50,0.67,0.83,1.00,1.00,
                             1.,1.,1.,1.,1.,1.,1.,1.,1.,1.,
                             1.,1.,1.,1.,1.,1.,1.};
    double[] green = {0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.08,0.15,0.23,0.31,0.38,0.46,0.53,0.59,0.66,
                             0.73,0.80,0.87,0.72,0.58,0.43,0.29,0.14,0.00,0.08,
                             0.17,0.25,0.33,0.42,0.50,0.58,0.67,0.75,0.83,0.92,
                             1.,1.,1.,1.,1.,1.,1.};
    double[] blue  = {0.30,0.33,0.36,0.39,0.42,0.45,0.48,0.52,0.56,0.60,
                             0.64,0.68,0.68,0.70,0.70,0.70,0.70,0.64,0.56,0.48,
                             0.40,0.33,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,
                             0.,0.17,0.33,0.50,0.67,0.83,1.};
    
    
    
    public TPalette(){
        //init("gold10");
        init("tab10");
        this.initSpacialtyPalette();
    }
    
    public final void init(String type){
        if(type.contains("default")==true){
            colorPalette.clear();
            colorPalette3D.clear();
            for(int loop = 0; loop < red.length; loop++){
                int pred   = (int) (255.0*red[loop]);
                int pgreen = (int) (255.0*green[loop]);
                int pblue  = (int) (255.0*blue[loop]);
                colorPalette3D.add(new Color(pred,pgreen,pblue));
            }
            colorPalette.add(Color.WHITE);
            colorPalette.add(Color.BLACK);            
            colorPalette.add(new Color(246,67,55));
            colorPalette.add(new Color(73,175,87));
            colorPalette.add(new Color(60,82,178));
            colorPalette.add(new Color(255,234,79));
            colorPalette.add(new Color(157,42,173));
            colorPalette.add(new Color(0,188,211));
            colorPalette.add(new Color(138,194,84));
            colorPalette.add(new Color(102,60,180));            
        }
        this.setColorScheme(type);
    }
      /**
     * returns color from color pallette. There are some reserved colors.
     * colors above 30000 are used for the data regions and axis coloring.
     * This is mainly used to change the style of the colors for different
     * environments.
     * 30001 - color for axis drawn
     * 30002 - color for axis labels and titles
     * 30003 - color for data region fill (this is the inside of axis)
     * 30004 - color for DataCanvas drawing.
     * 
     * @param color
     * @return 
     */
    protected final void initSpacialtyPalette(){
        specialtyColorPalette.clear();
        specialtyColorPalette.add(new Color(0,0,0));
        specialtyColorPalette.add(new Color(0,0,0));
        specialtyColorPalette.add(new Color(255,255,255));
        specialtyColorPalette.add(new Color(255,255,255));        
    }
    
    protected void setSpacialtyPalette(List<Color> colors){
        specialtyColorPalette.clear();
        specialtyColorPalette.addAll(colors);
    }

    public void setSpecialtyColorScheme(String scheme){
        if(scheme.compareTo("SolarizedLight")==0){
            List<Color> colors = Arrays.asList(
                    new Color(90,90,90), new Color(90,90,90),
                    new Color(238,232,212),new Color(238,232,212));
            this.setSpacialtyPalette(colors);
        } 
        if(scheme.compareTo("DefaultLight")==0){
            List<Color> colors = Arrays.asList(
                    new Color(0,0,0), new Color(0,0,0),
                    new Color(255,255,255),new Color(255,255,255));
            this.setSpacialtyPalette(colors);
        } 
        if(scheme.compareTo("SolarizedDark")==0){
            List<Color> colors = Arrays.asList(
                    new Color(200,200,200), new Color(200,200,200),
                    new Color(14,60,74),new Color(14,60,74)); 

            this.setSpacialtyPalette(colors);
        }        
    }
    
    public void setColorScheme(String scheme){
        
        if(scheme.compareTo("set1")==0){
            colorPalette.clear();
            colorPalette.add(Color.WHITE);
            colorPalette.add(Color.BLACK);
            colorPalette.add(new Color( 56,126,184)); // BLUE - 2
            colorPalette.add(new Color(227,25,28)); // ORANGE - 3
            colorPalette.add(new Color(79,175,74));  // GREEN - 4
            colorPalette.add(new Color(153,78,163));  // RED - 5
            colorPalette.add(new Color(255,255,50));  // PURPLE - 6
            colorPalette.add(new Color(166,86,40));  // BROWN - 7
            colorPalette.add(new Color(248,129,191));  // PINK - 8
            colorPalette.add(new Color(  27,190,207));  // CYAN  - 9            
            colorPalette.add(new Color(189,189,33));  // OLIVE - 10
        }
        if(scheme.compareTo("tab10")==0){
            colorPalette.clear();
            colorPalette.add(Color.WHITE);
            colorPalette.add(Color.BLACK);
            colorPalette.add(new Color( 20,120,178)); // BLUE - 2
            colorPalette.add(new Color(255,127, 30)); // ORANGE - 3
            colorPalette.add(new Color( 39,160, 56));  // GREEN - 4
            colorPalette.add(new Color(216, 39, 40));  // RED - 5
            colorPalette.add(new Color(148,104,186));  // PURPLE - 6
            colorPalette.add(new Color(141, 86, 76));  // BROWN - 7
            colorPalette.add(new Color(227,119,190));  // PINK - 8

            colorPalette.add(new Color(  0,188,203));  // CYAN  - 9
            colorPalette.add(new Color(186,185, 53));  // OLIVE - 10
            //colorPalette.add(new Color(127,127,127));  // GRAY - 11
            //colorPalette.add(new Color(  168,182,196));  // GRAY - 12
            //colorPalette.add(new Color());
        }
        
        if(scheme.compareTo("gold10")==0){
            colorPalette.clear();
            colorPalette.add(Color.WHITE); // white - 0
            colorPalette.add(Color.BLACK); // black - 1
            colorPalette.add(new Color( 37,75,135)); // NAVY - 2
            colorPalette.add(new Color( 37,118,117)); // TEAL - 3
            colorPalette.add(new Color( 255,195,114)); // GOLD 70 - 4
            colorPalette.add(new Color( 220,115,28)); // DARK GOLD - 5
            colorPalette.add(new Color( 209,65,36)); // Red - moved from 11 to 6 
            //colorPalette.add(new Color( 186,164,150)); // Neutral60 - 6
            
            colorPalette.add(new Color( 116,87,69)); // Dark Neutral - 7
            colorPalette.add(new Color( 220,156,191)); // Purple 50 - 8
            colorPalette.add(new Color( 160,27,104)); // Dark Purple - 9
            colorPalette.add(new Color( 231,158,142)); // Red 60 - 10
            //colorPalette.add(new Color( 209,65,36)); // Red - 11 
            //colorPalette.add(new Color( 186,164,150)); // Neutral60 - 6
            //colorPalette.add(new Color( 50,50,50)); // 12 - gray
        }
        
        if(scheme.compareTo("bright10")==0){
            colorPalette.clear();
            colorPalette.add(Color.WHITE); // white - 0
            colorPalette.add(Color.BLACK); // black - 1
            colorPalette.add(new Color( 65,155,249)); // BLUE - 2
            colorPalette.add(new Color( 246,152,51)); // ORANGE - 3
            colorPalette.add(new Color( 200,207,45)); // GREEN - 4
            colorPalette.add(new Color( 238,103,35)); // RED - 5
            colorPalette.add(new Color( 254,207,51)); // YELLOW - 6
            colorPalette.add(new Color( 13,207,218)); // CYAN - 7
            colorPalette.add(new Color( 227,119,190)); // PINK 50 - 8
            colorPalette.add(new Color( 253,189,57)); // ORANGE - 9
            colorPalette.add(new Color( 247,130,243)); //  - 10
        }
    }

    public TPalette2D palette2d(){ return this.colorPalette2D;}
    
    public static int rgbToInt(int r, int g, int b){
        int value = (r&0xFF)<<16|(g&0xFF)<<8|(b&0xFF);
        return value;
    }
    
    
    public static int colorToInt(Color r){
        int value = (r.getAlpha()&0xFF)<<24|(r.getRed()&0xFF)<<16
                |(r.getGreen()&0xFF)<<8|(r.getBlue()&0xFF);
        return value;
    }
    
    public static int createColor(int r, int g, int b){
        int word = b; int a = 255;
        word = (word|((g<<8)&0x0000FF00));
        word = (word|((r<<16)&0x00FF0000));
        word = (word|((a<<24)&0xFF000000));
       return word;
    }
    
    public static int createColor(int r, int g, int b, int a){
        int word = b;
        int alpha = a/2;        
        word = (word|((g<<8)&0x0000FF00));
        word = (word|((r<<16)&0x00FF0000));        
        word = (word|((alpha<<24)&0xFF000000));
       return word;
    }
    /**
     * returns color from color pallette. There are some reserved colors.
     * colors above 30000 are used for the data regions and axis coloring.
     * This is mainly used to change the style of the colors for different
     * environments.
     * 30001 - color for axis drawn
     * 30002 - color for axis labels and titles
     * 30003 - color for data region fill (this is the inside of axis)
     * 30004 - color for DataCanvas drawing.
     * 
     * @param color
     * @return 
     */
    
    public Color getColor(int color){
        
        /*if(color>30004) return Color.black; 
        if(color>30000){
            return this.specialtyColorPalette.get(color-30001);
        }*/
        
        int cid = color;        
        //if(color<0) cid = Math.abs(color);

        if(Math.abs(cid)>=200){
            //System.out.println(" decoding color " + cid);
            int a = (cid>>24)&0x7F;
            
            int r = (cid>>16)&0xFF;
            int g = (cid>>8)&0xFF;
            int b = (cid>>0)&0xFF;
            //System.out.println(" r = " + r + "g = "+ g + " b = " + b + " a = " + a);
           return new Color(r,g,b,a*2);
        }
        
        if(color<colorPalette.size()){
            return this.colorPalette.get(color);
        }
        
        if(color>=20&&color<99){
            double fraction = 0.9-(color-20)/100.0;
            int    index    = (color-20)%10;
            /*System.out.printf(" color = %4d, index = %4d, fraction = %.4f\n",
                    color,index,fraction);*/
            Color  lighter = TPalette.lighter(colorPalette.get(index), (float) fraction);
            return lighter;
            //int lookup = ;
            //System.out.println("color = " + color + "  look up = " + lookup);
            //return TPalette.getLighter(colorPalette.get(index), fraction);                        
        }
        
        if(color>=100&&color<199){
            double fraction = 0.9-(color-100)/100.0;
            int       index = (color-120)%10;
            /*System.out.printf(" color = %4d, index = %4d, fraction = %.4f\n",
                    color,index,fraction);*/
            return TPalette.getTanslucent(colorPalette.get(index), fraction);                        
        }
        
        int alfa = 255 - 25*(color/10);
        int base = color%10;
        Color col = colorPalette.get(base);
        return new Color(col.getRed(),col.getGreen(),col.getBlue(),alfa);
    }

    
    public static Color getLighter(Color col, double fraction){
        return new Color((int) (col.getRed()*fraction),
                (int) (col.getGreen()*fraction),
                (int) (col.getBlue()*fraction)
        );
    }
    
    public static Color getTanslucent(Color col, double fraction){
        return new Color((int) (col.getRed()),
                (int) (col.getGreen()),
                (int) (col.getBlue()), (int) (255*fraction)
        );
    }
    
    /**
     * Make a color lighter.
     * 
     * @param color
     *          Color to mix with white.
     * @param ratio
     *          White ratio (1.0 = complete white, 0.0 = color).
     * @return Lighter color.
     */
    public static Color lighter(Color color, float ratio) {
        return mergeColors(Color.WHITE, ratio, color, 1 - ratio);
    }
    
    /**
     * Merges two colors. The two floating point arguments specify "how much" of the corresponding color is added to the
     * resulting color. Both arguments should (but don't have to) add to <code>1.0</code>.
     * <p>
     * This method is null-safe. If one of the given colors is <code>null</code>, the other color is returned (unchanged).
     */
    public static Color mergeColors(Color a, float fa, Color b, float fb) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return new Color((fa * a.getRed() + fb * b.getRed()) / (fa + fb) / 255f,
                (fa * a.getGreen() + fb * b.getGreen()) / (fa + fb) / 255f,
                (fa * a.getBlue() + fb * b.getBlue()) / (fa + fb) / 255f);
    }
}
