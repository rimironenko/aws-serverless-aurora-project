
package com.home.amazon.serverless;

import com.home.amazon.serverless.lambda.GetAuroraItemFunction;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;

/**
 * The module containing all dependencies required by the {@link GetAuroraItemFunction}.
 */
public class DependencyFactory {

    public static final String AURORA_CLUSTER_ARN_ENV = "AuroraClusterArn";
    public static final String DATABASE_NAME_ENV = "DatabaseName";
    public static final String AURORA_SECRET_ARN_ENV = "AuroraSecretArn";

    private DependencyFactory() {}

    /**
     * @return an instance of RdsClient
     */
    public static RdsDataClient rdsClient() {
        return RdsDataClient.builder()
                       .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                       .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                       .httpClientBuilder(UrlConnectionHttpClient.builder()).build();
    }

    public static String auroraClusterArn() {
        return System.getenv(AURORA_CLUSTER_ARN_ENV);
    }

    public static String auroraDatabase() {
        return System.getenv(DATABASE_NAME_ENV);
    }

    public static String auroraSecretArn() {
        return System.getenv(AURORA_SECRET_ARN_ENV);
    }
}
