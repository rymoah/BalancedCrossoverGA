

/**
 * Class implementing a steady state GA for the problem of Orthogonal Arrays
 */

import java.util.Random;
import boolfun.*;
import genalg.*;

public class SteadyGAOA {
    
    public static void main(String[] args) {
        
        //Check number of command line arguments
        if(args.length != 12) {
            
            System.err.println("\nUsage: java genalg.SteadyGAOA nvar k t lambda p "
                    + "popsize fiteval tournsize mutprob crossop shuffle seed");
            System.err.println("\nwhere:");
            System.err.println("- nvar is the number of variables of the boolean functions (N = 2^n)");
            System.err.println("- k is the number of columns of the OA");
            System.err.println("- t is the strength of the OA");
            System.err.println("- lambda is the index of the OA (N = lambda * 2^t)");
            System.err.println("- p is the exponent of the L^p norm");
            System.err.println("- popsize is the size of the population");
            System.err.println("- fiteval is the maximum number of fitness evaluations");
            System.err.println("- tournsize is the size of the tournament for selection");
            System.err.println("- mutprob is the mutation probability");
            System.err.println("- crossop is the code for the crossover operator "
                    + "(0: one-point crossover, 1: balanced crossover with counters, 2: balanced crossover with zero-length maps, 3: balanced crossover with maps of 1s)");
            System.err.println("- shuffle is a boolean flag specifying whether to shuffle the parents positions to be copied in balanced crossover operators");
            System.err.println("- seed is the initial seed for the random number generator, read as a long variable\n");
            
            System.exit(1);
            
        }
        
        //Read command-line parameters
        int nvar = Integer.parseInt(args[0]);
        int k = Integer.parseInt(args[1]);
        int t = Integer.parseInt(args[2]);
        int lambda = Integer.parseInt(args[3]);
        int p = Integer.parseInt(args[4]);
        int popsize = Integer.parseInt(args[5]);
        int fiteval = Integer.parseInt(args[6]);
        int tournsize = Integer.parseInt(args[7]);
        double mutprob = Double.parseDouble(args[8]);
        int crossop = Integer.parseInt(args[9]);
        boolean shuffle = Boolean.parseBoolean(args[10]);
        long seed = Long.parseLong(args[11]);
        
        boolean unbal;
        
        //Determine unbalancedness flag (only set it to true if the crossover is one-point)
        if(crossop == 0) {
            unbal = true;
        } else {
            unbal = false;
        }
        
        //Initialize random number generator with the input seed
        Random genrand = new Random(seed);
        
        int N = (int)Math.pow(2, nvar);    //length of the boolean functions truth table (== number of rows in the OA), 2^nvar
        
        //Step 1: generate initial population of boolean matrices of size Nxk.
        //Each column is a boolean function of nvar variables (unbalanced if we
        //are using one-point crossover, balanced otherwise).
        boolean[][][] population;
        if(unbal) {
            population = GeneticTools.createUnbalOAPop(popsize, N, k, genrand);
        } else {
            population = GeneticTools.createBalOAPop(popsize, N, k, genrand);
        }
        //The fitness is the same both for unbalanced and balanced matrices,
        //since the balancedness of the columns is implicit in the definition of OA
        double[] fitnesses = FitnessFunctions.compFitnessOAPop(population, t, lambda, p);
        
        //Remember that this problem requires minimizing the fitness function
        int bestpos = GeneticTools.findMinFitIndivPos(fitnesses);
        double bestfit = fitnesses[bestpos];
        
        //Print initial info
        System.out.println("GA Info:");
        System.out.println("\n- Number of variables: "+nvar);
        System.out.println("- OA Profile: ("+N+", "+k+", "+t+", "+lambda+")");
        System.out.println("- Population size: "+popsize);
        System.out.println("- Fitness evaluations: "+fiteval);
        System.out.println("- Tournament size: "+tournsize);
        System.out.println("- Mutation probability: "+mutprob);
        System.out.println("- Crossover Operator: "+crossop);
        System.out.println("- Shuffle parents positions: "+shuffle);
        System.out.println("- Seed: "+seed);
        
        System.out.println("Best fitness in initial population: "+bestfit);
        System.out.println("Best initial individual:");

        for(int i=0; i<N; i++) {
            
            for(int j=0; j<k; j++) {
                
                System.out.print(BinTools.singleBool2Bin(population[bestpos][i][j])+" ");
                
            }
            
            System.out.println("");
            
        }
        
        System.out.println("");
        
        //Step 2: Iterate the Steady-state GA for fiteval evaluations
        for(int i=0; i<fiteval; i++){
            
            //Step 2a: select two individuals from the population for crossover,
            //using tournament selection. In this case, the problem is of
            //maximizing the fitness function (=nonlinearity), so the objective
            //flag is set to true
            int[] candpos = GeneticTools.tournSelection(popsize, fitnesses,
                    tournsize, genrand, true);
            
            //Step 2b: cross the two candidate parents, depending on the
            //crossover operator chosen
            boolean[][] child = CrossoverOperators.crossoverOA(
                    population[candpos[0]], population[candpos[1]], crossop,
                    shuffle, genrand);
            
            //Step 2c: Apply mutation operator on the child, depending on the
            //crossover operator used (one-point crossover: flip mutation, balanced crossover: swap mutation)
            MutationOperators.mutateOA(genrand, child, mutprob, unbal);
            
            //Step 2d: Evaluate child's fitness and apply elitist replacement
            double fitchild = FitnessFunctions.compFitnessOA(child, t, lambda, p);
            if((fitchild < fitnesses[candpos[0]]) || (fitchild < fitnesses[candpos[1]])) {
                
                //the child's fitness value is better than at least that of one
                //of its parents. Check if it is also better than the fitness of
                //the best individual in the population
                
                if(fitchild < bestfit) {
                    
                    //the child is better than the best individual, thus apply
                    //replacement operator over the whole population and update
                    //best individual
                    int replpos = GeneticTools.selectForDeath(popsize, true,
                            bestpos, genrand);
                    population[replpos] = child;
                    fitnesses[replpos] = fitchild;
                    bestpos = replpos;
                    bestfit = fitchild;
                    
                } else {
                    
                    //The child is not better than the best individual, thus
                    //apply replacement operator over the whole population
                    //*except* the best individual
                    int replpos = GeneticTools.selectForDeath(popsize, false,
                            bestpos, genrand);
                    population[replpos] = child;
                    fitnesses[replpos] = fitchild;
                        
                }
                
            }
            
        }
        
        System.out.println("Best fitness in final population: "+bestfit);
        System.out.println("Best initial individual:");
        for(int i=0; i<N; i++) {
            
            for(int j=0; j<k; j++) {
                
                System.out.print(BinTools.singleBool2Bin(population[bestpos][i][j])+" ");
                
            }
            
            System.out.println("");
            
        }
        
        System.out.println("");
        
    }
    
}
