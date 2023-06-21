package excel.mission;

import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class LoadNameMission {
    public static void main(String[] args){
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadConfig(ServerConstant.Mission.FILE_PATH_CONFIG_MISSION_NAME));

        //System.out.println(config.getDump());
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
        XSSFSheet sheet = workbook.getSheet("Mission Names");

        List<String> list = new ArrayList<>();

        //Row
        for(int i = 1; i < 100; i++){
            Row row = sheet.getRow(i);

            //Cell
            Cell cell = row.getCell(1);

            list.add(cell.getStringCellValue());
        }

        config.putUtfStringArray("list", list);
        //System.out.println(config.toJson());
        Utils.saveToFile(config.toJson(), "C:\\Users\\Quach Thanh Phong\\Downloads\\" + "test.json");
    }
}
