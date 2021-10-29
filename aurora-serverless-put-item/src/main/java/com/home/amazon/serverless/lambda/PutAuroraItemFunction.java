package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.home.amazon.serverless.core.BaseAuroraFunction;
import com.home.amazon.serverless.core.Book;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.SqlParameter;

import java.util.Collections;

public class PutAuroraItemFunction extends BaseAuroraFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final String INSERT_ITEM_SQL_STATEMENT = "insert into %s.books(name, author) values(:name, :author)";

    public PutAuroraItemFunction() {
        super();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String body = input.getBody();
        int statusCode = 204;
        if (body != null && !body.isEmpty()) {
            context.getLogger().log("Body: " + body);
            Book item = new Gson().fromJson(body, Book.class);
            if (item != null) {
                ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                        .database(auroraDatabase)
                        .resourceArn(auroraClusterArn)
                        .secretArn(auroraSecretArn)
                        .sql(String.format(INSERT_ITEM_SQL_STATEMENT, auroraDatabase))
                        .parameters(SqlParameter.builder().name("name").value(Field.builder().stringValue(item.getName()).build()).build(),
                                SqlParameter.builder().name("author").value(Field.builder().stringValue(item.getAuthor()).build()).build())
                        .build();
                ExecuteStatementResponse executeStatementResponse = rdsDataClient.executeStatement(request);
                if (executeStatementResponse.numberOfRecordsUpdated() == 1L) {
                    statusCode = 201;
                }
            }
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap());
    }

}
