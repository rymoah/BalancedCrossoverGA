/**
 * Class implementing a steady state GA for the Hyperbent functions problem.
 */

import java.util.Random;
import boolfun.*;
import genalg.*;

public class SteadyGABentBF {
    
    public static void main(String[] args) {
        
        if(args.length != 8) {
            
            System.err.println("\nUsage: java genalg.SteadyGABentBF nvar popsize"
                    + " fiteval tournsize mutprob crossop shuffle seed");
            System.err.println("\nwhere:");
            System.err.println("- nvar is the number of variables of the boolean "
                    + "functions");
            System.err.println("- popsize is the size of the population");
            System.err.println("- fiteval is the maximum number of fitness "
                    + "evaluations without improvement after which the GA is "
                    + "stopped");
            System.err.println("- tournsize is the size of the tournament for "
                    + "selection");
            System.err.println("- mutprob is the mutation probability");
            System.err.println("- crossop is the code for the crossover operator "
                    + "(0: one-point crossover, 1: balanced crossover with "
                    + "counters, 2: balanced crossover with zero-length maps, "
                    + "3: balanced crossover with maps of 1s)");
            System.err.println("- shuffle is a boolean flag specifying whether "
                    + "to shuffle the parents positions to be copied in balanced"
                    + " crossover operators");
            System.err.println("- seed is the initial seed for the random number generator, read as a long variable\n");
            
            System.exit(1);
            
        }
        
        //Read command-line parameters
        int nvar = Integer.parseInt(args[0]);
        int popsize = Integer.parseInt(args[1]);
        int fiteval = Integer.parseInt(args[2]);
        int tournsize = Integer.parseInt(args[3]);
        double mutprob = Double.parseDouble(args[4]);
        int crossop = Integer.parseInt(args[5]);
        boolean shuffle = Boolean.parseBoolean(args[6]);
        long seed = Long.parseLong(args[7]);
        
        boolean unbal;
        
        //Determine unbalancedness flag (only set it to true if the crossover is one-point)
        if(crossop == 0) {
            unbal = true;
        } else {
            unbal = false;
        }
        
        //Weight of a bent function (it can also be with + sign, it is just the complemented function)
        int weight = (int)Math.pow(2, nvar-1) - (int)Math.pow(2, (nvar/2)-1);   //number of 1s
        
        //Initialize random number generator with the input seed
        Random genrand = new Random(seed);
        
        int tlength = (int)Math.pow(2, nvar);    //length of the boolean functions truth table, 2^nvar
        
        //Step 1: generate initial population of boolean functions of nvar
        //variables (unbalanced if we are using one-point crossover, balanced
        //otherwise), evaluate their fitness, and find best and worse individual
        boolean[][] population;
        if(unbal) {
            population = GeneticTools.createUnbalPop(tlength, popsize, genrand);
        } else {
            population = GeneticTools.createUnbalPopBent(tlength, nvar, popsize, genrand);
        }
        
        //Compute the fitness only as the nonlinearity of the boolean function 
        //(no unbalancedness penalty factor)
        double[] fitnesses = FitnessFunctions.compFitnessBFPop(population, nvar, false);

        int bestpos = GeneticTools.findMaxFitIndivPos(fitnesses);
        double bestfit = fitnesses[bestpos];
        
        //Print initial information
        System.out.println("GA Info:");
        System.out.println("\n- Number of variables: "+nvar);
        System.out.println("- Population size: "+popsize);
        System.out.println("- Number of fitness evaluations: "+fiteval);
        System.out.println("- Tournament size: "+tournsize);
        System.out.println("- Mutation probability: "+mutprob);
        System.out.println("- Crossover Operator: "+crossop);
        System.out.println("- Shuffle parents positions: "+shuffle);
        System.out.println("Seed: "+seed);
        
        System.out.println("Best fitness in initial population: "+bestfit);
        System.out.println("Best initial individual: Function "+BinTools.bin2DecBig(population[bestpos]));
        
        
        //Step 2: Iterate the Steady-state GA until nonimp fitness evaluations
        //without improvement have been performed, or until an optimal solution
        //(== hyperbent function) is found
        for(int i=0; i<fiteval; i++) {
            //Step 2a: select two individuals from the population for crossover,
            //using tournament selection. In this case, the problem is of
            //maximize the fitness function (=nonlinearity), so the objective
            //flag is set to false
            int[] candpos = GeneticTools.tournSelection(popsize, fitnesses, tournsize, genrand, true);
            
            //Step 2b: cross the two candidate parents, depending on the
            //crossover operator chosen
            boolean[] child = new boolean[tlength];
            
            switch(crossop) {
                
                case 0: {
                    
                    //Apply classic one-point crossover. Since this operator
                    //returns two children, select randomly one of them.
                    boolean[][] children = CrossoverOperators.onePointCrossover(
                            genrand, population[candpos[0]], 
                            population[candpos[1]]);
                    
                    if(genrand.nextBoolean()) {
                        child = children[1];
                    } else {
                        child = children[0];
                    }
                    
                    break;
                    
                }
                
                case 1: {
                    
                    //Apply balanced crossover operator with counters for bent functions weight.
                    child = CrossoverOperators.balancedCounterCrossWeighted(
                            genrand, population[candpos[0]], 
                            population[candpos[1]], weight, shuffle);
                    
                    break;
                    
                }
                
                case 2: {
                    
                    //Apply balanced crossover operator with zeros-run length encoding
                    child = CrossoverOperators.balancedZeroLengthsCrossWeighted(
                            genrand, population[candpos[0]], 
                            population[candpos[1]], weight, shuffle);
                    
                    break;
                    
                }
                
                case 3: {
                    
                    //Apply balanced crossover operator with map of 1s encoding
                    child = CrossoverOperators.uniformOneMapCrossShuffle(genrand, 
                            population[candpos[0]], population[candpos[1]],
                            shuffle);
                    
                    break;
                    
                }
                
            }
            
            //Step 2c: Apply mutation operator on the child, depending on the
            //crossover operator used (one-point crossover: flip mutation, balanced crossover: swap mutation)
            if(crossop == 0) {
                MutationOperators.flipMutation(genrand, child, mutprob);
            } else {
                MutationOperators.swapMutation(genrand, child, mutprob);
            }
            
            //Step 2d: Evaluate child's fitness and apply elitist replacement
            double fitchild = FitnessFunctions.compFitnessBF(child, nvar, false);
            if((fitchild > fitnesses[candpos[0]]) || (fitchild > fitnesses[candpos[1]])) {
                
                //the child's fitness value is better than at least that of one
                //of its parents. Check if it is also better than the fitness of
                //the best individual in the population
                
                if(fitchild > bestfit) {
                    
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
        
        //Print final info
        System.out.println("Best fitness in final population: "+bestfit);
        System.out.println("Best final individual: Function "+BinTools.bin2DecBig(population[bestpos]));
        
    }
    
}
