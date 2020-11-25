package com.codebroker.mybatis.gameserver1.mapper;

import com.codebroker.mybatis.gameserver1.model.GameUser;
import com.codebroker.mybatis.gameserver1.model.GameUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface GameUserMapper {
    long countByExample(GameUserExample example);

    int deleteByExample(GameUserExample example);

    int deleteByPrimaryKey(Long uid);

    int insert(GameUser record);

    int insertSelective(GameUser record);

    List<GameUser> selectByExample(GameUserExample example);

    GameUser selectByPrimaryKey(Long uid);

    int updateByExampleSelective(@Param("record") GameUser record, @Param("example") GameUserExample example);

    int updateByExample(@Param("record") GameUser record, @Param("example") GameUserExample example);

    int updateByPrimaryKeySelective(GameUser record);

    int updateByPrimaryKey(GameUser record);
}