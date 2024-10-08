package twig.data;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import twig.config.TDataAttributes;
import twig.math.MultiIndex;


/**
 * Specifies the methods to create a 2D Histogram and operations to fill it and
 * set values to its bins
 * @author Gagik Gavalian
 * @author Erin Kirby
 * @version 061714
 */
public class H2F implements DataSet {

    private String hName = "basic2D";
    private Axis xAxis = new Axis();
    private Axis yAxis = new Axis();
    
    private long histogramUniqueID = 0L;
    
    protected double[] hBuffer;
    private MultiIndex offset;
    
    private TDataAttributes dataAttr = new TDataAttributes();

    private double     maximumBinValue = 0.0;
    private double     minimumBinValue = 0.0;
    private int             maximumBin = 0;
    private int             minimumBin = 0;
    
    public H2F() {
        offset = new MultiIndex(xAxis.getNBins(), yAxis.getNBins());
        hBuffer = new double[offset.getArraySize()];
        initAttributes();
    }
    
    @Override
    public void setName(String name){ this.hName = name;}
    /**
     * Creates an empty 2D Histogram with 1 bin x and y axes
     * 
     * @param name
     *            the desired name of the 2D Histogram
     */
    public H2F(String name) {
        hName = name;
        offset = new MultiIndex(xAxis.getNBins(), yAxis.getNBins());
        hBuffer = new double[offset.getArraySize()];
        initAttributes();
        
    }
    
    /**
     * Creates a 2D Histogram with the specified parameters.
     * 
     * @param name
     *            the name of the histogram
     * @param bx
     *            the number of x axis bins
     * @param xmin
     *            the minimum x axis value
     * @param xmax
     *            the maximum x axis value
     * @param by
     *            the number of y axis bins
     * @param ymin
     *            the minimum y axis value
     * @param ymax
     *            the maximum y axis value
     */
    public H2F(String name, int bx, double xmin, double xmax, int by,
            double ymin, double ymax) {
        hName = name;
        this.set(bx, xmin, xmax, by, ymin, ymax);
        offset = new MultiIndex(bx, by);
        hBuffer = new double[offset.getArraySize()];    
        initAttributes();
    }
    
    public H2F(String name, String title, int bx, double xmin, double xmax, int by,
            double ymin, double ymax) {
        
        hName = name;
        this.set(bx, xmin, xmax, by, ymin, ymax);
        offset = new MultiIndex(bx, by);
        hBuffer = new double[offset.getArraySize()];          
        initAttributes();
        this.setTitle(title);
    }
    
    public H2F(String name, double[] binsX, double[] binsY){
        xAxis.set(binsX);
        yAxis.set(binsY);
        int bx = xAxis.getNBins();
        int by = yAxis.getNBins();
        offset = new MultiIndex(bx, by);
        int buff = offset.getArraySize();
        hBuffer = new double[buff];
        this.initAttributes();
        this.hName = name;
    }
    
    public H2F(String name, String title, double[] binsX, double[] binsY){
        xAxis.set(binsX);
        yAxis.set(binsY);
        int bx = xAxis.getNBins();
        int by = yAxis.getNBins();
        offset = new MultiIndex(bx, by);
        int buff = offset.getArraySize();
        hBuffer = new double[buff];
        this.initAttributes();
        this.hName = name;
        attr().setTitle(title);
    }
    
    public H2F  setUniqueID(long id){ this.histogramUniqueID = id; return this;}
    public long getUniqueID(){ return this.histogramUniqueID;}
    
    private void initAttributes() {
        
    }
    /**
     * calculated the integral of the 2D histogram
     * @return integral
     */
    public double integral(){
        double summ = 0.0;
        for(int i = 0; i < hBuffer.length; i++){
            summ += hBuffer[i];
        }
        return summ;
    }
    /**
     * returns entries in the histogram
     * @return histogram entries
     */
    public int getEntries(){
        int entries = (int) integral();
        return entries;
    }
    /**
     * Sets the bins to the x and y axes and creates the buffer of the histogram
     * 
     * @param bx
     *            number of bins on the x axis
     * @param xmin
     *            the minimum value on the x axis
     * @param xmax
     *            the maximum value on the x axis
     * @param by
     *            number of bins on the y axis
     * @param ymin
     *            the minimum value on the y axis
     * @param ymax
     *            the maximum value on the y axis
     */
    public final void set(int bx, double xmin, double xmax, int by,
            double ymin, double ymax) {
        xAxis.set(bx, xmin, xmax);
        yAxis.set(by, ymin, ymax);
        offset = new MultiIndex(bx, by);
        int buff = offset.getArraySize();
        hBuffer = new double[buff];
    }
    
    /**
     *
     * @return the name of the Histogram
     */
    @Override
    public String getName() {
        return hName;
    }
    
    public Axis getAxisX(){ return this.xAxis;}
    public Axis getAxisY(){ return this.yAxis;}
    /**
     *
     * @return the x-axis of the 2D Histogram
     */
    public Axis getXAxis() {
        return xAxis;
    }
    
    public void normalize(double number){
        StatNumber  der = new StatNumber(number,0);
        StatNumber binc = new StatNumber();
        
        for(int i = 0; i < hBuffer.length; i++)
        {
            binc.set(hBuffer[i], hBuffer[i]);
            binc.divide(der);
            hBuffer[i] = binc.number();
            hBuffer[i] = binc.error();
        }
    }
    /**
     *
     * @return the y-axis of the 2D Histogram
     */
    public Axis getYAxis() {
        return yAxis;
    }
    
    public double getMaximum(){
        double maximum = 0.0;
        for(int loop = 0; loop < hBuffer.length; loop++)
            if(hBuffer[loop]>maximum) maximum = hBuffer[loop];
        return maximum;
    }
    
    /**
     * Checks if that bin is valid (exists)
     *
     * @param bx
     *            The x coordinate of the bin
     * @param by
     *            The y coordinate of the bin
     * @return The truth value of the validity of that bin
     */
    private boolean isValidBins(int bx, int by) {
        if ((bx >= 0) && (bx <= xAxis.getNBins()) && (by >= 0)
                && (by <= yAxis.getNBins())) {
            return true;
        }
        return false;
    }
    
    /**
     * Finds the bin content at that bin
     *
     * @param bx
     *            The x coordinate of the bin
     * @param by
     *            The y coordinate of the bin
     * @return The content at that bin
     */
    public double getBinContent(int bx, int by) {
        if (this.isValidBins(bx, by)) {
            int buff = offset.getArrayIndex(bx, by);
            if(buff>=0&&buff<hBuffer.length){
                return hBuffer[buff];
            } else {
                System.out.println("[Index] error for binx = "+ bx +
                        " biny = " + by);
            }
        }
        return 0.0;
    }
    
    public void modify(DataVector vecX, DataVector vecY, double xmin, double xmax, double ymin, double ymax){
        int nbinsX = xAxis.getNBins();
        int nbinsY = yAxis.getNBins();
        
        xAxis.set(nbinsX, xmin, xmax);
        yAxis.set(nbinsY, ymin, ymax);
        this.reset();
        for(int loop = 0; loop < vecX.getSize(); loop++){
            this.fill(vecX.getValue(loop),vecY.getValue(loop));
        }
    }
    /**
     * creates 2D histogram from given DataVectors, min and max are determined
     * by data vectors.
     * @param name name of 2D histogram
     * @param binsX number of bins in X
     * @param binsY number of bins in Y
     * @param vecX DataVector with x-points
     * @param vecY DataVector with y-points
     * @return histogram 2D
     */
    public static H2F create(String name, int binsX, int binsY, DataVector vecX, DataVector vecY){
        
        double minX = vecX.getMin();
        double maxX = vecX.getMax();
        double minY = vecY.getMin();
        double maxY = vecY.getMax();
        if(minX==maxX){
        	minX = .9999*minX;
        	maxX = 1.0001*maxX; 
        }
        if(minY==maxY){
        	minY = .9999*minY;
        	maxY = 1.0001*maxY; 
        }
        H2F h2 = new H2F(name,"",binsX,minX,maxX,binsY,minY,maxY);
        
        for(int i = 0; i < vecX.getSize(); i++){
            h2.fill(vecX.getValue(i),vecY.getValue(i));
        }
        return h2;
    }
 
    public static H2F create(int xBins, int yBins, float[] data){
        H2F h = new H2F("h2",xBins, 0.5, xBins+0.5,yBins, 0.5, yBins+0.5);
        for(int i = 0; i < h.hBuffer.length; i++) h.hBuffer[i] = data[i];
        return h;
    }
    
    public static H2F[] duplicate(int count, String title, int binsX, double minX, double maxX, int binsY, double minY, double maxY){
        H2F[] hc = new H2F[count];
        for(int loop = 0; loop < count; loop++) hc[loop] = new H2F(
                String.format("%s_%d",title,loop+1),binsX,minX,maxX,binsY,minY,maxY
        );
        return hc;
    }
    /**
     * divides the content of each bin in the histogram to the 
     * maximum bin.
     */
    public void unit(){
        float max = (float) this.getMaximum();
        if(max<0e-12){
            System.out.println("[H1F] ** error ** the histogram has very low maximum. can not be set to unit.");
            return;
        }
        for(int i = 0; i < this.hBuffer.length; i++){
            this.hBuffer[i] = this.hBuffer[i]/max;
            this.hBuffer[i] = this.hBuffer[i]/max;
        }
    }
    
    public int getDataBufferSize(){
        return this.hBuffer.length;
    }
    
    public float getDataBufferBin(int bin){
        return (float) this.hBuffer[bin];
    }
    
    public double getDataBufferBinAsDouble(int bin){
        return this.hBuffer[bin];
    }
    
    public void setDataBufferBin(int bin, double value){
        hBuffer[bin] = value;
    }
    /**
     * Sets the bin to that value
     *
     * @param bx
     *            The x coordinate of the bin
     * @param by
     *            The y coordinate of the bin
     * @param w
     *            The desired value to set the bin to
     */
    public void setBinContent(int bx, int by, double w) {
        if (this.isValidBins(bx, by)) {
            int buff = offset.getArrayIndex(bx, by);
            hBuffer[buff] = (float) w;
        }
    }
    
    /**
     * Adds 1.0 to the bin with that value
     *
     * @param x
     *            the x coordinate value
     * @param y
     *            the y coordinate value
     */
    public void fill(double x, double y) {
        int bin = this.findBin(x, y);
        if (bin >= 0)
            this.addBinContent(bin);
        
    }
    
    public void fill(DataVector vx, DataVector vy) {
        if(vx.getSize()!=vy.getSize()){
            System.out.println("[H1F::fill] ** error ** : --> vectors have different lengths.");
            return;
        }
        /*int bin = this.findBin(x, y);
        if (bin >= 0)
            this.addBinContent(bin);*/
        int nsize = vx.getSize();
        for(int loop = 0; loop < nsize; loop++){
            fill(vx.getValue(loop),vy.getValue(loop));
        }
    }
    public void fill(DataVector vx, DataVector vy, DataVector weight) {
        if(vx.getSize()!=vy.getSize()||vx.getSize()!=weight.getSize()){
            System.out.println("[H1F::fill] ** error ** : --> vectors have different lengths.");
            return;
        }
        
        /*int bin = this.findBin(x, y);
        if (bin >= 0)
            this.addBinContent(bin);*/
        int nsize = vx.getSize();
        for(int loop = 0; loop < nsize; loop++){
            fill(vx.getValue(loop),vy.getValue(loop),weight.getValue(loop));
        }
    }
    
    public void fill(double x, double y, double w) {
        int bin = this.findBin(x, y);
        if (bin >= 0) {
            this.addBinContent(bin, w);
        }
    }
    
    /**
     * Increments the current bin by 1.0
     *
     * @param bin
     *            the bin in array indexing format to increment
     */
    private void addBinContent(int bin) {
        hBuffer[bin] = (float) (hBuffer[bin] + 1.0);
        if(hBuffer[bin]>this.maximumBinValue)
            this.maximumBinValue = hBuffer[bin];
    }
    
    /**
     * Increments the bin with that value by that weight
     *
     * @param bin
     *            the bin to add the content to, in array indexing format
     * @param w
     *            the value to add to the bin content
     */
    private void addBinContent(int bin, double w) {
        hBuffer[bin] = (float) (hBuffer[bin] + w);
        if(w>0.0){
            if(hBuffer[bin]>this.maximumBinValue)
                this.maximumBinValue = hBuffer[bin];
        } else {
            if(hBuffer[bin]<this.maximumBinValue){
                this.minimumBinValue = hBuffer[bin];
            }
        }
    }
    public H2F crop(int startX, int startY, int binsX, int binsY){
        double minX = this.getAxisX().getBinLow(startX);
        double maxX = this.getAxisX().getBinHigh(startX+binsX);
        double minY = this.getAxisY().getBinLow(startY);
        double maxY = this.getAxisY().getBinHigh(startY+binsY);
        H2F h = new H2F(this.getName()+"_cropped",binsX, minX, maxX, binsY, minY, maxY);
        for(int x = 0; x < binsX; x++){
            for(int y = 0; y < binsY; y++){
                h.setBinContent(x, y, this.getBinContent(x+startX, y+startY));
            }
        }
        return h;
    }    
    
    public ArrayList<H1F>  getSlicesX(){
        return this.getSlicesX("");
    }
    public ArrayList<H1F>  getSlicesX(String attributes){
        ArrayList<H1F>  slices = new ArrayList<H1F>();
        for(int loop = 0; loop < this.getXAxis().getNBins(); loop++){
            H1F slice = this.sliceX(loop);
            slice.setName(this.getName()+"_"+loop);
            slice.attr().setTitleX(attr().getTitleY());
            slice.attr().set(attributes);
                        slice.setEntries((long) slice.integral());
            slices.add(slice);
        }
        return slices;
    }
    public ArrayList<H1F>  getSlicesY(){
        return this.getSlicesY("");
    }
    
    public ArrayList<H1F>  getSlicesY(String attributes){
        ArrayList<H1F>  slices = new ArrayList<H1F>();
        for(int loop = 0; loop < this.getYAxis().getNBins(); loop++){
            H1F slice = this.sliceY(loop);
            slice.setName(this.getName()+"_"+loop);
            slice.attr().setTitleX(attr().getTitleX());
            slice.attr().set(attributes);
            slice.setEntries((long) slice.integral());
            slices.add(slice);
        }
        return slices;
    }
    
    public void add(H2F h){
        if(h.getXAxis().getNBins()==this.getXAxis().getNBins()&&
                h.getYAxis().getNBins()==this.getYAxis().getNBins()){
            for(int loop = 0; loop < this.hBuffer.length; loop++){
                this.hBuffer[loop] = this.hBuffer[loop] + h.hBuffer[loop];
            }
        } else {
            System.out.println("[warning] ---> error adding histograms "
                    + this.getName() + "  " + h.getName()
                    + ". inconsistent bin numbers");
        }
    }    
    
    public static H2F  divide(H2F h1, H2F h2){
        if((h1.getXAxis().getNBins()!=h2.getXAxis().getNBins())||
                (h1.getYAxis().getNBins()!=h2.getYAxis().getNBins())
                ){
            System.out.println("[H2D::divide] error : histograms have inconsistent bins");
            return null;
        }
        
        H2F h2div = new H2F(h1.getName()+"_DIV",
                h1.getXAxis().getNBins(),h1.getXAxis().min(),h1.getXAxis().max(),
                h1.getYAxis().getNBins(),h1.getYAxis().min(),h1.getYAxis().max()
        );
        for(int bx = 0; bx < h1.getXAxis().getNBins();bx++){
            for(int by = 0; by < h1.getYAxis().getNBins();by++){
                if(h2.getBinContent(bx, by)!=0){
                    h2div.setBinContent(bx, by, h1.getBinContent(bx, by)/h2.getBinContent(bx, by));
                }
            }
        }
        return h2div;
    }
    
    public void discretize(double threshold){
        for(int r = 0; r < hBuffer.length; r++) 
            if(hBuffer[r]>threshold){
                hBuffer[r] = 1.0;
            } else { hBuffer[r] = 0.0;}
    }
    
    public void divide(H2F h){
        if(h.getXAxis().getNBins()==this.getXAxis().getNBins()&&
                h.getYAxis().getNBins()==this.getYAxis().getNBins()){
            for(int loop = 0; loop < this.hBuffer.length; loop++){
                if(h.hBuffer[loop]==0){
                    this.hBuffer[loop] = 0.0f;
                } else {
                    this.hBuffer[loop] = this.hBuffer[loop]/h.hBuffer[loop];
                }
            }
        } else {
            System.err.println("[H2D::divide] error the bins in 2d histogram do not match");
        }
    }
    /**
     * Finds which bin has that value.
     *
     * @param x
     *            The x value to search for
     * @param y
     *            The y value to search for
     * @return The bin, in array indexing format, which holds that x-y value
     */
    public int findBin(double x, double y) {
        int bx = xAxis.getBin(x);
        int by = yAxis.getBin(y);
        if (this.isValidBins(bx, by)) {
            return (offset.getArrayIndex(bx, by));
        }
        return -1;
    }
    public float[] getContentArrayFloat(){
        float[] data = new float[this.hBuffer.length];
        for(int i = 0; i < data.length; i++) data[i] = (float) this.hBuffer[i];
        return data;
    }
    /**
     * Generates a 2D array with the content in the histogram
     *
     * @return a 2D array with each bin in its array index
     */
    public double[][] getContentBuffer() {
        double[][] buff = new double[xAxis.getNBins()][yAxis.getNBins()];
        for (int xloop = 0; xloop < xAxis.getNBins(); xloop++) {
            for (int yloop = 0; yloop < yAxis.getNBins(); yloop++) {
                buff[xloop][yloop] = this.getBinContent(xloop, yloop);
            }
        }
        return buff;
    }
    
    /**
     * Creates an error buffer with each element being 0.0
     *
     * @return a double 2D array with a size of xAxis * yAxis with each element
     *         being 0.0
     */
    public double[][] getErrorBuffer() {
        double[][] buff = new double[xAxis.getNBins()][yAxis.getNBins()];
        for (int xloop = 0; xloop < xAxis.getNBins(); xloop++) {
            for (int yloop = 0; yloop < yAxis.getNBins(); yloop++) {
                buff[xloop][yloop] = 0.0;
            }
        }
        return buff;
    }
    
    /**
     * Specifies the region in the 2D histogram with those attributes
     *
     * @param name
     *            The name of the histogram
     * @param bx_start
     *            The x coordinate beginning
     * @param bx_end
     *            The x coordinate end
     * @param by_start
     *            The y coordinate beginning
     * @param by_end
     *            The y coordinate end
     * @return A 2D histogram with the entered specifications
     */
    public H2F getRegion(String name, int bx_start, int bx_end,
            int by_start, int by_end) {
        double xBinWidth = xAxis.getBinWidth(bx_start);
        double newXMin = xAxis.min() + (xBinWidth * bx_start);
        double newXMax = xAxis.min() + (xBinWidth * bx_end);
        
        double yBinWidth = yAxis.getBinWidth(by_start);
        double newYMin = yAxis.min() + (yBinWidth * by_start);
        double newYMax = yAxis.min() + (yBinWidth * by_end);
        H2F regHist = new H2F(name, bx_end - bx_start, newXMin,
                newXMax, by_end - by_start, newYMin, newYMax);
        
        double content = 0.0;
        for (int y = by_start; y < by_end; y++) {
            for (int x = bx_start; x < bx_end; x++) {
                content = this.getBinContent(x, y);
                regHist.setBinContent(x, y, content);
            }
        }
        return regHist;
    }
    
    public H2F histClone(String name){
        H2F hclone = new H2F(name,
                this.xAxis.getNBins(),this.xAxis.min(),this.xAxis.max(),
                this.yAxis.getNBins(),this.yAxis.min(),this.yAxis.max()
        );
        for(int loop = 0; loop < this.hBuffer.length; loop++){
            hclone.hBuffer[loop] = this.hBuffer[loop];
        }
        return hclone;
    }
    
    public GraphErrors  getProfileX(){
        GraphErrors graph = new GraphErrors();
        int nbinsX = this.getXAxis().getNBins();
        for(int loop = 0 ; loop < nbinsX; loop++){
            H1F h1 = this.sliceX(loop);
            double mean = h1.getMean();
            double rms  = h1.getRMS();
            //System.out.println("MEAN = " + mean + "  RMS = " + rms);
            double bincenter = this.getXAxis().getBinCenter(loop);
            if(h1.integral()>1.0){
                graph.addPoint(bincenter, mean, this.getXAxis().getBinWidth(loop)/2.0, rms);
            }
        }
        return graph;
    }
    
    public GraphErrors  getProfileY(){
        GraphErrors graph = new GraphErrors();
        int nbinsY = this.getYAxis().getNBins();
        for(int loop = 0 ; loop < nbinsY; loop++){
            H1F h1 = this.sliceY(loop);
            double mean = h1.getMean();
            double rms  = h1.getRMS();
            //System.out.println("MEAN = " + mean + "  RMS = " + rms);
            double bincenter = this.getYAxis().getBinCenter(loop);
            graph.addPoint(bincenter, mean, 0.0, rms);
        }
        return graph;
    }
    
    /**
     *
     * @param ngroups indicates how many bins of this have to be merged into one
     * bin of the result NOTE: If ngroup is not an exact divider of the number
     * of bins, the top limit of the rebinned histogram is reduced to the upper
     * edge of the last bin that can make a complete group.
     * 
     * @return a H2F object obtained from original histogram with rebinned
     * x-axis
     */
    public H2F rebinX(int ngroups) {
        int nbinsX = xAxis.getNBins() / ngroups;

        H2F hrebinX = new H2F(hName, attr().getTitle(),
                nbinsX, xAxis.min(), xAxis.min() + nbinsX * ngroups * xAxis.getBinWidth(0),
                yAxis.getNBins(), yAxis.min(), yAxis.max());

        for (int iby = 0; iby < yAxis.getNBins(); iby++) {
            for (int ibx = 0; ibx < nbinsX; ibx++) {
                double height = 0.0;
                for (int igroup = 0; igroup < ngroups; igroup++) {
                    height += this.getBinContent(ibx * ngroups + igroup, iby);
                }
                hrebinX.setBinContent(ibx, iby, height);
            }
        }
        return hrebinX;
    }

    /**
     *
     * @param ngroups indicates how many bins of this have to be merged into one
     * bin of the result NOTE: If ngroup is not an exact divider of the number
     * of bins, the top limit of the rebinned histogram is reduced to the upper
     * edge of the last bin that can make a complete group.
     * 
     * @return a H2F object obtained from original histogram with rebinned
     * y-axis
     */
    public H2F rebinY(int ngroups) {
        int nbinsY = yAxis.getNBins() / ngroups;

        H2F hrebinY = new H2F(hName, attr().getTitle(),
                xAxis.getNBins(), xAxis.min(), xAxis.max(),
                nbinsY, yAxis.min(), yAxis.min() + nbinsY * ngroups * yAxis.getBinWidth(0));

        for (int ibx = 0; ibx < xAxis.getNBins(); ibx++) {
            for (int iby = 0; iby < nbinsY; iby++) {
                double height = 0.0;
                for (int igroup = 0; igroup < ngroups; igroup++) {
                    height += this.getBinContent(ibx, iby * ngroups + igroup);
                }
                hrebinY.setBinContent(ibx, iby, height);
            }
        }
        return hrebinY;
    }

    /**
     * Creates a projection of the 2D histogram onto the X Axis, adding up all
     * the y bins for each x bin
     *
     * @return a H1F object that is a projection of the Histogram2D
     *         object onto the x-axis
     */
    public H1F projectionX() {
        String name = this.hName + "_projection_X";
        double xMin = xAxis.min();
        double xMax = xAxis.max();
        int xNum = xAxis.getNBins();
        H1F projX = new H1F(name, xNum, xMin, xMax);
        
        double height = 0.0;
        for (int x = 0; x < xAxis.getNBins(); x++) {
            height = 0.0;
            for (int y = 0; y < yAxis.getNBins(); y++) {
                height += this.getBinContent(x, y);
            }
            projX.setBinContent(x, height);
        }
        
        return projX;
    }
    
    public H1F profileX(){
        H1F h = new H1F(getName()+"_profileX",this.getXAxis().getNBins(),
                this.getXAxis().min(),this.getXAxis().max());
        H1F sliceX = sliceX(0);
        for(int i = 0; i < getXAxis().getNBins(); i++){
            this.sliceX(i, sliceX);
            double mean = sliceX.getMean();
            double rms  = sliceX.getRMS();
            h.setBinContent(i, mean);
            h.setBinError(i, rms);
        }
        return h;
    }
    /**
     * Creates a projection of the 2D histogram onto the Y Axis, adding up all
     * the x bins for each y bin
     *
     * @return a H1F object that is a projection of the Histogram2D
     *         object onto the y-axis
     */
    public H1F projectionY() {
        String name = this.hName + "_projection_Y";
        double yMin = yAxis.min();
        double yMax = yAxis.max();
        int yNum = yAxis.getNBins() ;
        H1F projY = new H1F(name, yNum, yMin, yMax);
        
        double height = 0.0;
        for (int y = 0; y < yAxis.getNBins(); y++) {
            height = 0.0;
            for (int x = 0; x < xAxis.getNBins(); x++) {
                height += this.getBinContent(x, y);
            }
            projY.setBinContent(y, height);
        }
        
        return projY;
    }
    
    /**
     * Creates a 1-D Histogram slice of the specified y Bin
     *
     * @param xBin		the bin on the y axis to create a slice of
     * @return 			a slice of the x bins on the specified y bin as a 1-D Histogram
     */
    public H1F sliceX(int xBin) {
        String name =  hName + "_slice_X_" + xBin;
        double yMin = yAxis.min();
        double yMax = yAxis.max();
        int    yNum = yAxis.getNBins();
        H1F sliceX = new H1F(name, name, yNum, yMin, yMax);        
        for (int y = 0; y < yNum; y++) {
            sliceX.setBinContent(y, this.getBinContent(xBin,y));
        }
        return sliceX;
    }
    /**
     * Creates a 1-D Histogram slice of the specified y Bin
     *
     * @param xBin		the bin on the y axis to create a slice of
     * @param sliceX            slice histogram to fill
     * @return 			a slice of the x bins on the specified y bin as a 1-D Histogram
     */
    public H1F sliceX(int xBin, H1F sliceX) {
        String name = hName + "_slice_X_" + xBin;
        double yMin = yAxis.min();
        double yMax = yAxis.max();
        int    yNum = yAxis.getNBins();
       // H1F sliceX = new H1F(name, name, yNum, yMin, yMax);        
        for (int y = 0; y < yNum; y++) {
            sliceX.setBinContent(y, this.getBinContent(xBin,y));
        }
        return sliceX;
    }
    /**
     * Creates a 1-D Histogram slice of the specified x Bin
     *
     * @param yBin the bin on the x axis to create a slice of
     * @return 	a slice of the y bins on the specified x bin as a 1-D Histogram
     */
    public H1F sliceY(int yBin) {
        String name = this.hName + "_slice_Y_" + yBin;
        double xMin = xAxis.min();
        double xMax = xAxis.max();
        int    xNum = xAxis.getNBins();
        H1F sliceY = new H1F(name, name, xNum, xMin, xMax);
        
        for (int y = 0; y < xNum; y++) {
            sliceY.setBinContent(y, this.getBinContent(y,yBin));
        }
        
        return sliceY;
    }
    
    /**
     * Creates a 1-D Histogram slice of the specified x Bin
     *
     * @param yBin the bin on the x axis to create a slice of
     * @return 	a slice of the y bins on the specified x bin as a 1-D Histogram
     */
    public H2F sliceY(int yBinMin, int yBinMax) {
        
        String name = String.format("%s:slice(%d,%d)",this.getName(),yBinMin,yBinMax);
        double xMin = xAxis.min();
        double xMax = xAxis.max();
        int    xNum = xAxis.getNBins();
        H2F sliceY = new H2F(name, name, xNum, xMin, xMax, 
                yBinMax-yBinMin + 1,
                this.getAxisY().getBinCenter(yBinMin)-this.getAxisY().getBinWidth(yBinMin)*0.5,
                this.getAxisY().getBinCenter(yBinMax)-this.getAxisY().getBinWidth(yBinMax)*0.5
        );
        
        for (int x = 0; x < xNum; x++) {
            for(int y = yBinMin; y <= yBinMax; y++){
                sliceY.setBinContent(x,y-yBinMin, this.getBinContent(x,y));
            }
        }       
        return sliceY;
    }
    
    public float[] offset() {
        float[] f = new float[hBuffer.length];
        for(int i = 0; i < hBuffer.length; i++) f[i] = (float) hBuffer[i];
        return f;
    }
    /**
     * Resets the content of the histogram, sets all bin contents to 0
     */
    @Override
    public void reset(){
        for(int bin = 0; bin < this.hBuffer.length; bin++){
            this.hBuffer[bin] = 0.0f;
        }
    }
    

    /**
     * returns a flat 1D histogram from the bins of 2D histogram
     * @param h2 reference histogram 2D
     * @return 1D flat histogram
     */
    public static H1F getH1F(H2F h2){
        H1F h1 = new H1F(h2.getName(),h2.getDataBufferSize(),0.0,h2.getDataBufferSize());
        for(int i = 0; i < h2.getDataBufferSize(); i++){
            h1.setBinContent(i, h2.getDataBufferBin(i));
        }
        return h1;
    }
    /*
    public H3F createH3F(int binsZ){
        H3F h3 = new H3F(
                getXAxis().getNBins(),
                getXAxis().min(),getXAxis().max(),
                getYAxis().getNBins(),
                getYAxis().min(),getYAxis().max(),
                binsZ,0.0,1.0
        );
        h3.reset();
        
        double h2min = getMin();
        double h2max = getMax();
        Axis   axisZ = new Axis(binsZ,h2min,h2max);
        for(int x = 0; x < getXAxis().getNBins(); x++){
            for(int y = 0; y < getYAxis().getNBins(); y++){
                double value = getBinContent(x,y);
                int     zbin = axisZ.getBin(value);
                h3.setBinContent(x, y, zbin, 1.0);
            }
        }
        return h3;
    } */
    
   
    public void save(String filename) {
        try {
            FileWriter file = new FileWriter(filename);
            file.write("#H2F: " + this.hName + ", nxbins: " + xAxis.getNBins()
                    + ", nybins: " + yAxis.getNBins() + "\n");
            file.write("#Bin Center X,Bin Center Y,Bin Value\n");

            for (int i = 0; i < xAxis.getNBins(); i++) {
                for (int j = 0; j < yAxis.getNBins(); j++) {
                    file.write(String.format("%f,%f,%f",
                            xAxis.getBinCenter(i), yAxis.getBinCenter(j), getBinContent(i, j)));
                    file.write('\n');
                }
            }
            file.close();
        } catch (IOException e) {
        }
    }

    public void sub(H2F h){
        if(h.getXAxis().getNBins()==this.getXAxis().getNBins()&&
                h.getYAxis().getNBins()==this.getYAxis().getNBins()){
            StatNumber   result = new StatNumber();
            StatNumber   denom  = new StatNumber();
            for(int loop = 0; loop < this.hBuffer.length; loop++){
                result.set(this.hBuffer[loop], Math.sqrt(Math.abs(this.hBuffer[loop])));
                denom.set(h.hBuffer[loop], Math.sqrt(Math.abs(h.hBuffer[loop])));
                result.subtract(denom);
                this.hBuffer[loop] = result.number();
            }
        } else {
            System.out.println("[warning] ---> histograms have different bin number. not added.");
        }
    }
    
    @Override
    public int getSize(int dimension) {
        if(dimension==0) return xAxis.getNBins();
        return yAxis.getNBins();
    }

    @Override
    public void getPoint(DataPoint point, int... coordinates) {
        point.x = this.xAxis.getBinCenter(coordinates[0]);
        point.xerror = this.xAxis.getBinWidth(coordinates[0]);
        
        point.y = this.yAxis.getBinCenter(coordinates[1]);
        point.yerror = this.yAxis.getBinWidth(coordinates[1]);
        
        point.z      = this.getBinContent(coordinates[0], coordinates[1]);
        point.zerror   = Math.sqrt(Math.abs(point.z));
        
    }

    @Override
    public void getRange(DataRange range) {
        range.set(
                this.xAxis.min(),  this.xAxis.max(),
                this.yAxis.min(),  this.yAxis.max()
        );
        range.z    = this.minimumBinValue;
        range.dept = this.maximumBinValue - this.minimumBinValue; 
    }

    @Override
    public TDataAttributes attr() {
        return this.dataAttr;
    }
    /* set Title of the histogram
     * @param title new title
     */
    public final void setTitle(String title){
        
        if(title.contains(";")==true){
           String[] tokens = title.split(";");
           if(tokens.length>0)
               this.attr().setTitle(tokens[0]);
           if(tokens.length>1)
               this.attr().setTitleX(tokens[1]);
           if(tokens.length>2)
               this.attr().setTitleY(tokens[2]);
        } else {
            this.attr().setTitle(title);
        }
    }
    
    @Override
    public List<String> getStats(String options) {
        List<String> stats = new ArrayList<>();
        stats.add(String.format("name : %s", this.getName()));
        stats.add(String.format("entries : %d", this.getEntries()));
        stats.add(String.format("max : %.3f", this.getMaximum()));
        return stats;
    }
}
