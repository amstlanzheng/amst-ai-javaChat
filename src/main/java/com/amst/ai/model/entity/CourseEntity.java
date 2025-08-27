package com.amst.ai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学科表(Course)表实体类
 *
 * @author amstlan
 * @since 2025-08-15 11:25:59
 */
@Data
@TableName("course")
public class CourseEntity  {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Object id;
    /**
     * 学科名称
     */
    @TableField(value = "name")
    private String name;
    /**
     * 学历背景要求：0-无，1-初中，2-高中、3-大专、4-本科以上
     */
    @TableField(value = "edu")
    private Integer edu;
    /**
     * 课程类型：编程、设计、自媒体、其它
     */
    @TableField(value = "type")
    private String type;
    /**
     * 课程价格
     */
    @TableField(value = "price")
    private Long price;
    /**
     * 学习时长，单位: 天
     */
    @TableField(value = "duration")
    private Object duration;




}

