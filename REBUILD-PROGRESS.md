# Rebuild progress journal

class 2 done: recommendMoviesForMood + summarizeMovieReviews schema/Java, ReviewRepository derived query; compile OK
class 3 done: agent-grade schema descriptions (Movie rewrite, rating->averageRating with @deprecated + @SchemaMapping alias, Mood/WatchStatus/Sentiment per-value, addToWatchlist contract), three-state summary service, SchemaDescriptionLintTest with shrinking legacy allowlist; full test suite green
class 4 done: apollo/ directory (mcp-config.yaml, three operation files, schema symlink); mcp_class_4b points here (ch04b defines no concrete config)
class 5 done: apollo/ extended (custom-scalars.json, forward_headers dev shortcut, per-operation leading-comment descriptions, mcp-config-support.yaml Contracts example)
class 6 done: spring-ai-bom 2.0.0 + spring-ai-starter-mcp-server-webmvc, application.yaml SYNC/STREAMABLE + capabilities block, HelloMcpTool ping smoke test; compile OK
class 7 done: MovieOperations constants + MovieMcpTools with three @McpTool methods (records nested; fail-fast executeOperation); NOTE Boot 4 = Jackson 3, ObjectMapper imports are tools.jackson.* not com.fasterxml.*; compile + context boot OK
