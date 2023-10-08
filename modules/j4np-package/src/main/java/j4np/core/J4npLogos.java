/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.core;

/**
 *
 * @author gavalian
 */
public class J4npLogos {
    public static String[] LogoTree = new String[]{
"              * *    ",
"           *    *  *",
"      *  *    *     *  *",
"     *     *    *  *    *",
" * *   *    *    *    *   *",
" *     *  *    * * .#  *   *",
" *   *     * #.  .# *   *",
"  *     \"#.  #: #\" * *    *",
" *   * * \"#. ##\"       *",
"   *       \"###",
"             \"##",
"              ##.",
"              .##:",
"              :###",
"              ;###",
"            ,####.",
"/\\/\\/\\/\\/\\/.######.\\/\\/\\/\\/\\        ",
    };
    
    public static String[] LogoTreeDense = new String[]{
"              * *    ",
"           * ** * **",
"      *  * ** * *** *  *",
"     * *** * ** *  * ** *",
" * *   * ** * ** * ** * * *",
" * *** *  * ** * * .#  * * *",
" *   * *** * #.  .# * * *",
"  * *** \"#.  #: #\" * * ** *",
" *   * * \"#. ##\"   *** *",
"   *       \"###",
"             \"##",
"              ##.",
"              .##:",
"              :###",
"              ;###",
"            ,####.",
"/\\/\\/\\/\\/\\/.######.\\/\\/\\/\\/\\        ",
    };
    
    public static void showTree(){
        for(int i=0;i<J4npLogos.LogoTree.length;i++){
            String st = J4npLogos.LogoTree[i];
            int len = st.length();
            System.out.print("   ");
            for(int c = 0; c < len; c++){
                char ch = st.charAt(c);
                if(ch=='*') System.out.print("\033[32m"+ch+"\033[0m");
                else if(ch=='#') System.out.print("\033[33m"+ch+"\033[0m");
                else System.out.print(ch);
            }
            System.out.println();
        }
    }
    
    public static void showTreeDense(){
        for(int i=0;i<J4npLogos.LogoTreeDense.length;i++){
            String st = J4npLogos.LogoTreeDense[i];
            int len = st.length();
            System.out.print("   ");
            for(int c = 0; c < len; c++){
                char ch = st.charAt(c);
                if(ch=='*') System.out.print("\033[32m"+ch+"\033[0m");
                else if(ch=='#') System.out.print("\033[33m"+ch+"\033[0m");
                else System.out.print(ch);
            }
            System.out.println();
        }
    }
}
