package com.amst.ai.service.impl;

import com.amst.ai.mapper.AiChatMessageUserMapper;
import com.amst.ai.model.entity.AiChatMessageUser;
import com.amst.ai.service.AiChatMessageUserService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
* @author amstl
* @description 针对表【AI_CHAT_MESSAGE_USER(用户与对话ID的关联关系)】的数据库操作Service实现
* @createDate 2025-08-27 19:20:46
*/
@Service
public class AiChatMessageUserServiceImpl extends ServiceImpl<AiChatMessageUserMapper, AiChatMessageUser>
    implements AiChatMessageUserService {

}




