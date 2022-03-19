package agentes;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import java.util.Scanner;

public class RLS extends Agent {

    protected void setup() {
        System.out.println(getLocalName() + " started.");

        // Add the generic behaviour
        RLSBehaviour rls = new RLSBehaviour();
        rls.action();
        doDelete();
    }

    //Comportamiento Cyclic para Sumatorias
    private class RLSBehaviour extends Behaviour {

        private int sumx2 = 0, sumX = 0, sumY = 0, sumXY = 0;
        private double B0, B1;

        public void action() {
            DataSetBehaviour data = new DataSetBehaviour();
            HelperArithmeticBehaviour sumatorias = new HelperArithmeticBehaviour();
            //Sumatorias
            sumX = sumatorias.sum(data.getX());
            sumY = sumatorias.sum(data.getY());
            sumXY = sumatorias.sumProduc(data.getX(), data.getY());
            sumx2 = sumatorias.sumPow2(data.getX());
            int[] r1 = {9, sumX};
            int[] r2 = {sumX, sumx2};
            int[] r3 = {sumY, sumXY};
            int[][] m = {r1, r2};

            //Cramer
            CramerBehaviour cramer = new CramerBehaviour();
            cramer.inicialize(2);
            cramer.setA(m);
            cramer.setB(r3);
            cramer.cramer();
            
            //Salida
            double[] aux = cramer.getCmr();
            B0 = aux[0];
            B1 = aux[1];
            System.out.println(cramer.getRes());
            System.out.println("Sales = " + String.format("%.3f", B0) + " + " + String.format("%.3f", B1) + " Advertising \n");
            
            //Predicción
            PredictBehaviour predict = new PredictBehaviour();
            predict.Predict(aux);

        }

        @Override
        public boolean done() {
            return true;
        }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }

    private class DataSetBehaviour extends Behaviour {

        private int[] x = {23, 26, 30, 34, 43, 48, 52, 57, 58};
        private int[] y = {651, 762, 856, 1063, 1190, 1298, 1421, 1440, 1518};
        
        //private int[] x = {1,2,3,4,5,6,7,8,9};
        //private int[] y = {2,4,6,8,10,12,14,16,18};

        @Override
        public void action() {

        }

        @Override
        public boolean done() {
            return true;
        }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }

        public int[] getX() {
            return x;
        }

        public int[] getY() {
            return y;
        }
    }

    private class HelperArithmeticBehaviour extends Behaviour {

        private int sum, i = 0;

        public void action() {
        }

        public int sum(int[] valores) {
            sum = 0;
            for (i = 0; i < 9; i++) {
                sum += valores[i];
            }
            return sum;
        }

        public int sumProduc(int[] x, int[] y) {
            sum = 0;
            for (i = 0; i < 9; i++) {
                sum += x[i] * y[i];
            }
            return sum;
        }

        public int sumPow2(int[] valores) {
            sum = 0;
            for (i = 0; i < 9; i++) {
                sum += valores[i] * valores[i];
            }
            return sum;
        }

        public boolean done() {
            return i == 9;
        }

        public int onEnd() {
            myAgent.doDelete();
            return super.onEnd();
        }
    }

    private class PredictBehaviour extends Behaviour {

        private int x;

        @Override
        public void action() {
        }

        public void Predict(double[] B) {
            Scanner input = new Scanner(System.in);
            System.out.print("Valor de x para la predicción: ");
            x = input.nextInt();
            double predict = B[0] + (B[1] * x);
            System.out.println("La predicción para x = " + x + " es: " + String.format("%.3f", predict));
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
}
