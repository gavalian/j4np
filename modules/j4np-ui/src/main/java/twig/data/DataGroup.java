/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.data;

import j4np.graphics.CanvasLayout;
import j4np.utils.json.Json;
import j4np.utils.json.JsonArray;
import j4np.utils.json.JsonObject;
import java.util.ArrayList;
import java.util.List;
import twig.graphics.TGCanvas;
import twig.graphics.TGDataCanvas;
import twig.graphics.TTabDataCanvas;
import twig.math.F1D;

/**
 * 
 * @author gavalian
 */
public class DataGroup {
    
    private String                  groupName = "datagroup";
    private List<DataSet>           groupData = new ArrayList<>();
    private List<DataGroupRegion> groupRegions = new ArrayList<>();    
    private List<DataGroupDescriptor> groupDescriptors = new ArrayList<>();
    
    private List<Double>   axisTickMarks = new ArrayList<>();
    private List<String>  axisTickLabels = new ArrayList<>();
    
    private int canvasDivisionX = 1;
    private int canvasDivisionY = 1;
    
    private String regionAttributes = "";
    private String canvasAttributes = "";
    
    private CanvasLayout  layout = null;
    
    public DataGroup(){
        groupRegions.add(new DataGroupRegion(0));
    }
    
    public DataGroup(int xsize, int ysize){
        this.canvasDivisionX = xsize; this.canvasDivisionY = ysize;
        for(int r = 0; r < xsize*ysize; r++) 
            groupRegions.add(new DataGroupRegion(r));
        layout = CanvasLayout.grid(xsize, ysize);
    }
    
    public DataGroup(String name, int xsize, int ysize){
        this.groupName = name;
        this.canvasDivisionX = xsize; this.canvasDivisionY = ysize;
        for(int r = 0; r < xsize*ysize; r++) 
            groupRegions.add(new DataGroupRegion(r));
        layout = CanvasLayout.grid(xsize, ysize);
        layout.show();
    }
    
    public DataGroup(String name, CanvasLayout lo){
        this.groupName = name;
        this.layout = lo;
        int size = this.layout.size();
        //this.canvasDivisionX = xsize; this.canvasDivisionY = ysize;
        
        for(int r = 0; r < size; r++) 
            groupRegions.add(new DataGroupRegion(r));
        //layout = CanvasLayout.grid(xsize, ysize);
    }
    
    public void show(){
        System.out.printf("[DataGroup] >>> size %d , columns = %d, rows = %d\n",
                this.groupData.size(), this.canvasDivisionX,this.canvasDivisionY);
        for(int i = 0; i < this.groupData.size(); i++){
            System.out.printf("%24s | %s\n",groupData.get(i).getName(),groupData.get(i).getClass().getName());
        }
        
        for(DataGroupRegion reg : this.groupRegions){
            System.out.println(reg);
        }
    }
    public String getName(){return groupName;}
    
    public DataGroup setName(String name){this.groupName = name; return this;}
    
    public List<DataGroupRegion> getRegions(){ return groupRegions;}
    
    public DataGroup[] duplicate(String... exts){
        DataGroup[] groups = new DataGroup[exts.length];
        for(int i = 0; i < groups.length; i++)
            groups[i] = this.duplicateDataGroup(exts[i]);
        return groups;
    }
    
    private DataGroup duplicateDataGroup(String ext){
        DataGroup group = new DataGroup();
        for(int k = 0; k < groupData.size(); k++){
            DataSet ds = groupData.get(k);
            if(ds instanceof H1F) { 
                H1F h = ((H1F) ds).copy();
                h.setName(ds.getName()+ext);
                h.attr().setTitle(ds.attr().getTitle());
                h.attr().setTitleX(ds.attr().getTitleX());
                h.attr().setTitleY(ds.attr().getTitleY());
                h.reset();
                group.groupData.add(h);
            }
            
            if(ds instanceof H2F) { 
                H2F h = ((H2F) ds).histClone(ds.getName()+ext);
                h.attr().setTitle(ds.attr().getTitle());
                h.attr().setTitleX(ds.attr().getTitleX());
                h.attr().setTitleY(ds.attr().getTitleY());
                //h.setName(ds.getName()+ext);
                h.reset();
                group.groupData.add(h);
            }
            if(ds instanceof GraphErrors) { 
                GraphErrors gr = ((GraphErrors) ds).copy();
                gr.attr().setTitle(ds.attr().getTitle());
                gr.attr().setTitleX(ds.attr().getTitleX());
                gr.attr().setTitleY(ds.attr().getTitleY());
                gr.setName(ds.getName()+ext);
            }
            
        }
        return group;
    }
        
    public void lineColors(int... colors){
        for(int i = 0; i < colors.length; i++){ 
            this.groupData.get(i).attr().setLineColor(colors[i]);
        }
    }
    
    public void fillColors(int... colors){
        for(int i = 0; i < colors.length; i++){ 
            this.groupData.get(i).attr().setFillColor(colors[i]);
        }
    }
    
    public void fill(double... values){
        int index = 0;
        int  data = 0;
        while(index<values.length&&data<groupData.size()){
            DataSet ds = this.groupData.get(data);
            data++;
            if(ds instanceof H1F){
                ((H1F) ds).fill(values[index]);
                index++;
            } else {            
                if(ds instanceof H2F){
                    ((H2F) ds).fill(values[index],values[index+1]);
                    index+=2;
                }
            }
        }
    }
    
    public void configure(JsonObject jsonObj){
        JsonArray regions = jsonObj.get("regions").asArray();
        for(int r = 0; r < regions.size(); r++){
            DataGroupRegion region = groupRegions.get(r);
            JsonArray desc = ((JsonObject) regions.get(r)).get("descriptors").asArray();
            for(int d = 0; d < desc.size(); d++){
                JsonObject dObj = desc.get(d).asObject();
                DataGroupDescriptor ptr = DataGroupDescriptor.fromJson(dObj);
                region.dataDescriptor.add(ptr);
            }
        }
    }
    
    public String toJson(){
        StringBuilder str = new StringBuilder();
        str.append("{\n");
        str.append("\"type\":\"datagroup\",\n");
        str.append(String.format("\"class\":\"%s\",\n",DataGroup.class.getName()));
        str.append(String.format("\"name\":\"%s\",\n",getName()));
        str.append(String.format("\"columns\":%d, \"rows\":%d,\n",this.canvasDivisionX,this.canvasDivisionY));
        str.append("\"datasets\": [");
        for(int i = 0; i < this.groupData.size(); i++){
            str.append(String.format("\"%s\"", this.groupData.get(i).getName()));
            if(i!=groupData.size()-1) str.append(",");
        }
        str.append("],\n");
        str.append("\"regions\": [\n");
        for(int i = 0; i < this.groupRegions.size(); i++){
            //str.append(String.format("{\"data\":\"%s\"",this.groupData.get(i).getName()));
            str.append(String.format("%s",this.groupRegions.get(i).toJson()));
            if(i!=this.groupRegions.size()-1) str.append(",\n"); else str.append("\n");
        }
        str.append("]\n");
        str.append("}");
        return str.toString();
    }
    
    public void draw(TGDataCanvas c){
        c.cd(0);
        for(DataSet ds : this.groupData){
            c.region().draw(ds,"same");
            c.next();
        }
        c.repaint();
    }
    public DataGroup setRegionAttributes(String attr){
        this.regionAttributes = attr; return this;
    }
    
    public DataGroup setCanvasAttributes(String attr){
        this.canvasAttributes = attr; return this;
    }
    
    public void draw(TGDataCanvas c, boolean recreate){
        if(recreate==false){
            draw(c); return;
        }
        
        //c.divide(canvasDivisionX, canvasDivisionY);
        c.divide(layout);
        for(int r = 0; r < groupRegions.size(); r++){
            DataGroupRegion reg = groupRegions.get(r);
            c.cd(reg.order); 
            c.region().set(regionAttributes);
            for(int d = 0; d < reg.dataDescriptor.size(); d++){
                DataGroupDescriptor desc = reg.dataDescriptor.get(d);
                c.region().draw(this.groupData.get(desc.region), desc.options+"same");               
            }
            
            if(reg.showLegend==true) c.region().showLegend(0.05, 0.95);
            if(reg.showStats==true) c.region().showStats(0.98, 0.97);
        }
        /*
        for(int i = 0; i < this.groupData.size(); i++){
            DataGroupDescriptor d = this.groupDescriptors.get(i);
            c.cd(d.region);
            c.region().draw(this.groupData.get(i),"same"+d.options);
            c.next();
        }*/
        c.repaint();
    }
    
    public void draw(TTabDataCanvas c){
        c.addCanvas(this.groupName, true);
        this.draw(c.activeCanvas(), true);
    }
    
    public static void draw(List<DataGroup> list, TTabDataCanvas c){
       for(DataGroup g : list) g.draw(c);
    }
    
    public void draw(TGCanvas c){
        this.draw(c.view());
        c.repaint();
    }
    
    public DataGroup  add(DataSet d){
        this.groupData.add(d);
        //this.groupDescriptors.add(new DataGroupDescriptor(0,""));
        return this;
    }
    
    public DataGroup  add(DataSet d, int region, String options){
        int order = this.groupData.size();
        this.groupData.add(d);
        this.groupRegions.get(region).dataDescriptor.add(new DataGroupDescriptor(d.getName(),order,options));
        //this.groupDescriptors.add(new DataGroupDescriptor(d.getName(),region,options));
        return this;
    }
    
    public List<DataSet> getData(){ return groupData;}
    public List<Double> getAxisTickMarks(){return this.axisTickMarks;}
    public List<String> getAxisTickLabels(){return this.axisTickLabels;}
    
    public static class DataGroupDescriptor {
        public String   name = "";
        public int      region = 0;
        public String  options = "";
        public DataGroupDescriptor(){}
        public DataGroupDescriptor(String nm, int r, String opt){ region = r; options = opt;name = nm;}
        public String  toJson(){
            return String.format("{ \"name\": \"%s\", \"region\":%d, \"options\":\"%s\"}", name,region,options);
        }
        
        public static DataGroupDescriptor fromJson(JsonObject json){
            String     name = json.get("name").asString();
            int      region = json.get("region").asInt();
            String  options = json.get("options").asString();
            
            return new DataGroupDescriptor(name,region,options);
        }
        
        @Override
        public String toString(){
           return String.format("name = %12s, region = %4d, options = [%s]", name,region,options);
        }
    }
    
    public static class DataGroupRegion {
       List<DataGroupDescriptor> dataDescriptor = new ArrayList<>();
       public int order = 0;
       boolean showLegend = false;
       boolean  showStats = false;
       public DataGroupRegion(int ord){ order = ord;}
       
       public String toJson(){
           StringBuilder str = new StringBuilder();
           str.append("{");
           str.append(String.format("\"order\":%d, \"showLegend\":%s, \"showStats\":%s, ",order,showLegend,showStats));
           str.append(" \"descriptors\": [");
           for(int i = 0; i < this.dataDescriptor.size();i++){               
               str.append(this.dataDescriptor.get(i).toJson());
               if(i!=dataDescriptor.size()-1) str.append(",");
           } str.append("]");
           str.append("}");
           return str.toString();
       }
       
       public static DataGroupRegion fromJson(JsonObject json){
           DataGroupRegion region = new DataGroupRegion(0);
           
           return region;
       }
       
       @Override
       public String toString(){
           StringBuilder str = new StringBuilder();
           str.append(String.format(" order = %4d , legend = %8s, stats %8s\n", 
                   order,showLegend,showStats));
           for(DataGroupDescriptor d : this.dataDescriptor){
               str.append("\t").append(d).append("\n");
           }
           return str.toString();
       }
    }
    
    public static DataGroup projectionY(H2F h2){
        int nbinsX = h2.getXAxis().getNBins();
        int nPads = (int) Math.sqrt(nbinsX);
        DataGroup group = new DataGroup(nPads,nPads);
        for(int i = 0; i < nbinsX; i++){
            H1F h = h2.sliceX(i);
            h.attr().setFillColor(4);
            h.attr().setTitleX(String.format("%.4f", h2.getXAxis().getBinCenter(i)));
            if(i<nPads*nPads){
                group.add(h, i, "");
            }
        }
        return group;
    }
    
    public static DataGroup fronJson(String archive, String directory, String json){
        
        JsonObject jsonObject = (JsonObject) Json.parse(json);
        String           name = jsonObject.get("name").asString();
        int           columns = jsonObject.get("columns").asInt();
        int              rows = jsonObject.get("rows").asInt();
        DataGroup grp = new DataGroup(name,columns,rows);
        
        JsonArray    datasets = jsonObject.get("datasets").asArray();
        JsonArray     regions = jsonObject.get("regions").asArray();
        int nData = datasets.size();
        for(int d = 0; d < nData; d++){
            
        }
        return grp;
    }
    
    public static void main(String[] args){
        
        DataGroup grp = new DataGroup("demoCanvas",2,2);
        
        grp.getRegions().get(0).showLegend = true;
        grp.getRegions().get(1).showStats = true;
        
        grp.add(TDataFactory.createH1F("h1",2000), 0, "");
        grp.add(TDataFactory.createH1F("h2",1000), 0, "");
        grp.add(TDataFactory.createH1F("h3",2000), 1, "");
        grp.add(TDataFactory.createH1F("h4",1000), 1, "A");
        
        grp.add(TDataFactory.createH1F("h5",3000,120, 0.0,1.0,0.6,0.1), 2, "");
        grp.add(TDataFactory.createH1F("h6",3000,120, 0.0,1.0,0.2,0.1), 2, "EP");
        F1D func = new F1D("func","[p0]+[p1]*x+[amp]*gaus(x,[mean],[sigma])",0,1.0);
        func.setParameters(1.0,1.0,100,0.2,0.02);
        func.attr().set("lw=3,ls=3,lc=2");
        //grp.add(func, 2, "");
        
        func.fit(grp.getData().get(5));
        func.show();
        H2F h2 = TDataFactory.createH2F(250000,80);
        h2.setName("h2_1");
        
        grp.add(h2, 3, "");
        
        grp.getData().get(0).attr().set("lc=1,fc=82");
        grp.getData().get(1).attr().set("lc=1,fc=84");
        grp.getData().get(2).attr().set("lc=1,fc=67");
        grp.getData().get(3).attr().set("lc=1,fc=7");
        
        grp.getData().get(4).attr().set("lc=1,fc=125");
        grp.getData().get(5).attr().set("lc=1,fc=123");
        
        System.out.println(grp.toJson());
        
        TGCanvas c = new TGCanvas(900,800);
        
        grp.draw(c.view(),true);
        
        DataSetSerializer.exportDataGroup(grp, "groups.twig", "data/groups");
        
        grp.show();
        
        DataGroup grp2 = DataSetSerializer.importDataGroup("groups.twig", "data/groups", "demoCanvas");
        grp2.show();
        
        TGCanvas c2 = new TGCanvas();
        grp2.draw(c2.view(), true);
    }
}
