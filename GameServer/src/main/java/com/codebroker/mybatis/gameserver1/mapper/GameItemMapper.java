package com.codebroker.mybatis.gameserver1.mapper;

import com.codebroker.mybatis.gameserver1.model.GameItem;
import com.codebroker.mybatis.gameserver1.model.GameItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface GameItemMapper {
    /**
     * @mbg.generated generated automatically, do not modify!
     */
    long countByExample(GameItemExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int deleteByExample(GameItemExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int deleteByPrimaryKey(Long sid);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int insert(GameItem record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int insertSelective(GameItem record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    List<GameItem> selectByExample(GameItemExample example, RowBounds rowBounds);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    List<GameItem> selectByExample(GameItemExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    GameItem selectByPrimaryKey(Long sid);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByExampleSelective(@Param("record") GameItem record, @Param("example") GameItemExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByExample(@Param("record") GameItem record, @Param("example") GameItemExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByPrimaryKeySelective(GameItem record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByPrimaryKey(GameItem record);
}