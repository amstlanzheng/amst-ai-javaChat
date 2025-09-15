package com.amst.ai.agent.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 校区表(School)表实体类
 *
 * @author amstlan
 * @since 2025-08-15 11:26:08
 */
@Data
@TableName("school")
public class SchoolEntity  {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Object id;
    /**
     * 校区名称
     */
    @TableField(value = "name")
    private String name;
    /**
     * 校区所在城市
     */
    @TableField(value = "city")
    private String city;




}

