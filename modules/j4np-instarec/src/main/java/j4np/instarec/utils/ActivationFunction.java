/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package j4np.instarec.utils;

/**
 *
 * @author gavalian
 */
public abstract class ActivationFunction {
    
    public abstract float     apply(float number);
    public abstract   int     type();
    public abstract   String  name();
    
    public static class ActivationRELU extends ActivationFunction {
        @Override
        public float apply(float number) {
            return Math.max(0, number);
        }        

        @Override
        public int type() {
            return 1;
        }

        @Override
        public String name() {
            return "RELU";
        }
    }
    
    public static class ActivationSIGM extends ActivationFunction {
        @Override
        public float apply(float number) {
            return (float) (1.0/(1.0 + Math.exp(-number)));
        }        
        
        @Override
        public int type() {
            return 2;
        }

        @Override
        public String name() {
            return "SIGM";                    
        }
    }
    
    public static class ActivationTANH extends ActivationFunction {
        @Override
        public float apply(float number) {
            return (float) (Math.tanh(number));
        } 

        @Override
        public int type() {
            return 3;
        }

        @Override
        public String name() {
            return "TANH";
        }
    }
    
    public static class ActivationLIN extends ActivationFunction {
        @Override
        public float apply(float number) {
            return number;
        } 

        @Override
        public int type() {
            return 4;
        }

        @Override
        public String name() {
            return "LIN";
        }
    }
    
    public static class ActivationSOFTMAX extends ActivationFunction {
        @Override
        public float apply(float number) {
            return (float) Math.exp(number);//(1.0/(1.0 + Math.exp(-number)));
        } 

        @Override
        public int type() {
            return 5;
        }

        @Override
        public String name() {
            return "SOFTMAX";
        }
    }
    
}
