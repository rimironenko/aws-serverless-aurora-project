package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.home.amazon.serverless.core.Book;
import com.home.amazon.serverless.core.DependencyFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAuroraItemFunctionTest {

    private static final String TEST_CLUSTER_ARN = "clusterArn";
    private static final String TEST_DB_NAME = "dbName";
    private static final String TEST_SECRET_ARN = "secretArn";

    private static final long TEST_ID = 1L;
    private static final String TEST_NAME = "name";
    private static final String TEST_AUTHOR = "author";

    @Mock
    private RdsDataClient client;

    @Mock
    private Context context;

    @Mock
    private ExecuteStatementResponse statementResponse;

    @Mock
    private APIGatewayProxyRequestEvent request;

    @Test
    public void shouldReturnAuroraItemIfExists() {
        when(client.executeStatement(any(ExecuteStatementRequest.class))).thenReturn(statementResponse);
        when(statementResponse.hasRecords()).thenReturn(true);
        Book testBook = new Book(TEST_ID);
        testBook.setAuthor(TEST_AUTHOR);
        testBook.setName(TEST_NAME);
        List<Field> book = new ArrayList<>();
        book.add(Field.builder().longValue(TEST_ID).build());
        book.add(Field.builder().stringValue(TEST_NAME).build());
        book.add(Field.builder().stringValue(TEST_AUTHOR).build());
        List<List<Field>> records = new ArrayList<>();
        records.add(book);
        when(statementResponse.records()).thenReturn(records);
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put(GetAuroraItemFunction.ID_SQL_PARAMETER_NAME,String.valueOf(TEST_ID));
        when(request.getPathParameters()).thenReturn(pathParameters);

        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.rdsClient()).thenReturn(client);
            when(DependencyFactory.auroraClusterArn()).thenReturn(TEST_CLUSTER_ARN);
            when(DependencyFactory.auroraDatabase()).thenReturn(TEST_DB_NAME);
            when(DependencyFactory.auroraSecretArn()).thenReturn(TEST_SECRET_ARN);
            GetAuroraItemFunction handler = new GetAuroraItemFunction();
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            assertFalse(response.getBody().isEmpty());
            assertEquals(new Gson().toJson(testBook), response.getBody());

        }
    }

}