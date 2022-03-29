package lon;

/**
 * Class for the structure that saves function, its walsh transform and fitness
 * function value.
 * 
 */

import java.util.Arrays;

public class SearchSolution{
    public boolean[] function;
    public int[] walsht;
    public double fitness;
    public int nvar;

    public SearchSolution(int nvar) {
        int n = (int)Math.pow(2,nvar);
        function = new boolean[n];
        walsht = new int[n];
        fitness = 0;        
    }
    
    public SearchSolution(boolean[] function, int[] walsht, double fitness, int nvar) {
        int n = (int)Math.pow(2,nvar);
        this.function = new boolean[n];
        this.walsht = new int[n];
        System.arraycopy(function,0,this.function,0,n);
        System.arraycopy(walsht,0,this.walsht,0,n);
        this.fitness = fitness;
        this.nvar = nvar;
    }
    
    public void updateFunction(boolean[] function, int[] walsht, double fitness){
        int n = function.length;
        System.arraycopy(function,0,this.function,0,n);
        System.arraycopy(walsht,0,this.walsht,0,n);
        this.fitness = fitness;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SearchSolution other = (SearchSolution) obj;
        if (Double.doubleToLongBits(this.fitness) != Double.doubleToLongBits(other.fitness)) {
            return false;
        }
        if (this.nvar != other.nvar) {
            return false;
        }
        if (!Arrays.equals(this.function, other.function)) {
            return false;
        }
        if (!Arrays.equals(this.walsht, other.walsht)) {
            return false;
        }
        return true;
    }
    
    
}