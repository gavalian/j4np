/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.data;

import j4np.utils.base.ArchiveUtils;
import j4np.utils.io.TextFileReader;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import j4np.utils.json.JsonValue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import twig.config.TDataAttributes;

/**
 *
 * @author gavalian
 */
public class DataSetSerializer {
    
    private static String emptyJsonDataSet = "{ \"name\": \"unknown\", \"type\": \"unknown\"}";
    private static Deflater     compresser = new Deflater(); 
    
    
    public static String toJson(DataSet ds){
        if(ds instanceof H1F){
            String jsonString = DataSetSerializer.serialize_H1F_JSON((H1F) ds);
            return jsonString;
        }
        
        if(ds instanceof H2F){
            String jsonString = DataSetSerializer.serialize_H2F_JSON((H2F) ds);
            return jsonString;
        }
        
        if(ds instanceof H3F){
            String jsonString = DataSetSerializer.serialize_H3F_JSON((H3F) ds);
            return jsonString;
        }
        
        if(ds instanceof GraphErrors){
            String jsonString = DataSetSerializer.serialize_GraphErrors_JSON((GraphErrors) ds);
            return jsonString;
        }
        
        
        return DataSetSerializer.emptyJsonDataSet;
    }
    
    public static void export(List<DataSet> dataList, String archive, String directory){
        for(DataSet dataSet : dataList){
            DataSetSerializer.export(dataSet, archive, directory);
        }
    }
    
    
    public static void exportDataGroup(DataGroup group, String archive, String directory){
        DataSetSerializer.export(group.getData(), archive, directory);
        String path = String.format("%s/%s.group", directory, group.getName());
        ArchiveUtils.addInputStream(archive, path, Arrays.asList(group.toJson()));
    }
    
    public static DataGroup importDataGroup(String archive, String directory, String filename){
        String path = directory + "/" + filename;
        
        if(path.endsWith(".group")==false){
                path += ".group";
        }
        
        String     jsonString = ArchiveUtils.getFile(archive, path);
        JsonObject jsonObject = (JsonObject) Json.parse(jsonString);
        String           type = jsonObject.get("class").asString();
        if(type.compareTo("twig.data.DataGroup")==0){
            JsonArray datasets = jsonObject.get("datasets").asArray();
             String           name = jsonObject.get("name").asString();
             String     attributes = jsonObject.get("attributes").asString();
             int           columns = jsonObject.get("columns").asInt();
             int              rows = jsonObject.get("rows").asInt();
             DataGroup grp = new DataGroup(name,columns,rows);
             grp.setRegionAttributes(attributes);
             for(int i = 0; i < datasets.size(); i++){
                 DataSet ds = DataSetSerializer.load(archive, directory + "/" + datasets.get(i).asString());
                 grp.getData().add(ds);
             }
             
             grp.configure(jsonObject);
             return grp;
        } else {
            System.out.println("[DataSetSerializer::importDataGroup] >>> error, the file is not a groups file.");
        }
        return null;
    }
    
    public static void export(DataSet ds, String archive, String directory){
        String jsonString = DataSetSerializer.toJson(ds);
        String       path = String.format("%s/%s.dataset",directory,ds.getName());
        ArchiveUtils.addInputStream(archive, path, Arrays.asList(jsonString));
    }
    
    public static List<DataSet>  importDir(String archive, String directory){
        List<DataSet> dataList = new ArrayList<>();
        return dataList;
    }
    
    public static List<DataSet> deserializeJsonArray(JsonArray array){
        List<DataSet> dslist = new ArrayList<>();
        for(JsonValue item : array.values()){
            DataSet ds = DataSetSerializer.deserialize(item.toString());
            if(ds!=null){
                dslist.add(ds);
            } else {
                dslist.add(new H1F("",10,0.0,1.0));
            }
        }
        return dslist;
    }
    
    public static DataSet deserialize(String jsonString){
        JsonObject jsonObject = (JsonObject) Json.parse(jsonString);
        String           type = jsonObject.get("class").asString();
        if(type.contains("H1F")==true){
            H1F h1f = DataSetSerializer.deserialize_H1F(jsonString);
            return h1f;
        }        
        if(type.contains("GraphErrors")==true){
            GraphErrors gre = DataSetSerializer.deserialize_GraphErrors_JSON(jsonString);
            return gre;
        }
        return null;
    }
    
    public static DataSet load(String archive, String directory){
        try {
            if(directory.endsWith(".dataset")==false){
                directory += ".dataset";
            }
            if(ArchiveUtils.hasFile(archive, directory)==true){
                
                String     jsonString = ArchiveUtils.getFile(archive, directory);
                
                JsonObject jsonObject = (JsonObject) Json.parse(jsonString);
                String           type = jsonObject.get("class").asString();
                
                if(type.contains("H1F")==true){
                    H1F h1f = DataSetSerializer.deserialize_H1F(jsonString);
                    return h1f;
                }
                
                if(type.contains("H3F")==true){
                    H3F h3f = DataSetSerializer.deserialize_H3F(jsonString);
                    return h3f;
                }
                
                if(type.contains("H2F")==true){
                    H2F h2f = DataSetSerializer.deserialize_H2F(jsonString);
                    return h2f;
                }
                
                if(type.contains("GraphErrors")==true){
                    GraphErrors gre = DataSetSerializer.deserialize_GraphErrors_JSON(jsonString);
                    return gre;
                }
                
            }  else {
                System.out.println("[] error with archive : " + archive );
                System.out.println("[] error loading file : " + directory );
            }
        } catch (Exception e){
            System.out.println(" error loading directory : " + directory);
            e.printStackTrace();
        }
        return null;
    }
    
        public static String serialize(DataSet ds){ 
        return DataSetSerializer.serialize(ds, false);
    }
    
    public static String serialize(DataSet ds, boolean compact){
        if(ds instanceof H1F){
            return DataSetSerializer.serialize_H1F_JSON((H1F) ds, compact); 
        }
        if(ds instanceof GraphErrors){
            return DataSetSerializer.serialize_GraphErrors_JSON((GraphErrors) ds);
        }
        return null;
    }
    
    public static String serialize_GraphErrors_JSON(GraphErrors gr){
        
        StringBuilder str = new StringBuilder();
                
        str.append(String.format("{\n\"name\": \"%s\",\n", gr.getName()));
        str.append(String.format("\"class\": \"%s\",\n", gr.getClass().getName()));        

        String attributes = DataSetSerializer.attributesToJson(gr.attr());
        str.append(attributes).append(",\n");
        
        str.append("\"dataX\": ")
                .append(DataSetSerializer.dataVectorToJson(gr.getVectorX()))
                .append(",\n");
        str.append("\"dataY\": ")
                .append(DataSetSerializer.dataVectorToJson(gr.getVectorY()))
                .append(",\n");
        str.append("\"dataEX\": ")
                .append(DataSetSerializer.dataVectorToJson(gr.getVectorEX()))
                .append(",\n");
        str.append("\"dataEY\": ")
                .append(DataSetSerializer.dataVectorToJson(gr.getVectorEY()));
        str.append("\n}\n"); 
        return str.toString();
    }
    
    public static String dataVectorToJson(DataVector vec){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < vec.getSize(); i++){
            if(i!=0) str.append(",");
            str.append(String.format("%e", vec.getValue(i)));
        }
        str.append("]");
        return str.toString();
    }
    
    public static void dataVectorFromJson(DataVector vec, JsonObject json, String name){
        vec.clear();
        //System.out.println(" getting -------- : "  + name);
        JsonArray vecData = json.get(name).asArray();
        for(int i = 0; i < vecData.size(); i++) vec.add(vecData.get(i).asDouble());
    }
    
    private static String serialize_H1F_JSON(H1F h){
        return DataSetSerializer.serialize_H1F_JSON(h, false);
    }
    
    private static String serialize_H2F_JSON(H2F h){
        return DataSetSerializer.serialize_H2F_JSON(h, false);
    }
    
    private static String serialize_H1F_JSON(H1F h, boolean compact){
        
        StringBuilder str = new StringBuilder();        
        String binContent = DataSetSerializer.jsonArray(h.histogramData);
        String binErrors  = DataSetSerializer.jsonArray(h.histogramDataError);
        
        str.append(String.format("{\"name\": \"%s\",\n", h.getName()));
        str.append(String.format("\"UID\": \"%d\",\n", h.getUniqueID()));
        str.append(String.format("\"class\": \"%s\",\n",h.getClass().getName()));        
        str.append(String.format("\"stats\": [%d,%d,%d],\n",
                h.getEntries(),h.getUnderflow(),h.getOverflow()));        
        String attributes = DataSetSerializer.attributesToJson(h.attr());
        
        str.append(attributes).append(",\n");
        
        str.append(String.format("\"axis\": %s,\n", 
                DataSetSerializer.jsonArray(h.getAxis().axisMargins)));        
        
        str.append("\"data\": ").append(binContent);
                //.append(",\n");
                
        if(compact==false){
            str.append(",\n").append("\"error\":")
                    .append(binErrors);
        }                        
        str.append("\n}\n");
        return str.toString();
    }
    
    private static String serialize_H2F_JSON(H2F h, boolean compact){
        
        StringBuilder str = new StringBuilder();        
        String binContent = DataSetSerializer.jsonArray(h.hBuffer);
        //String binErrors  = DataSetSerializer.jsonArray(h.histogramDataError);
        
        str.append(String.format("{\"name\": \"%s\",\n", h.getName()));
        str.append(String.format("\"UID\": \"%d\",\n", h.getUniqueID()));
        str.append(String.format("\"class\": \"%s\",\n",h.getClass().getName()));        
        str.append(String.format("\"stats\": [%d,%d,%d],\n",
                h.getEntries(),0,0));
        
        String attributes = DataSetSerializer.attributesToJson(h.attr());
        
        str.append(attributes).append(",\n");
        
        str.append(String.format("\"axisx\": %s,\n", 
                DataSetSerializer.jsonArray(h.getXAxis().axisMargins)));        
        str.append(String.format("\"axisy\": %s,\n", 
                DataSetSerializer.jsonArray(h.getYAxis().axisMargins))); 
        
        str.append("\"data\": ").append(binContent);
                //.append(",\n");
                
        /*if(compact==false){
            str.append(",\n").append("\"error\":")
                    .append(binErrors);
        } */                       
        str.append("\n}\n");
        return str.toString();
    }
    
    private static String serialize_H3F_JSON(H3F h){
        return DataSetSerializer.serialize_H3F_JSON(h,false);
    }
    
    private static String serialize_H3F_JSON(H3F h, boolean compact){
        
        StringBuilder str = new StringBuilder();        
        String binContent = DataSetSerializer.jsonArray(h.hBuffer);
        //String binErrors  = DataSetSerializer.jsonArray(h.histogramDataError);
        
        str.append(String.format("{\"name\": \"%s\",\n", h.getName()));
        str.append(String.format("\"UID\": \"%d\",\n", h.getUniqueID()));
        str.append(String.format("\"class\": \"%s\",\n",h.getClass().getName()));        
        str.append(String.format("\"stats\": [%d,%d,%d],\n",
                h.getEntries(),0,0));
        
        String attributes = DataSetSerializer.attributesToJson(h.attr());
        
        str.append(attributes).append(",\n");
        
        str.append(String.format("\"axisx\": %s,\n", 
                DataSetSerializer.jsonArray(h.getAxisX().axisMargins)));        
        str.append(String.format("\"axisy\": %s,\n", 
                DataSetSerializer.jsonArray(h.getAxisY().axisMargins))); 
        str.append(String.format("\"axisz\": %s,\n", 
                DataSetSerializer.jsonArray(h.getAxisZ().axisMargins)));
        
        str.append("\"data\": ").append(binContent);
                //.append(",\n");
                
        /*if(compact==false){
            str.append(",\n").append("\"error\":")
                    .append(binErrors);
        } */                       
        str.append("\n}\n");
        return str.toString();
    }
    public static void attributesFromJson(TDataAttributes attr, JsonObject json) {
        JsonArray  jsonAttr = json.get("attributes").asArray();
        
        int size = jsonAttr.size();
        for(int l = 0; l < size; l++){
            String  name = jsonAttr.get(l).asObject().get("name").asString();
            String  type = jsonAttr.get(l).asObject().get("type").asString();
            String value = jsonAttr.get(l).asObject().get("value").asString();
            //System.out.printf("%12s, %24s, %s\n",name,type,value);
            String accessor = "set" + name;


            if(type.compareTo("int")==0){
                Integer intValue = Integer.parseInt(value);
                
                try {
                    Method  method   = attr.getClass().getMethod(accessor, int.class);
                    method.invoke(attr, intValue);
                } catch (NoSuchMethodException ex) {
                    System.out.println(">>>> deserializing error : method not found : " + accessor);
                } catch (SecurityException ex) {
                    System.out.println(">>>> deserializing error : security vialoation for method : " + accessor);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            if(type.contains("String")==true){
                Method  method;
                try {
                    method = attr.getClass().getMethod(accessor, String.class);
                    method.invoke(attr, value);
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
                }                                
            }
        }
    }
    
    public static String attributesToJson(TDataAttributes attr){
        StringBuilder str = new StringBuilder();
        Field[] fields = attr.getClass().getFields();
        Method[] methods = attr.getClass().getMethods();
        str.append("\"attributes\": [");
        int counter = 0;
        for(int i = 0; i < methods.length; i++){
            try {
                String name  = methods[i].getName();
                if(name.contains("get")==true){

                    Object value = methods[i].invoke(attr);
                    Class  type = methods[i].getReturnType();
                    //String type  = "int";
                    String jsonObject = 
                            "{\"name\":" + "\"" + name.replace("get","") + "\","
                            + "\"type\":" + "\"" + type.getName() + "\","
                            + "\"value\":" + "\"" + value + "\"}";
                    //System.out.println(" [ " + name + " ] = " + value);
                    //System.out.println(jsonObject);
                    //System.out.println("counter = " + counter);
                    if(counter!=0) str.append(",");
                    str.append(jsonObject);
                    counter++;
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        str.append("]");
        return str.toString();
    }
    
    public static H1F deserialize_H1F(String json){
        
        JsonObject jsonObject = (JsonObject) Json.parse(json);
        String           name = jsonObject.get("name").asString();
        
        JsonArray  axisLimits = jsonObject.get("axis").asArray();
        
        
        int nBins = axisLimits.values().size();
        
        double[] axisBins = new double[nBins];
        
        for(int i = 0; i < nBins; i++) 
            axisBins[i] = axisLimits.get(i).asDouble();//Double.parseDouble(axisLimits.get(i).asString());
        
        H1F h = new H1F(name,axisBins);
        
        JsonArray  stats  = jsonObject.get("stats").asArray();
        int nStats = stats.size();
        if(nStats>=3){
             h.setEntries(stats.get(0).asInt());
             h.setUnderflow(stats.get(1).asInt());
             h.setOverflow(stats.get(2).asInt());
        }
        JsonArray  data  = jsonObject.get("data").asArray();
        
        JsonValue errorObj = jsonObject.get("error");
        
        JsonArray    error = null;
        if(errorObj!=null) error = jsonObject.get("error").asArray();
        //if(errorObj==null) System.out.println("[deserialize] ::: oh no, no error data");
        int nData = data.size();
        for(int bin = 0; bin < nData; bin++){
            double value = data.get(bin).asDouble();
            h.setBinContent(bin, value);
            if(error!=null){
                h.setBinError(bin, error.get(bin).asDouble());
            } else {
                h.setBinError(bin,Math.sqrt(Math.abs(value)));
            }
        }
        
        DataSetSerializer.attributesFromJson(h.attr(), jsonObject);
        
        return h;
    }
    
    
    public static H2F deserialize_H2F(String json){
        
        JsonObject jsonObject = (JsonObject) Json.parse(json);
        String           name = jsonObject.get("name").asString();
        
        JsonArray  axisLimitsX = jsonObject.get("axisx").asArray();
        JsonArray  axisLimitsY = jsonObject.get("axisy").asArray();
        
        int nBinsX = axisLimitsX.values().size();
        int nBinsY = axisLimitsY.values().size();
        
        double[] axisBinsX = new double[nBinsX];
        double[] axisBinsY = new double[nBinsY];
        
        for(int i = 0; i < nBinsX; i++) 
            axisBinsX[i] = axisLimitsX.get(i).asDouble();//Double.parseDouble(axisLimits.get(i).asString());
        
        for(int i = 0; i < nBinsY; i++) 
            axisBinsY[i] = axisLimitsY.get(i).asDouble();//Double.parseDouble(axisLimits.get(i).asString());

        H2F h = new H2F(name,axisBinsX,axisBinsY);
        
        JsonArray  stats  = jsonObject.get("stats").asArray();
        int nStats = stats.size();
        if(nStats>=3){
             //h.setEntries(stats.get(0).asInt());
             //h.setUnderflow(stats.get(1).asInt());
             //h.setOverflow(stats.get(2).asInt());
        }
        JsonArray  data  = jsonObject.get("data").asArray();
        
        JsonValue errorObj = jsonObject.get("error");
        
        JsonArray    error = null;
        if(errorObj!=null) error = jsonObject.get("error").asArray();
        //if(errorObj==null) System.out.println("[deserialize] ::: oh no, no error data");
        int nData = data.size();
        for(int bin = 0; bin < nData; bin++){
            double value = data.get(bin).asDouble();
            h.hBuffer[bin] = value;
            //if(error!=null){
            //    h.setBinError(bin, error.get(bin).asDouble());
            //} else {
            //    h.setBinError(bin,Math.sqrt(Math.abs(value)));
            //}
        }
        
        DataSetSerializer.attributesFromJson(h.attr(), jsonObject);
        
        return h;
    }
    
        
    public static H3F deserialize_H3F(String json){
        
        JsonObject jsonObject = (JsonObject) Json.parse(json);
        String           name = jsonObject.get("name").asString();
        
        JsonArray  axisLimitsX = jsonObject.get("axisx").asArray();
        JsonArray  axisLimitsY = jsonObject.get("axisy").asArray();
        JsonArray  axisLimitsZ = jsonObject.get("axisz").asArray();
        
        int nBinsX = axisLimitsX.values().size();
        int nBinsY = axisLimitsY.values().size();
        int nBinsZ = axisLimitsZ.values().size();
        
        double[] axisBinsX = new double[nBinsX];
        double[] axisBinsY = new double[nBinsY];
        double[] axisBinsZ = new double[nBinsZ];
        
        for(int i = 0; i < nBinsX; i++) 
            axisBinsX[i] = axisLimitsX.get(i).asDouble();//Double.parseDouble(axisLimits.get(i).asString());
        
        for(int i = 0; i < nBinsY; i++) 
            axisBinsY[i] = axisLimitsY.get(i).asDouble();//Double.parseDouble(axisLimits.get(i).asString());

        for(int i = 0; i < nBinsZ; i++) 
            axisBinsZ[i] = axisLimitsZ.get(i).asDouble();//Double.parseDouble(axisLimits.get(i).asString());
        
        H3F h = new H3F(axisBinsX,axisBinsY,axisBinsZ);
        
        JsonArray  stats  = jsonObject.get("stats").asArray();
        int nStats = stats.size();
        
        if(nStats>=3){
             //h.setEntries(stats.get(0).asInt());
             //h.setUnderflow(stats.get(1).asInt());
             //h.setOverflow(stats.get(2).asInt());
        }
        
        JsonArray  data  = jsonObject.get("data").asArray();
        JsonValue errorObj = jsonObject.get("error");        
        JsonArray    error = null;
        if(errorObj!=null) error = jsonObject.get("error").asArray();
        
        //if(errorObj==null) System.out.println("[deserialize] ::: oh no, no error data");
        int nData = data.size();
        for(int bin = 0; bin < nData; bin++){
            double value = data.get(bin).asDouble();
            h.hBuffer[bin] = value;
        }
        
        DataSetSerializer.attributesFromJson(h.attr(), jsonObject);
        
        return h;
    }
    
    public static GraphErrors deserialize_GraphErrors_JSON(String json){
        
        JsonObject jsonObject = (JsonObject) Json.parse(json);
        String           name = jsonObject.get("name").asString();
        
        DataVector  vx = new DataVector();
        DataVector  vy = new DataVector();
        DataVector vex = new DataVector();
        DataVector vey = new DataVector();
        
        DataSetSerializer.dataVectorFromJson(vx, jsonObject, "dataX");
        DataSetSerializer.dataVectorFromJson(vy, jsonObject, "dataY");
        DataSetSerializer.dataVectorFromJson(vex, jsonObject, "dataEX");
        DataSetSerializer.dataVectorFromJson(vey, jsonObject, "dataEY");
        GraphErrors graph = new GraphErrors(name,vx,vy,vex,vey);
        
        DataSetSerializer.attributesFromJson(graph.attr(), jsonObject);
        return graph;
    }
    /*public static String filedFromMethod(String method){
        
    }*/
    public static String jsonArray(double[] data){
        StringBuilder str = new StringBuilder();
        
        str.append("[");
        for(int i = 0; i < data.length; i++){
            if(i!=0) str.append(",");
            str.append(String.format("%e", data[i]));
        }
        str.append("]");
        return str.toString();
    } 
    
    public static String serializeDirectory(TDirectory dir, List<String> dataList){
        StringBuilder str = new StringBuilder();
        str.append("[");
        for(int i = 0; i < dataList.size(); i++){
            String  dataName = dataList.get(i);
            DataSet  dataSet = dir.get(dataName);
            if(dataSet==null) dataSet = new H1F("h100000",42,0.0,1.0);
            String jsonString = DataSetSerializer.serialize(dataSet);
            if(i!=0) str.append(",");
            str.append(jsonString);
        }
        str.append("]");
        return str.toString();
    }
    
    public static byte[] serializeDirectoryDeflate(TDirectory dir, List<String> dataList){
        String json = DataSetSerializer.serializeDirectory(dir, dataList);
        //System.out.println(json);
        byte[]  input = json.getBytes();
        byte[] output = new byte[input.length];
        Deflater     deflater = new Deflater(); 
        deflater.setInput(input);
        deflater.finish();
        int deflatedSize = deflater.deflate(output);
        deflater.end();
        byte[] result = new byte[deflatedSize];
        System.arraycopy(output, 0, result, 0, deflatedSize);
        return result;
    }
    
    public static String serializeDirectoryDeflateBase64(TDirectory dir, List<String> dataList){
        byte[] deflated = DataSetSerializer.serializeDirectoryDeflate(dir, dataList);
        String   base64 = Base64.getEncoder().encodeToString(deflated);
        return base64;
    }
    
    public static String deserializeDeflatedBase64(String base64) {
         try {
             byte[] data64 = Base64.getDecoder().decode(base64);
             Inflater inflater = new Inflater();
             inflater.setInput(data64);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data64.length);  
             byte[] buffer = new byte[1024];
             while (!inflater.finished()) {  
                 int count = inflater.inflate(buffer);  
                 outputStream.write(buffer, 0, count);                 
             }  
             outputStream.close();  
             byte[] output = outputStream.toByteArray();
             String dataString = new String(output);
             return dataString;
         } catch (DataFormatException ex) {
             Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
         } catch (IOException ex) {
            Logger.getLogger(DataSetSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }
         return null;
    }
    
    public static List<DataSet> deserializeDeflatedDataList(String base64) {
        String dataString = DataSetSerializer.deserializeDeflatedBase64(base64);
        if(dataString!=null){
            JsonArray jsonArray = (JsonArray) Json.parse(dataString);
            List<DataSet> dataList = DataSetSerializer.deserializeJsonArray(jsonArray);
            return dataList;
        }
        return new ArrayList<>();
    }
    //public static void deserializeDirectory(TDirectory dir, String jsonString){
    //    List<DataSet> dataSet = 
    //}
    
    public static void main(String[] argas){
        
        
        H3F h3 = new H3F(5,0,1,5,0,1,5,0,1);
        
        
        String json = DataSetSerializer.serialize_H3F_JSON(h3, false);
        
        System.out.println(json);
        
        
        H3F h32 = DataSetSerializer.deserialize_H3F(json);
        
        
        /*H1F h = new H1F("h",20,.0,1.0);
        Random r = new Random();
        for(int i = 0; i < 120; i++){
            h.fill(r.nextGaussian()+0.5);
        }
        String json = DataSetSerializer.serialize_H1F_JSON(h);
        System.out.println(json);
        */
        
        
        /*
        TDirectory dir = new TDirectory();
        
        dir.add("/server/dc", new H1F("h100",120,0.0,1.0));
        dir.add("/server/dc", new H1F("h200",120,0.0,1.0));
        dir.add("/server/dc", new H1F("h300",120,0.0,1.0));
        
        dir.add("/server/ec", new H1F("e101",120,0.0,1.0));
        dir.add("/server/ec", new H1F("e102",120,0.0,1.0));
        
        dir.show();
        List<String> list = Arrays.asList(
                "/server/dc/h100","/server/ec/e101");
        
        String json = DataSetSerializer.serializeDirectory(dir, list);
        System.out.println(json);
        
        String base64 = DataSetSerializer.serializeDirectoryDeflateBase64(dir, list);
        System.out.println(base64);
     
        System.out.printf("\n\n raw length = %d, base64 length = %d\n\n",
                json.length(),base64.length());

        String inflatedString = DataSetSerializer.deserializeDeflatedBase64(base64);
        
        System.out.println(inflatedString);
        
        List<DataSet>  dataSetList = DataSetSerializer.deserializeDeflatedDataList(base64);
        
        for(DataSet ds : dataSetList){
            System.out.printf("\t%s : %s\n\n",ds.getName(),ds.getClass().getName());
        }*/
        
       /* JsonObject obj = (JsonObject) Json.parse("{\"x\":3,\"y\":[4,5,6,7]}");
        
        int x = obj.get("x").asInt();
        System.out.println("x = " + x);
        JsonArray y = (JsonArray) obj.get("y").asArray();
        System.out.println("y = ");
        
        for(JsonValue v : y.values()){
            System.out.printf("\t %d\n",v.asInt());
        }
        
        JsonValue z =  obj.get("z");
        boolean flag = (z instanceof JsonArray);
        System.out.println(" does exist ? " + (z!=null) + " is array ? " + flag);
        
        H1F h = new H1F("h100",120,0.0,1.0);
        
        String hs = DataSetSerializer.serialize_H1F_JSON(h, true);
        
        System.out.println(hs);
        
        H1F hd = DataSetSerializer.deserialize_H1F(hs);*/
       
       
        /*
        H1F h = TDataFactory.createH1F(2500);
        GraphErrors gr = new GraphErrors("graphData",new double[]{1,2,3},new double[]{0.5,0.6,0.9});        
        h.attr().setTitle("first serialization of h1f");
        
        DataSetSerializer.export(  h, "dataStudies.twig", "data/study/ai");
        DataSetSerializer.export( gr, "dataStudies.twig", "data/study/ai");
        
        TextFileReader reader = new TextFileReader();
        reader.open("/Users/gavalian/Work/Software/project-10.0/temp/dataBins.txt");
        
        H1F ht = new H1F("mxGammaPPipPim",60,1.4,2.6);
        
        while(reader.readNext()==true){
            String[] tokens = reader.getString().split("\\s+");
            int bin = Integer.parseInt(tokens[0]);
            double content = Double.parseDouble(tokens[1]);
            ht.setBinContent(bin-1, content);
        }
        reader = new TextFileReader();
        reader.open("/Users/gavalian/Work/Software/project-10.0/temp/dataError.txt");
        while(reader.readNext()==true){
            String[] tokens = reader.getString().split("\\s+");
            int bin = Integer.parseInt(tokens[0]);
            double content = Double.parseDouble(tokens[1]);
            ht.setBinError(bin-1, content);
        }        
        DataSetSerializer.export(ht, 
                "/Users/gavalian/Work/Software/project-10.0/temp/thetaDataset.twig",
                "experiment/g11");
        */
    }
}
