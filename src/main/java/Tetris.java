import java.util.Random;
import java.util.Arrays;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Tetris {
    public static final int CELL_SIZE = 30;

    private static final int[] PUNTI_PER_RIGHE = {0, 100, 300, 500, 800};

    public static void main(String[] args) {
        int screenWidth = Griglia.COLONNE * CELL_SIZE;
        int screenHeight = Griglia.RIGHE * CELL_SIZE;

        InitWindow(screenWidth, screenHeight, "Javetris");
        SetTargetFPS(60);

        Pezzo pezzoVuoto = new voidPiece();
        Griglia griglia = new Griglia();
        Pezzo pezzoAttivo = generaNuovoPezzo();
        Pezzo pezzoTenuto = pezzoVuoto;

        float gravityTimer = 0.0f;
        float gravityInterval = 0.5f;

        int punteggio = 0;
        int livello = 1;
        int righeTotali = 0;

        while (!WindowShouldClose()) {

            if (IsKeyPressed(KEY_A) || IsKeyPressed(KEY_LEFT)) {
                if (!griglia.collisione(pezzoAttivo, pezzoAttivo.x - 1, pezzoAttivo.y)) pezzoAttivo.x--;
            }
            if (IsKeyPressed(KEY_D) || IsKeyPressed(KEY_RIGHT)) {
                if (!griglia.collisione(pezzoAttivo, pezzoAttivo.x + 1, pezzoAttivo.y)) pezzoAttivo.x++;
            }
            if (IsKeyPressed(KEY_S) || IsKeyPressed(KEY_DOWN)) {
                if (!griglia.collisione(pezzoAttivo, pezzoAttivo.x, pezzoAttivo.y + 1)) pezzoAttivo.y++;
            }
            if (IsKeyPressed(KEY_W) || IsKeyPressed(KEY_UP)) {
                pezzoAttivo.ruota();
                if (griglia.collisione(pezzoAttivo, pezzoAttivo.x, pezzoAttivo.y)) {
                    pezzoAttivo.ruota();
                    pezzoAttivo.ruota();
                    pezzoAttivo.ruota();
                }
            }

            if (IsKeyPressed(KEY_LEFT_CONTROL)){
                if ( pezzoTenuto == pezzoVuoto ){
                    pezzoTenuto = pezzoAttivo;
                    pezzoAttivo = generaNuovoPezzo();
                }
                Pezzo pTemp =  pezzoAttivo;
                pezzoAttivo = pezzoTenuto;
                pezzoTenuto = pTemp;
                pezzoAttivo.x = Griglia.COLONNE / 2 - (p.forma[0].length / 2);
                pezzoAttivo.y = 0;
            }

            gravityTimer += GetFrameTime();
            if (gravityTimer >= gravityInterval) {
                if (!griglia.collisione(pezzoAttivo, pezzoAttivo.x, pezzoAttivo.y + 1)) {
                    pezzoAttivo.y++;
                } else {
                    griglia.fissaPezzo(pezzoAttivo);

                    int righeEliminate = griglia.controllaRighe();
                    if (righeEliminate > 0) {
                        punteggio += PUNTI_PER_RIGHE[righeEliminate] * livello;
                        righeTotali += righeEliminate;

                        livello = (righeTotali / 10) + 1;
                        gravityInterval = Math.max(0.05f, 0.5f - (livello - 1) * 0.04f);
                    }

                    pezzoAttivo = generaNuovoPezzo();

                    if (griglia.collisione(pezzoAttivo, pezzoAttivo.x, pezzoAttivo.y)) {
                        System.out.println("GAME OVER! Punteggio finale: " + punteggio);
                        break;
                    }
                }
                gravityTimer = 0.0f;
            }

            BeginDrawing();
            ClearBackground(RAYWHITE);

            // Griglia
            for (int i = 0; i < screenWidth; i += CELL_SIZE) {
                DrawLine(i, 0, i, screenHeight, LIGHTGRAY);
            }
            for (int i = 0; i < screenHeight; i += CELL_SIZE) {
                DrawLine(0, i, screenWidth, i, LIGHTGRAY);
            }

            // Celle fissate
            for (int r = 0; r < Griglia.RIGHE; r++) {
                for (int c = 0; c < Griglia.COLONNE; c++) {
                    int val = griglia.getValore(r, c);
                    if (val != 0) {
                        DrawRectangle(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE, DARKGRAY);
                        DrawRectangleLines(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE, BLACK);
                    }
                }
            }

            // Pezzo attivo
            for (int r = 0; r < pezzoAttivo.forma.length; r++) {
                for (int c = 0; c < pezzoAttivo.forma[r].length; c++) {
                    if (pezzoAttivo.forma[r][c] != 0) {
                        int drawX = (pezzoAttivo.x + c) * CELL_SIZE;
                        int drawY = (pezzoAttivo.y + r) * CELL_SIZE;
                        DrawRectangle(drawX, drawY, CELL_SIZE, CELL_SIZE, RED);
                        DrawRectangleLines(drawX, drawY, CELL_SIZE, CELL_SIZE, MAROON);
                    }
                }
            }

            DrawText("SCORE: " + punteggio, 5, 5, 18, DARKBLUE);
            DrawText("LVL: " + livello,     5, 25, 18, DARKBLUE);
            DrawText("HOLD: " + pezzoTenuto, 5, 45, 18, DARKBLUE);

            EndDrawing();
        }

        CloseWindow();
    }
    private static void shuffleArray(char[] array)
    {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            if (index != i)
            {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }

    public static char[] PieceBag(){
        char[] Bag = {'I', 'J', 'L', 'T', 'S', 'Z', 'O'};
        shuffleArray(Bag);
        return Bag;
    }
    public static char[] removeFirst(char[] arr) {
        char[] arr2 = {};
        if (arr.length == 1) {
            return arr2;
        }

        arr2 = new char[arr.length - 1];

        // Copy the elements except the index
        // from original array to the other array
        for (int i = 1, k = 0; i < arr.length-1; i++) {
            arr2[k] = arr[i];
            k++;
        }
        return arr2;
    }
    public static char[] CurrentBag = {};
    public static Pezzo p = new voidPiece();

    private static Pezzo generaNuovoPezzo() {

        if ( CurrentBag == null || CurrentBag.length == 0 ) { CurrentBag = PieceBag();}

        switch (CurrentBag[0]) {
            case 'I': {
                p = new PieceI();
                break;
            }
            case 'J': {
                p = new PieceJ();
                break;
            }
            case 'L': {
                p = new PieceL();
                break;
            }
            case 'T': {
                p = new PieceT();
                break;
            }
            case 'S': {
                p = new PieceS();
                break;
            }
            case 'Z': {
                p = new PieceZ();
                break;
            }
            case 'O': {
                p = new PieceO();
                break;
            }
        }
        CurrentBag = removeFirst(CurrentBag);
        p.x = Griglia.COLONNE / 2 - (p.forma[0].length / 2);
        p.y = 0;
        return p;
    }
}