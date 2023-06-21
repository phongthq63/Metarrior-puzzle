package excel;

import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LoadLevelSage {
    public static void main(String[] args) {
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/user/" + "level.json"));

        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\userlevel.xlsx"));
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

        SFSArray sfsArray = new SFSArray();
        config.putSFSArray("listLevel", sfsArray);
        for(int i = 1; i<=212; i++){
            SFSObject sfsObject = new SFSObject();
            sfsObject.putInt("level", (int) sheet.getRow(i).getCell(1).getNumericCellValue());
            sfsObject.putLong("exp", (long) sheet.getRow(i).getCell(3).getNumericCellValue());
            sfsArray.addSFSObject(sfsObject);
        }

        //System.out.println(config.toJson());
//        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/conf/" + "test/level.json");
        System.out.println(config.toJson());
    }
}
