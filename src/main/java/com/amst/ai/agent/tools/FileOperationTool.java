package com.amst.ai.agent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

@Slf4j
@Component
public class FileOperationTool {

    @Tool(description = "读取指定路径的文本文件内容")
    public String readFile(@ToolParam(description = "文件路径") String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return "文件路径不能为空";
        }
        
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return "文件不存在: " + filePath;
            }
            
            return FileUtil.readString(file, Charset.defaultCharset());
        } catch (Exception e) {
            return "读取文件出错: " + e.getMessage();
        }
    }

    @Tool(description = "列出指定目录下的所有文件和文件夹")
    public String listFiles(@ToolParam(description = "目录路径") String dirPath) {
        if (!StringUtils.hasText(dirPath)) {
            return "目录路径不能为空";
        }
        
        try {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                return "目录不存在: " + dirPath;
            }
            
            if (!dir.isDirectory()) {
                return "指定路径不是目录: " + dirPath;
            }
            
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                return "目录为空: " + dirPath;
            }
            
            StringBuilder result = new StringBuilder();
            for (File file : files) {
                result.append(file.getName());
                if (file.isDirectory()) {
                    result.append("/");
                }
                result.append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "列出文件出错: " + e.getMessage();
        }
    }

    @Tool(description = "创建目录")
    public String createDirectory(@ToolParam(description = "要创建的目录路径") String dirPath) {
        if (!StringUtils.hasText(dirPath)) {
            return "目录路径不能为空";
        }
        
        try {
            File dir = new File(dirPath);
            if (dir.exists()) {
                return "目录已存在: " + dirPath;
            }
            
            boolean success = dir.mkdirs();
            if (success) {
                return "目录创建成功: " + dirPath;
            } else {
                return "目录创建失败: " + dirPath;
            }
        } catch (Exception e) {
            return "创建目录出错: " + e.getMessage();
        }
    }

    @Tool(description = "删除文件或目录")
    public String deleteFileOrDirectory(@ToolParam(description = "要删除的文件或目录路径") String path) {
        if (!StringUtils.hasText(path)) {
            return "路径不能为空";
        }
        
        try {
            File file = new File(path);
            if (!file.exists()) {
                return "文件或目录不存在: " + path;
            }
            
            boolean success = FileUtil.del(file);
            if (success) {
                return "删除成功: " + path;
            } else {
                return "删除失败: " + path;
            }
        } catch (Exception e) {
            return "删除出错: " + e.getMessage();
        }
    }

    @Tool(description = "写入内容到文件")
    public String writeFile(@ToolParam(description = "文件路径") String filePath,
                            @ToolParam(description = "要写入的内容") String content) {
        if (!StringUtils.hasText(filePath)) {
            return "文件路径不能为空";
        }
        
        try {
            File file = new File(filePath);
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            FileUtil.writeString(content, file, Charset.defaultCharset());
            return "文件写入成功: " + filePath;
        } catch (Exception e) {
            return "写入文件出错: " + e.getMessage();
        }
    }
}