/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.data.base;

/**
 * 
 * @author gavalian
 */
public abstract class DataActor extends Thread {
    
    private DataSource     dataSource = null;
    private DataSync         dataSync = null;
    private DataFrame<DataEvent>    dataFrame = null;
    
    private long startTime = 0L;
    private long   endTime = 0L;
    
    public DataActor(){}
    
    public DataActor setSource(DataSource src){ dataSource =  src; return this;}
    public DataActor setSync(DataSync sync){      dataSync = sync; return this;}
    public DataActor setDataFrame(DataFrame frame) { dataFrame = frame; return this;}
    public long      executionTime(){ return (endTime-startTime);}
    
    @Override
    public void run(){
        startTime = System.currentTimeMillis();
        int size = dataFrame.getCount();
        int nReceived = size;
        while(nReceived==size){
            nReceived = dataSource.nextFrame(dataFrame);
            for(int k = 0; k < nReceived; k++){
                try{
                    this.accept(dataFrame.getEvent(k));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            if(this.dataSync!=null) dataSync.addFrame(dataFrame);
        }
        endTime = System.currentTimeMillis();
    }
    
    public abstract void accept(DataEvent event);
    
}
