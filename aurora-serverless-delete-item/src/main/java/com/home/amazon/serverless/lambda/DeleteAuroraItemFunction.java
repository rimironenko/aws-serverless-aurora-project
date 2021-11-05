package com.home.amazon.serverless.lambda;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.home.amazon.serverless.core.BaseAuroraFunction;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.Collections;

public class DeleteAuroraItemFunction extends BaseAuroraFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final String REMOVE_BY_ID_SQL_STATEMENT = "delete from %s.books where id=:id";
    static final String ID_SQL_PARAMETER_NAME = "id";

    static final int HTTP_STATUS_CODE_NO_CONTENT = 204;
    static final int HTTP_STATUS_CODE_SUCCESS = 200;

    public DeleteAuroraItemFunction() {
        super();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        int statusCode = HTTP_STATUS_CODE_NO_CONTENT;
        String id = input.getPathParameters().get(ID_SQL_PARAMETER_NAME);
        if (id != null && !id.isEmpty()) {
            ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                    .database(auroraDatabase)
                    .resourceArn(auroraClusterArn)
                    .secretArn(auroraSecretArn)
                    .sql(String.format(REMOVE_BY_ID_SQL_STATEMENT, auroraDatabase))
                    .parameters(SqlParameter.builder().name(ID_SQL_PARAMETER_NAME).value(Field.builder().stringValue(id).build()).build())
                    .build();
            ExecuteStatementResponse executeStatementResponse = rdsDataClient.executeStatement(request);
            if (executeStatementResponse.numberOfRecordsUpdated() == 1L) {
                statusCode = HTTP_STATUS_CODE_SUCCESS;
            }
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap());
    }

}
