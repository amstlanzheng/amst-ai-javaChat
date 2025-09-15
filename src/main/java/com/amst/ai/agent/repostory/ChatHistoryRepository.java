package com.amst.ai.agent.repostory;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ChatHistoryRepository {
    /**
     * 保存会话历史
     * @param type 业务类型
     * @param chatId 会话ID
     */
    void save(String type,String chatId, HttpServletRequest request);

    /**
     * 获取会话历史
     * @param type 业务类型
     */
    List<String> getChatIds(String type, HttpServletRequest request);
}
