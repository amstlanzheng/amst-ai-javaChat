package com.amst.ai.agent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lanzhs
 */
@Slf4j

public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private String searchApiKey;
    public WebSearchTool(String searchApiKey) {
        this.searchApiKey = searchApiKey;
    }

    @Tool(description = """
    使用百度搜索引擎进行网络搜索。\
    重要：调用此工具时必须提供明确、具体的搜索关键词。\
    如果用户没有提供具体内容，请先询问用户要搜索什么。\
    适用场景：搜索电影、游戏、音乐、新闻、知识问答等。
    """)
    public String searchWeb(
            @ToolParam(description = """
            必须提供的搜索关键词。\
            示例：'2024年最新电影排行榜'、'王者荣耀新英雄'、'周杰伦最新歌曲'。\
            关键词应该具体明确，不能为空或过于简单。
            """) String keyWord) {
        // 检查参数是否为空
        if (!StringUtils.hasText(keyWord)) {
            return "搜索关键词不能为空";
        }
        
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", keyWord);
        paramMap.put("api_key", searchApiKey);
        paramMap.put("engine", "baidu");
        try {
            log.info("正在使用百度搜索 " + keyWord);
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 提取 organic_results 部分
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            if (organicResults == null || organicResults.isEmpty()) {
                return "未找到相关搜索结果";
            }
            List<Object> objects = organicResults.subList(0, Math.min(5, organicResults.size()));
            // 拼接搜索结果为字符串
            return objects.stream().map(obj -> {
                JSONObject tmpJsonObject = (JSONObject) obj;
                return tmpJsonObject.toString();
            }).collect(Collectors.joining(","));
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}