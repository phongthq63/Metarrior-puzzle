package excel;

import com.bamisu.gamelib.entities.Attr;
import com.bamisu.gamelib.item.ItemManager;
import com.bamisu.gamelib.item.entities.AttributeVO;
import com.bamisu.gamelib.item.entities.EquipLevelConfig;
import com.bamisu.gamelib.item.entities.EquipLevelConfigVO;
import com.bamisu.gamelib.item.entities.EquipLevelVO;
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
import java.util.ArrayList;

public class LoadAttributeEquip {
    public static void main(String[] args) {
        EquipLevelConfig equipLevelConfig = new EquipLevelConfig();
        equipLevelConfig.listAttrItem = new ArrayList<>();

        // Đọc một file XSL.
        FileInputStream inputStreamEquipStats = null;
        FileInputStream inputStreamEquipEXP = null;
        try {
            inputStreamEquipStats = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\Hero Equips(16).xlsx"));
            inputStreamEquipEXP = new FileInputStream(new File("D:\\project\\puzzle\\GameServer\\server\\src\\excel\\Hero Equips Enhancement(17).xlsx"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamEquipStats != null) {
                    inputStreamEquipStats.close();
                }

                if (inputStreamEquipEXP != null) {
                    inputStreamEquipEXP.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        // Đối tượng workbook cho file XSL.
        XSSFWorkbook workbookEquipStats = null;
        XSSFWorkbook workbookEquipEXP = null;
        try {
            workbookEquipStats = new XSSFWorkbook(inputStreamEquipStats);
            workbookEquipEXP = new XSSFWorkbook(inputStreamEquipEXP);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Lấy ra sheet đầu tiên từ workbook
        XSSFSheet sheetEquipStats = workbookEquipStats.getSheetAt(0);
        XSSFSheet sheetEquipEXP = workbookEquipEXP.getSheetAt(0);

        SFSArray listEquipObject = new SFSArray();
        for (int i = 2; i <= 307; i++) {
            try {
                double version = sheetEquipStats.getRow(i).getCell(0).getNumericCellValue();
                if (version == 1) {
                    EquipLevelConfigVO equipLevelConfigVO = new EquipLevelConfigVO();
                    equipLevelConfigVO.id = sheetEquipStats.getRow(i).getCell(2).getStringCellValue();
                    equipLevelConfigVO.maxLevel = 9;
                    equipLevelConfigVO.listLevel = new ArrayList<>();
                    for (int level = 0; level <= 9; level++) {
                        EquipLevelVO equipLevelVO = new EquipLevelVO();
                        equipLevelVO.level = level;
                        int exp = getExpNeed(level, sheetEquipEXP, Integer.parseInt(ItemManager.getInstance().getColorIdDependOnName(sheetEquipStats.getRow(i).getCell(11).getStringCellValue())));
                        equipLevelVO.expNeed = exp;
                        equipLevelVO.listAttr = new ArrayList<>();
                        for (int k = 13; k <= 28; k++) {
                            AttributeVO attribute = new AttributeVO();
                            try {
                                String value = sheetEquipStats.getRow(i).getCell(k).getStringCellValue();
                                if (value.equalsIgnoreCase("x")) continue;

                                attribute.attr = Attr.fromStrValue(sheetEquipStats.getRow(1).getCell(k).getStringCellValue()).getValue();
                                attribute.type = getType(value);
                                if (value.contains("%")) value = value.replace("%", "");
                                if (level == 0) {
                                    attribute.param = Float.valueOf(value);
                                } else {
                                    double s = getSum(Float.valueOf(value), level);
                                    attribute.param = (float) s;
                                }
                                equipLevelVO.listAttr.add(attribute);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println(i + " " + k);
                            }
                        }
                        equipLevelConfigVO.listLevel.add(equipLevelVO);
                    }
                    equipLevelConfig.listAttrItem.add(equipLevelConfigVO);
                }

            } catch (Exception e) {

            }

        }
        System.out.println(Utils.toJson(equipLevelConfig));
//        Utils.saveToFile(config.toJson(), System.getProperty("user.dir") + "/conf/test/" + "test2.json");
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
        if (a.contains("%")) {
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
