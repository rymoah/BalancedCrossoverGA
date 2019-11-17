package genalg;

/**
 * Class containing fitness functions implementations.
 */

import boolfun.*;
import oa.*;

public class FitnessFunctions {
    
    /**
     * Compute the fitness of a boolean function. The fitness equals the value
     * of nonlinearity of the function, computed through the Walsh transform,
     * if the boolean function is balanced. Otherwise, the fitness is the
     * nonlinearity of the function minus the unbalancedness.
     * 
     * @param function  A boolean function, represented by its truth table
     * @param nvar      number of variables of the boolean function
     * @param unbal     flag for specifying if unbalancedness must be considered
     * @return          the value of nonlinearity of the boolean function
     */
    public static double compFitnessBF(boolean[] function, int nvar, boolean unbal) {
        
        double fitness;
        
        //Step 1: convert the truth table in polar form (0 -> 1, 1 -> -1)
        int[] poltable = BinTools.bin2Pol(function);
        
        //Step 2: compute the Fast Walsh Transform over the polar table.
        //The returned value is the spectral radius (i.e. maximum absolute
        //value of the Walsh transform) of the function, used to compute the
        //nonlinearity later
        int sprad = BoolTransf.calcFWT(poltable, 0, function.length);
        
        //Step 3: compute the nonlinearity from the spectral radius, and set it
        //as the function fitness
        int nl = BoolTransf.calcNL(sprad, nvar);
        
        //Step 4: if unbal is set, subtract it from the nonlinearity to compute
        //the fitness function. Otherwise, use only nonlinearity
        if(unbal) {
            
            //No need to compute unbalancedness from scratch, the value
            //is the first coefficient in absolute value of the Walsh transform
            int unb = Math.abs(poltable[0]);
            fitness = nl - unb;
            
        } else {
            
            fitness = nl;
            
        }
        
        return fitness;
        
    }
    
    /**
     * Compute the fitness of a boolean function for the bent function problem.
     * fit(f)=sqrt(sum_(w \in F) (2^(nvar/2)-|Wf(w)|)^2)
     * 
     * @param function          a boolean function of nvar variables
     * @param nvar              number of variables of the boolean functions (must be even)
     * @param unbal             flag for specifying if unbalancedness must be considered
     * @return                  an array of fitnesses, one for each function in the population 
     */
    public static double compFitnessBent(boolean[] function, int nvar, boolean unbal) {
        
        double fitness = 0.0;
        double bentcoeff = Math.pow(2,nvar/2);
        
        //Step 1: convert the truth table in polar form (0 -> 1, 1 -> -1)
        int[] poltable = BinTools.bin2Pol(function);
        
        //Step 2: compute the Fast Walsh Transform over the polar table.
        //The returned value is the spectral radius (i.e. maximum absolute
        //value of the Walsh transform) of the function, used to compute the
        //nonlinearity later. In the poltable will be values of the Walsh Transform
        int sprad = BoolTransf.calcFWT(poltable, 0, function.length);
        
        //If unbalancedness must be considered, start the for loop from 0, otherwise from 1
        int start = 0;
        if(!unbal) {
            start = 1;
        }
        
        //Step 3: compute the fitness function: fit(function)=sqrt(sum_(w \in F) (2^(nvar/2)-|Wf(w)|)^2)
        //Loop over the Walsh Transform of the function
        for (int i = start; i < poltable.length; i++) {

            fitness+=(bentcoeff-Math.abs(poltable[i]))*(bentcoeff-Math.abs(poltable[i]));
            
        }
        
        return Math.sqrt(fitness);
        
    }
    
    /**
     * Compute the fitness of a binary matrix from being an Orthogonal Array of
     * specified parameters. The fitness is computed through the method
     * calcDistMatrix() from the class OrthogonalArrayTools, which counts for
     * the number of repeated t-uples in each submatrix, and normalizes the sum
     * using the L^p norm.
     * 
     * @param matrix    boolean matrix whose fitness must be computed 
     * @param t         strength of the OA
     * @param lambda    index of the OA
     * @param p         exponent in the L^p norm
     * @return 
     */
    public static double compFitnessOA(boolean[][] matrix, int t, 
            int lambda, int p) {
        
        double fitness = 0.0;
        
        int N = matrix.length;      //matrix rows
        int k = matrix[0].length;   //matrix columns
        
        //Step 1: convert the matrix from boolean to int (0-1 entries instead of true-false)
        int[][] nmatrix = new int[matrix.length][matrix[0].length];
        
        for(int i=0; i<matrix.length; i++) {
            
            for(int j=0; j<matrix[i].length; j++) {
                
                if(matrix[i][j]) {
                    
                    nmatrix[i][j] = 1;
                            
                } else {
                    
                    nmatrix[i][j] = 0;
                    
                }
                
            }
            
        }
        
        //Step 2: compute the distance of the matrix from being an OA(N,k,2,t)
        double matdist = OrthogonalArrayTools.calcDistMatrix(nmatrix, 2, t, lambda, p);
        fitness = matdist;
        
        return fitness;
        
    }
    
    /*---------------------------------------------------------------------------------------*/
    /* THE METHODS BELOW ITERATE THE THREE FITNESS FUNCTIONS OVER POPULATIONS OF INDIVIDUALS */
    /*---------------------------------------------------------------------------------------*/
    
    /**
     * Compute the fitness of a population of boolean functions (ie, iterates
     * the method compFitnessBF() over an array of boolean arrays).
     * 
     * @param population    a boolean matrix containing the boolean functions in the population
     * @param nvar          number of variables of the functions
     * @param unbal         unbalancedness flag for fitness function
     * @return              an array of fitness, one for each function in the population
     */
    public static double[] compFitnessBFPop(boolean[][] population, int nvar,
            boolean unbal) {
        
        double[] fitnesses = new double[population.length];
        
        for(int i=0; i<population.length; i++) {
            
            fitnesses[i] = compFitnessBF(population[i], nvar, unbal);
            
        }
        
        return fitnesses;
        
    }
    
    /**
     * Compute the fitness of a population of boolean functions (ie, iterates
     * the method compFitnessBent() over an array of boolean arrays).
     * 
     * @param population    a boolean matrix containing the boolean functions in the population
     * @param nvar          number of variables of the functions
     * @param unbal         unbalancedness flag for fitness function
     * @return              an array of fitness, one for each function in the population
     */
    public static double[] compFitnessBentPop(boolean[][] population, int nvar,
            boolean unbal) {
        
        double[] fitnesses = new double[population.length];
        
        for(int i=0; i<population.length; i++) {
            
            fitnesses[i] = compFitnessBent(population[i], nvar, unbal);
            
        }
        
        return fitnesses;
        
    }
    
    /**
     * Compute the fitness of a population of boolean matrices (ie, iterates
     * the method compFitnessOA() over an array of boolean matrices).
     * 
     * @param population    a boolean three-dimensional matrix containing the boolean matrices in the population
     * @param t             strength of the OA
     * @param lambda        index of the OA
     * @param p             exponent used in the L^p norm
     * @return              an array of fitness, one for each matrix in the population
     */
    public static double[] compFitnessOAPop(boolean[][][] population, int t,
            int lambda, int p) {
        
        double[] fitnesses = new double[population.length];
        
        for(int i=0; i<population.length; i++) {
            
            fitnesses[i] = compFitnessOA(population[i], t, lambda, p);
            
        }
        
        return fitnesses;
        
    }
    
}
