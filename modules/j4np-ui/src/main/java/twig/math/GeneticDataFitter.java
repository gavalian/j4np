/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import twig.data.H1F;
import twig.data.TDataFactory;
import twig.graphics.TGCanvas;
import twig.math.genetics.Chromosome;
import twig.math.genetics.Fitness;
import twig.math.genetics.GeneticAlgorithm;
import twig.math.genetics.IterartionListener;
import twig.math.genetics.Population;
import twig.widgets.PaveText;

/**
 *
 * @author gavalian
 */
public class GeneticDataFitter {
    
    private F1D func = null;
    private int initialPopulation = 5000;
    public GenFunction gfunc = null;
    
    public GeneticDataFitter(){
        
    }
    
    public void initFunction(double min, double max){
        //func =  new F1D("func","[a]+[b]*x+[c]*x*x+[amp]*gaus(x,[m],[s]))+[amp2]*gaus(x,[m2],[s2])",min,max);
    }
    
    private void initializeFromHisto(H1F h){
        double max = h.getMax();
        
    }
    
    public static class GenFitness implements Fitness<GenFunction, Double> {
        
        private H1F histo = null;
        
        public GenFitness(H1F h){
            histo = h;
        }
        
        @Override
        public Double calculate(GenFunction chromosome) {
            double chi2 = Func1D.calcChiSquare(chromosome.function, histo);
            return chi2;
        }
        
    }
    
    public static class GenFunction implements Chromosome<GenFunction>, Cloneable {

        private static final Random random = new Random();
        public F1D  function = null;
        
        public GenFunction(String expression, double min, double max){
            function = new F1D("a",expression, min, max);
            function.setParLimits(0, 0, 1.0);
            function.setParLimits(1, 0, 1.0);
            function.setParLimits(2, 0, 1.0);
            function.setParLimits(3, 0, 1.0);
            function.setParLimits(4, 0, 1.0);
            function.setParLimits(5, 0, 1.0);
            function.setParLimits(6, 0, 1.0);
        }
        
        @Override
        public List<GenFunction> crossover(GenFunction other) {
           GenFunction thisClone = this.clone();
           GenFunction otherClone = other.clone();
           int np = thisClone.function.getNPars();
           int index = random.nextInt( np - 1);
           for (int i = index; i < np; i++) {
               double tmp = thisClone.function.getParameter(i);
               thisClone.function.setParameter(i,  otherClone.function.getParameter(i));
               otherClone.function.setParameter(i, tmp);
           }
           return Arrays.asList(thisClone, otherClone);
        }
        
        @Override
        public GenFunction mutate() {
            GenFunction gen = this.clone();
            int par = random.nextInt(gen.function.getNPars());
            double value = 
                    function.parameter(par).min()+ random.nextDouble()*(
                    function.parameter(par).max()-function.parameter(par).min());
            
            gen.function.setParameter(par, value);
            return gen;
        }
        
        public GenFunction random(){
            GenFunction gen = this.clone();
            //int par = random.nextInt(gen.function.getNPars());
            for(int i = 0; i < gen.function.getNPars(); i++){
                double value = 
                    function.parameter(i).min()+ random.nextDouble()*(
                    function.parameter(i).max()-function.parameter(i).min());
           
                gen.function.setParameter(i, value);
            }
            return gen;
        }
        
        @Override
        protected GenFunction clone() {
            GenFunction f = new GenFunction(function.getExpression(),
                    function.getMin(), function.getMax());
            
            int np = function.getNPars();
            for(int i = 0; i < np; i++){                
                f.function.parameter(i).set(
                function.parameter(i).value(),
                        function.parameter(i).min(),
                        function.parameter(i).max()
                );
            }
            
            return f;
        }
        
        @Override
        public String toString(){
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < function.getNPars(); i++)
                str.append(String.format("%e ",function.getParameter(i)));
            return str.toString();
        }
    }
    
    private Population<GenFunction> createInitialPopulation(int populationSize,
            double min, double max) {
        
        Population<GenFunction> population = new Population<GenFunction>();
        
        GenFunction base = new GenFunction("[a]+[c]*gaus(x,[m],[s])+[d]*gaus(x,[m2],[s2])", min,max);
        for (int i = 0; i < populationSize; i++) {
            // each member of initial population
            // is mutated clone of base chromosome
            //GenFunction chr = base.mutate();
            GenFunction chr = base.random();
            //System.out.println(chr);
            population.addChromosome(chr);
        }                
        
        return population;
    }
    
    private List<GenFunction> createInitialPopulation2(int populationSize,
            double min, double max) {
        
        Population<GenFunction> population = new Population<GenFunction>();
        List<GenFunction>       list = new ArrayList<>();
        
        GenFunction base = new GenFunction("[a]+[c]*gaus(x,[m],[s])+[d]*gaus(x,[m2],[s2])", min,max);
        for (int i = 0; i < populationSize; i++) {
            // each member of initial population
            // is mutated clone of base chromosome
            //GenFunction chr = base.mutate();
            GenFunction chr = base.random();
            //System.out.println(chr);
            population.addChromosome(chr);
            //list.add(chr);
        }                        
        return list;
    }
/*
    private List<GenFunction>  reduce(List<GenFunction> list){
        
    }
*/
    
    public void addListener(GeneticAlgorithm<GenFunction, Double> ga ){
        ga.addIterationListener(new IterartionListener<GenFunction, Double>() {

            private final double threshold = 1e-5;
            
            @Override
            public void update(GeneticAlgorithm<GenFunction, Double> ga) {
                
                GenFunction best = ga.getBest();
                double bestFit = ga.fitness(best);
                int iteration = ga.getIteration();
                
                // Listener prints best achieved solution
                System.out.println(String.format("%s\t%s\t%s", iteration, bestFit, best));
                
                // If fitness is satisfying - we can stop Genetic algorithm
                if (bestFit < this.threshold) {
                    ga.terminate();
                }
            }
        });
        
    }
    
    
    public void fit(H1F h, double min, double max){
         Population<GenFunction> population = createInitialPopulation(
                 this.initialPopulation, min, max
                 );
         
         
        //ChromosomesComparator<> cc = new ChromosomesComparator();
        
         Fitness<GenFunction,Double> fitness = new GenFitness(h);
         //cc.fitnessFunc = fitness;
         
         //population.sortPopulationByFitness(chromosomesComparator);
         
         GeneticAlgorithm<GenFunction, Double> ga = new GeneticAlgorithm<GenFunction, Double>(population, fitness);

         this.addListener(ga);
         
         ga.evolveCrop(1500,100);
         this.gfunc = ga.getBest();
    }/*
        private static Population<MyVector> createInitialPopulation(int populationSize) {
                Population<MyVector> population = new Population<MyVector>();
                MyVector base = new MyVector();
                for (int i = 0; i < populationSize; i++) {
                        // each member of initial population
                        // is mutated clone of base chromosome
                        MyVector chr = base.mutate();
                        population.addChromosome(chr);
                }
                return population;
        }
    }
  */      
    public static void main(String[] args){
        
        H1F hl = TDataFactory.createH1F(8200, 240, 0.0, 1.0, 0.25, 0.15);
        H1F hu = TDataFactory.createH1F(4200, 240, 0.0, 1.0, 0.65, 0.08);
        H1F h  = H1F.add(hu, hl);
        H1F h2 = TDataFactory.createH1F(4000);
        H1F h3 = TDataFactory.createH1F(12, 120, 0.0, 1.0, 1.42, 0.02);
        
        //h.add(h3);
        
        TGCanvas c = new TGCanvas(400,400);
        F1D f = new F1D("f","[p0]+[a]*gaus(x,[m],[s])",0.1,0.75);
        f.setParameters(1,0.5,0.5,0.2);
        f.setParLimits(1,0.0,1.0);
        f.setParLimits(2,0.0,1.0);
        f.setParLimits(3,0.0,1.0);
        h.unit();
        h.attr().setLineColor(8);
        f.fit(h,"N");
        f.attr().set("lc=4,lw=4,ls=5");
        c.draw(h,"").draw(f,"same");
        
        GeneticDataFitter gf = new GeneticDataFitter();
        gf.fit(h, 0.0,1.0);
        
        gf.gfunc.function.attr().set("lc=5,lw=3,ls=1");
        F1D func = gf.gfunc.function;
        List<String>  lines = func.getStats("M");
        PaveText pt = new PaveText(lines,0.05,0.95);
        //func.attr().setLineColor(5);
        c.draw(func,"same").draw(pt);
        f.show();     
        
        func.show();
    }
}
