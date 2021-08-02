/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

/**
 *
 * @author gavalian
 * @param <T>
 */
public class BasicInputConverter<T> {

    String name = "";
    String description = "";
    T defaultValue;
    T value;
    
    public BasicInputConverter(T _dv){
        this.defaultValue = _dv;
    }

    public BasicInputConverter(String _name,T _dv){
        this.defaultValue = _dv;
        this.name = _name;
    }
    
    public BasicInputConverter(String _name, String _desc,T _dv){
        this.defaultValue = _dv;
        this.name = _name;
        this.description = _desc;
    }
    
    public String getName() { return name;}
    public String getDescription() { return description;}
    public void setName(String _name){name = _name;}
    public void setDescription(String _desc){ description = _desc;}
    
    public Object valueOf(String _value){

        if(_value.compareTo("!")==0) return defaultValue;
        /**
         * For integer Values parse INTEGER.
         */
        if(defaultValue instanceof Integer){
            try {
                Integer intValue = Integer.parseInt(_value);
                return  intValue;
            } catch(Exception e){
                
                return null;
            }

        }
        /**
         * for double values parse DOUBLE.
         */
        if(defaultValue instanceof Double){
            try {
                Double doubleValue = Double.parseDouble(_value);
                return doubleValue;
            } catch(Exception e){
                return null;
            }
        }
        
        if(defaultValue instanceof String){
            return _value;
        }
        
        return null;
    }
    
}
