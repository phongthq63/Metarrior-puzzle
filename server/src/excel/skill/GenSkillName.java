package excel.skill;

import com.bamisu.gamelib.entities.ResourcePackage;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.campaign.config.entities.MonsterOnTeam;
import com.bamisu.log.gameserver.module.campaign.entities.TeamUtils;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.entities.ICharacterVO;
import com.bamisu.gamelib.skill.config.HeroSkillConfig;
import com.bamisu.gamelib.skill.config.entities.BaseSkillInfo;
import com.bamisu.log.gameserver.module.tower.config.entities.TowerVO;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Popeye on 9:46 AM, 9/24/2020
 */
public class GenSkillName {
    public static void main(String[] args) {
        HeroSkillConfig heroSkillConfig = Utils.fromJson(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_SKILL_BASE_INFO), HeroSkillConfig.class);
        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\skill\\skill-name.xlsx"));
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
        for (int i = 1; i < 80; i++) {
            String id = "";
            for (int j = 1; j <= 4; j++){
                id = sheet.getRow(i).getCell(0).getStringCellValue();
                id += "S" + j;

                for (BaseSkillInfo baseSkillInfo : heroSkillConfig.list) {
                    if (id.equals(baseSkillInfo.id)){
                        baseSkillInfo.name = sheet.getRow(i).getCell(j + 1).getStringCellValue();
                        System.out.println(id + " " + baseSkillInfo.name);
                    }
                }
            }
        }

        System.out.println(Utils.toJson(heroSkillConfig.list));
    }
}
