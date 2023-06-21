package excel.character;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.module.characters.celestial.entities.LevelVO;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LoadLevelCelestial {
    public static void main(String[] args){
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadConfig(""));

//        //System.out.println(config.getDump());
        // Đọc một file XSL.
        File file = new File("C:\\Users\\Quach Thanh Phong\\Downloads\\" + "Puzzo KB Modules.xlsx");

        // Đối tượng workbook cho file XSL.
        XSSFWorkbook workbook = null;
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(file.toPath());
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
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

        // Lấy ra sheet đầu tiên từ workbook
        XSSFSheet sheet = workbook.getSheet("CBeast Stats");


        List<LevelVO> a = new ArrayList<>();
        //Row
        for(int i = 11; i < 510; i++){
            Row row = sheet.getRow(i);

            //Cell
            Cell cellLv = row.getCell(3);
            Cell cellCost = row.getCell(4);

            a.add(new LevelVO((short) cellLv.getNumericCellValue(), (long)cellCost.getNumericCellValue()));
        }

        config.getSFSArray("list").getSFSObject(0).putSFSArray("level", SFSArray.newFromJsonData(Utils.toJson(a)));
        //System.out.println(config.toJson());
        Utils.saveToFile(config.toJson(), "C:\\Users\\Quach Thanh Phong\\Downloads\\" + "test.json");
    }
}
