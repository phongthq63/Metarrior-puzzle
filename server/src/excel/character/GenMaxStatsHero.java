package excel.character;

import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.hero.define.EHeroType;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.gamelib.entities.ServerConstant;
import com.bamisu.gamelib.utils.Utils;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class GenMaxStatsHero {
    public static void main(String[] args){
        ISFSObject config = SFSObject.newFromJsonData(Utils.loadConfig(ServerConstant.Character.FILE_PATH_CONFIG_HERO));

        ISFSArray arrayPack = config.getSFSArray("listHeroModel");
        ISFSObject objPack;
        HeroVO heroCf;
        HeroModel heroModel;
        Stats stats;
        String idHero;
        int maxStar;
        for(int i = 0; i < arrayPack.size(); i++){
            objPack = arrayPack.getSFSObject(i);

            idHero = objPack.getUtfString("id");
            maxStar = objPack.getInt("maxStar");

            heroCf = CharactersConfigManager.getInstance().getHeroConfig(idHero);
            heroModel = HeroModel.createHeroModel(0, heroCf, EHeroType.NORMAL);
            heroModel.level = (short) CharactersConfigManager.getInstance().getMaxLevelHeroConfig(maxStar);
            heroModel.star = (short) maxStar;

            //Tinh chi so
//            stats = HeroManager.getInstance().getStatsHero(heroModel);
            stats = null;

            //Gan
            objPack.putInt("power", HeroManager.getInstance().getPower(stats));
            objPack.putFloat("health", Math.round(stats.readHp()));
            objPack.putFloat("strength", Math.round(stats.readStrength()));
            objPack.putFloat("intelligence", Math.round(stats.readIntelligence()));
            objPack.putFloat("dexterity", Math.round(stats.readDexterity()));
            objPack.putFloat("armor", Math.round(stats.readArmor()));
            objPack.putFloat("magicResistance", Math.round(stats.readMagicResistance()));
            objPack.putFloat("agility", Math.round(stats.readAgility()));
            objPack.putFloat("crit", stats.readCrit());
            objPack.putFloat("critBonus", stats.readCritDmg());
            objPack.putFloat("armorPenetration", stats.readArmorPenetration());
            objPack.putFloat("magicPenetration", stats.readMagicPenetration());
            objPack.putFloat("tenacity", Math.round(stats.readTenacity()));
            objPack.putFloat("elusiveness", Math.round(stats.readElusiveness()));
        }

        Utils.saveToFile(config.toJson(), "C:\\Users\\Quach Thanh Phong\\Downloads\\" + "test.json");
    }
}
