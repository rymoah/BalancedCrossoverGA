/**
 * Class implementing a steady state GA for the problem of Boolean functions,
 * allowing unbalancedness in the crossover with a certain probability.
 * 
 */

import java.util.Random;
import boolfun.*;
import genalg.*;

public class SteadyGABFUnb {
    
    public static void main(String[] args) {
        
        if(args.length != 13) {
            
            System.err.println("\nUsage: java genalg.SteadyGABF nvar popsize "
                    + "fiteval freqprint tournsize mutprob crossop shuffle seed coolingparam unbalanceProb frequency unbal");
            System.err.println("\nwhere:");
            System.err.println("- nvar is the number of variables of the boolean functions");
            System.err.println("- popsize is the size of the population");
            System.err.println("- fiteval is the maximum number of fitness evaluations");
            System.err.println("- freqprint is the sampling frequency to print info on the population (once every freqprint fitness evaluations)");
            System.err.println("- tournsize is the size of the tournament for selection");
            System.err.println("- mutprob is the mutation probability");
            System.err.println("- crossop is the code for the crossover operator "
                    + "(0: one-point crossover, 1: balanced crossover with counters, 2: balanced crossover with zero-length maps, 3: balanced crossover with maps of 1s, 4: balanced crossover with counters and unbalancedness is allowed)\");)");
            System.err.println("- shuffle is a boolean flag specifying whether to shuffle the parents positions to be copied in balanced crossover operators");            
            System.err.println("- seed is the initial seed for the random number generator, read as a long variable");
            System.err.println("- coolinparam is the reducing factor for the unbalancedness probability");
            System.err.println("- unbalanceProb is the initial probability if the unbalancedness");
            System.err.println("- frequp is the frequency of updating the unbalancedness probability");
            System.err.println("- unbal is a boolean flag to specify whether unbalancedness penalty factor must be considered in the fitness function computation or not\n");
            
            System.exit(1);
            
        }
        
        //Read command-line parameters
        int nvar = Integer.parseInt(args[0]);
        int popsize = Integer.parseInt(args[1]);
        int fiteval = Integer.parseInt(args[2]);
        int freqprint = Integer.parseInt(args[3]);
        int tournsize = Integer.parseInt(args[4]);
        double mutprob = Double.parseDouble(args[5]);
        int crossop = Integer.parseInt(args[6]);
        boolean shuffle = Boolean.parseBoolean(args[7]);
        long seed = Long.parseLong(args[8]);
        double coolingparam = Double.parseDouble(args[9]);
        double unbalanceProb = Double.parseDouble(args[10]);
        int frequpd = Integer.parseInt(args[11]);
        boolean unbal = Boolean.parseBoolean(args[12]);
        
        //Initialize random number generator with the input seed
        Random genrand;
        if(seed == 0)
            genrand = new Random(seed);
        else
            genrand = new Random();
        
        int tlength = (int)Math.pow(2, nvar);    //length of the boolean functions truth table, 2^nvar
        int weight = tlength/2;                  //weight of balanced boolean functions
        
        //Step 1: generate initial population of boolean functions of nvar
        //variables (unbalanced if we are using one-point crossover, balanced
        //otherwise), evaluate their fitness, and find best and worse individual
        boolean[][] population;
        if(unbal) {
            population = GeneticTools.createPartUnbalPop(tlength, weight, popsize, unbalanceProb, shuffle, genrand);
        } else {
            population = GeneticTools.createBalPop(tlength, popsize, genrand);
        }
        double[] fitnesses = FitnessFunctions.compFitnessBFPop(population, nvar, unbal);
        int bestpos = GeneticTools.findMaxFitIndivPos(fitnesses);
        double bestfit = fitnesses[bestpos];
        
        //Print initial information
        System.out.println("GA Info:");
        System.out.println("\n- Number of variables: "+nvar);
        System.out.println("- Population size: "+popsize);
        System.out.println("- Fitness evaluations: "+fiteval);
        System.out.println("- Sampling frequency: "+freqprint);
        System.out.println("- Tournament size: "+tournsize);
        System.out.println("- Mutation probability: "+mutprob);
        System.out.println("- Crossover Operator: "+crossop);
        System.out.println("- Shuffle parents positions: "+shuffle);
        System.out.println("- Cooling parameter: "+coolingparam);
        System.out.println("- Initial unbalancedness probability: "+unbalanceProb);
        System.out.println("- Frequency of changing the unbalancedness probability: "+frequpd);
        System.out.println("Seed: "+seed);
        
        //Print initial info
        //Compute average fitness and average HW
        double avgf = 0.0;
        double avghw = 0.0;
        for(int l=0; l<fitnesses.length; l++) {
            avgf += fitnesses[l];
            avghw += BinTools.computeHW(population[l]);
        }
        avgf /= fitnesses.length;
        avghw /= population.length;
        System.out.println("Best fitness in initial population: "+bestfit);
        System.out.println("Average fitness in initial population: "+avgf);
        System.out.println("Best initial individual: Function "+BinTools.bin2DecBig(population[bestpos]));
        System.out.println("Best initial individual HW: "+BinTools.computeHW(population[bestpos]));
        System.out.println("Average HW in initial population: "+avghw);
        
        System.out.println("Generation\tBest fit\tAvg fit\tBest HW\tavgHW");
       
        //Step 2: Iterate the Steady-state GA for fiteval evaluations
        for(int i=0; i<fiteval; i++){
            //Step 2a: select two individuals from the population for crossover,
            //using tournament selection. In this case, the problem is of
            //maximizing the fitness function (=nonlinearity), so the objective
            //flag is set to true
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
                    
                    //Apply balanced crossover operator with counters.
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
                
                case 4: {
                    
                    //Apply balanced crossover operator with counters and allowed unbalancedness.
                    child = CrossoverOperators.unbalancedCounterCrossWeighted(
                            genrand, population[candpos[0]],
                            population[candpos[1]], weight, shuffle, unbalanceProb);
                    
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
            double fitchild = FitnessFunctions.compFitnessBF(child, nvar, unbal);
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
            
            //Step 2e: Update value of the unbalanceProb
            if(i%frequpd == 0 && i>0) {
                unbalanceProb *= coolingparam;
            }
            if(i%freqprint == 0 && i>0) {
                //Compute average fitness and average HW
                avgf = 0.0;
                avghw = 0.0;
                for(int l=0; l<fitnesses.length; l++) {
                    avgf += fitnesses[l];
                    avghw += BinTools.computeHW(population[l]);
                }
                avgf /= fitnesses.length;
                avghw /= population.length;
                System.out.println(i+"\t"+bestfit+"\t"+avgf+"\t"+BinTools.computeHW(population[bestpos])+"\t"+avghw+"\t");
            }
        }
        
        //Print final info
        //Compute average fitness and average HW
        avgf = 0.0;
        avghw = 0.0;
        for(int l=0; l<fitnesses.length; l++) {
            avgf += fitnesses[l];
            avghw += BinTools.computeHW(population[l]);
        }
        avgf /= fitnesses.length;
        avghw /= population.length;
        System.out.println("Best fitness in final population: "+bestfit);
        System.out.println("Average fitness in final population: "+avgf);
        System.out.println("Best final individual: Function "+BinTools.bin2DecBig(population[bestpos]));
        System.out.println("Best final individual HW: "+BinTools.computeHW(population[bestpos]));
        System.out.println("Average HW in final population: "+avghw);
        
    }
    
}

