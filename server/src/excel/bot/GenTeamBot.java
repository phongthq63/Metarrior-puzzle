package excel.bot;

import com.bamisu.gamelib.utils.Utils;
import com.bamisu.log.gameserver.datamodel.hero.entities.HeroModel;
import com.bamisu.log.gameserver.module.bot.config.TeamBotConfig;
import com.bamisu.log.gameserver.module.bot.config.entities.TeamBotVO;
import com.bamisu.log.gameserver.module.characters.CharactersConfigManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GenTeamBot {

    public static void main(String[] args){
        HeroModel T1014 = HeroModel.createWithoutUser("T1014", 3, 1);
        HeroModel T1015 = HeroModel.createWithoutUser("T1015", 3, 1);
        HeroModel T1022 = HeroModel.createWithoutUser("T1022", 3, 1);
        HeroModel T1025 = HeroModel.createWithoutUser("T1025", 3, 1);
        HeroModel T1026 = HeroModel.createWithoutUser("T1026", 3, 1);
        HeroModel T1028 = HeroModel.createWithoutUser("T1028", 3, 1);
        HeroModel T1031 = HeroModel.createWithoutUser("T1031", 3, 1);
        HeroModel T1033 = HeroModel.createWithoutUser("T1033", 3, 1);
        HeroModel T1034 = HeroModel.createWithoutUser("T1034", 3, 1);
        HeroModel T1040 = HeroModel.createWithoutUser("T1040", 3, 1);
        HeroModel T1042 = HeroModel.createWithoutUser("T1042", 3, 1);
        HeroModel T1043 = HeroModel.createWithoutUser("T1043", 3, 1);
        HeroModel T1051 = HeroModel.createWithoutUser("T1051", 3, 1);
        HeroModel T1053 = HeroModel.createWithoutUser("T1053", 3, 1);
        HeroModel T1055 = HeroModel.createWithoutUser("T1053", 3, 1);
        HeroModel T1057 = HeroModel.createWithoutUser("T1057", 3, 1);

        TeamBotVO team1 = TeamBotVO.create(Arrays.asList(T1033, T1026, T1034, T1053, T1051));
        TeamBotVO team2 = TeamBotVO.create(Arrays.asList(T1033, T1026, T1034, T1031, T1014));
        TeamBotVO team3 = TeamBotVO.create(Arrays.asList(T1033, T1026, T1034, T1040, T1015));
        TeamBotVO team4 = TeamBotVO.create(Arrays.asList(T1033, T1026, T1034, T1040, T1025));
        TeamBotVO team5 = TeamBotVO.create(Arrays.asList(T1043, T1026, T1034, T1033, T1014));
        TeamBotVO team6 = TeamBotVO.create(Arrays.asList(T1042, T1028, T1057, T1031, T1014));
        TeamBotVO team7 = TeamBotVO.create(Arrays.asList(T1022, T1026, T1034, T1033, T1051));
        TeamBotVO team8 = TeamBotVO.create(Arrays.asList(T1033, T1055, T1034, T1053, T1051));
        TeamBotVO team9 = TeamBotVO.create(Arrays.asList(T1028, T1057, T1053, T1014, T1051));
        TeamBotVO team10 = TeamBotVO.create(Arrays.asList(T1042, T1028, T1040, T1015, T1025));
        TeamBotVO team11 = TeamBotVO.create(Arrays.asList(T1057, T1028, T1055, T1051, T1053));

        List<TeamBotVO> listGen = new ArrayList<>();
        List<HeroModel> listHeroModel;
        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team1.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team2.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team3.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team4.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team5.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team6.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team7.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team8.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team9.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team10.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        for(int star = 3; star <= 10; star++){
            int maxLevel = CharactersConfigManager.getInstance().getMaxLevelHeroConfig(star);

            for(int level = 1; level <= maxLevel; level++){
                listHeroModel = new ArrayList<>();

                for(HeroModel heroModel : team11.team){
                    int start = level - 4;
                    int end = level + 4;
                    int levelRd = Utils.randomInRange(start, end);
                    if(levelRd <= 0) levelRd = 1;
                    if(levelRd > maxLevel) levelRd = maxLevel;

                    listHeroModel.add(HeroModel.createWithoutUser(heroModel.id, star, levelRd));
                }

                listGen.add(TeamBotVO.create(listHeroModel));
            }
        }

        listGen = listGen.stream().sorted((o1, o2) -> o1.power - o2.power).collect(Collectors.toList());

        TeamBotConfig cf = new TeamBotConfig();
        cf.list = listGen;
        Utils.saveToFile(Utils.toJson(cf), "C:\\Users\\Quach Thanh Phong\\Downloads\\" + "test.json");
    }
}
