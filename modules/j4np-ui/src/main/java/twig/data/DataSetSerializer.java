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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twig.config.TDataAttributes;

/**
 *
 * @author gavalian
 */
public class DataSetSerializer {
    
    private static String emptyJsonDataSet = "{ \"name\": \"unknown\", \"type\": \"unknown\"}";
    
    public static String toJson(DataSet ds){
        if(ds instanceof H1F){
            String jsonString = DataSetSerializer.serialize_H1F_JSON((H1F) ds);
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
    
    public static void export(DataSet ds, String archive, String directory){
        String jsonString = DataSetSerializer.toJson(ds);
        String       path = String.format("%s/%s.dataset",directory,ds.getName());
        ArchiveUtils.addInputStream(archive, path, Arrays.asList(jsonString));
    }
    
    public static List<DataSet>  importDir(String archive, String directory){
        List<DataSet> dataList = new ArrayList<>();
        return dataList;
    }
    
    public static DataSet load(String archive, String directory){
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
            
            if(type.contains("GraphErrors")==true){
                GraphErrors gre = DataSetSerializer.deserialize_GraphErrors_JSON(jsonString);
                return gre;
            }
            
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
    
    public static String serialize_H1F_JSON(H1F h){
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
        str.append(String.format("\"axis\": %s,\n", DataSetSerializer.jsonArray(h.getAxis().axisMargins)));        
        str.append("\"data\": ").append(binContent)
                .append(",\n").append("\"error\":").append(binErrors).append("\n}\n");
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
        
        JsonArray  data  = jsonObject.get("data").asArray();
        JsonArray  error = jsonObject.get("error").asArray();
        
        int nData = data.size();
        for(int bin = 0; bin < nData; bin++){
            h.setBinContent(bin, data.get(bin).asDouble());
            h.setBinError(bin, error.get(bin).asDouble());
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
    
    
    public static void main(String[] argas){
        
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
    }
}
