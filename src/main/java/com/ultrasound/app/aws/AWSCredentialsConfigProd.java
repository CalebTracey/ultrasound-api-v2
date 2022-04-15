//// TODO uncomment for prod
package com.ultrasound.app.aws;

import com.amazonaws.auth.AWSCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Prod only - we set local AWS creds through Intellij Plugin for AWS

@Configuration
@Profile("prod")
public class AWSCredentialsConfigProd implements AWSCredentials {

    @Value("aws.access.key.id")
    private String awsAccessKey;

    @Value("aws.secret.key")
    private String awsSecretKey;

    @Override
    public String getAWSAccessKeyId() {
        return awsAccessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return awsSecretKey;
    }
}
