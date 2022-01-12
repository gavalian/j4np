/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.data.base;

import j4np.data.base.DataEvent;
import java.util.function.Consumer;



/**
 *
 * @author gavalian
 * @param <T>
 */
public  abstract class DataWorker<R extends DataSource,T extends DataEvent> implements Consumer<T> {
    
    private String name = "unknown";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public abstract boolean init(R src);
    public abstract void execute(T e);
        
    public boolean  parse(String json){
        return true;
    }
    
    public boolean  parseConfigFile(String file){
        return true;
    }

    
    @Override
    public void accept(T t) {
        execute(t);
    }
}
