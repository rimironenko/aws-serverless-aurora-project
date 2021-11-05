package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteAuroraItemFunctionTest {

    private static final String TEST_CLUSTER_ARN = "clusterArn";
    private static final String TEST_DB_NAME = "dbName";
    private static final String TEST_SECRET_ARN = "secretArn";

    private static final int TEST_ID = 1;
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
    public void shouldReturn200IfItemWasDeleted() {
        when(client.executeStatement(any(ExecuteStatementRequest.class))).thenReturn(statementResponse);
        when(statementResponse.numberOfRecordsUpdated()).thenReturn(1L);
        Book testBook = new Book(TEST_ID);
        testBook.setAuthor(TEST_AUTHOR);
        testBook.setName(TEST_NAME);
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put(DeleteAuroraItemFunction.ID_SQL_PARAMETER_NAME,String.valueOf(TEST_ID));
        when(request.getPathParameters()).thenReturn(pathParameters);

        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.rdsClient()).thenReturn(client);
            when(DependencyFactory.auroraClusterArn()).thenReturn(TEST_CLUSTER_ARN);
            when(DependencyFactory.auroraDatabase()).thenReturn(TEST_DB_NAME);
            when(DependencyFactory.auroraSecretArn()).thenReturn(TEST_SECRET_ARN);
            DeleteAuroraItemFunction handler = new DeleteAuroraItemFunction();
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            assertEquals(DeleteAuroraItemFunction.HTTP_STATUS_CODE_SUCCESS, response.getStatusCode());
        }
    }

    @Test
    public void shouldReturn204IfItemWasNotDeleted() {
        when(client.executeStatement(any(ExecuteStatementRequest.class))).thenReturn(statementResponse);
        Book testBook = new Book(TEST_ID);
        testBook.setAuthor(TEST_AUTHOR);
        testBook.setName(TEST_NAME);
        Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put(DeleteAuroraItemFunction.ID_SQL_PARAMETER_NAME,String.valueOf(TEST_ID));
        when(request.getPathParameters()).thenReturn(pathParameters);

        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.rdsClient()).thenReturn(client);
            when(DependencyFactory.auroraClusterArn()).thenReturn(TEST_CLUSTER_ARN);
            when(DependencyFactory.auroraDatabase()).thenReturn(TEST_DB_NAME);
            when(DependencyFactory.auroraSecretArn()).thenReturn(TEST_SECRET_ARN);
            DeleteAuroraItemFunction handler = new DeleteAuroraItemFunction();
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            assertEquals(DeleteAuroraItemFunction.HTTP_STATUS_CODE_NO_CONTENT, response.getStatusCode());
        }
    }

    @Test
    public void shouldReturn204IfRequestIsNotValid() {
        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.rdsClient()).thenReturn(client);
            when(DependencyFactory.auroraClusterArn()).thenReturn(TEST_CLUSTER_ARN);
            when(DependencyFactory.auroraDatabase()).thenReturn(TEST_DB_NAME);
            when(DependencyFactory.auroraSecretArn()).thenReturn(TEST_SECRET_ARN);
            DeleteAuroraItemFunction handler = new DeleteAuroraItemFunction();
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            assertEquals(DeleteAuroraItemFunction.HTTP_STATUS_CODE_NO_CONTENT, response.getStatusCode());
        }
    }

}