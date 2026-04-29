import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

import static com.raylib.Raylib.*;
import static com.raylib.Colors.*;

public class Tetris {
    public static final int CELL_SIZE = 30;
    private static final int[] PUNTI_PER_RIGHE = {0, 100, 300, 500, 800};

    public static LinkedList<Pezzo> pieceQueue = new LinkedList<>();

    public static void main(String[] args) {
        int playfieldWidth = Griglia.COLONNE * CELL_SIZE;
        int screenWidth = playfieldWidth + (6 * CELL_SIZE);
        int screenHeight = Griglia.RIGHE * CELL_SIZE;

        InitWindow(screenWidth, screenHeight, "Javetris");
        SetTargetFPS(60);

        boolean canSwap = true;
        Pezzo pezzoVuoto = new voidPiece();
        Griglia griglia = new Griglia();

        fillQueue();
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
           //MEccanica dell'holdare
            if (IsKeyPressed(KEY_LEFT_CONTROL) && canSwap) {
                canSwap = false;
                if (pezzoTenuto instanceof voidPiece) {
                    pezzoTenuto = pezzoAttivo;
                    pezzoAttivo = generaNuovoPezzo();
                } else {
                    Pezzo pTemp = pezzoAttivo;
                    pezzoAttivo = pezzoTenuto;
                    pezzoTenuto = pTemp;

                    pezzoAttivo.x = Griglia.COLONNE / 2 - (pezzoAttivo.forma[0].length / 2);
                    pezzoAttivo.y = 0;
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
                    canSwap = true;

                    if (griglia.collisione(pezzoAttivo, pezzoAttivo.x, pezzoAttivo.y)) {
                        System.out.println("GAME OVER! Punteggio finale: " + punteggio);
                        break;
                    }
                }
                gravityTimer = 0.0f;
            }
        //Griglia, UI  e mazzate varie
            BeginDrawing();
            ClearBackground(DARKGRAY);

            DrawRectangle(0, 0, playfieldWidth, screenHeight, BLACK);

            for (int i = 0; i <= playfieldWidth; i += CELL_SIZE) {
                DrawLine(i, 0, i, screenHeight, Fade(DARKGRAY, 0.5f));
            }
            for (int i = 0; i <= screenHeight; i += CELL_SIZE) {
                DrawLine(0, i, playfieldWidth, i, Fade(DARKGRAY, 0.5f));
            }

            for (int r = 0; r < Griglia.RIGHE; r++) {
                for (int c = 0; c < Griglia.COLONNE; c++) {
                    int val = griglia.getValore(r, c);
                    if (val != 0) {
                        drawRetroBlock(c * CELL_SIZE, r * CELL_SIZE, getPieceColor(val), CELL_SIZE);
                    }
                }
            }

            for (int r = 0; r < pezzoAttivo.forma.length; r++) {
                for (int c = 0; c < pezzoAttivo.forma[r].length; c++) {
                    int val = pezzoAttivo.forma[r][c];
                    if (val != 0) {
                        drawRetroBlock((pezzoAttivo.x + c) * CELL_SIZE, (pezzoAttivo.y + r) * CELL_SIZE, getPieceColor(val), CELL_SIZE);
                    }
                }
            }

            DrawRectangle(playfieldWidth, 0, 4, screenHeight, LIGHTGRAY);

            int uiX = playfieldWidth + 20;

            DrawText("SCORE", uiX, 20, 20, LIGHTGRAY);
            DrawText(String.valueOf(punteggio), uiX, 45, 20, GREEN);

            DrawText("LEVEL", uiX, 85, 20, LIGHTGRAY);
            DrawText(String.valueOf(livello), uiX, 110, 20, GREEN);

            DrawText("HOLD", uiX, 160, 20, LIGHTGRAY);
            drawPieceUI(pezzoTenuto, uiX, 190);

            DrawText("NEXT", uiX, 300, 20, LIGHTGRAY);
            for (int i = 0; i < 3; i++) {
                drawPieceUI(pieceQueue.get(i), uiX, 330 + (i * 80));
            }

            EndDrawing();
        }

        CloseWindow();
    }

    private static void fillQueue() {
        List<Pezzo> bag = Arrays.asList(
                new PieceI(), new PieceJ(), new PieceL(),
                new PieceT(), new PieceS(), new PieceZ(), new PieceO()
        );
        Collections.shuffle(bag);
        pieceQueue.addAll(bag);
    }

    private static Pezzo generaNuovoPezzo() {
        if (pieceQueue.size() <= 4) {
            fillQueue();
        }
        Pezzo p = pieceQueue.remove(0);
        p.x = Griglia.COLONNE / 2 - (p.forma[0].length / 2);
        p.y = 0;
        return p;
    }

    private static void drawRetroBlock(int x, int y, Color baseColor, int size) {
        DrawRectangle(x, y, size, size, baseColor);

        DrawLine(x, y, x + size, y, Fade(WHITE, 0.4f));
        DrawLine(x, y, x, y + size, Fade(WHITE, 0.4f));

        DrawLine(x, y + size, x + size, y + size, Fade(BLACK, 0.4f));
        DrawLine(x + size, y, x + size, y + size, Fade(BLACK, 0.4f));
    }

    private static void drawPieceUI(Pezzo p, int startX, int startY) {
        if (p == null || p instanceof voidPiece) return;
        int miniSize = 20;

        for (int r = 0; r < p.forma.length; r++) {
            for (int c = 0; c < p.forma[r].length; c++) {
                int val = p.forma[r][c];
                if (val != 0) {
                    drawRetroBlock(startX + c * miniSize, startY + r * miniSize, getPieceColor(val), miniSize);
                }
            }
        }
    }

    private static Color getPieceColor(int type) {
        switch (type) {
            case 1: return SKYBLUE;
            case 2: return ORANGE;
            case 3: return GREEN;
            case 4: return RED;
            case 5: return PURPLE;
            case 6: return YELLOW;
            case 7: return BLUE;
            default: return DARKGRAY;
        }
    }
}