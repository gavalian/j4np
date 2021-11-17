/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import twig.data.DataSet;
import twig.data.H1F;

/**
 *
 * @author gavalian
 */
public abstract class Tree {
    
    private String treeName = "tree";
    
    public Tree(){ }
    public Tree(String name){ setName(name);}
    
    public abstract double   getValue(int order);
    public abstract double   getValue(String branch);
    
    public abstract List<String> getBranches();
    public abstract int      getBranchOrder(String name);
    public abstract void     reset();
    public abstract boolean  next();
    
    public final void    setName(String name){ treeName = name;}
    public final String  getName(){ return treeName;}
    
    
    public final H1F geth(String expression, String cut, int bins, double min, double max){
        H1F h = new H1F(expression,bins,min,max);
        h.attr().setTitle(cut);
        geth(expression,cut,h);
        return h;
    }
    
    public final void geth(String expression, String cut, H1F h){
        reset();
        h.attr().setTitle(cut);
        h.attr().setTitleX(expression);
        List<String> branches = this.getBranches();
        TreeExpression  varExp = new TreeExpression(expression, branches);
        TreeCut         cutExp = new TreeCut("1",cut,branches);
        int counter = 0;
        long evaluate = 0L;
        long then = System.currentTimeMillis();
        while(this.next()==true){
            counter++;
            long start = System.nanoTime();
            if(cutExp.isValid(this)>0.5){
                h.fill(varExp.getValue(this));
            }
            long end = System.nanoTime();
            evaluate += (end - start);
        }
        long now = System.currentTimeMillis();
        System.out.printf("get::perf>> evaluated #%12d in %12d ms, total %12d ms\n", 
                counter,(int) (evaluate/1000000.0), now-then);
    }        
    
    public static List<H1F> createH1D(int[] bins, double[] limits){
        List<H1F> hList = new ArrayList<>();
        for(int i = 0; i < bins.length; i++){
            hList.add(new H1F("gen"+i,bins[i],limits[i*2],limits[i*2+1]));
        }
        return hList;
    }
    
    public List<H1F> getData(int[] bins, double[] limits, String expressions, String cuts){
        List<H1F> hList = Tree.createH1D(bins, limits);
        this.getData(hList, expressions, cuts);
        return hList;
    }
    
    public void getData(List<H1F> data,String expressions, String cuts){
        String[] tokens = expressions.split(":");
    }
}
