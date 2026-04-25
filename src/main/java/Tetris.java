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

        Griglia griglia = new Griglia();
        Pezzo pezzoAttivo = generaNuovoPezzo();

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

            EndDrawing();
        }

        CloseWindow();
    }

    // LEON MODIFICA QUESTO, PER LA QUEUE DEI PEZZI
    private static Pezzo generaNuovoPezzo() {
        Pezzo p = (Math.random() > 0.5) ? new Elle() : new Ipiss();
        p.x = Griglia.COLONNE / 2 - (p.forma[0].length / 2);
        p.y = 0;
        return p;
    }
}