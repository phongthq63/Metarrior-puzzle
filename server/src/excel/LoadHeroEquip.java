package excel;

import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.gamelib.item.ItemManager;
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

public class LoadHeroEquip {
    public static void main(String[] args) {
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/items/" + "items.json"));

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

        SFSArray listEquipObject = new SFSArray();
        config.putInt("sizeBag", 200);
        config.putInt("maxStar", 5);
        config.putSFSArray("listEquip", listEquipObject);
        for(int i = 3; i<=309; i++){
            try{
                if (sheet.getRow(i).getCell(0).getNumericCellValue() == 1){
                    SFSObject object = new SFSObject();
                    object.putUtfString("id", sheet.getRow(i).getCell(2).getStringCellValue());
                    object.putInt("position", (int) (sheet.getRow(i).getCell(9).getNumericCellValue() - 1));
                    object.putUtfString("name", sheet.getRow(i).getCell(12).getStringCellValue());
                    object.putInt("star", Integer.parseInt(ItemManager.getInstance().getColorIdDependOnName(sheet.getRow(i).getCell(11).getStringCellValue())));
                    object.putUtfString("kingdom", "1");
                    object.putInt("expFis", getFis(Integer.parseInt(ItemManager.getInstance().getColorIdDependOnName(sheet.getRow(i).getCell(11).getStringCellValue()))));
                    object.putUtfString("type", getType(sheet.getRow(i).getCell(7).getStringCellValue()));
                    object.putUtfString("profession", getProfession(sheet.getRow(i).getCell(6).getStringCellValue()));
                    listEquipObject.addSFSObject(object);
                }

            }catch (Exception e){

            }

//            }
        }

        System.out.println(config.toJson());
        System.out.println(System.getProperty("user.dir") + "/conf/test/" + "test1.json");
        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/conf/test/" + "test1.json");
    }

    public static String getProfession(String profession){
        if (profession.equals("x")){
            return "0";
        }else {
            return CharactersConfigManager.getInstance().getClassConfigDependName(profession).id;
        }
    }

    public static String getType(String type){
        if (type.equals("x")){
            return "0";
        }else{
            return ItemManager.getInstance().getWeaponIdDependOnName(type);
        }
    }

    public static int getFis(int star){
        if (star == 1){
            return 125;
        }else if (star == 2){
            return 250;
        }else if (star == 3){
            return 500;
        }else if (star == 4){
            return 1000;
        }else if (star == 5){
            return 2000;
        }else {
            return 0;
        }
    }
}
