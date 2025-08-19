package com.amst.ai.repostory;

import com.amst.ai.entity.AiChatMessageType;
import com.amst.ai.service.AiChatMessageTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class InMemoryChatHistoryRepository implements ChatHistoryRepository{

    private final Map<String, List<String>> chatHistory = new HashMap<>();

    private final AiChatMessageTypeService aiChatMessageTypeService;
    @Override
    public void save(String type, String chatId) {
        QueryWrapper<AiChatMessageType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("message_type",type);
        queryWrapper.eq("conversation_id",chatId);
        List<AiChatMessageType> list1 = aiChatMessageTypeService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list1)){
            AiChatMessageType aiChatMessageType = new AiChatMessageType();
            aiChatMessageType.setMessageType(type);
            aiChatMessageType.setConversationId(chatId);
            aiChatMessageTypeService.save(aiChatMessageType);
        }
    }

    @Override
    public List<String> getChatIds(String type) {
        List<AiChatMessageType> list = aiChatMessageTypeService.list(new QueryWrapper<AiChatMessageType>().orderByDesc("id").eq("message_type",type));
        if (!CollectionUtils.isEmpty(list)){
            return list.stream().map(AiChatMessageType::getConversationId).toList();
        }
        return List.of();
    }
}
