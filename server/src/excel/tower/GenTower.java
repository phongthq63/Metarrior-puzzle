package excel.tower;

import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.campaign.config.entities.Station;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.log.gameserver.module.tower.config.TowerConfig;
import com.bamisu.log.gameserver.module.tower.config.entities.TowerVO;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 8:50 PM, 6/1/2020
 */
public class GenTower {
    public static void main(String[] args) {
        FileInputStream inputStream = null;
//        TowerConfig towerConfig = null;
//        try {
//            inputStream = new FileInputStream("D:\\project\\puzzle\\GameServer\\server\\conf\\tower\\TowerConfig.json");
//            towerConfig = Utils.fromJson(IOUtils.toString(inputStream), TowerConfig.class);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        for (int i = 0; i < 40; i++) {
//            for (MonsterOnTeam monsterOnTeam : towerConfig.list.get(i).enemy) {
//                monsterOnTeam.lethal = 0;
//            }
//        }
//
//        System.out.println(Utils.toJson(towerConfig));
//        if (true) return;


        List<TowerVO> list = new ArrayList<>();
        // Đọc một file XSL.
        try {
            inputStream = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\tower\\tower.xlsx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Đối tượng workbook cho file XSL.
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Lấy ra sheet đầu tiên từ workbook
        XSSFSheet sheet = workbook.getSheetAt(0);

        TowerVO towerVO = null;
        for (int i = 2; i <= 301; i++) {
            System.out.println(i);
            towerVO = new TowerVO();
            towerVO.floor = Integer.parseInt(sheet.getRow(i).getCell(1).getStringCellValue());
            towerVO.bossMode = Utils.rate(Integer.parseInt(sheet.getRow(i).getCell(11).getStringCellValue()));
            towerVO.bbg = sheet.getRow(i).getCell(13).toString();
            towerVO.condition = Arrays.asList(sheet.getRow(i).getCell(14).toString().split(","));

            //reward
            towerVO.reward = new ArrayList<>();
            if(Integer.valueOf(sheet.getRow(i).getCell(16).toString()) > 0){
                towerVO.reward.add(new ResourcePackage("MON1000", Integer.valueOf(sheet.getRow(i).getCell(16).toString())));
            }
            if(Integer.valueOf(sheet.getRow(i).getCell(17).toString()) > 0){
                towerVO.reward.add(new ResourcePackage("MON1001", Integer.valueOf(sheet.getRow(i).getCell(17).toString())));
            }

            if(Integer.valueOf(sheet.getRow(i).getCell(18).toString()) > 0) {
                towerVO.reward.add(new ResourcePackage("MON1003", Integer.valueOf(sheet.getRow(i).getCell(18).toString())));
            }

            if(Integer.valueOf(sheet.getRow(i).getCell(19).toString()) > 0) {
                towerVO.reward.add(new ResourcePackage("SPI1049", Integer.valueOf(sheet.getRow(i).getCell(19).toString())));
            }

            if(Integer.valueOf(sheet.getRow(i).getCell(20).toString()) > 0) {
                towerVO.reward.add(new ResourcePackage("SPI1050", Integer.valueOf(sheet.getRow(i).getCell(20).toString())));
            }

            //enemy
            towerVO.enemy = new ArrayList<>();

            ICharacterVO characterVO = null;
            for (int col = 6; col <= 10; col++) {
                MonsterOnTeam monsterOnTeam = new MonsterOnTeam();
                monsterOnTeam.star = Integer.parseInt(sheet.getRow(i).getCell(4).getStringCellValue());
                monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(3).getStringCellValue());
                monsterOnTeam.lethal = Integer.parseInt(sheet.getRow(i).getCell(5).getStringCellValue());

                String strCharacterID = sheet.getRow(i).getCell(col).toString().split(",")[0];

                //Creep ngẫu nhiên
                if (strCharacterID.equalsIgnoreCase("M")) {
                    characterVO = CharactersConfigManager.getInstance().getRandomCreepConfig();
                    monsterOnTeam.kingdom = Kingdom.DARK.getId();

                    if (sheet.getRow(i).getCell(col).toString().split(",").length == 1) {
                        monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                    } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 2) {
                        if (sheet.getRow(i).getCell(col).toString().split(",")[1].contains("E")) {
                            monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                        } else {
                            monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                            monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[1]);
                        }
                    } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 3) {
                        monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                        monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[2]);
                    }
                }

                //Miniboss ngẫu nhiên
                else if (strCharacterID.equalsIgnoreCase("MBS")) {
                    characterVO = CharactersConfigManager.getInstance().getRandomMbossConfig();
                    monsterOnTeam.kingdom = Kingdom.DARK.getId();

                    if (sheet.getRow(i).getCell(col).toString().split(",").length == 1) {
                        monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                    } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 2) {
                        if (sheet.getRow(i).getCell(col).toString().split(",")[1].contains("E")) {
                            monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                        } else {
                            monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                            monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[1]);
                        }
                    } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 3) {
                        monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                        monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[2]);
                    }
                }

                //tướng ngẫu nhiên
                else if (strCharacterID.equalsIgnoreCase("T")) {
                    characterVO = CharactersConfigManager.getInstance().getRandomHeroConfig(monsterOnTeam.star, null, null, null);
                    monsterOnTeam.kingdom = ((HeroVO) characterVO).kingdom;
                    monsterOnTeam.element = ((HeroVO) characterVO).element;
                    if (sheet.getRow(i).getCell(col).toString().split(",").length == 2) {
                        monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[1]);
                    }
                }

                //chỉ định 1 con tướng
                else if (strCharacterID.contains("T")) {
                    characterVO = CharactersConfigManager.getInstance().getHeroConfig(strCharacterID);
                    monsterOnTeam.kingdom = ((HeroVO) characterVO).kingdom;
                    monsterOnTeam.element = ((HeroVO) characterVO).element;
                    if (sheet.getRow(i).getCell(col).toString().split(",").length == 2) {
                        monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[1]);
                    }
                }

                //chỉ định 1 con miniboss
                else if (strCharacterID.contains("MBS")) {
                    characterVO = CharactersConfigManager.getInstance().getMbossConfig(strCharacterID);
                    monsterOnTeam.kingdom = Kingdom.DARK.getId();

                    if (sheet.getRow(i).getCell(col).toString().split(",").length == 1) {
                        monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                    } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 2) {
                        if (sheet.getRow(i).getCell(col).toString().split(",")[1].contains("E")) {
                            monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                        } else {
                            monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                            monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[1]);
                        }
                    } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 3) {
                        monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                        monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[2]);
                    }
                }

                //chỉ định 1 con Creep
                else if (strCharacterID.contains("M")) {
                    characterVO = CharactersConfigManager.getInstance().getCreepConfig(strCharacterID);
                    monsterOnTeam.kingdom = Kingdom.DARK.getId();

                    if (sheet.getRow(i).getCell(col).toString().split(",").length == 1) {
                        monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                    } else {
                        if (sheet.getRow(i).getCell(col).toString().split(",")[1].contains("E")) {
                            monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                        } else {
                            monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                            monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[1]);
                        }
                    }
                }

                if (strCharacterID.equalsIgnoreCase("x")) {
                    monsterOnTeam.id = "";
                } else {
                    monsterOnTeam.id = characterVO.getID();
                }
                towerVO.enemy.add(monsterOnTeam);
            }

            String terrainOption = sheet.getRow(i).getCell(12).toString();
            if (terrainOption.contains("#")) {
                towerVO.terrain = towerVO.enemy.get(Integer.parseInt(String.valueOf(terrainOption.charAt(1)))).element;
            } else
                towerVO.terrain = TeamUtils.genElement(terrainOption);
            if (towerVO.terrain == null) {
                System.out.println("");
            }

            list.add(towerVO);
        }

        System.out.println(Utils.toJson(list));
    }
}
