/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.server;

import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import j4np.utils.json.JsonValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class DataRequestProtocol {
    
    public static String createRequestList(){
        return "{\"request\":\"list\"}";
    }
    
    public static String createRequestData(List<String> dataNames){
        StringBuilder str = new StringBuilder();
        str.append("{\"request\":\"data\",\"format\":\"base64-deflated\", \"list\":[");
        for(int i = 0 ; i < dataNames.size(); i++){
            if(i!=0) str.append(",");
            str.append("\"").append(dataNames.get(i)).append("\"");
        }
        str.append("]}");
        return str.toString();
    }
    
    
    public static String createSendData(String dataString){
        StringBuilder str = new StringBuilder();
        str.append("{\"send\":\"data\",\"format\":\"base64-deflated\", \"data\":");
        str.append("\"").append(dataString).append("\"}");
        return str.toString();
    }
    
    public static boolean isRequestList(String json){
        JsonObject   obj = (JsonObject) Json.parse(json);
        JsonValue  value = obj.get("request");
        if(value==null) return false;
        return (value.asString().compareTo("list")==0);
    }
    
    public static boolean isRequestData(String json){
        JsonObject   obj = (JsonObject) Json.parse(json);
        JsonValue  value = obj.get("request");
        //System.out.println("check request data " + value.asString() + (value.asString().compareTo("data")==0));
        if(value==null) return false;
        return (value.asString().compareTo("data")==0);
    }
    
    public static boolean isSendData(String json){
        JsonObject   obj = (JsonObject) Json.parse(json);
        JsonValue  value = obj.get("send");
        if(value==null) return false;
        return (value.asString().compareTo("data")==0);
    }
    
    public static boolean isSendList(String json){
        JsonObject   obj = (JsonObject) Json.parse(json);
        JsonValue  value = obj.get("send");
        if(value==null) return false;
        return (value.asString().compareTo("list")==0);
    }
    
    public static List<String> getRequestDataList(String json){
        List<String> dataList = new ArrayList<>();
        
        //System.out.println("[parsing] -> " + json);
        
        JsonObject   obj = (JsonObject) Json.parse(json);
        JsonValue  value = obj.get("list");
        if(value!=null){            
            JsonArray array = obj.get("list").asArray();            
            for(JsonValue item : array.values()){
                dataList.add(item.asString());
                //System.out.println(" adding - > " + item.asString());
            }
        }
        
        return dataList;
    }
    
    public static String getSendDataPayload(String json){
        JsonObject   obj = (JsonObject) Json.parse(json);
        JsonValue  value = obj.get("data");
        return value.asString();
    }
    
    public static List<String> getSendListPayload(String json){
        
        List<String> list = new ArrayList<>();
        JsonArray   array = (JsonArray) Json.parse(json);
        
        for(JsonValue value : array.values()){
            JsonObject obj = (JsonObject) value;
            String dir = obj.get("dir").asString();
            JsonArray datasets = obj.get("data").asArray();
            for(JsonValue item : datasets.values()){
                list.add(String.format("%s/%s", dir,item.asString()));
            }
        }
        /*
        JsonObject   obj = (JsonObject) Json.parse(json);
        JsonValue  value = obj.get("data");
        if(value!=null){
            JsonArray  array = (JsonArray) value;
            for(JsonValue v : array.values()){
                list.add(v.asString());
            }
        }*/
        
        return list;
    }
}
