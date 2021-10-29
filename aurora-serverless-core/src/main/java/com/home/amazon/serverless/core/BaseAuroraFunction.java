package com.home.amazon.serverless.core;

import software.amazon.awssdk.services.rdsdata.RdsDataClient;

public class BaseAuroraFunction {

    protected final RdsDataClient rdsDataClient;
    protected final String auroraClusterArn;
    protected final String auroraDatabase;
    protected final String auroraSecretArn;

    protected BaseAuroraFunction() {
        rdsDataClient = DependencyFactory.rdsClient();
        auroraClusterArn = DependencyFactory.auroraClusterArn();
        auroraDatabase = DependencyFactory.auroraDatabase();
        auroraSecretArn = DependencyFactory.auroraSecretArn();
    }

}
