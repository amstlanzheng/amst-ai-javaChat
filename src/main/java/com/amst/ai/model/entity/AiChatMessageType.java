package com.amst.ai.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("AI_CHAT_MESSAGE_TYPES")
public class AiChatMessageType {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    
    private String conversationId;
    
    private String messageType;
    
    private Integer isDeleted;
}