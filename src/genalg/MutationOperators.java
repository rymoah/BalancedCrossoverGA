package genalg;

/**
 * Class containing methods for various mutation operators.
 */

import boolfun.BoolFunReps;
import java.util.Random;
import java.util.Vector;

public class MutationOperators {
    
    /**
     * Classic flip-based mutation operator. Sample a random value with uniform
     * probability in [0,1]. If the sampled value is less than the mutation
     * probability, complement a random position in the truth table
     * 
     * @param genrand   Random number generator instance
     * @param ttable    Boolean array to mutate
     * @param pmut      Mutation probability
     */
    public static void flipMutation(Random genrand,
            boolean[] ttable, double pmut) {
            
        double ctoss = genrand.nextDouble();
        if(ctoss <= pmut) {
            
            int randpos = genrand.nextInt(ttable.length);
            ttable[randpos] = !ttable[randpos];

        }
        
    }
    
    /**
     * Classic flip-based mutation operator. For each position of the string, sample
     * a random value in [0,1] with uniform probability and if it is less than
     * the mutation probability flip the bit in that position
     * 
     * NOTICE: the difference from flipMutationAll() above is that in this case
     * we are sampling *for each* position of the chromosome, rather than for
     * a single one
     * 
     * @param genrand   Random number generator instance
     * @param ttable    Boolean array to mutate
     * @param pmut      Mutation probability
     */
    public static void flipMutationAll(Random genrand,
            boolean[] ttable, double pmut) {
        
        for(int i=0; i<ttable.length; i++) {
            
            double ctoss = genrand.nextDouble();
            if(ctoss <= pmut) {

                ttable[i] = !ttable[i];

            }
       
        }
        
    }
    
    /**
     * Swap-based mutation operator, to preserve the balancedness of a 
     * bitstring (truth table). Sample a random value [0,1] with uniform
     * probability. If the sampled number is less than the mutation probability,
     * swap two different bits in the truth table
     * 
     * @param genrand   Random number generator instance
     * @param ttable    Boolean array to mutate
     * @param pmut      Mutation probability 
     */
    public static void swapMutation(Random genrand, boolean[] ttable,
            double pmut) {    
               
        double ctoss = genrand.nextDouble();
        
        if(ctoss <= pmut) {
            
            //Build the map of ones and zeros of the table and randomly select
            //two positions to be swapped from them
            Vector<Integer> omap = BoolFunReps.buildOnesMap(ttable);
            Vector<Integer> zmap = BoolFunReps.buildZerosMap(ttable);
            
            int opos = omap.elementAt(genrand.nextInt(omap.capacity()));
            int zpos = zmap.elementAt(genrand.nextInt(zmap.capacity()));
            
            ttable[opos] = false;
            ttable[zpos] = true;
            
        }
        
    }
    
    /**
     * Swap-based mutation operator. For each position of the string, sample
     * a random value in [0,1] with uniform probability and if it is less than
     * the mutation probability swap the value in the position with another
     * complementary value contained at another position.
     * 
     * NOTICE: the difference from swapMutation() above is that in this case
     * we are sampling *for each* position of the chromosome, rather than for
     * a single one
     * 
     * @param genrand   Random number generator instance
     * @param ttable    Boolean array to mutate
     * @param pmut      Mutation probability 
     */
    public static void swapMutationAll(Random genrand, boolean[] ttable,
            double pmut) {
        
        //Build the map of ones and zeros of the table and randomly select
        //two positions to be swapped from them
        Vector<Integer> omap = BoolFunReps.buildOnesMap(ttable);
        Vector<Integer> zmap = BoolFunReps.buildZerosMap(ttable);
        
        for(int i=0; i<omap.capacity(); i++) {
            
            if(genrand.nextDouble() < pmut) {
                
                //Swap current 1 position with a 0 position selected at random
                int oind = i;
                int oval = omap.elementAt(oind);
                int zind = genrand.nextInt(zmap.capacity());
                int zval = zmap.elementAt(zind);
                ttable[oval] = false;
                ttable[zval] = true;
                
                //Update maps
                omap.set(oind, zval);
                zmap.set(zind, oval);
                
                
            }
            
        }
        
    }
    
    /**
     * Wrapper for mutation operator over binary matrices for the OA problem.
     * Depending on the flag unbal, use flip or swap mutation over the columns
     * of the matrix.
     * 
     * @param genrand   Random number generator instance
     * @param matrix    Boolean matrix to be mutated
     * @param pmut      Mutation probability
     * @param unbal     flag specifying whether to use flip mutation (true) or
     *                  swap-based mutation (false)
     */
    public static void mutateOA(Random genrand, boolean[][] matrix, double pmut,
            boolean unbal) {
        
        if(unbal) {
            //Apply flip mutation column-wise
            for(int j=0; j<matrix[0].length; j++) {

                for(int i=0; i<matrix.length; i++) {

                    if(genrand.nextDouble()<pmut) {
                        
                        matrix[i][j] = !matrix[i][j];
                        
                    }
 
                }

            }
            
        } else {
            
            //Apply swap mutation column-wise
            for(int j=0; j<matrix[0].length; j++) {

                if(genrand.nextDouble() < pmut) {
                    
                    //Randomly pick two loci and swaps the genes.
                    int pos1 = genrand.nextInt(matrix.length);
                    int pos2 = genrand.nextInt(matrix.length);
                    
                    boolean temp = matrix[pos1][j];
                    matrix[pos1][j] = matrix[pos2][j];
                    matrix[pos2][j] = temp;
                    
                }

            }
            
        }
        
    }
    
}
