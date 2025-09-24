package com.amst.ai.agent.controller;

import com.amst.ai.common.utils.MinioUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/minio")
@RequiredArgsConstructor
public class MinioController {

    private final MinioUtil minioUtil;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = minioUtil.upload(file);
            result.put("success", true);
            result.put("url", url);
            result.put("message", "文件上传成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> download(@PathVariable String fileName) {
        try {
            InputStream inputStream = minioUtil.download(fileName);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(inputStream);
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "文件下载失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String fileName) {
        Map<String, Object> result = new HashMap<>();
        try {
            minioUtil.delete(fileName);
            result.put("success", true);
            result.put("message", "文件删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "文件删除失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 获取文件URL
     */
    @GetMapping("/url/{fileName}")
    public ResponseEntity<Map<String, Object>> getFileUrl(@PathVariable String fileName) {
        Map<String, Object> result = new HashMap<>();
        try {
            String url = minioUtil.getFileUrl(fileName);
            result.put("success", true);
            result.put("url", url);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "获取文件URL失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}