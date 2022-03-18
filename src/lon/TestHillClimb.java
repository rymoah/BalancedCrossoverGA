package lon;

/**
 * 
 * Class to test the hill climbing algorithm for Boolean functions.
 * 
 */

import boolfun.*;
import genalg.*;
import java.util.Random;
import java.math.BigInteger;

public class TestHillClimb {
    
    public static void main(String[] args) {
        
        if(args.length != 2) {
            
            System.err.println("Usage: java lon.TestHillClimb nvar runs"); 
            System.exit(1);
            
        }
        
        int nvar = Integer.parseInt(args[0]);
        int tlength = (int)Math.pow(2,nvar);
        int runs = Integer.parseInt(args[1]);
        Random genrand = new Random();
        
        System.out.println("");
        
        for(int i=1; i<=runs; i++) {
            
            boolean[] infunc = CombTools.genBalancedBinaryString(genrand, tlength);
            BigInteger decinfunc = BinTools.bin2DecBig(infunc);
            double infit = FitnessFunctions.compFitnessBF(infunc, nvar, false);
            int[] walsht = BinTools.bin2Pol(infunc);
            BoolTransf.calcFWT(walsht, 0, tlength);
            SearchSolution locopt = LocalSearchTools.hillClimb(infunc, infit, walsht, nvar);
            BigInteger declocopt = BinTools.bin2DecBig(locopt.function);
            System.out.println("Run "+i+"; Initial Solution: "+decinfunc+
                    " ; Init Fitness: "+infit+" ; Local Optimum: "+declocopt+" ; Final Fitness: "+locopt.fitness);
            
        }
        
    }
    
}
