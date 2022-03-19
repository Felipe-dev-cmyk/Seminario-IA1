package agentes;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.util.Arrays;
import java.util.Random;

public class GeneticAlgorithm extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " started.");

        MaxOnesBehaviour max = new MaxOnesBehaviour();
        max.action();
        doDelete();
    }

    //Comportamiento Cyclic para Sumatorias
    private class MaxOnesBehaviour extends Behaviour {

        int generation = 1;
        int populationSize = 100, elitismValue;
        int[] fitness = new int[populationSize];
        String[] population = new String[populationSize];
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
                    System.out.println("Recombinando...");
                    population = crossover.CrossOver(populationSize, population, fitness);
                    System.out.println("Recombinacion lista");
                }

                doubleRand = random.nextDouble();
                if (mutationRate > doubleRand) {
                    System.out.println("Mutando...");
                    population = mutate.Mutate(populationSize, population);
                    System.out.println("Mutacion lista");
                }
                isCriterionSatisfied = populationM.EvaluatePopulation(populationSize, population);
                generation++;
            }
            System.out.println("\nFin de programa");
            if (!isCriterionSatisfied) {
                System.out.println("Solución no encontrada");
            } else {
                System.out.println("Total de generaciones: " + generation);
                for (int i = 0; i < populationSize; i++) {
                    if ("1111111111".equals(population[i])) {
                        System.out.println(i + ". Fittest: " + population[i]);
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
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    public class PopulationBehaviour extends Behaviour {

        String aux = "";
        Random random = new Random();

        @Override
        public void action() {
        }

        public void Generate(int populationSize, String[] population) {
            int intRand;
            for (int i = 0; i < populationSize; i++) { //Cantidad de genes
                aux = "";
                for (int j = 0; j < 10; j++) { //Cantidad de digitos en los genes
                    intRand = random.nextInt(2);
                    aux += Integer.toString(intRand);
                }
                population[i] = aux;
            }
        }

        public int[] EvaluateFitness(int populationSize, String[] population) {
            int[] fitnessAux = new int[populationSize];
            for (int i = 0; i < populationSize; i++) {
                fitnessAux[i] = getFitness(population[i], '1');
            }
            return fitnessAux;
        }

        public boolean EvaluatePopulation(int populationSize, String[] population) {
            for (int i = 0; i < populationSize; i++) {
                if ("1111111111".equals(population[i])) {
                    return true;
                }
            }
            return false;
        }

        public int getFitness(String cadena, char caracter) {
            int posicion, contador = 0;
            //se busca la primera vez que aparece
            posicion = cadena.indexOf(caracter);
            while (posicion != -1) { //mientras se encuentre el caracter
                contador++; //se cuenta
                //se sigue buscando a partir de la posición siguiente a la encontrada
                posicion = cadena.indexOf(caracter, posicion + 1);
            }
            return contador;
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

        public String[] CrossOver(int populationSize, String[] population, int[] fitness) {
            int parentIndx;
            String[] populationAux = new String[populationSize];
            RouletteWheelBehaviour roulette = new RouletteWheelBehaviour();

            for (int i = 0; i < populationSize; i++) {
                parentIndx = roulette.SelectParents(populationSize, fitness, population);

                String part1 = population[i].substring(0, 2);
                String part2 = population[parentIndx].substring(2, 4);
                String part3 = population[i].substring(4, 6);
                String part4 = population[parentIndx].substring(6, 8);
                String part5 = population[i].substring(8, 10);

                populationAux[i] = part1 + part2 + part3 + part4 + part5;
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

        public String[] Mutate(int populationSize, String[] population) {
            String[] populationAux = new String[populationSize];
            String auxGene;
            for (int i = 0; i < populationSize; i++) {
                auxGene = "";
                for (int j = 0; j < 10; j++) {
                    if (population[i].charAt(j) == '1') {
                        auxGene += '0';
                    } else if (population[i].charAt(j) == '0') {
                        auxGene += '1';
                    }
                }
                populationAux[i] = auxGene;
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

        public int SelectParents(int populationSize, int[] fitness, String[] population) {
            int sumFitness = 0, indx = -1;
            double[] promFitIndividual = new double[populationSize + 1];
            promFitIndividual[populationSize] = -1;
            for (int i = 0; i < populationSize; i++) {
                sumFitness += fitness[i];
            }
            for (int i = 0; i < populationSize; i++) {
                promFitIndividual[i] = ((double) fitness[i] / sumFitness) * 100;
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
