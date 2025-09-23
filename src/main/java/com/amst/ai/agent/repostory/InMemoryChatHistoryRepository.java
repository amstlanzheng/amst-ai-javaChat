package com.amst.ai.agent.repostory;

import com.amst.ai.common.enums.ErrorCode;
import com.amst.ai.user.contents.UserContents;
import com.amst.ai.common.exception.BusinessException;
import com.amst.ai.agent.model.entity.AiChatMessageType;
import com.amst.ai.agent.model.entity.AiChatMessageUser;
import com.amst.ai.user.model.entity.SysUser;
import com.amst.ai.agent.service.AiChatMessageTypeService;
import com.amst.ai.agent.service.AiChatMessageUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class InMemoryChatHistoryRepository implements ChatHistoryRepository{

    private final Map<String, List<String>> chatHistory = new HashMap<>();

    private final AiChatMessageTypeService aiChatMessageTypeService;

    private final AiChatMessageUserService aiChatMessageUserService;


    @Override
    public void save(String type, String chatId, HttpServletRequest request) {
        // 从session中获取用户信息
        SysUser sysUser = (SysUser) request.getSession().getAttribute(UserContents.USER_LOGIN_STATE);
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        // 根据对话类型和会话ID查询
        QueryWrapper<AiChatMessageType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("message_type",type);
        queryWrapper.eq("conversation_id",chatId);
        List<AiChatMessageType> list1 = aiChatMessageTypeService.list(queryWrapper);
        // 如果不存在，则创建
        if (CollectionUtils.isEmpty(list1)){
            AiChatMessageType aiChatMessageType = new AiChatMessageType();
            aiChatMessageType.setMessageType(type);
            aiChatMessageType.setConversationId(chatId);
            aiChatMessageTypeService.save(aiChatMessageType);
        }

        QueryWrapper<AiChatMessageUser> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("conversation_id",chatId);
        queryWrapper1.eq("user_id",sysUser.getId());
        List<AiChatMessageUser> list = aiChatMessageUserService.list(queryWrapper1);
        if (CollectionUtils.isEmpty(list)){
            AiChatMessageUser aiChatMessageUser = new AiChatMessageUser();
            aiChatMessageUser.setConversationId(chatId);
            aiChatMessageUser.setUserId(sysUser.getId());
            aiChatMessageUserService.save(aiChatMessageUser);
        }


    }

    @Override
    public List<String> getChatIds(String type, HttpServletRequest request) {
        SysUser sysUser = (SysUser) request.getSession().getAttribute(UserContents.USER_LOGIN_STATE);
        if (sysUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        List<AiChatMessageType> list = aiChatMessageTypeService.list(new QueryWrapper<AiChatMessageType>().orderByDesc("id").eq("message_type",type));

        if (!CollectionUtils.isEmpty(list)){
            List<String> chatIds = list.stream().map(AiChatMessageType::getConversationId).toList();
            List<AiChatMessageUser> list1 = aiChatMessageUserService.list(new QueryWrapper<AiChatMessageUser>().eq("user_id", sysUser.getId()).in("conversation_id", chatIds).orderByDesc("id"));
            if (!CollectionUtils.isEmpty(list1)){
                return list1.stream().map(AiChatMessageUser::getConversationId).toList();
            }
        }
        return List.of();
    }
}
