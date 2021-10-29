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
import java.util.List;

public class GetAuroraItemFunction extends BaseAuroraFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String GET_BY_ID_SQL_STATEMENT = "select id, name, author from %s.books where id=:id";
    static final String ID_SQL_PARAMETER_NAME = "id";

    public GetAuroraItemFunction() {
        super();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String responseBody = "";
        String id = input.getPathParameters().get(ID_SQL_PARAMETER_NAME);
        if (id != null && !id.isEmpty()) {
            ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                    .database(auroraDatabase)
                    .resourceArn(auroraClusterArn)
                    .secretArn(auroraSecretArn)
                    .sql(String.format(GET_BY_ID_SQL_STATEMENT, auroraDatabase))
                    .parameters(SqlParameter.builder().name(ID_SQL_PARAMETER_NAME).value(Field.builder().stringValue(id).build()).build())
                    .build();
            ExecuteStatementResponse executeStatementResponse = rdsDataClient.executeStatement(request);
            if (executeStatementResponse.hasRecords()) {
                List<List<Field>> records = executeStatementResponse.records();
                for (List<Field> record : records) {
                    Book result = transformToBook(record);
                    responseBody = new Gson().toJson(result);
                }
            }
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(200)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap())
                .withBody(responseBody);
    }

    private Book transformToBook(List<Field> record) {
        Long id = record.get(0).longValue();
        Book book = new Book(id);
        book.setName(record.get(1).stringValue());
        book.setAuthor(record.get(2).stringValue());
        return book;
    }
}
