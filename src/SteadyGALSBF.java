/**
 *
 * @author rymoah
 */

import java.util.Random;
import boolfun.*;
import genalg.*;
import lon.*;

public class SteadyGALSBF {
    
    public static void main(String[] args) {
        
        if(args.length != 10) {
            
            System.err.println("\nUsage: java genalg.SteadyGALSBF nvar popsize "
                    + "fiteval freqprint tournsize mutprob crossop shuffle seed");
            System.err.println("\nwhere:");
            System.err.println("- nvar is the number of variables of the boolean functions");
            System.err.println("- popsize is the size of the population");
            System.err.println("- fiteval is the maximum number of fitness evaluations");
            System.err.println("- freqprint is the sampling frequency to print info on the population (once every freqprint fitness evaluations)");
            System.err.println("- tournsize is the size of the tournament for selection");
            System.err.println("- mutprob is the mutation probability");
            System.err.println("- crossop is the code for the crossover operator "
                    + "(0: one-point crossover, 1: balanced crossover with counters, 2: balanced crossover with zero-length maps, 3: balanced crossover with maps of 1s)");
            System.err.println("- steepest is a boolean flag specifying whether to apply steepest hill climbing (true) or single swap (false)");            
            System.err.println("- shuffle is a boolean flag specifying whether to shuffle the parents positions to be copied in balanced crossover operators");            
            System.err.println("- seed is the initial seed for the random number generator, read as a long variable\n");
            
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
        boolean steepest = Boolean.parseBoolean(args[7]);
        boolean shuffle = Boolean.parseBoolean(args[8]);
        long seed = Long.parseLong(args[9]);
        
        boolean unbal;
        
        //Determine unbalancedness flag (only set it to true if the crossover is one-point)
        if(crossop == 0) {
            unbal = true;
        } else {
            unbal = false;
        }
        
        //Initialize random number generator with the input seed
        Random genrand = new Random(seed);
        
        int tlength = (int)Math.pow(2, nvar);    //length of the boolean functions truth table, 2^nvar
        int weight = tlength/2;                  //weight of balanced boolean functions
        
        //Step 1: generate initial population of boolean functions of nvar
        //variables (unbalanced if we are using one-point crossover, balanced
        //otherwise), evaluate their fitness, and find best and worse individual
        //store data about population in array of SearchSolution
        SearchSolution[] populationSS;
        boolean[][] population;
        if(unbal) {
            population = GeneticTools.createUnbalPop(tlength, popsize, genrand);
        } else {
            population = GeneticTools.createBalPop(tlength, popsize, genrand);
        }
        populationSS = FitnessFunctions.compFitnessBF_SSPop(population, nvar, unbal);
        //double[] fitnesses = FitnessFunctions.compFitnessBFPop(population, nvar, unbal);
        int bestpos = GeneticTools.findMaxFitIndivPos(populationSS);
        double bestfit = populationSS[bestpos].fitness;
        
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
        System.out.println("Seed: "+seed);
        
        //Print initial info
        //Compute average fitness and average HW
        double avgf = 0.0;
        double avghw = 0.0;
        for(int l=0; l<populationSS.length; l++) {
            avgf += populationSS[l].fitness;
            avghw += BinTools.computeHW(population[l]);
        }
        avgf /= populationSS.length;
        avghw /= population.length;
        System.out.println("Best fitness in initial population: "+bestfit);
        System.out.println("Average fitness in initial population: "+avgf);
        System.out.println("Best initial individual: Function "+BinTools.bin2DecBig(population[bestpos]));
        System.out.println("Best initial individual HW: "+BinTools.computeHW(population[bestpos]));
        System.out.println("Average HW in final population: "+avghw);
        
        System.out.println("Generation\tBest fit\tAvg fit\tBest HW\tavgHW");
        
        //Step 2: Iterate the Steady-state GA for fiteval evaluations
        for(int i=0; i<fiteval; i++){
            //Step 2a: select two individuals from the population for crossover,
            //using tournament selection. In this case, the problem is of
            //maximizing the fitness function (=nonlinearity), so the objective
            //flag is set to true
            int[] candpos = GeneticTools.tournSelectionSS(popsize, populationSS, tournsize, genrand, true);
            
            //Step 2b: cross the two candidate parents, depending on the
            //crossover operator chosen
            boolean[] childtable = new boolean[tlength];
            
            switch(crossop) {
                
                case 0: {
                    
                    //Apply classic one-point crossover. Since this operator
                    //returns two children, select randomly one of them.
                    boolean[][] children = CrossoverOperators.onePointCrossover(
                            genrand, population[candpos[0]],
                            population[candpos[1]]);
                    
                    if(genrand.nextBoolean()) {
                        childtable = children[1];
                    } else {
                        childtable = children[0];
                    }
                    
                    break;
                    
                }
                
                case 1: {
                    
                    //Apply balanced crossover operator with counters.
                    childtable = CrossoverOperators.balancedCounterCrossWeighted(
                            genrand, population[candpos[0]],
                            population[candpos[1]], weight, shuffle);
                    
                    break;
                    
                }
                
                case 2: {
                    
                    //Apply balanced crossover operator with zeros-run length encoding
                    childtable = CrossoverOperators.balancedZeroLengthsCrossWeighted(
                            genrand, population[candpos[0]],
                            population[candpos[1]], weight, shuffle);
                    
                    break;
                    
                }
                
                case 3: {
                    
                    //Apply balanced crossover operator with map of 1s encoding
                    childtable = CrossoverOperators.uniformOneMapCrossShuffle(genrand,
                            population[candpos[0]], population[candpos[1]],
                            shuffle);
                    
                    break;
                    
                }
                
            }
            
            //Step 2c: Apply mutation operator on the child, depending on the
            //crossover operator used (one-point crossover: flip mutation, balanced crossover: swap mutation)
            if(crossop == 0) {
                MutationOperators.flipMutation(genrand, childtable, mutprob);
            } else {
                MutationOperators.swapMutation(genrand, childtable, mutprob);
            }
            
            //Step 2d: Evaluate child's fitness and apply elitist replacement
            SearchSolution child = FitnessFunctions.compFitnessBF_SS(childtable, nvar, false);
            
            //Step 2e: Apply local search on the child
            if(steepest) {
                
                //..
                
            } else {
                
                //..
                
            }
            
            if((child.fitness > populationSS[candpos[0]].fitness) || (child.fitness > populationSS[candpos[1]].fitness)) {
                
                //the child's fitness value is better than at least that of one
                //of its parents. Check if it is also better than the fitness of
                //the best individual in the population
                
                if(child.fitness > bestfit) {
                    
                    //the child is better than the best individual, thus apply
                    //replacement operator over the whole population and update
                    //best individual
                    int replpos = GeneticTools.selectForDeath(popsize, true,
                            bestpos, genrand);
                    populationSS[replpos] = child;
                    bestpos = replpos;
                    bestfit = child.fitness;
                    
                } else {
                    
                    //The child is not better than the best individual, thus
                    //apply replacement operator over the whole population
                    //*except* the best individual
                    int replpos = GeneticTools.selectForDeath(popsize, false,
                            bestpos, genrand);
                    populationSS[replpos] = child;
                        
                }
                
            }
            
            if(i%freqprint == 0 && i>0) {
                //Compute average fitness and average HW
                avgf = 0.0;
                avghw = 0.0;
                for(int l=0; l<populationSS.length; l++) {
                    avgf += populationSS[l].fitness;
                    avghw += BinTools.computeHW(populationSS[l].function);
                }
                avgf /= populationSS.length;
                avghw /= populationSS.length;
                System.out.println(i+"\t"+bestfit+"\t"+avgf+"\t"+BinTools.computeHW(populationSS[bestpos].function)+"\t"+avghw+"\t");
            }
            
        }
        
        //Print final info
        //Compute average fitness and average HW
        avgf = 0.0;
        avghw = 0.0;
        for(int l=0; l<populationSS.length; l++) {
            avgf += populationSS[l].fitness;
            avghw += BinTools.computeHW(populationSS[l].function);
        }
        avgf /= populationSS.length;
        avghw /= population.length;
        System.out.println("Best fitness in final population: "+bestfit);
        System.out.println("Average fitness in final population: "+avgf);
        System.out.println("Best final individual: Function "+BinTools.bin2DecBig(populationSS[bestpos].function));
        System.out.println("Best final individual HW: "+BinTools.computeHW(populationSS[bestpos].function));
        System.out.println("Average HW in final population: "+avghw);
        
    }
    
}
