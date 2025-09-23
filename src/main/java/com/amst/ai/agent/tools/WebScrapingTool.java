package com.amst.ai.agent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author lanzhs
 */
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class WebScrapingTool {

    @Tool(description = "根据提供的URL链接抓取对应网页的HTML内容。当需要获取某个网页的最新信息、查看其源代码或分析页面结构时使用此工具。")
    public String scrapeWebPage(@ToolParam(description = "需要抓取的网页完整URL地址，必须以http://或https://开头", required = true) String url) {
        // 检查参数是否为空
        if (!StringUtils.hasText(url)) {
            return "URL不能为空";
        }
        
        try {
            // 添加超时、User-Agent等配置，模拟真实浏览器访问:cite[2]:cite[6]
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                    .timeout(30000) // 30秒超时
                    .get();

            // 获取页面标题和正文文本作为核心信息，而非全部HTML，有时更有效
            String title = doc.title();
            String bodyText = doc.body().text();

            // 返回结构化信息，避免过于冗长的HTML
            return String.format("网页标题: %s%n网页内容摘要: %s",
                    title,
                    bodyText.length() > 500 ? bodyText.substring(0, 500) + "..." : bodyText);

        } catch (IOException e) {
            return "抓取网页时出现错误: " + e.getMessage();
        }
    }
}