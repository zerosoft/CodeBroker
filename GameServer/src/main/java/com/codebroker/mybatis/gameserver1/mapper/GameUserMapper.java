package com.codebroker.mybatis.gameserver1.mapper;

import com.codebroker.mybatis.gameserver1.model.GameUser;
import com.codebroker.mybatis.gameserver1.model.GameUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface GameUserMapper {
    /**
     * @mbg.generated generated automatically, do not modify!
     */
    long countByExample(GameUserExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int deleteByExample(GameUserExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int deleteByPrimaryKey(Long uid);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int insert(GameUser record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int insertSelective(GameUser record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    List<GameUser> selectByExample(GameUserExample example, RowBounds rowBounds);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    List<GameUser> selectByExample(GameUserExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    GameUser selectByPrimaryKey(Long uid);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByExampleSelective(@Param("record") GameUser record, @Param("example") GameUserExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByExample(@Param("record") GameUser record, @Param("example") GameUserExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByPrimaryKeySelective(GameUser record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByPrimaryKey(GameUser record);
}