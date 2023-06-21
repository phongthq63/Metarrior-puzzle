package com.bamisu.log.gameserver.module.ingame.entities.effect;

import com.bamisu.gamelib.utils.Utils;

/**
 * Create by Popeye on 4:40 PM, 2/18/2020
 */
public enum EEffect {

    Stunned("SE001", EffectCategory.HARD, true, "Stun", 1),    //choáng
    Rooted("SE002", EffectCategory.HARD, true, "Rooted", 1),    //chói
    Paralyzed("SE003", EffectCategory.HARD, true, "Paralyzed", 1),  //tê liệt
    Sleep("SE004", EffectCategory.HARD, true, "Sleep", 1),  //ngủ
    Petrified("SE005", EffectCategory.HARD, true, "Petrified", 1),  //hóa đá
    Frozen("SE006", EffectCategory.HARD, true, "Frozen", 1),     //đóng băng
    Charmed("SE007", EffectCategory.HARD, true, "Charmed", 1),       //quyến rũ
    Confused("SE008", EffectCategory.HARD, true, "Confused", 1),    //hỗn loạn

    Silence("SE009", EffectCategory.SOFT, true, "Silence", 999),  //câm lặng
    Disarm("SE010", EffectCategory.SOFT, true, "Disarm", 999),    //giải giới
    Cripple("SE011", EffectCategory.SOFT, true, "Cripple", 1),  //bị què
    Curse("SE012", EffectCategory.SOFT, true, "Curse", 999), //bị nguyền
    Blind("SE013", EffectCategory.SOFT, true, "Blind", 999), //bị mù
    Stat_Buff("SE014", EffectCategory.SOFT, false, "Stat Buff", 999), //tăng chỉ số
    Stat_Debuff("SE015", EffectCategory.SOFT, true, "Stat Debuff", 999), //giảm chỉ số
    Poisoned("SE016", EffectCategory.SOFT, true, "Poisoned", 3),   //độc
    Bleed("SE017", EffectCategory.SOFT, true, "Bleed", 3), //chảy máu
    Leech_Per_Turn("SE018", EffectCategory.SOFT, true, "Leech Per Turn", 999),   //hút máu mỗi turn
    Burned("SE019", EffectCategory.SOFT, true, "Burned", 999),   //Thiêu đốt
    Frostbitten("SE020", EffectCategory.SOFT, true, "Frostbitten", 999), //Bỏng lạnh
    Infested("SE021", EffectCategory.SOFT, true, "Infested", 999),   //Nhiễm khuẩn
    Sludged("SE022", EffectCategory.SOFT, true, "Sludged", 999), //Bùn cát
    Magnetized("SE023", EffectCategory.SOFT, true, "Magnetized", 999),   //Từ hóa
    Invigorated("SE024", EffectCategory.SOFT, false, "Invigorated", 3),   //Hồi máu mỗi turn
    Immortal("SE025", EffectCategory.SOFT, false, "Immortal", 999),   //bất tử
    Immunity("SE026", EffectCategory.SOFT, false, "Immunity", 999),   //miễn nhiễm hiệ ứng
    Soulburn("SE027", EffectCategory.SOFT, true, "Soulburn", 999),   //giảm hồi máu và giáp ảo

    CLEAN_ALL_SE("SE050", EffectCategory.OTHER, false, "CleanAllSE", 0),   //xóa tất cả SE
    CLEAN_ALL_SE_NEGATIVE("SE051", EffectCategory.OTHER, false, "CleanAllSENegative", 0);   //xóa tất cả SE có hại

    private String id;
    private EffectCategory category;
    private String name;
    private int maxStack;
    private boolean isNegative; //là hiệu ứng có hại

    EEffect(String id, EffectCategory category, boolean isNegative, String name, int maxStack) {
        this.id = id;
        this.category = category;
        this.isNegative = isNegative;
        this.name = name;
        this.maxStack = maxStack;
    }

    public EffectCategory getCategory() {
        return category;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public static EEffect getRandom() {
        return values()[Utils.randomInRange(0, values().length - 1)];
    }

    public static EEffect getRandom(EffectCategory effectCategory) {
        EEffect tmp = null;
        do {
            tmp = values()[Utils.randomInRange(0, values().length - 1)];
        } while (tmp.getCategory() != effectCategory);

        return tmp;
    }

    public static EEffect fromName(String name) {
        for (EEffect effectEnum : values()) {
            if (effectEnum.name.equalsIgnoreCase(name)) return effectEnum;
        }

        return null;
    }

    public boolean isNegative() {
        return isNegative;
    }
}
