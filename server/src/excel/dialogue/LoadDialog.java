package excel.dialogue;

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

public class LoadDialog {
    public static void main(String[] args) {
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/test/" + "dialog.json"));

        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/excel/" + "DialogueSystemConfig.xlsx"));
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
        XSSFSheet sheet = workbook.getSheet("Sheet1");

        SFSArray sfsArray = new SFSArray();
        config.putSFSArray("list", sfsArray);
        for(int i = 1; i<=56; i++){
            SFSObject sfsObject = new SFSObject();
//            //System.out.println(sheet.getRow(i).getCell(0).getStringCellValue());
            sfsObject.putText("id_dialog", check(sheet.getRow(i).getCell(0).getStringCellValue()));
            sfsObject.putText("scene", check(sheet.getRow(i).getCell(1).getStringCellValue()));
            sfsObject.putText("left", check(sheet.getRow(i).getCell(2).getStringCellValue()));
            sfsObject.putText("middle", check(sheet.getRow(i).getCell(3).getStringCellValue()));
            sfsObject.putText("right", check(sheet.getRow(i).getCell(4).getStringCellValue()));
            sfsObject.putInt("active", (int) sheet.getRow(i).getCell(5).getNumericCellValue());
            sfsObject.putText("name", sheet.getRow(i).getCell(6).getStringCellValue());
            sfsObject.putText("skip", sheet.getRow(i).getCell(7).getStringCellValue());
            SFSArray dialog = new SFSArray();
//            for (int j = 4; j<=8; j++){
                SFSObject sfsDialog = new SFSObject();
                sfsDialog.putText("dialog1", sheet.getRow(i).getCell(8).getStringCellValue());
                sfsDialog.putText("dialog2", sheet.getRow(i).getCell(9).getStringCellValue());
                sfsDialog.putText("dialog3", sheet.getRow(i).getCell(10).getStringCellValue());
                sfsDialog.putText("dialog4", sheet.getRow(i).getCell(11).getStringCellValue());
                sfsDialog.putText("dialog5", sheet.getRow(i).getCell(12).getStringCellValue());
                dialog.addSFSObject(sfsDialog);
//            }
            sfsObject.putSFSArray("dialog", dialog);
            sfsArray.addSFSObject(sfsObject);
        }

//        //System.out.println(config.toJson());
        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/conf/" + "test/dialog.json");
    }

    public static String check(String x){
        if (x.equals("x")){
            return "";
        }
        return x;
    }
}
