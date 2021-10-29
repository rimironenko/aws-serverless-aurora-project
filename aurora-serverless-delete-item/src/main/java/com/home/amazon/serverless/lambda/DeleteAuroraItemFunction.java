package com.home.amazon.serverless.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.home.amazon.serverless.core.DependencyFactory;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.Collections;

public class DeleteAuroraItemFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final String REMOVE_BY_ID_SQL_STATEMENT = "delete from %s.books where id=:id";

    private final RdsDataClient rdsDataClient;
    private final String auroraClusterArn;
    private final String auroraDatabase;
    private final String auroraSecretArn;

    public DeleteAuroraItemFunction() {
        rdsDataClient = DependencyFactory.rdsClient();
        auroraClusterArn = DependencyFactory.auroraClusterArn();
        auroraDatabase = DependencyFactory.auroraDatabase();
        auroraSecretArn = DependencyFactory.auroraSecretArn();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        int statusCode = 204;
        String id = input.getPathParameters().get("id");
        if (id != null && !id.isEmpty()) {
            ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                    .database(auroraDatabase)
                    .resourceArn(auroraClusterArn)
                    .secretArn(auroraSecretArn)
                    .sql(String.format(REMOVE_BY_ID_SQL_STATEMENT, auroraDatabase))
                    .parameters(SqlParameter.builder().name("id").value(Field.builder().stringValue(id).build()).build())
                    .build();
            ExecuteStatementResponse executeStatementResponse = rdsDataClient.executeStatement(request);
            if (executeStatementResponse.numberOfRecordsUpdated() == 1L) {
                statusCode = 200;
            }
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap());
    }

}
