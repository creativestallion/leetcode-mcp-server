# CP Stats MCP Server

A [Model Context Protocol (MCP)](https://modelcontextprotocol.io) server built with **Java 21** and **Spring Boot** that exposes competitive programming and developer profile data as tools for LLMs like Claude Desktop and Cursor.

Ask Claude things like:
- _"How many medium problems have I solved on LeetCode?"_
- _"What's my Codeforces contest rating?"_
- _"Compare LeetCode stats for prashuchaudhry and neal_wu"_
- _"Compare Codeforces ratings for tourist and benq"_
- _"Show me creativestallion's top GitHub repositories"_
- _"Compare GitHub profiles for torvalds and gvanrossum"_

---

[//]: # (## Demo)

[//]: # ()
[//]: # (> _Add your GIF here_)

[//]: # ()
[//]: # (---)

## Architecture

```
Claude Desktop
     │
     │  stdio (JSON-RPC)
     ▼
Spring Boot MCP Server
     ├──► LeetCode GraphQL API
     ├──► Codeforces REST API
     └──► GitHub REST API
```

- Claude Desktop launches the JAR as a subprocess
- Communication happens over **stdio** using the MCP protocol
- All APIs are **public and unauthenticated**
- Multi-user queries run in **parallel using Java 21 virtual threads**

---

## Supported Platforms

| Platform | Type | Status |
|----------|------|--------|
| LeetCode | Unofficial GraphQL | ✅ Supported |
| Codeforces | Official REST API | ✅ Supported |
| GitHub | Official REST API | ✅ Supported |
| More | — | 🔜 Roadmap |

---

## Tools Exposed

### LeetCode
| Tool | Description | Multi-user |
|------|-------------|------------|
| `getUserProfile` | Global ranking, reputation, star rating, solve counts | ✅ parallel |
| `getProblemStats` | Easy / Medium / Hard breakdown | ✅ parallel |
| `getRecentActivity` | Last N accepted submissions with language and timestamp | ❌ single user |
| `getContestHistory` | Contest rating, global rank, top %, contests attended | ✅ parallel |

### Codeforces
| Tool | Description | Multi-user |
|------|-------------|------------|
| `getCodeforcesProfile` | Rating, rank, name | ✅ parallel |
| `getCodeforcesProblemStats` | Accepted submission stats | ✅ parallel |
| `getCodeforcesRecentActivity` | Last N submissions with verdict and language | ❌ single user |
| `getCodeforcesContestHistory` | Full contest rating history | ✅ parallel |

### GitHub
| Tool | Description | Multi-user |
|------|-------------|------------|
| `getGitHubProfile` | Name, bio, public repos, followers, following | ✅ parallel |
| `getGitHubRepositories` | Top 10 repos by stars with language and forks | ✅ parallel |
| `getGitHubRecentActivity` | Last N public events — pushes, PRs, issues | ❌ single user |

---

## Tech Stack

- Java 21 (virtual threads for parallel API calls)
- Spring Boot 3.5.x
- Spring AI 1.0.1 — MCP Server starter
- Spring WebFlux — WebClient for all API calls
- Docker

---

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- [Claude Desktop](https://claude.ai/download)

### 1. Clone and build

```bash
git clone https://github.com/creativestallion/leetcode-mcp-server.git
cd leetcode-mcp-server
mvn clean package -DskipTests
```

### 2. Configure Claude Desktop

Open your Claude Desktop config file:

```bash
# Mac
open ~/Library/Application\ Support/Claude/
```

Edit `claude_desktop_config.json` and add:

```json
{
  "mcpServers": {
    "cp-stats": {
      "command": "java",
      "args": [
        "-jar",
        "/absolute/path/to/leetcode-mcp-server/target/leetcode-mcp-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

### 3. Restart Claude Desktop

Fully quit (Cmd+Q) and reopen. Look for the hammer 🔨 icon in the chat input — that confirms the MCP server is connected.

### 4. Try it

```
Compare LeetCode problem stats for prashuchaudhry and neal_wu
What's the Codeforces rating for tourist?
Compare GitHub profiles for torvalds and gvanrossum
Show recent activity for creativestallion on GitHub
Compare contest history for tourist and benq on Codeforces
```

---

## Run with Docker

```bash
docker build -t cp-stats-mcp-server .
```

Update your Claude Desktop config to use Docker:

```json
{
  "mcpServers": {
    "cp-stats": {
      "command": "docker",
      "args": [
        "run",
        "--rm",
        "-i",
        "cp-stats-mcp-server"
      ]
    }
  }
}
```

---

## Development

### Test individual platform clients

```bash
# LeetCode
mvn spring-boot:run -Dspring-boot.run.profiles=test-client

# Codeforces
mvn spring-boot:run -Dspring-boot.run.profiles=test-cf

# GitHub
mvn spring-boot:run -Dspring-boot.run.profiles=test-gh
```

### Project structure

```
src/main/java/com/example/leetcode_mcp/
├── LeetcodeMcpApplication.java              # Entry point, tool registration
├── client/
│   ├── PlatformClient.java                  # Base interface (profile, recent activity)
│   ├── CompetitiveProgrammingClient.java    # CP interface (problem stats, contest history)
│   ├── LeetCodeClient.java                  # implements CompetitiveProgrammingClient
│   ├── CodeforcesClient.java                # implements CompetitiveProgrammingClient
│   ├── GitHubClient.java                    # implements PlatformClient
│   ├── LeetCodeClientRunner.java            # Dev test runner (test-client profile)
│   ├── CodeforcesClientRunner.java          # Dev test runner (test-cf profile)
│   └── GitHubClientRunner.java             # Dev test runner (test-gh profile)
└── tools/
    ├── LeetCodeTools.java                   # LeetCode @Tool definitions
    ├── CodeforcesTools.java                 # Codeforces @Tool definitions
    └── GitHubTools.java                     # GitHub @Tool definitions
```

---

## Key Design Decisions

**Why a two-level interface hierarchy?**
`PlatformClient` defines capabilities every platform shares — profile and recent activity. `CompetitiveProgrammingClient` extends it with CP-specific capabilities — problem stats and contest history. LeetCode and Codeforces implement the full CP interface. GitHub implements only the base. Adding a new platform means picking the right interface and implementing it — nothing else changes.

**Why virtual threads?**
Multi-user queries fire all API calls in parallel using `Executors.newVirtualThreadPerTaskExecutor()`. Latency scales with the slowest single call, not the sum of all calls. I/O-bound work like HTTP calls is exactly the use case virtual threads are designed for.

**Why stdio transport?**
Claude Desktop communicates with MCP servers over stdin/stdout. No HTTP server, no ports, no auth. The JAR is launched as a subprocess per session and exits when done.

**Why no database or cache?**
All queried data is public and live. The server is stateless by design — nothing to operate, nothing to secure, nothing to break.

**Why no authentication?**
LeetCode GraphQL, Codeforces REST, and GitHub REST all serve public profile data without any API key or login.

---

## License

MIT