package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.home.amazon.serverless.DependencyFactory;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.*;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class GetAuroraItemFunction implements RequestHandler<Object, String> {

    private static final String GET_BY_ID_SQL_STATEMENT = "select * from %s.books";

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
    public String handleRequest(Object input, Context context) {
        ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                .database(auroraDatabase)
                .resourceArn(auroraClusterArn)
                .secretArn(auroraSecretArn)
                .sql(String.format(GET_BY_ID_SQL_STATEMENT, auroraDatabase))
                //.parameters(SqlParameter.builder().name("id").value(Field.builder().stringValue("1").build()).build())
                .build();
        ExecuteStatementResponse executeStatementResponse = rdsDataClient.executeStatement(request);
        System.out.println(executeStatementResponse);
        return null;
    }
}
