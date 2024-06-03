/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package twig.math;

/**
 * source: https://ssteinberg.xyz/2019/07/29/fast-complex-error-function/
 * @author gavalian
 */
public class FadeevaFunction {
    
    static int M = 4;
    static int N = 1 << (M-1);
    static Complex[]   A = new Complex[] {
        new Complex(+0.983046454995208, 0.0),
        new Complex(-0.095450491368505, 0.0),
        new Complex(-0.106397537035019, 0.0),
        new Complex(+0.004553979597404, 0.0),
        new Complex(-0.000012773721299, 0.0),
        new Complex(-0.000000071458742, 0.0),
        new Complex(+0.000000000080803, 0.0),
        new Complex(-0.000000000000007, 0.0)
    };
    static Complex[]   B = new Complex[] {
        new Complex(0.0, -1.338045597353875),
        new Complex(0.0, +0.822618936152688),
        new Complex(0.0, -0.044470795125534),
        new Complex(0.0, -0.000502542048995),
        new Complex(0.0, +0.000011914499129),
        new Complex(0.0, -0.000000020157171),
        new Complex(0.0, -0.000000000001558),
        new Complex(0.0, +0.000000000000003),
    };
    static Complex[]   C = new Complex[]{
        new Complex(0.392699081698724, 0.0),
        new Complex(1.178097245096172, 0.0),
        new Complex(1.963495408493621, 0.0),
        new Complex(2.748893571891069, 0.0),
        new Complex(3.534291735288517, 0.0),
        new Complex(4.319689898685965, 0.0),
        new Complex(5.105088062083414, 0.0),
        new Complex(5.890486225480862, 0.0),
    };
    static double s = 2.75;
    
    public static Complex fadeeva(double re, double im){
        double sgni = im<0 ? -1 : 1;
        double z_re = re*sgni;
        double z_im = im*sgni;
        Complex z = new Complex(re,im);
        z.mult(sgni);
        
        //z *= sgni;

        Complex t = new Complex(0,0.5);
        t.mult(s).add(z);
        
        Complex w = new Complex(0.0,0.0);
        
        for(int m = 0; m < N; ++m){
            Complex a1 = FadeevaFunction.cprod(t, B[m]).add(A[m]);
            Complex a2 = FadeevaFunction.csqr(C[m]).sub(FadeevaFunction.csqr(t));
            w.add(FadeevaFunction.cdiv(a1, a2));
        }
        
        if(sgni<0){
            FadeevaFunction.csqr(z).mult(-1.0);
        }
	// Approximate
	/*const vec2 t = z + vec2(0,0.5)*s;
	vec2 w = vec2(.0);
	for (int m=0; m<N; ++m)
		w += cdiv(A[m] + cprod(t,B[m]), csqr(C[m]) - csqr(t));

	// Invert back
	if (sgni < 0)
		w = 2.0*cexp(-csqr(z)) - w;

	return w;*/
        return w;
    }
    
    
    // Error function
    /*
    vec2 erf(vec2 z) {
        vec2 z_1i = cprod(vec2(0,1), z);	// 1i*z
        return vec2(1,0) - cprod(cexp(-csqr(z)), faddeeva(z_1i));
    }*/
    
    // Auxulary functions
    /*
    float real(vec2 z) 				{ return z.x; }
    float imag(vec2 z) 				{ return z.y; }
    vec2  cprod(vec2 a, vec2 b) 	{ return vec2(a.x*b.x-a.y*b.y, a.x*b.y+a.y*b.x); }
    vec2  csqr(vec2 a) 				{ return vec2(a.x*a.x-a.y*a.y, 2*a.x*a.y); }
    
    vec2  cdiv(vec2 a, vec2 b) 	{ return vec2(a.x*b.x+a.y*b.y, a.y*b.x-a.x*b.y) * (1.0/(b.x*b.x+b.y*b.y)); }
    vec2  cexpi(float d) 			{ return vec2(cos(d), sin(d)); }
    vec2  cexp(vec2 z) 				{ return exp(z.x) * cexpi(z.y); }
    */
    static Complex cprod(Complex a, Complex b){return new Complex(a.re*b.re - a.im*b.im, a.re*b.im+a.im*b.re);}
    static Complex csqr(Complex a){ return new Complex(a.re*a.re-a.im*a.im, 2*a.re*a.im);}
    static Complex cdiv(Complex a, Complex b){ return new Complex(a.re*b.re,a.im*b.re-a.re*b.im).mult(1/(b.re*b.re+b.im*b.im));}
    static Complex cexpi(double d){ return new Complex(Math.cos(d),Math.sin(d));}
    static Complex cexpi(Complex z) { return cexpi(z.im).mult(Math.exp(z.re));}
    
    public static class Complex { 
        public double re; public double im; 
        public Complex(double r, double i) { re =r; im = i;}
        public Complex mult(double v){ re *= v; im*=v; return this;}
        public Complex add(Complex v){ re += v.re; im+=v.im; return this;}
        public Complex sub(Complex v) { re -= v.re; im -= v.im; return this;}
        @Override
        public String toString(){ return String.format("complex = (%8.5f, %8.5f)", re,im);}
    }
    
    
    public static void main(String[] args){
        System.out.println(FadeevaFunction.fadeeva(2.5,2.5));
    }
}
