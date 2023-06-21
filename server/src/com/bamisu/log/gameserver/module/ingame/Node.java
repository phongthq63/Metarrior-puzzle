package com.bamisu.log.gameserver.module.ingame;

import com.bamisu.log.gameserver.module.ingame.entities.Diamond;
import com.bamisu.gamelib.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 4:03 PM, 10/28/2019
 */
public class Node {
    public int row;
    public int col;
    public Diamond diamond;

    public Node() {
    }

    public Node(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public Node setRow(int row) {
        this.row = row;
        return this;
    }

    public Node setCol(int col) {
        this.col = col;
        return this;
    }

    public Node setDiamond(Diamond diamond) {
        this.diamond = diamond;
        return this;
    }

    public boolean samePosition(Node node){
        return this.row == node.row && this.col == node.col;
    }

    public static void swap(Node fromNode, Node toNode) {
        Diamond tmpDiamond = fromNode.diamond;
        fromNode.diamond = toNode.diamond;
        toNode.diamond = tmpDiamond;
    }

    public void setRandomDiamond() {
//        if (Utils.rate(2)) {
//            if (Utils.rate(50)) {
//                diamond = Diamond.getRandomBomb();
//            } else {
//                diamond = Diamond.getRandomFlash();
//            }
//        } else {
//            diamond = Diamond.getRandomNomal();
//        }

        diamond = Diamond.getRandomNomal();
    }

    @Override
    public String toString() {
        if (diamond == null) {
            return ".";
        }
        return diamond.toString() + "." + diamond.getValue();
    }

//    public boolean sameColor(Node node) {
//        return this.diamond == node.diamond;
//    }

    public boolean equalColor(Node node) {
        return diamond.equalColor(node.diamond);
    }

    public boolean sameColor(Node node) {
        return diamond.sameColor(node.diamond);
    }

    public void destroy() {
        diamond = null;
    }

    public List<Node> destroy(PuzzleTable puzzleTable, boolean fromNomal) {
        List<Node> listNode = new ArrayList<>();
        if (diamond != null) {  //chưa bị phá hủy
            //kim cương thường
            if (diamond == Diamond.RED || diamond == Diamond.GREEN || diamond == Diamond.BLUE || diamond == Diamond.YELLOW || diamond == Diamond.PURPLE) {
                diamond = null;
                if (fromNomal) {
                    return new ArrayList<>();
                } else {
                    return Arrays.asList(this);
                }

            }

            //bomb
            if (diamond == Diamond.B_RED || diamond == Diamond.B_GREEN || diamond == Diamond.B_BLUE || diamond == Diamond.B_YELLOW || diamond == Diamond.B_PURPLE) {
                diamond = null;
                listNode.add(this);

                if (row - 1 >= 0 && row - 1 < PuzzleTable.ROW) {
                    listNode.addAll(puzzleTable.getNode(row - 1, col).destroy(puzzleTable, false));
                }
                if (row + 1 >= 0 && row + 1 < PuzzleTable.ROW) {
                    listNode.addAll(puzzleTable.getNode(row + 1, col).destroy(puzzleTable, false));
                }
                if (col - 1 >= 0 && col - 1 < PuzzleTable.COL) {
                    listNode.addAll(puzzleTable.getNode(row, col - 1).destroy(puzzleTable, false));
                }
                if (col + 1 >= 0 && col + 1 < PuzzleTable.COL) {
                    listNode.addAll(puzzleTable.getNode(row, col + 1).destroy(puzzleTable, false));
                }
            }

            //flash
            if (diamond == Diamond.F_RED || diamond == Diamond.F_GREEN || diamond == Diamond.F_BLUE || diamond == Diamond.F_YELLOW || diamond == Diamond.F_PURPLE) {
                Diamond cacheDiamon = diamond;
                diamond = null;
                listNode.add(this);

                for (Node[] rowNode : puzzleTable.matrix) {
                    for (Node node : rowNode) {
                        if (node.diamond != null) {
                            if (!node.equals(this) && node.diamond.sameColor(cacheDiamon)) {
                                listNode.addAll(node.destroy(puzzleTable, false));
                            }
                        } else {
                            listNode.add(node);
                        }
                    }
                }
            }
        } else { //đã bị phá hủy
            if (fromNomal) {
                return new ArrayList<>();
            } else {
                return Arrays.asList(this);
            }
        }
        return listNode;
    }

    public Node cloneNode() {
        Node node = new Node(row, col);
        node.diamond = diamond;
        return node;
    }
}
