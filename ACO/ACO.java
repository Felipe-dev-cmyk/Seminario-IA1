/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package agente_aco;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.text.DecimalFormat;
import java.util.Random;

public class ACO extends Agent {

    @Override
    protected void setup() {
        System.out.println(getLocalName() + " started.");
        ACOBehaviour aco = new ACOBehaviour();
        aco.action();
        doDelete();
    }

    private class ACOBehaviour extends Behaviour {

        DecimalFormat df = new DecimalFormat("#.###");
        int populationSize = 10, cities = 5;

        //double[][] costsMatrix = new double[populationSize][cities];
        int[][] costsMatrix = {
            {(0), (10), (12), (11), (14)},
            {(10), (0), (13), (15), (8)},
            {(12), (13), (0), (9), (14)},
            {(11), (15), (9), (0), (16)},
            {(14), (8), (14), (16), (0)}
        };
        //double[][] pheromoneMatrix = new double[populationSize][cities];
        double[][] pheromoneMatrix = {
            {(1), (1), (1), (1), (1)},
            {(1), (1), (1), (1), (1)},
            {(1), (1), (1), (1), (1)},
            {(1), (1), (1), (1), (1)},
            {(1), (1), (1), (1), (1)},};
        double[][] H = new double[cities][cities];
        int[][] personalPaths = new int[populationSize][cities];
        int[] totalDistances = new int[populationSize];
        boolean[][] visited = new boolean[populationSize][cities];

        @Override
        public void action() {
            int pos;
            int next, alpha = 2, beta = 2, iterations = 0;
            //boolean finished = false;
            RouletteWheelBehaviour ruleta = new RouletteWheelBehaviour();
            PopulationBehaviour pManager = new PopulationBehaviour();
            MatrixManagerBehaviour mManager = new MatrixManagerBehaviour();
            while (iterations < 200) {
                System.out.println("Iteración: " + iterations);
                for (int i = 0; i < populationSize; i++) {
                    pos = (int) (Math.random() * (4) + 1);
                    //System.out.println("Ant: " + i);
                    //System.out.println("Initial pos: " + pos);
                    mManager.InicializeH(H,costsMatrix,cities);
                    if (iterations == 100) {
                        alpha = 0;
                        beta = 2;
                    }
                    for (int j = 0; j < cities; j++) {
                        personalPaths[i][j] = pos;
                        visited[i][pos] = true;

                        mManager.ChangeH(H, pos, cities); //Cambiar en H las ciudades visitadas a 0
                        if (j != cities - 1) {
                            next = ruleta.Roulette(cities, pheromoneMatrix, H, pos, alpha, beta, visited[i]);
                            pos = next; //Moverse a la ciudad siguiente
                        }
                        //System.out.println("Next: " + next);
                        //while (costsMatrix[pos][next] == 0 && H[pos][next] == 0) { //Si desde la ciudad actual se puede llegar a la ciudad seleccionada
                        //System.err.println("Entra en el while");
                        //next = ruleta.Roulette(cities, pheromoneMatrix, H, pos, alpha, beta, visited[i]);
                        //}
                        //System.out.println("Next: " + next);

                    }
                    totalDistances[i] = pManager.CalculateDistance(personalPaths[i], costsMatrix, cities);
                    System.out.print("Ant " + i + ": ");
                    pManager.ShowPath(personalPaths[i], cities);
                    System.out.println(" Distance " + totalDistances[i]);
                }
                mManager.UpdatePheromone(totalDistances, pheromoneMatrix, personalPaths, populationSize, cities);
                pManager.ClearVisited(visited, populationSize, cities);
                iterations++;
            }
            GetLastPath();

        }

        public void GetLastPath() {
            System.out.println("\nPheromone Matrix");
            for (int i = 0; i < cities; i++) {
                for (int j = 0; j < cities; j++) {
                    System.out.print(df.format(pheromoneMatrix[i][j]) + "\t");
                }
                System.out.println();
            }
            System.out.println();
            double totalPheromone = 0.0;
            int[] lastPath = new int[cities];
            int city = 0;
            double[][] pheromoneMatrixTmp = new double[cities][cities];
            //System.out.println("Temporal");
            //Copia de pheromoneMatrix para ir descartando ciudades ya visitadas
            //Mismo procedimiento que con la matriz H
            for (int i = 0; i < cities; i++) {
                for (int j = 0; j < cities; j++) {
                    pheromoneMatrixTmp[i][j] = pheromoneMatrix[i][j];
                }
            }

            //Get best path
            System.out.println("Best path");
            for (int i = 0; i < cities; i++) {
                for (int j = 0; j < cities; j++) {
                    //System.out.print("Sum + " + df.format(pheromoneMatrix[i][j]));
                    if (totalPheromone < pheromoneMatrixTmp[city][j]) {
                        totalPheromone = pheromoneMatrixTmp[city][j];
                        lastPath[i] = j;
                    }
                    for (int k = 0; k < cities; k++) {
                        for (int l = 0; l < cities; l++) {
                            if (l == city) {
                                pheromoneMatrixTmp[k][l] = 0;
                            }
                        }
                    }
                }
                //System.out.println("City " + city + ": " + totalPheromone);
                System.out.println("City " + city + " to " + lastPath[i]);
                city = lastPath[i];
                totalPheromone = 0.0;
            }
            //System.out.println();

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

    public class PopulationBehaviour extends Behaviour {

        @Override
        public void action() {

        }

        public int GetVisited(boolean[] visited, int cities) {
            int count = 0;
            for (int i = 0; i < cities; i++) {
                if (visited[i]) {
                    count++;
                }
            }
            return count;
        }

        public int GetLast(boolean[] visited, int cities) {
            int value = -1;
            for (int i = 0; i < cities; i++) {
                if (!visited[i]) {
                    value = i;
                }
            }
            return value;
        }

        public void ClearVisited(boolean[][] visited, int populationSize, int cities) {
            for (int i = 0; i < populationSize; i++) {
                for (int j = 0; j < cities; j++) {
                    visited[i][j] = false;
                }
            }
        }

        public void ShowPath(int[] path, int cities) {
            for (int i = 0; i < cities; i++) {
                System.out.print(path[i] + ",");
            }
        }

        public int CalculateDistance(int[] personalPath, int[][] costsMatrix, int cities) {
            int distance = 0;
            for (int i = 0; i < cities - 1; i++) {
                //System.out.println("C: " + costsMatrix[personalPath[i]][personalPath[i + 1]]);
                distance += costsMatrix[personalPath[i]][personalPath[i + 1]];
            }
            return distance;
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

    public class MatrixManagerBehaviour extends Behaviour {

        @Override
        public void action() {

        }

        public void InicializeH(double[][] H, int[][] costsMatrix, int cities) {
            //System.out.println("Inicialize H");
            for (int i = 0; i < cities; i++) {
                for (int j = 0; j < cities; j++) {
                    if (i == j) {
                        H[i][j] = 0;
                    } else {
                        H[i][j] = (1.0 / costsMatrix[i][j]);
                        /*if(){
                            
                        }*/
                    }
                    //System.out.println(i + "," + j + ": " + H[i][j]);
                }
            }
        }

        public void ChangeH(double[][] H, int pos, int cities) {
            //System.out.println("Update H");
            for (int i = 0; i < cities; i++) {
                for (int j = 0; j < cities; j++) {
                    if (j == pos) {
                        H[i][j] = 0;
                    }
                    //System.out.println(i + "," + j + ": " + H[i][j]);
                }
            }
        }

        public void UpdatePheromone(int[] totalDistances, double[][] pheromoneMatrix, int[][] personalPaths, int populationSize, int cities) {
            for (int i = 0; i < cities; i++) { //Evaporacion
                for (int j = 0; j < cities; j++) {
                    pheromoneMatrix[i][j] = (1 - 0.5) * pheromoneMatrix[i][j];
                }
            }
            /*System.out.println("Matriz antes");
            for (int i = 0; i < cities; i++) {
                for (int j = 0; j < cities; j++) {
                    System.out.print(pheromoneMatrix[i][j] + ",");
                }
                System.out.println();
            }*/
            //System.out.println("Add pheromone");
            int x, y;
            double pheromone; //Dejar feromona
            for (int i = 0; i < populationSize; i++) { //i = ant
                pheromone = 1.0 / totalDistances[i];
                //System.out.println("\tAnt " + i + ": Pheromone: " + pheromone);
                for (int j = 0; j < cities - 1; j++) { //j = city
                    x = personalPaths[i][j]; //ciudad inicial
                    y = personalPaths[i][j + 1]; //ciudad siguiente
                    pheromoneMatrix[x][y] = pheromoneMatrix[x][y] + pheromone;
                    //System.out.println(pheromoneMatrix[personalPaths[i][j]][personalPaths[i][j + 1]]);
                    //System.out.println("[" + personalPaths[i][j] + "," + personalPaths[i][j + 1] + "]");
                    //System.out.println("XY [" + x + "," + y + "]");
                    //System.out.println(pheromoneMatrix[x][y]);
                }
                pheromoneMatrix[personalPaths[i][cities - 1]][personalPaths[i][0]] = pheromoneMatrix[personalPaths[i][cities - 1]][personalPaths[i][0]] + pheromone;
                /*System.out.println("Matriz despues de Ant " + i);
                for (int j = 0; j < cities; j++) {
                    for (int k = 0; k < cities; k++) {
                        System.out.print(df.format(pheromoneMatrix[j][k]) + ",");
                    }
                    System.out.println();
                }*/
            }
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
        PopulationBehaviour pManager = new PopulationBehaviour();

        @Override
        public void action() {
        }

        public int Roulette(int cities, double[][] pheromoneM, double[][] H, int pos, int alpha, int beta, boolean[] visited) {

            int cantidadActual = cities - pManager.GetVisited(visited, cities);

            //se asignan los valores al arreglo, como va en orden a la hora de seleccionar la ciudad se toma el primer valor != 0 del arreglo de ciudades no visitadas
            double[] p = new double[cities];
            double sum = 0;
            int indx = -1;

            if (cantidadActual == 1) { //Si solo hay una ciudad disponible
                indx = pManager.GetLast(visited, cities);
                if (indx != -1) {
                    return indx;
                }
            }

            for (int i = 0; i < cities; i++) { //Denominador
                p[i] = Math.pow(pheromoneM[pos][i], alpha) * Math.pow(H[pos][i], beta);
                sum += p[i];
            }
            double[] values = new double[cities]; //Probabilidades de seleccionar cada ciudad
            for (int i = 0; i < cities; i++) {
                values[i] = p[i] / sum;
            }
            double[] cumulative = new double[cities];
            int prev = 0, avaliable = 0;
            int[] punteroCumulative = new int[cantidadActual];
            for (int i = 0; i < cities; i++) {
                if (values[i] != 0) {
                    cumulative[i] = cumulative[prev] + values[i];
                    prev = i;
                    punteroCumulative[avaliable] = i;
                    avaliable++;
                }
            }

            while (indx == -1) {
                double doubleRand = random.nextDouble();
                for (int i = 0; i < cantidadActual - 1; i++) {
                    if (doubleRand > cumulative[punteroCumulative[i]] && doubleRand < cumulative[punteroCumulative[i + 1]] && !visited[punteroCumulative[i]]) {
                        indx = punteroCumulative[i];
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
