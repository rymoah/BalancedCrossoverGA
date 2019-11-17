package oa;

/**
 * Class defining various support methods for orthogonal arrays.
 */

import boolfun.*;
import java.util.Arrays;

public class OrthogonalArrayTools {
    
    /**
    * Given an N x k matrix, return a N x t submatrix whose columns are specified
    * in a Boolean mask vector. The mask vector must have Hamming weight t.
    * 
    * @param matrix
    * @param t
    * @param mask
    * 
    * @return
    */
    public static int[][] extractSubmatrix(int[][] matrix, int t, 
            boolean[] mask) {
        
        int[][] submat = new int[matrix.length][t];
        
        //Convert the mask in an int array containing the indices of the selected columns
        int[] pos = new int[t];
        int j = 0;
        for(int i=0; i<mask.length; i++) {
            
            if(mask[i]) {
                pos[j] = i;
                j++;
            }
            
        }
        
        //Construct the submatrix
        for(int i=0; i<matrix.length; i++) {
            
            for(int k=0; k<t; k++) {
                
                submat[i][k] = matrix[i][pos[k]];
                
            }
            
        }
        
        return submat;
        
    }
    
    /**
     * Count the number of occurrences of a vector in the rows of a matrix.
     * 
     * @param matrix
     * @param vector
     * @return 
     */
    public static int countOccurrencesVector(int[][] matrix, int[] vector) {
        
        int occ = 0;
        
        for(int i=0; i<matrix.length; i++) {
            
            if(Arrays.equals(vector, matrix[i])) {
                
                occ++;
                
            }
            
        }
        
        return occ;
        
    }
    
    /**
     * Compute the distance of a N x t submatrix of s levels by using the distance
     * induced by the p-norm. In the particular case where p=0, the method computes
     * the Chebyshev's (or infinity) distance. The distance is computed between
     * the vector of occurrences of t-uples inside the matrix and the vector
     * whose components are all equal to lambda.
     * 
     * @param submatrix
     * @param s
     * @param lambda
     * @param p
     * @return 
     */
    public static double calcDistSubmatrix(int[][] submatrix, int s, int lambda, int p) {
        
        double dist = 0;
        
        int t = submatrix[0].length;    //t is the number of columns in the submatrix, using the OA notation
        int numtuples = (int)Math.pow(s, t);
        
        //Check whether we have to compute Chebyshev distance or not
        if(p > 0) {
           
            double exponent = 1.0 / (double)p;
            
            //Compute the usual L^p distance
            for(int i=0; i<numtuples; i++) {

                //Convert i to a t-uple in base s and count its occurrences in the submatrix
                int[] vect = BinTools.dec2NaryInt(i, t, s);
                int occ = countOccurrencesVector(submatrix, vect);

                //Update the distance
                double absdist = Math.abs(lambda - occ);
                dist += Math.pow(absdist, p);

            }
            
            dist = Math.pow(dist, exponent);
            
        } else {
            
            //Compute Chebyshev (max) distance
            for(int i=0; i<numtuples; i++) {

                //Convert i to a t-uple in base s and count its occurrences in the submatrix
                int[] vect = BinTools.dec2NaryInt(i, t, s);
                int occ = countOccurrencesVector(submatrix, vect);

                //Update the distance
                double absdist = Math.abs(lambda - occ);
                
                if(absdist > dist) {
                    
                    dist = absdist;
                    
                }

            }
            
        }
        
        return dist;
        
    }
    
    /**
     * Compute the distance of a N x k matrix by summing all the distances of its
     * N x t submatrices. If the distance is zero, then the matrix is an OA(N,k,s,t)
     * 
     * @param matrix
     * @param s
     * @param t
     * @param lambda
     * @param p
     * @return 
     */
    public static double calcDistMatrix(int[][] matrix, int s, int t, int lambda, int p) {
        
        double dist = 0;
        int k = matrix[0].length;   //Number of columns
        int[] submatrices = CombTools.genBinCombs(k-t, t);
        
        for(int i=0; i<submatrices.length; i++) {
            
            //Compute the distance of the current submatrix and add it to the total distance
            boolean[] mask = BinTools.dec2BinMod(submatrices[i], k);    //boolean mask (the position at 1 denote the selected columns)
            int[][] submatrix = extractSubmatrix(matrix, t, mask);
            double subdist = calcDistSubmatrix(submatrix, s, lambda, p);
            
            dist += subdist;
            
        }
        
        return dist;
        
    }    
    
}