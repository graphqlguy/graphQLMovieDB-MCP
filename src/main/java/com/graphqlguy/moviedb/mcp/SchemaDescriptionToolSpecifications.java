package com.graphqlguy.moviedb.mcp;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.provider.tool.SyncMcpToolProvider;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Class 8: the third registration route. We run the annotation scanner's own
 * machinery (SyncMcpToolProvider) by hand, rewrite each tool's description
 * from the GraphQL schema, and contribute the result as the bean the
 * auto-configuration registers. The annotation scanner itself is disabled in
 * application.yaml so every tool registers exactly once.
 */
@Configuration
public class SchemaDescriptionToolSpecifications {

    private static final Logger log =
        LoggerFactory.getLogger(SchemaDescriptionToolSpecifications.class);

    @Bean
    public List<McpServerFeatures.SyncToolSpecification> schemaDescribedTools(
            MovieMcpTools movieMcpTools,
            SafeMutationTools safeMutationTools,
            HelloMcpTool helloMcpTool,
            SchemaDescriptionProvider descriptions) {

        // Class 15: @PreAuthorize on the tool methods makes MovieMcpTools a
        // CGLIB proxy whose generated methods do not carry the @McpTool
        // annotation, so the stock scanner (getClass().getDeclaredMethods())
        // finds nothing. Scan the target class instead; because Method.invoke
        // dispatches virtually, the handler still invokes through the proxy, so
        // the @PreAuthorize advice runs on every tool call.
        List<McpServerFeatures.SyncToolSpecification> scanned =
            new SyncMcpToolProvider(List.of(movieMcpTools, safeMutationTools, helloMcpTool)) {
                @Override
                protected Method[] doGetClassMethods(Object bean) {
                    return AopUtils.getTargetClass(bean).getDeclaredMethods();
                }
            }.getToolSpecifications();

        return scanned.stream()
            .map(spec -> withSchemaDescription(spec, descriptions))
            .toList();
    }

    private McpServerFeatures.SyncToolSpecification withSchemaDescription(
            McpServerFeatures.SyncToolSpecification spec,
            SchemaDescriptionProvider descriptions) {

        McpSchema.Tool tool = spec.tool();

        String description = descriptions.describe(tool.name()).orElseGet(() -> {
            log.warn("No schema description for tool '{}'; falling back to the annotation description.",
                tool.name());
            return tool.description();
        });

        McpSchema.Tool rewritten = McpSchema.Tool.builder(tool.name(), tool.inputSchema())
            .title(tool.title())
            .description(description)
            .outputSchema(tool.outputSchema())
            .annotations(tool.annotations())
            .icons(tool.icons())
            .meta(tool.meta())
            .build();

        return McpServerFeatures.SyncToolSpecification.builder()
            .tool(rewritten)
            .callHandler(spec.callHandler())
            .build();
    }
}
