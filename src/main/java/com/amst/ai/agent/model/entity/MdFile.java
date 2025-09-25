package com.amst.ai.agent.model.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文件(MdFile)表实体类
 *
 * @author makejava
 * @since 2025-09-24 15:38:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MdFile extends Model<MdFile> {
    //id
    private Long id;
    //原始文件名
    private String fileName;
    //用户id
    private String conversationId;
    //文件id
    private String fileId;
    //创建时间
    private Date createTime;

    private Date updateTime;
    //是否删除
    private Integer isDeleted;


}

