package com.bamisu.log.gameserver.module.characters.entities;

import com.bamisu.log.gameserver.datamodel.hero.HeroSkillModel;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.entities.ICharacter;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;
import com.bamisu.log.gameserver.module.characters.hero.entities.HeroVO;
import com.bamisu.log.gameserver.module.hero.define.ETeamType;
import com.bamisu.log.gameserver.module.hero.entities.Stats;
import com.bamisu.log.gameserver.module.hero.HeroManager;
import com.bamisu.log.gameserver.module.ingame.entities.character.ECharacterType;
import com.smartfoxserver.v2.entities.Zone;

import java.util.ArrayList;
import java.util.List;

public class Hero implements ICharacter {
    public int characterType = ECharacterType.Hero.getType();
    public HeroModel heroModel;
    public HeroVO heroCf;
    public Stats stats;
    public int power;
    private HeroSkillModel skillModel;
    public int lethal = 0;

    public static Hero createHero(long uid, String hash, Zone zone, int lethal){
        Hero hero = new Hero();
        hero.heroModel = HeroManager.getInstance().getHeroModel(uid, hash, zone);
        hero.heroCf = CharactersConfigManager.getInstance().getHeroConfig(hero.heroModel.id);
        hero.stats = HeroManager.getInstance().getStatsHero(hero.heroModel, zone);
        hero.power = HeroManager.getInstance().getPower(hero.stats);
        hero.skillModel = HeroSkillModel.getFromDB(hero.heroModel, zone);
        hero.lethal = lethal;

        return hero;
    }

    public static Hero createHero(HeroModel heroModel, Zone zone){
        Hero hero = new Hero();
        if(heroModel != null){
            hero.heroModel = heroModel;
            hero.heroCf = CharactersConfigManager.getInstance().getHeroConfig(hero.heroModel.id);
            hero.stats = HeroManager.getInstance().getStatsHero(hero.heroModel, zone);
            hero.power = HeroManager.getInstance().getPower(hero.stats);
            hero.skillModel = HeroSkillModel.getFromDB(hero.heroModel, zone);
            hero.lethal = 0;
        }
        return hero;
    }

    /**
     *
     * @param level
     * @param id
     * @return
     */
    public static Hero createHero(String id, int level, int star, int lethal){
        Hero hero = new Hero();
        hero.heroModel  = HeroModel.createWithoutUser(id, star, level);
        hero.heroCf = CharactersConfigManager.getInstance().getHeroConfig(hero.heroModel.id);
        hero.stats = HeroManager.getInstance().getStatsHeroNormal(hero.heroModel);
        hero.power = HeroManager.getInstance().getPower(hero.stats);
        hero.skillModel = HeroSkillModel.create(id);
        hero.lethal = lethal;

        return hero;
    }

    public static List<Hero> getPlayerTeam(long uid, ETeamType teamType, Zone zone, boolean autoFill){
        List<Hero> team = new ArrayList<>();
        List<HeroModel> teamModel = HeroManager.getInstance().getUserMainListHeroModel(uid, teamType, zone, autoFill);
        for(HeroModel heroModel : teamModel){
            if(heroModel == null){
                team.add(null);
            }else {
                team.add(Hero.createHero(heroModel, zone));
            }
        }
        return team;
    }

    public Stats readStats() {
        return (stats == null) ? readStatsHero() : stats;
    }

    public HeroSkillModel getSkillModel() {
        return skillModel;
    }

    /**
     * Luc chien Hero
     * @return
     */
    public int readPowerHero(){
        return power;
    }

    /**
     * Stats Hero mac do
     * @return
     */
    public Stats readStatsHero(){
        return stats;
    }

    @Override
    public String readID() {
        return heroModel.id;
    }

    @Override
    public Object getSkill() {
        return skillModel;
    }

    @Override
    public int readLevel() {
        return heroModel.readLevel();
    }

    @Override
    public int readStar() {
        return heroModel.star;
    }

    public int readClass() {
        return Integer.valueOf(heroCf.clas);
    }

    public String readElement(){
        return heroCf.element;
    }

    @Override
    public String readKingdom() {
        return heroCf.kingdom;
    }

    @Override
    public int readCharacterType() {
        return characterType;
    }

    /**
     * Mau
     * @return
     */
    @Override
    public float readHP() {
        return readStats().readHp() * (100 + getLethal()) / 100;
    }

    /**
     * Damage vat ly
     * @return
     */
    @Override
    public float readSTR() {
        return readStats().readStrength() * (100 + getLethal()) / 100;
    }

    /**
     * Damage phep
     * @return
     */
    @Override
    public float readINT() {
        return readStats().readIntelligence() * (100 + getLethal()) / 100;
    }

    @Override
    public float readATK() {
        return 0;
    }

    /**
     * Chinh xac
     * @return
     */
    @Override
    public float readDEX() {
        return readStats().readDexterity();
    }

    /**
     * Giap
     * @return
     */
    @Override
    public float readARM() {
        return readStats().readArmor();
    }

    /**
     * Khang phep
     * @return
     */
    @Override
    public float readMR() {
        return readStats().readMagicResistance();
    }

    @Override
    public float readDEF() {
        return 0;
    }

    /**
     * Toc do
     * @return
     */
    @Override
    public float readAGI() {
        return readStats().readAgility();
    }

    /**
     * Ti le chi mang
     * @return
     */
    @Override
    public float readCRIT() {
        return readStats().readCrit();
    }

    /**
     * Damage chi mang
     * @return
     */
    @Override
    public float readCRITBONUS() {
        return readStats().readCritDmg();
    }

    /**
     * Xuyen giap
     * @return
     */
    @Override
    public float readAPEN() {
        return readStats().readArmorPenetration();
    }

    /**
     * Xuyen khang phep
     * @return
     */
    @Override
    public float readMPEN() {
        return readStats().readMagicPenetration();
    }

    @Override
    public float readDPEN() {
        return 0;
    }

    /**
     * Khang hieu ung
     * @return
     */
    @Override
    public float readTEN() {
        return readStats().readTenacity();
    }

    /**
     * Ti le ne
     * @return
     */
    @Override
    public float readELU() {
        return readStats().readElusiveness();
    }

    @Override
    public int getLethal() {
        return lethal;
    }
}
