/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.ccdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author gavalian
 */
public class DetectorTools {
    
    public static long hardwareEncoder(long crate, int slot, int channel){
        long word = 0;
        word = (crate<<38)|(slot<<24)|(channel);
        return word;
    }
    
    public static void hardwareDecoder(long value, int[] address){
        address[0] = ( (int) (value>>38))&0x0000FFFF;
        address[1] = ( (int) (value>>24))&0x00003FFF;
        address[2] = ( (int) (value))&0x0000FFFF;
    }
    
    public static void softwareDecoder(long value, int[] address){
        address[0] = ( (int) (value>>56))&0x000000FF;
        address[1] = ( (int) (value>>48))&0x000000FF;
        address[2] = ( (int) (value>>32))&0x0000FFFF;
        address[3] = ( (int) (value>> 8))&0x000FFFFF;
        address[4] = ( (int) (value)    )&0x000000FF;
    }
    /**
     * bits 64-56 - detector type
     * bits 56-48 - sector
     * @param type
     * @param sector
     * @param layer
     * @param component
     * @param order
     * @return 
     */
    public static long softwareEncoder(long type, long sector, long layer, int component, int order){
        long word = 0L;
        word = (type<<56) | ( sector<<48)|(layer<<32)|(component<<8)|(order);
        return word;
    }
    
    public static long fadcEncoder(long type, long ped, long nsa, int nsb, int tet){
        long word = 0L;
        word = (type<<56) | ( ped<<40)|(nsa<<32)|(nsb<<16)|(tet);
        return word;
    }
    
    public static void fadcDecoder(long value, int[] address){
        address[0] = ( (int) (value>>56))&0x000000FF;
        address[1] = ( (int) (value>>40))&0x0000FFFF;
        address[2] = ( (int) (value>>32))&0x000000FF;
        address[3] = ( (int) (value>>16))&0x000000FF;
        address[4] = ( (int) (value)    )&0x0000FFFF;
    }
    
    public static List<Integer> getCreates(Map<Long,Long> map){
       Set<Integer> cset = new HashSet<>();
       int[] address = new int[3];
       for(Map.Entry<Long,Long> entry : map.entrySet()){
           DetectorTools.hardwareDecoder(entry.getKey(), address);
           cset.add(address[0]);
       }
       List<Integer> list = new ArrayList<>();
       for(Integer item : cset) list.add(item);
       Collections.sort(list);
       return list;
    }
    
    public static void main(String[] args){
        long value = DetectorTools.hardwareEncoder(20, 15, 6);
        System.out.printf("hw = %016X\n",value);
        long addr = DetectorTools.softwareEncoder(18,78, 1568, 122,46);
        System.out.printf("sf = %016X\n",addr);
        int[] address = new int[5];
        
        DetectorTools.softwareDecoder(addr, address);
        System.out.println(" address : " + Arrays.toString(address));
    }
    
}
