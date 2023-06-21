package excel.campain;

import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.adventure.entities.LootVO;
import com.bamisu.log.gameserver.module.campaign.config.MainCampaignConfig;
import com.bamisu.log.gameserver.module.campaign.config.entities.Area;
import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.campaign.config.entities.StarRewardVO;
import com.bamisu.log.gameserver.module.campaign.config.entities.Station;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepVO;
import com.bamisu.log.gameserver.module.characters.element.Element;
import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.kingdom.Kingdom;
import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Create by Popeye on 5:47 PM, 6/8/2020
 */
public class GenConfigCampain {
    public static void main(String[] args) {
        MainCampaignConfig mainCampaignConfig = new MainCampaignConfig();
//        MainCampaignConfig mainCampaignConfig = null;
        FileInputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream("D:\\project\\puzzle\\GameServer\\server\\conf\\campaign\\main.json");
//            mainCampaignConfig = Utils.fromJson(IOUtils.toString(inputStream), MainCampaignConfig.class);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        for(int i = 0; i<3; i++){
//            for(Station station : mainCampaignConfig.area.get(i).station){
//                for(MonsterOnTeam monsterOnTeam : station.enemy){
//                    monsterOnTeam.lethal = 0;
//                }
//            }
//        }
//
//        System.out.println(Utils.toJson(mainCampaignConfig.area));
//        if(true) return;

        // Đọc một file XSL.
//        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("D:\\project\\mewa-server\\server\\src\\excel\\campain\\main_mewa.xlsx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        int cacheArea = -1;
        Area area = null;
        for (int i = 3; i < 501; i++) {
            try {
                Station station = new Station();

                station.number = Integer.parseInt(sheet.getRow(i).getCell(1).getStringCellValue());
                int areaID = Integer.parseInt(sheet.getRow(i).getCell(2).getStringCellValue().split("-")[0]);
                station.name = sheet.getRow(i).getCell(2).getStringCellValue();

                //next area
                if (areaID != cacheArea) {
                    if (area != null) {
                        mainCampaignConfig.area.add(area);
                    }
                    cacheArea = areaID;
                    area = new Area();
                    area.name = "Area " + areaID;

                    area.reward = new ArrayList<>();
                    area.reward.add(new StarRewardVO((byte) 1, Arrays.asList(new ResourcePackage("MON1001", 100))));
                    area.reward.add(new StarRewardVO((byte) 2, Arrays.asList(new ResourcePackage("MON1001", 200))));
                    area.reward.add(new StarRewardVO((byte) 3, Arrays.asList(new ResourcePackage("MON1001", 300))));
                }

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
                        } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 2){
                            if (sheet.getRow(i).getCell(col).toString().split(",")[1].contains("E")) {
                                monsterOnTeam.element = sheet.getRow(i).getCell(col).toString().split(",")[1];
                            } else {
                                monsterOnTeam.element = Element.v1.get(Utils.randomInRange(0, Element.v1.size() - 1)).getId();
                                monsterOnTeam.level = Integer.parseInt(sheet.getRow(i).getCell(col).toString().split(",")[1]);
                            }
                        } else if (sheet.getRow(i).getCell(col).toString().split(",").length == 3){
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
                    station.enemy.add(monsterOnTeam);
                }

                //condition
                station.condition = Arrays.asList(
                        sheet.getRow(i).getCell(15).toString(),
                        sheet.getRow(i).getCell(20).toString(),
                        sheet.getRow(i).getCell(22).toString()
                );

                station.bossMode = sheet.getRow(i).getCell(11).getStringCellValue().equalsIgnoreCase("Yes");
                station.terrain = sheet.getRow(i).getCell(12).getStringCellValue();
                station.bbg = sheet.getRow(i).getCell(13).toString();

                //phần thưởng theo số sao của mỗi stage
                station.complete.add(new StarRewardVO((byte) 1, Arrays.asList(
                        new ResourcePackage("MON1001", (int)sheet.getRow(i).getCell(16).getNumericCellValue()),
                        new ResourcePackage("MON1002", (int)sheet.getRow(i).getCell(17).getNumericCellValue()),
                        new ResourcePackage("MON1018", (int)sheet.getRow(i).getCell(18).getNumericCellValue()),
                        new ResourcePackage("SOG", (int)sheet.getRow(i).getCell(19).getNumericCellValue())
                )));
                station.complete.add(new StarRewardVO((byte) 2, Arrays.asList(
                        new ResourcePackage("SOG", (int)sheet.getRow(i).getCell(21).getNumericCellValue())
                )));
                station.complete.add(new StarRewardVO((byte) 3, Arrays.asList(
                        new ResourcePackage("SOG", (int)sheet.getRow(i).getCell(23).getNumericCellValue()),
                        new ResourcePackage("SPI1078", Integer.valueOf(sheet.getRow(i).getCell(24).getStringCellValue())),
                        new ResourcePackage("SPI1089", Integer.valueOf(sheet.getRow(i).getCell(25).getStringCellValue()))
                )));

                //afk
                String strLootMON1001 = sheet.getRow(i).getCell(26).getStringCellValue();
                String strLootMON1002 = sheet.getRow(i).getCell(27).getStringCellValue();
                String strLootSOG = sheet.getRow(i).getCell(28).getStringCellValue();
                String strLootMON1018 = sheet.getRow(i).getCell(31).getStringCellValue();
                String strLootMON1019 = sheet.getRow(i).getCell(32).getStringCellValue();

                int amountMON1001 = Integer.parseInt(strLootMON1001.split("/")[0]);
                int perMinuteMON1001 = strLootMON1001.split("/").length == 1 ? 1 : Integer.parseInt(strLootMON1001.split("/")[1]);

                int amountMON1002 = Integer.parseInt(strLootMON1002.split("/")[0]);
                int perMinuteMON1002 = strLootMON1002.split("/").length == 1 ? 1 : Integer.parseInt(strLootMON1002.split("/")[1]);

                int amountMON1003 = Integer.parseInt(strLootSOG.split("/")[0]);
                int perMinuteMON1003 = strLootSOG.split("/").length == 1 ? 1 : Integer.parseInt(strLootSOG.split("/")[1]);

                int amountMON1018 = Integer.parseInt(strLootMON1018.split("/")[0]);
                int perMinuteMON1018 = strLootMON1018.split("/").length == 1 ? 1 : Integer.parseInt(strLootMON1018.split("/")[1]);

                int amountMON1019 = Integer.parseInt(strLootMON1019.split("/")[0]);
                int perMinuteMON1019 = strLootMON1019.split("/").length == 1 ? 1 : Integer.parseInt(strLootMON1019.split("/")[1]);

                station.reward.loot.add(new LootVO("MON1001", amountMON1001, perMinuteMON1001));
                station.reward.loot.add(new LootVO("MON1002", amountMON1002, perMinuteMON1002));
                station.reward.loot.add(new LootVO("SOG", amountMON1003, perMinuteMON1003));
                station.reward.loot.add(new LootVO("MON1018", amountMON1018, perMinuteMON1018));
                station.reward.loot.add(new LootVO("MON1019", amountMON1019, perMinuteMON1019));
                area.station.add(station);
            } catch (Exception ede) {
                System.out.println(i + 1);
                ede.printStackTrace();
                return;
            }
        }
        mainCampaignConfig.area.add(area);
        System.out.println(Utils.toJson(mainCampaignConfig));
    }
}
