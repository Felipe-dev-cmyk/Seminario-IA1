/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package agentes;

import jade.core.behaviours.Behaviour;

public class CramerBehaviour extends Behaviour {

    private int tamaño;
    private int a[][];
    private int b[];
    private int solucion[];
    private double cmr[];
    private float determinante;
    private String res;

    @Override
    public void action() {
    }

    public void inicialize(int tam) {
        this.tamaño = tam;
        a = new int[this.tamaño][this.tamaño];
        b = new int[this.tamaño];
        cmr = new double[this.tamaño];
        res = "";
    }

    public String getRes() {
        return res;
    }

    public void setA(int[][] a) {
        this.a = a;
    }

    public void setB(int[] b) {
        this.b = b;
    }

    public double[] getCmr() {
        return cmr;
    }

    public int determinante(int x[][]) {
        int det = 0;
        int N = tamaño;
        switch (N) {
            case 2:
                det = ((x[0][0] * x[1][1]) - (x[1][0] * x[0][1]));
                break;
            case 3:	//Método de Gauss
                det = ((x[0][0]) * (x[1][1]) * (x[2][2]) + (x[1][0]) * (x[2][1]) * (x[0][2]) + (x[2][0]) * (x[0][1]) * (x[1][2])) - ((x[2][0]) * (x[1][1]) * (x[0][2]) + (x[1][0]) * (x[0][1]) * (x[2][2]) + (x[0][0]) * (x[2][1]) * (x[1][2]));
                break;
            default:	//Desarrollo a partir de los elementos de una fila/columna			
                for (int z = 0; z < x.length; z++) {
                    det += (x[z][0] * adj(x, z, 0));
                }
        }
        this.determinante = det;
        return det;
    }

    public int adj(int x[][], int i, int j) {
        int adjunto;
        int y[][] = new int[x.length - 1][x.length - 1];
        int m, n;
        for (int k = 0; k < y.length; k++) {
            if (k < i) {
                m = k;
            } else {
                m = k + 1;
            }
            for (int l = 0; l < y.length; l++) {
                if (l < j) {
                    n = l;
                } else {
                    n = l + 1;
                }
                y[k][l] = x[m][n];
            }
        }
        adjunto = (int) Math.pow(-1, i + j) * determinante(y);
        return adjunto;
    }

    //sustituye los valores de b en a en la posicion pos
    public int[][] sustituye(int a[][], int b[], int pos) {
        int c[][] = new int[a.length][a.length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                if (j == pos) {
                    c[i][j] = b[i];
                } else {
                    c[i][j] = a[i][j];
                }
            }
        }
        return c;
    }

//calcula cramer en base a su determinante     
    public void cramer() {
        double Rcramer[] = new double[b.length];
        int det = determinante(a);
        if (det == 0) {
            System.out.println("No tiene solucion con la regla de Cramer");
            cmr = Rcramer;
        }
        int detTemp;
        int c[][] = new int[a.length][a.length];
        for (int i = 0; i < a.length; i++) {
            c = sustituye(a, b, i);
            detTemp = determinante(c);
            Rcramer[i] = (float) detTemp / (float) det;
        }
        cmr = Rcramer;
        int j = 0;
        for (int i = 0; i < a.length; i++) {
            res = res + "El valor de B" + j + " es: " + String.format("%.3f", cmr[i]) + "\n";
            j++;
        }
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
