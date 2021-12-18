/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import twig.data.DataSet;
import twig.data.H1F;
import twig.data.H2F;
import twig.studio.TwigStudio;

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
    
    
    protected H1F getByStringH1F(String desc){
        String dataName = this.getNameByExperession(desc);
        String[] operand = desc.split(">>");
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(operand[1]);
        try { 
            m.find(); 
            System.out.println(m.group(1));
            String[] data = m.group(1).split(",");
            if(data.length==3){
                H1F h = new H1F(dataName,
                        Integer.parseInt(data[0]),
                        Double.parseDouble(data[1]),
                        Double.parseDouble(data[2])
                );
                return h;
            }
        } catch (Exception e){
            System.out.println("[tree] error : syntax error in string [" + desc +"]");
        }
        
        return null;
    }
    
    protected H2F getByStringH2F(String desc){
        
        String dataName = this.getNameByExperession(desc);
        String[] operand = desc.split(">>");
        
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(operand[1]);
        try { 
            m.find(); 
            System.out.println(m.group(1));
            String[] data = m.group(1).split(",");
            if(data.length==6){
                H2F h = new H2F(dataName,
                        Integer.parseInt(data[0]),
                        Double.parseDouble(data[1]),
                        Double.parseDouble(data[2]),
                        Integer.parseInt(data[3]),
                        Double.parseDouble(data[4]),
                        Double.parseDouble(data[5])
                );
                return h;
            }
        } catch (Exception e){
            System.out.println("[tree] error : syntax error in string [" + desc +"]");
        }
        
        return null;
    }
    
    protected String getNameByExperession(String exp){
        String[] operands = exp.split(">>");
        int index = operands[1].indexOf("(");        
        return operands[1].substring(0, index);
    }
    
    public final void draw(String expression){
        this.draw(expression, "", "");
    }
    
    public final void draw(String expression, String cuts){
        this.draw(expression, cuts, "");
    }
    
    public final void draw(String expression, String cuts, String options){

        int  index = expression.indexOf(">>");
        String exp = expression.substring(0, index);        
        
        if(exp.contains(":")==true){
            H2F h2 = this.getByStringH2F(expression);
            geth2(exp, cuts, h2);
            TwigStudio.getInstance().dir().add("/studio", h2);
            TwigStudio.getInstance().getCanvas().view().region().draw(h2, options);
            if(options.contains("same")==false)
                TwigStudio.getInstance().getCanvas().view().next();
            TwigStudio.getInstance().getCanvas().repaint();
        } else {
            H1F h = this.getByStringH1F(expression);
            this.geth(exp, cuts, h);
            TwigStudio.getInstance().dir().add("/studio", h);
            TwigStudio.getInstance().getCanvas().view().region().draw(h, options);
            if(options.contains("same")==false)
                TwigStudio.getInstance().getCanvas().view().next();
            TwigStudio.getInstance().getCanvas().repaint();
        }
        //System.out.println(" parsing " + exp);
        //H1F h = this.geth(expression, cuts, 0, 0, 0)
        
    } 
    
    public final H1F geth(String expression, String cut, int bins, double min, double max){
        H1F h = new H1F(expression,bins,min,max);
        h.attr().setTitle(cut);
        geth(expression,cut,h);
        return h;
    }
    
    public final H2F geth2(String expression, String cut, 
            int binsX, double minX, double maxX,int binsY, double minY, double maxY){
        H2F h2 = new H2F(expression,binsX,minX,maxX,binsY,minY,maxY);
        h2.attr().setTitle(cut);
        geth2(expression,cut,h2);
        return h2;
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
        
    public final void geth2(String expression, String cut, H2F h){
        reset();
        h.attr().setTitle(cut);
        h.attr().setTitleX(expression);
        String[] axisExp = expression.split(":");
        
        List<String> branches = this.getBranches();
        TreeExpression  varExpX = new TreeExpression(axisExp[1], branches);
        TreeExpression  varExpY = new TreeExpression(axisExp[0], branches);
        
        TreeCut         cutExp = new TreeCut("1",cut,branches);
        int counter = 0;
        long evaluate = 0L;
        long then = System.currentTimeMillis();
        while(this.next()==true){
            counter++;
            long start = System.nanoTime();
            if(cutExp.isValid(this)>0.5){
                double vX = varExpX.getValue(this);
                double vY = varExpY.getValue(this);
                h.fill(vX,vY);
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
