package excel;

import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Iterator;

/**
 * Create by Popeye on 4:52 PM, 5/4/2020
 */
public class LoadHeroLevel {
    public static void main(String[] args) {
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/server/conf/characters/" + "hero/LevelHero.json"));

        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/server/excel/" + "Hero Level.xlsx"));
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


        SFSArray sfsArray1;
        SFSArray sfsArray2;

        SFSObject levelupObject = new SFSObject();
        SFSObject breakLimitObject = new SFSObject();

        config.putSFSObject("levelup", levelupObject);
        config.putSFSObject("breakLimit", breakLimitObject);
        for(int i = 2; i<=241; i++){
            sfsArray1 = new SFSArray();

            SFSObject sfsObject1001 = new SFSObject();
            sfsObject1001.putUtfString("id", "MON1001");
            sfsObject1001.putInt("amount", (int) Math.floor((sheet.getRow(i).getCell(4).getNumericCellValue())));
            sfsArray1.addSFSObject(sfsObject1001);

            SFSObject sfsObject1002 = new SFSObject();
            sfsObject1002.putUtfString("id", "MON1002");
            sfsObject1002.putInt("amount", (int) Math.floor((sheet.getRow(i).getCell(5).getNumericCellValue())));
            sfsArray1.addSFSObject(sfsObject1002);

            levelupObject.putSFSArray(i - 1 + "", sfsArray1);
            ////
            if(sheet.getRow(i).getCell(6).getNumericCellValue() > 0){
                sfsArray2 = new SFSArray();

                SFSObject sfsObject1003 = new SFSObject();
                sfsObject1003.putUtfString("id", "SOG");
                sfsObject1003.putInt("amount", (int) Math.floor((sheet.getRow(i).getCell(6).getNumericCellValue())));
                sfsArray2.addSFSObject(sfsObject1003);

                breakLimitObject.putSFSArray(i - 1 + "", sfsArray2);
            }
        }

        System.out.println(config.toJson());
        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/server/conf/" + "test/test1.json");
    }
}
