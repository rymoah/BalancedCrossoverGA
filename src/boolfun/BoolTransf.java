package boolfun;

/**
 * Utilities to compute various transforms (Walsh, Haar, Moebius) and check
 * cryptographic properties of boolean functions. Unless otherwise specified,
 * the boolean functions are represented in their polar form (0->1 and 1->-1)
 * with LSBF order (Least Significant Bit First).
 */
public class BoolTransf {
    
    /**
     * Computes the Walsh Transform of a boolean function using the Fast Walsh
     * Transform (FWT) algorithm, which requires O(NlogN) operations (N=2^n is
     * the length of the truth table). The method directly computes the spectrum
     * in the original vector, and it must be called with the initial parameters
     * (vector, 0, vector.length). The reference for the algorithm is Carlet,
     * "Cryptography and Error-Correcting Codes", chapter 8 in Crama, Hammer,
     * "Boolean Models and Methods in Mathematics, Computer Science and
     * Engineering", p. 272.
     * 
     * @param vector an array of integers representing the boolean function.
     * @param start  the index of the truth table where to start computations.
     * @param end    the index of the truth table where to stop computations.
     */
    public static int calcFWT(int[] vector, int start, int length) {
        
        int half = length/2;
        
        //Main cycle: split vector in two parts (v0 e v1), 
        //update v0 as v0=v0+v1, and v1 as v1=v0-v1.
        for(int i=start; i<start+half; i++) {
            int temp = vector[i];
            vector[i] += vector[i+half];
            vector[i+half] = temp - vector[i+half];
        }
        
        //Recursive call on v0 and v1.
        if(half>1) {
            
            int val1 = calcFWT(vector,start,half);
            int val2 = calcFWT(vector,start+half,half);
            
            if(val1 > val2) {
                    return val1;
            }
            else {
                return val2;
            }

        } else {
        
            //If we have reached half=1 (function in 2 variables),
            //return the highest coefficient in absolute value.
            if(Math.abs(vector[start]) > Math.abs(vector[start+half]))
                return Math.abs(vector[start]);
            else
                return Math.abs(vector[start+half]);           
            
        }
        
    }
    
    /**
     * Computes the nonlinearity of a boolean function, given in input its
     * spectral radius and the number of variables.
     * 
     * @param sprad   the spectral radius of the function.
     * @param nvar    the number of variables of the function.
     * @return nl     the nonlinearity of the function
     */
    public static int calcNL(int sprad, int nvar) {

        int nl = (int)Math.pow(2, nvar-1) - sprad/2;

        return nl;

    }
    
    /**
     * Compute the Walsh transform of an n-variable boolean function over the
     * restricted set of inputs E_{n,k}, for all k in {2..n/2}. The method
     * computes a matrix whose rows are the indexed by the coefficients a in
     * F_2^n, while the columns represent the weight restrictions k in {2..n/2}
     * The last row contains the maximum absolute values (for computing the
     * restricted nonlinearities later).
     * 
     * NOTE: each W_f_(k)(a) is computed with the naive algorithm, since the
     * fast Walsh transform cannot be applied in this case
     * 
     * TODO: is it possible to make it more efficient? One straightforward
     * improvement would be to parallelize at the level of the weights k, since
     * the columns of the matrix are computed independently from one another.
     * 
     * @param function  the truth table of a boolean function of n variables
     * @param n         the number of variables of the boolean function
     * @param inmat     the three dimensional boolean inmat containing all
                        input vectors in weightwise order
     * @return 
     */
    public static int[][] resWalshTransforms(boolean[] function, int n, 
            boolean[][][] inmat) {
        
        int[][] reswt = new int[function.length+1][n/2-1];
        
        //Main cycle: loop over all 2^n binary coefficients a in F_2^n
        for(int a = 0; a<function.length; a++) {
            
            boolean[] bina = BinTools.dec2BinMod(a, n);
            
            //loop over all weights k in {2..n/2}
            for(int k=2; k<=(n/2); k++) {
            
                //loop over all inputs in E_{n,k}
                for(int x=0; x<inmat[k-2].length; x++) {
                    
                    //Evaluate the term (-1)^{f(x) XOR a.x}
                    boolean fx = function[BinTools.bin2Dec(inmat[k-2][x])];
                    boolean dotp = BinTools.scalarProduct(bina, inmat[k-2][x]);
                    if(fx ^ dotp) {
                        reswt[a][k-2]--;
                    } else {
                        reswt[a][k-2]++;
                    }
                    
                }
                
                //Update maximum absolute value for W_f_k
                if(Math.abs(reswt[a][k-2]) > reswt[function.length][k-2]) {
                    reswt[function.length][k-2] = Math.abs(reswt[a][k-2]);
                }
                
            }
            
        }
        
        return reswt;
        
    }
    
    /**
     * Compute the unbalancedness of the weightwise restrictions of a boolean
     * function.
     * 
     * @param n         number of variable of the function
     * @param function  truth table of the boolean function
     * @param inmat     the three dimensional boolean inmat containing all
                        input vectors in weightwise order
     * @param sizes     sizes of the sets E_{n,k}
     * @return 
     */
    public static int[] compResUnb(int n, boolean[] function, boolean[][][] inmat,
            int[] sizes) {
        
        int[] unb = new int[n-1];
        
        for(int k=0; k<n-1; k++) {
            
            int sizein = sizes[k+1];
            //System.out.print("sizein: "+sizein);
            int weight = 0;
            //compute the weight of the k-th restriction
            for(int x=0; x<inmat[k].length; x++) {
                int decx = BinTools.bin2Dec(inmat[k][x]);
                if(function[decx]) {
                    weight++;
                }
            }
            //Compute unbalancedness as the deviation from |E_{n,k}|/2
            unb[k] = Math.abs((sizein/2) - weight); 
            //System.out.println(" Balanced weight "+(k+1)+": "+(sizein/2)+" Weight: "+weight);
            
        }
        
        return unb;
        
    }
    
    /**
     * Compute the restricted nonlinearities of a Boolean function, given the
     * weightwise restrictions of the Walsh transform
     * @param n     number of variables of the function
     * @param reswt weightwise restricted Walsh transforms. Last row contains 
     *              the maximum absolute values
     * @param sizes sizes of the sets E_{n,k}
     * @return 
     */
    public static int[] compResNl(int n, int[][] reswt, int[] sizes) {
        
        int[] nls = new int[(n/2)-1];
        
        for(int k=2; k<=(n/2); k++) {
            
            //Use last row of the Walsh table to get the maximum abs value
            int sizein = sizes[k];    //sizes of E_{n,k} starts from weight k=1, that's why we take k-1 instead of k-2 here
            nls[k-2] = (sizein / 2) - (reswt[reswt.length-1][k-2] / 2);
        }
        
        return nls;
        
    }
    
}
