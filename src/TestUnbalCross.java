/**
 * Class to test the unbalancedCounterCrossWeighted() crossover operator method
 * 
 * @author Luca Mariot
 */

import boolfun.BinTools;
import boolfun.CombTools;
import genalg.CrossoverOperators;
import java.util.Random;

public class TestUnbalCross {
    
    public static void main(String[] args) {
        
        if(args.length != 4) {
            
            System.err.println("Usage: java TestUnbalCross length weight unbalanceP nchild");
            System.exit(1);
            
        }
        
        int length = Integer.parseInt(args[0]);
        int weight = Integer.parseInt(args[1]);
        double unbalanceP = Double.parseDouble(args[2]);
        int nchild = Integer.parseInt(args[3]);
        
        Random genrand = new Random();
        
        //Create two partially balanced parents
        boolean[] parent1 = CombTools.createPartUnbalBitString(length, weight, unbalanceP, false, genrand);
        boolean[] parent2 = CombTools.createPartUnbalBitString(length, weight, unbalanceP, false, genrand);

        System.out.println("\nBitstring length: "+length);
        System.out.println("Target weight: "+weight);
        System.out.println("Unbalancedness probability: "+unbalanceP);
        
        System.out.println("\nParent 1: "+BinTools.bool2Bin(parent1)+" HW: "+BinTools.computeHW(parent1));
        System.out.println("Parent 2: "+BinTools.bool2Bin(parent2)+" HW: "+BinTools.computeHW(parent2)+"\n");
        
        //Create nchild children with the partially unbalanced crossover operator
        for(int i=0; i<nchild; i++) {
            
            boolean[] child = CrossoverOperators.unbalancedCounterCrossWeighted(genrand, parent1, parent2, weight, false, unbalanceP);
            System.out.println("Child "+(i+1)+": "+BinTools.bool2Bin(child)+" HW: "+BinTools.computeHW(child));
            
        }
        
        System.out.println("");
        
        
    }
    
}
