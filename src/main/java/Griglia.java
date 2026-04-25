public class Griglia {
    public static final int RIGHE = 20;
    public static final int COLONNE = 10;
    private int[][] matrice = new int[RIGHE][COLONNE];

    public int getValore(int r, int c) {
        return matrice[r][c];
    }

    public boolean collisione(Pezzo p, int nx, int ny) {
        for (int r = 0; r < p.forma.length; r++) {
            for (int c = 0; c < p.forma[r].length; c++) {
                if (p.forma[r][c] != 0) {
                    int tx = nx + c;
                    int ty = ny + r;
                    if (tx < 0 || tx >= COLONNE || ty >= RIGHE) return true;
                    if (ty >= 0 && matrice[ty][tx] != 0) return true;
                }
            }
        }
        return false;
    }

    public void fissaPezzo(Pezzo p) {
        for (int r = 0; r < p.forma.length; r++) {
            for (int c = 0; c < p.forma[r].length; c++) {
                if (p.forma[r][c] != 0 && (p.y + r) >= 0 && (p.y + r) < RIGHE) {
                    matrice[p.y + r][p.x + c] = p.forma[r][c];
                }
            }
        }
    }

    public int controllaRighe() {
        int righeEliminate = 0;
        for (int r = RIGHE - 1; r >= 0; r--) {
            boolean piena = true;
            for (int c = 0; c < COLONNE; c++) {
                if (matrice[r][c] == 0) {
                    piena = false;
                    break;
                }
            }
            if (piena) {
                for (int i = r; i > 0; i--) {
                    matrice[i] = matrice[i - 1].clone();
                }
                matrice[0] = new int[COLONNE];
                r++;
                righeEliminate++;
            }
        }
        return righeEliminate;
    }
}