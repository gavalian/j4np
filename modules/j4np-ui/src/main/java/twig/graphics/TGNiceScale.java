package twig.graphics;

import java.util.ArrayList;
import java.util.List;

/**
 * NiceScale class provides nice labels calculations
 * for graphics axis.
 * @author gavalian
 */

public class TGNiceScale {
    
    private static int[] powers = new int[]{1,10,100,1000,10000,100000,1000000,10000000};
    
    private double minPoint;
    private double maxPoint;
    private double maxTicks = 10;
    private double tickSpacing;
    private double range;
    private double niceMin;
    private double niceMax;
    private String orderString = "%.2f";
    
    /**
     * Instantiates a new instance of the NiceScale class.
     *
     * @param min the minimum data point on the axis
     * @param max the maximum data point on the axis
     */
    public TGNiceScale(double min, double max) {
        this.minPoint = min;
        this.maxPoint = max;
        calculate();
    }
    /**
     * Calculate and update values for tick spacing and nice
     * minimum and maximum data points on the axis.
     */
    private void calculate() {
        this.range = niceNum(maxPoint - minPoint, false);
        this.tickSpacing = niceNum(range / (maxTicks - 1), true);
        this.niceMin =
                Math.floor(minPoint / tickSpacing) * tickSpacing;
        this.niceMax =
                Math.ceil(maxPoint / tickSpacing) * tickSpacing;
    }
    
    public double getSpacing(){
        return this.tickSpacing;
    }
    /**
     * Returns a "nice" number approximately equal to range Rounds
     * the number if round = true Takes the ceiling if round = false.
     *
     * @param range the data range
     * @param round whether to round the result
     * @return a "nice" number to be used for the data range
     */
    private double niceNum(double range, boolean round) {
        double exponent; /** exponent of range */
        double fraction; /** fractional part of range */
        double niceFraction; /** nice, rounded fraction */
        
        exponent = Math.floor(Math.log10(range));
        fraction = range / Math.pow(10, exponent);
        
        if (round) {
            if (fraction < 1.5)
                niceFraction = 1;
            else if (fraction < 3)
                niceFraction = 2;
            else if (fraction < 7)
                niceFraction = 5;
            else
                niceFraction = 10;
        } else {
            if (fraction <= 1)
                niceFraction = 1;
            else if (fraction <= 2)
                niceFraction = 2;
            else if (fraction <= 5)
                niceFraction = 5;
            else
                niceFraction = 10;
        }
        
        return niceFraction * Math.pow(10, exponent);
    }
    /**
     * Sets the minimum and maximum data points for the axis.
     *
     * @param minPoint the minimum data point on the axis
     * @param maxPoint the maximum data point on the axis
     */
    public void setMinMaxPoints(double minPoint, double maxPoint) {
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        calculate();
    }
    
    /**
     * Sets maximum number of tick marks we're comfortable with
     *
     * @param maxTicks the maximum number of tick marks for the axis
     */
    public void setMaxTicks(double maxTicks) {
        this.maxTicks = maxTicks;
        calculate();
    }
    
    public void getTicks(List<Double> ticks, List<String> texts){
        ticks.clear(); texts.clear();
        this.setOrderString();
        String format = this.getOrderString();

        for(int i = 0 ; i < this.maxTicks+5; i++){
            //double value = this.minPoint + i*this.tickSpacing;
            double value = niceMin + i*this.tickSpacing;
            if(value>=this.minPoint&&value<=this.maxPoint){
                ticks.add(value);
                texts.add(String.format(format, value));
            }
        }
    }
    
    public void getMinorTicks(List<Double> ticks){
        ticks.clear(); 
        this.setOrderString();
        String format = this.getOrderString();
        
        for(int i = 0 ; i < this.maxTicks+5; i++){
            //double value = this.minPoint + i*this.tickSpacing;
            double value = niceMin + i*this.tickSpacing - this.tickSpacing*0.5;
            if(value>=this.minPoint&&value<=this.maxPoint){
                ticks.add(value);
                //texts.add(String.format(format, value));
            }
        }
    }
    
    public void getTicksLog(List<Double> ticks, List<String> texts){
        ticks.clear(); texts.clear();
        
        double maxpoint = this.maxPoint;
        double minpoint = this.minPoint;
        if(minpoint<0.0000000001) minpoint = 0.001;
        
        int maxPower = (int) Math.log10(maxpoint);
        int minPower = (int) Math.log10(minpoint);
        int  expstep = 1;
        if( (maxPower - minPower) > this.maxTicks) {
            expstep = (int) (Math.abs(maxPower-minPower)/this.maxTicks) + 1;
            //System.out.println("--- let's readjust this thing..... exstep = " + expstep);  
            //System.out.println(Math.abs(maxPower-minPower) + "  " + this.maxTicks);
        }
        //System.out.println(" max = " + maxPower + " min power = " + minPower);
        for(int power = maxPower; power >= minPower; power-= expstep){            
            double value = Math.pow(10, power);
            if(value>this.minPoint&&value<this.maxPoint){
                ticks.add(Math.pow(10, power));
                if(power>=0)
                    texts.add(String.format("10^%d", power));
                else texts.add(String.format("10^-^%d", Math.abs(power)));
            }
        }
        
    }
    
    public void getTicks(List<Double> ticks){
        ticks.clear();
        for(int i = 0 ; i < this.maxTicks+5; i++){
            //double value = this.minPoint + i*this.tickSpacing;
            double value = niceMin + i*this.tickSpacing;
            if(value>=this.minPoint&&value<=this.maxPoint)
                ticks.add(value);
        }
    }
    public int getNumberOrder(double num){
        
        if(num<10e-32) return 0;
        
        for(int i = 0; i < TGNiceScale.powers.length; i++){
            int data = ( (int) (num*TGNiceScale.powers[i]));
            int tail = data%10;
            if(data!=0&&tail==0&&i==0) return 0;
            if(data!=0&&tail==0)       return (i-1);
            /*System.out.printf("oreder = %3d, dive = %9d, data = %5d, tail = %5d\n",
                    i,NiceScale.powers[i],data,tail);*/
        }
        return -1;
    }
    
    public int getScaleOrder(){
        //System.out.println(" Nice Min = " + this.niceMin + " nice MAx = " + niceMax);
        //if(this.tickSpacing>0){ return 0;}
        for(int i = 0; i < TGNiceScale.powers.length; i++){
            int data = ( (int) (this.tickSpacing*TGNiceScale.powers[i]));
            int tail = data%10;
            if(data!=0&&tail==0) return (i-1);
            //System.out.printf("oreder = %3d, dive = %9d, data = %5d, tail = %5d\n",
            //        i,NiceScale.powers[i],data,tail);
        }
        return -1;
    }
    
    public static void getScaleOrder(double[] values){
        
    }
    public String getOrderString(){return this.orderString;}
    
    public void   setOrderString(){
        int order = this.getNumberOrder(this.tickSpacing);
        //System.out.println(" ORDER = " + order);
        if(order==0){
            int nminorder = getNumberOrder(niceMin);
            //System.out.println("ORDER MIN = " + niceMin + "  " + nminorder);
            if(nminorder==0){ 
                orderString = "%.0f";
                return;
            }
        }
        if(order>=0){
            orderString = "%."+order+"f";
        } else {
            orderString = "%e";
        }
        
    }
    
    public static void main(String[] args){
        
        
//        TGNiceScale  scale = new TGNiceScale(0.1678,0.28765);
        TGNiceScale  scale = new TGNiceScale(0., 1);
        
        List<Double>  ticks = new ArrayList<>();
        List<String>  texts = new ArrayList<>();
        scale.setMaxTicks(10);
        scale.getTicksLog(ticks,texts);
        
        for(int i = 0; i < ticks.size(); i++){
            System.out.printf("%4d : %f ? %s\n",i, ticks.get(i),texts.get(i));
        }
        
        
        
        /*
        TGNiceScale nice = new TGNiceScale(0,10000);
        nice.setMinMaxPoints(0, 100000);
        nice.setOrderString();
        
        System.out.println("Order String = " +  nice.getOrderString());
        
        System.out.println("spacing " + nice.getSpacing() + " ");
        System.out.println("order   " + nice.getScaleOrder());
        System.out.println("num order " + nice.getNumberOrder(100));
        List<Double> ticks = new ArrayList<Double>();
        nice.getTicks(ticks);
        for(int i = 0; i < ticks.size(); i++){
            System.out.println(" --- " + i + "  value = " + ticks.get(i));
        }
        
        
        for(int i = 0; i < 10 ; i++){
            double num = 100*Math.pow(10, -i);
            System.out.printf("%18.8f   -  %d\n",num,nice.getNumberOrder(num));
        }
        
        System.out.printf("%.0f\n",100000.0);
        
        
        System.out.println("----------------->>>");
        nice.setMinMaxPoints(0, 11744.739990);
        
        nice.setOrderString();
        System.out.println(nice.getOrderString());*/
    }
}
