package excel;

import com.bamisu.gamelib.entities.Attr;
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

public class LoadGem {
    public static void main(String[] args) {
        ISFSObject config1 = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/items/" + "stone.json"));
        ISFSObject config2 = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/items/" + "stone_attribute.json"));

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
        XSSFSheet sheet = workbook.getSheetAt(14);

//        SFSArray listEquipObject = new SFSArray();
//        config1.putInt("sizeBag", 100);
//        config1.putInt("maxStar", 9);
//        config1.putSFSArray("listStone", listEquipObject);
//        OUTERLOOP:
//        for(int i = 2; i<=91; i++){
//            SFSObject object = new SFSObject();
////            //System.out.println(sheet.getRow(i).getCell(1).getStringCellValue());
//            String id = sheet.getRow(i).getCell(1).getStringCellValue();
//            String [] rootId = id.split("_");
//            if (Integer.parseInt(rootId[1]) > 1){
//                continue OUTERLOOP;
//            }
//            object.putUtfString("id", rootId[0]);
//            object.putUtfString("name", sheet.getRow(i).getCell(6).getStringCellValue());
//            object.putInt("type", getType(sheet.getRow(i).getCell(4).getStringCellValue()));
//            listEquipObject.addSFSObject(object);
//
//        }

        SFSArray listGemObject = new SFSArray();
        config2.putSFSArray("listAttrStone", listGemObject);
        int count = 2;
        for (int i = 2; i<=91; i+=10){
            //=======================Gen attribute===========================
            SFSObject objectGem = new SFSObject();
            String id = sheet.getRow(i).getCell(1).getStringCellValue();
            String [] rootId = id.split("_");
            objectGem.putUtfString("id", rootId[0]);
            objectGem.putInt("maxLevel", 10);
            SFSArray array = new SFSArray();
            for (int level = 1; level<= 10; level++){
                SFSObject sfsObject = new SFSObject();
                sfsObject.putInt("level", level);
                SFSArray attr = new SFSArray();
                for (int k = 7; k <= 15; k++){
                    try{
                        //System.out.println(sheet.getRow(count).getCell(k).getNumericCellValue());
                        double value = sheet.getRow(count).getCell(k).getNumericCellValue();
                        SFSObject attribute = new SFSObject();
                        attribute.putInt("attr", Attr.fromStrValue(sheet.getRow(1).getCell(k).getStringCellValue()).getValue());
                        attribute.putShort("type", getTypeAttr(Attr.fromStrValue(sheet.getRow(1).getCell(k).getStringCellValue()).getValue()));
                        attribute.putDouble("param", value);
                        attr.addSFSObject(attribute);
                    }catch (Exception e){

                    }

                }
                //System.out.println("===========");
                count++;
                sfsObject.putSFSArray("listAttr", attr);
                array.addSFSObject(sfsObject);

            }
            objectGem.putSFSArray("listLevel", array);
            listGemObject.addSFSObject(objectGem);
//            ===========================================
        }

//        Utils.saveToFile(config1.toJson(), System.getProperty("user.dir") + "/conf/test/" + "gem_id.json");
        Utils.saveToFile(config2.toJson(), System.getProperty("user.dir") + "/conf/test/" + "gem_attr.json");
    }

    private static int getType(String stringCellValue) {
        return Integer.parseInt(ItemManager.getInstance().getGemTypeDependOnName(stringCellValue));
    }

    private static short getTypeAttr(int a) {
        if (a == 12 || a == 13 || a == 14){
            return 1;
        }else{
            return 0;
        }
    }

//    private static boolean checkLength(double value) {
//        if ()
//    }

}
