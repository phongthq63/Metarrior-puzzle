package excel.checkin30days;

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

public class LoadEvent1 {
    public static void main(String[] args) {
        ISFSObject config = new SFSObject();

        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\checkin30days\\30 days Check-In(25).xlsx"));
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

        SFSArray listGift = new SFSArray();
        config.putSFSArray("listGift", listGift);
        config.putInt("time", 33);
        for (int i = 13; i < 43; i++) {
            SFSObject object = new SFSObject();
            object.putUtfString("id", sheet.getRow(i).getCell(2).getStringCellValue());
            object.putInt("amount", Integer.parseInt(sheet.getRow(i).getCell(3).getStringCellValue()));
            object.putBool("fx", sheet.getRow(i).getCell(4).getStringCellValue().equalsIgnoreCase("Yes"));
            listGift.addSFSObject(object);
        }

        System.out.println(config.toJson());
    }
}
