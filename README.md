# graphQLMovieDB-MCP

**`main` is the starting point of the course, not the finished project.** It contains the Movie Database application exactly as the [GraphQL + MCP tutorial](https://graphqlguy.com/docs/tutorial-graphql-mcp/mcp-and-graphql) expects it before any MCP work, and every class branch adds that class's work on top: `mcp_class_2` through `mcp_class_19` (plus `mcp_class_4b`), with `mcp_class_19` as the finished state. (The tutorial itself is publishing soon; the link goes live with it.)

The application is a Spring for GraphQL movie database (Spring Boot 4, Java 21, in-memory H2): movies, TV shows, people, and reviews behind one GraphQL endpoint, with JWT-backed user accounts, a per-user watchlist, live TMDB community ratings, a review subscription, and query instrumentation. It began in the site's [Spring GraphQL tutorial](https://graphqlguy.com/docs/tutorial-SpringGraphQL/your-first-graphql-service) and gained a few extra domain objects for this course, so clone this starting point even if you built that tutorial's version yourself.

## Following along

Each class branch bookmarks the repository state at the end of that class. To follow class N, start from the previous class's branch (`main` for Class 2), code along with the tutorial, and compare your result:

```bash
git switch mcp_class_7
```

Classes without new code point at the previous class's commit (`mcp_class_4b`, `mcp_class_14`), and Class 20 is a code-free epilogue that stays on the Class 19 state.

## What the classes add

- **Schema work (Classes 2-3):** two agent-designed operations (`recommendMoviesForMood`, `summarizeMovieReviews`), an audit of `addToWatchlist`, and agent-grade schema descriptions with a CI lint gate.
- **Track A - Apollo MCP Server (Classes 4-5):** a curated `apollo/` directory (config, operations, custom scalar mappings) served by the standalone `apollo-mcp-server` binary; no Java changes.
- **Track B - Spring AI MCP starter (Classes 6-12):** the in-process path; `@McpTool` methods that call `ExecutionGraphQlService`, schema-derived tool descriptions, structured outputs and errors, progress, elicitation and sampling, and a four-level test suite.
- **Track C - DIY (Class 13):** the same protocol hand-built in a Spring `@RestController` that accepts JSON-RPC 2.0 at `/diy-mcp/message`.
- **Cross-cutting (Classes 15-17):** OAuth 2.1 resource server on `/mcp`, agent-specific security (stage-and-confirm, audit, rate limiting), and production observability.
- **Clients (Classes 18-19):** the `moviedb-agent/` Spring AI ChatClient app and the stdio profile plus launcher for Claude Desktop.

## Running

```bash
./mvnw spring-boot:run     # GraphQL at :8080/graphql, GraphiQL at :8080/graphiql
./mvnw test                # full test suite
```

From `mcp_class_6` onwards the Spring AI track also serves MCP at `:8080/mcp`. From `mcp_class_18` onwards the agent client lives in `moviedb-agent/` and builds separately: `./mvnw -f moviedb-agent/pom.xml spring-boot:run` with `ANTHROPIC_API_KEY` set.
