/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.evio;

/**
 *
 * @author gavalian
 */
public class EvioDataUtils {
    public static int decodeTag(int desc){ return (desc>>16)&0x0000FFFF;}
    public static int decodeNum(int desc){ return (desc)&0x000000FF;}
    public static int decodeType(int desc){ return (desc>>8)&0x000000AF;}
}
