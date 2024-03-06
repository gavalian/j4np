/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.publish;

import j4np.hipo5.data.Bank;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.GraphErrors;
import twig.data.H1F;
import twig.data.H2F;
import twig.graphics.TGCanvas;
import twig.widgets.Polygon;

/**
 *
 * @author gavalian
 */
public class InstaRec {
    
    public GraphErrors getGraphClusters(Bank c){
        DriftChamberTools dt = new DriftChamberTools();
        int nrows = c.getRows();
        int[] sector = new int[nrows/3];
        int[]  layer = new int[nrows/3];
        int[]   wire = new int[nrows/3];
        
        int[] value = c.getInt("byte");
        int counter = 0;
        for(int i = 0; i < nrows; i+=3){
            int ii = c.getInt(0, i);
            sector[counter] = ii/10;
            layer[counter] = ii - 10*(ii/10);
            wire[counter] = c.getInt(0, i+1);
            //System.out.printf(" %d %d %d\n",sector[counter],layer[counter],wire[counter]);
            counter++;
        }
        
        GraphErrors g = dt.getGraph(sector, layer, wire);
        return g;
    }
    
    public H1F[] getClusterPositions(int l, Bank c){
        H1F[] h = H1F.duplicate(6, "clusters", 112,0.5,112.5);
        int nrows = c.getRows();
        int[] value = c.getInt("byte");
        int counter = 0;
        for(int i = 0; i < nrows; i+=3){
            int ii = c.getInt(0, i);
            int sector = ii/10;
            int layer = ii - 10*(ii/10);
            int wire = c.getInt(0, i+1);
            if(l==layer) h[sector-1].fill(wire);
            //System.out.printf(" %d %d %d\n",sector[counter],layer[counter],wire[counter]);
            counter++;
        }
        return h;
    }
    
    public List<GraphErrors> getGraphTracks(Bank t, Bank rec){
        DriftChamberTools dt = new DriftChamberTools();
        int nrows = t.getRows();
        Random r = new Random();
        List<GraphErrors> list = new ArrayList<>();
        int ntrk = nrows/6;
        double[] traj = new double[6];
        int counter = 0;
        for(int k = 0; k < ntrk; k++){
            for(int i = 0; i < 6; i++){
                traj[i] = t.getInt(0,counter); traj[i] /= 100.0;
                counter++;
            }
            int s = findSector(k,rec);
            System.out.printf("%d : (%d)  %s\n", k, s,Arrays.toString(traj));

            GraphErrors g = dt.getTrackGraph(s, traj, true);
            list.add(g);
        }
        
        return list;
    }
    
    public void makePlot(Bank c, Bank t, Bank dc, Bank rec){
        
        DriftChamberTools dt = new DriftChamberTools();
        GraphErrors g = this.getGraphClusters(c);
        List<GraphErrors> trk = this.getGraphTracks(t,rec);        
        List<Polygon> poly = dt.getBoundaries();        
        GraphErrors tdc = dt.getGraph(dc.getInt("sector"), dc.getInt("layer"), dc.getInt("component"));
        TGCanvas cv = new TGCanvas(800,800);
        //cv.draw(g); 
        cv.region().draw(poly);
        cv.region().draw(tdc,"sameP");
        cv.region().draw(trk, "samePL");

        //DriftChamberTools.setStyle(cv);
    }
    
    public int findSector(int index, Bank b){
        for(int i = 0; i < b.getRows(); i++)
            if(b.getInt("index",i)==index) return b.getInt("sector",i);
        return -1;
    }
    
    public void makePlot2D(Bank dc, Bank cl){
        TGCanvas c = new TGCanvas(800,800);
        H2F[] h = DriftChamberTools.getHistosSector(dc.getInt("sector"), dc.getInt("layer"), dc.getInt("component"));
        H1F[] h1 = this.getClusterPositions(1, cl);
        c.view().divide(2,6);
        for(int i = 0; i < h.length; i++){
            H2F hc = h[i].crop(0, 0, 112, 6);
            c.cd(i).draw(hc);
        }        
        for(int i = 0; i < h1.length; i++){
            c.cd(i+6).draw(h1[i]);
        }
        
    }
    
    public static GraphErrors getTrack(int charge){
        GraphErrors g = new GraphErrors();
        if(charge>0){
            g.addPoint( 3.5, 0);
            g.addPoint( 5, 1);
            g.addPoint( 8.5, 2);
            g.addPoint( 10, 3);
            g.addPoint(14.5, 4);
            g.addPoint(18, 5);
        } else {
            g.addPoint( 16.5, 0);
            g.addPoint( 16, 1);
            g.addPoint( 14.5, 2);
            g.addPoint( 14, 3);
            g.addPoint( 11.5, 4);
            g.addPoint( 10, 5);
        }
        g.attr().set("ms=15,mc=4,lc=4,lw=3");
        return g;
    }
    
    public static GraphErrors getRandom(int count){
        GraphErrors g = new GraphErrors();
        Random r = new Random();
        
        for(int i = 0; i < count; i++){
            double x = r.nextInt(24);
            double y = r.nextInt(6);
            if(y%2==0) x += 0.5;

            g.addPoint(x,y);
        }
        g.attr().set("ms=14,mc=3");
        return g;
    }
    
    public static GraphErrors getGrid(){
        GraphErrors g = new GraphErrors();
        for(int x = 0; x < 24; x++){
            for(int y = 0; y < 6; y++){
                double xc = x;
                if(y%2==0) xc+=0.5;
                g.addPoint(xc, y);
            }
        }
        g.attr().set("mt=9,ms=24,fc=0,lc=31");
        g.attr().setMarkerOutlineWidth(1);
        g.attr().setMarkerOutlineColor(31);
        g.attr().setMarkerColor(0);
        return g;
    }
    
    public static GraphErrors getGrid2(){
        GraphErrors g = new GraphErrors();
        double[] ypos = new double[]{0,1,3,4,6,7};
        for(int x = 0; x < 50; x++){
            for(int y = 0; y < 6; y++){
                double xc = x;
                if(y%2==0) xc+=0.5;
                g.addPoint(xc, ypos[y]);
            }
        }
        g.attr().set("mt=9,ms=24,fc=0,lc=31");
        g.attr().setMarkerOutlineWidth(1);
        g.attr().setMarkerOutlineColor(31);
        g.attr().setMarkerColor(0);
        return g;
    }
    
    public static void drawDenoise(){
        GraphErrors g = InstaRec.getRandom(60);
        GraphErrors gp = InstaRec.getTrack(1);
        GraphErrors gn = InstaRec.getTrack(-1);
        GraphErrors grid = InstaRec.getGrid();        
        
        TGCanvas c = new TGCanvas(800,500);
        c.draw(g,"P").draw(gp,"PLsame").draw(gn,"samePL");
        
        GraphErrors gp2 = InstaRec.getTrack(1);
        GraphErrors gn2 = InstaRec.getTrack(-1);
        
        gp2.attr().set("lc=2,mc=2");
        gn2.attr().set("lc=2,mc=2");
        TGCanvas c2 = new TGCanvas(800,500);
        c2.draw(g,"P").draw(gp2,"PLsame").draw(gn2,"samePL");
        

        TGCanvas c3 = new TGCanvas("c3",700,210);
        c3.draw(grid,"P").draw(g,"sameP").draw(gp,"samePL").draw(gn,"samePL");
        c3.region().set("al=n,ac=0,ml=0,mr=0,mt=0,mb=0");
        c3.region().axisLimitsY(-1, 6);
        c3.region().axisLimitsX(-0.5, 24);

    }
    public static GraphErrors track(double angle, double steep){
        double r = 1; 
        double step = 1;
        GraphErrors g = new GraphErrors();
        g.addPoint(0, 0);
        for(int i = 0; i < 6; i++){
        
            double x = (r + step*i) *Math.cos(Math.toRadians(angle+steep*i));
            double y = (r + step *i) *Math.sin(Math.toRadians(angle+steep*i));
            
            g.addPoint(x, y);
        }
        g.attr().set("mc=2,mo=2,ms=8,lw=2");
        return g;
    }
    
    public static GraphErrors track(double angle, double steep, double scale, double[] rpos){
        //double r = 1; 
        double step = 1;
        GraphErrors g = new GraphErrors();
        g.addPoint(0, 0);
        for(int i = 0; i < 6; i++){
            double r = rpos[i];
            double x = scale*(r ) *Math.cos(Math.toRadians(angle+steep*i));
            double y = scale*(r + step *i) *Math.sin(Math.toRadians(angle+steep*i));
            
            g.addPoint(x, y);
        }
        g.attr().set("mc=2,mo=2,ms=8,lw=2");
        return g;
    }
    public static GraphErrors outline(int r){
        GraphErrors g = new GraphErrors();
        for(int angle = 0; angle <= 360; angle +=10){
            double x = (r ) *Math.cos(Math.toRadians(angle));
            double y = (r ) *Math.sin(Math.toRadians(angle));
            g.addPoint(x, y);
        }
        g.attr().set("lc=31,lw=2");
        return g;
    }
    public static GraphErrors noise(int level){
        GraphErrors g = new GraphErrors();
        Random rn = new Random();
        for(int i = 0; i < level; i++){
            double     r = rn.nextInt(6)+1;
            double angle = rn.nextInt(360);
            double x = (r ) *Math.cos(Math.toRadians(angle));
            double y = (r ) *Math.sin(Math.toRadians(angle));
            
            g.addPoint(x, y);
        }
        g.attr().set("mc=3,mo=3,ms=12");
        return g;
    }
    
    public static void drawStages(int stage){
        TGCanvas c = new TGCanvas("dc_stages_"+stage, 300,350);
        //c.setName("dc_stages_"+stage);
        //c.setTitle("dc stages");
        GraphErrors gt[] = new GraphErrors[4];
        gt[0]= InstaRec.track(45, 5);
        gt[1] = InstaRec.track(210, -7);
        gt[2] = InstaRec.track(310, 8);
        gt[3] = InstaRec.track(260, -2);
        
        
        
        int noise = 250;
        if(stage>2) noise = 40;
        GraphErrors gn = InstaRec.noise(noise);
        
        for(int i = 1; i <7; i++){
            GraphErrors go = InstaRec.outline(i);
            c.draw(go,"Lsame");
        }
        gn.attr().set("mc=#FF9800,ms=12");
        c.draw(gn,"Psame");
        for(int i = 0; i < gt.length; i++){
            gt[i].attr().set("mc=3,mo=3,ms=12");
            c.draw(gt[i],"Psame");
        }

        String[] attr = new String[]{"lc=#5F8670,mc=#5F8670,mo=#5F8670",
            "lc=#3559E0,mc=#3559E0,mo=#3559E0","lc=#B80000,mc=#B80000,mo=#B80000","lc=6,mc=6"};
        int ntracks = stage;
        for(int k = 0; k < ntracks; k++){
            gt[k].attr().set(attr[k]);
            c.draw(gt[k],"PLsame");
        }
        
        c.region().axisLimitsX(-7, 7);
        c.region().axisLimitsY(-7, 7);
        c.region().set("an=n,ac=0,mr=0,ml=0,mt=0,mb=0");
        c.repaint();
    }
    
    public static void CLASLogo(){
        
        TGCanvas c = new TGCanvas("clas12_logo",600,650);
        DriftChamberTools tools = new DriftChamberTools();
        List<Polygon> dc = tools.getBoundaries();
        GraphErrors gt[] = new GraphErrors[3];
        double[] pos1 = new double[]{44,59,89,105,138,157};
        double[] pos2 = new double[]{47,61,94,108,140,152};
        double[] pos3 = new double[]{47,60,90,104,135,150};
        
        for(int i = 0; i < dc.size(); i++){
            //dc.get(i).attrFill().setFillColor(1);
            //dc.get(i).attrLine().setLineColor(1);
        }
        
        gt[0]= InstaRec.track(30, 4,1.0,pos1);
        gt[1] = InstaRec.track(310, 1,1, pos2);
        gt[2] = InstaRec.track(170, -5,1, pos3);
        
        gt[0].attr().set("ms=12,mc=5,lc=5");
        gt[1].attr().set("ms=12");
        gt[2].attr().set("ms=12");
        
        c.region().draw(gt[0],"samePL").draw(gt[1],"samePL").draw(gt[2],"samePL").draw(dc);
        c.region().axisLimitsX(-180, 180);
        c.region().axisLimitsY(-180, 180);
        c.repaint();
    }
    public static void drawEvent(){
        String file = "/Users/gavalian/Work/Software/project-10.8/study/instarec/cooked_data.h5";
        HipoReader r = new HipoReader(file);
        
        Bank[] b = r.getBanks("MLTR::Clusters","MLTR::Tracks","DC::tdc","REC::Track");
        Event event = new Event();
        r.getEvent(event, 2); // 15 is good
        
        event.read(b);
        
        InstaRec ir = new InstaRec();
        ir.makePlot(b[0], b[1], b[2],b[3]);
    }
    
    public static void drawRegression(){
        GraphErrors grid = InstaRec.getGrid2();
        
        TGCanvas c3 = new TGCanvas("c3",700,280);
        c3.draw(grid,"P");
        // 0.7 Gev particle, theta = 12 deg, phi = 60 degrees
        //c3.draw(gr0p7,"samePL").draw(gr6p3,"samePL").draw(gr5p0,"samePL");
        c3.region().set("al=n,ac=0,ml=0,mr=0,mt=0,mb=0");
        c3.region().axisLimitsY(-1, 8);
        c3.region().axisLimitsX(-0.5, 50);
    }
    
    
    public static void main(String[] args){
        //InstaRec.drawEvent();
        
        //ir.makePlot2D(b[2],b[0]);
        
        //InstaRec.drawDenoise();
        InstaRec.drawRegression();
       // InstaRec.drawStages(1);
       // InstaRec.drawStages(2);
       // InstaRec.drawStages(3);
       // InstaRec.drawStages(4);
        
        //InstaRec.CLASLogo();
    }
}
