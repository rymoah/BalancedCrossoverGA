# Balanced Crossover Operators for GA

GitHub repository for the source code and the experimental data of the paper:

L. Manzoni, L. Mariot, E. Tuba: Balanced Crossover Operators in Genetic Algorithms. Swarm and Evolutionary Computation 54:100646 (2020) DOI: https://doi.org/10.1016/j.swevo.2020.100646 (postprint available at https://lucamariot.org/files/papers/mmt_swevo_2019_postprint.pdf)

Compiling
---------
To compile the code, just run javac *.java from inside the directory src/

Running
-------
The three main classes that can be run are the following:

* SteadyGABentBF:      Steady-state GA for the bent functions optimization problem
* SteadyGABF:	       Steady-state GA for the balanced nonlinear boolean function optimization problem
* SteadyGAOA:	       Steady-state GA for the binary orthogonal arrays optimization problem

Launching any class by java ClassName without arguments prints some information on how to run the GA.

### Examples:

$ java SteadyGABent 6 50 500000 3 0.2 0 -6217988296348310147

Launch the steady-state GA for the bent function problem for functions of n=6 variables, a population of
P=50 individuals, number of fitness evaluations fit=500000, tournament size t=3, mutation probability
p_m=0.2, crossover operator 0 (one-point crossover) and initial random seed -6217988296348310147

$ java SteadyGABF 6 50 500000 3 0.2 1 -6217988296348310147

Launch the steady-state GA for the nonlinear balanced boolean functions problem for functions of n=6
variables, a population of P=50 individuals, number of fitness evaluations fit=500000, tournament size
t=3, mutation probability p_m=0.2, crossover operator 1 (counter-based balanced crossover) and initial
random seed -6217988296348310147

$ java SteadyGAOA 4 8 3 2 2 50 500000 3 0.2 2 -6217988296348310147

Launch the steady-state GA for the binary orthogonal arrays problem for arrays of N=2^4 rows, k=8 columns,
strength t=3, lambda=2, exponent in fitness function p=2 (i.e. use Euclidean distance), P=50 individuals,
number of fitness evaluations fit=500000, tournament size t=3, mutation probability p_m=0.2, crossover
operator 2 (zero-lengths balanced crossover) and initial random seed -6217988296348310147
