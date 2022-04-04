/**
 *
 * @author rymoah
 */

import java.util.Vector;
import java.util.Random;
import boolfun.*;
import genalg.*;
import java.util.Arrays;
import lon.*;

public class SteadyGALSBF {
    
    public static double[] computePopStats(SearchSolution[] pop) {
        
        double avgfit = 0.0;
        double varfit = 0.0;
        double stdfit = 0.0;
        double minfit = pop[0].fitness;
        double maxfit = 0.0;
        double medfit = 0.0;
        double[] fitnesses = new double[pop.length];
        
        //compute average, max and min fitnesses
        for(int i=0; i<pop.length; i++) {
            fitnesses[i] = pop[i].fitness;
            avgfit += fitnesses[i];
            if(fitnesses[i] < minfit) {
                minfit = fitnesses[i];
            }
            if(fitnesses[i] > maxfit) {
                maxfit = fitnesses[i];
            }
        }
        avgfit /= pop.length;
        
        //compute fitness variance and standard deviation
        for(int i=0; i<pop.length; i++) {
            varfit += Math.pow((fitnesses[i] - avgfit), 2);
        }
        varfit /= pop.length - 1;
        stdfit = Math.sqrt(varfit);
        
        //Sort fitness array and compute median
        Arrays.sort(fitnesses);
        if(pop.length % 2 == 0) {
            medfit = ((double)fitnesses[fitnesses.length/2] + (double)fitnesses[fitnesses.length/2 - 1])/2;
        } else {
            medfit = (double) fitnesses[fitnesses.length/2];
        }
        
        double avgdist = 0.0;
        double vardist = 0.0;
        double stddist = 0.0;
        double mindist = Math.pow(2, pop[0].nvar);
        double maxdist = 0.0;
        double meddist = 0.0;
        
        Vector<Double> dists = new Vector<Double>();
        
        //compute average, max and min distances
        for(int i=0; i<pop.length-1; i++) {
            for(int j=i+1; j<pop.length; j++) {
                double dist = (double)BinTools.computeHD(pop[i].function, pop[j].function);
                dists.add(dist);
                avgdist += dist;
                if(dist < mindist) {
                    mindist = dist;
                }
                if(dist > maxdist) {
                    maxdist = dist;
                }
            }
        }
        dists.trimToSize();
        avgdist /= dists.capacity();
        
        //compute distance variance and standard deviation
        for(int i=0; i<dists.capacity(); i++) {
            vardist += Math.pow((dists.elementAt(i) - avgdist), 2);
        }
        vardist /= dists.capacity() - 1;
        stddist = Math.sqrt(vardist);
        
        //Sort distance vector and compute median
        Arrays.sort(fitnesses);
        if(dists.capacity() % 2 == 0) {
            meddist = ((double)dists.elementAt(dists.capacity()/2) + (double)dists.elementAt(dists.capacity()/2 - 1))/2;
        } else {
            meddist = (double)dists.elementAt(dists.capacity()/2);
        }
        
        double[] stats = {avgfit, medfit, varfit, stdfit, maxfit, minfit, 
                          avgdist, meddist, vardist, stddist, maxdist, mindist};
        
        return stats;
        
    }
    
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
        System.out.println("\nInitial population info:");
        System.out.println("gen\teval\tavgf\tmedf\tvarf\tstdf\tmaxf\tminf\tavgd\tmedd\tvard\tstdd\tmaxd\tmind");
        //Compute statistics
        double[] stats = computePopStats(populationSS); 
        System.out.print(0+"\t"+0+"\t");
        for(int s=0; s<stats.length; s++) {
            System.out.printf("%.2f", stats[s]);
            System.out.print("\t");
        }
        System.out.println("");
        System.out.println("Best fitness in initial population: "+bestfit);
        
        System.out.println("\ngen\teval\tavgf\tmedf\tvarf\tstdf\tmaxf\tminf\tavgd\tmedd\tvard\tstdd\tmaxd\tmind");
        
        //Step 2: Iterate the Steady-state GA for fiteval evaluations
        int i = 0;
        int gen = 0;
        while(i<fiteval){
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
            i++;
            gen++;
            
            //Step 2e: Apply local search on the child
            if(steepest) {
                
                child = LocalSearchTools.hillClimb(child.function, child.fitness, child.walsht, child.nvar);
                
            } else {
                
                child = LocalSearchTools.findBestNeighbor(child.function, child.fitness, child.walsht, child.nvar);             
                
            }
            
            i += (child.eval/nvar);
            
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
            
            if(gen%freqprint == 0 && i>0) {
                //Compute statistics
                stats = computePopStats(populationSS); 
                System.out.print(gen+"\t"+i+"\t");
                for(int s=0; s<stats.length; s++) {
                    System.out.printf("%.2f", stats[s]);
                    System.out.print("\t");
                }
                System.out.println("");
            }
            
        }
        
        //Print final info
        //Compute average fitness and average HW
        System.out.println("\nfinal population info:");
        System.out.println("gen\teval\tavgf\tmedf\tvarf\tstdf\tmaxf\tminf\tavgd\tmedd\tvard\tstdd\tmaxd\tmind");
        //Compute statistics
        stats = computePopStats(populationSS); 
        System.out.print(0+"\t"+0+"\t");
        for(int s=0; s<stats.length; s++) {
            System.out.printf("%.2f", stats[s]);
            System.out.print("\t");
        }
        System.out.println("");
        System.out.println("Best fitness in final population: "+bestfit);
        
    }
    
}
