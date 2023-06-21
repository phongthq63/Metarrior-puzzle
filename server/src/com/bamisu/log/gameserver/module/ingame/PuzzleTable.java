package com.bamisu.log.gameserver.module.ingame;

import com.bamisu.gamelib.config.CampaignSpecialPuzzleConfig;
import com.bamisu.gamelib.config.TowerSpecialPuzzleConfig;
import com.bamisu.gamelib.utils.IngameUtils;
import com.bamisu.log.gameserver.module.ingame.entities.BaseTable;
import com.bamisu.log.gameserver.module.ingame.entities.Diamond;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.CampaignFightManager;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.EFightingFunction;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.PvMManager;
import com.bamisu.log.gameserver.module.ingame.entities.fighting.TowerFightingManager;
import com.bamisu.log.gameserver.module.ingame.entities.skill.Combo;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.SFSArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Popeye on 3:57 PM, 10/28/2019
 */
public class PuzzleTable extends BaseTable {
    public PuzzleTable() {
//        getMatrixWithNoCombo();
    }

    public PuzzleTable(Node[][] maxtrix) {
        this.matrix = maxtrix;
    }


    @Override
    public void move(int fromR, int fromC, int toR, int toC) {
        Node fromNode = getNode(fromR, fromC);
        Node toNode = getNode(toR, toC);

        Node.swap(fromNode, toNode);

    }

    public PuzzleTable clone(){
        Node[][] tmpMatrix = new Node[PuzzleTable.ROW][PuzzleTable.ROW];
        for(int i = 0; i < PuzzleTable.ROW; i++){
            for(int j = 0; j < PuzzleTable.COL; j++){
                tmpMatrix[i][j] = matrix[i][j].cloneNode();
            }
        }

        return new PuzzleTable(tmpMatrix);
    }

    @Override
    public void touch(int row, int col) {
    }

    @Override
    public List<List<Node>> checkWin() {
        List<List<Node>> tmpListAll = new ArrayList<>();
        List<Node> tmpList = new ArrayList<>();
        List<List<Node>> rowCombo = new ArrayList<>();
        List<List<Node>> colCombo = new ArrayList<>();

        //check all row
        for (int r = 0; r < ROW; r++) {
            for (int c = 0; c < COL; c++) {
                if (c == 0) {
                    tmpList.add(getNode(r, c));
                    continue;
                } else {
                    if (getNode(r, c).sameColor(getNode(r, c - 1))) {
                        tmpList.add(getNode(r, c));
                    } else {
                        if (tmpList.size() >= 3) {
                            tmpListAll.add(tmpList);
                        }
                        tmpList = new ArrayList<>();
                        tmpList.add(getNode(r, c));
                    }

                    if (c == COL - 1) {
                        if (tmpList.size() >= 3) {
                            tmpListAll.add(tmpList);
                        }
                        tmpList = new ArrayList<>();
                    }
                }
            }
            rowCombo.addAll(tmpListAll);
            tmpListAll.clear();
        }

        tmpListAll = new ArrayList<>();
        tmpList = new ArrayList<>();
        //check all col
        for (int c = 0; c < COL; c++) {
            for (int r = 0; r < ROW; r++) {
                if (r == 0) {
                    tmpList.add(getNode(r, c));
                    continue;
                } else {
                    if (getNode(r, c).sameColor(getNode(r - 1, c))) {
                        tmpList.add(getNode(r, c));
                    } else {
                        if (tmpList.size() >= 3) {
                            tmpListAll.add(tmpList);
                        }
                        tmpList = new ArrayList<>();
                        tmpList.add(getNode(r, c));
                    }

                    if (r == ROW - 1) {
                        if (tmpList.size() >= 3) {
                            tmpListAll.add(tmpList);
                        }
                        tmpList = new ArrayList<>();
                    }
                }
            }
            colCombo.addAll(tmpListAll);
            tmpListAll.clear();
        }

        //check full
        List<List<Node>> allCombo = new ArrayList<>();
        if (rowCombo.isEmpty() || colCombo.isEmpty()) {
            allCombo.addAll(rowCombo);
            allCombo.addAll(colCombo);
        } else {
            Map<List<Node>, Boolean> flagCol = new HashMap<>();
            for (List<Node> rowCom : rowCombo) {
                for (List<Node> colCom : colCombo) {
                    Map<Node, Boolean> mapCheck = new HashMap<>();
                    for (Node node : rowCom) {
                        mapCheck.put(node, true);
                    }

                    boolean flag = false;
                    for (Node node : colCom) {
                        if (!mapCheck.containsKey(node)) {
                            mapCheck.put(node, true);
                        } else {
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        //ghép rowCom va colCom
                        merge(rowCom, colCom);
                        flagCol.put(colCom, true);
                    } else {
                        if (!flagCol.containsKey(colCom)) {
                            flagCol.put(colCom, false);
                        }
                    }
                }
            }

            //////
            //Hết col sang row
            for (List<Node> colCom : colCombo) {
                if (!flagCol.get(colCom)) {
                    allCombo.add(colCom);
                }
            }

            Map<List<Node>, Boolean> flagRow = new HashMap<>();
            for (List<Node> rowCom : rowCombo) {
                if (!flagRow.containsKey(rowCom) || !flagRow.get(rowCom)) {
                    for (List<Node> tmprowCom : rowCombo) {
                        if (rowCom != tmprowCom) {
                            if (!flagRow.containsKey(tmprowCom) || !flagRow.get(tmprowCom)) {
                                Map<Node, Boolean> mapCheck = new HashMap<>();
                                for (Node node : rowCom) {
                                    mapCheck.put(node, true);
                                }

                                boolean flag = false;
                                for (Node node : tmprowCom) {
                                    if (!mapCheck.containsKey(node)) {
                                        mapCheck.put(node, true);
                                    } else {
                                        flag = true;
                                        break;
                                    }
                                }

                                if (flag) {
                                    //ghép rowCom va colCom
                                    merge(rowCom, tmprowCom);
                                    flagRow.put(tmprowCom, true);
                                } else {
                                    if (!flagRow.containsKey(tmprowCom)) {
                                        flagRow.put(tmprowCom, false);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (List<Node> colCom : rowCombo) {
                if (!flagRow.containsKey(colCom) || !flagRow.get(colCom)) {
                    allCombo.add(colCom);
                }
            }
        }

//        long befo = System.currentTimeMillis();
        return allCombo;
    }

    /**
     * phá hủy những ô bị ăn
     *
     * @param listCombo
     */
//    @Override
//    public void destroyCombo(List<List<Node>> listCombo) {
//        //luu tam cac combo cua kim cuong dac biet
//        List<List<Node>> tmpListCombo = new ArrayList<>();
//        for (List<Node> combo : listCombo) {
//            for (Node node : combo) {
//                List<Node> destroyNode = node.destroy(this, true);
//                if (!destroyNode.isEmpty()) {
//                    tmpListCombo.add(destroyNode);
//                }
//            }
//        }
//
//        if (!tmpListCombo.isEmpty()) {
//            listCombo.addAll(tmpListCombo);
//        }
////        //System.out.println(toString());
//    }

    @Override
    public void destroyCombo(List<List<Node>> listCombo) {
        //luu tam cac combo cua kim cuong dac biet
        List<Node> tmpListCombo;
        for (List<Node> combo : listCombo) {
            tmpListCombo = new ArrayList<>();
            for (Node node : combo) {
                List<Node> destroyNode = node.destroy(this, true);
                if (!destroyNode.isEmpty()) {
                    tmpListCombo.addAll(destroyNode);
                }
            }


            if(!tmpListCombo.isEmpty()){
                for(Node node : tmpListCombo){
                    if(!combo.contains(node)){
                        combo.add(node);
                    }
                }
            }
        }
//        //System.out.println(toString());
    }

    @Override
    public void destroyAsTouch(int x, int y, List<List<Node>> listCombo) {
        List<Node> combo = new ArrayList<>();
        //luu tam cac combo cua kim cuong dac biet
        List<Node> tmpListCombo = new ArrayList<>();
        List<Node> destroyNode = matrix[x][y].destroy(this, false);
        if (!destroyNode.isEmpty()) {
            tmpListCombo.addAll(destroyNode);
        }

        if(!tmpListCombo.isEmpty()){
            for(Node node : tmpListCombo){
                if(!combo.contains(node)){
                    combo.add(node);
                }
            }
        }

        listCombo.add(combo);
    }

    @Override
    protected void destroyAll() {
        for (Node[] combo : matrix) {
            for (Node node : combo) {
                node.destroy();
            }
        }
    }

    /**
     * sinh ra kim cương mới lấp vào những ô bị phá hủy
     *
     * @param listCombo
     */
    @Override
    public List<List<Node>> fill(Map<String, Node> assignMap, List<List<Node>> listCombo, List<Node> specialFill) {
        //mảng trả về cho client
        List<List<Node>> returnCombo = new ArrayList<>();
        List<Node> tmpCombo;

        //fill special diamond
        for (Node node : assignMap.values()){
            getNode(node.row, node.col).setDiamond(node.diamond);
            specialFill.add(getNode(node.row, node.col).cloneNode());
        }

        //tạo 1 table tạm, rỗng
        PuzzleTable tmpTable = new PuzzleTable();
        tmpTable.destroyAll();

        //tạo ra kim cương để lấp đầy. lưu vào table tạm
        for (List<Node> combo : listCombo) {
            tmpCombo = new ArrayList<>();
            for (Node node : combo) {
                if (tmpTable.getNode(node.row, node.col).diamond == null) {
                    if(assignMap.containsKey(node.row + "," + node.col)){
//                        tmpTable.getNode(node.row, node.col).setDiamond(assignMap.get(node.row + "," + node.col));
                        tmpCombo.add(getNode(node.row, node.col).cloneNode()); //lấy từ matrix gốc đã sinh ra diamond đặc biệt trước đó để trả về trong fill
                    }else {
                        tmpTable.getNode(node.row, node.col).setRandomDiamond();
                        tmpCombo.add(tmpTable.getNode(node.row, node.col).cloneNode());
                    }
                }
            }
            returnCombo.add(tmpCombo);
        }
//        //System.out.println(tmpTable.toString());

        //lấp đầy vào table gốc
        List<Node> tmpCol;
        for (int j = 0; j < COL; j++) {
            tmpCol = new ArrayList<>();

            for (int i = 0; i < ROW; i++) {
                if (tmpTable.getNode(i, j).diamond != null) {
                    Node node = new Node();
                    node.diamond = tmpTable.getNode(i, j).diamond;
                    tmpCol.add(node);
                }
            }

            for (int i = 0; i < ROW; i++) {
                if (this.getNode(i, j).diamond != null) {
                    Node node = new Node();
                    node.diamond = this.getNode(i, j).diamond;
                    tmpCol.add(node);
                }
            }

            for (int i = 0; i < ROW; i++) {
                try {
                    this.getNode(i, j).diamond = tmpCol.get(i).diamond;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

//        //System.out.println(toString());

//        for (List<Node> list : returnCombo) {
//            for (Node node : list) {
//                System.out.print("(" + node.row + "," + node.col + ")");
//            }
//            //System.out.println();
//        }

        return returnCombo;
    }

    private void merge(List<Node> rowCom, List<Node> colCombo) {
        for (Node node : colCombo) {
            if (!rowCom.contains(node)) {
                rowCom.add(node);
            }
        }
    }

    public static void main(String[] args) {

    }

    private String toConfigString() {
        String s = "";
        for(Node[] row : matrix){
            for(Node node : row){
                s += node.diamond.getValue() + "-";
            }
        }
        return s;
    }

    public Node getNode(Node node) {
        return getNode(node.row, node.col);
    }

    public void genMatrixWithNoCombo() {
        String s = IngameUtils.tableArray.getUtfString(Utils.randomInRange(0, IngameUtils.tableArray.size() - 1));
        String[] strDiamon = s.split("-");
        int index = 0;
        for (int i = 0; i < BaseTable.ROW; i++) {
            for (int j = 0; j < BaseTable.COL; j++) {
                matrix[i][j] = new Node(i, j);
                matrix[i][j].setDiamond(Diamond.valueOf(Integer.valueOf(strDiamon[index])) );
                index++;
            }
        }
    }

    public void fillSpecialPuzzle(PvMManager pvMManager) {
        //puzzle 100
        String strLockPuzzleList = null;
        if(pvMManager.function == EFightingFunction.CAMPAIGN){
            String stageName = ((CampaignFightManager) pvMManager).stationConfig.name;
            if(CampaignSpecialPuzzleConfig.getInstance().lockMap.containsKey(stageName)){
                strLockPuzzleList = CampaignSpecialPuzzleConfig.getInstance().lockMap.get(stageName);
            }
        }

        if(pvMManager.function == EFightingFunction.TOWER){
            String stageName = String.valueOf(((TowerFightingManager) pvMManager).towerInfo.floor);
            if(TowerSpecialPuzzleConfig.getInstance().lockMap.containsKey(stageName)){
                strLockPuzzleList = TowerSpecialPuzzleConfig.getInstance().lockMap.get(stageName);
            }
        }

        if(strLockPuzzleList != null){
            String[] lockPuzzleList = strLockPuzzleList.split("#");
            for(String strNode : lockPuzzleList){
                try {
                    if(!strNode.isEmpty()){
                        matrix[Integer.parseInt(strNode.split(",")[0])][Integer.parseInt(strNode.split(",")[1])].diamond = Diamond.BLACK_HOLD;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
}
