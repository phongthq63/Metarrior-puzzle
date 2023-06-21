package excel.campain;

import com.bamisu.gamelib.config.CampaignSpecialPuzzleConfig;
import com.bamisu.gamelib.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Create by Popeye on 8:30 AM, 1/23/2021
 */
public class GenCampaignPuzzle {
    public static void main(String[] args) {
        CampaignSpecialPuzzleConfig campaignSpecialPuzzleConfig = new CampaignSpecialPuzzleConfig();
        FileInputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream("D:\\project\\puzzle\\GameServer\\server\\conf\\campaign\\special-puzzle.json");
//            campaignSpecialPuzzleConfig = Utils.fromJson(IOUtils.toString(inputStream), CampaignSpecialPuzzleConfig.class);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
            inputStream = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\campain\\campaign-lock-puzzle.xlsx"));
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
        for (int i = 2; i <= 301; i++) {
            String stageName = sheet.getRow(i).getCell(2).getStringCellValue();
            String strLock = sheet.getRow(i).getCell(3).getStringCellValue();

            if(strLock.isEmpty() || strLock.equalsIgnoreCase("x")) continue;

            campaignSpecialPuzzleConfig.lockMap.put(stageName, strLock);
        }

        System.out.println(Utils.toJson(campaignSpecialPuzzleConfig));
        if (true) return;
    }
}
