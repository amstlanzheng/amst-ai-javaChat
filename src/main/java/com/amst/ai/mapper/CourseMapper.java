package com.amst.ai.mapper;

import java.util.List;

import com.amst.ai.entity.CourseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


/**
 * 学科表(Course)表数据库访问层
 *
 * @author amstlan
 * @since 2025-08-15 11:25:59
 */
public interface CourseMapper extends BaseMapper<CourseEntity> {

/**
* 批量新增数据（MyBatis原生foreach方法）
*
* @param entities List<CourseEntity> 实例对象列表
* @return 影响行数
*/
int insertBatch(@Param("entities") List<CourseEntity> entities);

/**
* 批量新增或按主键更新数据（MyBatis原生foreach方法）
*
* @param entities List<CourseEntity> 实例对象列表
* @return 影响行数
* @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
*/
int insertOrUpdateBatch(@Param("entities") List<CourseEntity> entities);

}

