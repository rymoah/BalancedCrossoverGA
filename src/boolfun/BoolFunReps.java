package boolfun;

/**
 * Methods to extract different representations of boolean functions: maps of
 * 0s ans 1s, run-length coding, enumerative encoding for bipermutive rules, etc.
 */

import java.util.Vector;

public class BoolFunReps {
    
    /**
     * Returns an array containing the position of the 1s
     * in the truth table of a boolean function.
     * 
     * @param ttable    The truth table of a balanced boolean function
     * @return          A vector containing the positions of the 1s in the
     *                  function.
     */
    public static Vector<Integer> buildOnesMap(boolean[] ttable) {
        
        Vector<Integer> omap = new Vector<Integer>();
        
        for(int i=0; i<ttable.length; i++) {
            
            if(ttable[i]) {
                
                omap.add(i);
                
            }
            
        }

        omap.trimToSize();
        return omap;
        
    }
    
    /**
     * Returns an array containing the position of the 0s
     * in the truth table of a boolean function.
     * 
     * @param ttable    The truth table of a balanced boolean function
     * @return          A vector containing the positions of the 0s in the
     *                  function.
     */
    public static Vector<Integer> buildZerosMap(boolean[] ttable) {
        
        Vector<Integer> zmap = new Vector<Integer>();
        
        for(int i=0; i<ttable.length; i++) {
            
            if(!ttable[i]) {
                
                zmap.add(i);
                
            }
            
        }

        zmap.trimToSize();
        return zmap;
        
    }
    
    /**
     * Return the map of the run lengths of zeros in a balanced boolean
     * function, given its truth table.
     * 
     * @param ttable    Truth table of the boolean function.
     * @return zlmap    an array containing the run lengths of the 0s in the
     *                  boolean function.
     */
    public static int[] buildZeroLengthsMap(boolean[] ttable) {
        
        int[] zlmap = new int[(ttable.length/2) + 1];
        int zcount = 0;
        int zmapcnt = 0;
        
        for(int i=0; i<ttable.length; i++) {
            
            //If the current bit is a 0, increment the counter.
            if(!ttable[i]) {
                
                zcount++;
                
            } else {
                
                //Otherwise, update the run length map and reset the 0 counter.
                zlmap[zmapcnt] = zcount;
                zcount = 0;
                zmapcnt++;
            }
            
        }
        
        //Handle the case in which the function terminates with 0.
        if(zcount > 0) {
            
            zlmap[zlmap.length-1] =  zcount;
            
        }
        
        
        return zlmap;
        
    }
    
    /**
     * Return the map of the run lengths of zeros in an unbalanced boolean
     * function, given its truth table.
     * 
     * @param ttable    Truth table of the boolean function.
     * @param ttable    Length of the zeros map (equal to number of 1s + 1)
     * @return zlmap    an array containing the run lengths of the 0s in the
     *                  boolean function.
     */
    public static int[] buildUnbalancedZeroLengthsMap(boolean[] ttable, int length) {
        
        int[] zlmap = new int[length];
        int zcount = 0;
        int zmapcnt = 0;
        
        for(int i=0; i<ttable.length; i++) {
            
            //If the current bit is a 0, increment the counter.
            if(!ttable[i]) {
                
                zcount++;
                
            } else {
                
                //Otherwise, update the run length map and reset the 0 counter.
                zlmap[zmapcnt] = zcount;
                zcount = 0;
                zmapcnt++;
            }
            
        }
        
        //Handle the case in which the function terminates with 0.
        if(zcount > 0) {
            
            zlmap[zlmap.length-1] =  zcount;
            
        }
        
        
        return zlmap;
        
    }
    
}
