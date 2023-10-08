/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.neural.monitor;

import j4np.data.base.DataFrame;
import j4np.data.base.DataSource;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import j4np.hipo5.io.HipoReader;
import j4np.neural.data.DataNodes;
import j4np.neural.finder.NeuralTrackFinder;
import j4np.physics.Vector3;
import j4np.physics.store.ReactionLambdaX;
import j4np.physics.store.ReactionPionX;
import j4np.physics.store.ReactionProtonX;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import twig.config.TPalette2D;
import twig.config.TStyle;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.TDirectory;
import twig.graphics.TTabCanvas;
import twig.tree.TreeCut;

/**
 *
 * @author gavalian
 */
public class DecoderMonitor {
    
    private NeuralTrackFinder tFinder = null; 
    
    
    Map<Integer,Integer>  topologyMap =  new HashMap<>();
    String[] topologyNames = new String[]{
        "1-","1+","1-1+","1-1-","1+1+", 
        "2-1+","2+1-","2+:2-","2:2-:X+:X-"};
    
    TDirectory      dir = new TDirectory();
    List<H2F>    list2D = new ArrayList<>();
    
    
    List<H2F>      dcCL = new ArrayList<>();
    
    List<H1F>   hTracksPos = new ArrayList<>();
    List<H1F>   hTracksNeg = new ArrayList<>();
    
    List<H1F>   hTracksMomPos = new ArrayList<>();
    List<H1F>   hTracksMomNeg = new ArrayList<>();
    
    List<H1F>   hTracksVertex = new ArrayList<>();
    
    List<H2F>   hTracksThetaPhi = new ArrayList<>();
   
    
    List<CompositeNode> nodes = new ArrayList<>();
    
    H1F         hTopology = null;
    
    int sleepDelay = 400;
    int eventFrameSize = 50;
    
    DataSource src = null;
    
    DataFrame<Event>  eventFrame = new DataFrame();
    
    private JPanel controls = null;
    
    
    int debugMode = 1;
    
    
    List<H1F>   hPhysics = new ArrayList<>();
    List<TreeCut>  cutsList = new ArrayList<>();
    
    ReactionPionX pionX   = new ReactionPionX("11:211:11:X+:X-:Xn",10.57);
    ReactionProtonX protonX = new ReactionProtonX("11:211:11:X+:X-:Xn",10.57);
    ReactionLambdaX lambdaX = new ReactionLambdaX("11:211:11:X+:X-:Xn",10.57);
        
    public DecoderMonitor(){
        initUI();
        initialize();
        TStyle.getInstance().getPalette().palette2d().setPalette(TPalette2D.PaletteName.kBird);
    }

    public void initTrackFinder(int run){
        tFinder = new NeuralTrackFinder();
        tFinder.setRun(run);
        tFinder.loadNetwork();
    }
    
    public void setDelay(int sleep){ this.sleepDelay = sleep;}
    public void setDebugMode(int mode){this.debugMode = mode;}
    public void initUI(){
        
        controls = new JPanel();
        controls.setLayout(new FlowLayout());
        
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e) {
                dir.reset();
            }
        
        });
        controls.add(reset);
    }
    
    private void initFrames(int size){
        eventFrame.reset();
        for(int i = 0; i < size; i++) eventFrame.addEvent(new Event());
    } 
    
    public void setFrameSize(int size){ eventFrameSize = size;}
    
    protected final void initialize(){
        
        
        
        this.hPhysics.add(new H1F("hMx2",120,-0.5,1.0));
        this.hPhysics.add(new H1F("hMx2cut",120,-0.5,1.0));
        this.hPhysics.add(new H1F("hRhoX",100,0.,4.4));
        
        this.hPhysics.add(new H1F("hRhoInv",120,0.2,2.5));
        this.hPhysics.add(new H1F("hPpimInv",60,1.,1.4));
        
        this.hPhysics.get(1).attr().set("fc=147");
        this.hPhysics.get(2).attr().set("fc=171");
        this.hPhysics.get(3).attr().set("fc=174");
        this.hPhysics.get(4).attr().set("fc=177");
        
        this.hPhysics.get(0).attr().setTitleX("M^2x(e#pi^-p) [GeV]");
        this.hPhysics.get(2).attr().setTitleX("Mx(e^-p) [GeV]");
        this.hPhysics.get(3).attr().setTitleX("Invariant Mass (#pi^+#pi^-) [GeV]");
        this.hPhysics.get(4).attr().setTitleX("Invariant Mass (p#pi^-) [GeV]");
        
        this.cutsList.add(new TreeCut("c1","rho>0.5&&rho<1.35&&mx2>-0.5&&mx2<1.8&&n1p>0.5&&n2p>1.5&&p1p>1",this.pionX.getBranches()));
        this.cutsList.add(new TreeCut("c2","rho>0.5&&rho<1.35&&mx2>-0.1&&mx2<0.2&&n1p>0.5&&n2p>1.5&&p1p>1",this.pionX.getBranches()));
        this.cutsList.add(new TreeCut("c3","mx2>-0.1&&mx2<0.3&&n1p>0.5&&n2p>0.5&&p1p>1",this.pionX.getBranches()));
        
        
        this.cutsList.add(new TreeCut("cp1","rho>0&&rho<2.8&&n1p>2.0&&n2p<3&&n2p>0.5&&p1p<3.2&&p1p>0.5", this.protonX.getBranches()));
        
        
        this.cutsList.add(new TreeCut("cl1","mx2>0.4&&mx2<0.7&&n1p>2&&n2p<1&&p1p>1", this.lambdaX.getBranches()));
        
        for(int s = 1; s <=6; s++){
            H2F dc = new H2F("occupancydc_s"+s,"DC occupancy:layer:wire",
                    36,0.5,36.5,112,0.5,112.5);
            H2F ec = new H2F("occupancyec_s"+s,"EC occupancy:layer:strip",
                    9,0.5,9.5,78,0.5,78.5);
            
            dc.attr().setTitleX("Layer (S"+s+")");
            ec.attr().setTitleX("Layer (S"+s+")");
            
            dc.attr().setTitleY("Wire");
            ec.attr().setTitleY("Strip");
            
            dir.add("decoder/dc", dc);
            dir.add("decoder/ec", ec);
            list2D.add(dc);
            list2D.add(ec);
            
            
        }
        
        
        for(int s = 1; s <=6 ; s++){
            H2F hcl = new H2F("clusters_s"+s, 6 , 0.5,6.5, 112,0.5,112.5);
            H1F trqp = new H1F("trackquality_pos_s"+s, 100, 0.0,1.0);
            H1F trqn = new H1F("trackquality_neg_s"+s, 100, 0.0,1.0);
            H1F ppp = new H1F("partmom_pos_s"+s,60,0.0,10.0);
            H1F ppn = new H1F("partmom_neg_s"+s,60,0.0,10.0);
            hcl.attr().setTitleX("superlayer");
            hcl.attr().setTitleY("wire");
            
            trqn.attr().set("fc=155,lc=5");
            trqp.attr().set("fc=152,lc=2");
            trqn.attr().setTitleX("AI track id quality (sector " + s +")");
            trqp.attr().setTitleX("AI track id quality (sector " + s +")");
            trqp.attr().setLegend("positive particles");
            trqn.attr().setLegend("negative particles");
            
            ppp.attr().set("fc=157,lc=7");
            ppn.attr().set("fc=153,lc=3");
            ppp.attr().setTitleX("track momentum [GeV] (sector " + s +")");
            ppn.attr().setTitleX("track momentum [GeV] (sector " + s +")");
            ppp.attr().setLegend("positive particles");
            ppn.attr().setLegend("negative particles");
            
            H1F hvrt = new H1F("vertex_s_"+s,120,-20,10);
            
            hvrt.attr().set("fc=144,lc=4");
            hvrt.attr().setTitleX("Z vertex (sector " + s +") [cm]");
            hvrt.attr().setLegend("Vertex Z Sector " + s);
            
            dir.add("tracking/clusters",hcl);
            dir.add("tracking/tracks",trqp);
            dir.add("tracking/tracks",trqn);
            dir.add("tracking/particles",ppp);
            dir.add("tracking/particles",hvrt);
            dir.add("tracking/particles",ppn);
            
            hTracksVertex.add(hvrt);
            this.dcCL.add(hcl);
            this.hTracksNeg.add(trqn);
            this.hTracksPos.add(trqp);
            this.hTracksMomNeg.add(ppn);
            this.hTracksMomPos.add(ppp);
            
            topologyMap.put( 0, -1);
            topologyMap.put( 1, 1);
            topologyMap.put(10, 2);
            topologyMap.put(11, 3);
            topologyMap.put(12, 4);
            topologyMap.put( 2, 5);
            topologyMap.put(20, 6);
            topologyMap.put(21, 7);
            topologyMap.put(22, 8);    
            topologyMap.put(33, 9);

            
        }
        
        
        hTopology = new H1F("topology",18,0.5,18.5);
        hTopology.attr().set("lw=25,lc=3");
        
        dir.add("tracking/physics", hTopology);
        
        H2F h1 = new H2F("angles_charge_pos",120, -180,180, 45, 0.0, 45.0);
        h1.attr().setTitleX("#phi [deg] (positive particles)");
        h1.attr().setTitleY("#theta [deg]");

        H2F h2 = new H2F("angles_charge_neg",120, -180,180, 45, 0.0, 45.0);
        h2.attr().setTitleX("#phi [deg] (negative particles)");
        h2.attr().setTitleY("#theta [deg]");
        
        this.hTracksThetaPhi.add(h1);
        this.hTracksThetaPhi.add(h2);
        dir.add("tracking/particles",h1);
        dir.add("tracking/particles",h2);
        
        CompositeNode n1 = DataNodes.getNodeDC();
        CompositeNode n2 = DataNodes.getNodeEC();
        CompositeNode n3 = DataNodes.getNodeClusters();
        CompositeNode n4 = DataNodes.getNodeTracks();
        CompositeNode n5 = DataNodes.getNodeParticles();
        nodes.add(n1);
        nodes.add(n2);
        nodes.add(n3);
        nodes.add(n4);
        nodes.add(n5);
    }
    
    public int getTopologyBin(int cpos, int cneg){
        int key = cpos*10+cneg;
        if(topologyMap.containsKey(key)==true) return topologyMap.get(key);
        return -1;
    }
    
    protected void fillDetector(CompositeNode node, List<H2F> list, int offset){
        int nrows = node.getRows();        
        for(int i = 0; i < nrows; i++){
            int sector = node.getInt(0, i);
            int layer = node.getInt(1, i);
            int component = node.getInt(2, i);
            int index = (sector-1)*2+offset;
            list.get(index).fill(layer, component);
        }
    }
    
    public void fillClusters(Event e){
        e.read(nodes.get(2),nodes.get(2).getGroup(),nodes.get(2).getItem());
        if(debugMode>0) System.out.println("::::: loaded clusters bank with rows : " + nodes.get(2).getRows());
        CompositeNode n = nodes.get(2);
        //n.print();
       
        for(int r = 0; r < n.getRows(); r++){
            int sector = n.getInt(1, r);
            this.dcCL.get(sector-1).fill(n.getInt(2, r), n.getDouble(3,r));
        }
    }
    
    public void fillTracks(Event e){
        e.read(nodes.get(3),nodes.get(3).getGroup(),nodes.get(3).getItem());
        if(debugMode>0) System.out.println("::::: loaded tracks bank with rows : " + nodes.get(3).getRows());
        CompositeNode n = nodes.get(3);
        
        for(int r = 0; r < n.getRows(); r++){
            int    sector = n.getInt(1, r);
            int    charge = n.getInt(2, r);
            double   prob = n.getDouble(3, r);
            if(charge<0){
                this.hTracksNeg.get(sector-1).fill(prob);
            } else {
                this.hTracksPos.get(sector-1).fill(prob);
            }
        }
    }
    
    public void fillParticles(Event e){
        e.read(nodes.get(4),nodes.get(4).getGroup(),nodes.get(4).getItem());
        if(debugMode>0) System.out.println("::::: loaded particles bank with rows : " + nodes.get(4).getRows());
        CompositeNode n = nodes.get(4);
        int count_pos = 0;
        int count_neg = 0;
        Vector3 vec = new Vector3();        
        for(int r = 0; r < n.getRows(); r++){
            int    sector = n.getInt(2, r);
            int    charge = n.getInt(1, r);
            vec.setXYZ(n.getDouble(4, r), n.getDouble(5,r), n.getDouble(6,r) );
            if(charge<0){
                this.hTracksMomNeg.get(sector-1).fill(vec.mag());
                this.hTracksThetaPhi.get(1).fill(
                        Math.toDegrees(vec.phi()), 
                        Math.toDegrees(vec.theta())
                        );
                if(sector==1){
                    this.hTracksVertex.get(0).fill(n.getDouble(9,r)*20.0-15.0);
                }
                count_neg++;
            } else {                
                this.hTracksMomPos.get(sector-1).fill(vec.mag());
                this.hTracksThetaPhi.get(0).fill(
                        Math.toDegrees(vec.phi()), 
                        Math.toDegrees(vec.theta())
                        );
                count_pos++;
            }
        }
        
        if(count_pos>3||count_neg>3) { count_pos=3; count_neg = 3;}
        //if(count_neg>3) count_neg=3;
        
        int chargebin = this.getTopologyBin(count_pos, count_neg);
        if(chargebin>0){
            int index = (chargebin-1)*2;
            this.hTopology.fill(index);
        }
    }
    
    
    public void fillEvent(Event e){
        for(int i = 0; i < this.nodes.size(); i++){
            e.read(nodes.get(i),nodes.get(i).getGroup(),nodes.get(i).getItem());            
        }
        
        this.fillDetector(nodes.get(0), list2D, 0);
        this.fillDetector(nodes.get(1), list2D, 1);
        
    }
    
    public void setSource(DataSource ds){ src = ds;}
    
    
    public void fillPhysics(Event e){
        try {
            //e.scanShow();
            this.pionX.read(e);
            this.protonX.read(e);
            this.lambdaX.read(e);
            if(this.cutsList.get(0).isValid(pionX)>0.5){
                this.hPhysics.get(0).fill(pionX.getValue("mx2"));
            }
            if(this.cutsList.get(1).isValid(pionX)>0.5){
                this.hPhysics.get(1).fill(pionX.getValue("mx2"));
            }
            if(this.cutsList.get(2).isValid(pionX)>0.5){
                this.hPhysics.get(2).fill(pionX.getValue("rho"));
            }
            
            if(this.cutsList.get(3).isValid(protonX)>0.5){
                this.hPhysics.get(3).fill(protonX.getValue("rho"));
            }
            
            if(this.cutsList.get(4).isValid(lambdaX)>0.5){
                //System.out.println(" lambda value = " + lambdaX.getValue("lam"));
                this.hPhysics.get(4).fill(lambdaX.getValue("lam"));
            }
            
        } catch (Exception ex){
            //System.out.println("oooops");
        }
    }
    
    public void draw(){
        
        TTabCanvas canvas = new TTabCanvas(controls,1400,800);
        
        canvas.setTitle("արհեստական ​​բանականություն");
        canvas.getDataCanvas().addCanvas("DC", true);
        canvas.getDataCanvas().addCanvas("EC", false);
        canvas.getDataCanvas().addCanvas("CLUSTERS", false);
        canvas.getDataCanvas().addCanvas("TRACKS", false);        
        canvas.getDataCanvas().addCanvas("MOM", false);
        canvas.getDataCanvas().addCanvas("ANGLE", false);
        canvas.getDataCanvas().addCanvas("TOPOLOGY", false);
        canvas.getDataCanvas().addCanvas("VERTEX", false);
        canvas.getDataCanvas().addCanvas("PHYSICS", false);
        
        for(int k = 0; k < 4; k++)
            canvas.getDataCanvas().getCanvases().get(k).divide(6, 1);
        //canvas.getDataCanvas().getCanvases().get(2).divide(6, 1);
        for(int k = 4; k < 6; k++)
            canvas.getDataCanvas().getCanvases().get(k).divide(2, 3);
                        
        canvas.getDataCanvas().getCanvases().get(6).divide(1, 2);
        canvas.getDataCanvas().getCanvases().get(7).divide(2, 2);
        canvas.getDataCanvas().getCanvases().get(8).divide(2, 3);
        canvas.getDataCanvas().getCanvases().get(9).divide(2, 2);
        
        for(int i = 0; i < 6; i++){
            canvas.getDataCanvas().getCanvases().get(1).region(i).draw(list2D.get(i*2));
            canvas.getDataCanvas().getCanvases().get(2).region(i).draw(list2D.get(i*2+1));
            canvas.getDataCanvas().getCanvases().get(3).region(i).draw(this.dcCL.get(i));
            
            canvas.getDataCanvas().getCanvases().get(4).region(i).draw(this.hTracksNeg.get(i));
            canvas.getDataCanvas().getCanvases().get(4).region(i).draw(this.hTracksPos.get(i),"same");
            canvas.getDataCanvas().getCanvases().get(4).region(i).showLegend(0.05, 0.98);
            canvas.getDataCanvas().getCanvases().get(4).region(i).getAxisFrame().setLogY(true);
            canvas.getDataCanvas().getCanvases().get(4).region(i).getAxisFrame().setBackgroundColor(235, 235, 235);
            
            canvas.getDataCanvas().getCanvases().get(5).region(i).draw(this.hTracksMomPos.get(i));
            canvas.getDataCanvas().getCanvases().get(5).region(i).draw(this.hTracksMomNeg.get(i),"same");
            canvas.getDataCanvas().getCanvases().get(5).region(i).showLegend(0.05, 0.98);
            canvas.getDataCanvas().getCanvases().get(5).region(i).getAxisFrame().setBackgroundColor(235, 235, 235);
            
            canvas.getDataCanvas().getCanvases().get(8).region(i).draw(this.hTracksVertex.get(i));
            //canvas.getDataCanvas().getCanvases().get(6).region(i).draw(this.hTracksMomNeg.get(i));
            
        }
        
        canvas.getDataCanvas().getCanvases().get(6).region(0).draw(this.hTracksThetaPhi.get(0));
        canvas.getDataCanvas().getCanvases().get(6).region(1).draw(this.hTracksThetaPhi.get(1));
        
        canvas.getDataCanvas().getCanvases().get(7).region(0).draw(this.hTopology,"BR");
        canvas.getDataCanvas().getCanvases().get(7).region(0).axisY().getAttributes().setAxisTicksPosition(
        Arrays.asList(1.0,3.0,5.0,7.0,9.0,
                11.0,13.0,15.0,17.0));
       
        canvas.getDataCanvas().getCanvases().get(7).region(0).axisY().getAttributes().setAxisTicksString(
                Arrays.asList(topologyNames));
        canvas.getDataCanvas().getCanvases().get(7).region(0).getInsets().left(120);
        canvas.getDataCanvas().getCanvases().get(7).region(0).getAxisFrame().setBackgroundColor(210, 210, 210);
        

        
        canvas.getDataCanvas().getCanvases().get(9).region(0).draw(this.hPhysics.get(0));
        canvas.getDataCanvas().getCanvases().get(9).region(0).draw(this.hPhysics.get(1),"same");
        canvas.getDataCanvas().getCanvases().get(9).region(1).draw(this.hPhysics.get(2));
        canvas.getDataCanvas().getCanvases().get(9).region(2).draw(this.hPhysics.get(3));
        canvas.getDataCanvas().getCanvases().get(9).region(3).draw(this.hPhysics.get(4));
        
        
        for(int j = 0; j < canvas.getDataCanvas().getCanvases().size(); j++){
            canvas.getDataCanvas().getCanvases().get(j).initTimer(2000+500);
        }
        
        
    }
    
    public void processSource(){
        this.initFrames(this.eventFrameSize);
        
        this.draw();
        int counter = 0;
        while(src.hasNext()==true){
            src.nextFrame(eventFrame);
           
            for(int i = 0; i < eventFrame.getCount(); i++){
                Event evnt = (Event) eventFrame.getEvent(i);
                if(tFinder!=null){
                    tFinder.processEvent(evnt);
                }
                                
                this.fillEvent(evnt);
                this.fillClusters(evnt);
                this.fillTracks(evnt);
                this.fillParticles(evnt);
                this.fillPhysics(evnt);
            }
            
            try {
                Thread.sleep(sleepDelay);
            } catch (InterruptedException ex) {
                Logger.getLogger(DecoderMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
    }
    
    public static void main(String[] args){
        //String file = "/Users/gavalian/Work/Software/project-10.7/distribution/caos/coda/decoder/clas_pin_018302.evio.00001_dc.h5";
        String file = "/Users/gavalian/Work/Software/project-10.7/study/rgd/airec_018312_0.h5";
        HipoReader r = new HipoReader(file);
        
        DecoderMonitor mon = new DecoderMonitor();
        mon.setFrameSize(200);
        mon.setDelay(0);
        mon.setDebugMode(0);

        mon.initTrackFinder(27);
        mon.setSource(r);
        mon.processSource();
    }
}
