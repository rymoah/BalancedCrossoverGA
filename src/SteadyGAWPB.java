/**
 *
 * @author rymoah
 */

import boolfun.*;
import genalg.*;
import java.util.Random;

public class SteadyGAWPB {
    
    public static void main(String[] args) {
        
        if(args.length != 10) {
            
            System.err.println("\nUsage: java genalg.SteadyGAWPB nvar popsize "
                    + "fiteval freqprint tournsize mutprob crossop shuffle seed coolingparam unbalanceProb frequency unbal");
            System.err.println("\nwhere:");
            System.err.println("- nvar is the number of variables of the boolean functions");
            System.err.println("- popsize is the size of the population");
            System.err.println("- fiteval is the maximum number of fitness evaluations");
            System.err.println("- fitfun is the fitness function to be maximized (false: sum nonlinearity, true: min nonlinearity)");
            System.err.println("- freqprint is the sampling frequency to print info on the population (once every freqprint fitness evaluations)");
            System.err.println("- tournsize is the size of the tournament for selection");
            System.err.println("- mutprob is the mutation probability");
            System.err.println("- crossop is the code for the crossover operator "
                    + "(0: one-point crossover, 1: balanced crossover with counters, 2: balanced crossover with zero-length maps, 3: balanced crossover with maps of 1s\");)");
            System.err.println("- shuffle is a boolean flag specifying whether to shuffle the parents positions to be copied in balanced crossover operators");
            System.err.println("- unbal is a boolean flag to specify whether unbalancedness penalty factor must be considered in the fitness function computation or not\n");
            
            System.exit(1);
            
        }
        
        //Read command-line parameters
        int nvar = Integer.parseInt(args[0]);
        int popsize = Integer.parseInt(args[1]);
        int fiteval = Integer.parseInt(args[2]);
        boolean fitfunc = Boolean.parseBoolean(args[3]);
        int freqprint = Integer.parseInt(args[4]);
        int tournsize = Integer.parseInt(args[5]);
        double mutprob = Double.parseDouble(args[6]);
        int crossop = Integer.parseInt(args[7]);
        boolean shuffle = Boolean.parseBoolean(args[8]);
        boolean unbal = Boolean.parseBoolean(args[9]);
        
        //Initialize random number generator with the input seed
        Random genrand = new Random();
        
        int flength = (int)Math.pow(2, nvar);    //length of the boolean functions truth table, 2^nvar
        int[] sizes = CombTools.allBinCoeffs(nvar);
        boolean[][][] inmat = CombTools.genBinCombsMatrix(nvar, sizes);
        
        //Step 1: generate initial population of boolean functions of nvar
        //variables (random if we use one-point crossover, WPB if balanced crossover
        //are used), evaluate their fitness, and find best and worse individual
        boolean[][] population;
        if(unbal) {
            population = GeneticTools.createUnbalPop(flength, popsize, genrand);
        } else {
            population = GeneticTools.createWPBPop(flength, popsize, inmat, genrand);
        }
        double[] fitnesses = FitnessFunctions.compFitnessWPBFuncPop(population, nvar, inmat, sizes, unbal, fitfunc);
        int bestpos = GeneticTools.findMaxFitIndivPos(fitnesses);
        double bestfit = fitnesses[bestpos];
        
        //Print initial information
        System.out.println("GA Info:");
        System.out.println("\n- Number of variables: "+nvar);
        System.out.println("- Population size: "+popsize);
        System.out.println("- Fitness evaluations: "+fiteval);
        System.out.print("- Fitness function: ");
        if(fitfunc && unbal) {
            System.out.println("delta_pen * min(nl(f)) - pen(f)");
        } else {
            if(fitfunc && !unbal) {
                System.out.println("min(nl(f)");
            } else {
                if(!fitfunc && unbal) {
                    System.out.println("delta_pen * sum(nl(f)) - pen(f)");
                } else {
                    System.out.println("sum(nl(f))");
                }
            }
        }
        System.out.println("- Sampling frequency: "+freqprint);
        System.out.println("- Tournament size: "+tournsize);
        System.out.println("- Mutation probability: "+mutprob);
        System.out.println("- Crossover Operator: "+crossop);
        System.out.println("- Shuffle parents positions: "+shuffle);
        
        //Print initial info
        double avgf = 0.0;
        double avghw = 0.0;
        for(int l=0; l<fitnesses.length; l++) {
            avgf += fitnesses[l];
            avghw += BinTools.computeHW(population[l]);
        }
        avgf /= fitnesses.length;
        avghw /= population.length;
        System.out.println("\nBest fitness in initial population: "+bestfit);
        System.out.println("Average fitness in initial population: "+avgf);
        System.out.println("Best initial individual: Function "+BinTools.bin2DecBig(population[bestpos]));
        System.out.println("Best initial individual HW: "+BinTools.computeHW(population[bestpos]));
        System.out.println("Average HW in initial population: "+avghw);
        int[][] bestreswt = BoolTransf.resWalshTransforms(population[bestpos], nvar, inmat);
        int[] bestnls = BoolTransf.compResNl(nvar, bestreswt, sizes);
        int[] bestunb = BoolTransf.compResUnb(nvar, population[bestpos], inmat, sizes);
        
        System.out.print("Nonlinearities: ");
        for(int i=0; i<bestnls.length; i++) {
            System.out.print(bestnls[i]+" ");
        }
        System.out.println("");

        System.out.print("Unbalancedness: ");
        for(int i=0; i<bestunb.length; i++) {
            System.out.print(bestunb[i]+" ");
        }
        System.out.println("");
        
        System.out.println("Generation\tBest fit\tAvg fit");
       
        //Step 2: Iterate the Steady-state GA for fiteval evaluations
        for(int i=1; i<=fiteval; i++){
            //Step 2a: select two individuals from the population for crossover,
            //using tournament selection. In this case, the problem is of
            //maximizing the fitness function (=nonlinearity), so the objective
            //flag is set to true
            int[] candpos = GeneticTools.tournSelection(popsize, fitnesses, tournsize, genrand, true);
            
            //Step 2b: apply crossover on the two candidate parents, and apply
            //mutation on the resulting child
            boolean[] child = CrossoverOperators.crossoverWPB(population[candpos[0]], population[candpos[1]], inmat, crossop, shuffle, genrand, mutprob);
            
            
            //Step 2d: Evaluate child's fitness and apply elitist replacement
            double fitchild = FitnessFunctions.compFitnessWPBFunc(nvar, child, inmat, sizes, unbal, fitfunc);
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
            
            if(i%freqprint == 0 && i>0) {
                //Compute average fitness and average HW
                avgf = 0.0;
                for(int l=0; l<fitnesses.length; l++) {
                    avgf += fitnesses[l];
                }
                avgf /= fitnesses.length;
                System.out.println(i+"\t"+bestfit+"\t"+avgf);
            }
        }
        
        //Print final info
        //Compute average fitness and average HW
        avgf = 0.0;
     
        for(int l=0; l<fitnesses.length; l++) {
            avgf += fitnesses[l];            
        }
        avgf /= fitnesses.length;
        System.out.println("Best fitness in final population: "+bestfit);
        System.out.println("Average fitness in final population: "+avgf);
        System.out.println("Best final individual: Function "+BinTools.bin2DecBig(population[bestpos]));
        
        bestreswt = BoolTransf.resWalshTransforms(population[bestpos], nvar, inmat);
        bestnls = BoolTransf.compResNl(nvar, bestreswt, sizes);
        bestunb = BoolTransf.compResUnb(nvar, population[bestpos], inmat, sizes);
        
        System.out.print("Nonlinearities: ");
        for(int i=0; i<bestnls.length; i++) {
            System.out.print(bestnls[i]+" ");
        }
        System.out.println("");

        System.out.print("Unbalancedness: ");
        for(int i=0; i<bestunb.length; i++) {
            System.out.print(bestunb[i]+" ");
        }
        System.out.println("");
        
    }
    
}
