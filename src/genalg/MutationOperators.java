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
