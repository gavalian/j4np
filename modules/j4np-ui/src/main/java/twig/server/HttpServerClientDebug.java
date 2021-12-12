/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class HttpServerClientDebug {
    public static void debug1(){
        String request = DataRequestProtocol.createRequestData(Arrays.asList(
                "/server/dc/h100","/server/dc/h101","/server/dc/h102","/server/dc/h103"));
        
        System.out.println(request);
        
        List<String> dataList = DataRequestProtocol.getRequestDataList(request);
        System.out.printf("SIZE = %d\n",dataList.size());
        
        for(String data : dataList) System.out.println("\t--> " + data);
    }
    
    public static void main(String[] args){
        HttpServerClientDebug.debug1();
    }
}
