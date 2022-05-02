/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.tree;

import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.Table;
import com.indvd00m.ascii.render.elements.Text;
import j4np.utils.io.TextFileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import twig.data.AsciiPlot;
import twig.data.DataSet;
import twig.data.DataVector;
import twig.data.H1F;
import twig.data.H2F;
import twig.data.Range;
import twig.graphics.TGDataCanvas;
import twig.server.TreeModelMaker;
import twig.studio.TreeProvider;
import twig.studio.TwigStudio;

/**
 *
 * @author gavalian
 */
public abstract class Tree implements TreeProvider {
    
    private String       treeName = "tree";
    private int       defaultBins = 100;
    private Logger     treeLogger = Logger.getLogger(Tree.class.getName());
    
    private List<TreeCut>  treeCuts = new ArrayList<>();
    
    public Tree(){ treeLogger.setLevel(Level.OFF); }
    
    public Tree(String name){ setName(name);treeLogger.setLevel(Level.OFF);}
    
    public abstract double   getValue(int order);
    public abstract double   getValue(String branch);    
    public abstract List<String> getBranches();    
    public abstract int      getBranchOrder(String name);
    public abstract void     reset();
    public abstract boolean  next();
    
    public void  setLoggerLevel(Level level){
        treeLogger.setLevel(level);
    }
    
    public void setLoggerLevelFine(){ treeLogger.setLevel(Level.FINE);}
    public void setLoggerLevel(){ treeLogger.setLevel(Level.INFO);}
    
    public Tree  setDefaultBins(int bins){
        this.defaultBins = bins; return this;
    }
    
    public final void    setName(String name){ treeName = name;}
    public final String  getName(){ return treeName;}
    
    
    public void showBranches(){
        List<String> list = this.getBranches();
        System.out.printf("-- list of branches --\n");
        for(String item : list){
            System.out.printf("\t-> %s\n",item);
        }
    }
    
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
    
    
    public Tree addCut(String name, String cutExp){
        List<String> branches = getBranches();
        boolean valid = TreeCut.validateExpression(cutExp, branches);
        if(valid==true){
            treeCuts.add(new TreeCut(name,cutExp,branches));
        } else {
            System.out.printf("\nERROR : cut expression [%s] is just plain wrong...\n\n",cutExp);
        }
        return this;
    }
    
    public List<TreeCut>  getDefaultCuts(){return this.treeCuts;}
    
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
    
    public void ascii(String expression){
        this.ascii(expression, "", "");
    }
    
    public void ascii(String expression, String cuts){
        this.ascii(expression, cuts,"");
    }
    
    public void ascii(String expression, String cuts, String options){
        if(expression.contains(">>")==true){
            int  index = expression.indexOf(">>");
            String exp = expression.substring(0, index);
            H1F      h = this.getByStringH1F(expression);
            geth(exp, cuts, h);
            AsciiPlot.draw(h);
        } else {
            H1F h = this.gethundef(expression, cuts, 100);
            AsciiPlot.draw(h);
        }
    }
    
    private  void drawUndefined(String expression, String cuts, String options){
        if(expression.contains(":")==true){
            H2F h = this.geth2undef(expression, cuts, 100,100);
            
            TwigStudio.getInstance().getCanvas().view().region().draw(h, options);
            if(options.contains("same")==false)
                TwigStudio.getInstance().getCanvas().view().next();
            TwigStudio.getInstance().getCanvas().repaint();
        } else {
            H1F h = this.gethundef(expression, cuts, 100);
            TwigStudio.getInstance().getCanvas().view().region().draw(h, options);
            if(options.contains("same")==false)
                TwigStudio.getInstance().getCanvas().view().next();
            TwigStudio.getInstance().getCanvas().repaint();
        }
    }
    
    public final void draw(String expression, String drawCuts, String options){
                        
        String cuts = drawCuts;

        if(treeCuts.size()>0){
            if(drawCuts.length()>0){
                cuts = drawCuts + "&&" + TreeCut.combine(treeCuts);
            } else {
                cuts = TreeCut.combine(treeCuts);
            }
        }
        
        //System.out.printf("draw >>> (%s) (%s) (%s)\n",expression,cuts,options);
        
        if(expression.contains(">>")==false) {
            drawUndefined( expression,  cuts,  options);
            return;
        }
        
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
    
    
    private String getUpdatedCutString(String cut){
        StringBuilder str = new StringBuilder();
                
        if(cut.length()>0){
            str.append(cut);
            if(treeCuts.size()>0)
                str.append("&&").append(TreeCut.combine(treeCuts));
        } else {
            str.append(TreeCut.combine(treeCuts));
        }               
        return str.toString();
    }
    
    private H1F gethundef(String expression, String cutExpression, int bins){                
        String cut = this.getUpdatedCutString(cutExpression);
        //System.out.printf("draw[][] >>> (%s) (%s) (%d) {} debug (%d) (%s) \n",
        //        expression,cut,bins,this.treeCuts.size(), 
        //        TreeCut.combine(treeCuts));
        reset();
        DataVector v = new DataVector();
        List<String> branches = this.getBranches();
        TreeExpression  varExp = new TreeExpression(expression, branches);
        TreeCut         cutExp = new TreeCut("1",cut,branches);
        int    counter = 0;
        long  evaluate = 0L;
        long      then = System.currentTimeMillis();
        long     start = System.nanoTime();
        
        while(this.next()==true){
            counter++;
            if(cutExp.isValid(this)>0.5){ v.add(varExp.getValue(this));}
        }
        
        long end = System.nanoTime();
        evaluate += (end - start);
        long now = System.currentTimeMillis();                
        long uid = TwigStudio.getInstance().getNextUniqueId();
        H1F h = H1F.create("h"+uid, bins, v);
        h.setUniqueID(uid);        
        TwigStudio.getInstance().addDataSet(uid, h);
        h.attr().setTitle(cut);
        h.attr().setTitleX(expression);
        
        treeLogger.log(Level.INFO ,String.format(
                "get::perf >> evaluated #%12d in %12d ms, total %12d ms\n", 
                counter,(int) (evaluate/1000000.0), now-then));
        treeLogger.log(Level.INFO , String.format("get::conf >> exp = '%s', cuts = '%s'",
                expression,cut));
        treeLogger.log(Level.INFO , String.format("get::conf >> vector size = %d, [%f,%f]",
                v.getSize(),v.getMin(),v.getMax()));
        
        return h;
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
    /**
     * fills expression provided by "expression" into provided
     * histogram "h", if the "cut" is valid.
     * @param expression - expression to evaluate
     * @param cut - expression representing a cut
     * @param h - histogram to fill with the expression
     */
    public final void geth(String expression, String cut, H1F h){
        reset();
        h.attr().setTitle(cut);
        h.attr().setTitleX(expression);
        List<String> branches = this.getBranches();
        TreeExpression  varExp = new TreeExpression(expression, branches);
        TreeCut         cutExp = new TreeCut("1",cut,branches);
        int counter = 0;

        long evaluate = 0L;
        long read = 0L;

        boolean isDone = false;
        while(this.next()==true){
            long then = System.nanoTime();
            //boolean status = this.next();
            long now = System.nanoTime();
            read += (now-then);            
            counter++;
            long start = System.nanoTime();
            if(cutExp.isValid(this)>0.5){
                h.fill(varExp.getValue(this));
            }
            long end = System.nanoTime();
            evaluate += (end - start);
        }

        System.out.printf("get::perf>> evaluated #%12d in %12d ms, read %12.4f ms\n", 
                counter,(int) (evaluate/1000000.0), (read/1000000.0));
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
    
    public final H2F geth2undef(String expression, String cut, int binsX, int binsY){
        reset();
        DataVector vx = new DataVector();
        DataVector vy = new DataVector();
        
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
                vx.add(vX);vy.add(vY);
            }
            long end = System.nanoTime();
            evaluate += (end - start);
        }
        long now = System.currentTimeMillis();
        H2F h = H2F.create("h2000000", binsX, binsY, vx, vy);
        h.attr().setTitle(cut);
        h.attr().setTitleX(axisExp[0]);
        h.attr().setTitleY(axisExp[1]);
        System.out.printf("get::perf>> evaluated #%12d in %12d ms, total %12d ms\n", 
                counter,(int) (evaluate/1000000.0), now-then);
        return h;
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
    
    
    @Override
    public TreeModel getTreeModel(){
        TreeModelMaker    tm = new TreeModelMaker();
        List<String>   nodes = this.getBranches();
        Collections.sort(nodes);
        tm.setList(nodes);

        DefaultMutableTreeNode root = tm.getTreeModel();
        return new DefaultTreeModel(root);
    }
    
    
    @Override
    public void      draw(String path, TGDataCanvas c){
           System.out.println("will be drawing this : " + path);
           if(path.contains("/")==true){
               String branch = path.replaceAll("/", "");               
               H1F h = this.gethundef(branch, "", 100);
               c.region().draw(h);
               c.next();
           }
    }
    
    public void scan(){
        List<String> names = this.getBranches();
        List<Range>  ranges = new ArrayList<>();
        this.reset();
        this.next();
        for(int i = 0; i < names.size(); i++){
            Range r = new Range(this.getValue(names.get(i)),this.getValue(names.get(i)));
            ranges.add(r);
        }
        
        while(this.next() == true){
            for(int i = 0; i < names.size(); i++){
                double value = this.getValue(names.get(i));
                Range r = ranges.get(i);
                if(value>r.max()) ranges.get(i).set(r.min(), value);
                if(value<r.min()) ranges.get(i).set(value,r.max());
            }
        }
        
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(72).height(names.size()*2+3);
        Table table = new Table(3, names.size()+1);
        table.setElement(1, 1, new Text(" variable"),true);
        table.setElement(2, 1, new Text(" minimum"),true);
        table.setElement(3, 1, new Text(" maximum"),true);
        //System.out.println("---------------------------------------------------");
        for(int i = 0; i < names.size(); i++){
            table.setElement(1, i+2, new Text(names.get(i)));
            table.setElement(2, i+2, new Text(String.format("%f",ranges.get(i).min())));
            table.setElement(3, i+2, new Text(String.format("%f",ranges.get(i).max())));

            /*System.out.println(String.format("%24s : %14f %14f", names.get(i),
                    ranges.get(i).min(),ranges.get(i).max()));*/
        }
        builder.element(table);
        ICanvas canvas = render.render(builder.build());
        String s = canvas.getText();              
        System.out.println(s);
        //System.out.println("---------------------------------------------------");
    }
    
    public void export(String filename){
        List<String> branches = this.getBranches();
        TextFileWriter w = new TextFileWriter();
        w.open(filename);
        w.writeString("#" + Arrays.toString(branches.toArray()));
        this.reset();
        int nrows = branches.size();
        while(this.next()==true){
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < nrows; i++){
                if(i!=0) str.append(",");
                str.append(String.format("%e", this.getValue(i)));
            }
            w.writeString(str.toString());
        }
        w.close();
    }
    
    @Override
    public void execute(String command){
        String[] tokens = command.split("/");
        System.out.println(" command = " + Arrays.toString(tokens));
        if(tokens[0].compareTo("addcut")==0){
            this.treeCuts.clear();
            this.addCut(tokens[1], tokens[2]);
        }
        
        if(tokens[0].compareTo("defaultbins")==0){
            this.defaultBins = Integer.parseInt(tokens[1]);
        }
    }
}
