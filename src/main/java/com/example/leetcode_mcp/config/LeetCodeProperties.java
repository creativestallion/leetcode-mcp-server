package com.example.leetcode_mcp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "leetcode")
public class LeetCodeProperties {
    private String username;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
