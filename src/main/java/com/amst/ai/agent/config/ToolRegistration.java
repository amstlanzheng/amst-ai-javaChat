package com.amst.ai.agent.config;

import com.amst.ai.agent.tools.*;
import com.amst.ai.common.utils.MinioUtil;
import jakarta.annotation.Resource;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lanzhs
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Resource
    private MinioUtil minioUtil;


    @Bean
    public FileOperationTool fileOperationTool() {
        return new FileOperationTool();
    }
    
    @Bean
    public WebSearchTool webSearchTool() {
        return new WebSearchTool(searchApiKey);
    }
    
    @Bean
    public WebScrapingTool webScrapingTool() {
        return new WebScrapingTool();
    }
    
    @Bean
    public ResourceDownloadTool resourceDownloadTool() {
        return new ResourceDownloadTool();
    }
    
    @Bean
    public TerminalOperationTool terminalOperationTool() {
        return new TerminalOperationTool();
    }
    
    @Bean
    public PDFGenerationTool pdfGenerationTool() {
        return new PDFGenerationTool(minioUtil);
    }
    
    @Bean
    public TerminateTool terminateTool() {
        return new TerminateTool();
    }


    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool(minioUtil);
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool(minioUtil);
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool(minioUtil);
        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool
        );
    }
}