/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.utils.dsl;

/**
 *
 * @author gavalian
 */
@DSLSystem (system="math", info="math calculations module")
public class DummyModule {
    public DummyModule(){
        
    }
    
    @DSLCommand(
            command="add",
            info="adds two numbers",
            defaults={"1","2"},
            descriptions={"first number", "second number" }
    )
    public void add(int i1, int i2){        
        int summ = i1 + i2;
        System.out.println(i1 + " + " + i2 + " = " + summ);
    }
    
    @DSLCommand(
            command="mult",
            info="multiplies two numbers",
            defaults={"2","3"},
            descriptions={"first number", "second number" }
    )
    public void mult(int i1, int i2){        
        int summ = i1 * i2;
        System.out.println(i1 + " * " + i2 + " = " + summ);
    }
}
