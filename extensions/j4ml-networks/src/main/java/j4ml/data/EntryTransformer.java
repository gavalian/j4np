/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4ml.data;

/**
 *
 * @author gavalian
 */
public class EntryTransformer {
    DataTransformer  input = new DataTransformer();
    DataTransformer output = new DataTransformer();
    public EntryTransformer(){}
    public DataTransformer input() { return  input;}
    public DataTransformer output(){ return output;}
}
