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