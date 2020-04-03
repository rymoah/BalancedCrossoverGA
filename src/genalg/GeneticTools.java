package genalg;

/**
 * Class containing methods for various genetic operators.
 */

import boolfun.BinTools;
import java.util.Random;
import boolfun.CombTools;

public class GeneticTools {
    
    /**
     * Find the position of the individual with maximum fitness value
     * 
     * @param fitnesses     array of fitness values of the population
     * @return the position in the array fitnesses holding the maximum value
     */
    public static int findMaxFitIndivPos(double[] fitnesses) {
        
        int maxfitpos = 0;
        
        for(int i=1; i<fitnesses.length; i++) {
            
            if(fitnesses[i] >= fitnesses[maxfitpos]) {
                maxfitpos = i;
            }
            
        }
        
        return maxfitpos;
        
    }
    
    /**
     * Find the position of the individual with minimum fitness value.
     * 
     * @param fitnesses     array of fitness values of the population
     * @return the position in the array fitnesses holding the minimal value
     */
    public static int findMinFitIndivPos(double[] fitnesses) {
        
        int minfitpos = 0;
        
        for(int i=1; i<fitnesses.length; i++) {
            
            if(fitnesses[i] < fitnesses[minfitpos]) {
                minfitpos = i;
            }
            
        }
        
        return minfitpos;
        
    }
    
    /**
     * Find the position of the individual with minimum fitness value.
     * 
     * @param fitnesses     matrix of fitness values of the population
     * @return the position in the fitnesses matrix holding the minimal value
     */
    public static int findMinFitIndivPosMatrix(double[][] fitnesses) {
        
        int minfitpos = 0;
        
        for(int i=1; i<fitnesses.length; i++) {
            
            if(fitnesses[i][3] < fitnesses[minfitpos][3]) {
                minfitpos = i;
            }
            
        }
        
        return minfitpos;
        
    }
    
    /**
     * Find the positions of the two individuals with the two lowest values of
     * fitness function (the values are not necessarily distinct)
     * 
     * @param fitnesses     array of fitness values of the population
     * @return 
     */
    public static int[] findMinFit2IndivPos(double[] fitnesses) {
        
        int[] minfitpos = new int[2];
        if(fitnesses[0] < fitnesses[1]) {
            minfitpos[0] = 0;
            minfitpos[1] = 1;
        } else {
            minfitpos[0] = 1;
            minfitpos[1] = 0;
        }
        
        for(int i=2; i<fitnesses.length; i++) {
            
            if(fitnesses[i] < fitnesses[minfitpos[0]]) {
                minfitpos[0] = i;
            } else {
                
                if((fitnesses[i] < fitnesses[minfitpos[1]]) && 
                   (fitnesses[i] >= fitnesses[minfitpos[0]])) {
                    
                    minfitpos[1] = i;
                    
                }
                
            }
            
        }
        
        return minfitpos;
        
    }
    
    /**
     * Find the positions of the two individuals with the two highest values of
     * fitness function (the values are not necessarily distinct)
     * 
     * @param fitnesses     array of fitness values of the population
     * @return 
     */
    public static int[] findMaxFit2IndivPos(double[] fitnesses) {
        
        int[] maxfitpos = new int[2];
        if(fitnesses[0] > fitnesses[1]) {
            maxfitpos[0] = 0;
            maxfitpos[1] = 1;
        } else {
            maxfitpos[0] = 1;
            maxfitpos[1] = 0;
        }
        
        for(int i=2; i<fitnesses.length; i++) {
            
            if(fitnesses[i] > fitnesses[maxfitpos[0]]) {
                maxfitpos[0] = i;
            } else {
                
                if((fitnesses[i] > fitnesses[maxfitpos[1]]) && 
                   (fitnesses[i] <= fitnesses[maxfitpos[0]])) {
                    
                    maxfitpos[1] = i;
                    
                }
                
            }
            
        }
        
        return maxfitpos;
        
    }
    
    /**
     * Tournament selection operator. Returns an array containing the positions
     * of the individuals selected for the tournament.
     * 
     * @param fitnesses     array of fitness values of the population
     * @param tsize         tournament size
     * @param genrand       a Random instance representing a pseudorandom generator
     * @param objective     flag specifying whether the objective of the problem
     *                      is to minimize or maximize the fitness function
     * @return 
     */
    public static int[] tournSelection(int popsize, double[] fitnesses,
            int tsize, Random genrand, boolean objective) {
        
        int[] candpos = new int[tsize];
        double[] candfit = new double[tsize];
        
        //Draw tsize individual at random from the population
        for(int i=0; i<tsize; i++) {
            
            candpos[i] = genrand.nextInt(popsize);
            candfit[i] = fitnesses[candpos[i]];
            
        }
        
        //Determine the two winner parents as those having the lowest (objective=false) or highest (objective=true)
        //fitness function value in the tournament pool
        int[] winpos;
        if(objective) {
            winpos = findMaxFit2IndivPos(candfit);
        } else {
            winpos = findMinFit2IndivPos(candfit);
        }
        
        return winpos;
        
    }
    
    /**
     * Tournament selection operator. Returns an array containing the positions
     * of the individuals selected for the tournament.
     * 
     * @param fitnesses     array of fitness values of the population
     * @param tsize         tournament size
     * @param genrand       a Random instance representing a pseudorandom generator
     * @param objective     flag specifying whether the objective of the problem
     *                      is to minimize or maximize the fitness function
     * @return 
     */
    public static int[] tournSelectionMatrix(int popsize, double[][] fitnesses,
            int tsize, Random genrand, boolean objective) {
        
        int[] candpos = new int[tsize];
        double[] candfit = new double[tsize];
        
        //Draw tsize individual at random from the population
        for(int i=0; i<tsize; i++) {
            
            candpos[i] = genrand.nextInt(popsize);
            candfit[i] = fitnesses[candpos[i]][3];
            
        }
        
        //Determine the two winner parents as those having the lowest (objective=false) or highest (objective=true)
        //fitness function value in the tournament pool
        int[] winpos = new int[2];
        if(objective) {
            winpos = findMaxFit2IndivPos(candfit);
        } else {
            winpos = findMinFit2IndivPos(candfit);
        }
        
        return winpos;
        
    }
    
    /**
     * Elitist replacement operator. It returns a random position in the whole
     * population if a boolean flag is set (meaning that the current child is
     * better than the current best individual in the population, so that the
     * latter can be included for potential replacement). Otherwise, it returns
     * a random position in the population *except* for the position of the
     * best individual, in order to ensure elitism.
     * 
     * @param popsize       size of the population
     * @param replacebest   boolean flag indicating if the best individual is
     *                      to be included in the candidates for replacement
     * @param bestpos       position of the best individual in the population
     * @param genrand       a Random instance representing a pseudorandom number
     *                      generator
     * 
     * @return              a position in the population specifying the
     *                      individual to be replaced
     */
    public static int selectForDeath(int popsize, boolean replacebest,
            int bestpos, Random genrand) {
        
        int toReplace = -1;
        
        if(replacebest) {
            
            //Return a random position in the whole population, including
            //the best individual
            toReplace = genrand.nextInt(popsize); 
            
        }   else {
            
            //Return a random position in the whole population, except the
            //position of the best individual. In order to do so, build an
            //array of increasing integers not containing bestpos
            int[] replaceable = new int[popsize-1];
            for(int i=0; i<bestpos; i++) {
                replaceable[i] = i;
            }
            for(int i=bestpos; i<replaceable.length; i++) {
                replaceable[i] = i+1;
            }
            
            //Finally, sample a value from the "punctured" array
            toReplace = replaceable[genrand.nextInt(replaceable.length)];
            
        }
        
        return toReplace;
        
    }
    
    /**
     * Create a population of balanced bitstrings.
     * 
     * @param length    length of the bitstrings
     * @param popsize   size of the population
     * @param genrand   a Random instance representing a pseudorandom generator   
     * @return 
     */
    public static boolean[][] createBalPop(int length, int popsize, Random genrand) {
        
        boolean[][] population = new boolean[popsize][];
        
        for(int i=0; i<popsize; i++) {
            
            population[i] = CombTools.genBalancedBinaryString(genrand, length);
            
        }
        
        return population;
        
    }
    
    /**
     * Create a population of unbalanced bitstrings with Hamming weight
     * 2^(n-1) - 2^((n/2)-1) (weight of bent functions of n variables)
     * 
     * @param length    length of the bitstrings
     * @param nvar      number of variables of the boolean functions
     * @param popsize   size of the population
     * @param genrand   a Random instance representing a pseudorandom generator
     * @return 
     */
    public static boolean[][] createUnbalPopBent(int length, int nvar, int popsize, Random genrand) {
        
        boolean[][] population = new boolean[popsize][];
        
        for(int i=0; i<popsize; i++) {
            
            population[i] = CombTools.genUnbalBentBinString(genrand, length, nvar);
            
        }
        
        return population;
        
    }
    
    /**
     * Create a population of binary matrices of size Nxk, where each column is
     * a balanced boolean function.
     * 
     * @param popsize   size of the population
     * @param N         number of rows of the matrices
     * @param k         number of columns of the matrices
     * @param genrand   a Random instance representing a pseudorandom generator   
     * @return 
     */
    public static boolean[][][] createBalOAPop(int popsize, int N, int k, Random genrand) {
        
        boolean[][][] population = new boolean[popsize][][];
        
        for(int i=0; i<popsize; i++) {
            
            //Use createBalPop to generate a set of k balanced boolean functions
            //(the functions will be the k balanced columns of the current matrix)
            boolean[][] transcand = createBalPop(N, k, genrand);
            population[i] = CrossoverOperators.transposeMatrix(transcand);
            
        }
        
        return population;
        
    }
    
    /**
     * Create a population of generic boolean functions (with no constraints on
     * their Hamming weight), with uniform probability.
     * 
     * @param length    length of the bitstrings
     * @param popsize   size of the population
     * @param genrand   a Random instance representing a pseudorandom generator
     * @return 
     */
    public static boolean[][] createUnbalPop(int length, int popsize, Random genrand) {
        
        boolean[][] population = new boolean[popsize][length];
        
        for(int i=0; i<popsize; i++) {
            
            for(int j=0; j<length; j++) {
                population[i][j] = genrand.nextBoolean();
            }
            
        }
        
        return population;
        
    }
    
    /**
     * Create a population of generic boolean functions (with no constraints on
     * their Hamming weight), with uniform probability.
     * 
     * @param length        length of the bitstring
     * @param weight        target weight for the bitstring
     * @param popsize       size of the population
     * @param unbalanceP    probability of copying the wrong value
     * @param shuffle       boolean flag to specify whether shuffling should be used or not
     * @param genrand       a Random instance representing a pseudorandom generator
     * @return 
     */
    public static boolean[][] createPartUnbalPop(int length, int weight, 
            int popsize, double unbalanceP, boolean shuffle, Random genrand) {
        
        boolean[][] population = new boolean[popsize][];
        
        for(int i=0; i<popsize; i++) {
            
            population[i] = CombTools.createPartUnbalBitString(length, weight, 
                    unbalanceP, shuffle, genrand);
            
        }
        
        return population;
        
    }
    
    /**
     * Create a population of binary matrices of size Nxk, with unbalanced columns.
     * 
     * @param popsize   size of the population
     * @param N         number of rows of the matrices
     * @param k         number of columns of the matrices
     * @param genrand   a Random instance representing a pseudorandom generator
     * @return 
     */
    public static boolean[][][] createUnbalOAPop(int popsize, int N, int k, Random genrand) {
        
        boolean[][][] population = new boolean[popsize][N][k];
        
        for(int i=0; i<popsize; i++) {
            
            for(int j=0; j<N; j++) {
                
                for(int l=0; l<k; l++) {
                    
                    population[i][j][l] = genrand.nextBoolean();
                    
                }
                
            }
            
        }
        
        return population;
        
    }
    
}
