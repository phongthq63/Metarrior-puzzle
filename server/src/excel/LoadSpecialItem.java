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

public class LoadSpecialItem {
    public static void main(String[] args) {
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/items/" + "special_item.json"));

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
        XSSFSheet sheet = workbook.getSheet("Special Items");

        SFSArray sfsArray = new SFSArray();
        config.putSFSArray("listItem", sfsArray);
        for(int i = 3; i<=128; i++){
            try{
                if (sheet.getRow(i).getCell(0).getNumericCellValue() == 1 && !sheet.getRow(i).getCell(7).getStringCellValue().equals("")){
                    SFSObject sfsObject = new SFSObject();
                    sfsObject.putText("id", sheet.getRow(i).getCell(1).getStringCellValue());
                    sfsObject.putText("name", sheet.getRow(i).getCell(6).getStringCellValue());
                    sfsObject.putText("time", "");
                    sfsObject.putBool("using", true);
                    sfsObject.putText("desc", sheet.getRow(i).getCell(7).getStringCellValue());
                    sfsArray.addSFSObject(sfsObject);
                }
            }catch (Exception e){

            }

        }

        //System.out.println(config.toJson());
        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/conf/" + "test/special.json");
    }
}
