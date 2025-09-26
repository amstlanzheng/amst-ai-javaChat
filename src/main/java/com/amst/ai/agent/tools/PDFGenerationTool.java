package com.amst.ai.agent.tools;

import cn.hutool.core.io.FileUtil;
import com.amst.ai.agent.contents.FileConstant;
import com.amst.ai.common.utils.MinioUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.amst.ai.agent.contents.FileConstant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PDFGenerationTool {
    
    private final MinioUtil minioUtil;

    @Tool(description = "根据给定内容生成PDF文件存入Minio")
    public String generatePDF(
            @ToolParam(description = "保存生成的PDF文件的文件名") String fileName,
            @ToolParam(description = "要包含在PDF中的内容") String content) {
        // 为文件生成唯一标识符，避免重名
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        String fileDir = FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + uniqueFileName;
        try {
            // 创建目录
            final File mkdir = FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // 使用项目资源目录下的字体文件
                ClassPathResource fontResource = new ClassPathResource("fonts/LXGWWenKaiGBScreen.ttf");
                byte[] fontBytes = FileUtil.readBytes(fontResource.getFile());
                PdfFont font = PdfFontFactory.createFont(fontBytes, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                document.setFont(font);
                // 创建段落
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            //存入minio
            File file = new File(filePath);
            InputStream inputStream = FileUtil.getInputStream(file);
            minioUtil.upload(inputStream, uniqueFileName, "application/pdf");
            FileUtil.del(new File(fileDir));
            // 返回可通过HTTP直接下载的Minio预签名URL
            String downloadUrl = minioUtil.getFileUrl(uniqueFileName);
            return "PDF生成成功，下载链接: " + downloadUrl;
        } catch (IOException e) {
            return "生成PDF时出错: " + e.getMessage();
        } catch (Exception e) {
            log.error("上传PDF文件出错: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}