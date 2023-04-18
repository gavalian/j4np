/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twig.math;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gavalian
 */
public class UserParameters implements Cloneable {
    
    List<UserParameter>  parameters = new ArrayList<UserParameter>();
    
    public UserParameters(){
        
    }
    
    public int contains(String name){
        for(int i = 0; i < parameters.size(); i++){
            if(parameters.get(i).name().compareTo(name)==0)
                return i;
        }
        return -1;
    }
    public UserParameter  getParameter(int index){
        return parameters.get(index);
    }
    
    public void setParameters(double[] pars){
        if(pars.length!=parameters.size()){
            System.out.println("UserParameters : error -> wrong number of parameters");
        }
        for(int i = 0; i < pars.length; i++){
            parameters.get(i).setValue(pars[i]);
        }
    }
    
    public List<UserParameter>  getParameters(){
        return this.parameters;
    }
    
    public void reset(){
        //this.parameters.clear();
    }
    
    public void clear(){
        this.parameters.clear();
    }
    
    public void getCopy(UserParameters par){
        this.parameters.clear();
        for(int i = 0; i < par.getParameters().size(); i++){
            this.parameters.add(par.getParameter(i).getCopy());
        }        
    }
    
    
    @Override
    public UserParameters clone(){
        UserParameters up = new UserParameters();
        int npars = this.getParameters().size();
        for(int i = 0; i < npars; i++) up.parameters.add(parameters.get(i).clone());
        return up;
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        str.append(String.format("PARAMETER SET N PARAMS = %d\n**\n", parameters.size()));
        for(int i = 0; i < parameters.size(); i++){
            str.append(parameters.get(i).toString());
            str.append("\n");
        }
        return str.toString();
    }
    
    public void randomize(){
        for(UserParameter p : this.parameters) p.setRandom();
    }
    
    public static void main(String[] args){
        UserParameters up = new UserParameters();
        up.getParameters().add(new UserParameter("p0",1.2));
        up.getParameters().add(new UserParameter("p1",1.4));
        up.getParameters().add(new UserParameter("p2",1.6));
        
        
        UserParameters up2 = up.clone();
        
        up2.getParameter(1).setValue(2.4);

        
        System.out.println(up);
        System.out.println(up2);
        
    }
}
