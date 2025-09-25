package com.amst.ai.agent.repostory;

import com.amst.ai.agent.model.entity.MdFile;
import com.amst.ai.agent.service.MdFileService;
import com.amst.ai.common.enums.ErrorCode;
import com.amst.ai.common.exception.BusinessException;
import com.amst.ai.common.utils.MinioUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalPdfFileRepository implements FileRepository {

    private final VectorStore vectorStore;

    // 会话id 与 文件名的对应关系，方便查询会话历史时重新加载文件
    private final Properties chatFiles = new Properties();
    
    private final MdFileService mdFileService;
    
    private final MinioUtil minioUtil;

    @Override
    public boolean save(String chatId, Resource resource) {

        //1.生成随机文件名
        String filename = UUID.randomUUID().toString();
        
        // 2.获取源文件名
        String filenameOrigin = resource.getFilename();
        //3.获取源文件的Input Stream  保存文件
        try (InputStream inputStream = resource.getInputStream()){
            // 根据文件扩展名设置正确的内容类型
            String contentType = "application/octet-stream"; // 默认类型
            if (filenameOrigin != null) {
                if (filenameOrigin.endsWith(".md")) {
                    contentType = "text/markdown; charset=utf-8";
                } else if (filenameOrigin.endsWith(".pdf")) {
                    contentType = "application/pdf";
                }
            }
            minioUtil.upload(inputStream, filename, contentType);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to save resource.");
        }
        // 存入数据库
        final MdFile mdFile = new MdFile();
        mdFile.setConversationId(chatId);
        mdFile.setFileName(filenameOrigin);
        mdFile.setFileId(filename);
        mdFile.insertOrUpdate();
        
        // 3.保存映射关系
        chatFiles.put(chatId, filename);
        return true;
    }

    @Override
    public Resource getFile(String chatId) {
        if (!chatFiles.containsKey(chatId)) {
            return null;
        }
        QueryWrapper<MdFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", chatId);
        final List<MdFile> list = mdFileService.list(queryWrapper);
        if (list.isEmpty()) {
            return null;
        }
        try {
            InputStream download = minioUtil.download(list.getFirst().getFileId());
            // 将InputStream转换为字节数组以支持多次读取
            byte[] bytes = download.readAllBytes();
            String filename = list.getFirst().getFileName();
            return new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
        } catch (Exception e) {
            log.error("Failed to get resource.", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to get resource.");
        }
    }

    @Override
    public String getFileURL(String chatId, Map<String, String> fileName) {
        if (!chatFiles.containsKey(chatId)) {
            return null;
        }
        QueryWrapper<MdFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("conversation_id", chatId);
        final List<MdFile> list = mdFileService.list(queryWrapper);
        if (list.isEmpty()) {
            return null;
        }
        fileName.put("fileName", list.getFirst().getFileName());
        return minioUtil.getFileURL(list.getFirst().getFileId());

    }

    @PostConstruct
    private void init() {
        FileSystemResource pdfResource = new FileSystemResource("chat-pdf.properties");
        if (pdfResource.exists()) {
            try {
                chatFiles.load(new BufferedReader(new InputStreamReader(pdfResource.getInputStream(), StandardCharsets.UTF_8)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 注意：PgVectorStore使用数据库存储向量数据，不需要从文件加载
    }

    @PreDestroy
    private void persistent() {
        try {
            chatFiles.store(new FileWriter("chat-pdf.properties"), LocalDateTime.now().toString());
            // 注意：PgVectorStore使用数据库存储向量数据，不需要保存到文件
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}