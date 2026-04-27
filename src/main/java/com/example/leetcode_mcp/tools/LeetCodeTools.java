package com.example.leetcode_mcp.tools;

import com.example.leetcode_mcp.client.LeetCodeClient;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class LeetCodeTools {

    private final LeetCodeClient client;

    public LeetCodeTools(LeetCodeClient client) {
        this.client = client;
    }

    @Tool(description = """
            Returns the LeetCode profile for the user including their global ranking,
            reputation, star rating, and total accepted submissions broken down by difficulty
            (Easy, Medium, Hard). Use this when asked about the user's overall profile or ranking.
            """)
    public String getUserProfile() {
        return client.getUserProfile();
    }

    @Tool(description = """
            Returns the number of LeetCode problems solved broken down by difficulty:
            Easy, Medium, and Hard. Also returns the total accepted submission count.
            Use this when asked how many problems have been solved overall or by difficulty.
            """)
    public String getProblemStats() {
        return client.getProblemStats();
    }

    @Tool(description = """
            Returns the last N accepted LeetCode submissions for the user.
            Each submission includes the problem title, slug, programming language, and timestamp.
            Use this when asked about recent activity, recent submissions, or what problems
            were solved recently. Default limit is 10, maximum is 20.
            """)
    public String getRecentSubmissions(int limit) {
        if (limit < 1) limit = 10;
        if (limit > 20) limit = 20;
        return client.getRecentSubmissions(limit);
    }

    @Tool(description = """
            Returns the LeetCode contest history for the user including their contest rating,
            global ranking among contest participants, total participants, top percentage,
            and number of contests attended. Use this when asked about contest performance
            or competitive programming rating.
            """)
    public String getContestHistory() {
        return client.getContestHistory();
    }
}
