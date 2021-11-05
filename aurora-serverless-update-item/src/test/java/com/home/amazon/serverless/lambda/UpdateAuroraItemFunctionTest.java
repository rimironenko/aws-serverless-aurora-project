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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAuroraItemFunctionTest {

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
    public void shouldUpdateAuroraItemIfRequestIsValid() {
        when(client.executeStatement(any(ExecuteStatementRequest.class))).thenReturn(statementResponse);
        when(statementResponse.numberOfRecordsUpdated()).thenReturn(1L);
        Book testBook = new Book(TEST_ID);
        testBook.setAuthor(TEST_AUTHOR);
        testBook.setName(TEST_NAME);
        when(request.getBody()).thenReturn(new Gson().toJson(testBook));
        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.rdsClient()).thenReturn(client);
            when(DependencyFactory.auroraClusterArn()).thenReturn(TEST_CLUSTER_ARN);
            when(DependencyFactory.auroraDatabase()).thenReturn(TEST_DB_NAME);
            when(DependencyFactory.auroraSecretArn()).thenReturn(TEST_SECRET_ARN);
            UpdateAuroraItemFunction handler = new UpdateAuroraItemFunction();
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            assertEquals(UpdateAuroraItemFunction.HTTP_STATUS_CODE_SUCCESS, (int) response.getStatusCode());
        }
    }

    @Test
    public void shouldNotUpdateAuroraItemIfUpdateFailed() {
        when(client.executeStatement(any(ExecuteStatementRequest.class))).thenReturn(statementResponse);
        Book testBook = new Book(TEST_ID);
        testBook.setAuthor(TEST_AUTHOR);
        testBook.setName(TEST_NAME);
        when(request.getBody()).thenReturn(new Gson().toJson(testBook));
        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.rdsClient()).thenReturn(client);
            when(DependencyFactory.auroraClusterArn()).thenReturn(TEST_CLUSTER_ARN);
            when(DependencyFactory.auroraDatabase()).thenReturn(TEST_DB_NAME);
            when(DependencyFactory.auroraSecretArn()).thenReturn(TEST_SECRET_ARN);
            UpdateAuroraItemFunction handler = new UpdateAuroraItemFunction();
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            assertEquals(UpdateAuroraItemFunction.HTTP_STATUS_CODE_NO_CONTENT, (int) response.getStatusCode());
        }
    }

    @Test
    public void shouldNotUpdateAuroraItemIfURequestIsNotValid() {
        try (MockedStatic<DependencyFactory> dependencyFactoryMockedStatic = mockStatic(DependencyFactory.class)) {
            when(DependencyFactory.rdsClient()).thenReturn(client);
            when(DependencyFactory.auroraClusterArn()).thenReturn(TEST_CLUSTER_ARN);
            when(DependencyFactory.auroraDatabase()).thenReturn(TEST_DB_NAME);
            when(DependencyFactory.auroraSecretArn()).thenReturn(TEST_SECRET_ARN);
            UpdateAuroraItemFunction handler = new UpdateAuroraItemFunction();
            APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);
            assertEquals(UpdateAuroraItemFunction.HTTP_STATUS_CODE_NO_CONTENT, (int) response.getStatusCode());
        }
    }

}