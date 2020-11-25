package com.codebroker.mybatis.gameserver1.mapper;

import com.codebroker.mybatis.gameserver1.model.GameItem;
import com.codebroker.mybatis.gameserver1.model.GameItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GameItemMapper {
    long countByExample(GameItemExample example);

    int deleteByExample(GameItemExample example);

    int deleteByPrimaryKey(Long sid);

    int insert(GameItem record);

    int insertSelective(GameItem record);

    List<GameItem> selectByExample(GameItemExample example);

    GameItem selectByPrimaryKey(Long sid);

    int updateByExampleSelective(@Param("record") GameItem record, @Param("example") GameItemExample example);

    int updateByExample(@Param("record") GameItem record, @Param("example") GameItemExample example);

    int updateByPrimaryKeySelective(GameItem record);

    int updateByPrimaryKey(GameItem record);
}