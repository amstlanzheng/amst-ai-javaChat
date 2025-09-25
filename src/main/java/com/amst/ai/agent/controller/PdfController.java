package com.amst.ai.agent.controller;

import com.amst.ai.agent.repostory.ChatHistoryRepository;
import com.amst.ai.agent.repostory.FileRepository;
import com.amst.ai.common.enums.ErrorCode;
import com.amst.ai.common.exception.BusinessException;
import com.amst.ai.common.result.Result;
import com.amst.ai.user.contents.UserContents;
import com.amst.ai.user.model.entity.SysUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lanzhs
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/md")
public class PdfController {

    private final FileRepository fileRepository;

    private final VectorStore vectorStore;

    private final ChatClient pdfChatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    /**
     * 文件上传
     */
    @RequestMapping("/upload/{chatId}")
    public Result<String> uploadPdf(@PathVariable String chatId, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        //登录校验
        SysUser surer = (SysUser) request.getSession().getAttribute(UserContents.USER_LOGIN_STATE);
        if (surer == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        try {
            // 1. 校验文件是否为MD格式
            if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith(".md")) {
                return Result.fail("请上传MARKDOWN文件！");
            }
            // 文件大小不能超过60k
            if (file.getSize() > 1024 * 60) {
                return Result.fail("文件过大，请上传60KB以下的文件！");
            }
            // 2.保存文件
            boolean success = fileRepository.save(chatId, file.getResource());
            if(! success) {
                return Result.fail("保存文件失败！");
            }
            // 3.写入向量库
            this.writeToVectorStore(file.getResource());
            return Result.ok();
        } catch (Exception e) {
            log.error("Failed to upload MD.", e);
            return Result.fail("上传文件失败！");
        }
    }

    /**
     * 文件下载
     */
    @GetMapping("/file/{chatId}")
    public Result<Map<String, String>> download(@PathVariable("chatId") String chatId, HttpServletRequest request) {
        //登录校验
        SysUser surer = (SysUser) request.getSession().getAttribute(UserContents.USER_LOGIN_STATE);
        if (surer == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录");
        }
        // 1.读取文件
        Map<String, String> res = new HashMap<>();
        String resource = fileRepository.getFileURL(chatId, res);
        res.put("fileUrl", resource);

        // 3.返回文件
        return Result.ok(res);
    }

    private void writeToVectorStore(Resource resource) {
        // 1.创建Markdown的读取器
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false)
                .build();
        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);

        // 2.读取Markdown文档，拆分为Document
        List<Document> documents = reader.get();
        // 3.为每个文档添加文件名元数据
        String filename = resource.getFilename();
        if (filename != null) {
            for (Document document : documents) {
                document.getMetadata().put("file_name", filename);
            }
        }
        // 4.写入向量库
        vectorStore.add(documents);
    }

    @RequestMapping("/chat")
    public Flux<String> chat(@RequestParam("chatId") String chatId, @RequestParam("prompt") String prompt, HttpServletRequest request) {
        //找到会话文件
        Resource resource = fileRepository.getFile(chatId);
        if (resource == null) {
            return Flux.just("请先上传MD文件！");
        }
        // 1.保存会话内容
        chatHistoryRepository.save("md", chatId, request);
        // 2.获取会话内容
        String filename = resource.getFilename();
        String filterExpression = filename != null ? "file_name == '" + filename + "'" : null;
        return pdfChatClient.prompt()
                .user(prompt)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(a -> {
                    if (filterExpression != null) {
                        a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, filterExpression);
                    }
                })
                .stream()
                .content();
    }

}