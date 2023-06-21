
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

public class LoadAttributeEquipBK {
    public static void main(String[] args) {
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadFile(System.getProperty("user.dir") + "/conf/items/" + "item_attribute.json"));

        // Đọc một file XSL.
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(System.getProperty("user.dir") + "/excel/" + "Hero Equips(16).xlsx"));
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

        XSSFSheet sheetExpNeed = workbook.getSheetAt(13);
        SFSArray listEquipObject = new SFSArray();
        config.putSFSArray("listAttrItem", listEquipObject);
        for (int i = 3; i <= 309; i++) {
            try {
                double version = sheet.getRow(i).getCell(0).getNumericCellValue();
                if (version == 1) {
                    SFSObject object = new SFSObject();
                    object.putUtfString("id", sheet.getRow(i).getCell(2).getStringCellValue());
                    object.putInt("maxLevel", 9);
                    SFSArray array = new SFSArray();
                    for (int level = 0; level <= 9; level++) {
                        SFSObject sfsObject = new SFSObject();
                        sfsObject.putInt("level", level);
                        int exp = getExpNeed(level, sheetExpNeed, Integer.parseInt(ItemManager.getInstance().getColorIdDependOnName(sheet.getRow(i).getCell(11).getStringCellValue())));
                        sfsObject.putInt("expNeed", exp);
                        SFSArray attr = new SFSArray();
                        for (int k = 13; k <= 28; k++) {
                            SFSObject attribute = new SFSObject();
                            try {
                                double value = sheet.getRow(i).getCell(k).getNumericCellValue();
                                attribute.putInt("attr", Attr.fromStrValue(sheet.getRow(1).getCell(k).getStringCellValue()).getValue());
                                attribute.putShort("type", getType(sheet.getRow(2).getCell(k).getStringCellValue()));
                                if (level == 0) {
                                    attribute.putDouble("param", value);
                                } else {
                                    double a = getSum(value, level);
                                    attribute.putDouble("param", a);
                                }
                                attr.addSFSObject(attribute);
                            } catch (Exception e) {

                            }

                        }
                        sfsObject.putSFSArray("listAttr", attr);
                        array.addSFSObject(sfsObject);

                    }
                    object.putSFSArray("listLevel", array);
                    listEquipObject.addSFSObject(object);
                }

            } catch (Exception e) {

            }

        }
        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/conf/test/" + "test2.json");
    }

    private static double getSum(double value, int level) {
        double sum = value;
        for (int i = 0; i < level; i++) {
            sum += (sum * 0.06);
        }
        return sum;
    }

    private static boolean checkLength(double value) {
        String text = Double.toString(Math.abs(value));
        int integerPlaces = text.indexOf('.');
        int decimalPlaces = text.length() - integerPlaces - 1;
        return decimalPlaces > 2;
    }

    private static int getExpNeed(int level, XSSFSheet sheet, int star) {
        if (level == 9) {
            return 0;
        }
        return (int) sheet.getRow(getRow(level) + 1).getCell(getCell(star)).getNumericCellValue();

    }

    private static int getCell(int star) {
        return star;
    }

    private static int getRow(int level) {
        return 14 + level;
    }

    private static short getType(String a) {
        if (a.equals("%")) {
            return 1;
        } else {
            return 0;
        }
    }

    private static double getParam(String stringCellValue) {
        if (stringCellValue.contains("%")) {
            return Integer.parseInt(stringCellValue.replace("%", ""));
        } else {
            return Integer.parseInt(stringCellValue);
        }
    }

}

