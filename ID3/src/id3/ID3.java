/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package id3;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ID3 
{ 
    public static double[] count=new double[3];
    public static double[] probability=new double[3];
  //  public static int[] target;
    static int m;
    public void initialise()
    {
        for(int i=0;i<3;i++)
        {
            count[i]=0;
            probability[i]=0;
        }
    }
    public double Log2(double value)
    {
        return Math.log(value)/Math.log(2);
    }
    
    private void InitTarget(int[] target,int[][] features,int nrows,int ncols)
    {
        
        for(int i=0;i<nrows;i++)
        {
            for(int j=0;j<ncols;j++)
            {
                if(j==(ncols-1))
                {
                    target[i]=features[i][j];
                    
                }
            }
        }

    }
 public double CalculateEntropy(int attribute,double count)
    {
        if(attribute==0||count==0)
            return 0;
        else
        {
        double a=attribute/count;
        return (a*Log2(1/a));
        }
    }
 
    public double CalculateTotalEntropy(int[] target,int nrows,int ncols)
    {
        int zerocount=0;
        int onecount=0;
        for(int i=0;i<nrows;i++)
        {
            if(target[i]==0)
               zerocount++;
            else if(target[i]==1)
               onecount++;
        }
        double entropy=CalculateEntropy(zerocount,nrows)+CalculateEntropy(onecount,nrows);
        return entropy;
   }
        
    public  double  ConditionalEntropy(int[][] features,double nrows,double ncols,int index,int[] target)
    {
        int[] atargetcount=new int[2];
        int[] btargetcount=new int[2];
        int[] ctargetcount=new int[2];
        double total=0;
        double a=0;
        double b=0;
        double c=0;
        double aentropy;
        double bentropy;
        double centropy;
        initialise();
        for(int i=0;i<nrows;i++)
        {
            for(int j=0;j<ncols;j++)
            {
                if(j==index)
                {
                    if(features[i][j]==0)
                    {
                        count[0]++;
                        if(target[i]==0)
                            atargetcount[0]++;
                        else
                            atargetcount[1]++;
                    }
                    else if(features[i][j]==1)
                    {
                        count[1]++;
                        if(target[i]==0)
                            btargetcount[0]++;
                        else
                            btargetcount[1]++;
                    }
                    else if(features[i][j]==2)
                    {
                        count[2]++;
                        if(target[i]==0)
                            ctargetcount[0]++;
                        else
                            ctargetcount[1]++;
                    }
                  
                 }
            }
        }
        for(int i=0;i<3;i++)
        {
            if(count[i]==0)
               probability[i]=0;
            else
            probability[i]=(count[i]/nrows);
        }
       
        for(int val : atargetcount)
        {
            a+=CalculateEntropy(val,count[0]);
        }
        if(probability[0]==0)
        {
        aentropy=0;
        }
        else
        {
        aentropy=probability[0]*a;
        }
        
        for(int val : btargetcount)
        {
            b+=CalculateEntropy(val,count[1]);
        }
        if(probability[1]==0)
        {
        bentropy=0;
        }
        else
        {
        bentropy=probability[1]*b;
        }
        for(int val : ctargetcount)
        {
            c+=CalculateEntropy(val,count[2]);
        }
         if(probability[2]==0)
         {
        centropy=0;
         }
        else
         {
        centropy=probability[2]*c;
         }
         total=aentropy+bentropy+centropy;
        return total;
    }
    
    public double CalculateGain(double totalentropy,double entropy)
    {
        return totalentropy-entropy;
    }
    
    public int max(double[] gain)
    {
        double maximum=gain[0];
        int index=0;
        
        for(int i=1;i<gain.length;i++)
        {
            if(gain[i]>maximum)
            {
                maximum=gain[i];
                index=i;
            }
        }
        return index;
    }
     public double maxgainvalue(double[] gain)
    {
        double maximum=gain[0];
        
        
        for(int i=1;i<gain.length;i++)
        {
            if(gain[i]>maximum)
            {
                maximum=gain[i];
               
            }
        }
        return maximum;
    }

    public void Create_Partition(int[] input,int[][] features,int nrows,int ncols,int[] temptarget,int[][] temp)
    {
        int b;
        int len=input.length;
        
        for(b=0;b<len;b++)
        {
            for(int i=0;i<nrows;i++)
            {
                 if(i==input[b]-1)
                 {
                    for(int j=0;j<ncols;j++)
                    {
                        temp[b][j]=features[i][j];
                    }
                }
            }
        }

      
    }
    public void Split(String partitionfile,int attribute_to_split,int nrows,int ncols,int[][] features,int linecnt,String outputfile) throws IOException
    {
        
         int input[];
         int[] temptarget;
         int[][]temp;
         int len;
         int[] gain_index;
         String[] value=new String[linecnt];
         String partition_to_replace=" ";
         double  split_attribute=0;
         int num_partitions=0;
         double total_temp_entropy;
         double[] temp_entropy;
         double[] temp_gain;
         double[] temp_attribute_to_split;
         double[] svalue;
         double[] fvalue;
         String[] inputarray=null;
         String[] name=new String[linecnt];
         temp_attribute_to_split=new double[linecnt];
         svalue=new double[linecnt];
         fvalue=new double[linecnt];
         gain_index=new int[linecnt];
         temp_entropy=new double[ncols-1];
         temp_gain=new double[ncols-1];
         BufferedReader br = new BufferedReader(new FileReader(partitionfile));
         String line;
       
        while((line=br.readLine())!=null)
        { 
             
             String[] data=line.split(" ");
             value[num_partitions]=line;
             input=new int[data.length-1];
             len=input.length;
             temptarget=new int[len];
             temp=new int[len][ncols];
              name[num_partitions]=data[0];
             
             for(int i=1;i<data.length;i++)
             {
                 input[i-1]=Integer.parseInt(data[i]);
                
             }
             Create_Partition(input,features,nrows,ncols,temptarget,temp);
             InitTarget(temptarget,temp,len,ncols);
             total_temp_entropy= CalculateTotalEntropy(temptarget,len,ncols);
          
             for(int i=0;i<ncols-1;i++)
            {
                temp_entropy[i]=ConditionalEntropy(temp,len,ncols,i,temptarget);
              
                temp_gain[i]=CalculateGain(total_temp_entropy,temp_entropy[i]);
           
                
            }
            temp_attribute_to_split[num_partitions]=maxgainvalue(temp_gain);
            gain_index[num_partitions]=max(temp_gain);
            svalue[num_partitions]=(((double)len)/nrows); 
            fvalue[num_partitions]=svalue[num_partitions]*temp_attribute_to_split[num_partitions];
            num_partitions++;
         }
         double maxf=max(fvalue);
         for(int k=0;k<linecnt;k++)
        { 
          if(k==maxf)
          {
              partition_to_replace=name[k];
              split_attribute=gain_index[k];
              String inputline=value[k];
              inputarray=inputline.split(" ");
          }
       }
      
        Split_Partition(inputarray,value,maxf,split_attribute,features,nrows,ncols,outputfile,linecnt,partition_to_replace);
    }
    
   public void Split_Partition(String[] inputarray,String[] value,double maxf,double split_attribute,int[][] features,int nrows,int ncols,String outputfile,int linecnt,String partition_to_replace) throws IOException
   {
       
       File file = new File(outputfile);
       int h=1;
       if (!file.exists()) 
       {
	file.createNewFile();
       }
	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	BufferedWriter bw = new BufferedWriter(fw);
        int[] integer_inputarray=new int[inputarray.length];
        ArrayList<Integer> apartition=new ArrayList<>() ;
        ArrayList<Integer> bpartition=new ArrayList<>() ;
        ArrayList<Integer> cpartition=new ArrayList<>() ;
        String partition_name=partition_to_replace;
        String original_partition=partition_to_replace;
        ArrayList<String> newnames=new ArrayList<>();
        int n=0;
        for(int i=1;i<inputarray.length;i++)
        {
          
           integer_inputarray[i-1]=Integer.parseInt(inputarray[i]);
        }
         for(int k=0;k<integer_inputarray.length;k++)
         {
             for(int i=0;i<nrows;i++)
             {
                 if(i==integer_inputarray[k]-1)
                 {
                     for(int j=0;j<ncols;j++)
                     {
                       if(j==split_attribute)
                       {
                           if(features[i][j]==0)
                           {
                               apartition.add(integer_inputarray[k]);
                           }
                           else if(features[i][j]==1)
                           {
                               bpartition.add(integer_inputarray[k]);
                           }
                           else
                               cpartition.add(integer_inputarray[k]);
                       }
                     }
                 }
             }
         }
        
         for(int k=0;k<linecnt;k++)
         {
             if(k!=maxf)
             {
                
                 bw.write(value[k]);
                 bw.write("\n");
             }
             else
             {
               
                 if(!apartition.isEmpty())
                 {
                     if((inputarray.length-1)==1||((inputarray.length-1)==apartition.size()))
                          bw.write(partition_name+" ");
                     else
                     {
                         String newname=partition_name.replace(partition_name,partition_name+(h++));
                         bw.write(newname+" ");
                         newnames.add(newname);
                     }
                  for(int i=0;i<apartition.size();i++)
                  { 
                    bw.write(apartition.get(i)+" ");
                  }
                  bw.write("\n");
                 }
                 
                 if(!bpartition.isEmpty())
                 {
                   if((inputarray.length-1)==1||((inputarray.length-1)==bpartition.size()))
                   { 
                       bw.write(partition_name.replace(partition_name,partition_name)+" ");
                   }
                     else
                   {
                       String newname=partition_name.replace(partition_name,partition_name+(h++));
                         bw.write(newname+" ");
                        newnames.add(newname);
                
                   }
                 
                  for(int i=0;i<bpartition.size();i++)
                  {
                    
                    bw.write(bpartition.get(i)+" ");
                  }
                bw.write("\n");

                 }
                if(!cpartition.isEmpty())
                 {
                      if((inputarray.length-1)==1||((inputarray.length-1)==cpartition.size()))
                          bw.write(partition_name.replace(partition_name,partition_name)+" ");
                     else
                      {
                          String newname=partition_name.replace(partition_name,partition_name+(h++));
                         bw.write(newname+" ");
                        newnames.add(newname);
                       
                      }
               for(int i=0;i<cpartition.size();i++)
                {
                    bw.write(cpartition.get(i)+" ");
                }
               bw.write("\n");
             }
             }
           }
         bw.close();
         if(newnames.isEmpty())
             System.out.println("No more partitioning possible;End of the algorithm.");
         else
         {    
         System.out.print("The partition "+original_partition+" was replaced with partitions ");
         for(int i=0;i<newnames.size();i++)
         {
             System.out.print(newnames.get(i)+",");
         }
         System.out.println("using feature "+((int)split_attribute+1));
         }
       
   }
  
    public static void main(String[] args) throws IOException 
    {
   
        int attribute_to_split;
        double total_entropy;
        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter names of the files dataset, input-partition, output-partition");
        String s = br1.readLine();
        String[] file=s.split(" ");
        BufferedReader br = new BufferedReader(new FileReader(file[0]));  
        String line,first; 
        int nrows,ncols;
        int k=0;
        first=br.readLine();
        String[] information = first.split(" ");
        nrows = Integer.parseInt(information[0]);
        ncols = Integer.parseInt(information[1]);
        int[] target=new int[nrows];
        double[] entropy= new double[ncols-1];
        double[] gain= new double[ncols-1];
        int[][] features=new int[nrows][ncols];     
        while((line=br.readLine())!=null)
        {
                String[] data=line.split(" ");
                for(int j=0;j<ncols;j++)
                {
                         
                   features[k][j]=Integer.parseInt(data[j]); 
                       
                }
                k++;

          }
        for(int i=0;i<ncols-1;i++)
        {
            entropy[i]=0;
            gain[i]=0;
        }
        ID3 id3 = new ID3();
        id3.InitTarget(target,features,nrows,ncols);
        total_entropy= id3.CalculateTotalEntropy(target,nrows,ncols);
        id3.initialise();
        for(int i=0;i<ncols-1;i++)
        {
            entropy[i]=id3.ConditionalEntropy(features,nrows,ncols,i,target);
            gain[i]=id3.CalculateGain(total_entropy,entropy[i]);
        }
        attribute_to_split=id3.max(gain);
        BufferedReader br2 = new BufferedReader(new FileReader(file[1]));
        String partitionline;
        int linecnt=0;
        while((partitionline=br2.readLine())!=null)
         { 
             linecnt++;
         }
        id3.Split(file[1],attribute_to_split,nrows,ncols,features,linecnt,file[2]);
         
    }  
}
