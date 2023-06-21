package excel.character;

import com.bamisu.log.gameserver.module.characters.entities.Mboss;
import com.bamisu.log.gameserver.module.characters.hero.CharacterStatsGrowConfig;
import com.bamisu.log.gameserver.module.characters.hero.HeroConfig;
import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.characters.mboss.MbossConfig;
import com.bamisu.log.gameserver.module.characters.mboss.entities.MbossVO;
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
 * Create by Popeye on 9:09 AM, 6/19/2020
 */
public class LoadMBoss {
    public static void main(String[] args) {
        //miniboss
        MbossConfig mbossConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/mboss/MBoss.json"), MbossConfig.class);
        CharacterStatsGrowConfig characterStatsGrowConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/mboss/MBossStatsGrowConfig.json"), CharacterStatsGrowConfig.class);
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
        List<MbossVO> excelListMBoss = new ArrayList<>();

        //hero
        for (int i = 57; i < 60; i++) {
            String version = sheet.getRow(i).getCell(1).getStringCellValue();
            String id = sheet.getRow(i).getCell(0).getStringCellValue();
            if(version.equalsIgnoreCase("1")){
                MbossVO mbossVO = new MbossVO();
                mbossVO.id = id;
                mbossVO.health = Float.parseFloat(sheet.getRow(i).getCell(10).getStringCellValue());
                mbossVO.strength = Float.parseFloat(sheet.getRow(i).getCell(11).getStringCellValue());
                mbossVO.intelligence = Float.parseFloat(sheet.getRow(i).getCell(12).getStringCellValue());
                mbossVO.armor = Float.parseFloat(sheet.getRow(i).getCell(13).getStringCellValue());
                mbossVO.magicResistance = Float.parseFloat(sheet.getRow(i).getCell(14).getStringCellValue());
                mbossVO.dexterity = Float.parseFloat(sheet.getRow(i).getCell(15).getStringCellValue());
                mbossVO.agility = Float.parseFloat(sheet.getRow(i).getCell(16).getStringCellValue());
                mbossVO.elusiveness = Float.parseFloat(sheet.getRow(i).getCell(17).getStringCellValue());
                mbossVO.armorPenetration = Float.parseFloat(sheet.getRow(i).getCell(18).getStringCellValue());
                mbossVO.magicPenetration = Float.parseFloat(sheet.getRow(i).getCell(19).getStringCellValue());
                mbossVO.crit = Float.parseFloat(sheet.getRow(i).getCell(20).getStringCellValue());
                mbossVO.critBonus = Float.parseFloat(sheet.getRow(i).getCell(21).getStringCellValue());
                mbossVO.tenacity = Float.parseFloat(sheet.getRow(i).getCell(22).getStringCellValue());
                excelListMBoss.add(mbossVO);

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
        for(MbossVO mbossVO : mbossConfig.listMBoss){
            for(MbossVO tmpMbossVO : excelListMBoss){
                if(mbossVO.id.equalsIgnoreCase(tmpMbossVO.id)){
                    mbossVO.health = tmpMbossVO.health;
                    mbossVO.strength = tmpMbossVO.strength;
                    mbossVO.intelligence = tmpMbossVO.intelligence;
                    mbossVO.armor = tmpMbossVO.armor;
                    mbossVO.magicResistance = tmpMbossVO.magicResistance;
                    mbossVO.dexterity = tmpMbossVO.dexterity;
                    mbossVO.agility = tmpMbossVO.agility;
                    mbossVO.elusiveness = tmpMbossVO.elusiveness;
                    mbossVO.armorPenetration = tmpMbossVO.armorPenetration;
                    mbossVO.magicPenetration = tmpMbossVO.magicPenetration;
                    mbossVO.crit = tmpMbossVO.crit;
                    mbossVO.critBonus = tmpMbossVO.critBonus;
                    mbossVO.tenacity = tmpMbossVO.tenacity;
                }
            }
        }

        Collections.sort(mbossConfig.listMBoss, (o1, o2) -> {
            if(Integer.parseInt(o1.id.replace("MBS","")) > Integer.parseInt(o2.id.replace("MBS",""))) return 1;
            if(Integer.parseInt(o1.id.replace("MBS","")) < Integer.parseInt(o2.id.replace("MBS",""))) return -1;
            return 0;
        });

        System.out.println("Hero list: " + Utils.toJson(mbossConfig.listMBoss));
        System.out.println("Hero grow: " + Utils.toJson(characterStatsGrowConfig.list));
        System.out.println("Hero grow: " + mbossConfig.listMBoss.size());
        System.out.println("Hero grow: " + characterStatsGrowConfig.list.size());
    }
}
