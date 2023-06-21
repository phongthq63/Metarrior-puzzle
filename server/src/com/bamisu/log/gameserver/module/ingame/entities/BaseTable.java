package com.bamisu.log.gameserver.module.ingame.entities;

import com.bamisu.log.gameserver.module.ingame.Node;
import com.bamisu.log.gameserver.module.ingame.UtilsIngame;
import com.bamisu.gamelib.utils.Utils;

import java.util.List;
import java.util.Map;

/**
 * Create by Popeye on 4:33 PM, 10/28/2019
 */
public abstract class BaseTable {
    public static final int ROW = 7;
    public static final int COL = 6;

    public Node[][] matrix = new Node[ROW][COL];

    public void renewMatrix() {
        matrix = new Node[ROW][COL];

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                matrix[i][j] = new Node(i, j);
                matrix[i][j].setRandomDiamond();
            }
        }
    }

    public void renewMatrixWithNoCombo() {
        List<List<Node>> combos;
        do {
            renewMatrix();
            combos = checkWin();
        } while (!combos.isEmpty());
    }

    public abstract List<List<Node>> checkWin();

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                s += matrix[i][j].toString() + "\t";
            }
            s += "\n";
        }

        return s;
    }

    public abstract void move(int fromR, int fromC, int toR, int toC);

    public abstract void touch(int r, int c);

    public abstract List<List<Node>> fill(Map<String, Node> assignMap, List<List<Node>> listCombo, List<Node> specialFill);

    public abstract void destroyCombo(List<List<Node>> listCombo);

    public abstract void destroyAsTouch(int x, int y, List<List<Node>> listCombo);

    public Node getNode(int fromR, int fromC) {
        return matrix[fromR][fromC];
    }

    protected abstract void destroyAll();


}
