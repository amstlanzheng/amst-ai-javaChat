package com.amst.ai.agent.controller;

import com.amst.ai.agent.repostory.ChatHistoryRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class CustomerController {

    private final ChatClient courseChatClient;

    private final ChatHistoryRepository chatHistoryRepository;



    @GetMapping(value = "/service")
    public Flux<String> chat(@RequestParam String prompt, String chatId, HttpServletRequest request) {
        //保存会话Id
        chatHistoryRepository.save("service",chatId,  request);

        // 创建会话
        return courseChatClient.prompt()
                .user(prompt)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .stream()
                .content();
    }


}
