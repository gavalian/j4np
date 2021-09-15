/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.base;

/**
 * This is data source interface. It is used to implement
 * different data sources (formats) that can work the same
 * int the data analysis and processing programs.
 * @author gavalian
 */
public interface DataSource {

    /**
     * open stream for reading. It can be a file or
     * a streaming data source.
     * @param url 
     */
    public    void open(String url);
    /**
     * Routine notifies if there are events left in the stream 
     * to read.
     * @return true for events present
     */
    public boolean hasNext();
    /**
     * reads next event from the stream.
     * @param event event object
     * @return if the event was successfully read
     */
    public boolean next(DataEvent event);
    /**
     * Returns current position in the stream relative to 
     * the beginning.
     * @return relative position of reading in the stream
     */
    public int position();
    /**
     * set position in the stream to pos. next read with function
     * next(event) will be from this position in the stream.
     * @param pos
     * @return true if the read was successful 
     */
    public boolean position(int pos);
    /**
     * Returns number of events in the stream when applicable.
     * For files that can be random read, this functionality 
     * will be implemented.
     * @return number of events contained in the file
     */
    public int entries();
    /**
     * reads the next frame from the file. returns the
     * number of events that were read from the file. if
     * the end of file is not reached returned value should 
     * be equal to frame.getCount().
     * @param frame
     * @return 
     */
    public int nextFrame(DataFrame frame);
}
