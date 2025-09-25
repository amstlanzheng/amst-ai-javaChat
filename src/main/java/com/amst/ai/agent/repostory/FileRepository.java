package com.amst.ai.agent.repostory;

import org.springframework.core.io.Resource;

import java.util.Map;

public interface FileRepository {
    /**
     * 保存文件,还要记录chatId与文件的映射关系
     * @param chatId 会话id
     * @param resource 文件
     * @return 上传成功，返回true； 否则返回false
     */
    boolean save(String chatId, Resource resource);

    /**
     * 根据chatId获取文件
     * @param chatId 会话id
     * @return 找到的文件
     */
    Resource getFile(String chatId);

    /**
     * 根据chatId获取文件URL
     * @param chatId 会话id
     *               、文件名
     * @return 文件URL
     */
    String getFileURL(String chatId, Map<String, String> fileName);
}