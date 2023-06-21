package excel.character;

import com.bamisu.log.gameserver.module.characters.hero.CharacterStatsGrowConfig;
import com.bamisu.log.gameserver.module.characters.hero.HeroConfig;
import com.bamisu.log.gameserver.module.characters.hero.entities.CharacterStatsGrowVO;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.gamelib.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by Popeye on 4:12 PM, 6/18/2020
 */
public class LoadHero {
    public static void main(String[] args) {
        //Hero
//        HeroConfig heroConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/server/conf/characters/hero/HeroMax.json"), HeroConfig.class);
//        CreepConfig creepConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/creep/Creep.json"), CreepConfig.class);
        CharacterStatsGrowConfig characterStatsGrowConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/server/conf/characters/hero/HeroStatsGrowMinConfig.json"), CharacterStatsGrowConfig.class);
//        CharacterStatsGrowConfig heroGrowConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/hero/HeroStatsGrowConfig.json"), CharacterStatsGrowConfig.class);
//        heroGrowConfig.list.clear();

        //miniboss
//        MbossConfig mbossConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/mboss/MBoss.json"), MbossConfig.class);
//        CharacterStatsGrowConfig characterStatsGrowConfig = Utils.fromJson(Utils.loadFile(System.getProperty("user.dir") + "/conf/characters/mboss/MBossStatsGrowConfig.json"), CharacterStatsGrowConfig.class);

//        //System.out.println(heroConfig.listHeroModel.size());
        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("P:\\Metarrior\\puzzle\\server\\excel\\HeroStats.xlsx"));
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
        List<HeroVO> excelListHero = new ArrayList<>();

        //hero
//        for (int i = 7; i <= 59; i++) {
//            String id = sheet.getRow(i).getCell(0).getStringCellValue();
//
//            for(CharacterStatsGrowVO vo : characterStatsGrowConfig.list){
//                if(vo.id.equalsIgnoreCase(id)){
//                    vo.enhanceLevel.hp = (float) sheet.getRow(i).getCell(42).getNumericCellValue();
//                    vo.enhanceLevel.strength = (float) sheet.getRow(i).getCell(45).getNumericCellValue();
//                    vo.enhanceLevel.intelligence = (float) sheet.getRow(i).getCell(48).getNumericCellValue();
//                    vo.enhanceLevel.armor = (float) sheet.getRow(i).getCell(51).getNumericCellValue();
//                    vo.enhanceLevel.magicResistance = (float) sheet.getRow(i).getCell(54).getNumericCellValue();
//                    vo.enhanceLevel.agility = (float) sheet.getRow(i).getCell(57).getNumericCellValue();
//                    vo.enhanceLevel.armorPenetration = (float) sheet.getRow(i).getCell(60).getNumericCellValue();
//                    vo.enhanceLevel.magicPenetration = (float) sheet.getRow(i).getCell(63).getNumericCellValue();
//                    vo.enhanceLevel.crit = (float) sheet.getRow(i).getCell(66).getNumericCellValue();
//                }
//            }
//        }
        for (int i = 7; i <= 59; i++) {
            String id = sheet.getRow(i).getCell(0).getStringCellValue();

            for(CharacterStatsGrowVO vo : characterStatsGrowConfig.list){
                if(vo.id.equalsIgnoreCase(id)){
                    vo.enhanceLevel.hp = (float) sheet.getRow(i).getCell(43).getNumericCellValue();
                    vo.enhanceLevel.strength = (float) sheet.getRow(i).getCell(46).getNumericCellValue();
                    vo.enhanceLevel.intelligence = (float) sheet.getRow(i).getCell(49).getNumericCellValue();
                    vo.enhanceLevel.armor = (float) sheet.getRow(i).getCell(52).getNumericCellValue();
                    vo.enhanceLevel.magicResistance = (float) sheet.getRow(i).getCell(55).getNumericCellValue();
                    vo.enhanceLevel.agility = (float) sheet.getRow(i).getCell(58).getNumericCellValue();
                    vo.enhanceLevel.armorPenetration = (float) sheet.getRow(i).getCell(61).getNumericCellValue();
                    vo.enhanceLevel.magicPenetration = (float) sheet.getRow(i).getCell(64).getNumericCellValue();
                    vo.enhanceLevel.crit = (float) sheet.getRow(i).getCell(67).getNumericCellValue();
                }
            }
        }


//        for (int i = 7; i <= 59; i++) {
//            String id = sheet.getRow(i).getCell(0).getStringCellValue();
//
//            for (HeroVO heroVO : heroConfig.listHero) {
//                if (heroVO.id.equalsIgnoreCase(id)) {
//                    heroVO.health = (float) sheet.getRow(i).getCell(15).getNumericCellValue();
//                    heroVO.strength = (float) sheet.getRow(i).getCell(18).getNumericCellValue();
//                    heroVO.intelligence = (float) sheet.getRow(i).getCell(21).getNumericCellValue();
//                    heroVO.armor = (float) sheet.getRow(i).getCell(24).getNumericCellValue();
//                    heroVO.magicResistance = (float) sheet.getRow(i).getCell(27).getNumericCellValue();
//                    heroVO.agility = (float) sheet.getRow(i).getCell(30).getNumericCellValue();
//                    heroVO.armorPenetration = (float) sheet.getRow(i).getCell(33).getNumericCellValue();
//                    heroVO.magicPenetration = (float) sheet.getRow(i).getCell(36).getNumericCellValue();
//                    heroVO.crit = (float) sheet.getRow(i).getCell(39).getNumericCellValue();
//                }
//            }
//        }

//        for (int i = 7; i <= 59; i++) {
//            String id = sheet.getRow(i).getCell(0).getStringCellValue();
//
//            for (HeroVO heroVO : heroConfig.listHero) {
//                if (heroVO.id.equalsIgnoreCase(id)) {
//                    heroVO.health = (float) sheet.getRow(i).getCell(16).getNumericCellValue();
//                    heroVO.strength = (float) sheet.getRow(i).getCell(19).getNumericCellValue();
//                    heroVO.intelligence = (float) sheet.getRow(i).getCell(22).getNumericCellValue();
//                    heroVO.armor = (float) sheet.getRow(i).getCell(25).getNumericCellValue();
//                    heroVO.magicResistance = (float) sheet.getRow(i).getCell(28).getNumericCellValue();
//                    heroVO.agility = (float) sheet.getRow(i).getCell(31).getNumericCellValue();
//                    heroVO.armorPenetration = (float) sheet.getRow(i).getCell(34).getNumericCellValue();
//                    heroVO.magicPenetration = (float) sheet.getRow(i).getCell(37).getNumericCellValue();
//                    heroVO.crit = (float) sheet.getRow(i).getCell(40).getNumericCellValue();
//                }
//            }
//        }

        System.out.println(Utils.toJson(characterStatsGrowConfig.list));
    }
}
