package com.bamisu.log.gameserver.module.ingame.cmd;

import com.bamisu.log.gameserver.module.ingame.Node;
import com.bamisu.log.gameserver.module.ingame.PuzzleTable;
import com.bamisu.gamelib.entities.Params;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.js.IntArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 9:54 AM, 2/11/2020
 */
public class TableConverter {
    public static Node server2ClientNode(Node node) {
        Node tmp = node.cloneNode();
        tmp.row = node.col;
        tmp.col = Math.abs(PuzzleTable.ROW - 1 - node.row);
        return tmp;
    }

    public static SFSObject client2ServerSFSObject(ISFSObject node) {
        SFSObject sfsObject = new SFSObject();
        sfsObject.putInt(Params.X, Math.abs(PuzzleTable.ROW - 1 - node.getInt(Params.Y)));
        sfsObject.putInt(Params.Y, node.getInt(Params.X));
        return sfsObject;
    }

    public static List<Node> server2ClientListNode(List<Node> list) {
        List<Node> tmp = new ArrayList<>();
        for (Node node : list) {
            tmp.add(server2ClientNode(node));
        }
        return tmp;
    }

    public static SFSArray listComboToSFSArray(List<List<Node>> combos) {
        //pack table
        SFSArray tableSFSArray = new SFSArray();
        for (List<Node> combo : combos) {
            SFSArray comboSFSArray = new SFSArray();
            for (Node node : combo) {
                Node tmpNode = server2ClientNode(node);
                SFSObject sfsObject = new SFSObject();
                sfsObject.putInt(Params.X, tmpNode.row);
                sfsObject.putInt(Params.Y, tmpNode.col);
                if (tmpNode.diamond != null) {
                    sfsObject.putInt(Params.TYPE, tmpNode.diamond.getValue());
                } else {
                    sfsObject.putInt(Params.TYPE, -1);
                }

                comboSFSArray.addSFSObject(sfsObject);
            }
            tableSFSArray.addSFSArray(comboSFSArray);
        }
        return tableSFSArray;
    }

    public static SFSArray comboToSFSArray(List<Node> combos) {
        //pack table
        SFSArray comboSFSArray = new SFSArray();
        for (Node node : combos) {
            Node tmpNode = server2ClientNode(node);
            SFSObject sfsObject = new SFSObject();
            sfsObject.putInt(Params.X, tmpNode.row);
            sfsObject.putInt(Params.Y, tmpNode.col);
            if (tmpNode.diamond != null) {
                sfsObject.putInt(Params.TYPE, tmpNode.diamond.getValue());
            } else {
                sfsObject.putInt(Params.TYPE, -1);
            }

            comboSFSArray.addSFSObject(sfsObject);
        }
        return comboSFSArray;
    }


    public static SFSArray tableToSFSArray(PuzzleTable puzzleTable) {
        //pack table
        SFSArray table = new SFSArray();
        for (int rowIndex = puzzleTable.ROW - 1; rowIndex >= 0; rowIndex--) {
            SFSArray rowArray = new SFSArray();
            Node[] row = puzzleTable.matrix[rowIndex];
            for (Node node : row) {
                SFSObject sfsObject = new SFSObject();
                sfsObject.putInt(Params.TYPE, node.diamond.getValue());
                rowArray.addSFSObject(sfsObject);
            }
            table.addSFSArray(rowArray);
        }
        return table;
    }

    public static List<Integer> tableToIntArray(PuzzleTable puzzleTable) {
        List<Integer> list = new ArrayList<>();
        for (Node[] row : puzzleTable.matrix) {
            for (Node node : row) {
                list.add(node.diamond.getValue());
            }
        }
        return list;
    }
}
