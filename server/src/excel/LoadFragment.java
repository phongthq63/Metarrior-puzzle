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

public class LoadFragment {
    public static void main(String[] args) {
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/items/" + "fragment_hero.json"));

        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/excel/" + "Puzzo KB Modules.xlsx"));
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
        XSSFSheet sheet = workbook.getSheetAt(12);

        SFSArray listFragment = new SFSArray();
        config.putSFSArray("listFragment", listFragment);
        for(int i = 1; i<=55; i++){
            try{
                SFSObject object = new SFSObject();
                object.putUtfString("id", sheet.getRow(i).getCell(1).getStringCellValue());
                object.putUtfString("name", sheet.getRow(i).getCell(2).getStringCellValue());
                object.putInt("need", 60);
                object.putUtfString("desc", "");
                object.putInt("star", (int) sheet.getRow(i).getCell(6).getNumericCellValue());
                object.putUtfString("border", sheet.getRow(i).getCell(4).getStringCellValue());
                listFragment.addSFSObject(object);
            }catch (Exception e){

            }

        }

        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/conf/test/" + "fragment.json");
    }
}
