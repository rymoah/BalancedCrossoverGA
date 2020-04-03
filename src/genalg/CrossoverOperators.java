package genalg;

/**
 * Class containing methods for crossover operators, both unbalanced and balanced.
 */

import java.util.Vector;
import java.util.Random;
import boolfun.*;

public class CrossoverOperators {
    
    /**
     * Classic one-point crossover operator between two binary strings.
     * 
     * @param genrand   A Random instance representing a pseudorandom generator.
     * @param parent1   Binary representation of the first parent.
     * @param parent2   Binary representation of the second parent.
     * @return          Two boolean arrays representing the children produced
     *                  by one-point crossover.
     */
    public static boolean[][] onePointCrossover(Random genrand, 
            boolean[] parent1, boolean[] parent2) {
        
        //Create the arrays to contain the truth tables of the children.
        boolean[][] children = new boolean[2][parent1.length];
        
        //Pick up a crossover point, with uniform probability.
        int crossp = genrand.nextInt(parent1.length);
        //System.out.println("Crossover point: "+crossp);
        
        //Build the children by crossing the truth tables of the two parents
        //at the crossover point.
        for(int i=0; i<crossp; i++) {
            
            children[0][i] = parent1[i];
            children[1][i] = parent2[i];
            
        }
        
        for(int i=crossp; i<parent1.length; i++) {
            
            children[0][i] = parent2[i];
            children[1][i] = parent1[i];
            
        }
        
        //Return the truth tables of the children.
        return children;
        
    }
    
    /**
     * Crossover operator for weighted balanced boolean function, based on the
     * truth table representation and using counters
     * 
     * @param genrand   A Random instance representing a pseudorandom generator.
     * @param parent1   Binary representation of the first parent.
     * @param parent2   Binary representation of the second parent.
     * @param weight    Hamming weight, the number of ones of both parents and the child.
     * @param shuffle   Flag for shuffling positions of the child.
     * @return          A boolean array representing the child produced
     *                  by crossover.
     */
    public static boolean[] balancedCounterCrossWeighted(Random genrand,
            boolean[] parent1, boolean[] parent2, int weight, boolean shuffle) {
        
        boolean[] child = new boolean[parent1.length];
        
        int ocount = 0;     //counter for ones
        int zcount = 0;     //counter for zeros
        
        int complweight = parent1.length - weight;
        
        int[] positions = new int[parent1.length];
        if(shuffle) {
            positions = CombTools.randPerm(genrand,parent1.length);
        }
        else{
            for (int i = 0; i < parent1.length; i++) {
                positions[i] = i;
            }
        }
        
        for(int i=0; i<child.length; i++) {
            
            //If we have reached the prescribed number of 1s, put only 0s.
            if(ocount == weight) {
                
                child[positions[i]] = false;
                
            } else {
                
                //If we have reached one half of 0s, put only 1s.
                if(zcount == complweight) {
                    
                    child[positions[i]] = true;
                    
                } else {
                    
                    //In all other cases, select randomly one of the parents,
                    //and copy its i-th bit in the child's table.
                    boolean candpar = genrand.nextBoolean();
                    
                    if(!candpar) {
                        
                        child[positions[i]] = parent1[i];
                        
                        //Update the counters of 0s and 1s.
                        if(!parent1[i]) {
                            zcount++;
                        } else {
                            ocount++;
                        }
                        
                    } else {
                        
                        child[positions[i]] = parent2[i];
                        
                        //Update the counters of 0s and 1s.
                        if(!parent2[i]) {
                            zcount++;
                        } else {
                            ocount++;
                        }
                        
                    }
                    
                }
                
            }
            
        }
        
        return child;
        
    }
    
    /**
     * Crossover operator for weighted balanced boolean function, based on the
     * truth table representation and using counters, unbalancedness is allowed
     * with a certain probability
     *
     * @param genrand A Random instance representing a pseudorandom generator.
     * @param parent1 Binary representation of the first parent.
     * @param parent2 Binary representation of the second parent.
     * @param weigth Hamming weight, the number of ones of both parents and the
     * child.
     * @param shuffle Flag for shuffling positions of the child.
     * @param unbalanceP A probability of introducing the unbalancedness.
     * @return A boolean array representing the child produced by crossover.
     */
    public static boolean[] unbalancedCounterCrossWeighted(Random genrand,
            boolean[] parent1, boolean[] parent2, int weight, boolean shuffle,
            double unbalanceP) {

        boolean[] child = new boolean[parent1.length];
        
        int ocount = 0;     //counter for ones
        int zcount = 0;     //counter for zeros

        int complweight = parent1.length - weight;

        int[] positions = new int[parent1.length];
        if (shuffle) {
            positions = CombTools.randPerm(genrand, parent1.length);
        } else {
            for (int i = 0; i < parent1.length; i++) {
                positions[i] = i;
            }
        }

        //While desired balancedness is not reached,
        //select randomly one of the parents,
        //and copy its i-th bit in the child's table.
        int i = 0;
        while (ocount != weight && zcount != complweight) {

            boolean candpar = genrand.nextBoolean();

            if (!candpar) {

                child[positions[i]] = parent1[i];

                //Update the counters of 0s and 1s.
                if (!parent1[i]) {
                    zcount++;
                } else {
                    ocount++;
                }

            } else {

                child[positions[i]] = parent2[i];

                //Update the counters of 0s and 1s.
                if (!parent2[i]) {
                    zcount++;
                } else {
                    ocount++;
                }

            }
            i++;
        }

        //If we have reached the prescribed number of 1s, 
        //put 1s with probability unbalanceP and after put only 0s.
        if (ocount == weight) {
            while (genrand.nextDouble() < unbalanceP && i < child.length) {
                child[positions[i]] = true;
                i++;
            }
            while (i < child.length) {
                child[positions[i]] = false;
                i++;
            }

        } else {

            //If we have reached one half of 0s, put only 1s.
            //put 0s with probability unbalanceP and after put only 1s.
            while (genrand.nextDouble() < unbalanceP && i < child.length) {
                child[positions[i]] = false;
                i++;
            }
            while (i < child.length) {
                child[positions[i]] = true;
                i++;
            }

        }
        return child;
    }
    
    /**
     * Search for an int value in a int vector, returning its position (-1 is
     * returned if it is not contained).
     * 
     * @param vect  A vector of int
     * @param val   The int value to be searched.
     * @return      The position of value val in vect if it is present, -1
     *              otherwise.
     */
    public static int positionOf(int val, Vector<Integer> vect) {
        
        for(int i=0; i<vect.capacity(); i++) {
            
            if(vect.elementAt(i) == val) {
                return i;
            }
            
        }
        
        return -1;
        
    }
    
    /**
     * Uniform crossover operator for Boolean functions based on the map of 1s
     * representation. The functions can be either balanced or of any fixed
     * Hamming weight (hence, it can be used also for the bent function problem).
     * 
     * @param genrand   A Random instance representing a pseudorandom generator.
     * @param parent1   Binary representation of the first parent.
     * @param parent2   Binary representation of the second parent.
     * @param shuffle   Flag for shuffling positions of the child.
     * @return          A boolean array representing the child produced
     *                  by crossover.
     */
    public static boolean[] uniformOneMapCrossShuffle(Random genrand,
            boolean[] parent1, boolean[] parent2, boolean shuffle) {
        
        boolean[] child = new boolean[parent1.length];
        
        //Compute the maps of 1s of the two parents.
        Vector<Integer> omap1 = BoolFunReps.buildOnesMap(parent1);
        Vector<Integer> omap2 = BoolFunReps.buildOnesMap(parent2);
        
        int[] omapchild = new int[omap1.capacity()];
        
        //order of filling the child's table by chosen parent values
        int[] positions = new int[omap1.capacity()];
        if(shuffle) {
            positions = CombTools.randPerm(genrand,omap1.capacity());
        }
        else{
            for (int i = 0; i < omap1.capacity(); i++) {
                positions[i] = i;
            }
        }
        
        for(int i=0; i<omapchild.length; i++) {
            
            if(omap1.capacity() == 0) {
                
                //If the 1s map of the first parent is empty,
                //copy from the second parent.
                int candval = omap2.elementAt(0);
                omapchild[positions[i]] = candval;
                omap2.remove(0);
                omap2.trimToSize();
                
            } else {
                
                    if(omap2.capacity() == 0) {

                    //If the 1s map of the second parent is empty,
                    //copy from the second parent.
                    int candval = omap1.elementAt(0);
                    omapchild[positions[i]] = candval;
                    omap1.remove(0);
                    omap1.trimToSize();

                } else {

                    //In all other cases, Select randomly one of the parents.
                    boolean candpar = genrand.nextBoolean();

                    if(!candpar) {

                        //Parent1 selected. Pick randomly a position on the map
                        //and copy the value in the map of the child.
                        int candpos = genrand.nextInt(omap1.capacity());
                        int candval = omap1.elementAt(candpos);
                        omapchild[positions[i]] = candval;

                        //Remove the value from the parent selected
                        //(and also from the other parent, if it is in common).
                        omap1.remove(candpos);
                        omap1.trimToSize();
                        int pos2 = positionOf(candval, omap2);
                        if( pos2 > -1) {

                            omap2.remove(pos2);
                            omap2.trimToSize();

                        }

                    } else {

                        //Parent2 selected. Pick randomly a position on the map
                        //and copy the value in the map of the child.
                        int candpos = genrand.nextInt(omap2.capacity());
                        int candval = omap2.elementAt(candpos);
                        omapchild[positions[i]] = candval;

                        //Remove the value from the parent selected
                        //(and also from the other parent, if it is in common).
                        omap2.remove(candpos);
                        omap2.trimToSize();
                        int pos1 = positionOf(candval, omap1);
                        if( pos1 > -1) {

                            omap1.remove(pos1);
                            omap1.trimToSize();

                        }

                    }
                }
            }
                    
        }
        
        //Convert the 1s map in the truth table
        for(int i=0; i<omapchild.length; i++) {
            
            child[omapchild[i]] = true;
            
        }
        
        return child;
        
    }
    
    /**
     * Crossover operator for weighted balanced boolean functions, based on the
     * zero-lengths map representation
     * 
     * @param genrand   A Random instance representing a pseudorandom generator.
     * @param parent1   Binary representation of the first parent.
     * @param parent2   Binary representation of the second parent.
     * @param weight    Hamming weight, number of ones in both parents and the child.
     * @param shuffle   Flag for shuffling positions of the child.   
     * @return child    A boolean array representing the child produced
     *                  by crossover.
     */
    public static boolean[] balancedZeroLengthsCrossWeighted(Random genrand,
            boolean[] parent1, boolean[] parent2, int weight, boolean shuffle) {
        
        boolean[] child = new boolean[parent1.length];
        
        int complweight = parent1.length - weight;                      //number of 0s
        int maplength = weight+1;
        
        //order of filling the child's table by chosen parent values
        int[] positions = new int[maplength];
        if(shuffle) {
            positions = CombTools.randPerm(genrand,maplength);
        }
        else{
            for (int i = 0; i < maplength; i++) {
                positions[i] = i;
            }
        }
        
        //Compute the 0s run lengths maps of the two parents.
        int[] zlmap1 = BoolFunReps.buildUnbalancedZeroLengthsMap(parent1, maplength);
        int[] zlmap2 = BoolFunReps.buildUnbalancedZeroLengthsMap(parent2, maplength);
        
        int[] zlmapchild = new int[maplength];
        
        //Initialise the counter of 0s.
        int sumz = 0;
        
        for(int i=0; i<zlmapchild.length-1; i++) {
            
            //If we have already reached one half of 0s in the truth table,
            //put only zeros in the run lengths map of the child
            if(sumz == complweight) {
                
                zlmapchild[positions[i]] = 0;
                
            } else {
                
                //Otherwise, pick randomly one of the two parents and copy the
                //i-th 0s run length in the map of the child.
                boolean candgen = genrand.nextBoolean();
                
                if(!candgen) {
                    
                    //Copy from parent1
                    if((sumz + zlmap1[i]) <= (complweight)) {
                        
                        zlmapchild[positions[i]] = zlmap1[i];
                        sumz += zlmap1[i];
                        
                    } else {
                        
                        zlmapchild[positions[i]] = (complweight) - sumz;
                        sumz = complweight;
                        
                    }
                    
                } else {
                    
                    //Copy from parent2
                    if((sumz + zlmap2[i]) <= (complweight)) {
                        
                        zlmapchild[positions[i]] = zlmap2[i];
                        sumz += zlmap2[i];
                        
                    } else {
                        
                        zlmapchild[positions[i]] = (complweight) - sumz;
                        sumz = complweight;
                        
                    }
                    
                }
                
            }
            
        }
        
        //Compute last run length
        zlmapchild[positions[zlmapchild.length-1]] = complweight - sumz;
        
        //Convert the run lengths map in a truth table
        int i = 0;  //index for the truth table
        for(int j=0; j<zlmapchild.length-1; j++) {
            
            int offset = zlmapchild[j];
            child[i+offset] = true;
            i += offset+1;
            
        }
        
        if(zlmapchild[zlmapchild.length-1] == 0) {
            child[child.length-1] = true;
        }
        
        return child;
                
    }
    
    /**
     * Method used to transpose a matrix. This is used when a new OA offspring is
     * constructed by crossover, since in this case the columns are crossed.
     * 
     * @param matrix    A boolean matrix
     * @return          The transpose of the matrix passed as input parameter
     */
    public static boolean[][] transposeMatrix(boolean[][] matrix) {
        
       boolean[][] transpose = new boolean[matrix[0].length][matrix.length];
       
       for(int i=0; i<transpose.length; i++) {
           
           for(int j=0; j<transpose[0].length; j++) {
               
               transpose[i][j] = matrix[j][i];
               
           }
           
       }
       
       return transpose;
        
    }
    
    /**
     * Wrapper method for crossing over two boolean matrices for the OA problem.
     * 
     * @param genrand   A Random instance representing a pseudorandom generator.
     * @param parent1   boolean matrix representing the first parent
     * @param parent2   boolean matrix representing the second parent
     * @param crossop   integer identifier for the crossover operator to be used
     * @param shuffle   flag for shuffling positions of the child.   
     * @return          a boolean matrix representing the child produced by crossover
     */
    public static boolean[][] crossoverOA(boolean[][] parent1, boolean[][] parent2,
            int crossop, boolean shuffle, Random genrand) {
        
        int weight = parent1.length/2;  //weight of each column
        boolean[][] child;
        boolean[][] transchild = new boolean[parent1[0].length][parent1.length];    //child with transpose matrix, used for actual crossover
        
        boolean[][] transparent1 = transposeMatrix(parent1);                        //parent1 with transpose matrix
        boolean[][] transparent2 = transposeMatrix(parent2);                        //parent2 with transpose matrix 
        
        //Switch on the crossover operator to construct transchild
        switch(crossop) {
            
            case 0: {
                
                //Perform one-point crossover on the rows of transchild
                for(int i=0; i<transchild.length; i++) {
                    
                    boolean[][] children = onePointCrossover(genrand, transparent1[i], transparent2[i]);
                    
                    //Since one-point crossover returns two children, randomly select one of them
                    if(genrand.nextBoolean()) {
                        transchild[i] = children[1];
                    } else {
                        transchild[i] = children[0];
                    }
                    
                }
                
                break;
                
            }
            
            case 1: {
                
                //Perform balanced crossover with counters on the rows of transchild
                for(int i=0; i<transchild.length; i++) {
                    
                    transchild[i] = balancedCounterCrossWeighted(genrand,
                            transparent1[i], transparent2[i], weight, shuffle);
                    
                }
                
                break;
                
            }
            
            case 2: {
                
                //Perform balanced crossover with zero-length encoding on the rows of transchild
                for(int i=0; i<transchild.length; i++) {
                    
                    transchild[i] = balancedZeroLengthsCrossWeighted(genrand,
                            transparent1[i], transparent2[i], weight, shuffle);
                    
                }
                
                break;
                
            }
            
            case 3: {
                
                //Perform balanced crossover with zero-length encoding on the rows of transchild
                for(int i=0; i<transchild.length; i++) {
                    
                    transchild[i] = uniformOneMapCrossShuffle(genrand,
                            transparent1[i], transparent2[i], shuffle);
                    
                }
                
                break;
                
            }            
            
        }
        
        //Finally, transpose the matrix to get the child
        child = transposeMatrix(transchild);
        
        return child;
        
    }
    
}
