public abstract class Pezzo {
    public int[][] forma;
    public int x, y;

    public void ruota() {
        int n = forma.length;

        for (int i = 0; i < n / 2; i++) {
            for (int j = i; j < n - i - 1; j++) {
                int temp = forma[i][j];
                forma[i][j] = forma[n - 1 - j][i];
                forma[n - 1 - j][i] = forma[n - 1 - i][n - 1 - j];
                forma[n - 1 - i][n - 1 - j] = forma[j][n - 1 - i];
                forma[j][n - 1 - i] = temp;
            }
        }
    }

}
class PieceI extends Pezzo {
    public PieceI() {
        this.forma = new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
    }
}

class PieceL extends Pezzo {
    public PieceL() {
        this.forma = new int[][]{
                {0, 0, 2},
                {2, 2, 2},
                {0, 0, 0}
        };
    }
}


class PieceS extends Pezzo {
    public PieceS() {
        this.forma = new int[][]{
                {0, 0, 0},
                {0, 3, 3},
                {3, 3, 0},
        };
    }
}

class PieceZ extends Pezzo { //On a second thought, this could be mirrored at runtime to not create an extra
    //class
    public PieceZ(){
        this.forma = new int[][]{
                {0, 0, 0},
                {4, 4, 0},
                {0, 4, 4},
        };
    }
}

class PieceT extends Pezzo{
    public PieceT(){
        this.forma = new int[][]{
                {0, 0, 0},
                {0, 5, 0},
                {5, 5, 5},
        };
    }
}

class PieceO extends Pezzo{
    public PieceO(){
        this.forma = new int[][]{
                {6, 6},
                {6, 6},
        };
    }
}