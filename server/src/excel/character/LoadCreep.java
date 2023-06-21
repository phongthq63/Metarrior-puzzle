package excel.character;

import com.bamisu.log.gameserver.module.characters.creep.CreepConfig;
import com.bamisu.log.gameserver.module.characters.creep.entities.CreepVO;
import com.bamisu.log.gameserver.module.characters.hero.CharacterStatsGrowConfig;
import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;
import com.bamisu.gamelib.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Create by Popeye on 9:32 AM, 6/19/2020
 */
public class LoadCreep {
    public static void main(String[] args) {
        //miniboss
        CreepConfig creepConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/creep/Creep.json"), CreepConfig.class);
        CharacterStatsGrowConfig characterStatsGrowConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/creep/CreepStatsGrowConfig.json"), CharacterStatsGrowConfig.class);
        characterStatsGrowConfig.list.clear();

        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\character\\Character stats.xlsx"));
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
        List<CreepVO> excelListMBoss = new ArrayList<>();

        //hero
        for (int i = 61; i < 86; i++) {
            String version = sheet.getRow(i).getCell(1).getStringCellValue();
            String id = sheet.getRow(i).getCell(0).getStringCellValue();
            if(version.equalsIgnoreCase("1")){
                CreepVO creepVO = new CreepVO();
                creepVO.id = id;
                creepVO.name = sheet.getRow(i).getCell(2).getStringCellValue();
                creepVO.health = Float.parseFloat(sheet.getRow(i).getCell(10).getStringCellValue());
                creepVO.strength = Float.parseFloat(sheet.getRow(i).getCell(11).getStringCellValue());
                creepVO.intelligence = Float.parseFloat(sheet.getRow(i).getCell(12).getStringCellValue());
                creepVO.armor = Float.parseFloat(sheet.getRow(i).getCell(13).getStringCellValue());
                creepVO.magicResistance = Float.parseFloat(sheet.getRow(i).getCell(14).getStringCellValue());
                creepVO.dexterity = Float.parseFloat(sheet.getRow(i).getCell(15).getStringCellValue());
                creepVO.agility = Float.parseFloat(sheet.getRow(i).getCell(16).getStringCellValue());
                creepVO.elusiveness = Float.parseFloat(sheet.getRow(i).getCell(17).getStringCellValue());
                creepVO.armorPenetration = Float.parseFloat(sheet.getRow(i).getCell(18).getStringCellValue());
                creepVO.magicPenetration = Float.parseFloat(sheet.getRow(i).getCell(19).getStringCellValue());
                creepVO.crit = Float.parseFloat(sheet.getRow(i).getCell(20).getStringCellValue());
                creepVO.critBonus = Float.parseFloat(sheet.getRow(i).getCell(21).getStringCellValue());
                creepVO.tenacity = Float.parseFloat(sheet.getRow(i).getCell(22).getStringCellValue());
                excelListMBoss.add(creepVO);

                CharacterStatsGrowVO statsGrowVO = new CharacterStatsGrowVO();
                statsGrowVO.id = id;
                statsGrowVO.enhanceLevel.hp = Float.parseFloat(sheet.getRow(i).getCell(23).getStringCellValue());
                statsGrowVO.enhanceLevel.strength = Float.parseFloat(sheet.getRow(i).getCell(24).getStringCellValue());
                statsGrowVO.enhanceLevel.intelligence = Float.parseFloat(sheet.getRow(i).getCell(25).getStringCellValue());
                statsGrowVO.enhanceLevel.armor = Float.parseFloat(sheet.getRow(i).getCell(26).getStringCellValue());
                statsGrowVO.enhanceLevel.magicResistance = Float.parseFloat(sheet.getRow(i).getCell(27).getStringCellValue());
                statsGrowVO.enhanceLevel.dexterity = Float.parseFloat(sheet.getRow(i).getCell(28).getStringCellValue());
                statsGrowVO.enhanceLevel.agility = Float.parseFloat(sheet.getRow(i).getCell(29).getStringCellValue());
                statsGrowVO.enhanceLevel.elusiveness = Float.parseFloat(sheet.getRow(i).getCell(30).getStringCellValue());
                statsGrowVO.enhanceLevel.armorPenetration = Float.parseFloat(sheet.getRow(i).getCell(31).getStringCellValue());
                statsGrowVO.enhanceLevel.magicPenetration = Float.parseFloat(sheet.getRow(i).getCell(32).getStringCellValue());
                statsGrowVO.enhanceLevel.crit = Float.parseFloat(sheet.getRow(i).getCell(33).getStringCellValue());
                statsGrowVO.enhanceLevel.critDmg = Float.parseFloat(sheet.getRow(i).getCell(34).getStringCellValue());
                statsGrowVO.enhanceLevel.tenacity = Float.parseFloat(sheet.getRow(i).getCell(35).getStringCellValue());
                characterStatsGrowConfig.list.add(statsGrowVO);
            }
        }
        //stats
        for(CreepVO creepVO : creepConfig.listCreep){
            for(CreepVO tmpCreepVO : excelListMBoss){
                if(creepVO.id.equalsIgnoreCase(tmpCreepVO.id)){
                    creepVO.name = tmpCreepVO.name;
                    creepVO.health = tmpCreepVO.health;
                    creepVO.strength = tmpCreepVO.strength;
                    creepVO.intelligence = tmpCreepVO.intelligence;
                    creepVO.armor = tmpCreepVO.armor;
                    creepVO.magicResistance = tmpCreepVO.magicResistance;
                    creepVO.dexterity = tmpCreepVO.dexterity;
                    creepVO.agility = tmpCreepVO.agility;
                    creepVO.elusiveness = tmpCreepVO.elusiveness;
                    creepVO.armorPenetration = tmpCreepVO.armorPenetration;
                    creepVO.magicPenetration = tmpCreepVO.magicPenetration;
                    creepVO.crit = tmpCreepVO.crit;
                    creepVO.critBonus = tmpCreepVO.critBonus;
                    creepVO.tenacity = tmpCreepVO.tenacity;
                }
            }
        }

        Collections.sort(creepConfig.listCreep, (o1, o2) -> {
            if(Integer.parseInt(o1.id.replace("M","")) > Integer.parseInt(o2.id.replace("M",""))) return 1;
            if(Integer.parseInt(o1.id.replace("M","")) < Integer.parseInt(o2.id.replace("M",""))) return -1;
            return 0;
        });

        System.out.println("Hero list: " + Utils.toJson(creepConfig.listCreep));
        System.out.println("Hero grow: " + Utils.toJson(characterStatsGrowConfig.list));
        System.out.println("Hero list: " + creepConfig.listCreep.size());
        System.out.println("Hero grow: " + characterStatsGrowConfig.list.size());
    }
}
