package com.amst.ai.agent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.amst.ai.common.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class FileOperationTool {

    @Autowired
    private MinioUtil minioUtil;

    @Tool(description = "读取指定文件的文本文件内容")
    public String readFile(@ToolParam(description = "文件名称") String fileName) throws Exception {
        if (!minioUtil.exists(fileName)) {
            return "文件不能为空";
        }
        
        try {
            InputStream download = minioUtil.download(fileName);
            File file = FileUtil.writeBytes(download.readAllBytes(), fileName);
            return FileUtil.readString(file, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "读取文件出错: " + e.getMessage();
        }
    }

    @Tool(description = "列出指定Minio的bucket下的所有文件和文件夹")
    public String listFiles() {
        
        try {
            minioUtil.checkBucketExists();
            List<String> result = minioUtil.listFiles();
            return result.toString();
        } catch (Exception e) {
            return "列出文件出错: " + e.getMessage();
        }
    }


    @Tool(description = "删除文件")
    public String deleteFileOrDirectory(@ToolParam(description = "要删除的文件名称") String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "路径不能为空";
        }
        
        try {

            minioUtil.delete(fileName);
            return "文件删除成功";
        } catch (Exception e) {
            return "删除出错: " + e.getMessage();
        }

    }

    @Tool(description = "写入内容到文件并上传到MinIO，返回文件访问URL")
    public String writeFile(@ToolParam(description = "文件名") String fileName,
                            @ToolParam(description = "要写入的内容") String content) {
        if (!StringUtils.hasText(fileName)) {
            return "文件名不能为空";
        }
        
        try {
            // 将内容转换为字节数组，使用UTF-8编码
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            
            // 上传到MinIO并获取URL
            String fileUrl = minioUtil.upload(bytes, fileName, "text/plain; charset=utf-8");
            
            return "文件上传成功，访问URL: " + fileUrl;
        } catch (Exception e) {
            return "上传文件到MinIO出错: " + e.getMessage();
        }
    }
}