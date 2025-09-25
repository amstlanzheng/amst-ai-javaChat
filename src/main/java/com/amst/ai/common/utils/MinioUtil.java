package com.amst.ai.common.utils;

import com.amst.ai.common.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioUtil {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问URL
     */
    public String upload(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        return uploadFile(file.getInputStream(), fileName, file.getContentType());
    }

    /**
     * 上传文件 - 字节数组方式
     *
     * @param bytes       文件字节数组
     * @param fileName    文件名
     * @param contentType 文件类型
     * @return 文件访问URL
     */
    public String upload(byte[] bytes, String fileName, String contentType) throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return uploadFile(inputStream, fileName, contentType);
    }

    /**
     * 上传文件 - InputStream方式
     *
     * @param inputStream 文件流
     * @param fileName    文件名
     * @param contentType 文件类型
     * @return 文件访问URL
     */
    public String upload(InputStream inputStream, String fileName, String contentType) throws Exception {
        return uploadFile(inputStream, fileName, contentType);
    }

    /**
     * 上传文件基础方法
     *
     * @param inputStream 文件流
     * @param fileName    文件名
     * @param contentType 文件类型
     * @return 文件访问URL
     */
    private String uploadFile(InputStream inputStream, String fileName, String contentType) throws Exception {
        try {
            // 检查存储桶是否存在
            checkBucketExists();

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build()
            );

            // 返回文件访问URL
            return getFileUrl(fileName);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new Exception("文件上传失败", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 下载文件
     *
     * @param fileName 文件名
     * @return 文件流
     */
    public InputStream download(String fileName) throws Exception {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            throw new Exception("文件下载失败", e);
        }
    }

    /**
     * 删除文件
     *
     * @param fileName 文件名
     */
    public void delete(String fileName) throws Exception {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            throw new Exception("文件删除失败", e);
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String getFileUrl(String fileName) throws Exception {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .expiry(7, TimeUnit.DAYS) // 7天有效期
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件URL失败: {}", e.getMessage(), e);
            throw new Exception("获取文件URL失败", e);
        }
    }

    /**
     * 检查存储桶是否存在，不存在则创建
     */
    public void checkBucketExists() throws Exception {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .build()
            );

            if (!exists) {
                // 创建存储桶
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(minioConfig.getBucketName())
                                .build()
                );
            }
        } catch (Exception e) {
            log.error("检查或创建存储桶失败: {}", e.getMessage(), e);
            throw new Exception("检查或创建存储桶失败", e);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName 文件名
     * @return 是否存在
     */
    public boolean exists(String fileName) throws Exception {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            throw e;
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", e.getMessage(), e);
            throw new Exception("检查文件是否存在失败", e);
        }
    }

    /**
     * 列出存储桶下的所有文件
     * @return 文件名列表
     */
    public List<String> listFiles() throws Exception {
        try {
            List<String> fileNames = new ArrayList<>();
            
            // 列出存储桶中的所有对象
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .build()
            );
            
            // 遍历结果并添加文件名到列表
            for (Result<Item> result : results) {
                Item item = result.get();
                fileNames.add(item.objectName());
            }
            
            return fileNames;
        } catch (Exception e) {
            log.error("列出文件失败: {}", e.getMessage(), e);
            throw new Exception("列出文件失败", e);
        }
    }

    /**
     * 获取文件访问URL
     *
     * @param fileId 文件ID
     * @return 文件访问URL
     */
    public String getFileURL(String fileId) {
        // 获取文件访问URL
        String fileURL;
        try {
            fileURL = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(fileId)
                            .expiry(7, TimeUnit.DAYS) // 7天有效期
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fileURL;

    }
}