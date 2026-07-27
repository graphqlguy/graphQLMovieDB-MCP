# graphQLMovieDB-MCP

Companion code for the **GraphQL + MCP tutorial** at [graphqlguy.com](https://graphqlguy.com/docs/tutorial-graphql-mcp/mcp-and-graphql). It starts from the completed Movie Database application built in the site's Spring GraphQL tutorial (Spring Boot 4, Java 21, H2) and turns it into an MCP server three different ways.

What the tracks add on top of the base app:

- **Schema work (Classes 2-3):** two agent-designed operations (`recommendMoviesForMood`, `summarizeMovieReviews`), an audit of `addToWatchlist`, and agent-grade schema descriptions with a CI lint gate.
- **Track A - Apollo MCP Server (Classes 4-5):** a curated `apollo/` directory (config, operations, custom scalar mappings) served by the standalone `apollo-mcp-server` binary; no Java changes.
- **Track B - Spring AI MCP starter (Classes 6-12):** the in-process path; `@McpTool` methods that call `ExecutionGraphQlService`, schema-derived tool descriptions, structured outputs and errors, progress, elicitation and sampling, and a four-level test suite.
- **Track C - DIY (Class 13):** the same protocol hand-built in a Spring `@RestController` speaking JSON-RPC 2.0 at `/diy-mcp/message`.
- **Cross-cutting (Classes 15-17):** OAuth 2.1 resource server on `/mcp`, agent-specific security (stage-and-confirm, audit, rate limiting), and production observability.
- **Clients (Classes 18-19):** the `moviedb-agent/` Spring AI ChatClient app and the stdio profile plus launcher for Claude Desktop.

## Branch-per-class convention

Each class of the tutorial has a branch bookmarking the repository state at the end of that class: `mcp_class_2` through `mcp_class_20` (plus `mcp_class_4b`). Classes that introduce no new code (4b, 14, 20) point at the previous class's commit. `main` is the final state. To follow along with a class:

```bash
git switch mcp_class_7
```

## Running

```bash
./mvnw spring-boot:run     # GraphQL at :8080/graphql, GraphiQL at :8080/graphiql, MCP at :8080/mcp
./mvnw test                # full test suite
```

The agent client (Class 18) lives in `moviedb-agent/` and builds separately: `./mvnw -f moviedb-agent/pom.xml spring-boot:run` with `ANTHROPIC_API_KEY` set.
