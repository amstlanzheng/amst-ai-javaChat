package com.amst.ai.agent.controller;

import com.amst.ai.agent.repostory.ChatHistoryRepository;
import com.amst.ai.agent.tools.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    private final ChatClient chatMultiClient;

    private final ChatHistoryRepository chatHistoryRepository;


    @Resource
    private ToolCallback[] allTools;

    @PostMapping(value = "/chat")
    public Flux<String> chat(@RequestParam String prompt, @RequestParam  String chatId, @RequestParam(required = false) List<MultipartFile> files, HttpServletRequest request) {
        //保存会话Id
        chatHistoryRepository.save("chat",chatId,  request);

        // 创建会话
        if (CollectionUtils.isEmpty( files)){
            //普通聊天
            return textChat(prompt,chatId);
        }
        return multipartChat(prompt, chatId, files);

    }

    /**
     * 多模态上传
     * @param question  问题
     * @param chatId 会话Id
     * @param files 文件集合
     * @return
     */
    private Flux<String> multipartChat(String question, String chatId, List<MultipartFile> files) {
        // 文件处理
        // 文件处理
        List<Media> media = files.stream()
                .map(file -> {
                    // 修复：正确获取文件的 MIME 类型，而不是使用文件名
                    String mimeType = file.getContentType();
                    if (mimeType == null || mimeType.isEmpty()) {
                        // 如果无法从文件获取 MIME 类型，则使用默认值
                        mimeType = "application/octet-stream";
                    }
                    return new Media(MimeTypeUtils.parseMimeType(mimeType), file.getResource());
                })
                .toList();
        return chatMultiClient.prompt()
                .user(p -> p.text(question).media(media.toArray(Media[]::new)))
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }

    private Flux<String> textChat(String question, String chatId) {
        return chatClient.prompt()
                .user(question)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }
}