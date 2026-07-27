package com.graphqlguy.moviedb.mcp;

import org.mockito.Mockito;
import org.springframework.graphql.ExecutionGraphQlRequest;
import org.springframework.graphql.ExecutionGraphQlResponse;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.ResponseField;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Class 12 test helper. {@link #empty()} returns a Mockito-mocked
 * ExecutionGraphQlService whose response yields an empty list for any
 * operation and any field path, so any tool built on it runs its full
 * extraction path against empty data. {@link #returning(Object)} is the
 * variant with a canned value.
 */
final class MockGraphQlServiceFactory {

    private MockGraphQlServiceFactory() {}

    static ExecutionGraphQlService empty() {
        return returning(List.of());
    }

    static ExecutionGraphQlService returning(Object value) {
        ExecutionGraphQlService graphql = Mockito.mock(ExecutionGraphQlService.class);
        ExecutionGraphQlResponse response = Mockito.mock(ExecutionGraphQlResponse.class);
        ResponseField field = Mockito.mock(ResponseField.class);

        when(field.getValue()).thenReturn(value);
        when(response.field(anyString())).thenReturn(field);
        when(response.getErrors()).thenReturn(List.of());
        when(graphql.execute(any(ExecutionGraphQlRequest.class))).thenReturn(Mono.just(response));
        return graphql;
    }
}
