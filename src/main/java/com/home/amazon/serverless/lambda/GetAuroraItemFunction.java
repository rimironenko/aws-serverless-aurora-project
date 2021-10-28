package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.home.amazon.serverless.DependencyFactory;
import com.home.amazon.serverless.dto.Book;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.*;

import java.util.ArrayList;
import java.util.List;

public class GetAuroraItemFunction implements RequestHandler<Object, List<Book>> {

    private static final String GET_BY_ID_SQL_STATEMENT = "select * from %s.books where id=:id";
    private static final String ID_SQL_PARAMETER_NAME = "id";

    private final RdsDataClient rdsDataClient;
    private final String auroraClusterArn;
    private final String auroraDatabase;
    private final String auroraSecretArn;

    public GetAuroraItemFunction() {
        rdsDataClient = DependencyFactory.rdsClient();
        auroraClusterArn = DependencyFactory.auroraClusterArn();
        auroraDatabase = DependencyFactory.auroraDatabase();
        auroraSecretArn = DependencyFactory.auroraSecretArn();
    }


    @Override
    public List<Book> handleRequest(Object input, Context context) {
        List<Book> result = new ArrayList<>();
        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .database(auroraDatabase)
                .resourceArn(auroraClusterArn)
                .secretArn(auroraSecretArn)
                .sql(String.format(GET_BY_ID_SQL_STATEMENT, auroraDatabase))
                .parameters(SqlParameter.builder().name(ID_SQL_PARAMETER_NAME).value(Field.builder().stringValue("1").build()).build())
                .build();
        ExecuteStatementResponse executeStatementResponse = rdsDataClient.executeStatement(request);
        if (executeStatementResponse.hasRecords()) {
            List<List<Field>> records = executeStatementResponse.records();
            for (List<Field> record : records) {
                result.add(transformToBook(record));
            }
        }
        return result;
    }

    private Book transformToBook(List<Field> record) {
        Long id = record.get(0).longValue();
        Book book = new Book(id);
        book.setName(record.get(1).stringValue());
        book.setAuthor(record.get(2).stringValue());
        return book;
    }
}
