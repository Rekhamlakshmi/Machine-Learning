package adaboosting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.lang.Math.*;
import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 *
 * @author Rekha
 */
public class AdaBoosting {

    /**
     * @param args the command line arguments
     */
   // static ArrayList<Double> error_prob=new ArrayList<>() ;
    double CalculateThreshold(double[] xvalues, int[] yvalues, double[] probabilities, double[] goodnessweight, double[] threshold, int q, ArrayList<ArrayList<Double>> fvalues, double bound, char[] c) {

        ArrayList<Double> error_prob = new ArrayList<>();
        double ht_error = 0;
        double normalizationfac = 0;
       // char c;

        int arraysize = yvalues.length;
        double[] normalizedprobabilities = new double[arraysize];
        int[] correctness = new int[yvalues.length];

        int[] h1 = new int[arraysize];
        int aindex;
        int bindex;
        ArrayList<Integer> errors = new ArrayList<>();
        ArrayList<Integer> indexes = new ArrayList<>();
        double[] prenormalised_array = new double[arraysize];
        ArrayList<Double> fval = new ArrayList<>();

        ArrayList<ArrayList<Integer>> final_wrong = new ArrayList<ArrayList<Integer>>();
        int errorsize = 0;
        //Special Case when bindex=0
        bindex = 0;
        int count = 0;
        double zeroerror = 0;
        ArrayList<Integer> zerowrong = new ArrayList<>();
        for (int j = bindex; j < yvalues.length; j++) {
            if (yvalues[j] != yvalues[0]) {
                count++;
                zeroerror += probabilities[j];
                zerowrong.add(j);
                // System.out.println("b prob "+probabilities[j]);
            }
        }

        error_prob.add(zeroerror);
        indexes.add(0);
        final_wrong.add(zerowrong);

        for (int i = 1; i < yvalues.length; i++) {
            int acount = 0;
            int bcount = 0;
            int a = yvalues[i - 1];
            int b = yvalues[i];
            double error = 0;
            ArrayList<Integer> wrong = new ArrayList<>();
            if (a != b) {
                aindex = i - 1;
                bindex = i;
            //    System.out.println("Index of a "+(i-1));
                //   System.out.println("Index of b "+(i));
                for (int j = 0; j < aindex; j++) {
                    if (yvalues[j] != a) {
                        acount++;
                        error += probabilities[j];
                        wrong.add(j);

                    }
                }
                for (int j = bindex; j < yvalues.length; j++) {
                    if (yvalues[j] != b) {
                        bcount++;
                        error += probabilities[j];
                        wrong.add(j);

                    }
                }
                errors.add(acount + bcount);

                indexes.add(aindex);
                error_prob.add(error);
//                for(int k=0;k<wrong.size();k++)
//                {
//                    System.out.println("Wrong values "+wrong.get(k));
//                }
                final_wrong.add(wrong);

            }

        }
        double min = error_prob.get(0);
        int minindex = indexes.get(0);
        int valindex = 0;
        for (int k = 0; k < error_prob.size(); k++) {

          //  System.out.println("Error is "+error_prob.get(k));
            //  System.out.println("Index of a is "+indexes.get(k));
            if (error_prob.get(k) < min) {
                min = error_prob.get(k);
                minindex = indexes.get(k);
                valindex = k;
            }
        }
        int v = minindex;
        int w = minindex + 1;

        // Special Case: When valindex = 0
        /**
         * ************************************************************************
         */
//        if(valindex==0)
//        {
//            double r=error_prob.get(0);
//            double s=error_prob.get(1);
//            System.out.println("Before 0 "+error_prob.get(0));
//            System.out.println("Between 0 and 1 "+error_prob.get(1));
//            if(r<s)
//            {
//                threshold[q]=0;
//                c='>';
//                
//            }
//            else
//            {
//              threshold[q]=(xvalues[v]+xvalues[w])/2;
//              if(yvalues[minindex]>0)
//              {
//                c='<';
//              }
//            else
//            {
//                c='>';
//            }
//            }
//            
//        }
//        else
        /**
         * ******************************************************************
         */
        {
       // System.out.println("Min error is "+min);
            // System.out.println("Min index is "+v);
            threshold[q] = (xvalues[v] + xvalues[w]) / 2;
            if (yvalues[minindex] > 0) {
                c[q] = '<';
            } else {
                c[q] = '>';
            }
        }

        System.out.println("The selected weak classifier h_" + (q + 1) + ": I(x " + c[q] + " " + threshold[q] + ")");
        ht_error = error_prob.get(valindex);
        System.out.println("The error e of h_" + (q + 1) + ": " + ht_error);
        goodnessweight[q] = CalculateWeight(ht_error);
        System.out.println("The weight of h_" + (q + 1) + ": " + goodnessweight[q]);
        ArrayList<Integer> wrongarray = final_wrong.get(valindex);
//        for(int i=0;i<wrongarray.size();i++)
//        {          
//                System.out.println("Wrong indexes are "+wrongarray.get(i));        
//        }
        int g = 0;
        for (int h = 0; h < yvalues.length; h++) {
            correctness[h] = 1;
        }

        for (int i = 0; i < wrongarray.size(); i++) {
            correctness[wrongarray.get(i)] = 0;

        }
//         for(int h=0;h<yvalues.length;h++)
//        {
//            System.out.print(correctness[h]+" ");
//        }
//        System.out.println(" ");
        CalculateQ(correctness, goodnessweight[q], arraysize, probabilities, prenormalised_array);
        for (int i = 0; i < arraysize; i++) {
            //   System.out.print(prenormalised_array[i]+" ");
            normalizationfac += prenormalised_array[i];

        }

        System.out.println("Normalization Factor Zt :" + normalizationfac);
        bound *= normalizationfac;

        CalculateNormalizedProb(prenormalised_array, normalizationfac, normalizedprobabilities);
        System.out.print("Normalized Probabilities :");
        for (int i = 0; i < arraysize; i++) {
            System.out.print(normalizedprobabilities[i] + " ");
        }
        System.out.println(" ");
        if (q == 0) {
            System.out.println("The boosted classifier:f_" + (q + 1) + "(x)= " + goodnessweight[q] + " I(x " + c[q] + " " + threshold[q] + ")");
        } else {
            System.out.print("The boosted classifier:f_" + (q + 1) + "(x)= ");
            for (int p = 0; p <= q; p++) {
                if (p < q) {
                    System.out.print(goodnessweight[p] + " I(x " + c[p] + " " + threshold[p] + ")+");
                } else {
                    System.out.print(goodnessweight[p] + " I(x " + c[p] + " " + threshold[p] + ")");
                }
            }
            System.out.println(" ");
        }

        System.arraycopy(normalizedprobabilities, 0, probabilities, 0, probabilities.length);

        /**
         * *****Special Case: Valindex=0*********
         */
//        if(valindex==0)
//        {
//            v=0;
//            w=0;
//           
//        }
        /**
         * ***************************************
         */
        int a1 = yvalues[v];
        int b1 = yvalues[w];
        for (int j = 0; j <= v; j++) {
            h1[j] = a1;
        }
        for (int j = w; j < yvalues.length; j++) {
            h1[j] = b1;
        }
        //
        // Temp F array multiplied with alpha
        for (int y = 0; y < h1.length; y++) {
            fval.add(h1[y] * goodnessweight[q]);
        }

        fvalues.add(fval);
        // System.out.println("Bound: "+errorbound);
        return bound;
    }

    void CalculateNormalizedProb(double[] prenormalised_array, double normalizationfac, double[] normalizedprobabilities) {
        for (int i = 0; i < prenormalised_array.length; i++) {
            normalizedprobabilities[i] = (prenormalised_array[i] / normalizationfac);
        }
    }

    void CalculateQ(int[] correctness, double alpha, int arraysize, double[] probabilities, double[] prenormalised_array) {
        double[] q_array = new double[arraysize];
        // double[] prenormalised_array=new double[arraysize];
        for (int i = 0; i < arraysize; i++) {
            if (correctness[i] == 1) {
                q_array[i] = exp((-alpha));
            } else {
                q_array[i] = exp((alpha));
            }
        }

        // System.out.print("Q array");
        for (int i = 0; i < arraysize; i++) {
            prenormalised_array[i] = q_array[i] * probabilities[i];
        }

    }

    double CalculateWeight(double ht_error) {
        double n = 1 - ht_error;
        double m = ht_error;
        double alpha = 0.5 * log(n / m);
        return alpha;
    }

    public static void main(String[] args) throws IOException {
        String first, second, third, fourth;
        double normalizationfac = 0;
        double bound = 1;
        int iterations, size;
        double epsilon;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter the name of the input file");
        String s = br.readLine();
        BufferedReader br1 = new BufferedReader(new FileReader(s));
        first = br1.readLine();
        String[] information = first.split(" ");
        iterations = Integer.parseInt(information[0]);
        size = Integer.parseInt(information[1]);
        epsilon = Double.parseDouble(information[2]);
    //  System.out.println("Number of iterations is "+iterations);
        //  System.out.println("Size of the input is "+size);
        //  System.out.println("Epsilon is "+epsilon);

        double[] goodnessweight = new double[iterations];
        double[] threshold = new double[iterations];
        double[] xvalues = new double[size];
        int[] yvalues = new int[size];
        double[] probabilities = new double[size];
        double[] f = new double[size];
        double[] errorcount = new double[iterations];
        char[] c = new char[iterations];
        second = br1.readLine();
        String[] input = second.split(" ");
        for (int i = 0; i < input.length; i++) {
            xvalues[i] = Double.parseDouble(input[i]);
        }
//        System.out.println("The x values are :");
//        for(int i=0;i<xvalues.length;i++)
//      {
//          System.out.println(xvalues[i]);
//      }

        third = br1.readLine();
        String[] y = third.split(" ");
        for (int i = 0; i < y.length; i++) {
            yvalues[i] = Integer.parseInt(y[i]);
        }
        //      System.out.println("The y values are :");
//      for(int i=0;i<yvalues.length;i++)
//      {
//          System.out.println(yvalues[i]);
//      }
        fourth = br1.readLine();
        String[] prob = fourth.split(" ");
        for (int i = 0; i < prob.length; i++) {
            probabilities[i] = Double.parseDouble(prob[i]);
        }
//      System.out.println("The probabilities are :");
//      for(int i=0;i<probabilities.length;i++)
//      {
//          System.out.println(probabilities[i]);
//      }

        AdaBoosting ada = new AdaBoosting();
        ArrayList<ArrayList<Double>> fvalues = new ArrayList<ArrayList<Double>>();
        for (int q = 0; q < iterations; q++) {
            System.out.println("\n");
            System.out.println("Iteration " + (q + 1));

            bound = ada.CalculateThreshold(xvalues, yvalues, probabilities, goodnessweight, threshold, q, fvalues, bound, c);

            for (int n = 0; n < size; n++) {
                f[n] = f[n] + fvalues.get(q).get(n);
            }
//        System.out.print("Farray:");
//        for(int i=0;i<size;i++)
//        {
//            System.out.print(f[i]+"\t");
//        }
//        System.out.println(" ");
            for (int i = 0; i < iterations; i++) {
                errorcount[i] = 0;
            }

            for (int i = 0; i < size; i++) {
                if ((f[i] > 0 && yvalues[i] < 0) || (f[i] < 0 && yvalues[i] > 0)) {
                    errorcount[q]++;
                }
            }
            //    System.out.println("No.of wrongly classified examples "+errorcount[q]);
            System.out.println("Error Et:" + errorcount[q] / size);
            System.out.println("Bound on Et: " + bound);

        }
    }

}
