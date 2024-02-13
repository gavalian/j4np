/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.clas12.et;

import j4np.data.base.DataFrame;
import j4np.hipo5.data.CompositeBank;
import j4np.hipo5.data.CompositeNode;
import j4np.hipo5.data.Event;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import twig.data.H1F;
import twig.server.HttpDataServer;
import twig.server.HttpServerConfig;

/**
 *
 * @author gavalian
 */
public class MonitorSourceEt  {
    
    Timer etTimer = null;
    DataFrame<Event> etEvents = new DataFrame<>();
    DataSourceEt etSource = null;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    HttpDataServer   httpServer = null;
    private H1F hdcbank ;//= new H1F("");
    CompositeNode cbank = new CompositeNode(120*1024);
    
    public MonitorSourceEt(String file, int interval){
        etSource = new DataSourceEt("localhost");
        etSource.open(file);
        this.initServer();
        this.initData();
        this.initTimer(interval);
    }
    
    public void initData(){
        hdcbank = new H1F("hdcdata",120,0.0,800);
        hdcbank.attr().set("fc=42,lc=2");
        hdcbank.attr().setTitleX("Number Rows in DC bank");
        hdcbank.attr().setTitleY("counts");
        hdcbank.attr().setLegend("DC bank row multiplicity");
        httpServer.getDirectory().add("/et/monitor", hdcbank);
    }
    
    private void initServer(){
	HttpServerConfig config = new HttpServerConfig();
        config.serverPort = 8525;
        
        HttpDataServer.create(config);
        HttpDataServer.getInstance().initDefault();
        HttpDataServer.getInstance().start();
    }
    
    private void initTimer(int interval){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                onTimerEvent();
                /*for(int i = 0; i < canvasPads.size();i++){
                    System.out.println("PAD = " + i);
                    canvasPads.get(i).show();
                }*/
            }
        };
        etTimer = new Timer("EmbeddeCanvasTimer");
        etTimer.scheduleAtFixedRate(timerTask, 30, interval);
    }
    
    private void onTimerEvent(){
        Date date = new Date();
        //System.out.printf("%s: receive buffer with %d events\n",
        //            dateFormat.format(date),etEvents.getCount());
        try {
            etSource.nextFrame(etEvents);
            int size = 0;
            System.out.printf("%s: receive buffer with %d events\n",
                    dateFormat.format(date),etEvents.getCount());
            for(int i = 0; i < etEvents.getCount(); i++){
                Event e = (Event) etEvents.getEvent(i);
                e.read(cbank, 42, 11);
                
                hdcbank.fill(cbank.getRows());
            }
        } catch (Exception e){
            System.out.printf("%s: error receiving events from et ring.....\n",
                    dateFormat.format(date));
        }
    }

    public static void main(String[] args){
	String file = args[0];
	Integer interval = Integer.parseInt(args[1]);
	MonitorSourceEt mon = new MonitorSourceEt(file,interval);
    }
}
