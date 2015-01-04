/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package realadaboosting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;
import java.util.ArrayList;

/**
 *
 * @author Rekha
 */
public class RealAdaboosting {

     void CalculateNormalizedProb(double[] prenormalised_array,double normalizationfac,double[] normalizedprobabilities)
    {
        for(int i=0;i<prenormalised_array.length;i++)
        {
            normalizedprobabilities[i]=(prenormalised_array[i]/normalizationfac);
        }
    }
    
    
    
    
   
     double CalculateG(double[] xvalues,int[] yvalues,double[] probabilities,double[] threshold,int q,double epsilon,ArrayList<ArrayList<Double>> fvalues,double bound)
    {
           int arraysize=yvalues.length;
           int aindex;
           int bindex;
           char c;
           int[] h=new int[arraysize];
           int[] h1=new int[arraysize];
           double[] garray=new double[arraysize];
           double[] prenormalizedprob=new double[arraysize];
           double[] normalizedprob=new double[arraysize];
           double zvalue=0;
          //  double bound=1;
            ArrayList<Integer> indexes=new ArrayList<>() ;
            ArrayList<Double> rplus=new ArrayList<>();
            ArrayList<Double> rminus=new ArrayList<>();
            ArrayList<Double> wplus=new ArrayList<>();
            ArrayList<Double> wminus=new ArrayList<>();
            ArrayList<Double> gvalues=new ArrayList<>();
            ArrayList<Double> fval=new ArrayList<>();
            ArrayList<Double> ctplusvalues=new ArrayList<>();
            ArrayList<Double> ctminusvalues=new ArrayList<>();
           for(int i=1;i<yvalues.length;i++)
           {
            double rpluscount=0;
            double rminuscount=0;
            double wpluscount=0;
            double wminuscount=0;
            int a=yvalues[i-1];
            int b=yvalues[i];
            double error=0;
            
            ArrayList<Integer> wrong=new ArrayList<>();
            if(a!=b)
            {       
                aindex=i-1;
                bindex=i;
              //  System.out.println("Index of a "+(i-1));
              //  System.out.println("Index of b "+(i));
                
                for(int j=0;j<=aindex;j++)
                {
                    h[j]=a;
                }
                for(int j=bindex;j<yvalues.length;j++)
                {
                    h[j]=b;         
                }
                for(int k=0;k<arraysize;k++)
                {
                    if(h[k]==1 && yvalues[k]==1)
                    {
                        rpluscount+=probabilities[k];
                    }
                     if(h[k]==-1 && yvalues[k]==-1)
                    {
                        rminuscount+=probabilities[k];
                    }
                      if(h[k]==-1 && yvalues[k]==1)
                    {
                        wpluscount+=probabilities[k];
                    }
                       if(h[k]==1 && yvalues[k]==-1)
                    {
                        wminuscount+=probabilities[k];
                    }
                }
                
               rplus.add(rpluscount);
               rminus.add(rminuscount);
               wplus.add(wpluscount);
               wminus.add(wminuscount);
               double gvalue= sqrt(rpluscount*wminuscount)+sqrt(wpluscount*rminuscount);
               double ctplus=0.5*log((rpluscount+epsilon)/(wminuscount+epsilon));
               double ctminus=0.5*log((wpluscount+epsilon)/(rminuscount+epsilon));
               
               gvalues.add(gvalue);
               ctplusvalues.add(ctplus);
               ctminusvalues.add(ctminus);
               indexes.add(aindex);
               
//                System.out.println("rplus="+rpluscount);
//                System.out.println("rminus="+rminuscount);
//                System.out.println("wplus="+wpluscount);
//                System.out.println("wminus="+wminuscount);
//                System.out.println("GValue="+gvalue);
            }  
           }
        //   System.out.println("GVALUES SIZE:"+ gvalues.size());
            double ming=gvalues.get(0); 
            double minctplus=ctplusvalues.get(0);
            double minctminus=ctminusvalues.get(0);
            int minindex=indexes.get(0);;
            int valindex=0;
            for(int p=0;p<gvalues.size();p++)
            {
                if(gvalues.get(p)<ming)
                {
                    ming=gvalues.get(p);
                    valindex=p;
                    minindex=indexes.get(p);      
                }
            }
            
          
            int v=minindex;
            int w=minindex+1;
       // System.out.println("Min index is "+v);
        threshold[q]=(xvalues[v]+xvalues[w])/2;
        if(yvalues[minindex]>0)
        {
            c='<';
        }
        else
        {
            c='>';
        }
        
        System.out.println("The selected weak classifier h_"+(q+1)+": I(x "+c+" "+threshold[q]+")");
        System.out.println("The G error value of h_"+(q+1)+": "+ming);
        System.out.println("Ct+ : "+ctplusvalues.get(valindex));  
        System.out.println("Ct- : "+ctminusvalues.get(valindex));
        int a1= yvalues[v];
        int b1=yvalues[w];
         for(int j=0;j<=v;j++)
         {
                    h1[j]=a1;
         }
         for(int j=w;j<yvalues.length;j++)
         {
                    h1[j]=b1;         
         }
         for(int d=0;d<arraysize;d++)
         {
             if(h1[d]==1)
             {
             garray[d]=ctplusvalues.get(valindex);
             }
             if(h1[d]==-1)
             {
              garray[d]=ctminusvalues.get(valindex);
             }
         }
         
         for(int e=0;e<arraysize;e++)
         {
             prenormalizedprob[e]=probabilities[e]*(exp(-yvalues[e]*garray[e]));
             zvalue+=prenormalizedprob[e];
             
         }
         System.out.println("The probabilities normalization factor Zt: "+zvalue);
//         System.out.print("Prenormalized Probabilities: ");
//         for(int d=0;d<arraysize;d++)
//         {
//             System.out.print(prenormalizedprob[d]+" ");
//         }
//         System.out.println(" ");
         CalculateNormalizedProb(prenormalizedprob,zvalue,normalizedprob);
         System.out.print("Normalized Probabilities: ");
         for(int d=0;d<normalizedprob.length;d++)
         {
             System.out.print(normalizedprob[d]+" ");
         }
         System.out.println(" ");
         System.arraycopy(normalizedprob, 0, probabilities, 0, normalizedprob.length);
         
         for(int s=0;s<garray.length;s++)
         {
             fval.add(garray[s]);
         }
         
         fvalues.add(fval);
         bound=bound*zvalue;
         return bound;
    }
    
    
    public static void main(String[] args) throws IOException 
    {
      String first,second,third,fourth;
      double normalizationfac=0;
      double errorbound=1;
      int iterations,size;
      double epsilon;
      double bound=1;
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));  
      System.out.println("Enter the name of the input file");
      String s = br.readLine();
      BufferedReader br1 = new BufferedReader(new FileReader(s));
      first=br1.readLine();
      String[] information = first.split(" ");
      iterations = Integer.parseInt(information[0]);
      size=Integer.parseInt(information[1]);
      epsilon=Double.parseDouble(information[2]);
   //   System.out.println("Number of iterations is "+iterations);
   //  System.out.println("Size of the input is "+size);
   //   System.out.println("Epsilon is "+epsilon);
      double[] xvalues=new double[size];
      int[] yvalues=new int[size];
      double[] errorcount=new double[iterations];
      double[] probabilities=new double[size];
      double[] threshold=new double[iterations];
      second=br1.readLine();
      String[] input=second.split(" ");
      for(int i=0;i<input.length;i++)
      {
          xvalues[i]=Double.parseDouble(input[i]);
      }
//        System.out.println("The x values are :");
//        for(int i=0;i<xvalues.length;i++)
//      {
//          System.out.println(xvalues[i]);
//      }
//      
      third=br1.readLine();
      String[] y=third.split(" ");
      for(int i=0;i<y.length;i++)
      {
          yvalues[i]=Integer.parseInt(y[i]);
      }
//      System.out.println("The y values are :");
//      for(int i=0;i<yvalues.length;i++)
//      {
//          System.out.println(yvalues[i]);
//      }
      fourth=br1.readLine();
      String[] prob=fourth.split(" ");
      for(int i=0;i<prob.length;i++)
      {
          probabilities[i]=Double.parseDouble(prob[i]);
      }
//      System.out.println("The probabilities are :");
//      for(int i=0;i<probabilities.length;i++)
//      {
//          System.out.println(probabilities[i]);
//      }
      
      RealAdaboosting ada=new RealAdaboosting();
      ArrayList<ArrayList<Double>> fvalues=new ArrayList<ArrayList<Double>>();
   
      double[] f=new double[size];
      for(int q=0;q<iterations;q++)
      {
        System.out.println(" ");
        System.out.println("Iteration "+ (q+1));
        bound=ada.CalculateG(xvalues, yvalues, probabilities,threshold,q,epsilon,fvalues,bound);
        for(int n=0;n<size;n++)
        {
            f[n]=f[n]+fvalues.get(q).get(n);
        }
          System.out.print("f_"+(q+1)+"(x): ");
        for(int i=0;i<size;i++)
        {
            System.out.print(f[i]+" ");
        }
        for(int i=0;i<iterations;i++)
        {
            errorcount[i]=0;
        }
        
         // System.out.println("Error Et");
          System.out.println(" ");
          for(int i=0;i<size;i++)
          {
              if((f[i]>0 && yvalues[i]<0)|| (f[i]<0 && yvalues[i]>0))
              {
                  errorcount[q]++;
              }
          }
          System.out.println("Error Et:"+errorcount[q]/size);
          System.out.println("Bound on Et :"+bound);
     }
    }
    
}
