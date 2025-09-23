package com.amst.ai.agent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class ResourceDownloadTool {

    @Tool(description = "从指定URL下载资源文件")
    public String downloadResource(@ToolParam(description = "资源URL") String url,
                                   @ToolParam(description = "保存文件名，可选") String filename) {
        if (!StringUtils.hasText(url)) {
            return "URL不能为空";
        }

        try {
            // 生成文件名
            if (!StringUtils.hasText(filename)) {
                filename = "download_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".tmp";
            }

            // 构造保存路径
            String savePath = "downloads/" + filename;

            // 创建目录
            File saveDir = new File("downloads");
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }

            // 下载文件
            HttpUtil.downloadFile(url, FileUtil.file(savePath));

            return "文件下载成功，保存路径: " + savePath;
        } catch (Exception e) {
            return "下载文件失败: " + e.getMessage();
        }
    }
}