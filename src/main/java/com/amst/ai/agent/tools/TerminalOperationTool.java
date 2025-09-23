package com.amst.ai.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class TerminalOperationTool {

    @Tool(description = "执行命令或者脚本")
    public String executeCommand(@ToolParam(description = "Command to execute") String command) {
        //判断操作系统
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return executeWindowsCommand(command);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return executeTerminalCommand(command);
        } else {
            return "不支持的操作系统";
        }
    }


    public String executeTerminalCommand(String command) {
        if (!StringUtils.hasText(command)) {
            return "命令不能为空";
        }
        
        StringBuilder output = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }



    public String executeWindowsCommand(String command) {
        if (!StringUtils.hasText(command)) {
            return "命令不能为空";
        }
        
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", command);
            Process process = processBuilder.start();
            
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            output.append("Error executing command: ").append(e.getMessage());
        }
        return output.toString();
    }
}