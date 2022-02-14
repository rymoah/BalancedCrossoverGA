package boolfun;

/**
 * Utilities to generate combinations of objects (particularly bitstrings with
 * a specified Hamming weight) and compute their cardinalities. The bitstrings
 * are sorted in LSBF (Least Significant Bit First) order, unless otherwise
 * specified.
 */
import java.util.Vector;
import java.util.Random;

public class CombTools {
    
    /**
     * Computes the factorial of an integer number.
     * 
     * @param num   an integer number
     * @return fact the factorial of num
     */
    public static int factorial(int num) {
        
        int fact = 1;
        
        if((num==0) || (num==1)) {            
            return fact;   
        }
        
        for(int i=2; i<=num; i++) {
            fact *= i;
        }
        
        return fact;
        
    }
    
    /**
     * Compute all binomial coefficients (n,k) with k in {0,...n}. This is
     * done by computing the (n+1)-th row of Pascal's triangle
     * 
     * @param n         the size of the set from which combinations are drawn.
     * @return 
     */
    public static int[] allBinCoeffs(int n) {
        
        int[][] pascal = new int[2][]; //Keep only two rows of the triangle, for memory efficiency
        pascal[0] = new int[1];
        pascal[0][0] = 1;              //Initialize the vertex of the triangle
        
        for(int i=1; i<=n; i++) {
            
            //Size of the next row is the size of the previous row +1
            pascal[1] = new int[i+1];
            //Initialize left and right extremes at 1
            pascal[1][0] = 1;           
            pascal[1][i] = 1;
            
            if(i>1) {
                //Compute each remaining element as the sum of the previous two in row 0
                for(int j=1; j<i; j++) {
                    pascal[1][j] = pascal[0][j-1] + pascal[0][j];
                }
            }
            
            //Update rows
            pascal[0] = pascal[1];
            
        }
        
        return pascal[1];
        
    }
    
    /**
     * Computes the binomial coefficient (n,k) using Pascal's triangle.
     * 
     * NOTICE: the smallest input where this overflows (because of int return
     * type) is n=34 and k=17
     * 
     * @param n         the size of the set from which combinations are drawn.
     * @param k         the size of the combinations.
     * @return bCoeff   the binomial coefficient (n,k)
     */
    public static int binCoeff(int n, int k) {
        
        //Construct the row of Pascal's triangle corresponding to n and return
        //the k-th element in it
        return(allBinCoeffs(n)[k]);
        
    }
    
    /**
     * Generates all the (s+t)-bit strings with Hamming weight t, in decimal
     * notation. The algorithm is described in Knuth, "The Art of Computer
     * Programming, pre-Fascicle 4A" (Algorithm L, p. 4).
     * 
     * @param s         number of 0s in the bitstrings
     * @param t         number of 1s in the bitstrings
     * @return combset  array of integers representing the bitstrings of length
     *                  (s+t) and Hamming weight t
     */
    public static int[] genBinCombs(int s, int t) {

        int size = binCoeff(s+t, t);
        int[] combset = new int[size];
        
        int index = 0; //index for the set combs.
        
        //Initialisation
        int[] comb = new int[t+2]; //the two additional cells are sentinels.
        for(int j=0; j<t; j++) {
            comb[j] = j;
        }
        comb[t] = s+t;
        comb[t+1] = 0;
        
        int j = 0;
        
        while(j<t) {
            
            boolean[] conf = new boolean[s+t];
            
            for(int k=0; k<t; k++) {
                conf[comb[k]] = true;
            }
            
            //Convert the combination in decimal notation and
            //copy it in the final set.
            int deccomb = BinTools.bin2Dec(conf);
            combset[index] = deccomb;
            index++;            
            
            j=0;
            while((comb[j]+1)==comb[j+1]) {
                comb[j] = j;
                j++;
            }
            
            if(j<t) {
                comb[j]++;
            }
            
        }
        
        return combset;
      
    }
    
    /**
     * Generates all the (s+t)-bit strings with Hamming weight t, in binary
     * notation. The algorithm is described in Knuth, "The Art of Computer
     * Programming, pre-Fascicle 3A" (Algorithm L, p. 4).
     * 
     * NOTICE: we pass the binomial coefficient as a parameter for efficiency
     * reasons.
     * 
     * @param s         number of 0s in the bitstrings
     * @param t         number of 1s in the bitstrings
     * @param size      binomial coefficient (s+t, t)
     * @return combset  array of integers representing the bitstrings of length
     *                  (s+t) and Hamming weight t
     */
    public static boolean[][] genBinCombsBin(int s, int t, int size) {

        boolean[][] combset = new boolean[size][];
        
        int index = 0; //index for the set combs.
        
        //Initialisation
        int[] comb = new int[t+2]; //the two additional cells are sentinels.
        for(int j=0; j<t; j++) {
            comb[j] = j;
        }
        comb[t] = s+t;
        comb[t+1] = 0;
        
        int j = 0;
        
        while(j<t) {
            
            boolean[] conf = new boolean[s+t];
            
            for(int k=0; k<t; k++) {
                conf[(s+t)-1-comb[k]] = true;
            }
            
            //Copy the combination in the final set.
            combset[index] = conf;
            index++;            
            
            j=0;
            while((comb[j]+1)==comb[j+1]) {
                comb[j] = j;
                j++;
            }
            
            if(j<t) {
                comb[j]++;
            }
            
        }
        
        return combset;
      
    }
    
    /**
     * Generate the three-dimensional inmat containing all input vectors in
     * F_2^n (excluding 0..0 and 1..1) in increasing Hamming weight order.
     * 
     * @param n         Vector space dimension
     * @param bincoeffs vector of binomial coefficients (n,k)
     * @return 
     */
    public static boolean[][][] genBinCombsMatrix(int n, int[] bincoeffs) {
        
        //The number of weights to be considered is n-1 (from 1 to n-1)
        boolean[][][] matrix = new boolean[n-1][][];
        
        //Fill the inmat by calling genBinCombsBin with increasing (respectively,
        //decreasing) values of 1s (respectively, 0s)
        for(int i=0; i<matrix.length; i++) {
            matrix[i] = genBinCombsBin(n-i-1, i+1, bincoeffs[i+1]);
        }
        
        return matrix;
        
    }
    
    /**
     * Generate a random balanced binary string, represented as a boolean array.
     * 
     * @param genrand   Random number generator instance
     * @param tablen    length of the binary string
     * @return 
     */
    public static boolean[] genBalancedBinaryString(Random genrand, int tablen) {
     
        boolean[] ttable = new boolean[tablen];
        
        Vector<Integer> positions = new Vector<Integer>();
        
        for(int i=0; i<tablen; i++) {
            positions.add(i);
        }
        
        positions.trimToSize();
        
        for(int i=0; i<tablen/2; i++) {
            
            int candpos = genrand.nextInt(positions.capacity());
            ttable[positions.elementAt(candpos)] = true;
            positions.remove(candpos);
            positions.trimToSize();
            
        }
        
        return ttable;
        
    }
    
    /**
     * Generate a random unbalanced binary string, represented as a boolean array.
     * 
     * @param genrand   Random number generator instance
     * @param tablen    length of the binary string
     * @return 
     */
    public static boolean[] genUnbalancedBinaryString(Random genrand, int tablen) {
     
        boolean[] ttable = new boolean[tablen];
        
        for(int i=0; i<tablen; i++) {
            
            ttable[i] = genrand.nextBoolean();
            
        }
        
        return ttable;
        
    }
    
    /**
     * Generate a random unbalanced binary string, whose Hamming weight is
     * 2^{n-1} - 2^{(n/2)-1} (i.e., the weight that a bent function must have)
     * 
     * @param genrand   Random number generator instance
     * @param tablen    length of the binary string
     * @param nvar      number of variables of the boolean function
     * @return 
     */
    public static boolean[] genUnbalBentBinString(Random genrand, int tablen, int nvar) {
        
        boolean[] ttable = new boolean[tablen];
        Vector<Integer> positions = new Vector<Integer>();
        
        for(int i=0; i<tablen; i++) {
            positions.add(i);
        }
        
        positions.trimToSize();
        
        //Weight of a bent function (it can also be with + sign, it is just the complemented function)
        int weight = (int)Math.pow(2, nvar-1) - (int)Math.pow(2, (nvar/2)-1);
        
        for(int i=0; i<weight; i++) {
            
            int candpos = genrand.nextInt(positions.capacity());
            ttable[positions.elementAt(candpos)] = true;
            positions.remove(candpos);
            positions.trimToSize();
            
        }
        
        return ttable;
        
    }
    
        /**
     * Create a partially unbalanced binary string.
     * After reaching the prescribed weight, with a certain probability the
     * wrong value is still copied in the child, until the sampled random
     * number is higher than the probability. At that point, only the
     * complementary value is copied up to the end of the child. The method
     * can be used both with the shuffling operation or not
     * 
     * @param length        length of the bitstring
     * @param weight        target weight for the bitstring
     * @param unbalanceP    probability of copying the wrong value
     * @param shuffle       boolean flag to specify whether shuffling should be used or not
     * @param genrand       a Random instance representing a pseudorandom generator
     * @return 
     */
    public static boolean[] createPartUnbalBitString(int length, int weight, 
            double unbalanceP, boolean shuffle, Random genrand) {
        
        boolean[] binstring = new boolean[length];
        
        int ocount = 0;     //counter for ones
        int zcount = 0;     //counter for zeros
        int complweight = length - weight;  //number of desired 0s in the bitstring

        //Check whether shuffling must be applied
        int[] positions = new int[binstring.length];
        if (shuffle) {
            positions = CombTools.randPerm(genrand, binstring.length);
        } else {
            for (int i = 0; i < binstring.length; i++) {
                positions[i] = i;
            }
        }
        
        //While desired balancedness is not reached,
        //select randomly one of the parents,
        //and copy its i-th bit in the child's table.
        int i = 0;
        while (ocount != weight && zcount != complweight) {
            
            //Sample a random bit, copy it in the current position and update
            //the relevant counter
            boolean candval = genrand.nextBoolean();
            binstring[positions[i]] = candval;
            
            if(candval) {
                ocount++;
            } else {
                zcount++;
            }
            
            i++;
            
        }     
        
        //If we have reached the prescribed number of 1s, put 1s with
        //probability unbalanceP and after put only 0s. Else, do the reverse
        //copy 0 with probability unbalanceP and after put only 1s).
        if(ocount==weight) {
            
            while (genrand.nextFloat() < unbalanceP && i < binstring.length) {
                binstring[positions[i]] = true;
                i++;
            }
            while (i < binstring.length) {
                binstring[positions[i]] = false;
                i++;
            }
            
        } else {
            
            while (genrand.nextFloat() < unbalanceP && i < binstring.length) {
                binstring[positions[i]] = false;
                i++;
            }
            while (i < binstring.length) {
                binstring[positions[i]] = true;
                i++;
            }
            
        }
        
        return binstring;
        
    }
    
    /**
     * Generate a balanced n-ary string where each symbol in {0,...,n-1}
     * occurs the same number of times.
     * 
     * @param n         number of symbols in the alphabet
     * @param lambda    number of occurrences of each symbol in the string
     * @param genrand   random number generator instance
     * @return an int array of length n*lambda where each of the first n
     *          integer numbers appears exactly lambda times
     */
    public static int[] genRandNaryBalString(int n, int lambda, Random genrand) {
        
        //Instantiate array of length n*freq
        int[] balstr = new int[n*lambda];
        
        //Generate initial vector of ordered values
        Vector<Integer> init = new Vector<Integer>();
        
        for(int i=0; i<n; i++) {
            
            for(int j=0; j<lambda; j++) {
                
                init.add(i);
                
            }
            
        }
        
        init.trimToSize();
        
        //Generate balanced string by sampling without replacement random positions
        //from the ordered vector (See Fisher-Yates shuffle)
        for(int i=0; i<balstr.length; i++) {
            
            int j = genrand.nextInt(init.capacity());
            balstr[i] = init.elementAt(j);
            
            init.removeElementAt(j);
            init.trimToSize();
            
        }
        
        return balstr;
        
    }
    
    /**
     * Convert an n-ary string to a binary matrix.
     * 
     * @param string    array representing an n-ary string
     * @param col       number of columns in the matrix
     * @param n         number of symbols in the string
     * @return 
     */
    public static int[][] convNaryStrToBinMat(int[] string, int col, int n) {
        
        int[][] matrix = new int[string.length][col];
        
        for(int i=0; i<matrix.length; i++) {
            
            matrix[i] = BinTools.dec2NaryInt(string[i], col, n);
            
        }
        
        return matrix;
        
    }
    
    /**
     * Add a column to a matrix. This method is used in the incremental
     * construction of OA.
     * 
     * @param matrix
     * @param column
     * @return 
     */
    public static int[][] addColToBinMat(int[][] matrix, int[] column) {
        
        int[][] newmat = new int[matrix.length][matrix[0].length+1];
        
        for(int i=0; i<matrix.length; i++) {
            
            for(int j=0; j<matrix[0].length; j++) {
                
                //Copy old matrix
                newmat[i][j] = matrix[i][j];
                
            }
            
            //Copy new column
            newmat[i][matrix[0].length] = column[i];
            
        }
        
        return newmat;
        
    }
    
    /**
     * Create permutation of size int length (inside-out algorithm)
     * 
     * @param genrand   A Random instance representing a pseudorandom generator.
     * @param length    The int value, length of the permutation.
     * @return          Permutation of the size length
     */
    public static int[] randPerm(Random genrand, int length){
        int[] vect = new int[length];
        if(length > 0){
            vect[0]=0;
            for (int i = 1; i < length; i++) {
                int j = genrand.nextInt(i);
                vect[i] = vect[j];
                vect[j] = i;
            }            
        }
        return vect;
    }
    
    /**
     * Generate the truth table of a random WPB function of n variables, with
     * f(0)=0 and f(1)=1.
     * 
     * @param flength   length of the boolean function (2^n)
     * @param inmat     the three dimensional boolean inmat containing all
                        input vectors in weightwise order
     * @param gen       random number generator
     * @return 
     */
    public static boolean[] genRandomWPBFunction(int flength, 
            boolean[][][] inmat, Random gen) {
        
        boolean[] function = new boolean[flength];
        
        //Loop over all weights
        for(int k=0; k<inmat.length; k++) {
            //Generate a balanced string of length bincoeff(n,k)
            boolean[] balstr = genBalancedBinaryString(gen, inmat[k].length);
            //Set the function value of each vector in E_{n,k}
            for(int i=0; i<inmat[k].length; i++) {
                int x = BinTools.bin2Dec(inmat[k][i]);
                function[x] = balstr[i];
            }
        }
        
        //Set f(0)=0 and f(1) = 1
        function[0] = false;
        function[flength-1] = true;
        
        return function;
        
    }
    
}
