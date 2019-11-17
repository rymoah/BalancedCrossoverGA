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
    
}
