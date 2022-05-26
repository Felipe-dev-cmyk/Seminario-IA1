package agente_pso;


import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.util.Random;
import java.lang.Math;
import java.text.DecimalFormat;

public class PSO extends Agent {


    @Override
    protected void setup() {
        System.out.println(getLocalName() + " started.");
        PSOBehaviour pso = new PSOBehaviour();
        pso.action();
        doDelete();
    }

    private class PSOBehaviour extends Behaviour {

        Random random = new Random();
        int t = 0, populationSize = 10;
        //double w = 0.72984, c1 = 2.05, c2 = 2.05; //Weight, Explotation, Exploration
        double w = 0.9, c1 = 2, c2 = 2;
        double r1 = random.nextDouble(), r2 = random.nextDouble();

        double[][] posPBest = new double[populationSize][3]; //Position of PersonalBest of each Particle
        double[] fitnessPBest = new double[populationSize]; //Fitness of PersonalBest of each Particle

        double posGBest[] = {0, 0, 0}; //Position of GlobalBest
        double fitnessGBest = -999;//Fitness of GlobalBest

        double[] fitness = new double[populationSize];
        double[][] velocity = new double[populationSize][3];
        double particlesPopulation[][] = new double[populationSize][3];
        boolean finished = false;

        @Override
        public void action() {
            PopulationBehaviour populationM = new PopulationBehaviour();
            populationM.Inicialize(populationSize, particlesPopulation, velocity);
            while(!finished && t < 3000){
                
                populationM.EvaluateFitness(populationSize, particlesPopulation, fitness);
                populationM.EvaluatePBest(populationSize, particlesPopulation, fitness, posPBest, fitnessPBest);
                fitnessGBest = populationM.EvaluateGBest(populationSize, particlesPopulation, fitness, posGBest, fitnessGBest);
                
                CalculateVelocity();
                UpdatePosition();
                finished = populationM.Finished(populationSize, fitness, fitnessGBest);
                System.out.println("Ronda " + (t+1));
                System.out.println("GlobalBest[" + t + "]: " + "[" + posGBest[0] + "," + posGBest[1] + "," + posGBest[2] + "]");
                System.out.println("FitnessGBest: " + fitnessGBest + "\n");
                t++;
                if(fitnessGBest > 90){
                    c1 = 0;
                    c2 = 4;
                }
                
            }
            DecimalFormat df = new DecimalFormat("###.##");
            for(int i = 0; i < populationSize; i++){
                System.out.println("ParticlesPos: " + i + ": [" + df.format(particlesPopulation[i][0]) + "," + df.format(particlesPopulation[i][1]) + "," + df.format(particlesPopulation[i][2]) + "]");
                System.out.println("ParticlesFitness " + i + ": " + df.format(fitness[i]));
            }
            
            System.out.println("\nFinal GlobalBest: " + "[" + df.format(posGBest[0]) + "," + df.format(posGBest[1]) + "," + df.format(posGBest[2]) + "]");
        }

        public void UpdatePosition() {
            //System.out.println("Actualizando posicion...");
            for (int i = 0; i < populationSize; i++) {
                particlesPopulation[i][0] += velocity[i][0]; //x
                particlesPopulation[i][1] += velocity[i][1]; //y
                particlesPopulation[i][2] += velocity[i][2]; //z
            }
        }
        
        public void CalculateVelocity(){
            //System.out.println("Calculando Velocidad...");
            for(int i = 0; i < populationSize; i++){
                velocity[i][0] = w * velocity[i][0] + (c1 * r1 * (posPBest[i][0] - particlesPopulation[i][0])) + (c2 * r2 * (posGBest[0] - particlesPopulation[i][0])); //x
                velocity[i][1] = w * velocity[i][1] + (c1 * r1 * (posPBest[i][1] - particlesPopulation[i][1])) + (c2 * r2 * (posGBest[1] - particlesPopulation[i][1])); //y
                velocity[i][2] = w * velocity[i][2] + (c1 * r1 * (posPBest[i][2] - particlesPopulation[i][2])) + (c2 * r2 * (posGBest[2] - particlesPopulation[i][2])); //z
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
        DataSet data = new DataSet();
        double Benetton = data.Benetton();
        
        @Override
        public void action() {
        }

        public void Inicialize(int populationSize, double[][] particlesPopulation, double[][] velocity) {
            int intRand;
            System.out.println("Inicializando...");
            for (int i = 0; i < populationSize; i++) { //Cantidad de particulas
                intRand = (int)(Math.random()*(350-300+1)+300); //x = 323
                velocity[i][0] = .1 * (int)(Math.random()*(10+1)+1);
                particlesPopulation[i][0] = intRand + velocity[i][0];
                
                intRand = (int)(Math.random()*(20-10+1)+10); //y = 14
                velocity[i][1] = .1 * (int)(Math.random()*(10+1)+1);
                particlesPopulation[i][1] = intRand + velocity[i][1];
                
                intRand = (int)(Math.random()*(50-40+1)+40); //z = 47
                velocity[i][2] = .1 * (int)(Math.random()*(10+1)+1);
                particlesPopulation[i][2] = intRand + velocity[i][2];
                System.out.println("Position[" + i + "]: " + "[" + particlesPopulation[i][0] + "," + particlesPopulation[i][1] + "," + particlesPopulation[i][2] + "]");
                System.out.println("Velocity[" + i + "]: " + "[" + velocity[i][0] + "," + velocity[i][1] + "," + velocity[i][2] + "]");
                
            }
            
        }

        public void EvaluatePBest(int populationSize, double[][] particlesPos, double[] particlesFitness, double[][] posPBest, double[] fitnessPBest) {
            //System.out.println("Evaluando PBest...");
            for (int i = 0; i < populationSize; i++) {
                //System.out.println("Fitness " + i + ": " + particlesFitness[i]);
                //System.out.println("Fitness PBest " + i + ": " + fitnessPBest[i]);
                if (particlesFitness[i] > fitnessPBest[i]) {
                    //System.out.println("Update of PBest " + i);

                    posPBest[i][0] = particlesPos[i][0]; //Change the position of the PBest
                    posPBest[i][1] = particlesPos[i][1];
                    posPBest[i][2] = particlesPos[i][2];
                    
                    fitnessPBest[i] = particlesFitness[i]; //Change the fitness of the PBest
                }
            }
        }

        public double EvaluateGBest(int populationSize, double[][] particlesPos, double[] particlesFitness, double[] posGBest, double fitnessGBest) {
            //System.out.println("Evaluando GBest...");
            for (int i = 0; i < populationSize; i++) {
                if (particlesFitness[i] > fitnessGBest) {
                    //System.out.println("Update of GBest " + i);

                    posGBest[0] = particlesPos[i][0]; //Change the position of the GBest
                    posGBest[1] = particlesPos[i][1];
                    posGBest[2] = particlesPos[i][2];
                    
                    //System.out.println("FitnessGBest: " + particlesFitness[i]);
                    fitnessGBest = particlesFitness[i]; //Change the fitness of the GBest
                    //System.out.println("FitnessGBest uploaded: " + particlesFitness[i]);
                }
            }
            return fitnessGBest;
        }

        public void EvaluateFitness(int populationSize, double[][] particlesPopulation, double[] particlesFitness) { //Fitness of each particle
            double aux = 0.0, sum = 0.0;
            //System.out.println("Evaluando Fitness...");
            for(int i = 0; i < populationSize; i++){
                /*x = particlesPopulation[i][0];
                y = particlesPopulation[i][1];
                z = particlesPopulation[i][2];
                
                particlesFitness[i] = ((10 * ((x-1) * (x-1))) + (20 * ((y-2) * (y-2))) + (30*((z-3) * (z-3))));*/
                sum = data.GetSum(particlesPopulation[i]);
                //System.out.println("Position " + i + ": [" + particlesPopulation[i][0] + "," + particlesPopulation[i][1] + "," + particlesPopulation[i][2] + "]");
                aux = (1.0 / 9.0) * sum;
                //System.out.println("Fitness: " + i + ": " + aux);
                particlesFitness[i] = 100 - Math.abs(aux - Benetton);
                System.out.println("Valor de  Benetton" + Benetton);
                //System.out.println("Fitness " + i + ": " + particlesFitness[i]);
                
            }
        }

        public boolean Finished(int populationSize, double[] particlesFitness, double gBestFitness){
            //System.out.println("Criterio de paro...");
            double difference, maxFitness = 100;
            difference = maxFitness - gBestFitness;
            for(int i = 0; i < populationSize; i++){
                if(difference < .5){ //Tolerancia de maximo fitness = 99.5
                    difference = gBestFitness - particlesFitness[i];
                    if(difference > .5){ //Tolerancia de distancia entre fitness, por ser double afectan los ultimos decimales
                        return false;
                    }
                }
                else{
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public boolean done() {
            return true;
        }
    }
    
    public class DataSet extends Behaviour{
        private int[] x = {23, 26, 30, 34, 43, 48, 52, 57, 58}; //Advertising
        private int[] y = {651, 762, 856, 1063, 1190, 1298, 1421, 1440, 1518}; //Sales
        private int[] z = {1, 2, 3, 4, 5, 6, 7, 8, 9}; //Year
        
        @Override
        public void action() {
            
        }
        
        public double GetSum(double[] particlePos){
            double sum = 0.0, aux = 0.0;
            for(int i = 0; i < 9; i++){
                aux = y[i] - (particlePos[0] + (particlePos[1] * x[i]) + (particlePos[2] * z[i]));
                sum += aux * aux;
            }
            return sum;
        }
        
        public double Benetton(){
            double sum = 0.0, aux = 0.0, aux2 = 0.0;
            for(int i = 0; i < 9; i++){
                aux = y[i] - (323 + (14 * x[i]) + (47 * z[i]));
                sum += aux * aux;
            }
            //System.out.println("Sumatoria valores Benetton: " + sum);
            aux = (1.0/9.0) * sum;
            //System.out.println("Ecuacion fitness valores Benetton: " + aux);
            
            //aux2 = 100 - Math.abs(aux - aux);
            return aux;
            //System.out.println("Fitness porcentaje: " + aux2);
            
            /*double a = 0.0, b = 0.0, c = 0.0;
            for(int i = 0; i < 9; i++){
                a += y[i];
                b += x[i];
                c += z[i];
            }
            aux = a - (323 + (14 * b) + (47 * c));
            aux = aux * aux;
            System.out.println("Sumatoria 2: " + aux);
            aux = (1.0/9.0) * aux;
            System.out.println("Fitness 2: " + aux);*/
        }
        
        public void Prueba(){
            double sum = 0.0, aux = 0.0, aux2 = 0.0;
            for(int i = 0; i < 9; i++){
                aux = y[i] - (323 + (14 * x[i]) + (47 * z[i]));
                sum += aux * aux;
            }
            
            aux = (1.0/9.0) * sum;
            System.out.println("Double antes: " + aux);
            aux = Math.round(aux);
            System.out.println("Double despues: " + aux);
            
        }
        
        @Override
        public boolean done() {
            return true;
        }
    }
}

