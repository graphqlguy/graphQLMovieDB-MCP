package com.graphqlguy.moviedb.mcp;

import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SchemaDescriptionProvider {

    private final GraphQlSource graphQlSource;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public SchemaDescriptionProvider(GraphQlSource graphQlSource) {
        this.graphQlSource = graphQlSource;
    }

    public Optional<String> describe(String operationName) {
        return Optional.ofNullable(cache.computeIfAbsent(operationName, this::lookup));
    }

    private String lookup(String operationName) {
        return findField(graphQlSource.schema().getQueryType(), operationName)
            .or(() -> findField(graphQlSource.schema().getMutationType(), operationName))
            .map(GraphQLFieldDefinition::getDescription)
            .orElse(null);
    }

    private Optional<GraphQLFieldDefinition> findField(GraphQLObjectType type, String name) {
        if (type == null) return Optional.empty();
        return Optional.ofNullable(type.getFieldDefinition(name));
    }
}
