package agentes;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.util.Random;

public class GeneticAlgorithm extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " started.");

        RLSBehaviour max = new RLSBehaviour();
        max.action();
        doDelete();
    }

    //Comportamiento Cyclic para Sumatorias
    private class RLSBehaviour extends Behaviour {

        int generation = 1;
        int populationSize = 100, elitismValue;
        int[][] fitness = new int[populationSize][2];
        String[][] population = new String[populationSize][2];
        double crossoverRate = .3, mutationRate = .5;
        boolean isCriterionSatisfied = false;
        Random random = new Random();

        @Override
        public void action() {
            PopulationBehaviour populationM = new PopulationBehaviour();
            CrossoverBehaviour crossover = new CrossoverBehaviour();
            MutationBehaviour mutate = new MutationBehaviour();
            double doubleRand;

            populationM.Generate(populationSize, population);
            isCriterionSatisfied = populationM.EvaluatePopulation(populationSize, population);
            while (!isCriterionSatisfied && generation < 10000) {
                fitness = populationM.EvaluateFitness(populationSize, population);
                System.out.println("Generación: " + generation);
                doubleRand = random.nextDouble();

                if (crossoverRate > doubleRand) {
                    System.out.print("Recombinando...");
                    population = crossover.CrossOver(populationSize, population, fitness);
                    System.out.println("\nRecombinacion lista");
                }

                doubleRand = random.nextDouble();
                if (mutationRate > doubleRand) {
                    System.out.print("Mutando...");
                    population = mutate.Mutate(populationSize, population);
                    System.out.println("\nMutacion lista");
                }
                isCriterionSatisfied = populationM.EvaluatePopulation(populationSize, population);
                generation++;
            }
            System.out.println("\nFin de programa");
            if (!isCriterionSatisfied) {
                int fittestB0 = 0, fittestB1 = 0, indxB0 = 0, indxB1 = 0;
                System.out.println("Solución no encontrada");
                for(int i = 0; i < populationSize; i++){
                    if(fittestB0 < fitness[i][0]){
                        fittestB0 = fitness[i][0];
                        indxB0 = i;
                    }
                    if(fittestB1 < fitness[i][0]){
                        fittestB1 = fitness[i][1];
                        indxB1 = i;
                    }
                }
                System.out.println("Mejor Fitness B0: " + fittestB0 + "%: " + Integer.parseInt(population[indxB0][0], 2));
                System.out.println("Mejor Fitness B1: " + fittestB1 + "%: " + Integer.parseInt(population[indxB1][1], 2));
            } else {
                System.out.println("Total de generaciones: " + generation);
                for (int i = 0; i < populationSize; i++) {
                    if ("10101000".equals(population[i][0]) && "10111".equals(population[i][1])) {
                        System.out.println(i + ". Fittest B0: " + Integer.parseInt(population[i][0], 2));
                        System.out.println(i + ". Fittest B1: " + Integer.parseInt(population[i][1], 2));
                    }
                }
            }
        }

        @Override
        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }

        @Override
        public boolean done() {
            return true;
        }
    }

    public class PopulationBehaviour extends Behaviour {

        Random random = new Random();

        @Override
        public void action() {
        }

        public void Generate(int populationSize, String[][] population) {
            int intRand;
            String auxB0, auxB1;
            for (int i = 0; i < populationSize; i++) { //Cantidad de genes
                auxB0 = "";
                auxB1 = "";
                for (int j = 0; j < 8; j++) { //Cantidad de digitos en los genes X
                    intRand = random.nextInt(2);
                    auxB0 += Integer.toString(intRand);
                }
                for (int j = 0; j < 5; j++) { //Cantidad de digitos en los genes Y
                    intRand = random.nextInt(2);
                    auxB1 += Integer.toString(intRand);
                }
                population[i][0] = auxB0;
                population[i][1] = auxB1;
            }
        }

        public int[][] EvaluateFitness(int populationSize, String[][] population) {
            int[][] fitnessAux = new int[populationSize][2];
            for (int i = 0; i < populationSize; i++) {
                fitnessAux[i][0] = getFitness(population[i][0], 0); //X
                fitnessAux[i][1] = getFitness(population[i][1], 1); //Y
            }
            return fitnessAux;
        }

        public boolean EvaluatePopulation(int populationSize, String[][] population) {
            for (int i = 0; i < populationSize; i++) {
                if ("10101000".equals(population[i][0]) && "10111".equals(population[i][1])) {
                    return true;
                }
            }
            return false;
        }

        public int getFitness(String cadena, int b) {
            int auxB = Integer.parseInt(cadena, 2);
            if (b == 0) { //X
                return 100 - Math.abs(auxB - 200);
            } else if (b == 1) { //Y
                return 100 - Math.abs(auxB - 4);
            }
            return -1;
        }

        public int[] getFittest(String[][] population, int populationSize) {
            int[] fittestIndx = {-1, -1};
            for (int i = 0; i < populationSize; i++) {
                if ("11001000".equals(population[i][0])) {
                    fittestIndx[0] = i;
                }
                if ("100".equals(population[i][1])) {
                    fittestIndx[1] = i;
                }
            }
            return fittestIndx;
        }

        @Override
        public boolean done() {
            return true;
        }
    }

    private class CrossoverBehaviour extends Behaviour {

        Random random = new Random();

        @Override
        public void action() {

        }

        public String[][] CrossOver(int populationSize, String[][] population, int[][] fitness) {
            int parentIndx;
            String part1, part2, part3, part4;
            //String[][] populationAux = new String[populationSize][2];
            String[][] populationAux = population;
            RouletteWheelBehaviour roulette = new RouletteWheelBehaviour();
            PopulationBehaviour populationMAux = new PopulationBehaviour();

            int[] indxFittest = populationMAux.getFittest(population, populationSize);
            if (indxFittest[0] != -1 && indxFittest[1] != -1) { //Tenemos B0 y B1 pero en distintas coordenadas
                populationAux[indxFittest[0]][0] = population[indxFittest[0]][0];
                populationAux[indxFittest[0]][1] = population[indxFittest[1]][1];
            } else if (indxFittest[0] != -1 && indxFittest[1] == -1) { //Tenemos solo B0
                //Recombinar B1
                System.out.print("B1");
                for (int i = 0; i < populationSize; i++) {
                    parentIndx = roulette.SelectParents(populationSize, fitness, population, 1);

                    part1 = population[i][1].substring(0, 2);
                    part2 = population[parentIndx][1].substring(2, 5);

                    populationAux[i][1] = part1 + part2;
                }
            } else if (indxFittest[0] == -1 && indxFittest[1] != -1) { //Tenemos solo B1
                //Recombinar B0
                System.out.print("B0");
                for (int i = 0; i < populationSize; i++) {
                    parentIndx = roulette.SelectParents(populationSize, fitness, population, 0);

                    part1 = population[i][0].substring(0, 2);
                    part2 = population[parentIndx][0].substring(2, 4);
                    part3 = population[i][0].substring(4, 6);
                    part4 = population[parentIndx][0].substring(6, 8);

                    populationAux[i][0] = part1 + part2 + part3 + part4;
                }
            } else {
                System.out.print("Ambos valores");
                for (int i = 0; i < populationSize; i++) {
                    parentIndx = roulette.SelectParents(populationSize, fitness, population, 0);

                    part1 = population[i][0].substring(0, 2);
                    part2 = population[parentIndx][0].substring(2, 4);
                    part3 = population[i][0].substring(4, 6);
                    part4 = population[parentIndx][0].substring(6, 8);

                    populationAux[i][0] = part1 + part2 + part3 + part4;

                    parentIndx = roulette.SelectParents(populationSize, fitness, population, 1);

                    part1 = population[i][1].substring(0, 2);
                    part2 = population[parentIndx][1].substring(2, 5);

                    populationAux[i][1] = part1 + part2;

                }
            }
            return populationAux;

        }

        @Override
        public boolean done() {
            return true;
        }

        @Override
        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }

    private class MutationBehaviour extends Behaviour {

        @Override
        public void action() {
        }

        public String[][] Mutate(int populationSize, String[][] population) {
            String[][] populationAux = population;
            PopulationBehaviour populationMAux = new PopulationBehaviour();
            String auxGene;

            int[] indxFittest = populationMAux.getFittest(population, populationSize);
            if (indxFittest[0] != -1 && indxFittest[1] == -1) { //Tenemos solo B0
                //Mutar B1
                System.out.print("B1");
                for (int i = 0; i < populationSize; i++) {
                    auxGene = "";
                    for (int j = 0; j < 5; j++) {
                        if (population[i][1].charAt(j) == '1') {
                            auxGene += '0';
                        } else if (population[i][1].charAt(j) == '0') {
                            auxGene += '1';
                        }
                    }
                    populationAux[i][1] = auxGene;
                }
            } else if (indxFittest[0] == -1 && indxFittest[1] != -1) { //Tenemos solo B1
                //Mutar B0
                System.out.print("B0");
                for (int i = 0; i < populationSize; i++) {
                    auxGene = "";
                    for (int j = 0; j < 8; j++) {
                        if (population[i][0].charAt(j) == '1') {
                            auxGene += '0';
                        } else if (population[i][0].charAt(j) == '0') {
                            auxGene += '1';
                        }
                    }
                    populationAux[i][0] = auxGene;
                }
            } else {
                //Mutar ambos
                System.out.print("Ambos valores");
                for (int i = 0; i < populationSize; i++) { //Mutar B0
                    auxGene = "";
                    for (int j = 0; j < 8; j++) {
                        if (population[i][0].charAt(j) == '1') {
                            auxGene += '0';
                        } else if (population[i][0].charAt(j) == '0') {
                            auxGene += '1';
                        }
                    }
                    populationAux[i][0] = auxGene;
                    for (int j = 0; j < 5; j++) { //Mutar B1
                        if (population[i][1].charAt(j) == '1') {
                            auxGene += '0';
                        } else if (population[i][1].charAt(j) == '0') {
                            auxGene += '1';
                        }
                    }
                    populationAux[i][1] = auxGene;
                }
            }
            return populationAux;
        }

        @Override
        public boolean done() {
            return true;
        }

        @Override
        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }

    }

    public class RouletteWheelBehaviour extends Behaviour {

        Random random = new Random();

        @Override
        public void action() {
        }

        public int SelectParents(int populationSize, int[][] fitness, String[][] population, int b) {
            int sumFitness = 0, indx = -1;
            double[] promFitIndividual = new double[populationSize + 1];
            promFitIndividual[populationSize] = -1;
            if (b == 0) { //Seleccionar B0
                for (int i = 0; i < populationSize; i++) {
                    sumFitness += fitness[i][0];
                }
                for (int i = 0; i < populationSize; i++) {
                    promFitIndividual[i] = ((double) fitness[i][0] / sumFitness) * 100;
                }
            } else if (b == 1) { //Seleccionar B1
                for (int i = 0; i < populationSize; i++) {
                    sumFitness += fitness[i][1];
                }
                for (int i = 0; i < populationSize; i++) {
                    promFitIndividual[i] = ((double) fitness[i][1] / sumFitness) * 100;
                }
            }

            while (indx == -1) {
                for (int i = 0; i < populationSize; i++) {
                    double doubleRand = random.nextDouble();
                    if (doubleRand > promFitIndividual[i] && doubleRand < promFitIndividual[i + 1]) {
                        indx = i;
                        break;
                    }
                }
            }
            return indx;
        }

        @Override
        public boolean done() {
            return true;
        }
    }
}
