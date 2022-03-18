package lon;

/**
 * Class with methods for performing local search/hill climbing on candidate
 * solutions with fixed Hamming weights.
 * 
 */

import boolfun.*;
import genalg.*;
import java.util.Arrays;

//class for the structure that saves function, its walsh transform and fitness function value
class SearchSolution{
    boolean[] function;
    int[] walsht;
    double fitness;
    int nvar;

    public SearchSolution(int nvar) {
        int n = (int)Math.pow(2,nvar);
        function = new boolean[n];
        walsht = new int[n];
        fitness = 0;        
    }
    
    public SearchSolution(boolean[] function, int[] walsht, double fitness, int nvar) {
        int n = (int)Math.pow(2,nvar);
        this.function = new boolean[n];
        this.walsht = new int[n];
        System.arraycopy(function,0,this.function,0,n);
        System.arraycopy(walsht,0,this.walsht,0,n);
        this.fitness = fitness;
        this.nvar = nvar;
    }
    
    public void updateFunction(boolean[] function, int[] walsht, double fitness){
        int n = function.length;
        System.arraycopy(function,0,this.function,0,n);
        System.arraycopy(walsht,0,this.walsht,0,n);
        this.fitness = fitness;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchSolution other = (SearchSolution) obj;
        if (Double.doubleToLongBits(this.fitness) != Double.doubleToLongBits(other.fitness)) {
            return false;
        }
        if (this.nvar != other.nvar) {
            return false;
        }
        if (!Arrays.equals(this.function, other.function)) {
            return false;
        }
        if (!Arrays.equals(this.walsht, other.walsht)) {
            return false;
        }
        return true;
    }
    
    
}

public class LocalSearchTools {
    
    /*public static int[] swap(boolean[] function, int y, int z){
        
    }*/
    
    /**
     * Update the Walsh transform of a Boolean function in an efficient way
     * when two bit positions y,z in the truth table are swapped.
     * 
     * @param function  truth table of the function
     * @param walsht    Walsh transform of the function
     * @param nvar      number of variables
     * @param y         position of the first bit in the truth table to swap
     * @param z         position of the second bit in the truth table to swap
     * @return 
     */
    public static int[] updateWHTSwap(boolean[] function, int[] walsht,
            int nvar, int y, int z) {
        
        int[] upwalsht = new int[walsht.length];
        
        //Convert y and z in binary to compute scalar products in the loop
        boolean[] biny = BinTools.dec2BinMod(y, nvar);
        boolean[] binz = BinTools.dec2BinMod(z, nvar);
        
        //Compute the term (-1)^f(z) - (-1)^f(y) which is constant in updating
        //all Walsh coefficients
        int polfy = 0;
        if(function[y]) {
            polfy = -1;
        } else {
            polfy = 1;
        }
        int polfz = 0;
        if(function[z]) {
            polfz = -1;
        } else {
            polfz = 1;
        }
        int deltaf = polfz - polfy;
        
        //Main loop: go over all 2^nvar Walsh coefficients
        for(int a=0; a<walsht.length; a++) {
            
            boolean[] bina = BinTools.dec2BinMod(a, nvar);
            boolean ay = BinTools.scalarProduct(bina, biny);
            int polay = 0;
            if(ay) {
                polay = -1;
            } else {
                polay = 1;
            }
            boolean az = BinTools.scalarProduct(bina, binz);
            int polaz = 0;
            if(az) {
                polaz = -1;
            } else {
                polaz = 1;
            }
            
            //Compute the updated term as wt[a] + delta, with
            //delta = [(-1)^f(z) - (-1)^f(y)] * [(-1)^ay - (-1)^az]
            upwalsht[a] = walsht[a] + (deltaf * (polay - polaz));
            
        }
        
        return upwalsht;
        
    }
    
    /**
     * Generate and evaluate the neighborhood solutions
     * 
     * @param function  truth table of the function
     * @param fitness   fitness function value for the function
     * @param walsht    Walsh transform of the function
     * @param nvar      number of variables
     * @return 
     */
    public static SearchSolution findBestNeighbor(boolean[] function, double fitness, 
            int[] walsht, int nvar) {
        
        SearchSolution best = new SearchSolution(function, walsht, fitness, nvar);
        int n=function.length;
        boolean[] tempFunction = new boolean[n];
        
        //genereting all neighboors
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                //swap only if the elements are different
                System.arraycopy(function, 0, tempFunction, 0, n);
                if(function[i]!=function[j]){
                    //swap i and j
                    tempFunction[i] = !tempFunction[i];
                    tempFunction[j] = !tempFunction[j];
                    //evaluate new solution
                    int[] upwalsht = updateWHTSwap(function, walsht, nvar, i, j);
                    double upfitness = FitnessFunctions.compFitnessBFGlob(upwalsht, nvar, false);
                    
                    //check if the new solution is better 
                    //save the better one
                    if(upfitness>fitness){
                         best.updateFunction(tempFunction, upwalsht, upfitness);
                    }
                }
            }
            
        }
        
        return best;
    }
    
    /**
     * Hill-climbing method that uses function findBestNeighbor
     * 
     * @param function  truth table of the function
     * @param fitness   fitness function value for the function
     * @param walsht    Walsh transform of the function
     * @param nvar      number of variables
     * @return 
     */
    public static SearchSolution hillClimb(boolean[] function, double fitness, 
            int[] walsht, int nvar){
        
        SearchSolution best = new SearchSolution(function, walsht, fitness, nvar);
        SearchSolution temp = new SearchSolution(nvar);
        do{
            temp.updateFunction(best.function, best.walsht, best.fitness);
            best = findBestNeighbor(best.function, best.fitness, best.walsht, nvar);
        }while(temp.fitness != best.fitness);
        
        return best;
    }
    
    //Test main
    public static void main(String[] args) {
        
        //f(x1,x2,x3) = x1 XOR x2 XOR x2
        boolean[] function = {false, true, true, false, true, false, false, true};
        int nvar = 3;
        
        //Walsh transform
        int[] walsht = BinTools.bin2Pol(function);
        int sprad = BoolTransf.calcFWT(walsht, 0, walsht.length);
        
        //Fitness evaluation (nonlinearity only, the function is balanced)
        double fitness = FitnessFunctions.compFitnessBFGlob(walsht, nvar, false);
        
        //Swap two positions y and z and update Walsh transform
        int y = 3;
        int z = 7;
        int[] upwalsht = updateWHTSwap(function, walsht, nvar, y, z);
        double upfitness = FitnessFunctions.compFitnessBFGlob(upwalsht, nvar, false);
        
        //Update truth table
        boolean[] upfunction = new boolean[function.length];
        System.arraycopy(function, 0, upfunction, 0, function.length);
        boolean temp = upfunction[y];
        upfunction[y] = upfunction[z];
        upfunction[z] = temp;
        
        //Compute from scratch Walsh transform for double-check (UpWalsh*)
        int[] upwalsht1 = BinTools.bin2Pol(upfunction);
        BoolTransf.calcFWT(upwalsht1, 0, upwalsht.length);
        double upfitness1 = FitnessFunctions.compFitnessBFGlob(upwalsht1, nvar, false);
        
        //print results
        System.out.println("x\tf(x)\tWalsh\tf'(x)\tUpWalsh\tUpWalsh*");
        for(int i=0; i<walsht.length; i++) {
            System.out.println(BinTools.bool2Bin(BinTools.dec2BinMod(i, nvar))+
                    "\t"+BinTools.singleBool2Bin(function[i])+
                    "\t"+walsht[i]+"\t"+BinTools.singleBool2Bin(upfunction[i])+
                    "\t"+upwalsht[i]+"\t"+upwalsht1[i]);
        }
        System.out.println("\nfit(f) = "+fitness);
        System.out.println("fit(f') = "+upfitness);
        System.out.println("fit(f')* = "+upfitness1);
        
        //simple test for hill climbing
        SearchSolution locopt = hillClimb(function, fitness, walsht, nvar);
        System.out.println("");
        System.out.println("x\tf'(x)");
        for(int i=0; i<walsht.length; i++) {
            System.out.println(BinTools.bool2Bin(BinTools.dec2BinMod(i, nvar))+
                    "\t"+BinTools.singleBool2Bin(locopt.function[i]));
        }
        System.out.println("fit(f') = "+locopt.fitness);
        
    }
    
}
