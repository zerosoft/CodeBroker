package com.codebroker.mybatis.global.mapper;

import java.util.List;

import com.codebroker.mybatis.global.model.UserAcccount;
import com.codebroker.mybatis.global.model.UserAcccountExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface UserAcccountMapper {
    /**
     * @mbg.generated generated automatically, do not modify!
     */
    long countByExample(UserAcccountExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int deleteByExample(UserAcccountExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int deleteByPrimaryKey(Long sid);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int insert(UserAcccount record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int insertSelective(UserAcccount record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    List<UserAcccount> selectByExample(UserAcccountExample example, RowBounds rowBounds);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    List<UserAcccount> selectByExample(UserAcccountExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    UserAcccount selectByPrimaryKey(Long sid);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByExampleSelective(@Param("record") UserAcccount record, @Param("example") UserAcccountExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByExample(@Param("record") UserAcccount record, @Param("example") UserAcccountExample example);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByPrimaryKeySelective(UserAcccount record);

    /**
     * @mbg.generated generated automatically, do not modify!
     */
    int updateByPrimaryKey(UserAcccount record);
}