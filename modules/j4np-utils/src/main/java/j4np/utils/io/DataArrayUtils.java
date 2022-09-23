/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.io;

/**
 *
 * @author gavalian
 */
public class DataArrayUtils {
    /**
     * Swaps values from array b with array a, the resulting
     * new array is returned. The indices for the swap are
     * provided by array index.
     * @param a reference array
     * @param b swapping array
     * @param index indices for the array to to copy from array b
     * @return returns new array where all values match with a except 
     * for elements given by index.
     */
    public static double[] swap(double[] a, double[] b, int[] index){
        double[] c = new double[a.length];
        for(int i = 0; i < a.length; i++) c[i] = a[i];
        for(int i = 0; i < index.length; i++){
            c[index[i]] = b[index[i]];
        }
        return c;
    }
    /**
     * Copy given array into a ne array and return the result.
     * @param a initial array to be copied
     * @return a copy of given array
     */
    public static double[] copy(double[] a){
        double[] b = new double[a.length];
        for(int k = 0; k < b.length; k++) b[k] = a[k];
        return b;
    }
    /**
     * Copy partial content of the array into a new array
     * @param a initial array
     * @param start index of the first element to copy
     * @param end index of last element to copy
     * @return new array
     */
    public static double[] copy(double[] a, int start, int end){
        double[] b = new double[end-start+1];
        for(int k = 0; k < b.length; k++) b[k] = a[k+start];
        return b;
    }
    /**
     * converts a double array to float array.
     * @param a inital double array
     * @return new float array
     */
    public static float[] toFloat(double[] a){
        float[] b = new float[a.length];
        for(int k = 0; k < b.length; k++) b[k] = (float) a[k];
        return b;
    }
    /**
     * converts a double array to float array.
     * @param a inital double array
     * @return new float array
     */
    public static double[] toDouble(float[] a){
        double[] b = new double[a.length];
        for(int k = 0; k < b.length; k++) b[k] =  a[k];
        return b;
    }
    /**
     * Returns a difference value between two arrays. 
     * @param a reference array 
     * @param b reference array 
     * @return mean absolute error for all elements in two arrays
     */
    public static double difference(double[] a, double[] b){
        double diff = 0;
        for(int i = 0; i < a.length; i++){
            diff += Math.abs(a[i]-b[i]);
        }
        return diff;
    }
    
    public static String getDataString(double[] array){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.length; i++) str.append(String.format("%8.5f ", array[i]));
        return str.toString();
    }
    
    
    public static String getDataString(double[] array, int prec){
        String format = "%." + String.format("%d", prec) + "f";
        StringBuilder str = new StringBuilder();
        str.append(String.format(format, array[0]));        
        for(int i = 1; i < array.length; i++) 
            str.append(",").append(String.format(format, array[i]));
        return str.toString();
    }
    
    public static String doubleToString(double[] array, String delim){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.length; i++) {
            if(i!=0) str.append(delim);
            str.append(String.format("%8.5f", array[i]));
        }//.append(delim);
        return str.toString();
    }
    
    public static String floatToString(float[] array, String delim){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.length; i++) 
            str.append(String.format("%8.5f", array[i])).append(delim);
        return str.toString();
    }
    
    public static String intToString(int[] array, String delim){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < array.length; i++) 
            str.append(String.format("%8d", array[i])).append(delim);
        return str.toString();
    }
        
    public static double[] stringToDouble(String line, String delim){
        String[] tokens = line.trim().split(delim);
        double[]  array = new double[tokens.length];
        for(int i = 0; i < array.length; i++) 
            array[i] = Double.parseDouble(tokens[i]);               
        return array;
    }
    
    public static int[] stringToInt(String line, String delim){
        String[] tokens = line.trim().split(delim);
        int[]  array = new int[tokens.length];
        for(int i = 0; i < array.length; i++) 
            array[i] = Integer.parseInt(tokens[i]);               
        return array;
    }
    
    public static void byteToString(byte[] array, int wrap, int max){
        int size = array.length;
        if(size>max) size = max;        
        for(int i = 0; i < size; i++){
            System.out.printf("%2s ",
                    Integer.toHexString(array[i]).replaceAll(" ", "0"));
            if((i+1)%wrap==0) System.out.println();
        }
        System.out.println();
    }
}
