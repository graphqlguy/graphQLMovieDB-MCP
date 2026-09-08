# graphQLMovieDB-MCP - Class 13

This branch is the repository state at the end of [Class 13: Building MCP From the Protocol Up](https://graphqlguy.com/docs/tutorial-graphql-mcp/diy-mcp-from-protocol), part of the [GraphQL + MCP course](https://graphqlguy.com/docs/tutorial-graphql-mcp/mcp-and-graphql).

**What Class 13 adds:** the `diymcp/` package: `DiyMcpController`, `DiyMcpToolConfiguration` and `ToolRegistry`, which answer JSON-RPC 2.0 at `/diy-mcp/message` with no MCP library involved.

**Following along:** start from `mcp_class_12`, work through the lesson, then compare your result with this branch. The next class continues on `mcp_class_14`.

## The application

A Spring for GraphQL movie database (Spring Boot 4, Java 21, in-memory H2): movies, TV shows, people, and reviews behind one GraphQL endpoint, with JWT-backed user accounts, a per-user watchlist, live TMDB community ratings, a review subscription, and query instrumentation. It began in the site's [Spring GraphQL tutorial](https://graphqlguy.com/docs/tutorial-SpringGraphQL/your-first-graphql-service) and gained a few extra domain objects for this course, so clone this repository even if you built that tutorial's version yourself.

## Running

```bash
./mvnw spring-boot:run     # GraphQL at :8080/graphql, GraphiQL at :8080/graphiql
./mvnw test                # full test suite
```

From `mcp_class_6` onwards the Spring AI track also serves MCP at `:8080/mcp`. From `mcp_class_18` onwards the agent client lives in `moviedb-agent/` and builds separately: `./mvnw -f moviedb-agent/pom.xml spring-boot:run` with `ANTHROPIC_API_KEY` set.

## Every branch

Each branch is the repository state at the end of one class. [Class 1](https://graphqlguy.com/docs/tutorial-graphql-mcp/mcp-and-graphql) and [Class 20](https://graphqlguy.com/docs/tutorial-graphql-mcp/beyond-tool-enumeration) add no code, so Class 1 reads against `main` and Class 20 against `mcp_class_19`.

| Branch | Class | Adds |
| --- | --- | --- |
| `main` | before [Class 1](https://graphqlguy.com/docs/tutorial-graphql-mcp/mcp-and-graphql) | the Movie Database, untouched by MCP work |
| `mcp_class_2` | [Class 2: Extending the Movie Schema for Agents](https://graphqlguy.com/docs/tutorial-graphql-mcp/extending-schema-for-agents) | two agent-designed operations |
| `mcp_class_3` | [Class 3: Schema Descriptions as Agent Documentation](https://graphqlguy.com/docs/tutorial-graphql-mcp/schema-descriptions-as-agent-docs) | agent-facing schema descriptions and their lint gate |
| `mcp_class_4` | [Class 4: Apollo MCP Server in Twenty Minutes](https://graphqlguy.com/docs/tutorial-graphql-mcp/apollo-mcp-server-in-twenty-minutes) | the `apollo/` directory Apollo MCP Server reads |
| `mcp_class_4b` | [Class 4b: Dynamic Discovery and the Search / Introspect / Execute Pattern](https://graphqlguy.com/docs/tutorial-graphql-mcp/dynamic-discovery-search-introspect-execute) | the dynamic-discovery Apollo config |
| `mcp_class_5` | [Class 5: Apollo in Practice and Its Limits](https://graphqlguy.com/docs/tutorial-graphql-mcp/apollo-in-practice) | the Apollo configs and tool output schemas |
| `mcp_class_6` | [Class 6: Spring AI MCP Server Starter, the Spring-Native Path](https://graphqlguy.com/docs/tutorial-graphql-mcp/spring-ai-mcp-server-starter) | the Spring AI MCP starter, serving `/mcp` |
| `mcp_class_7` | [Class 7: Translating GraphQL Operations Into MCP Tools](https://graphqlguy.com/docs/tutorial-graphql-mcp/translating-operations-to-tools) | `MovieMcpTools` and the pinned operations |
| `mcp_class_8` | [Class 8: Tool Descriptions From the Schema](https://graphqlguy.com/docs/tutorial-graphql-mcp/tool-descriptions-from-schema) | tool descriptions read from the schema |
| `mcp_class_9` | [Class 9: Structured Outputs and Errors](https://graphqlguy.com/docs/tutorial-graphql-mcp/structured-outputs-and-errors) | structured results and typed errors |
| `mcp_class_10` | [Class 10: Streaming, Progress, and Long-Running Tools](https://graphqlguy.com/docs/tutorial-graphql-mcp/streaming-progress-long-running) | progress notifications on a long tool |
| `mcp_class_11` | [Class 11: Elicitation and Sampling](https://graphqlguy.com/docs/tutorial-graphql-mcp/elicitation-and-sampling) | sampling, elicitation, and the server-side fallback |
| `mcp_class_12` | [Class 12: Testing the Spring AI MCP Server](https://graphqlguy.com/docs/tutorial-graphql-mcp/testing) | the four levels of tests |
| `mcp_class_13` | [Class 13: Building MCP From the Protocol Up](https://graphqlguy.com/docs/tutorial-graphql-mcp/diy-mcp-from-protocol) | JSON-RPC by hand in `diymcp/` |
| `mcp_class_14` | [Class 14: When DIY Pays Off](https://graphqlguy.com/docs/tutorial-graphql-mcp/when-diy-pays-off) | no code; the class weighs the three tracks |
| `mcp_class_15` | [Class 15: OAuth 2.1 Resource Server](https://graphqlguy.com/docs/tutorial-graphql-mcp/oauth2-resource-server) | OAuth 2.1 on `/mcp` |
| `mcp_class_16` | [Class 16: Agent-Specific Security](https://graphqlguy.com/docs/tutorial-graphql-mcp/agent-specific-security) | stage-and-confirm writes, audit, rate limits |
| `mcp_class_17` | [Class 17: Observability and Deployment](https://graphqlguy.com/docs/tutorial-graphql-mcp/observability-and-deployment) | tool metrics, cost, production profile |
| `mcp_class_18` | [Class 18: Building a Spring AI Agent Client](https://graphqlguy.com/docs/tutorial-graphql-mcp/spring-ai-agent-client) | the `moviedb-agent/` client module |
| `mcp_class_19` | [Class 19: Plugging Into Claude Desktop via stdio](https://graphqlguy.com/docs/tutorial-graphql-mcp/claude-desktop-via-stdio) | the stdio profile and Claude Desktop launcher |
