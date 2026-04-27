package com.example.leetcode_mcp.client;


import com.example.leetcode_mcp.config.LeetCodeProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class LeetCodeClient {

    private final WebClient webClient;
    private final LeetCodeProperties properties;

    public LeetCodeClient(LeetCodeProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder()
                .baseUrl("https://leetcode.com/graphql")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Referer", "https://leetcode.com")
                .build();
    }

    public String getUsername() {
        return properties.getUsername();
    }

//    public String query(final String graphqlQuery) {
//        var body = "{\"query\": \"" + graphqlQuery.replace("\"", "\\\"").replace("\n", " ") + "\"}";
//        return webClient.post()
//                .bodyValue(body)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }

    public String getUserProfile() {
        var query = """
            {
              matchedUser(username: "%s") {
                username
                profile {
                  realName
                  ranking
                  reputation
                  starRating
                }
                submitStats {
                  acSubmissionNum {
                    difficulty
                    count
                  }
                }
              }
            }
            """.formatted(properties.getUsername());
        return rawQuery(query);
    }

    public String getProblemStats() {
        var query = """
            {
              matchedUser(username: "%s") {
                submitStats {
                  acSubmissionNum {
                    difficulty
                    count
                  }
                }
              }
            }
            """.formatted(properties.getUsername());
        return rawQuery(query);
    }

    public String getRecentSubmissions(int limit) {
        var query = """
            {
              recentAcSubmissionList(username: "%s", limit: %d) {
                title
                titleSlug
                timestamp
                lang
              }
            }
            """.formatted(properties.getUsername(), limit);
        return rawQuery(query);
    }

    public String getContestHistory() {
        var query = """
            {
              userContestRanking(username: "%s") {
                rating
                globalRanking
                totalParticipants
                topPercentage
                attendedContestsCount
              }
            }
            """.formatted(properties.getUsername());
        return rawQuery(query);
    }

    private String rawQuery(String graphqlQuery) {
        var body = "{\"query\": \"" + graphqlQuery.replace("\"", "\\\"").replace("\n", " ") + "\"}";
        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
