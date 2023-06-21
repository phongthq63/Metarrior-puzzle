package com.bamisu.log.gameserver.module.arena.config.entities;

import java.util.List;
import java.util.stream.Collectors;

public class SearchFightArenaVO {
    public List<Integer> range;
    public int count;

    public static SearchFightArenaVO create(SearchFightArenaVO vo){
        SearchFightArenaVO searchFightArenaVO = new SearchFightArenaVO();
        searchFightArenaVO.range = vo.range.parallelStream().map(Integer::new).collect(Collectors.toList());
        searchFightArenaVO.count = vo.count;

        return searchFightArenaVO;
    }
}
