package com.amst.ai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * (CourseReservation)表实体类
 *
 * @author amstlan
 * @since 2025-08-15 11:26:06
 */
@Data
@TableName("course_reservation")
public class CourseReservationEntity  {
    @TableId(value = "id")
    private Integer id;
    /**
     * 预约课程
     */
    @TableField(value = "course")
    private String course;
    /**
     * 学生姓名
     */
    @TableField(value = "student_name")
    private String studentName;
    /**
     * 联系方式
     */
    @TableField(value = "contact_info")
    private String contactInfo;
    /**
     * 预约校区
     */
    @TableField(value = "school")
    private String school;
    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;




}

